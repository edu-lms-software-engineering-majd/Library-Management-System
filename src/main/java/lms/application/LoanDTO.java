package lms.application;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) for representing a loan record.
 */
public record LoanDTO(
    UUID loanId,
    UUID userId,
    UUID bookId,
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
    public static LoanDTO fromLoan(lms.domain.Loan loan) {
        return new LoanDTO(
            loan.getLoanId(),
            loan.getUserId(),
            loan.getBookId(),
            loan.getBorrowDate(),
            loan.getDueDate(),
            loan.getReturnDate(),
            loan.isReturned(),
            loan.isFineApplied(),
            loan.isOverdue(),
            loan.getOverdueDays(),
            loan.calculateFine()
        );
    }
}
