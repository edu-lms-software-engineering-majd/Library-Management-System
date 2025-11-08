package lms.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import lms.domain.Account;
import lms.domain.AccountStatus;
import lms.domain.Book;
import lms.domain.BookRepository;
import lms.domain.CD;
import lms.domain.CDRepository;
import lms.domain.Journal;
import lms.domain.JournalsRepository;
import lms.domain.Loan;
import lms.domain.LoanRepository;
import lms.domain.User;
import lms.domain.UserRepository;
import lms.domain.exception.BorrowNotAllowedException;
import lms.domain.exception.ItemNotAvailableException;
import lms.domain.exception.ItemNotFoundException;
import lms.domain.exception.LoanNotFoundException;
import lms.domain.exception.UserNotFoundException;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

	@Mock
	private UserRepository userRepo;

	@Mock
	private BookRepository bookRepo;

	@Mock
	private CDRepository cdRepo;

	@Mock
	private JournalsRepository journalRepo;

	@Mock
	private LoanRepository loanRepo;

	@Mock
	private AccountService accountService;

	private LoanService loanService;
	private UserDTO testUserDTO;
	private User testUser;
	private Book testBook;
	private CD testCD;
	private Journal testJournal;
	private Loan testLoan;
	private UUID testUserId;
	private UUID testItemId;
	private UUID testLoanId;

	@BeforeEach
	void setUp() {
		loanService = new LoanService(userRepo, bookRepo, cdRepo, journalRepo, loanRepo, accountService);
		
		testUserId = UUID.randomUUID();
		testItemId = UUID.randomUUID();
		testLoanId = UUID.randomUUID();
		
		testUserDTO = new UserDTO(testUserId, "testuser", "Test", "User", null);
		testUser = new User("Test", "User", "test@example.com", "testuser", "hashedpass", null);
		testBook = new Book("Test Book", "Author", "ISBN123", "Publisher", 2024, "Fiction", 5, "English", "A1");
		testCD = new CD("Test Album", "Artist");
		testJournal = new Journal("Test Journal", "Author");
		testLoan = new Loan(testUserId, testItemId, "book", LocalDate.now());
	}

	@Test
	void givenValidUserAndAvailableBook_whenLoanItem_thenLoanIsCreated() throws Exception {
		when(userRepo.getByID(testUserId)).thenReturn(Optional.of(testUser));
		when(bookRepo.getBookById(testItemId)).thenReturn(Optional.of(testBook));
		when(loanRepo.save(any(Loan.class))).thenReturn(true);

		Loan result = loanService.loanItem(testUserDTO, testItemId, "book");

		assertNotNull(result);
		verify(userRepo).getByID(testUserId);
		verify(bookRepo).getBookById(testItemId);
		verify(loanRepo).save(any(Loan.class));
		verify(bookRepo).updateBook(testBook);
		verify(userRepo).update(testUser);
	}

	@Test
	void givenValidUserAndAvailableCD_whenLoanItem_thenLoanIsCreated() throws Exception {
		when(userRepo.getByID(testUserId)).thenReturn(Optional.of(testUser));
		when(cdRepo.getCDById(testItemId)).thenReturn(Optional.of(testCD));
		when(loanRepo.save(any(Loan.class))).thenReturn(true);

		Loan result = loanService.loanItem(testUserDTO, testItemId, "cd");

		assertNotNull(result);
		verify(cdRepo).getCDById(testItemId);
		verify(cdRepo).updateCD(testCD);
	}

	@Test
	void givenValidUserAndAvailableJournal_whenLoanItem_thenLoanIsCreated() throws Exception {
		when(userRepo.getByID(testUserId)).thenReturn(Optional.of(testUser));
		when(journalRepo.getJournalById(testItemId)).thenReturn(Optional.of(testJournal));
		when(loanRepo.save(any(Loan.class))).thenReturn(true);

		Loan result = loanService.loanItem(testUserDTO, testItemId, "journal");

		assertNotNull(result);
		verify(journalRepo).getJournalById(testItemId);
		verify(journalRepo).updateJournal(testJournal);
	}

	@Test
	void givenNonExistentUser_whenLoanItem_thenThrowUserNotFoundException() throws Exception {
		when(userRepo.getByID(testUserId)).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, 
			() -> loanService.loanItem(testUserDTO, testItemId, "book"));
		
		verify(loanRepo, never()).save(any(Loan.class));
	}

	@Test
	void givenNonExistentBook_whenLoanItem_thenThrowItemNotFoundException() throws Exception {
		when(userRepo.getByID(testUserId)).thenReturn(Optional.of(testUser));
		when(bookRepo.getBookById(testItemId)).thenReturn(Optional.empty());

		assertThrows(ItemNotFoundException.class, 
			() -> loanService.loanItem(testUserDTO, testItemId, "book"));
		
		verify(loanRepo, never()).save(any(Loan.class));
	}

	@Test
	void givenUserCannotBorrow_whenLoanItem_thenThrowBorrowNotAllowedException() throws Exception {
		User userAtLimit = new User("Test", "User", "test@example.com", "testuser", "hashedpass", null);
		for (int i = 0; i < 10; i++) {
			userAtLimit.addLoan(new Loan(testUserId, UUID.randomUUID(), "book", LocalDate.now()));
		}
		
		when(userRepo.getByID(testUserId)).thenReturn(Optional.of(userAtLimit));

		assertThrows(BorrowNotAllowedException.class, 
			() -> loanService.loanItem(testUserDTO, testItemId, "book"));
		
		verify(loanRepo, never()).save(any(Loan.class));
	}

	@Test
	void givenUnavailableItem_whenLoanItem_thenThrowItemNotAvailableException() throws Exception {
		testBook.setAvailableCopies(0);
		
		when(userRepo.getByID(testUserId)).thenReturn(Optional.of(testUser));
		when(bookRepo.getBookById(testItemId)).thenReturn(Optional.of(testBook));

		assertThrows(ItemNotAvailableException.class, 
			() -> loanService.loanItem(testUserDTO, testItemId, "book"));
		
		verify(loanRepo, never()).save(any(Loan.class));
	}

	@Test
	void givenUnsupportedItemType_whenLoanItem_thenThrowIllegalArgumentException() throws Exception {
		when(userRepo.getByID(testUserId)).thenReturn(Optional.of(testUser));

		assertThrows(IllegalArgumentException.class, 
			() -> loanService.loanItem(testUserDTO, testItemId, "magazine"));
	}

	@Test
	void givenValidLoan_whenReturnItem_thenLoanIsUpdatedAndItemIsAvailable() throws Exception {
		when(loanRepo.findById(testLoanId)).thenReturn(Optional.of(testLoan));
		when(userRepo.getByID(testUserId)).thenReturn(Optional.of(testUser));
		when(bookRepo.getBookById(testItemId)).thenReturn(Optional.of(testBook));

		boolean result = loanService.returnItem(testUserId, testLoanId);

		assertTrue(result);
		assertTrue(testLoan.isReturned());
		verify(loanRepo).update(testLoan);
		verify(bookRepo).updateBook(testBook);
		verify(userRepo).update(testUser);
	}

	@Test
	void givenOverdueLoan_whenReturnItem_thenFineIsApplied() throws Exception {
		testLoan.setDueDate(LocalDate.now().minusDays(5));
		
		when(loanRepo.findById(testLoanId)).thenReturn(Optional.of(testLoan));
		when(userRepo.getByID(testUserId)).thenReturn(Optional.of(testUser));
		when(bookRepo.getBookById(testItemId)).thenReturn(Optional.of(testBook));

		loanService.returnItem(testUserId, testLoanId);

		assertTrue(testLoan.isFineApplied());
		verify(loanRepo).update(testLoan);
	}

	@Test
	void givenNonExistentLoan_whenReturnItem_thenThrowLoanNotFoundException() {
		when(loanRepo.findById(testLoanId)).thenReturn(Optional.empty());

		assertThrows(LoanNotFoundException.class, 
			() -> loanService.returnItem(testUserId, testLoanId));
	}

	@Test
	void givenAlreadyReturnedLoan_whenReturnItem_thenThrowIllegalStateException() {
		testLoan.returnItem();
		
		when(loanRepo.findById(testLoanId)).thenReturn(Optional.of(testLoan));

		assertThrows(IllegalStateException.class, 
			() -> loanService.returnItem(testUserId, testLoanId));
	}

	@Test
	void givenUserId_whenGetUserActiveLoans_thenReturnsActiveLoans() throws Exception {
		List<Loan> expectedLoans = Arrays.asList(testLoan);
		when(loanRepo.findActiveLoansByUser(testUserId)).thenReturn(expectedLoans);

		List<Loan> result = loanService.getUserActiveLoans(testUserId);

		assertEquals(1, result.size());
		verify(loanRepo).findActiveLoansByUser(testUserId);
	}

	@Test
	void givenUserId_whenGetUserAllLoans_thenReturnsAllLoans() throws Exception {
		Loan returnedLoan = new Loan(testUserId, testItemId, "book", LocalDate.now().minusDays(10));
		returnedLoan.returnItem();
		List<Loan> expectedLoans = Arrays.asList(testLoan, returnedLoan);
		
		when(loanRepo.findByUserId(testUserId)).thenReturn(expectedLoans);

		List<Loan> result = loanService.getUserAllLoans(testUserId);

		assertEquals(2, result.size());
		verify(loanRepo).findByUserId(testUserId);
	}

	@Test
	void whenGetOverdueLoans_thenReturnsOverdueLoans() {
		testLoan.setDueDate(LocalDate.now().minusDays(3));
		List<Loan> expectedLoans = Arrays.asList(testLoan);
		
		when(loanRepo.findOverdueLoans()).thenReturn(expectedLoans);

		List<Loan> result = loanService.getOverdueLoans();

		assertEquals(1, result.size());
		verify(loanRepo).findOverdueLoans();
	}

	@Test
	void givenValidLoanId_whenGetLoan_thenReturnsLoan() {
		when(loanRepo.findById(testLoanId)).thenReturn(Optional.of(testLoan));

		Loan result = loanService.getLoan(testLoanId);

		assertNotNull(result);
		assertEquals(testLoanId, result.getLoanId());
		verify(loanRepo).findById(testLoanId);
	}

	@Test
	void givenInvalidLoanId_whenGetLoan_thenThrowIllegalArgumentException() {
		when(loanRepo.findById(testLoanId)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, 
			() -> loanService.getLoan(testLoanId));
	}

	@Test
	void givenValidLoanId_whenGetLoanWithItemInfo_thenReturnsLoanAndItem() {
		when(loanRepo.findById(testLoanId)).thenReturn(Optional.of(testLoan));
		when(bookRepo.getBookById(testItemId)).thenReturn(Optional.of(testBook));

		Object[] result = loanService.getLoanWithItemInfo(testLoanId);

		assertEquals(2, result.length);
		assertTrue(result[0] instanceof Loan);
		assertTrue(result[1] instanceof Book);
	}

	@Test
	void givenUserWithOverdueLoans_whenCalculateUserTotalFines_thenReturnsCorrectAmount() throws Exception {
		Loan overdueLoan1 = new Loan(testUserId, testItemId, "book", LocalDate.now().minusDays(35));
		Loan overdueLoan2 = new Loan(testUserId, testItemId, "cd", LocalDate.now().minusDays(25));
		overdueLoan1.setDueDate(LocalDate.now().minusDays(5));
		overdueLoan2.setDueDate(LocalDate.now().minusDays(3));
		
		List<Loan> activeLoans = Arrays.asList(overdueLoan1, overdueLoan2);
		when(loanRepo.findActiveLoansByUser(testUserId)).thenReturn(activeLoans);

		double result = loanService.calculateUserTotalFines(testUserId);

		assertTrue(result > 0);
	}

	@Test
	void givenUserWithNoOverdueLoans_whenCalculateUserTotalFines_thenReturnsZero() throws Exception {
		List<Loan> activeLoans = Arrays.asList(testLoan);
		when(loanRepo.findActiveLoansByUser(testUserId)).thenReturn(activeLoans);

		double result = loanService.calculateUserTotalFines(testUserId);

		assertEquals(0.0, result);
	}

	@Test
	void whenGetLoanStatistics_thenReturnsCorrectStatistics() {
		Loan activeLoan = new Loan(testUserId, testItemId, "book", LocalDate.now());
		Loan returnedLoan = new Loan(testUserId, testItemId, "cd", LocalDate.now().minusDays(10));
		returnedLoan.returnItem();
		Loan overdueLoan = new Loan(testUserId, testItemId, "journal", LocalDate.now().minusDays(20));
		overdueLoan.setDueDate(LocalDate.now().minusDays(5));
		
		List<Loan> allLoans = Arrays.asList(activeLoan, returnedLoan, overdueLoan);
		when(loanRepo.findAll()).thenReturn(allLoans);

		int[] stats = loanService.getLoanStatistics();

		assertEquals(3, stats[0]);
		assertEquals(2, stats[1]);
		assertEquals(1, stats[2]);
	}

	@Test
	void givenUserId_whenGetUserActiveLoansWithBooks_thenReturnsLoansWithBooks() throws Exception {
		when(loanRepo.findActiveLoansByUser(testUserId)).thenReturn(Arrays.asList(testLoan));
		when(bookRepo.getBookById(testItemId)).thenReturn(Optional.of(testBook));

		List<Object[]> result = loanService.getUserActiveLoansWithBooks(testUserId);

		assertEquals(1, result.size());
		assertEquals(2, result.get(0).length);
	}

	@Test
	void whenCheckAndNotifyOverdueLoans_thenNotifiesUsers() {
		testLoan.setDueDate(LocalDate.now().minusDays(2));
		
		when(loanRepo.findOverdueLoans()).thenReturn(Arrays.asList(testLoan));
		when(userRepo.getByID(testUserId)).thenReturn(Optional.of(testUser));
		when(bookRepo.getBookById(testItemId)).thenReturn(Optional.of(testBook));

		loanService.checkAndNotifyOverdueLoans();

		verify(loanRepo).findOverdueLoans();
		verify(userRepo).getByID(testUserId);
	}

	@Test
	void givenOverdueLoansWithNoUser_whenCheckAndNotifyOverdueLoans_thenSkipsNotification() {
		testLoan.setDueDate(LocalDate.now().minusDays(2));
		
		when(loanRepo.findOverdueLoans()).thenReturn(Arrays.asList(testLoan));
		when(userRepo.getByID(testUserId)).thenReturn(Optional.empty());

		loanService.checkAndNotifyOverdueLoans();

		verify(loanRepo).findOverdueLoans();
	}
}
