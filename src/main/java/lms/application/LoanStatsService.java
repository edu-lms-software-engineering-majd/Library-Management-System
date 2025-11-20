package lms.application;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import lms.domain.Loan;
import lms.domain.LoanRepository;

public class LoanStatsService {

	private final LoanRepository loanRepo;

	public LoanStatsService(LoanRepository loanRepo) {
		if (loanRepo == null)
			throw new IllegalArgumentException("LoanRepository cannot be null");

		this.loanRepo = loanRepo;
	}

	// =============================================================
	// Basic Counts
	// =============================================================
	public long countTotalLoans() {
		return loanRepo.getAllLoans().size();
	}

	public long countActiveLoans() {
		return loanRepo.getAllLoans().stream().filter(l -> !l.isReturned()).count();
	}

	public long countReturnedLoans() {
		return loanRepo.getAllLoans().stream().filter(Loan::isReturned).count();
	}

	public long countOverdueLoans() {
		return loanRepo.getAllLoans().stream().filter(Loan::isOverdue).count();
	}

	public long countLoansByItemType(String itemType) {
		return loanRepo.getAllLoans().stream().filter(l -> l.getItemType().equalsIgnoreCase(itemType)).count();
	}

	public long countLoansByUser(UUID userId) {
		return loanRepo.getAllLoans().stream().filter(l -> l.getUserId().equals(userId)).count();
	}

	// =============================================================
	// Aggregations & Grouping
	// =============================================================
	public Map<String, Long> countLoansPerItemType() {
		return loanRepo.getAllLoans().stream().collect(Collectors.groupingBy(Loan::getItemType, Collectors.counting()));
	}

	public Map<UUID, Long> countLoansPerUser() {
		return loanRepo.getAllLoans().stream().collect(Collectors.groupingBy(Loan::getUserId, Collectors.counting()));
	}

	// =============================================================
	// Date Range Statistics
	// =============================================================
	public long countLoansBorrowedBetween(LocalDate start, LocalDate end) {
		return loanRepo.getAllLoans().stream().filter(l -> !l.getBorrowDate().isBefore(start))
				.filter(l -> !l.getBorrowDate().isAfter(end)).count();
	}

	public long countLoansReturnedBetween(LocalDate start, LocalDate end) {
		return loanRepo.getAllLoans().stream().filter(Loan::isReturned).filter(l -> !l.getReturnDate().isBefore(start))
				.filter(l -> !l.getReturnDate().isAfter(end)).count();
	}

	// =============================================================
	// Top borrowed items
	// =============================================================
	public List<Map.Entry<UUID, Long>> getTopBorrowedItems(int limit) {
		return loanRepo.getAllLoans().stream().collect(Collectors.groupingBy(Loan::getItemId, Collectors.counting()))
				.entrySet().stream().sorted(Map.Entry.<UUID, Long>comparingByValue().reversed()).limit(limit).toList();
	}

	// =============================================================
	// Loan duration statistics
	// =============================================================
	public double getAverageLoanDuration() {
		List<Long> durations = loanRepo.getAllLoans().stream().filter(Loan::isReturned)
				.map(l -> ChronoUnit.DAYS.between(l.getBorrowDate(), l.getReturnDate())).toList();

		if (durations.isEmpty())
			return 0.0;

		return durations.stream().mapToLong(Long::longValue).average().orElse(0.0);
	}

	public long getMaxLoanDuration() {
		return loanRepo.getAllLoans().stream().filter(Loan::isReturned)
				.mapToLong(l -> ChronoUnit.DAYS.between(l.getBorrowDate(), l.getReturnDate())).max().orElse(0);
	}

	public long getMinLoanDuration() {
		return loanRepo.getAllLoans().stream().filter(Loan::isReturned)
				.mapToLong(l -> ChronoUnit.DAYS.between(l.getBorrowDate(), l.getReturnDate())).min().orElse(0);
	}

	// =============================================================
	// Loans due soon stats
	// =============================================================
	public long countLoansDueSoon(int days) {
		LocalDate now = LocalDate.now();
		LocalDate target = now.plusDays(days);

		return loanRepo.getAllLoans().stream().filter(l -> !l.isReturned())
				.filter(l -> l.getDueDate().isAfter(now) && l.getDueDate().isBefore(target)).count();
	}
}
