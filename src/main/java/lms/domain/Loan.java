package lms.domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import lms.domain.strategy.FineStrategy;
import lms.domain.strategy.FineStrategyFactory;

/**
 * Represents a loan in the library system.
 */
public class Loan {

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

	// ✅ New fine calculation using Strategy pattern
	public double calculateFine() {
		long daysOverdue = getDaysOverdue();
		if (daysOverdue <= 0)
			return 0.0;

		FineStrategy strategy = FineStrategyFactory.getStrategy(itemType);
		return strategy.calculateFine(daysOverdue);
	}

	private static LocalDate calculateDueDate(String itemType, LocalDate borrowDate) {
		return switch (itemType.toLowerCase()) {
		case "book" -> borrowDate.plusDays(28);
		case "cd" -> borrowDate.plusDays(21);
		case "journal" -> borrowDate.plusDays(7);
		default -> borrowDate.plusDays(14);
		};
	}

	public long getDaysOverdue() {
		if (!isOverdue())
			return 0;
		return ChronoUnit.DAYS.between(dueDate, LocalDate.now());
	}

	public boolean isOverdue() {
		return LocalDate.now().isAfter(dueDate) && returnDate == null;
	}

	public void returnItem() {
		if (returnDate != null)
			throw new IllegalStateException("Item already returned");
		this.returnDate = LocalDate.now();
	}

	public UUID getLoanId() {
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

	public LocalDate getDueDate() {
		return dueDate;
	}

	public LocalDate getReturnDate() {
		return returnDate;
	}

	public boolean isFineApplied() {
		return fineApplied;
	}

	public void markFineApplied() {
		this.fineApplied = true;
	}

	public boolean isActive() {
		return returnDate == null;
	}

	public boolean canExtend() {
		return isActive() && !isOverdue();
	}

	public void extendLoan(int days) {
		if (!canExtend())
			throw new IllegalStateException("Cannot extend overdue or returned loan");
		this.dueDate = this.dueDate.plusDays(days);
	}
}
