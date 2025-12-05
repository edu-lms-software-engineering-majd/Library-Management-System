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
	// The rest of the tests remain unchanged
	// ---------------------------

	@Test
	void givenValidParameters_whenCreateBookLoan_thenLoanIsInitializedCorrectly() {
		assertNotNull(bookLoan);
		assertNotNull(bookLoan.getId());
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

	@Test
	void givenLoanNotOverdue_whenCalculateFine_thenReturnZero() {

		assertEquals(0.0, bookLoan.calculateFine());
	}

	@Test
	void givenOverdueBookLoan_whenCalculateFine_thenReturnCorrectFineAmount() {

		LocalDate borrowDate = LocalDate.now().minusDays(33);
		Loan loan = new Loan(testUserId, testItemId, "book", borrowDate);

		double expectedFine = 5 * 0.50;
		assertEquals(expectedFine, loan.calculateFine());
	}

	@Test
	void givenOverdueCDLoan_whenCalculateFine_thenReturnCorrectFineAmount() {

		LocalDate borrowDate = LocalDate.now().minusDays(26);
		Loan loan = new Loan(testUserId, testItemId, "cd", borrowDate);

		double expectedFine = 5 * 20.0;
		assertEquals(expectedFine, loan.calculateFine());
	}

	@Test
	void givenOverdueJournalLoan_whenCalculateFine_thenReturnCorrectFineAmount() {

		LocalDate borrowDate = LocalDate.now().minusDays(10);
		Loan loan = new Loan(testUserId, testItemId, "journal", borrowDate);

		double expectedFine = 3 * 15.0;
		assertEquals(expectedFine, loan.calculateFine());
	}

	@Test
	void givenOverdueGenericLoan_whenCalculateFine_thenReturnCorrectFineAmount() {

		LocalDate borrowDate = LocalDate.now().minusDays(24);
		Loan loan = new Loan(testUserId, testItemId, "magazine", borrowDate);

		double expectedFine = 10 * 5.0;
		assertEquals(expectedFine, loan.calculateFine());
	}

	@Test
	void givenReturnedOverdueLoan_whenCalculateFine_thenReturnZero() {

		LocalDate borrowDate = LocalDate.now().minusDays(30);
		Loan loan = new Loan(testUserId, testItemId, "book", borrowDate);
		loan.returnItem();

		assertEquals(0.0, loan.calculateFine());
	}

	@Test
	void givenNewLoan_whenCheckIsFineApplied_thenReturnFalse() {

		assertFalse(bookLoan.isFineApplied());
	}

	@Test
	void givenLoan_whenMarkFineApplied_thenIsFineAppliedReturnsTrue() {

		bookLoan.markFineApplied();

		assertTrue(bookLoan.isFineApplied());
	}

	@Test
	void givenActiveLoanNotOverdue_whenCanExtend_thenReturnTrue() {

		assertTrue(bookLoan.canExtend());
	}

	@Test
	void givenOverdueLoan_whenCanExtend_thenReturnFalse() {

		LocalDate borrowDate = LocalDate.now().minusDays(30);
		Loan loan = new Loan(testUserId, testItemId, "book", borrowDate);

		assertFalse(loan.canExtend());
	}

	@Test
	void givenReturnedLoan_whenCanExtend_thenReturnFalse() {

		bookLoan.returnItem();

		assertFalse(bookLoan.canExtend());
	}

	@Test
	void givenActiveLoanNotOverdue_whenExtendLoan_thenDueDateIsExtended() {

		LocalDate originalDueDate = bookLoan.getDueDate();
		int extensionDays = 7;

		bookLoan.extendLoan(extensionDays);

		assertEquals(originalDueDate.plusDays(extensionDays), bookLoan.getDueDate());
	}

	@Test
	void givenActiveLoan_whenExtendLoanBy14Days_thenDueDateIsExtendedCorrectly() {

		LocalDate originalDueDate = bookLoan.getDueDate();

		bookLoan.extendLoan(14);

		assertEquals(originalDueDate.plusDays(14), bookLoan.getDueDate());
	}

	@Test
	void givenOverdueLoan_whenExtendLoan_thenThrowIllegalStateException() {

		LocalDate borrowDate = LocalDate.now().minusDays(30);
		Loan loan = new Loan(testUserId, testItemId, "book", borrowDate);

		assertThrows(IllegalStateException.class, () -> {
			loan.extendLoan(7);
		});
	}

	@Test
	void givenReturnedLoan_whenExtendLoan_thenThrowIllegalStateException() {

		bookLoan.returnItem();

		assertThrows(IllegalStateException.class, () -> {
			bookLoan.extendLoan(7);
		});
	}

	@Test
	void givenLoan_whenGetLoanId_thenReturnNonNullUUID() {

		assertNotNull(bookLoan.getId());
	}

	@Test
	void givenLoan_whenGetUserId_thenReturnCorrectUserId() {

		assertEquals(testUserId, bookLoan.getUserId());
	}

	@Test
	void givenLoan_whenGetItemId_thenReturnCorrectItemId() {

		assertEquals(testItemId, bookLoan.getItemId());
	}

	@Test
	void givenLoan_whenGetItemType_thenReturnCorrectItemType() {

		assertEquals("book", bookLoan.getItemType());
		assertEquals("cd", cdLoan.getItemType());
		assertEquals("journal", journalLoan.getItemType());
	}

	@Test
	void givenLoan_whenGetBorrowDate_thenReturnCorrectBorrowDate() {

		assertEquals(LocalDate.now(), bookLoan.getBorrowDate());
	}

	@Test
	void givenNewLoan_whenGetReturnDate_thenReturnNull() {

		assertNull(bookLoan.getReturnDate());
	}

	@Test
	void givenReturnedLoan_whenGetReturnDate_thenReturnCurrentDate() {

		bookLoan.returnItem();

		assertNotNull(bookLoan.getReturnDate());
		assertEquals(LocalDate.now(), bookLoan.getReturnDate());
	}

	@Test
	void givenMultipleDifferentLoans_whenCreateLoans_thenEachHasUniqueLoanId() {

		Loan loan1 = new Loan(testUserId, testItemId, "book", LocalDate.now());
		Loan loan2 = new Loan(testUserId, testItemId, "book", LocalDate.now());
		Loan loan3 = new Loan(testUserId, testItemId, "book", LocalDate.now());

		assertFalse(loan1.getId().equals(loan2.getId()));
		assertFalse(loan2.getId().equals(loan3.getId()));
		assertFalse(loan1.getId().equals(loan3.getId()));
	}

	@Test
	void givenLoanExtendedMultipleTimes_whenExtendLoan_thenDueDateAccumulatesCorrectly() {

		LocalDate originalDueDate = bookLoan.getDueDate();

		bookLoan.extendLoan(7);
		bookLoan.extendLoan(7);

		assertEquals(originalDueDate.plusDays(14), bookLoan.getDueDate());
	}

	@Test
	void givenLoanNearDueDate_whenExtendLoan_thenLoanIsNoLongerNearDue() {

		LocalDate borrowDate = LocalDate.now().minusDays(26);
		Loan loan = new Loan(testUserId, testItemId, "book", borrowDate);

		assertTrue(loan.canExtend());
		loan.extendLoan(7);

		assertEquals(borrowDate.plusDays(28 + 7), loan.getDueDate());
		assertFalse(loan.isOverdue());
	}

	@Test
	void givenPastBorrowDate_whenCreateLoan_thenDueDateCalculatesFromBorrowDate() {

		LocalDate pastDate = LocalDate.of(2024, 1, 15);
		Loan loan = new Loan(testUserId, testItemId, "book", pastDate);

		assertEquals(pastDate, loan.getBorrowDate());
		assertEquals(pastDate.plusDays(28), loan.getDueDate());
	}

	@Test
	void givenLoanOverdueBy1Day_whenCalculateFine_thenReturnCorrectAmount() {

		LocalDate borrowDate = LocalDate.now().minusDays(29);
		Loan loan = new Loan(testUserId, testItemId, "book", borrowDate);

		assertEquals(1, loan.getDaysOverdue());
		assertEquals(0.50, loan.calculateFine());
	}

	@Test
	void givenLoanOverdueBy10Days_whenCalculateFine_thenReturnCorrectAmount() {

		LocalDate borrowDate = LocalDate.now().minusDays(38);
		Loan loan = new Loan(testUserId, testItemId, "book", borrowDate);

		assertEquals(10, loan.getDaysOverdue());
		assertEquals(5.00, loan.calculateFine());
	}

	@Test
	void givenFineAlreadyApplied_whenMarkFineAppliedAgain_thenRemainsTrue() {

		bookLoan.markFineApplied();
		assertTrue(bookLoan.isFineApplied());

		bookLoan.markFineApplied();
		assertTrue(bookLoan.isFineApplied());
	}

	@Test
	void givenMixedCaseItemType_whenCreateLoan_thenDueDateCalculatesCorrectly() {

		Loan loan1 = new Loan(testUserId, testItemId, "BooK", LocalDate.now());
		Loan loan2 = new Loan(testUserId, testItemId, "CD", LocalDate.now());
		Loan loan3 = new Loan(testUserId, testItemId, "JoUrNaL", LocalDate.now());

		assertEquals(LocalDate.now().plusDays(28), loan1.getDueDate());
		assertEquals(LocalDate.now().plusDays(21), loan2.getDueDate());
		assertEquals(LocalDate.now().plusDays(7), loan3.getDueDate());
	}

	@Test
	void givenExtendedLoan_whenCheckIsActive_thenStillReturnTrue() {

		bookLoan.extendLoan(7);

		assertTrue(bookLoan.isActive());
	}

	@Test
	void givenExtendedLoan_whenReturnItem_thenLoanIsMarkedReturned() {

		bookLoan.extendLoan(7);
		bookLoan.returnItem();

		assertFalse(bookLoan.isActive());
		assertNotNull(bookLoan.getReturnDate());
	}

	@Test
	void givenLoanReturnedOnDueDate_whenCheckIsOverdue_thenReturnFalse() {

		LocalDate borrowDate = LocalDate.now().minusDays(28);
		Loan loan = new Loan(testUserId, testItemId, "book", borrowDate);

		assertFalse(loan.isOverdue());
		loan.returnItem();

		assertFalse(loan.isOverdue());
	}
}
