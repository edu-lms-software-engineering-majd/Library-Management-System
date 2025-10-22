package lms.domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class Loan {
	
	private static final double BOOK_FINE_PER_DAY = 0.50;
	private static final double CD_FINE_PER_DAY = 0.75;
	private static final double JOURNAL_FINE_PER_DAY = 1.00;
	private static final double GENERIC_FINE_PER_DAY = 0.25;

	private UUID loanId;
	private UUID userId;
	private UUID itemId;
	private String itemType;
	private final LocalDate borrowDate;
	private LocalDate dueDate;
	private LocalDate returnDate;
	private boolean fineApplied;

	public Loan(UUID userId, UUID itemId, String itemType, LocalDate borrowDate) {
		this.loanId = UUID.randomUUID();
		this.userId = userId;
		this.itemId = itemId;
		this.itemType = itemType;
		this.borrowDate = borrowDate;
		this.dueDate = calculateDueDate(itemType, borrowDate);
		this.returnDate = null;
	}

	public boolean isOverdue() {

		return LocalDate.now().isAfter(dueDate) && returnDate == null;
	}

	public boolean isActive() {

		return returnDate == null;
	}

	public boolean isReturned() {

		return returnDate != null;
	}

	public long getDaysOverdue() {

		if (!isOverdue())
			return 0;

		return ChronoUnit.DAYS.between(dueDate, LocalDate.now());
	}

	public void returnItem() {

		if (returnDate != null) {
			throw new IllegalStateException("Item already returned");
		}

		this.returnDate = LocalDate.now();
	}

	public boolean canExtend() {

		return isActive() && !isOverdue();
	}

	public void extendLoan(int days) {

		if (!canExtend()) {
			throw new IllegalStateException("Cannot extend overdue or returned loan");
		}

		this.dueDate = this.dueDate.plusDays(days);
	}

	private static LocalDate calculateDueDate(String itemType, LocalDate borrowDate) {

		return switch (itemType.toLowerCase()) {
		case "book" -> borrowDate.plusDays(28);
		case "cd" -> borrowDate.plusDays(21);
		case "journal" -> borrowDate.plusDays(7);
		default -> borrowDate.plusDays(14);
		};
	}
	
	public double calculateFine() {

		long daysOverdue = getDaysOverdue();
		
		if (daysOverdue <= 0)
			return 0.0;

		double finePerDay = switch (itemType.toLowerCase()) {
			case "book" -> BOOK_FINE_PER_DAY;
			case "cd" -> CD_FINE_PER_DAY;
			case "journal" -> JOURNAL_FINE_PER_DAY;
			default -> GENERIC_FINE_PER_DAY;
		};

		return daysOverdue * finePerDay;
	}

	public void markFineApplied() {
		
		this.fineApplied = true;
	}
	
	public boolean isFineApplied() {
	    return fineApplied;
	}

	public UUID getLoanId() {
		return loanId;
	}

	public void setLoanId(UUID loanId) {
		this.loanId = loanId;
	}

	public UUID getUserId() {
		return userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	public UUID getItemId() {
		return itemId;
	}

	public void setItemId(UUID itemId) {
		this.itemId = itemId;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	public LocalDate getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(LocalDate returnDate) {
		this.returnDate = returnDate;
	}

	public LocalDate getBorrowDate() {
		return borrowDate;
	}
}