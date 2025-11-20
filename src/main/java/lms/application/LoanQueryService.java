package lms.application;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import lms.domain.Loan;
import lms.domain.LoanRepository;

public class LoanQueryService {

	private final LoanRepository loanRepo;

	public LoanQueryService(LoanRepository loanRepo) {
		if (loanRepo == null)
			throw new IllegalArgumentException("LoanRepository cannot be null");

		this.loanRepo = loanRepo;
	}

	// ================================================================
	// Basic Filters
	// ================================================================
	public List<Loan> getActiveLoans() {
		return loanRepo.getAllLoans().stream().filter(l -> !l.isReturned()).collect(Collectors.toList());
	}

	public List<Loan> getReturnedLoans() {
		return loanRepo.getAllLoans().stream().filter(Loan::isReturned).collect(Collectors.toList());
	}

	public List<Loan> getLoansByUser(UUID userId) {
		return loanRepo.getAllLoans().stream().filter(l -> l.getUserId().equals(userId)).collect(Collectors.toList());
	}

	public List<Loan> getLoansByItem(UUID itemId) {
		return loanRepo.getAllLoans().stream().filter(l -> l.getItemId().equals(itemId)).collect(Collectors.toList());
	}

	public List<Loan> getLoansByItemType(String itemType) {
		return loanRepo.getAllLoans().stream().filter(l -> l.getItemType().equalsIgnoreCase(itemType))
				.collect(Collectors.toList());
	}

	// ================================================================
	// Overdue & Due Soon
	// ================================================================
	public List<Loan> getOverdueLoans() {
		return loanRepo.getAllLoans().stream().filter(Loan::isOverdue).collect(Collectors.toList());
	}

	public List<Loan> getOverdueLoansByUser(UUID userId) {
		return loanRepo.getAllLoans().stream().filter(l -> l.getUserId().equals(userId)).filter(Loan::isOverdue)
				.collect(Collectors.toList());
	}

	public List<Loan> getLoansDueSoon(int days) {
		LocalDate now = LocalDate.now();
		LocalDate target = now.plusDays(days);

		return loanRepo.getAllLoans().stream().filter(l -> !l.isReturned())
				.filter(l -> l.getDueDate().isAfter(now) && l.getDueDate().isBefore(target))
				.collect(Collectors.toList());
	}

	// ================================================================
	// Date Range Filters
	// ================================================================
	public List<Loan> getLoansBorrowedBetween(LocalDate start, LocalDate end) {
		return loanRepo.getAllLoans().stream().filter(l -> !l.getBorrowDate().isBefore(start))
				.filter(l -> !l.getBorrowDate().isAfter(end)).collect(Collectors.toList());
	}

	public List<Loan> getLoansDueBetween(LocalDate start, LocalDate end) {
		return loanRepo.getAllLoans().stream().filter(l -> !l.getDueDate().isBefore(start))
				.filter(l -> !l.getDueDate().isAfter(end)).collect(Collectors.toList());
	}

	public List<Loan> getLoansReturnedBetween(LocalDate start, LocalDate end) {
		return loanRepo.getAllLoans().stream().filter(Loan::isReturned).filter(l -> !l.getReturnDate().isBefore(start))
				.filter(l -> !l.getReturnDate().isAfter(end)).collect(Collectors.toList());
	}

	// ================================================================
	// Searching
	// ================================================================
	public List<Loan> searchLoans(String keyword) {
		if (keyword == null || keyword.isBlank())
			return loanRepo.getAllLoans();

		String lower = keyword.toLowerCase();

		return loanRepo
				.getAllLoans().stream().filter(l -> l.getItemType().toLowerCase().contains(lower)
						|| l.getUserId().toString().contains(lower) || l.getItemId().toString().contains(lower))
				.collect(Collectors.toList());
	}
}
