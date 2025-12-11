package lms.persistence;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import lms.domain.Loan;
import lms.domain.LoanRepository;
import lms.domain.User;
import lms.domain.exception.ItemNotFoundException;
import lms.domain.exception.ItemTypeNotFoundException;
import lms.domain.exception.LoanAlreadyExistsException;
import lms.domain.exception.LoanNotFoundException;
import lms.domain.exception.UserNotFoundException;

/**
 * In-memory implementation of {@link LoanRepository} using a static HashMap.
 * 
 * <p>Stores loans in a map with support for CRUD operations and complex queries by user, item,
 * status, and date ranges. Includes loan statistics and validation with custom exceptions.
 * This implementation is not thread-safe and intended for testing purposes.</p>
 * 
 * @author Majd Awwad
 * @version 2.0
 */
public class StaticLoanRepository implements LoanRepository {

	private static final StaticLoanRepository INSTANCE = new StaticLoanRepository();

	/**
	 * Returns the singleton instance of the repository.
	 * 
	 * @return the shared StaticLoanRepository instance
	 */
	public static StaticLoanRepository getInstance() {
		return INSTANCE;
	}

	private static final Map<UUID, Loan> loans = new HashMap<>();
	
	static {
		StaticUserRepository userRepo = StaticUserRepository.getInstance();
		User user = userRepo.getByUserName("user").orElseThrow();
		StaticBookRepository bookRepo = StaticBookRepository.getInstance();
		var book = bookRepo.getBookByName("Clean Code").orElseThrow();
		Loan loan = new Loan(user.getUserID(), book.getId(), "Book", LocalDate.now().minusDays(30));
		loans.put(loan.getId(), loan);
	}

	/**
	 * Validates that a loan exists in the repository.
	 *
	 * @param loanId the loan ID to check
	 * @throws LoanNotFoundException if loan does not exist
	 */
	private void validateLoanExists(UUID loanId) throws LoanNotFoundException {
		if (!loans.containsKey(loanId))
			throw new LoanNotFoundException("Loan not found: " + loanId);
	}

	/**
	 * Validates that a user has loans in the repository.
	 *
	 * @param userId the user ID to check
	 * @throws UserNotFoundException if user has no loans
	 */
	private void validateUserExists(UUID userId) throws UserNotFoundException {
		boolean exists = loans.values().stream().anyMatch(l -> l.getUserId().equals(userId));
		if (!exists)
			throw new UserNotFoundException("User has no loans: " + userId);
	}

	/**
	 * Validates that an item has loans in the repository.
	 *
	 * @param itemId the item ID to check
	 * @throws ItemNotFoundException if item has no loans
	 */
	private void validateItemExists(UUID itemId) throws ItemNotFoundException {
		boolean exists = loans.values().stream().anyMatch(l -> l.getItemId().equals(itemId));
		if (!exists)
			throw new ItemNotFoundException("Item has no loans: " + itemId);
	}

	/**
	 * Retrieves a loan by its unique identifier.
	 *
	 * @param loanId the loan ID
	 * @return an Optional containing the loan if found, otherwise empty
	 */
	@Override
	public Optional<Loan> findById(UUID loanId) {
		return Optional.ofNullable(loans.get(loanId));
	}

	/**
	 * Saves a new loan to the repository.
	 *
	 * @param loan the loan to save
	 * @return true if saved successfully
	 * @throws LoanAlreadyExistsException if loan ID already exists
	 */
	@Override
	public boolean save(Loan loan) throws LoanAlreadyExistsException {
		if (loans.containsKey(loan.getId()))
			throw new LoanAlreadyExistsException();

		loans.put(loan.getId(), loan);
		return true;
	}

	/**
	 * Updates an existing loan in the repository.
	 *
	 * @param loan the loan with updated information
	 * @return true if updated successfully
	 * @throws LoanNotFoundException if loan does not exist
	 */
	@Override
	public boolean update(Loan loan) throws LoanNotFoundException {
		validateLoanExists(loan.getId());
		loans.put(loan.getId(), loan);
		return true;
	}

	/**
	 * Deletes a loan from the repository.
	 *
	 * @param loanId the ID of the loan to delete
	 * @return true if deleted successfully
	 * @throws LoanNotFoundException if loan does not exist
	 */
	@Override
	public boolean delete(UUID loanId) throws LoanNotFoundException {
		validateLoanExists(loanId);
		loans.remove(loanId);
		return true;
	}

	/**
	 * Retrieves all loans in the repository.
	 *
	 * @return a list of all loans
	 */
	@Override
	public List<Loan> findAll() {
		return new ArrayList<>(loans.values());
	}

	/**
	 * Retrieves all loans for a specific user.
	 *
	 * @param userId the user ID
	 * @return a list of loans associated with the user
	 * @throws UserNotFoundException if user has no loans
	 */
	@Override
	public List<Loan> findByUserId(UUID userId) throws UserNotFoundException {
		validateUserExists(userId);
		return loans.values().stream().filter(l -> l.getUserId().equals(userId)).collect(Collectors.toList());
	}

	/**
	 * Retrieves all active (not returned) loans for a specific user.
	 *
	 * @param userId the user ID
	 * @return a list of active loans associated with the user
	 * @throws UserNotFoundException if user has no loans
	 */
	@Override
	public List<Loan> findActiveLoansByUser(UUID userId) throws UserNotFoundException {
		validateUserExists(userId);
		return loans.values().stream().filter(l -> l.getUserId().equals(userId) && !l.isReturned())
				.collect(Collectors.toList());
	}

	/**
	 * Retrieves all returned loans for a specific user.
	 *
	 * @param userId the user ID
	 * @return a list of returned loans associated with the user
	 * @throws UserNotFoundException if user has no loans
	 */
	@Override
	public List<Loan> findReturnedLoansByUser(UUID userId) throws UserNotFoundException {
		validateUserExists(userId);
		return loans.values().stream().filter(l -> l.getUserId().equals(userId) && l.isReturned())
				.collect(Collectors.toList());
	}

	/**
	 * Retrieves all overdue loans for a specific user.
	 *
	 * @param userId the user ID
	 * @return a list of overdue loans associated with the user
	 * @throws UserNotFoundException if user has no loans
	 */
	@Override
	public List<Loan> findOverdueLoansByUser(UUID userId) throws UserNotFoundException {
		validateUserExists(userId);
		return loans.values().stream().filter(l -> l.getUserId().equals(userId) && l.isOverdue())
				.collect(Collectors.toList());
	}

	/**
	 * Counts the number of active loans for a specific user.
	 *
	 * @param userId the user ID
	 * @return the count of active loans
	 * @throws UserNotFoundException if user has no loans
	 */
	@Override
	public int countActiveLoansByUser(UUID userId) throws UserNotFoundException {
		return findActiveLoansByUser(userId).size();
	}

	/**
	 * Retrieves all loans for a specific item.
	 *
	 * @param itemId the item ID
	 * @return a list of loans associated with the item
	 * @throws ItemNotFoundException if item has no loans
	 */
	@Override
	public List<Loan> findByItemId(UUID itemId) throws ItemNotFoundException {
		validateItemExists(itemId);
		return loans.values().stream().filter(l -> l.getItemId().equals(itemId)).collect(Collectors.toList());
	}

	/**
	 * Retrieves the active loan for a specific item, if any.
	 *
	 * @param itemId the item ID
	 * @return an Optional containing the active loan if found, otherwise empty
	 * @throws ItemNotFoundException if item has no loans
	 */
	@Override
	public Optional<Loan> findActiveLoanByItemId(UUID itemId) throws ItemNotFoundException {
		validateItemExists(itemId);
		return loans.values().stream().filter(l -> l.getItemId().equals(itemId) && !l.isReturned()).findFirst();
	}

	/**
	 * Retrieves all loans for items of a specific type.
	 *
	 * @param itemType the item type (e.g., "Book", "CD", "Journal")
	 * @return a list of loans for the specified item type
	 * @throws ItemTypeNotFoundException if no loans exist for the item type
	 */
	@Override
	public List<Loan> findByItemType(String itemType) throws ItemTypeNotFoundException {
		List<Loan> found = loans.values().stream().filter(l -> l.getItemType().equalsIgnoreCase(itemType))
				.collect(Collectors.toList());

		if (found.isEmpty())
			throw new ItemTypeNotFoundException("No loans with item type: " + itemType);

		return found;
	}

	/**
	 * Retrieves all overdue loans.
	 *
	 * @return a list of overdue loans
	 */
	@Override
	public List<Loan> findOverdueLoans() {
		return loans.values().stream().filter(Loan::isOverdue).toList();
	}

	/**
	 * Retrieves all active (not returned) loans.
	 *
	 * @return a list of active loans
	 */
	@Override
	public List<Loan> findActiveLoans() {
		return loans.values().stream().filter(l -> !l.isReturned()).toList();
	}

	/**
	 * Retrieves all returned loans.
	 *
	 * @return a list of returned loans
	 */
	@Override
	public List<Loan> findReturnedLoans() {
		return loans.values().stream().filter(Loan::isReturned).toList();
	}

	/**
	 * Retrieves all loans with fines applied.
	 *
	 * @return a list of loans with fines
	 */
	@Override
	public List<Loan> findLoansWithFines() {
		return loans.values().stream().filter(Loan::isFineApplied).toList();
	}

	/**
	 * Retrieves all loans borrowed on a specific date.
	 *
	 * @param borrowDate the borrow date
	 * @return a list of loans borrowed on the specified date
	 */
	@Override
	public List<Loan> findByBorrowDate(LocalDate borrowDate) {
		return loans.values().stream().filter(l -> l.getBorrowDate().equals(borrowDate)).toList();
	}

	/**
	 * Retrieves all loans due on a specific date.
	 *
	 * @param dueDate the due date
	 * @return a list of loans due on the specified date
	 */
	@Override
	public List<Loan> findByDueDate(LocalDate dueDate) {
		return loans.values().stream().filter(l -> l.getDueDate().equals(dueDate)).toList();
	}

	/**
	 * Retrieves all loans returned on a specific date.
	 *
	 * @param returnDate the return date
	 * @return a list of loans returned on the specified date
	 */
	@Override
	public List<Loan> findByReturnDate(LocalDate returnDate) {
		return loans.values().stream().filter(l -> returnDate.equals(l.getReturnDate())).toList();
	}

	/**
	 * Retrieves all loans borrowed within a date range.
	 *
	 * @param start the start date (inclusive)
	 * @param end the end date (inclusive)
	 * @return a list of loans borrowed within the date range
	 */
	@Override
	public List<Loan> findByBorrowDateRange(LocalDate start, LocalDate end) {
		return loans.values().stream()
				.filter(l -> !l.getBorrowDate().isBefore(start) && !l.getBorrowDate().isAfter(end)).toList();
	}

	/**
	 * Retrieves all loans due within a date range.
	 *
	 * @param start the start date (inclusive)
	 * @param end the end date (inclusive)
	 * @return a list of loans due within the date range
	 */
	@Override
	public List<Loan> findByDueDateRange(LocalDate start, LocalDate end) {
		return loans.values().stream().filter(l -> !l.getDueDate().isBefore(start) && !l.getDueDate().isAfter(end))
				.toList();
	}

	/**
	 * Counts the total number of loans in the repository.
	 *
	 * @return the total count of loans
	 */
	@Override
	public long countTotalLoans() {
		return loans.size();
	}

	/**
	 * Counts the number of active (not returned) loans.
	 *
	 * @return the count of active loans
	 */
	@Override
	public long countActiveLoans() {
		return loans.values().stream().filter(l -> !l.isReturned()).count();
	}

	/**
	 * Counts the number of overdue loans.
	 *
	 * @return the count of overdue loans
	 */
	@Override
	public long countOverdueLoans() {
		return loans.values().stream().filter(Loan::isOverdue).count();
	}

	/**
	 * Counts the number of loans for a specific item type.
	 *
	 * @param itemType the item type (e.g., "Book", "CD", "Journal")
	 * @return the count of loans for the item type
	 */
	@Override
	public long countLoansByItemType(String itemType) {
		return loans.values().stream().filter(l -> l.getItemType().equalsIgnoreCase(itemType)).count();
	}

	/**
	 * Checks if an item is currently on loan (not returned).
	 *
	 * @param itemId the item ID
	 * @return true if item is on loan, false otherwise
	 */
	@Override
	public boolean isItemOnLoan(UUID itemId) {
		return loans.values().stream().anyMatch(l -> l.getItemId().equals(itemId) && !l.isReturned());
	}

	/**
	 * Checks if a user has any overdue loans.
	 *
	 * @param userId the user ID
	 * @return true if user has overdue loans, false otherwise
	 */
	@Override
	public boolean hasOverdueLoans(UUID userId) {
		return loans.values().stream().anyMatch(l -> l.getUserId().equals(userId) && l.isOverdue());
	}

	/**
	 * Retrieves loans that are due within a specified number of days.
	 *
	 * @param days the number of days from now
	 * @return a list of loans due within the specified days
	 */
	@Override
	public List<Loan> findLoansDueSoon(int days) {
		LocalDate now = LocalDate.now();
		LocalDate limit = now.plusDays(days);

		return loans.values().stream().filter(l -> !l.isReturned() && !l.getDueDate().isAfter(limit)).toList();
	}

	/**
	 * Retrieves the most recent loan for a specific user.
	 *
	 * @param userId the user ID
	 * @return an Optional containing the most recent loan if found, otherwise empty
	 */
	@Override
	public Optional<Loan> findMostRecentLoanByUser(UUID userId) {
		return loans.values().stream().filter(l -> l.getUserId().equals(userId))
				.max(Comparator.comparing(Loan::getBorrowDate));
	}

	/**
	 * Retrieves the most recent loan for a specific item.
	 *
	 * @param itemId the item ID
	 * @return an Optional containing the most recent loan if found, otherwise empty
	 */
	@Override
	public Optional<Loan> findMostRecentLoanByItem(UUID itemId) {
		return loans.values().stream().filter(l -> l.getItemId().equals(itemId))
				.max(Comparator.comparing(Loan::getBorrowDate));
	}
}
