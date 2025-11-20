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

	// ===============================
	// Basic Filters
	// ===============================

	public List<Loan> getActiveLoans() {
		return loanRepo.findAll().stream().filter(l -> !l.isReturned()).collect(Collectors.toList());
	}

	public List<Loan> getLoansByItemType(String itemType) {
		return loanRepo.findAll().stream().filter(l -> l.getItemType().equalsIgnoreCase(itemType))
				.collect(Collectors.toList());
	}

	public List<Loan> getReturnedLoans() {
		return loanRepo.findAll().stream().filter(Loan::isReturned).collect(Collectors.toList());
	}

	public List<Loan> getLoansByUser(UUID userId) {
		return loanRepo.findAll().stream().filter(l -> l.getUserId().equals(userId)).collect(Collectors.toList());
	}

	public List<Loan> getLoansByItem(UUID itemId) {
		return loanRepo.findAll().stream().filter(l -> l.getItemId().equals(itemId)).collect(Collectors.toList());
	}

	public List<Loan> getOverdueLoans() {
		return loanRepo.findAll().stream().filter(Loan::isOverdue).collect(Collectors.toList());
	}

	public List<Loan> getLoansByBorrowDate(LocalDate borrowDate) {
		return loanRepo.findAll().stream().filter(l -> l.getBorrowDate().equals(borrowDate))
				.collect(Collectors.toList());
	}

	public List<Loan> getLoansByDueDate(LocalDate dueDate) {
		return loanRepo.findAll().stream().filter(l -> l.getDueDate().equals(dueDate)).collect(Collectors.toList());
	}

	public List<Loan> getLoansByReturnDate(LocalDate returnDate) {
		return loanRepo.findAll().stream().filter(l -> returnDate.equals(l.getReturnDate()))
				.collect(Collectors.toList());
	}

	public List<Loan> getLoansDueSoon(int days) {
		LocalDate now = LocalDate.now();
		LocalDate limit = now.plusDays(days);

		return loanRepo.findAll().stream().filter(l -> !l.isReturned() && !l.getDueDate().isAfter(limit))
				.collect(Collectors.toList());
	}

	public List<Loan> getLoansBorrowedInRange(LocalDate start, LocalDate end) {
		return loanRepo.findAll().stream()
				.filter(l -> !l.getBorrowDate().isBefore(start) && !l.getBorrowDate().isAfter(end))
				.collect(Collectors.toList());
	}
}
