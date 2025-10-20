package lms.application;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lms.domain.Account;
import lms.domain.Book;
import lms.domain.BookRepo;
import lms.domain.Loan;
import lms.domain.LoanRepo;
import lms.domain.exception.PermissionDeniedException;
import lms.persistence.AccountRepo;

/**
 * Application service responsible for managing borrowing and returning operations
 * in the Library Management System.
 *
 * <p>
 * This service coordinates loan and return processes between domain entities
 * and applies business rules related to library operations.
 * </p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Manage book borrowing operations</li>
 *   <li>Handle returning of borrowed books</li>
 *   <li>Calculate and apply overdue fines</li>
 *   <li>Provide information about active and overdue loans</li>
 * </ul>
 *
 * @author Majd
 * @version 1.0
 */
public class LoanService {
    private final LoanRepo loanRepo;
    private final BookRepo bookRepo;
    private final AccountRepo accountRepo;
    private final AccountService accountService;

    /**
     * Creates a new loan service.
     *
     * @param loanRepo       repository for loans
     * @param bookRepo       repository for books
     * @param accountRepo    repository for accounts
     * @param accountService account management service
     */
    public LoanService(LoanRepo loanRepo, BookRepo bookRepo, AccountRepo accountRepo, AccountService accountService) {
        this.loanRepo = loanRepo;
        this.bookRepo = bookRepo;
        this.accountRepo = accountRepo;
        this.accountService = accountService;
    }

    /**
     * Executes a book borrowing process for a given user.
     *
     * <p>
     * The process includes the following steps:
     * </p>
     * <ol>
     *   <li>Verify that the user is allowed to borrow</li>
     *   <li>Check book availability</li>
     *   <li>Ensure the user has not already borrowed the same book</li>
     *   <li>Create a new loan record</li>
     *   <li>Update available book copies</li>
     *   <li>Update the user’s current borrowed count</li>
     * </ol>
     *
     * @param userId the user ID
     * @param bookId the book ID
     * @return true if the process succeeded, false otherwise
     * @throws IllegalStateException    if the user cannot borrow at this time
     * @throws IllegalArgumentException if the book does not exist
     */
    public boolean borrowBook(UUID userId, UUID bookId) {
        // 1. Check if user can borrow
        if (!accountService.canUserBorrow(userId)) {
            Account account = accountService.getAccountInfo(userId);
            throw new IllegalStateException("User cannot borrow books at the moment. Status: "
                    + account.getStatus() + ", Balance: " + account.getBalance()
                    + ", Borrowed: " + account.getCurrentBorrowedCount() + "/"
                    + account.getMaxBorrowLimit());
        }

        // 2. Check if book exists and is available
        Book book = bookRepo.getBookById(bookId);
        if (book == null) {
            throw new IllegalArgumentException("Book not found - ID: " + bookId);
        }

        if (book.getAvailableCopies() <= 0) {
            throw new IllegalStateException("Book is not available for borrowing. Available copies: "
                    + book.getAvailableCopies() + "/" + book.getTotalCopies());
        }

        // 3. Ensure the user hasn’t already borrowed this book
        List<Loan> activeLoans = loanRepo.findActiveLoansByUser(userId);
        boolean alreadyBorrowed = activeLoans.stream()
                .anyMatch(loan -> loan.getBookId().equals(bookId));

        if (alreadyBorrowed) {
            throw new IllegalStateException("User has already borrowed this book.");
        }

        // 4. Create loan record
        Loan loan = new Loan(userId, bookId);
        boolean loanSaved = loanRepo.save(loan);

        if (loanSaved) {
            // 5. Update available book copies
            book.setAvailableCopies(book.getAvailableCopies() - 1);
            bookRepo.updateBook(book);

            // 6. Update user’s borrowed count
            Account account = accountRepo.findByUserId(userId)
                    .orElseThrow(() -> new IllegalStateException("User account not found"));
            account.incrementBorrowedCount();
            accountRepo.update(account);

            System.out.println("✅ Book borrowed successfully: " + book.getTitle());
            System.out.println("   Due date: " + loan.getDueDate());
            return true;
        }

        return false;
    }

    /**
     * Executes the process of returning a borrowed book.
     *
     * <p>
     * The process includes the following steps:
     * </p>
     * <ol>
     *   <li>Verify that the loan exists</li>
     *   <li>Update return date</li>
     *   <li>Update available book copies</li>
     *   <li>Update user’s borrowed count</li>
     *   <li>Calculate and apply fines if overdue</li>
     * </ol>
     *
     * @param loanId the loan ID
     * @return true if successful
     * @throws IllegalArgumentException if the loan does not exist
     * @throws IllegalStateException    if the book has already been returned
     */
    public boolean returnBook(UUID loanId) {
        // 1. Verify loan existence
        Loan loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found - ID: " + loanId));

        if (loan.isReturned()) {
            throw new IllegalStateException("Book already returned on: " + loan.getReturnDate());
        }

        // 2. Update return date
        loan.returnBook();

        // 3. Update available book copies
        Book book = bookRepo.getBookById(loan.getBookId());
        if (book == null) {
            throw new IllegalStateException("Book linked to loan not found");
        }
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepo.updateBook(book);

        // 4. Update user’s borrowed count
        Account account = accountRepo.findByUserId(loan.getUserId())
                .orElseThrow(() -> new IllegalStateException("User account not found"));
        account.decrementBorrowedCount();

        // 5. Apply fines if overdue
        double fineAmount = 0.0;
        if (loan.isOverdue()) {
            fineAmount = loan.calculateFine();
            account.addFine(fineAmount,
                    "Late return of book '" + book.getTitle() + "' - " + loan.getOverdueDays() + " day(s) overdue");
            loan.markFineApplied();

            System.out.println("⚠️  Late fine applied: " + fineAmount + " NIS");
            System.out.println("   Overdue days: " + loan.getOverdueDays());
        }

        // 6. Save all changes
        loanRepo.update(loan);
        accountRepo.update(account);

        System.out.println("✅ Book returned successfully: " + book.getTitle());
        if (fineAmount > 0) {
            System.out.println("   Current balance: " + account.getBalance() + " NIS");
        }

        return true;
    }

    /**
     * Retrieves the list of active loans for a given user.
     *
     * @param userId the user ID
     * @return list of active loans
     */
    public List<Loan> getUserActiveLoans(UUID userId) {
        return loanRepo.findActiveLoansByUser(userId);
    }

    /**
     * Retrieves all loans (active and completed) for a given user.
     *
     * @param userId the user ID
     * @return list of all user loans
     */
    public List<Loan> getUserAllLoans(UUID userId) {
        return loanRepo.findByUserId(userId);
    }

    /**
     * Retrieves all overdue loans in the system.
     *
     * @return list of overdue loans
     */
    public List<Loan> getOverdueLoans() {
        return loanRepo.findOverdueLoans();
    }

    /**
     * Retrieves a specific loan by ID.
     *
     * @param loanId the loan ID
     * @return the loan object
     * @throws IllegalArgumentException if the loan does not exist
     */
    public Loan getLoan(UUID loanId) {
        return loanRepo.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found - ID: " + loanId));
    }

    /**
     * Retrieves loan details along with book information.
     *
     * @param loanId the loan ID
     * @return an array containing [Loan, Book]
     */
    public Object[] getLoanWithBookInfo(UUID loanId) {
        Loan loan = getLoan(loanId);
        Book book = bookRepo.getBookById(loan.getBookId());
        return new Object[] { loan, book };
    }

    /**
     * Calculates the total amount of fines owed by a user.
     *
     * @param userId the user ID
     * @return total fine amount
     */
    public double calculateUserTotalFines(UUID userId) {
        List<Loan> activeLoans = getUserActiveLoans(userId);
        return activeLoans.stream().filter(Loan::isOverdue).mapToDouble(Loan::calculateFine).sum();
    }

    /**
     * Extends a loan period (admin only).
     *
     * @param userDTO        the user requesting the extension
     * @param loanId         the loan ID
     * @param additionalDays number of extra days to add
     * @return true if the extension is successful
     * @throws PermissionDeniedException if the user is not an admin
     */
    public boolean extendLoan(UserDTO userDTO, UUID loanId, int additionalDays) throws PermissionDeniedException {
        AuthorizationService.ensureAdmin(userDTO);

        if (additionalDays <= 0) {
            throw new IllegalArgumentException("Additional days must be positive");
        }

        Loan loan = getLoan(loanId);
        if (loan.isReturned()) {
            throw new IllegalStateException("Cannot extend a completed loan");
        }

        // In a real implementation, the dueDate would be updated.
        // This is just a simulation for now.
        System.out.println("✅ Loan extended for " + additionalDays + " additional days");
        return true;
    }

    /**
     * Retrieves loan statistics.
     *
     * @return an array containing [total loans, active loans, overdue loans]
     */
    public int[] getLoanStatistics() {
        List<Loan> allLoans = loanRepo.findAll();
        int totalLoans = allLoans.size();
        int activeLoans = (int) allLoans.stream().filter(loan -> !loan.isReturned()).count();
        int overdueLoans = (int) allLoans.stream().filter(Loan::isOverdue).count();

        return new int[] { totalLoans, activeLoans, overdueLoans };
    }

    /**
     * Retrieves active loans for a user with their corresponding book information.
     *
     * @param userId the user ID
     * @return list of arrays containing [Loan, Book]
     */
    public List<Object[]> getUserActiveLoansWithBooks(UUID userId) {
        List<Loan> activeLoans = getUserActiveLoans(userId);
        List<Object[]> result = new ArrayList<>();

        for (Loan loan : activeLoans) {
            Book book = bookRepo.getBookById(loan.getBookId());
            if (book != null) {
                result.add(new Object[] { loan, book });
            }
        }

        return result;
    }
}
