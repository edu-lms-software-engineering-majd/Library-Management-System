package lms.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LoanTest {

	private Loan bookLoan;
	private Loan cdLoan;
	private Loan journalLoan;
	private UUID testUserId;
	private UUID testItemId;

	@BeforeEach
	void setUp() {
		testUserId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
		testItemId = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
		bookLoan = new Loan(testUserId, testItemId, "book", LocalDate.now());
		cdLoan = new Loan(testUserId, testItemId, "cd", LocalDate.now());
		journalLoan = new Loan(testUserId, testItemId, "journal", LocalDate.now());
	}

	@AfterEach
	void tearDown() {
		bookLoan = null;
		cdLoan = null;
		journalLoan = null;
	}

	// ---------------------------
	// Due Date Tests
	// ---------------------------

	@Test
	void givenBookType_whenCreateLoan_thenDueDateIs28DaysFromBorrowDate() {
		LocalDate b = LocalDate.now();
		Loan loan = new Loan(testUserId, testItemId, "book", b);
		assertEquals(b.plusDays(28), loan.getDueDate());
	}

	@Test
	void givenCDType_whenCreateLoan_thenDueDateIs21DaysFromBorrowDate() {
		LocalDate b = LocalDate.now();
		Loan loan = new Loan(testUserId, testItemId, "cd", b);
		assertEquals(b.plusDays(21), loan.getDueDate());
	}

	@Test
	void givenJournalType_whenCreateLoan_thenDueDateIs7DaysFromBorrowDate() {
		LocalDate b = LocalDate.now();
		Loan loan = new Loan(testUserId, testItemId, "journal", b);
		assertEquals(b.plusDays(7), loan.getDueDate());
	}

	// ---------------------------
	// Overdue / Fine Tests
	// ---------------------------

	@Test
	void givenOverdueBookLoan_whenCalculateFine_thenReturnCorrectFineAmount() {
		LocalDate borrowDate = LocalDate.now().minusDays(33); // 5 overdue
		Loan loan = new Loan(testUserId, testItemId, "book", borrowDate);
		double expectedFine = 5 * 10.0; // Book = 10/day
		assertEquals(expectedFine, loan.calculateFine());
	}

	@Test
	void givenOverdueCDLoan_whenCalculateFine_thenReturnCorrectFineAmount() {
		LocalDate borrowDate = LocalDate.now().minusDays(26); // 5 overdue
		Loan loan = new Loan(testUserId, testItemId, "cd", borrowDate);
		double expectedFine = 5 * 20.0; // CD = 20/day
		assertEquals(expectedFine, loan.calculateFine());
	}

	@Test
	void givenOverdueJournalLoan_whenCalculateFine_thenReturnCorrectFineAmount() {
		LocalDate borrowDate = LocalDate.now().minusDays(10); // 3 overdue
		Loan loan = new Loan(testUserId, testItemId, "journal", borrowDate);
		double expectedFine = 3 * 15.0; // Journal = 15/day
		assertEquals(expectedFine, loan.calculateFine());
	}

	@Test
	void givenOverdueGenericLoan_whenCalculateFine_thenReturnCorrectFineAmount() {
		LocalDate borrowDate = LocalDate.now().minusDays(24); // 10 overdue
		Loan loan = new Loan(testUserId, testItemId, "magazine", borrowDate);
		double expectedFine = 10 * 5.0; // Default = 5/day
		assertEquals(expectedFine, loan.calculateFine());
	}

	@Test
	void givenLoanOverdueBy1Day_whenCalculateFine_thenReturnCorrectAmount() {
		LocalDate b = LocalDate.now().minusDays(29);
		Loan loan = new Loan(testUserId, testItemId, "book", b);
		assertEquals(1, loan.getDaysOverdue());
		assertEquals(10.0, loan.calculateFine()); // 1 * 10
	}

	@Test
	void givenLoanOverdueBy10Days_whenCalculateFine_thenReturnCorrectAmount() {
		LocalDate b = LocalDate.now().minusDays(38);
		Loan loan = new Loan(testUserId, testItemId, "book", b);
		assertEquals(10, loan.getDaysOverdue());
		assertEquals(100.0, loan.calculateFine()); // 10 * 10
	}

	// ---------------------------
	// The rest of the tests remain unchanged
	// ---------------------------

	@Test
	void givenValidParameters_whenCreateBookLoan_thenLoanIsInitializedCorrectly() {
		assertNotNull(bookLoan);
		assertNotNull(bookLoan.getLoanId());
		assertEquals(testUserId, bookLoan.getUserId());
		assertEquals(testItemId, bookLoan.getItemId());
		assertEquals("book", bookLoan.getItemType());
		assertEquals(LocalDate.now(), bookLoan.getBorrowDate());
		assertEquals(LocalDate.now().plusDays(28), bookLoan.getDueDate());
		assertNull(bookLoan.getReturnDate());
		assertTrue(bookLoan.isActive());
	}

	@Test
	void givenReturnedLoan_whenReturnItem_thenLoanIsNoLongerActive() {
		bookLoan.returnItem();
		assertFalse(bookLoan.isActive());
		assertEquals(LocalDate.now(), bookLoan.getReturnDate());
	}

	@Test
	void givenReturnedLoan_whenReturnItemAgain_thenThrowException() {
		bookLoan.returnItem();
		assertThrows(IllegalStateException.class, () -> bookLoan.returnItem());
	}

	@Test
	void givenLoanWithinDueDate_whenCheckIsOverdue_thenReturnFalse() {
		assertFalse(bookLoan.isOverdue());
	}

	@Test
	void givenLoanPastDueDate_whenCheckIsOverdue_thenReturnTrue() {
		LocalDate b = LocalDate.now().minusDays(30);
		Loan loan = new Loan(testUserId, testItemId, "book", b);
		assertTrue(loan.isOverdue());
	}

	@Test
	void givenReturnedLoanPastDueDate_whenCheckIsOverdue_thenReturnFalse() {
		LocalDate b = LocalDate.now().minusDays(30);
		Loan loan = new Loan(testUserId, testItemId, "book", b);
		loan.returnItem();
		assertFalse(loan.isOverdue());
	}

	@Test
	void givenOverdueLoan_whenGetDaysOverdue_thenReturnCorrectNumberOfDays() {
		LocalDate b = LocalDate.now().minusDays(33);
		Loan loan = new Loan(testUserId, testItemId, "book", b);
		assertEquals(5, loan.getDaysOverdue());
	}

	@Test
	void givenReturnedOverdueLoan_whenGetDaysOverdue_thenReturnZero() {
		LocalDate b = LocalDate.now().minusDays(30);
		Loan loan = new Loan(testUserId, testItemId, "book", b);
		loan.returnItem();
		assertEquals(0, loan.getDaysOverdue());
	}

	// جميع الاختبارات الأخرى تبقى كما هي 100%
}
