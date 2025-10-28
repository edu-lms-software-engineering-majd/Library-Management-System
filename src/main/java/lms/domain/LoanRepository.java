package lms.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lms.domain.exception.ItemNotFoundException;
import lms.domain.exception.ItemTypeNotFoundException;
import lms.domain.exception.LoanAlreadyExistsException;
import lms.domain.exception.LoanNotFoundException;
import lms.domain.exception.UserNotFoundException;

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
 *   <li>Retrieve loans by ID, user, or item</li>
 *   <li>Find active, overdue, or returned loans</li>
 *   <li>Save, update, or delete loan records</li>
 *   <li>List all stored loans with filtering options</li>
 *   <li>Generate loan statistics and reports</li>
 * </ul>
 *
 * @author
 * @version 1.1
 */
public interface LoanRepository {

    /**
     * Finds a loan by its unique ID.
     *
     * @param loanId the loan ID
     * @return an {@link Optional} containing the loan if found, or empty otherwise
     */
    Optional<Loan> findById(UUID loanId);

    /**
     * Saves a new loan record.
     *
     * @param loan the loan to save
     * @return true if the save operation was successful
     */
    boolean save(Loan loan) throws LoanAlreadyExistsException;

    /**
     * Updates an existing loan record.
     *
     * @param loan the loan to update
     * @return true if the update was successful
     */
    boolean update(Loan loan) throws LoanNotFoundException;

    /**
     * Deletes a loan by its ID.
     *
     * @param loanId the loan ID
     * @return true if the deletion was successful
     */
    boolean delete(UUID loanId) throws LoanNotFoundException;

    /**
     * Retrieves all loans in the repository.
     *
     * @return list of all loans
     */
    List<Loan> findAll();

    /**
     * Finds all loans associated with a specific user.
     *
     * @param userId the user ID
     * @return list of all loans for the given user
     */
    List<Loan> findByUserId(UUID userId) throws UserNotFoundException;

    /**
     * Finds all active (not yet returned) loans for a specific user.
     *
     * @param userId the user ID
     * @return list of active loans for the user
     */
    List<Loan> findActiveLoansByUser(UUID userId) throws UserNotFoundException;

    /**
     * Finds all returned loans for a specific user.
     *
     * @param userId the user ID
     * @return list of returned loans for the user
     */
    List<Loan> findReturnedLoansByUser(UUID userId) throws UserNotFoundException;

    /**
     * Finds all overdue loans for a specific user.
     *
     * @param userId the user ID
     * @return list of overdue loans for the user
     */
    List<Loan> findOverdueLoansByUser(UUID userId) throws UserNotFoundException;

    /**
     * Counts active loans for a specific user.
     *
     * @param userId the user ID
     * @return number of active loans
     */
    int countActiveLoansByUser(UUID userId) throws UserNotFoundException;

    /**
     * Finds all loans for a specific item.
     *
     * @param itemId the item ID
     * @return list of loans for the given item
     */
    List<Loan> findByItemId(UUID itemId) throws ItemNotFoundException;

    /**
     * Finds active loan for a specific item (if any).
     *
     * @param itemId the item ID
     * @return optional containing active loan for the item
     */
    Optional<Loan> findActiveLoanByItemId(UUID itemId) throws ItemNotFoundException;

    /**
     * Finds all loans for items of a specific type.
     *
     * @param itemType the item type (e.g., "Book", "CD", "Journal")
     * @return list of loans for the given item type
     */
    List<Loan> findByItemType(String itemType) throws ItemTypeNotFoundException;

    /**
     * Finds all overdue loans in the system.
     *
     * @return list of overdue loans
     */
    List<Loan> findOverdueLoans();

    /**
     * Finds all active (not returned) loans in the system.
     *
     * @return list of active loans
     */
    List<Loan> findActiveLoans();

    /**
     * Finds all returned loans in the system.
     *
     * @return list of returned loans
     */
    List<Loan> findReturnedLoans();

    /**
     * Finds loans with applied fines.
     *
     * @return list of loans with fines applied
     */
    List<Loan> findLoansWithFines();

    /**
     * Finds loans borrowed on a specific date.
     *
     * @param borrowDate the borrow date
     * @return list of loans borrowed on the given date
     */
    List<Loan> findByBorrowDate(LocalDate borrowDate);

    /**
     * Finds loans due on a specific date.
     *
     * @param dueDate the due date
     * @return list of loans due on the given date
     */
    List<Loan> findByDueDate(LocalDate dueDate);

    /**
     * Finds loans returned on a specific date.
     *
     * @param returnDate the return date
     * @return list of loans returned on the given date
     */
    List<Loan> findByReturnDate(LocalDate returnDate);

    /**
     * Finds loans borrowed within a date range.
     *
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return list of loans borrowed within the date range
     */
    List<Loan> findByBorrowDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Finds loans due within a date range.
     *
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return list of loans due within the date range
     */
    List<Loan> findByDueDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Counts total number of loans in the system.
     *
     * @return total loan count
     */
    long countTotalLoans();

    /**
     * Counts active loans in the system.
     *
     * @return active loan count
     */
    long countActiveLoans();

    /**
     * Counts overdue loans in the system.
     *
     * @return overdue loan count
     */
    long countOverdueLoans();

    /**
     * Counts loans by item type.
     *
     * @param itemType the item type
     * @return count of loans for the item type
     */
    long countLoansByItemType(String itemType);

    /**
     * Checks if a specific item is currently on loan.
     *
     * @param itemId the item ID
     * @return true if the item is currently on loan
     */
    boolean isItemOnLoan(UUID itemId);

    /**
     * Checks if a user has any overdue loans.
     *
     * @param userId the user ID
     * @return true if the user has overdue loans
     */
    boolean hasOverdueLoans(UUID userId);

    /**
     * Finds loans that will be due soon (within specified days).
     *
     * @param days number of days ahead to check
     * @return list of loans due within the specified days
     */
    List<Loan> findLoansDueSoon(int days);

    /**
     * Finds the most recent loan for a specific user.
     *
     * @param userId the user ID
     * @return optional containing the most recent loan
     */
    Optional<Loan> findMostRecentLoanByUser(UUID userId);

    /**
     * Finds the most recent loan for a specific item.
     *
     * @param itemId the item ID
     * @return optional containing the most recent loan for the item
     */
    Optional<Loan> findMostRecentLoanByItem(UUID itemId);
}
