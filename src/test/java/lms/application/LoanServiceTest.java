package lms.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import lms.domain.Account;
import lms.domain.Book;
import lms.domain.BookRepository;
import lms.domain.CD;
import lms.domain.CDRepository;
import lms.domain.Journal;
import lms.domain.JournalsRepository;
import lms.domain.Loan;
import lms.domain.LoanRepository;
import lms.domain.Role;
import lms.domain.User;
import lms.domain.UserRepository;
import lms.domain.exception.BorrowNotAllowedException;
import lms.domain.exception.ItemNotAvailableException;
import lms.domain.exception.ItemNotFoundException;
import lms.domain.exception.LoanNotFoundException;
import lms.domain.exception.PermissionDeniedException;
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
	private NotificationService notificationService;

	private LoanService loanService;

	private UserDTO librarianUser;
	private UserDTO memberUser;

	@BeforeEach
	void setUp() {
		loanService = new LoanService(userRepo, bookRepo, cdRepo, journalRepo, loanRepo, notificationService);

		librarianUser = new UserDTO(UUID.randomUUID(), "libuser", "Lib", "User", "libuser@mail.com", Role.LIBRARIAN);

		memberUser = new UserDTO(UUID.randomUUID(), "member", "Member", "User", "member@mail.com", Role.MEMBER);
	}

	@Test
	void shouldLoanBookSuccessfully() throws Exception {
		UUID itemId = UUID.randomUUID();

		User user = mock(User.class);
		when(userRepo.getByID(memberUser.userID())).thenReturn(Optional.of(user));
		when(user.canBorrow()).thenReturn(true);
		when(user.getUserID()).thenReturn(memberUser.userID());

		Book book = mock(Book.class);
		when(book.isAvailable()).thenReturn(true);
		when(bookRepo.getBookById(itemId)).thenReturn(Optional.of(book));

		when(loanRepo.save(any(Loan.class))).thenReturn(true);
		when(bookRepo.updateBook(book)).thenReturn(true);
		when(userRepo.update(user)).thenReturn(true);

		Loan loan = loanService.loanItem(memberUser, itemId, "book");

		assertNotNull(loan);
		verify(book).decrementAvailableCopies();
		verify(user).addLoan(loan);
		verify(loanRepo).save(loan);
		verify(bookRepo).updateBook(book);
		verify(userRepo).update(user);
	}

	@Test
	void shouldLoanCDSuccessfully() throws Exception {
		UUID itemId = UUID.randomUUID();

		User user = mock(User.class);
		when(userRepo.getByID(memberUser.userID())).thenReturn(Optional.of(user));
		when(user.canBorrow()).thenReturn(true);
		when(user.getUserID()).thenReturn(memberUser.userID());

		CD cd = mock(CD.class);
		when(cd.isAvailable()).thenReturn(true);
		when(cdRepo.getCDById(itemId)).thenReturn(Optional.of(cd));

		when(loanRepo.save(any(Loan.class))).thenReturn(true);
		when(cdRepo.updateCD(cd)).thenReturn(true);
		when(userRepo.update(user)).thenReturn(true);

		Loan loan = loanService.loanItem(memberUser, itemId, "cd");

		assertNotNull(loan);
		verify(cd).decrementAvailableCopies();
		verify(cdRepo).updateCD(cd);
	}

	@Test
	void shouldLoanJournalSuccessfully() throws Exception {
		UUID itemId = UUID.randomUUID();

		User user = mock(User.class);
		when(userRepo.getByID(memberUser.userID())).thenReturn(Optional.of(user));
		when(user.canBorrow()).thenReturn(true);
		when(user.getUserID()).thenReturn(memberUser.userID());

		Journal journal = mock(Journal.class);
		when(journal.isAvailable()).thenReturn(true);
		when(journalRepo.getJournalById(itemId)).thenReturn(Optional.of(journal));

		when(loanRepo.save(any(Loan.class))).thenReturn(true);
		when(journalRepo.updateJournal(journal)).thenReturn(true);
		when(userRepo.update(user)).thenReturn(true);

		Loan loan = loanService.loanItem(memberUser, itemId, "journal");

		assertNotNull(loan);
		verify(journal).decrementAvailableCopies();
		verify(journalRepo).updateJournal(journal);
	}

	@Test
	void shouldThrowWhenUserNotFound() {
		UUID itemId = UUID.randomUUID();
		when(userRepo.getByID(memberUser.userID())).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> loanService.loanItem(memberUser, itemId, "book"));
	}

	@Test
	void shouldThrowWhenUserCannotBorrow() {
		UUID itemId = UUID.randomUUID();

		User user = mock(User.class);
		when(userRepo.getByID(memberUser.userID())).thenReturn(Optional.of(user));
		when(user.canBorrow()).thenReturn(false);

		assertThrows(BorrowNotAllowedException.class, () -> loanService.loanItem(memberUser, itemId, "book"));
	}

	@Test
	void shouldThrowWhenBookNotFound() {
		UUID itemId = UUID.randomUUID();

		User user = mock(User.class);
		when(userRepo.getByID(memberUser.userID())).thenReturn(Optional.of(user));
		when(user.canBorrow()).thenReturn(true);

		when(bookRepo.getBookById(itemId)).thenReturn(Optional.empty());

		assertThrows(ItemNotFoundException.class, () -> loanService.loanItem(memberUser, itemId, "book"));
	}

	@Test
	void shouldThrowWhenItemNotAvailable() {
		UUID itemId = UUID.randomUUID();

		User user = mock(User.class);
		when(userRepo.getByID(memberUser.userID())).thenReturn(Optional.of(user));
		when(user.canBorrow()).thenReturn(true);

		Book book = mock(Book.class);
		when(book.isAvailable()).thenReturn(false);
		when(bookRepo.getBookById(itemId)).thenReturn(Optional.of(book));

		assertThrows(ItemNotAvailableException.class, () -> loanService.loanItem(memberUser, itemId, "book"));
	}

	@Test
	void shouldThrowForUnsupportedItemType() {
		UUID itemId = UUID.randomUUID();

		User user = mock(User.class);
		when(userRepo.getByID(memberUser.userID())).thenReturn(Optional.of(user));
		when(user.canBorrow()).thenReturn(true);

		assertThrows(IllegalArgumentException.class, () -> loanService.loanItem(memberUser, itemId, "dvd"));
	}

	@Test
	void shouldRejectReturnWhenNotLibrarian() {
		UUID loanId = UUID.randomUUID();

		AuthService authServiceMock = mock(AuthService.class);
		when(authServiceMock.getCurrentUser()).thenReturn(memberUser);

		try (MockedStatic<AuthService> authMock = mockStatic(AuthService.class)) {
			authMock.when(AuthService::getInstance).thenReturn(authServiceMock);

			assertThrows(IllegalStateException.class, () -> loanService.returnItem(memberUser.userID(), loanId));
		}
	}

	@Test
	void shouldThrowWhenLoanNotFound() {
		UUID loanId = UUID.randomUUID();

		AuthService authServiceMock = mock(AuthService.class);
		when(authServiceMock.getCurrentUser()).thenReturn(librarianUser);

		try (MockedStatic<AuthService> authMock = mockStatic(AuthService.class)) {
			authMock.when(AuthService::getInstance).thenReturn(authServiceMock);

			when(loanRepo.findById(loanId)).thenReturn(Optional.empty());

			assertThrows(LoanNotFoundException.class, () -> loanService.returnItem(librarianUser.userID(), loanId));
		}
	}

	@Test
	void shouldThrowWhenLoanAlreadyReturned() throws Exception {
		UUID loanId = UUID.randomUUID();
		Loan loan = mock(Loan.class);

		AuthService authServiceMock = mock(AuthService.class);
		when(authServiceMock.getCurrentUser()).thenReturn(librarianUser);

		try (MockedStatic<AuthService> authMock = mockStatic(AuthService.class)) {
			authMock.when(AuthService::getInstance).thenReturn(authServiceMock);

			when(loanRepo.findById(loanId)).thenReturn(Optional.of(loan));
			when(loan.isActive()).thenReturn(false);

			assertThrows(IllegalStateException.class, () -> loanService.returnItem(librarianUser.userID(), loanId));
		}
	}

	@Test
	void shouldReturnItemSuccessfullyWithoutFine() throws Exception {
		UUID loanId = UUID.randomUUID();
		UUID itemId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();

		Loan loan = mock(Loan.class);
		Book book = mock(Book.class);
		User user = mock(User.class);

		AuthService authServiceMock = mock(AuthService.class);
		when(authServiceMock.getCurrentUser()).thenReturn(librarianUser);

		try (MockedStatic<AuthService> authMock = mockStatic(AuthService.class)) {
			authMock.when(AuthService::getInstance).thenReturn(authServiceMock);

			when(loanRepo.findById(loanId)).thenReturn(Optional.of(loan));
			when(loan.isActive()).thenReturn(true);
			when(loan.isOverdue()).thenReturn(false);
			when(loan.getItemId()).thenReturn(itemId);
			when(loan.getItemType()).thenReturn("book");
			when(loan.getUserId()).thenReturn(userId);

			when(bookRepo.getBookById(itemId)).thenReturn(Optional.of(book));
			when(userRepo.getByID(userId)).thenReturn(Optional.of(user));

			when(loanRepo.update(loan)).thenReturn(true);
			when(bookRepo.updateBook(book)).thenReturn(true);
			when(userRepo.update(user)).thenReturn(true);

			boolean result = loanService.returnItem(librarianUser.userID(), loanId);

			assertTrue(result);
			verify(loan).returnItem();
			verify(book).incrementAvailableCopies();
			verify(loanRepo).update(loan);
			verify(userRepo).update(user);
			verify(user, never()).getAccount();
		}
	}

	@Test
	void shouldApplyFineWhenOverdue() throws Exception {
		UUID loanId = UUID.randomUUID();
		UUID itemId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();

		Loan loan = mock(Loan.class);
		Book book = mock(Book.class);
		User user = mock(User.class);
		Account account = mock(Account.class);

		AuthService authServiceMock = mock(AuthService.class);
		when(authServiceMock.getCurrentUser()).thenReturn(librarianUser);

		try (MockedStatic<AuthService> authMock = mockStatic(AuthService.class)) {
			authMock.when(AuthService::getInstance).thenReturn(authServiceMock);

			when(loanRepo.findById(loanId)).thenReturn(Optional.of(loan));
			when(loan.isActive()).thenReturn(true);
			when(loan.isOverdue()).thenReturn(true);
			when(loan.calculateFine()).thenReturn(15.0);

			when(loan.getItemId()).thenReturn(itemId);
			when(loan.getItemType()).thenReturn("book");
			when(loan.getUserId()).thenReturn(userId);

			when(bookRepo.getBookById(itemId)).thenReturn(Optional.of(book));
			when(userRepo.getByID(userId)).thenReturn(Optional.of(user));
			when(user.getAccount()).thenReturn(account);

			when(loanRepo.update(loan)).thenReturn(true);
			when(bookRepo.updateBook(book)).thenReturn(true);
			when(userRepo.update(user)).thenReturn(true);

			boolean result = loanService.returnItem(librarianUser.userID(), loanId);

			assertTrue(result);
			verify(account).addFine(eq(15.0), contains("Late return"));
			verify(loan).markFineApplied();
		}
	}

	@Test
	void shouldNotifyUserForOverdueLoans() throws Exception {
		UUID userId = UUID.randomUUID();
		UUID itemId = UUID.randomUUID();

		Loan loan = mock(Loan.class);
		User user = mock(User.class);
		Book book = mock(Book.class);

		when(loan.getUserId()).thenReturn(userId);
		when(loan.getItemId()).thenReturn(itemId);
		when(loan.getItemType()).thenReturn("book");

		when(loanRepo.findOverdueLoans()).thenReturn(List.of(loan));
		when(userRepo.getByID(userId)).thenReturn(Optional.of(user));
		when(bookRepo.getBookById(itemId)).thenReturn(Optional.of(book));
		when(book.getTitle()).thenReturn("Clean Code");

		loanService.checkAndNotifyOverdueLoans();

		verify(notificationService).notifyOverdueItem(user, "Clean Code");
	}

	@Test
	void shouldSkipNotificationWhenUserMissing() throws Exception {
		Loan loan = mock(Loan.class);
		when(loanRepo.findOverdueLoans()).thenReturn(List.of(loan));
		when(userRepo.getByID(any())).thenReturn(Optional.empty());

		loanService.checkAndNotifyOverdueLoans();

		verify(notificationService, never()).notifyOverdueItem(any(), anyString());
	}

	@Test
	void shouldGetUserActiveLoans() throws Exception {
		UUID userId = UUID.randomUUID();
		Loan loan1 = mock(Loan.class);
		Loan loan2 = mock(Loan.class);

		when(loanRepo.findActiveLoansByUser(userId)).thenReturn(List.of(loan1, loan2));

		List<Loan> result = loanService.getUserActiveLoans(userId);

		assertEquals(2, result.size());
		verify(loanRepo).findActiveLoansByUser(userId);
	}

	@Test
	void shouldGetUserAllLoans() throws Exception {
		UUID userId = UUID.randomUUID();
		Loan loan1 = mock(Loan.class);

		when(loanRepo.findByUserId(userId)).thenReturn(List.of(loan1));

		List<Loan> result = loanService.getUserAllLoans(userId);

		assertEquals(1, result.size());
		verify(loanRepo).findByUserId(userId);
	}

	@Test
	void shouldGetOverdueLoans() {
		Loan loan = mock(Loan.class);
		when(loanRepo.findOverdueLoans()).thenReturn(List.of(loan));

		List<Loan> result = loanService.getOverdueLoans();

		assertEquals(1, result.size());
		verify(loanRepo).findOverdueLoans();
	}

	@Test
	void shouldGetLoanById() {
		UUID loanId = UUID.randomUUID();
		Loan loan = mock(Loan.class);
		when(loanRepo.findById(loanId)).thenReturn(Optional.of(loan));

		Loan result = loanService.getLoan(loanId);

		assertSame(loan, result);
	}

	@Test
	void shouldThrowWhenLoanNotFoundInGetLoan() {
		UUID loanId = UUID.randomUUID();
		when(loanRepo.findById(loanId)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () -> loanService.getLoan(loanId));
	}

	@Test
	void shouldGetLoanWithItemInfo() throws Exception {
		UUID loanId = UUID.randomUUID();
		UUID itemId = UUID.randomUUID();

		Loan loan = mock(Loan.class);
		Book book = mock(Book.class);

		when(loanRepo.findById(loanId)).thenReturn(Optional.of(loan));
		when(loan.getItemId()).thenReturn(itemId);
		when(loan.getItemType()).thenReturn("book");
		when(bookRepo.getBookById(itemId)).thenReturn(Optional.of(book));

		Object[] result = loanService.getLoanWithItemInfo(loanId);

		assertEquals(2, result.length);
		assertSame(loan, result[0]);
		assertSame(book, result[1]);
	}

	@Test
	void shouldCalculateTotalFinesForOverdueLoans() throws Exception {
		UUID userId = UUID.randomUUID();

		Loan overdueLoan = mock(Loan.class);
		Loan notOverdueLoan = mock(Loan.class);

		when(overdueLoan.isOverdue()).thenReturn(true);
		when(overdueLoan.calculateFine()).thenReturn(10.0);

		when(notOverdueLoan.isOverdue()).thenReturn(false);

		when(loanRepo.findActiveLoansByUser(userId)).thenReturn(List.of(overdueLoan, notOverdueLoan));

		double total = loanService.calculateUserTotalFines(userId);

		assertEquals(10.0, total, 0.0001);
		verify(overdueLoan).calculateFine();
		verify(notOverdueLoan, never()).calculateFine();
	}

	@Test
	void shouldExtendLoanWhenAdminAndActive() throws PermissionDeniedException {
		UUID loanId = UUID.randomUUID();
		Loan loan = mock(Loan.class);

		when(loanRepo.findById(loanId)).thenReturn(Optional.of(loan));
		when(loan.isActive()).thenReturn(true);

		UserDTO adminUser = new UserDTO(UUID.randomUUID(), "admin", "Ahmad", "Salameh", "admin@mail.com", Role.ADMIN);

		boolean result = loanService.extendLoan(adminUser, loanId, 5);

		assertTrue(result);
	}

	@Test
	void shouldRejectExtendLoanForNonAdmin() {
		UUID loanId = UUID.randomUUID();

		assertThrows(PermissionDeniedException.class, () -> loanService.extendLoan(memberUser, loanId, 5));
	}

	@Test
	void shouldThrowWhenAdditionalDaysNotPositive() {
		UUID loanId = UUID.randomUUID();

		UserDTO adminUser = new UserDTO(UUID.randomUUID(), "admin", "Ahmad", "Salameh", "admin@mail.com", Role.ADMIN);

		assertThrows(IllegalArgumentException.class, () -> loanService.extendLoan(adminUser, loanId, 0));
	}

	@Test
	void shouldThrowWhenExtendingCompletedLoan() {
		UUID loanId = UUID.randomUUID();
		Loan loan = mock(Loan.class);

		when(loanRepo.findById(loanId)).thenReturn(Optional.of(loan));
		when(loan.isActive()).thenReturn(false);

		UserDTO adminUser = new UserDTO(UUID.randomUUID(), "admin", "Ahmad", "Salameh", "admin@mail.com", Role.ADMIN);

		assertThrows(IllegalStateException.class, () -> loanService.extendLoan(adminUser, loanId, 3));
	}

	@Test
	void shouldComputeLoanStatistics() {
		Loan activeLoan = mock(Loan.class);
		Loan overdueLoan = mock(Loan.class);
		Loan returnedLoan = mock(Loan.class);

		when(activeLoan.isActive()).thenReturn(true);
		when(activeLoan.isOverdue()).thenReturn(false);

		when(overdueLoan.isActive()).thenReturn(true);
		when(overdueLoan.isOverdue()).thenReturn(true);

		when(returnedLoan.isActive()).thenReturn(false);
		when(returnedLoan.isOverdue()).thenReturn(false);

		when(loanRepo.findAll()).thenReturn(List.of(activeLoan, overdueLoan, returnedLoan));

		int[] stats = loanService.getLoanStatistics();

		assertEquals(3, stats[0]);
		assertEquals(2, stats[1]);
		assertEquals(1, stats[2]);
	}

	@Test
	void shouldReturnLoansWithExistingBooks() throws Exception {
		UUID userId = UUID.randomUUID();
		UUID bookId1 = UUID.randomUUID();
		UUID bookId2 = UUID.randomUUID();

		Loan loan1 = mock(Loan.class);
		Loan loan2 = mock(Loan.class);

		when(loan1.getItemId()).thenReturn(bookId1);
		when(loan2.getItemId()).thenReturn(bookId2);

		when(loanRepo.findActiveLoansByUser(userId)).thenReturn(List.of(loan1, loan2));

		Book book1 = mock(Book.class);

		when(bookRepo.getBookById(bookId1)).thenReturn(Optional.of(book1));
		when(bookRepo.getBookById(bookId2)).thenReturn(Optional.empty());

		List<Object[]> result = loanService.getUserActiveLoansWithBooks(userId);

		assertEquals(1, result.size());
		assertSame(loan1, result.get(0)[0]);
		assertSame(book1, result.get(0)[1]);
	}
}
