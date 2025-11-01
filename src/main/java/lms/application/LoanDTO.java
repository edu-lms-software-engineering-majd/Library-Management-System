package lms.application;

import java.time.LocalDate;
import java.util.UUID;

import lms.domain.Loan;

/**
 * Data Transfer Object (DTO) for representing a loan record.
 */
public record LoanDTO(
    UUID loanId,
    UUID userId,
    UUID itemId,
    LocalDate borrowDate,
    LocalDate dueDate,
    LocalDate returnDate,
    boolean isReturned,
    boolean fineApplied,
    boolean isOverdue,
    int overdueDays,
    double calculatedFine
) {

    /**
     * Converts a {@link lms.domain.Loan} object into a {@link LoanDTO}.
     *
     * @param loan the Loan domain object
     * @return a new LoanDTO containing the same data
     */
    public static LoanDTO fromLoan(Loan loan) {
        return new LoanDTO(
            loan.getLoanId(),
            loan.getUserId(),
            loan.getItemId(),
            loan.getBorrowDate(),
            loan.getDueDate(),
            loan.getReturnDate(),
            loan.isReturned(),
            loan.isFineApplied(),
            loan.isOverdue(),
            loan.getDaysOverdue(),
            loan.calculateFine()
        );
    }
}
