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
 * 
 * Refactored by: Ahmad Salameh
 * </p>
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
		return new LoanDTO(loan.getLoanId(), loan.getUserId(), loan.getItemId(), loan.getBorrowDate(),
				loan.getDueDate(), loan.getReturnDate(), loan.getReturnDate() != null, loan.isFineApplied(),
				loan.isOverdue(), loan.getDaysOverdue(), loan.calculateFine());
	}
}
