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
import lms.domain.exception.ItemNotFoundException;
import lms.domain.exception.ItemTypeNotFoundException;
import lms.domain.exception.LoanAlreadyExistsException;
import lms.domain.exception.LoanNotFoundException;
import lms.domain.exception.UserNotFoundException;

public class StaticLoanRepository implements LoanRepository {

	private static final Map<UUID, Loan> loans = new HashMap<>();

	// ===============================
	// Helpers
	// ===============================
	private void validateLoanExists(UUID loanId) throws LoanNotFoundException {
		if (!loans.containsKey(loanId))
			throw new LoanNotFoundException("Loan not found: " + loanId);
	}

	private void validateUserExists(UUID userId) throws UserNotFoundException {
		boolean exists = loans.values().stream().anyMatch(l -> l.getUserId().equals(userId));
		if (!exists)
			throw new UserNotFoundException("User has no loans: " + userId);
	}

	private void validateItemExists(UUID itemId) throws ItemNotFoundException {
		boolean exists = loans.values().stream().anyMatch(l -> l.getItemId().equals(itemId));
		if (!exists)
			throw new ItemNotFoundException("Item has no loans: " + itemId);
	}

	// ===============================
	// CRUD
	// ===============================

	@Override
	public Optional<Loan> findById(UUID loanId) {
		return Optional.ofNullable(loans.get(loanId));
	}

	@Override
	public boolean save(Loan loan) throws LoanAlreadyExistsException {
		if (loans.containsKey(loan.getLoanId()))
			throw new LoanAlreadyExistsException();

		loans.put(loan.getLoanId(), loan);
		return true;
	}

	@Override
	public boolean update(Loan loan) throws LoanNotFoundException {
		validateLoanExists(loan.getLoanId());
		loans.put(loan.getLoanId(), loan);
		return true;
	}

	@Override
	public boolean delete(UUID loanId) throws LoanNotFoundException {
		validateLoanExists(loanId);
		loans.remove(loanId);
		return true;
	}

	@Override
	public List<Loan> findAll() {
		return new ArrayList<>(loans.values());
	}

	// ===============================
	// User-based queries
	// ===============================

	@Override
	public List<Loan> findByUserId(UUID userId) throws UserNotFoundException {
		validateUserExists(userId);
		return loans.values().stream().filter(l -> l.getUserId().equals(userId)).collect(Collectors.toList());
	}

	@Override
	public List<Loan> findActiveLoansByUser(UUID userId) throws UserNotFoundException {
		validateUserExists(userId);
		return loans.values().stream().filter(l -> l.getUserId().equals(userId) && !l.isReturned())
				.collect(Collectors.toList());
	}

	@Override
	public List<Loan> findReturnedLoansByUser(UUID userId) throws UserNotFoundException {
		validateUserExists(userId);
		return loans.values().stream().filter(l -> l.getUserId().equals(userId) && l.isReturned())
				.collect(Collectors.toList());
	}

	@Override
	public List<Loan> findOverdueLoansByUser(UUID userId) throws UserNotFoundException {
		validateUserExists(userId);
		return loans.values().stream().filter(l -> l.getUserId().equals(userId) && l.isOverdue())
				.collect(Collectors.toList());
	}

	@Override
	public int countActiveLoansByUser(UUID userId) throws UserNotFoundException {
		return findActiveLoansByUser(userId).size();
	}

	// ===============================
	// Item-based queries
	// ===============================

	@Override
	public List<Loan> findByItemId(UUID itemId) throws ItemNotFoundException {
		validateItemExists(itemId);
		return loans.values().stream().filter(l -> l.getItemId().equals(itemId)).collect(Collectors.toList());
	}

	@Override
	public Optional<Loan> findActiveLoanByItemId(UUID itemId) throws ItemNotFoundException {
		validateItemExists(itemId);
		return loans.values().stream().filter(l -> l.getItemId().equals(itemId) && !l.isReturned()).findFirst();
	}

	@Override
	public List<Loan> findByItemType(String itemType) throws ItemTypeNotFoundException {
		List<Loan> found = loans.values().stream().filter(l -> l.getItemType().equalsIgnoreCase(itemType))
				.collect(Collectors.toList());

		if (found.isEmpty())
			throw new ItemTypeNotFoundException("No loans with item type: " + itemType);

		return found;
	}

	// ===============================
	// General filters
	// ===============================

	@Override
	public List<Loan> findOverdueLoans() {
		return loans.values().stream().filter(Loan::isOverdue).toList();
	}

	@Override
	public List<Loan> findActiveLoans() {
		return loans.values().stream().filter(l -> !l.isReturned()).toList();
	}

	@Override
	public List<Loan> findReturnedLoans() {
		return loans.values().stream().filter(Loan::isReturned).toList();
	}

	@Override
	public List<Loan> findLoansWithFines() {
		return loans.values().stream().filter(Loan::isFineApplied).toList();
	}

	@Override
	public List<Loan> findByBorrowDate(LocalDate borrowDate) {
		return loans.values().stream().filter(l -> l.getBorrowDate().equals(borrowDate)).toList();
	}

	@Override
	public List<Loan> findByDueDate(LocalDate dueDate) {
		return loans.values().stream().filter(l -> l.getDueDate().equals(dueDate)).toList();
	}

	@Override
	public List<Loan> findByReturnDate(LocalDate returnDate) {
		return loans.values().stream().filter(l -> returnDate.equals(l.getReturnDate())).toList();
	}

	@Override
	public List<Loan> findByBorrowDateRange(LocalDate start, LocalDate end) {
		return loans.values().stream()
				.filter(l -> !l.getBorrowDate().isBefore(start) && !l.getBorrowDate().isAfter(end)).toList();
	}

	@Override
	public List<Loan> findByDueDateRange(LocalDate start, LocalDate end) {
		return loans.values().stream().filter(l -> !l.getDueDate().isBefore(start) && !l.getDueDate().isAfter(end))
				.toList();
	}

	// ===============================
	// Stats
	// ===============================

	@Override
	public long countTotalLoans() {
		return loans.size();
	}

	@Override
	public long countActiveLoans() {
		return loans.values().stream().filter(l -> !l.isReturned()).count();
	}

	@Override
	public long countOverdueLoans() {
		return loans.values().stream().filter(Loan::isOverdue).count();
	}

	@Override
	public long countLoansByItemType(String itemType) {
		return loans.values().stream().filter(l -> l.getItemType().equalsIgnoreCase(itemType)).count();
	}

	@Override
	public boolean isItemOnLoan(UUID itemId) {
		return loans.values().stream().anyMatch(l -> l.getItemId().equals(itemId) && !l.isReturned());
	}

	@Override
	public boolean hasOverdueLoans(UUID userId) {
		return loans.values().stream().anyMatch(l -> l.getUserId().equals(userId) && l.isOverdue());
	}

	@Override
	public List<Loan> findLoansDueSoon(int days) {
		LocalDate now = LocalDate.now();
		LocalDate limit = now.plusDays(days);

		return loans.values().stream().filter(l -> !l.isReturned() && !l.getDueDate().isAfter(limit)).toList();
	}

	@Override
	public Optional<Loan> findMostRecentLoanByUser(UUID userId) {
		return loans.values().stream().filter(l -> l.getUserId().equals(userId))
				.max(Comparator.comparing(Loan::getBorrowDate));
	}

	@Override
	public Optional<Loan> findMostRecentLoanByItem(UUID itemId) {
		return loans.values().stream().filter(l -> l.getItemId().equals(itemId))
				.max(Comparator.comparing(Loan::getBorrowDate));
	}
}
