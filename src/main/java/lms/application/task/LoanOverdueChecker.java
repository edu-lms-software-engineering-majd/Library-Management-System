package lms.application.task;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import lms.application.NotificationService;
import lms.application.email.EmailService;
import lms.domain.Book;
import lms.domain.CD;
import lms.domain.Journal;
import lms.domain.Loan;
import lms.domain.LoanRepository;
import lms.domain.User;
import lms.domain.UserRepository;
import lms.persistence.StaticBookRepository;
import lms.persistence.StaticCDRepository;
import lms.persistence.StaticJournalsRepository;
import lms.persistence.StaticLoanRepository;
import lms.persistence.StaticUserRepository;

/**
 * Background task that checks for overdue loans and notifies affected users.
 * 
 * <p>
 * This class implements {@link Runnable} to support scheduled execution by
 * a timer or scheduler service. When executed, it:
 * </p>
 * <ul>
 * <li>Finds all loans that are overdue and have not been notified</li>
 * <li>Sends email notifications to users with overdue items</li>
 * <li>Creates in-system notifications for tracking</li>
 * <li>Marks loans as notified to prevent duplicate notifications</li>
 * </ul>
 * 
 * <p>
 * This task is typically scheduled to run periodically (e.g., daily) to ensure
 * timely notification of overdue items.
 * </p>
 * 
 * @author Majd Awwad
 * @version 1.0
 * @see lms.application.SchedulerService
 * @see lms.application.NotificationService
 */
public class LoanOverdueChecker implements Runnable {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;

    /**
     * Constructs a new overdue checker with default repository instances.
     * Uses singleton instances of the repositories and services.
     */
    public LoanOverdueChecker() {
        this.loanRepository = StaticLoanRepository.getInstance();
        this.userRepository = StaticUserRepository.getInstance();
        this.emailService = EmailService.getInstance();
        this.notificationService = new NotificationService();
    }

    /**
     * Gets the human-readable item name based on item type and ID.
     *
     * @param itemId the item UUID
     * @param itemType the type of item (Book, CD, Journal)
     * @return the item name or "Unknown Item" if not found
     */
    private String getItemName(java.util.UUID itemId, String itemType) {
        try {
            switch (itemType.toLowerCase()) {
                case "book":
                    Book book = StaticBookRepository.getInstance().getBookById(itemId).orElse(null);
                    return book != null ? book.getTitle() : "Unknown Book";
                case "cd":
                    CD cd = StaticCDRepository.getInstance().getCDById(itemId).orElse(null);
                    return cd != null ? cd.getTitle() : "Unknown CD";
                case "journal":
                    Journal journal = StaticJournalsRepository.getInstance().getJournalById(itemId).orElse(null);
                    return journal != null ? journal.getTitle() : "Unknown Journal";
                default:
                    return "Unknown Item";
            }
        } catch (Exception e) {
            return itemType + " (ID: " + itemId + ")";
        }
    }

    @Override
    public void run() {
        System.out.println("Running overdue loan check...");
        List<Loan> overdueLoans = loanRepository.findAll().stream()
                .filter(loan -> loan.isOverdue() && !loan.isNotified())
                .collect(Collectors.toList());

        for (Loan loan : overdueLoans) {
            try {
                User user = userRepository.getByID(loan.getUserId()).orElse(null);
                if (user == null) {
                    System.err.println("User not found for loan ID: " + loan.getId());
                    continue;
                }

                String itemName = getItemName(loan.getItemId(), loan.getItemType());
                long daysOverdue = loan.getDaysOverdue();
                double fineAmount = loan.calculateFine();
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
                String formattedDueDate = loan.getDueDate().format(dateFormatter);

                String body = String.format(
                    "Dear %s,\n\n" +
                    "This is a friendly reminder that the following item is overdue:\n\n" +
                    "Item: %s (%s)\n" +
                    "Due Date: %s\n" +
                    "Days Overdue: %d\n" +
                    "Current Fine: $%.2f\n\n" +
                    "Please return this item to the library as soon as possible to avoid additional fines.\n\n" +
                    "Thank you for your cooperation.\n\n" +
                    "Best regards,\n" +
                    "Library Management System",
                    user.getFullName(),
                    itemName,
                    loan.getItemType(),
                    formattedDueDate,
                    daysOverdue,
                    fineAmount
                );

                notificationService.createNotification(user, body, lms.domain.NotificationType.OVERDUE);

                loan.setNotified(true);
                loanRepository.update(loan);

                System.out.println("Sent overdue notification for loan ID: " + loan.getId());

            } catch (Exception e) {
                System.err.println("Failed to send notification for loan ID: " + loan.getId());
                e.printStackTrace();
            }
        }
        System.out.println("Overdue loan check finished.");
    }
}
