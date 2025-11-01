package lms.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing {@link Loan} persistence operations.
 *
 * <p>
 * Defines the contract for interacting with the loan data source
 * (e.g., database, file, or in-memory storage).
 * </p>
 *
 * <h3>Responsibilities:</h3>
 * <ul>
 *   <li>Retrieve loans by ID or user</li>
 *   <li>Find active or overdue loans</li>
 *   <li>Save, update, or delete loan records</li>
 *   <li>List all stored loans</li>
 * </ul>
 *
 * @author
 * @version 1.0
 */
public interface LoanRepo {

    /**
     * Finds a loan by its unique ID.
     *
     * @param loanId the loan ID
     * @return an {@link Optional} containing the loan if found, or empty otherwise
     */
    Optional<Loan> findById(UUID loanId);

    /**
     * Finds all loans associated with a specific user.
     *
     * @param userId the user ID
     * @return list of all loans for the given user
     */
    List<Loan> findByUserId(UUID userId);

    /**
     * Finds all active (not yet returned) loans for a specific user.
     *
     * @param userId the user ID
     * @return list of active loans for the user
     */
    List<Loan> findActiveLoansByUser(UUID userId);

    /**
     * Finds all overdue loans in the system.
     *
     * @return list of overdue loans
     */
    List<Loan> findOverdueLoans();

    /**
     * Saves a new loan record.
     *
     * @param loan the loan to save
     * @return true if the save operation was successful
     */
    boolean save(Loan loan);

    /**
     * Updates an existing loan record.
     *
     * @param loan the loan to update
     * @return true if the update was successful
     */
    boolean update(Loan loan);

    /**
     * Deletes a loan by its ID.
     *
     * @param loanId the loan ID
     * @return true if the deletion was successful
     */
    boolean delete(UUID loanId);

    /**
     * Retrieves all loans in the repository.
     *
     * @return list of all loans
     */
    List<Loan> findAll();
}
