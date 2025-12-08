package lms.application;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import lms.domain.Loan;
import lms.domain.LoanRepository;
import lms.domain.exception.UserNotFoundException;

/**
 * Service providing statistical analysis and reporting for library loans.
 * 
 * <p>
 * This service offers various statistical methods to analyze loan data including:
 * </p>
 * <ul>
 * <li>Counting loans by status (active, returned, overdue)</li>
 * <li>Analyzing loans by item type and user</li>
 * <li>Tracking loans over time periods</li>
 * <li>Identifying top borrowed items</li>
 * <li>Calculating average loan durations</li>
 * </ul>
 * 
 * <p>
 * This service is particularly useful for:
 * </p>
 * <ul>
 * <li>Administrative dashboards and reports</li>
 * <li>Library performance analysis</li>
 * <li>Identifying popular items and usage patterns</li>
 * <li>Planning and decision-making</li>
 * </ul>
 * 
 * @author Majd Awwad
 * @version 2.0
 * @see LoanQueryService
 * @see Loan
 */
public class LoanStatsService {

	private final LoanRepository loanRepo;

	/**
	 * Constructs a new statistics service with the required repository.
	 * 
	 * @param loanRepo the loan repository for accessing loan data
	 * @throws IllegalArgumentException if loanRepo is null
	 */
	public LoanStatsService(LoanRepository loanRepo) {
		if (loanRepo == null)
			throw new IllegalArgumentException("LoanRepository cannot be null");
		this.loanRepo = loanRepo;
	}

	
	 
	public long countTotalLoans() {
		return loanRepo.findAll().size();
	}

	public long countActiveLoans() {
		return loanRepo.countActiveLoans();
	}

	public long countReturnedLoans() {
		return loanRepo.findReturnedLoans().size();
	}

	public long countOverdueLoans() {
		return loanRepo.countOverdueLoans();
	}

	public long countLoansByItemType(String itemType) {
		return loanRepo.countLoansByItemType(itemType);
	}

	public long countLoansByUser(UUID userId) throws UserNotFoundException {
		return loanRepo.findByUserId(userId).size();
	}

	
	 
	public Map<String, Long> countLoansPerItemType() {
		return loanRepo.findAll().stream().collect(Collectors.groupingBy(Loan::getItemType, Collectors.counting()));
	}

	public Map<UUID, Long> countLoansPerUser() {
		return loanRepo.findAll().stream().collect(Collectors.groupingBy(Loan::getUserId, Collectors.counting()));
	}

	 
	public long countLoansBorrowedBetween(LocalDate start, LocalDate end) {
		return loanRepo.findAll().stream().filter(l -> !l.getBorrowDate().isBefore(start))
				.filter(l -> !l.getBorrowDate().isAfter(end)).count();
	}

	public long countLoansReturnedBetween(LocalDate start, LocalDate end) {
		return loanRepo.findReturnedLoans().stream().filter(l -> !l.getReturnDate().isBefore(start))
				.filter(l -> !l.getReturnDate().isAfter(end)).count();
	}

	 
	public List<Map.Entry<UUID, Long>> getTopBorrowedItems(int limit) {
		return loanRepo.findAll().stream().collect(Collectors.groupingBy(Loan::getItemId, Collectors.counting()))
				.entrySet().stream().sorted(Map.Entry.<UUID, Long>comparingByValue().reversed()).limit(limit).toList();
	}

 
	public double getAverageLoanDuration() {
		List<Long> durations = loanRepo.findReturnedLoans().stream()
				.map(l -> ChronoUnit.DAYS.between(l.getBorrowDate(), l.getReturnDate())).toList();

		if (durations.isEmpty())
			return 0.0;

		return durations.stream().mapToLong(Long::longValue).average().orElse(0.0);
	}

	public long getMaxLoanDuration() {
		return loanRepo.findReturnedLoans().stream()
				.mapToLong(l -> ChronoUnit.DAYS.between(l.getBorrowDate(), l.getReturnDate())).max().orElse(0);
	}

	public long getMinLoanDuration() {
		return loanRepo.findReturnedLoans().stream()
				.mapToLong(l -> ChronoUnit.DAYS.between(l.getBorrowDate(), l.getReturnDate())).min().orElse(0);
	}
 
	public long countLoansDueSoon(int days) {
		return loanRepo.findLoansDueSoon(days).size();
	}
}
