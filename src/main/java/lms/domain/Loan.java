package lms.domain;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents a book loan by a user in the Library Management System.
 *
 * <p>This entity stores all loan-related information including:</p>
 * <ul>
 *   <li>The user who borrowed the book</li>
 *   <li>The borrowed book</li>
 *   <li>Borrow, due, and return dates</li>
 *   <li>The current loan status (returned or active)</li>
 *   <li>Whether a fine has been applied for lateness</li>
 * </ul>
 *
 * @author
 * @version 1.0
 */
public class Loan {

    private final UUID loanId;
    private final UUID userId;
    private final UUID bookId;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;
    private boolean isReturned;
    private boolean fineApplied;

    /**
     * Creates a new loan for the given user and book.
     *
     * @param userId the user ID
     * @param bookId the book ID
     */
    public Loan(UUID userId, UUID bookId) {
        this.loanId = UUID.randomUUID();
        this.userId = userId;
        this.bookId = bookId;
        this.borrowDate = LocalDate.now();
        this.dueDate = LocalDate.now().plusDays(28); // Default loan period: 28 days
        this.isReturned = false;
        this.fineApplied = false;
        this.returnDate = null;
    }

    /**
     * Constructor for restoring a loan from storage (e.g., database or file).
     *
     * @param loanId       unique ID of the loan
     * @param userId       ID of the user
     * @param bookId       ID of the book
     * @param borrowDate   date when the book was borrowed
     * @param dueDate      date when the book is due
     * @param returnDate   date when the book was returned (if applicable)
     * @param isReturned   true if the book was returned
     * @param fineApplied  true if a fine has been applied
     */
    public Loan(UUID loanId, UUID userId, UUID bookId, LocalDate borrowDate,
                LocalDate dueDate, LocalDate returnDate, boolean isReturned, boolean fineApplied) {
        this.loanId = loanId;
        this.userId = userId;
        this.bookId = bookId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.isReturned = isReturned;
        this.fineApplied = fineApplied;
    }

    // ===== Getters =====

    public UUID getLoanId() { return loanId; }
    public UUID getUserId() { return userId; }
    public UUID getBookId() { return bookId; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public boolean isReturned() { return isReturned; }
    public boolean isFineApplied() { return fineApplied; }

    // ===== Domain Logic =====

    /**
     * Marks the loan as returned and sets the return date.
     *
     * @throws IllegalStateException if the book has already been returned
     */
    public void returnBook() {
        if (isReturned) {
            throw new IllegalStateException("Book has already been returned.");
        }
        this.returnDate = LocalDate.now();
        this.isReturned = true;
    }

    /**
     * Checks whether this loan is overdue.
     *
     * @return true if the due date has passed and the book has not been returned
     */
    public boolean isOverdue() {
        return !isReturned && LocalDate.now().isAfter(dueDate);
    }

    /**
     * Calculates the number of overdue days.
     *
     * @return number of days overdue, or 0 if not overdue
     */
    public int getOverdueDays() {
        if (!isOverdue()) {
            return 0;
        }
        return (int) java.time.temporal.ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }

    /**
     * Calculates the fine based on the number of overdue days.
     *
     * @return total fine amount (2 NIS per overdue day)
     */
    public double calculateFine() {
        int overdueDays = getOverdueDays();
        return overdueDays * 2.0; // Temporary fixed fine rate
    }

    /**
     * Marks that a fine has been applied for this loan.
     *
     * @throws IllegalStateException if the loan is not overdue
     */
    public void markFineApplied() {
        if (!isOverdue()) {
            throw new IllegalStateException("Cannot apply fine to a non-overdue loan.");
        }
        this.fineApplied = true;
    }

    /**
     * Checks whether this loan is currently active (not returned).
     *
     * @return true if the loan is active
     */
    public boolean isActive() {
        return !isReturned;
    }

    /**
     * Gets the remaining days until the due date.
     *
     * @return number of days remaining (negative if overdue)
     */
    public int getDaysRemaining() {
        if (isReturned) {
            return 0;
        }
        return (int) java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
    }

    // ===== Object Overrides =====

    @Override
    public String toString() {
        return String.format(
            "Loan[ID: %s, User: %s, Book: %s, Borrowed: %s, Due: %s, Returned: %s, Overdue: %s]",
            loanId.toString().substring(0, 8),
            userId.toString().substring(0, 8),
            bookId.toString().substring(0, 8),
            borrowDate, dueDate, isReturned, isOverdue()
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Loan loan = (Loan) obj;
        return loanId.equals(loan.loanId);
    }

    @Override
    public int hashCode() {
        return loanId.hashCode();
    }
}
