package lms.application;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import lms.domain.Loan;
import lms.domain.LoanRepository;

/**
 * Service providing query operations for retrieving and filtering loan records.
 * 
 * <p>
 * This service offers various query methods to retrieve loan information based on
 * different criteria including:
 * </p>
 * <ul>
 * <li>Filtering by loan status (active, returned, overdue)</li>
 * <li>Filtering by user, item, or item type</li>
 * <li>Filtering by dates (borrow date, due date, return date)</li>
 * <li>Finding loans due soon for proactive notifications</li>
 * <li>Retrieving loans within date ranges</li>
 * </ul>
 * 
 * <p>
 * This service separates query operations from the main {@link LoanService} to follow
 * the Single Responsibility Principle. While LoanService handles loan lifecycle
 * operations (borrow, return, extend), this service focuses purely on retrieving
 * and filtering existing loan data.
 * </p>
 * 
 * @author Majd Awwad
 * @version 2.0
 * @see LoanStatsService
 * @see LoanService
 * @see Loan
 */
public class LoanQueryService {

	private final LoanRepository loanRepo;

	/**
	 * Constructs a new query service with the required repository.
	 * 
	 * @param loanRepo the loan repository for accessing loan data
	 * @throws IllegalArgumentException if loanRepo is null
	 */
	public LoanQueryService(LoanRepository loanRepo) {
		if (loanRepo == null)
			throw new IllegalArgumentException("LoanRepository cannot be null");
		this.loanRepo = loanRepo;
	}

	/**
	 * Retrieves all active (not returned) loans.
	 * 
	 * @return a list of all active loans
	 */
	public List<Loan> getActiveLoans() {
		return loanRepo.findAll().stream().filter(l -> !l.isReturned()).collect(Collectors.toList());
	}

	/**
	 * Retrieves all loans for a specific item type.
	 * 
	 * @param itemType the type of item (e.g., "book", "cd", "journal")
	 * @return a list of loans for the specified item type
	 */
	public List<Loan> getLoansByItemType(String itemType) {
		return loanRepo.findAll().stream().filter(l -> l.getItemType().equalsIgnoreCase(itemType))
				.collect(Collectors.toList());
	}

	/**
	 * Retrieves all returned loans.
	 * 
	 * @return a list of all returned loans
	 */
	public List<Loan> getReturnedLoans() {
		return loanRepo.findAll().stream().filter(Loan::isReturned).collect(Collectors.toList());
	}

	/**
	 * Retrieves all loans for a specific user.
	 * 
	 * @param userId the unique identifier of the user
	 * @return a list of loans for the specified user
	 */
	public List<Loan> getLoansByUser(UUID userId) {
		return loanRepo.findAll().stream().filter(l -> l.getUserId().equals(userId)).collect(Collectors.toList());
	}

	/**
	 * Retrieves all loans for a specific item.
	 * 
	 * @param itemId the unique identifier of the item
	 * @return a list of loans for the specified item
	 */
	public List<Loan> getLoansByItem(UUID itemId) {
		return loanRepo.findAll().stream().filter(l -> l.getItemId().equals(itemId)).collect(Collectors.toList());
	}

	/**
	 * Retrieves all overdue loans.
	 * 
	 * @return a list of all overdue loans
	 */
	public List<Loan> getOverdueLoans() {
		return loanRepo.findAll().stream().filter(Loan::isOverdue).collect(Collectors.toList());
	}

	/**
	 * Retrieves all loans borrowed on a specific date.
	 * 
	 * @param borrowDate the borrow date to search for
	 * @return a list of loans borrowed on the specified date
	 */
	public List<Loan> getLoansByBorrowDate(LocalDate borrowDate) {
		return loanRepo.findAll().stream().filter(l -> l.getBorrowDate().equals(borrowDate))
				.collect(Collectors.toList());
	}

	/**
	 * Retrieves all loans due on a specific date.
	 * 
	 * @param dueDate the due date to search for
	 * @return a list of loans due on the specified date
	 */
	public List<Loan> getLoansByDueDate(LocalDate dueDate) {
		return loanRepo.findAll().stream().filter(l -> l.getDueDate().equals(dueDate)).collect(Collectors.toList());
	}

	/**
	 * Retrieves all loans returned on a specific date.
	 * 
	 * @param returnDate the return date to search for
	 * @return a list of loans returned on the specified date
	 */
	public List<Loan> getLoansByReturnDate(LocalDate returnDate) {
		return loanRepo.findAll().stream().filter(l -> returnDate.equals(l.getReturnDate()))
				.collect(Collectors.toList());
	}

	/**
	 * Retrieves all loans that are due within a specified number of days.
	 * 
	 * @param days the number of days from now to check
	 * @return a list of loans due within the specified number of days
	 */
	public List<Loan> getLoansDueSoon(int days) {
		LocalDate now = LocalDate.now();
		LocalDate limit = now.plusDays(days);

		return loanRepo.findAll().stream().filter(l -> !l.isReturned() && !l.getDueDate().isAfter(limit))
				.collect(Collectors.toList());
	}

	/**
	 * Retrieves all loans borrowed within a specific date range.
	 * 
	 * @param start the start date of the range (inclusive)
	 * @param end the end date of the range (inclusive)
	 * @return a list of loans borrowed within the specified date range
	 */
	public List<Loan> getLoansBorrowedInRange(LocalDate start, LocalDate end) {
		return loanRepo.findAll().stream()
				.filter(l -> !l.getBorrowDate().isBefore(start) && !l.getBorrowDate().isAfter(end))
				.collect(Collectors.toList());
	}
}
