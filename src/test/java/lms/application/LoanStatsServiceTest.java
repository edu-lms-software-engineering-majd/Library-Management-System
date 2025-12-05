package lms.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lms.domain.Loan;
import lms.persistence.StaticLoanRepository;

class LoanStatsServiceTest {

	private StaticLoanRepository loanRepo;
	private LoanStatsService statsService;

	UUID userA;
	UUID userB;
	UUID item1;
	UUID item2;
	UUID item3;

	Loan loan1;
	Loan loan2;
	Loan loan3;
	Loan loan4;
	Loan loan5;

	@BeforeEach
	void setup() throws Exception {

		loanRepo = StaticLoanRepository.getInstance();

		loanRepo.findAll().forEach(l -> {
			try {
				loanRepo.delete(l.getId());
			} catch (Exception ignored) {
			}
		});

		statsService = new LoanStatsService(loanRepo);

		userA = UUID.randomUUID();
		userB = UUID.randomUUID();

		item1 = UUID.randomUUID();
		item2 = UUID.randomUUID();
		item3 = UUID.randomUUID();

		loan1 = new Loan(userA, item1, "book", LocalDate.now().minusDays(3));
		loanRepo.save(loan1);

		loan2 = new Loan(userA, item2, "book", LocalDate.now().minusDays(10));
		loan2.returnItem(LocalDate.now().minusDays(5));
		loanRepo.save(loan2);

		loan3 = new Loan(userB, item2, "book", LocalDate.now().minusDays(30));
		loan3.returnItem(LocalDate.now().minusDays(10));
		loanRepo.save(loan3);

		loan4 = new Loan(userB, item1, "book", LocalDate.now().minusDays(50));
		loanRepo.save(loan4);

		loan5 = new Loan(userB, item3, "cd", LocalDate.now().minusDays(2));
		loanRepo.save(loan5);
	}

	@Test
	void testCountTotalLoans() {
		assertEquals(5, statsService.countTotalLoans());
	}

	@Test
	void testCountActiveLoans() {
		assertEquals(3, statsService.countActiveLoans());
	}

	@Test
	void testCountReturnedLoans() {
		assertEquals(2, statsService.countReturnedLoans());
	}

	@Test
	void testCountOverdueLoans() {
		assertTrue(statsService.countOverdueLoans() >= 1);
	}

	@Test
	void testCountLoansByItemType() {
		assertEquals(4, statsService.countLoansByItemType("book"));
		assertEquals(1, statsService.countLoansByItemType("cd"));
	}

	@Test
	void testCountLoansByUser() throws Exception {
		assertEquals(2, statsService.countLoansByUser(userA));
		assertEquals(3, statsService.countLoansByUser(userB));
	}

	@Test
	void testCountLoansPerItemType() {
		Map<String, Long> counts = statsService.countLoansPerItemType();
		assertEquals(4, counts.get("book"));
		assertEquals(1, counts.get("cd"));
	}

	@Test
	void testCountLoansPerUser() {
		Map<UUID, Long> map = statsService.countLoansPerUser();
		assertEquals(2, map.get(userA));
		assertEquals(3, map.get(userB));
	}

	@Test
	void testCountLoansBorrowedBetween() {
		LocalDate start = LocalDate.now().minusDays(100);
		LocalDate end = LocalDate.now().minusDays(1);

		long count = statsService.countLoansBorrowedBetween(start, end);
		assertEquals(5, count);
	}

	@Test
	void testCountLoansReturnedBetween() {
		LocalDate start = LocalDate.now().minusDays(30);
		LocalDate end = LocalDate.now().minusDays(1);

		long count = statsService.countLoansReturnedBetween(start, end);
		assertEquals(2, count);
	}

	@Test
	void testGetTopBorrowedItems() {
		List<Map.Entry<UUID, Long>> top = statsService.getTopBorrowedItems(2);

		assertEquals(2, top.size());
		assertTrue(top.get(0).getValue() >= top.get(1).getValue());
	}

	@Test
	void testAverageLoanDuration() {
		double avg = statsService.getAverageLoanDuration();
		assertTrue(avg > 0);
	}

	@Test
	void testMaxLoanDuration() {
		long max = statsService.getMaxLoanDuration();
		assertEquals(20, max);
	}

	@Test
	void testMinLoanDuration() {
		long min = statsService.getMinLoanDuration();
		assertEquals(5, min);
	}

	@Test
	void testCountLoansDueSoon() {
		long count = statsService.countLoansDueSoon(10);
		assertTrue(count >= 1);
	}
}
