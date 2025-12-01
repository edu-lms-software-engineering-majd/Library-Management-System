package lms.application.task;

import java.util.List;
import java.util.stream.Collectors;

import lms.application.NotificationService;
import lms.application.email.EmailService;
import lms.domain.Loan;
import lms.domain.LoanRepository;
import lms.domain.User;
import lms.domain.UserRepository;
import lms.persistence.StaticLoanRepository;
import lms.persistence.StaticUserRepository;

public class LoanOverdueChecker implements Runnable {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;

    public LoanOverdueChecker() {
        this.loanRepository = StaticLoanRepository.getInstance();
        this.userRepository = StaticUserRepository.getInstance();
        this.emailService = EmailService.getInstance();
        this.notificationService = new NotificationService();
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
                    System.err.println("User not found for loan ID: " + loan.getLoanId());
                    continue;
                }

                String subject = "Overdue Loan Notification";
                String body = "Dear " + user.getFullName() + ",\n\n"
                            + "This is a reminder that your loan for item ID '"
                            + loan.getItemId() + "' was due on "
                            + loan.getDueDate() + ". Please return it as soon as possible.";

                emailService.sendEmail(user.getEmail(), subject, body);
                notificationService.createNotification(user, body);

                loan.setNotified(true);
                loanRepository.save(loan);

                System.out.println("Sent overdue notification for loan ID: " + loan.getLoanId());

            } catch (Exception e) {
                System.err.println("Failed to send notification for loan ID: " + loan.getLoanId());
                e.printStackTrace();
            }
        }
        System.out.println("Overdue loan check finished.");
    }
}
