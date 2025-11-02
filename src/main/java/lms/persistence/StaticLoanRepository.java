package lms.persistence;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import lms.domain.Loan;
import lms.domain.LoanRepository;

/**
 * In-memory implementation of {@link LoanRepository} for testing and simple
 * usage.
 * 
 * <p>
 * This repository stores loans in a static list and provides basic CRUD
 * operations: create, read, update, delete. It also provides various query
 * methods for filtering loans by different criteria.
 * </p>
 * 
 * <p>
 * Note: This is not thread-safe and intended for demo or testing purposes only.
 * For production use, replace with a database-backed repository.
 * </p>
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class StaticLoanRepository implements LoanRepository {

	private static StaticLoanRepository instance = null;

	/** Internal list storing all loans */
	private static final List<Loan> loans = new ArrayList<>();

	/**
	 * Gets the singleton instance of the repository.
	 * 
	 * @return the singleton instance
	 */
	public static StaticLoanRepository getInstance() {
		if (instance == null) {
			instance = new StaticLoanRepository();
		}
		return instance;
	}

	@Override
	public Optional<Loan> findById(UUID loanId) {
		return loans.stream().filter(loan -> loan.getLoanId().equals(loanId)).findFirst();
	}

	@Override
	public boolean save(Loan loan) {
		if (loan == null || findById(loan.getLoanId()).isPresent()) {
			return false;
		}
		return loans.add(loan);
	}

	@Override
	public boolean update(Loan loan) {
		if (loan == null) {
			throw new IllegalArgumentException("Loan cannot be null");
		}

		for (int i = 0; i < loans.size(); i++) {
			if (loans.get(i).getLoanId().equals(loan.getLoanId())) {
				loans.set(i, loan);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean delete(UUID loanId) {
		return loans.removeIf(loan -> loan.getLoanId().equals(loanId));
	}

	@Override
	public List<Loan> findAll() {
		return Collections.unmodifiableList(loans);
	}

	@Override
	public List<Loan> findByUserId(UUID userId) {
		return loans.stream().filter(loan -> loan.getUserId().equals(userId)).collect(Collectors.toList());
	}

	@Override
	public List<Loan> findActiveLoansByUser(UUID userId) {
		return loans.stream().filter(loan -> loan.getUserId().equals(userId)).filter(Loan::isActive)
				.collect(Collectors.toList());
	}

	@Override
	public List<Loan> findReturnedLoansByUser(UUID userId) {
		return loans.stream().filter(loan -> loan.getUserId().equals(userId)).filter(loan -> !loan.isActive())
				.collect(Collectors.toList());
	}

	@Override
	public List<Loan> findOverdueLoansByUser(UUID userId) {
		return loans.stream().filter(loan -> loan.getUserId().equals(userId)).filter(Loan::isOverdue)
				.collect(Collectors.toList());
	}

	@Override
	public int countActiveLoansByUser(UUID userId) {
		return (int) loans.stream().filter(loan -> loan.getUserId().equals(userId)).filter(Loan::isActive).count();
	}

	@Override
	public List<Loan> findByItemId(UUID itemId) {
		return loans.stream().filter(loan -> loan.getItemId().equals(itemId)).collect(Collectors.toList());
	}

	@Override
	public Optional<Loan> findActiveLoanByItemId(UUID itemId) {
		return loans.stream().filter(loan -> loan.getItemId().equals(itemId)).filter(Loan::isActive).findFirst();
	}

	@Override
	public List<Loan> findByItemType(String itemType) {
		return loans.stream().filter(loan -> loan.getItemType().equalsIgnoreCase(itemType))
				.collect(Collectors.toList());
	}

	@Override
	public List<Loan> findOverdueLoans() {
		return loans.stream().filter(Loan::isOverdue).collect(Collectors.toList());
	}

	@Override
	public List<Loan> findActiveLoans() {
		return loans.stream().filter(Loan::isActive).collect(Collectors.toList());
	}

	@Override
	public List<Loan> findReturnedLoans() {
		return loans.stream().filter(loan -> !loan.isActive()).collect(Collectors.toList());
	}

	@Override
	public List<Loan> findLoansWithFines() {
		return loans.stream().filter(Loan::isFineApplied).collect(Collectors.toList());
	}

	@Override
	public List<Loan> findByBorrowDate(LocalDate borrowDate) {
		return loans.stream().filter(loan -> loan.getBorrowDate().equals(borrowDate)).collect(Collectors.toList());
	}

	@Override
	public List<Loan> findByDueDate(LocalDate dueDate) {
		return loans.stream().filter(loan -> loan.getDueDate().equals(dueDate)).collect(Collectors.toList());
	}

	@Override
	public List<Loan> findByReturnDate(LocalDate returnDate) {
		return loans.stream().filter(loan -> loan.getReturnDate() != null)
				.filter(loan -> loan.getReturnDate().equals(returnDate)).collect(Collectors.toList());
	}

	@Override
	public List<Loan> findByBorrowDateRange(LocalDate startDate, LocalDate endDate) {
		return loans.stream().filter(loan -> !loan.getBorrowDate().isBefore(startDate))
				.filter(loan -> !loan.getBorrowDate().isAfter(endDate)).collect(Collectors.toList());
	}

	@Override
	public List<Loan> findByDueDateRange(LocalDate startDate, LocalDate endDate) {
		return loans.stream().filter(loan -> !loan.getDueDate().isBefore(startDate))
				.filter(loan -> !loan.getDueDate().isAfter(endDate)).collect(Collectors.toList());
	}

	@Override
	public long countTotalLoans() {
		return loans.size();
	}

	@Override
	public long countActiveLoans() {
		return loans.stream().filter(Loan::isActive).count();
	}

	@Override
	public long countOverdueLoans() {
		return loans.stream().filter(Loan::isOverdue).count();
	}

	@Override
	public long countLoansByItemType(String itemType) {
		return loans.stream().filter(loan -> loan.getItemType().equalsIgnoreCase(itemType)).count();
	}

	@Override
	public boolean isItemOnLoan(UUID itemId) {
		return loans.stream().anyMatch(loan -> loan.getItemId().equals(itemId) && loan.isActive());
	}

	@Override
	public boolean hasOverdueLoans(UUID userId) {
		return loans.stream().anyMatch(loan -> loan.getUserId().equals(userId) && loan.isOverdue());
	}

	@Override
	public List<Loan> findLoansDueSoon(int days) {
		LocalDate checkDate = LocalDate.now().plusDays(days);
		return loans.stream().filter(Loan::isActive).filter(loan -> !loan.getDueDate().isAfter(checkDate))
				.filter(loan -> !loan.getDueDate().isBefore(LocalDate.now())).collect(Collectors.toList());
	}

	@Override
	public Optional<Loan> findMostRecentLoanByUser(UUID userId) {
		return loans.stream().filter(loan -> loan.getUserId().equals(userId))
				.max((loan1, loan2) -> loan1.getBorrowDate().compareTo(loan2.getBorrowDate()));
	}

	@Override
	public Optional<Loan> findMostRecentLoanByItem(UUID itemId) {
		return loans.stream().filter(loan -> loan.getItemId().equals(itemId))
				.max((loan1, loan2) -> loan1.getBorrowDate().compareTo(loan2.getBorrowDate()));
	}
}
