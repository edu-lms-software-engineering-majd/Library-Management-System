package lms.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lms.domain.strategy.BookFineStrategy;
import lms.domain.strategy.CDFineStrategy;
import lms.domain.strategy.FineStrategy;
import lms.domain.strategy.JournalFineStrategy;

class FineStrategyTest {

	private static FineStrategy bookStrategy;
	private static FineStrategy cdStrategy;
	private static FineStrategy journalStrategy;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {

	}

	@BeforeEach
	void setUp() {
		
		bookStrategy = new BookFineStrategy();
		cdStrategy = new CDFineStrategy();
		journalStrategy = new JournalFineStrategy();
	}

	@AfterEach
	void tearDown() {
		
		bookStrategy = null;
		cdStrategy = null;
		journalStrategy = null;
	}

	@Test
	void givenBookOverdue_whenCalculateFine_thenReturn50CentsPerDay() {
		double fine = bookStrategy.calculateFine(3);
		assertEquals(1.50, fine);
	}

	@Test
	void givenCDOverdue_whenCalculateFine_thenReturn75CentsPerDay() {
		double fine = cdStrategy.calculateFine(2);
		assertEquals(1.50, fine);
	}

	@Test
	void givenJournalOverdue_whenCalculateFine_thenReturn1DollarPerDay() {
		double fine = journalStrategy.calculateFine(4);
		assertEquals(4.0, fine);
	}

	@Test
	void givenNegativeDays_whenCalculateFine_thenThrowIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> {
			bookStrategy.calculateFine(-2);
		});
	}

	@Test
	void givenZeroDays_whenCalculateFine_thenReturnZero() {
		double fineBook = bookStrategy.calculateFine(0);
		double fineCD = cdStrategy.calculateFine(0);
		double fineJournal = journalStrategy.calculateFine(0);

		assertEquals(0.0, fineBook);
		assertEquals(0.0, fineCD);
		assertEquals(0.0, fineJournal);
	}

}

