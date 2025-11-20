package lms.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import lms.domain.Loan;
import lms.domain.LoanRepository;

public class StaticLoanRepository implements LoanRepository {

	private static final StaticLoanRepository INSTANCE = new StaticLoanRepository();

	/** In-memory storage */
	private static final Map<UUID, Loan> loans = new HashMap<>();

	private StaticLoanRepository() {
	}

	public static StaticLoanRepository getInstance() {
		return INSTANCE;
	}

	// ============================================
	// Validation
	// ============================================
	private void validate(Loan loan) {
		if (loan == null)
			throw new IllegalArgumentException("Loan cannot be null");

		if (loan.getLoanId() == null)
			throw new IllegalArgumentException("Loan ID cannot be null");

		if (loan.getUserId() == null)
			throw new IllegalArgumentException("Loan must reference a user");

		if (loan.getItemId() == null)
			throw new IllegalArgumentException("Loan must reference an item");

		if (loan.getLoanDate() == null)
			throw new IllegalArgumentException("Loan date cannot be null");

		if (loan.getDueDate() == null)
			throw new IllegalArgumentException("Due date cannot be null");
	}

	// ============================================
	// CRUD
	// ============================================
	@Override
	public boolean addLoan(Loan loan) {
		validate(loan);

		if (loans.containsKey(loan.getLoanId()))
			throw new IllegalArgumentException("Loan already exists.");

		loans.put(loan.getLoanId(), loan);
		return true;
	}

	@Override
	public boolean updateLoan(Loan loan) {
		validate(loan);

		if (!loans.containsKey(loan.getLoanId()))
			return false;

		loans.put(loan.getLoanId(), loan);
		return true;
	}

	@Override
	public boolean deleteLoan(UUID loanId) {
		if (loanId == null)
			throw new IllegalArgumentException("Loan ID cannot be null");

		return loans.remove(loanId) != null;
	}

	@Override
	public Optional<Loan> getLoanById(UUID loanId) {
		if (loanId == null)
			return Optional.empty();
		return Optional.ofNullable(loans.get(loanId));
	}

	@Override
	public List<Loan> getAllLoans() {
		return Collections.unmodifiableList(new ArrayList<>(loans.values()));
	}

	// ============================================
	// Basic Queries (allowed)
	// ============================================
	@Override
	public List<Loan> getLoansByUser(UUID userId) {
		if (userId == null)
			return List.of();

		return loans.values().stream().filter(l -> l.getUserId().equals(userId)).toList();
	}

	@Override
	public List<Loan> getLoansByItem(UUID itemId) {
		if (itemId == null)
			return List.of();

		return loans.values().stream().filter(l -> l.getItemId().equals(itemId)).toList();
	}

	@Override
	public List<Loan> getActiveLoans() {
		return loans.values().stream().filter(l -> !l.isReturned()).toList();
	}
}
