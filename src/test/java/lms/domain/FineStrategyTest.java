package lms.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lms.domain.strategy.FineStrategy;

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
	void setUp() throws Exception {
		bookStrategy = null;
		cdStrategy = null;
		journalStrategy = null;
	}

	@AfterEach
	void tearDown() throws Exception {

		bookStrategy = null;
		cdStrategy = null;
		journalStrategy = null;
	}

	@Test
	void givenBookOverdue_whenCalculateFine_thenReturn10PerDay() {
		double fine = bookStrategy.calculateFine(3);
		assertEquals(30.0, fine);
	}

	@Test
	void givenCDOverdue_whenCalculateFine_thenReturn20PerDay() {
		double fine = cdStrategy.calculateFine(2);
		assertEquals(40.0, fine);
	}

	@Test
	void givenJournalOverdue_whenCalculateFine_thenReturn15PerDay() {
		double fine = journalStrategy.calculateFine(4);
		assertEquals(60.0, fine);
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
