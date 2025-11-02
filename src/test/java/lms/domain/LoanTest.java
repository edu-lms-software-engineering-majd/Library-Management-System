package lms.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LoanTest {

	private static final int BOOK_LOAN_DAYS = 28;
	private static final int CD_LOAN_DAYS = 21;
	private static final int JOURNAL_LOAN_DAYS = 7;
	private static final int DEFAULT_LOAN_DAYS = 14;

	private Loan bookLoan;
	private Loan cdLoan;
	private Loan journalLoan;
	private UUID testUserId;
	private UUID testItemId;

	@BeforeEach
	void setUp() {
		testUserId = UUID.randomUUID();
		testItemId = UUID.randomUUID();
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

	@Test
	void givenValidInputs_whenCreateBookLoan_thenLoanIsInitializedCorrectly() {
		assertNotNull(bookLoan.getLoanId());
		assertEquals(testUserId, bookLoan.getUserId());
		assertEquals(testItemId, bookLoan.getItemId());
		assertEquals("book", bookLoan.getItemType());
		assertNotNull(bookLoan.getBorrowDate());
		assertEquals(LocalDate.now().plusDays(BOOK_LOAN_DAYS), bookLoan.getDueDate());
		assertFalse(bookLoan.isReturned());
		assertTrue(bookLoan.isActive());
	}

	@Test
	void givenValidInputs_whenCreateCDLoan_thenDueDateIsCalculatedCorrectly() {
		assertEquals(LocalDate.now().plusDays(CD_LOAN_DAYS), cdLoan.getDueDate());
	}

	@Test
	void givenValidInputs_whenCreateJournalLoan_thenDueDateIsCalculatedCorrectly() {
		assertEquals(LocalDate.now().plusDays(JOURNAL_LOAN_DAYS), journalLoan.getDueDate());
	}

	@Test
	void givenUnknownItemType_whenCreateLoan_thenDefaultDueDateIsUsed() {
		Loan genericLoan = new Loan(testUserId, testItemId, "unknown", LocalDate.now());
		assertEquals(LocalDate.now().plusDays(DEFAULT_LOAN_DAYS), genericLoan.getDueDate());
	}

	@Test
	void givenActiveLoan_whenReturnItem_thenLoanIsMarkedAsReturned() {
		bookLoan.returnItem();

		assertTrue(bookLoan.isReturned());
		assertFalse(bookLoan.isActive());
		assertEquals(LocalDate.now(), bookLoan.getReturnDate());
	}

	@Test
	void givenReturnedLoan_whenReturnItemAgain_thenThrowIllegalStateException() {
		bookLoan.returnItem();

		assertThrows(IllegalStateException.class, () -> bookLoan.returnItem());
	}

	@Test
	void givenLoanWithinDueDate_whenCheckOverdue_thenReturnsFalse() {
		assertFalse(bookLoan.isOverdue());
	}

	@Test
	void givenLoanPastDueDate_whenCheckOverdue_thenReturnsTrue() {
		bookLoan.setDueDate(LocalDate.now().minusDays(1));

		assertTrue(bookLoan.isOverdue());
	}

	@Test
	void givenReturnedLoan_whenCheckOverdue_thenReturnsFalse() {
		bookLoan.setDueDate(LocalDate.now().minusDays(5));
		bookLoan.returnItem();

		assertFalse(bookLoan.isOverdue());
	}

	@Test
	void givenLoanOnTime_whenGetDaysOverdue_thenReturnsZero() {
		assertEquals(0, bookLoan.getDaysOverdue());
	}

	@Test
	void givenOverdueLoan_whenGetDaysOverdue_thenReturnsCorrectDays() {
		int daysOverdue = 5;
		bookLoan.setDueDate(LocalDate.now().minusDays(daysOverdue));

		assertEquals(daysOverdue, bookLoan.getDaysOverdue());
	}

	@Test
	void givenReturnedLoan_whenGetDaysOverdue_thenReturnsZero() {
		bookLoan.setDueDate(LocalDate.now().minusDays(5));
		bookLoan.returnItem();

		assertEquals(0, bookLoan.getDaysOverdue());
	}

	@Test
	void givenActiveLoanNotOverdue_whenCanExtend_thenReturnsTrue() {
		assertTrue(bookLoan.canExtend());
	}

	@Test
	void givenOverdueLoan_whenCanExtend_thenReturnsFalse() {
		bookLoan.setDueDate(LocalDate.now().minusDays(1));

		assertFalse(bookLoan.canExtend());
	}

	@Test
	void givenReturnedLoan_whenCanExtend_thenReturnsFalse() {
		bookLoan.returnItem();

		assertFalse(bookLoan.canExtend());
	}

	@Test
	void givenActiveLoan_whenExtendLoan_thenDueDateIsExtended() {
		LocalDate originalDueDate = bookLoan.getDueDate();
		int extensionDays = 7;

		bookLoan.extendLoan(extensionDays);

		assertEquals(originalDueDate.plusDays(extensionDays), bookLoan.getDueDate());
	}

	@Test
	void givenOverdueLoan_whenExtendLoan_thenThrowIllegalStateException() {
		bookLoan.setDueDate(LocalDate.now().minusDays(1));

		assertThrows(IllegalStateException.class, () -> bookLoan.extendLoan(7));
	}

	@Test
	void givenReturnedLoan_whenExtendLoan_thenThrowIllegalStateException() {
		bookLoan.returnItem();

		assertThrows(IllegalStateException.class, () -> bookLoan.extendLoan(7));
	}

	@Test
	void givenLoanOnTime_whenCalculateFine_thenReturnsZero() {
		assertEquals(0.0, bookLoan.calculateFine());
	}

	@Test
	void givenOverdueBookLoan_whenCalculateFine_thenReturnsCorrectAmount() {
		int daysOverdue = 5;
		bookLoan.setDueDate(LocalDate.now().minusDays(daysOverdue));

		double expectedFine = daysOverdue * 0.50;
		assertEquals(expectedFine, bookLoan.calculateFine());
	}

	@Test
	void givenOverdueCDLoan_whenCalculateFine_thenReturnsCorrectAmount() {
		int daysOverdue = 3;
		cdLoan.setDueDate(LocalDate.now().minusDays(daysOverdue));

		double expectedFine = daysOverdue * 0.75;
		assertEquals(expectedFine, cdLoan.calculateFine());
	}

	@Test
	void givenOverdueJournalLoan_whenCalculateFine_thenReturnsCorrectAmount() {
		int daysOverdue = 4;
		journalLoan.setDueDate(LocalDate.now().minusDays(daysOverdue));

		double expectedFine = daysOverdue * 1.00;
		assertEquals(expectedFine, journalLoan.calculateFine());
	}

	@Test
	void givenOverdueGenericLoan_whenCalculateFine_thenReturnsCorrectAmount() {
		Loan genericLoan = new Loan(testUserId, testItemId, "unknown", LocalDate.now());
		int daysOverdue = 10;
		genericLoan.setDueDate(LocalDate.now().minusDays(daysOverdue));

		double expectedFine = daysOverdue * 0.25;
		assertEquals(expectedFine, genericLoan.calculateFine());
	}

	@Test
	void givenNewLoan_whenCheckFineApplied_thenReturnsFalse() {
		assertFalse(bookLoan.isFineApplied());
	}

	@Test
	void givenLoan_whenMarkFineApplied_thenFineAppliedIsTrue() {
		bookLoan.markFineApplied();

		assertTrue(bookLoan.isFineApplied());
	}

	@Test
	void givenLoan_whenSetUserId_thenUserIdIsUpdated() {
		UUID newUserId = UUID.randomUUID();

		bookLoan.setUserId(newUserId);

		assertEquals(newUserId, bookLoan.getUserId());
	}

	@Test
	void givenLoan_whenSetItemId_thenItemIdIsUpdated() {
		UUID newItemId = UUID.randomUUID();

		bookLoan.setItemId(newItemId);

		assertEquals(newItemId, bookLoan.getItemId());
	}

	@Test
	void givenLoan_whenSetItemType_thenItemTypeIsUpdated() {
		bookLoan.setItemType("magazine");

		assertEquals("magazine", bookLoan.getItemType());
	}

	@Test
	void givenLoan_whenSetDueDate_thenDueDateIsUpdated() {
		LocalDate newDueDate = LocalDate.now().plusDays(30);

		bookLoan.setDueDate(newDueDate);

		assertEquals(newDueDate, bookLoan.getDueDate());
	}

	@Test
	void givenLoan_whenSetReturnDate_thenReturnDateIsUpdated() {
		LocalDate returnDate = LocalDate.now().minusDays(1);

		bookLoan.setReturnDate(returnDate);

		assertEquals(returnDate, bookLoan.getReturnDate());
	}

	@Test
	void givenActiveLoan_whenIsActive_thenReturnsTrue() {
		assertTrue(bookLoan.isActive());
	}

	@Test
	void givenReturnedLoan_whenIsActive_thenReturnsFalse() {
		bookLoan.returnItem();

		assertFalse(bookLoan.isActive());
	}

	@Test
	void givenNewLoan_whenIsReturned_thenReturnsFalse() {
		assertFalse(bookLoan.isReturned());
	}

	@Test
	void givenReturnedLoan_whenIsReturned_thenReturnsTrue() {
		bookLoan.returnItem();

		assertTrue(bookLoan.isReturned());
	}

	@Test
	void givenCaseInsensitiveItemType_whenCreateLoan_thenDueDateIsCalculatedCorrectly() {
		Loan upperCaseLoan = new Loan(testUserId, testItemId, "BOOK", LocalDate.now());
		Loan mixedCaseLoan = new Loan(testUserId, testItemId, "BooK", LocalDate.now());

		assertEquals(LocalDate.now().plusDays(BOOK_LOAN_DAYS), upperCaseLoan.getDueDate());
		assertEquals(LocalDate.now().plusDays(BOOK_LOAN_DAYS), mixedCaseLoan.getDueDate());
	}
}
