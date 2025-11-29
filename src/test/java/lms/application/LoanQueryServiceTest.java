package lms.application;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lms.domain.Loan;
import lms.persistence.StaticLoanRepository;

class LoanQueryServiceTest {

	private StaticLoanRepository loanRepo;
	private LoanQueryService queryService;

	private UUID userA;
	private UUID userB;
	private UUID item1;
	private UUID item2;

	Loan loan1;
	Loan loan2;
	Loan loan3;
	Loan loan4;

	@BeforeEach
	void setup() throws Exception {
		loanRepo = StaticLoanRepository.getInstance();

		loanRepo.findAll().forEach(l -> {
			try {
				loanRepo.delete(l.getLoanId());
			} catch (Exception ignored) {
			}
		});

		queryService = new LoanQueryService(loanRepo);

		userA = UUID.randomUUID();
		userB = UUID.randomUUID();
		item1 = UUID.randomUUID();
		item2 = UUID.randomUUID();

		loan1 = new Loan(userA, item1, "book", LocalDate.now().minusDays(3));
		loanRepo.save(loan1);

		loan2 = new Loan(userA, item2, "book", LocalDate.now().minusDays(10));
		loan2.returnItem();
		loanRepo.save(loan2);

		loan3 = new Loan(userB, item1, "book", LocalDate.now().minusDays(40));
		loanRepo.save(loan3);

		loan4 = new Loan(userB, UUID.randomUUID(), "cd", LocalDate.now());
		loanRepo.save(loan4);
	}

	@Test
	void testGetActiveLoans() {
		List<Loan> active = queryService.getActiveLoans();
		assertTrue(active.contains(loan1));
		assertTrue(active.contains(loan3));
		assertFalse(active.contains(loan2));
	}

	@Test
	void testGetReturnedLoans() {
		List<Loan> returned = queryService.getReturnedLoans();
		assertEquals(1, returned.size());
		assertTrue(returned.contains(loan2));
	}

	@Test
	void testGetLoansByUser() {
		List<Loan> loansUserA = queryService.getLoansByUser(userA);
		assertEquals(2, loansUserA.size());

		List<Loan> loansUserB = queryService.getLoansByUser(userB);
		assertEquals(2, loansUserB.size());
	}

	@Test
	void testGetLoansByItem() {
		List<Loan> itemLoans = queryService.getLoansByItem(item1);
		assertEquals(2, itemLoans.size());
		assertTrue(itemLoans.contains(loan1));
		assertTrue(itemLoans.contains(loan3));
	}

	@Test
	void testGetOverdueLoans() {
		List<Loan> overdue = queryService.getOverdueLoans();
		assertTrue(overdue.contains(loan3));
		assertFalse(overdue.contains(loan1));
		assertFalse(overdue.contains(loan2));
	}

	@Test
	void testGetLoansByItemType() {
		List<Loan> cds = queryService.getLoansByItemType("cd");
		assertEquals(1, cds.size());
		assertTrue(cds.contains(loan4));
	}

	@Test
	void testGetLoansByBorrowDate() {
		List<Loan> todayLoans = queryService.getLoansByBorrowDate(LocalDate.now());
		assertTrue(todayLoans.contains(loan4));
	}

	@Test
	void testGetLoansDueSoon() {
		List<Loan> soonDue = queryService.getLoansDueSoon(5);
		assertNotNull(soonDue);
	}

	@Test
	void testGetLoansInRange() {
		LocalDate start = LocalDate.now().minusDays(50);
		LocalDate end = LocalDate.now().minusDays(1);

		List<Loan> loans = queryService.getLoansBorrowedInRange(start, end);

		assertTrue(loans.contains(loan1));
		assertTrue(loans.contains(loan2));
		assertTrue(loans.contains(loan3));
		assertFalse(loans.contains(loan4));
	}
}
