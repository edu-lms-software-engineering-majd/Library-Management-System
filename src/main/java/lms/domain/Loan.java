package lms.domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import lms.domain.strategy.FineStrategy;
import lms.domain.strategy.FineStrategyFactory;
import lms.domain.utils.LoanValidator;

/**
 * Represents a loan of a library item to a user.
 * 
 * <p>Manages loan lifecycle including borrow dates, due dates, returns,
 * and fine calculations based on item type.</p>
 * 
 * @author Majd Awwad
 * @version 2.0
 */
public class Loan {

	private final UUID loanId;
	private final UUID userId;
	private final UUID itemId;
	private final String itemType;
	private final LocalDate borrowDate;
	private LocalDate dueDate;
	private LocalDate returnDate;
	private boolean fineApplied;
	private boolean notified;

	/**
	 * Creates a new loan for the specified user and item.
	 * 
	 * @param userId     the borrower's ID
	 * @param itemId     the item ID
	 * @param itemType   the item type (book, cd, journal)
	 * @param borrowDate the borrow date
	 * @throws IllegalArgumentException if any parameter is invalid
	 */
	public Loan(UUID userId, UUID itemId, String itemType, LocalDate borrowDate) {

		LoanValidator.getInstance().validate(userId, itemId, itemType, borrowDate);

		this.loanId = UUID.randomUUID();
		this.userId = userId;
		this.itemId = itemId;
		this.itemType = itemType;
		this.borrowDate = borrowDate;
		this.dueDate = calculateDueDate(itemType, borrowDate);
		this.returnDate = null;
		this.fineApplied = false;
		this.notified = false;
	}

	/**
	 * Calculates the fine amount based on item type and days overdue.
	 * Returns 0.0 if not overdue.
	 * 
	 * @return the calculated fine amount
	 */
	public double calculateFine() {
		long daysOverdue = getDaysOverdue();
		if (daysOverdue <= 0)
			return 0.0;

		FineStrategy strategy = FineStrategyFactory.getStrategy(itemType);
		return strategy.calculateFine(daysOverdue);
	}

	/**
	 * Calculates the due date based on item type:
	 * Books (28 days), CDs (21 days), Journals (7 days), Other (14 days).
	 * 
	 * @param itemType   the type of item
	 * @param borrowDate the borrow date
	 * @return the calculated due date
	 */
	private static LocalDate calculateDueDate(String itemType, LocalDate borrowDate) {
		return switch (itemType.toLowerCase()) {
		case "book" -> borrowDate.plusDays(28);
		case "cd" -> borrowDate.plusDays(21);
		case "journal" -> borrowDate.plusDays(7);
		default -> borrowDate.plusDays(14);
		};
	}

	/**
	 * Returns the number of days this loan is overdue.
	 * 
	 * @return days overdue, or 0 if not overdue
	 */
	public long getDaysOverdue() {
		if (!isOverdue())
			return 0;
		return ChronoUnit.DAYS.between(dueDate, LocalDate.now());
	}

	/**
	 * Checks if this loan is overdue.
	 * 
	 * @return true if past due and not returned
	 */
	public boolean isOverdue() {
		return LocalDate.now().isAfter(dueDate) && returnDate == null;
	}

	/**
	 * Marks this loan as returned.
	 * 
	 * @throws IllegalStateException if already returned
	 */
	public void returnItem() {
		LoanValidator.getInstance().validateCanReturn(returnDate);
		this.returnDate = LocalDate.now();
	}

	public UUID getId() {
		return loanId;
	}

	public UUID getUserId() {
		return userId;
	}

	public UUID getItemId() {
		return itemId;
	}

	public String getItemType() {
		return itemType;
	}

	public LocalDate getBorrowDate() {
		return borrowDate;
	}

	/** @return the date the item is due to be returned */
	public LocalDate getDueDate() {
		return dueDate;
	}

	/** @return the date the item was returned (null if still borrowed) */
	public LocalDate getReturnDate() {
		return returnDate;
	}

	/** @return true if a fine has been applied to this loan */
	public boolean isFineApplied() {
		return fineApplied;
	}

	/**
	 * Marks that a fine has been applied for this loan. This prevents duplicate
	 * fine applications.
	 */
	public void markFineApplied() {
		this.fineApplied = true;
	}

	/**
	 * Checks if the loan is still active (not yet returned).
	 * 
	 * @return true if the item has not been returned
	 */
	public boolean isActive() {
		return returnDate == null;
	}

	/**
	 * Checks if the loan can be extended.
	 * 
	 * @return true if loan is active and not overdue
	 */
	public boolean canExtend() {
		return isActive() && !isOverdue();
	}

	/**
	 * Extends the loan period by the specified number of days.
	 * 
	 * @param days the number of days to extend (must be positive)
	 * @throws IllegalStateException    if loan cannot be extended
	 * @throws IllegalArgumentException if days is not positive
	 */
	public void extendLoan(int days) {
		LoanValidator.getInstance().validateCanExtend(isActive(), isOverdue());
		LoanValidator.getInstance().validateExtensionDays(days);
		this.dueDate = this.dueDate.plusDays(days);
	}

	public boolean isReturned() {
		return returnDate != null;
	}

	public void returnItem(LocalDate returnDate) {
		if (returnDate == null) {
			throw new IllegalArgumentException("Return date cannot be null.");
		}
		this.returnDate = returnDate;
	}

	public boolean isNotified() {
		return notified;
	}

	public void setNotified(boolean notified) {
		this.notified = notified;
	}
}