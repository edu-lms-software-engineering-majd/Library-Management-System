package lms.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import lms.domain.Loan;
import lms.domain.LoanRepo;

public class StaticLoanRepo implements LoanRepo {
	private final Map<UUID, Loan> loans = new HashMap<>();

	@Override
	public Optional<Loan> findById(UUID loanId) {
		return Optional.ofNullable(loans.get(loanId));
	}

	@Override
	public List<Loan> findByUserId(UUID userId) {
		return loans.values().stream().filter(loan -> loan.getUserId().equals(userId)).collect(Collectors.toList());
	}

	@Override
	public List<Loan> findActiveLoansByUser(UUID userId) {
		return loans.values().stream().filter(loan -> loan.getUserId().equals(userId) && !loan.isReturned())
				.collect(Collectors.toList());
	}

	@Override
	public List<Loan> findOverdueLoans() {
		return loans.values().stream().filter(Loan::isOverdue).collect(Collectors.toList());
	}

	@Override
	public boolean save(Loan loan) {
		if (loans.containsKey(loan.getLoanId())) {
			return false;
		}
		loans.put(loan.getLoanId(), loan);
		return true;
	}

	@Override
	public boolean update(Loan loan) {
		if (!loans.containsKey(loan.getLoanId())) {
			return false;
		}
		loans.put(loan.getLoanId(), loan);
		return true;
	}

	@Override
	public boolean delete(UUID loanId) {
		return loans.remove(loanId) != null;
	}

	@Override
	public List<Loan> findAll() {
		return new ArrayList<>(loans.values());
	}
}