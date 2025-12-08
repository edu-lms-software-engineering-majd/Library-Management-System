package lms.application;

import java.time.LocalDate;
import java.util.UUID;

import lms.domain.Loan;

/**
 * Data Transfer Object (DTO) for representing a loan record.
 *
 * <p>
 * This DTO provides a safe, immutable structure for exposing loan information
 * from the application layer to the presentation layer.
 * </p>
 *
 * <p>
 * Refactored by: Ahmad Salameh
 * </p>
 * 
 * @author Majd Awwad
 * @param loanId the unique identifier for the loan
 * @param userId the unique identifier for the user who borrowed the item
 * @param itemId the unique identifier for the borrowed item
 * @param borrowDate the date when the item was borrowed
 * @param dueDate the date when the item is due to be returned
 * @param returnDate the date when the item was actually returned (null if not yet returned)
 * @param returned whether the item has been returned
 * @param fineApplied whether a fine has been applied to this loan
 * @param overdue whether the loan is currently overdue
 * @param overdueDays the number of days the loan is overdue
 * @param calculatedFine the calculated fine amount for this loan
 */
public record LoanDTO(UUID loanId, UUID userId, UUID itemId, LocalDate borrowDate, LocalDate dueDate,
		LocalDate returnDate, boolean returned, boolean fineApplied, boolean overdue, long overdueDays,
		double calculatedFine) {

	/**
	 * Converts a {@link lms.domain.Loan} object into a {@link LoanDTO}.
	 *
	 * @param loan the Loan domain object
	 * @return a new LoanDTO containing the mapped loan data
	 */
	public static LoanDTO fromLoan(Loan loan) {
		return new LoanDTO(loan.getId(), loan.getUserId(), loan.getItemId(), loan.getBorrowDate(),
				loan.getDueDate(), loan.getReturnDate(), loan.getReturnDate() != null, loan.isFineApplied(),
				loan.isOverdue(), loan.getDaysOverdue(), loan.calculateFine());
	}
}
