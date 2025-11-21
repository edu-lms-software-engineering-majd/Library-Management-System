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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

		librarianUser = new UserDTO(UUID.randomUUID(), "libuser", "Lib", "User", Role.LIBRARIAN);
		memberUser = new UserDTO(UUID.randomUUID(), "member", "Member", "User", Role.MEMBER);
	}

	// =====================================================================================
	// loanItem() tests
	// =====================================================================================
	@Nested
	@DisplayName("loanItem() Tests")
	class LoanItemTests {

		@Test
		@DisplayName("✅ Should loan BOOK successfully when user can borrow and book is available")
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

			assertNotNull(loan, "Loan should be created");
			verify(book).decrementAvailableCopies();
			verify(user).addLoan(loan);
			verify(loanRepo).save(loan);
			verify(bookRepo).updateBook(book);
			verify(userRepo).update(user);
		}

		@Test
		@DisplayName("✅ Should loan CD successfully")
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
		@DisplayName("✅ Should loan Journal successfully")
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
		@DisplayName("❌ Should throw UserNotFoundException when user does not exist")
		void shouldThrowWhenUserNotFound() {
			UUID itemId = UUID.randomUUID();
			when(userRepo.getByID(memberUser.userID())).thenReturn(Optional.empty());

			assertThrows(UserNotFoundException.class, () -> loanService.loanItem(memberUser, itemId, "book"));
		}

		@Test
		@DisplayName("❌ Should throw BorrowNotAllowedException when user cannot borrow")
		void shouldThrowWhenUserCannotBorrow() {
			UUID itemId = UUID.randomUUID();

			User user = mock(User.class);
			when(userRepo.getByID(memberUser.userID())).thenReturn(Optional.of(user));
			when(user.canBorrow()).thenReturn(false);

			assertThrows(BorrowNotAllowedException.class, () -> loanService.loanItem(memberUser, itemId, "book"));
		}

		@Test
		@DisplayName("❌ Should throw ItemNotFoundException when book does not exist")
		void shouldThrowWhenBookNotFound() {
			UUID itemId = UUID.randomUUID();

			User user = mock(User.class);
			when(userRepo.getByID(memberUser.userID())).thenReturn(Optional.of(user));
			when(user.canBorrow()).thenReturn(true);

			when(bookRepo.getBookById(itemId)).thenReturn(Optional.empty());

			assertThrows(ItemNotFoundException.class, () -> loanService.loanItem(memberUser, itemId, "book"));
		}

		@Test
		@DisplayName("❌ Should throw ItemNotAvailableException when item is not available")
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
		@DisplayName("❌ Should throw IllegalArgumentException for unsupported item type")
		void shouldThrowForUnsupportedItemType() {
			UUID itemId = UUID.randomUUID();

			User user = mock(User.class);
			when(userRepo.getByID(memberUser.userID())).thenReturn(Optional.of(user));
			when(user.canBorrow()).thenReturn(true);

			assertThrows(IllegalArgumentException.class, () -> loanService.loanItem(memberUser, itemId, "dvd"));
		}
	}

	// =====================================================================================
	// returnItem() tests
	// =====================================================================================
	@Nested
	@DisplayName("returnItem() Tests")
	class ReturnItemTests {

		@Test
		@DisplayName("❌ Should reject return when current user is not librarian")
		void shouldRejectReturnWhenNotLibrarian() {
			UUID loanId = UUID.randomUUID();

			try (MockedStatic<AuthService> authMock = mockStatic(AuthService.class)) {
				authMock.when(AuthService::getCurrentUser).thenReturn(memberUser);

				assertThrows(IllegalStateException.class, () -> loanService.returnItem(memberUser.userID(), loanId));
			}
		}

		@Test
		@DisplayName("❌ Should throw LoanNotFoundException when loan does not exist")
		void shouldThrowWhenLoanNotFound() {
			UUID loanId = UUID.randomUUID();

			try (MockedStatic<AuthService> authMock = mockStatic(AuthService.class)) {
				authMock.when(AuthService::getCurrentUser).thenReturn(librarianUser);

				when(loanRepo.findById(loanId)).thenReturn(Optional.empty());

				assertThrows(LoanNotFoundException.class, () -> loanService.returnItem(librarianUser.userID(), loanId));
			}
		}

		@Test
		@DisplayName("❌ Should throw when loan already returned")
		void shouldThrowWhenLoanAlreadyReturned() throws Exception {
			UUID loanId = UUID.randomUUID();
			Loan loan = mock(Loan.class);

			try (MockedStatic<AuthService> authMock = mockStatic(AuthService.class)) {
				authMock.when(AuthService::getCurrentUser).thenReturn(librarianUser);

				when(loanRepo.findById(loanId)).thenReturn(Optional.of(loan));
				when(loan.isActive()).thenReturn(false);

				assertThrows(IllegalStateException.class, () -> loanService.returnItem(librarianUser.userID(), loanId));
			}
		}

		@Test
		@DisplayName("✅ Should return item successfully when not overdue")
		void shouldReturnItemSuccessfullyWithoutFine() throws Exception {
			UUID loanId = UUID.randomUUID();
			UUID itemId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();

			Loan loan = mock(Loan.class);
			Book book = mock(Book.class);
			User user = mock(User.class);

			try (MockedStatic<AuthService> authMock = mockStatic(AuthService.class)) {
				authMock.when(AuthService::getCurrentUser).thenReturn(librarianUser);

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
				// لا غرامة لأن isOverdue=false
				verify(user, never()).getAccount();
			}
		}

		@Test
		@DisplayName("✅ Should apply fine when item is overdue on return")
		void shouldApplyFineWhenOverdue() throws Exception {
			UUID loanId = UUID.randomUUID();
			UUID itemId = UUID.randomUUID();
			UUID userId = UUID.randomUUID();

			Loan loan = mock(Loan.class);
			Book book = mock(Book.class);
			User user = mock(User.class);
			Account account = mock(Account.class);

			try (MockedStatic<AuthService> authMock = mockStatic(AuthService.class)) {
				authMock.when(AuthService::getCurrentUser).thenReturn(librarianUser);

				when(loanRepo.findById(loanId)).thenReturn(Optional.of(loan));
				when(loan.isActive()).thenReturn(true);
				when(loan.isOverdue()).thenReturn(true);
				when(loan.calculateFine()).thenReturn(15.0);
				// when(loan.getDaysOverdue()).thenReturn(3);

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
	}

	// =====================================================================================
	// Overdue notifications tests
	// =====================================================================================
	@Nested
	@DisplayName("checkAndNotifyOverdueLoans() Tests")
	class OverdueNotificationTests {

		@Test
		@DisplayName("✅ Should notify user for each overdue loan with existing user")
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
		@DisplayName("✔️ Should skip notification when user not found")
		void shouldSkipNotificationWhenUserMissing() throws Exception {
			Loan loan = mock(Loan.class);
			when(loanRepo.findOverdueLoans()).thenReturn(List.of(loan));
			when(userRepo.getByID(any())).thenReturn(Optional.empty());

			loanService.checkAndNotifyOverdueLoans();

			verify(notificationService, never()).notifyOverdueItem(any(), anyString());
		}
	}

	// =====================================================================================
	// Simple query methods tests
	// =====================================================================================
	@Nested
	@DisplayName("Loan query methods Tests")
	class QueryMethodsTests {

		@Test
		@DisplayName("✅ getUserActiveLoans should delegate to repository")
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
		@DisplayName("✅ getUserAllLoans should delegate to repository")
		void shouldGetUserAllLoans() throws Exception {
			UUID userId = UUID.randomUUID();
			Loan loan1 = mock(Loan.class);

			when(loanRepo.findByUserId(userId)).thenReturn(List.of(loan1));

			List<Loan> result = loanService.getUserAllLoans(userId);

			assertEquals(1, result.size());
			verify(loanRepo).findByUserId(userId);
		}

		@Test
		@DisplayName("✅ getOverdueLoans should return repository list")
		void shouldGetOverdueLoans() {
			Loan loan = mock(Loan.class);
			when(loanRepo.findOverdueLoans()).thenReturn(List.of(loan));

			List<Loan> result = loanService.getOverdueLoans();

			assertEquals(1, result.size());
			verify(loanRepo).findOverdueLoans();
		}

		@Test
		@DisplayName("✅ getLoan should return loan by ID")
		void shouldGetLoanById() {
			UUID loanId = UUID.randomUUID();
			Loan loan = mock(Loan.class);
			when(loanRepo.findById(loanId)).thenReturn(Optional.of(loan));

			Loan result = loanService.getLoan(loanId);

			assertSame(loan, result);
		}

		@Test
		@DisplayName("❌ getLoan should throw IllegalArgumentException when not found")
		void shouldThrowWhenLoanNotFoundInGetLoan() {
			UUID loanId = UUID.randomUUID();
			when(loanRepo.findById(loanId)).thenReturn(Optional.empty());

			assertThrows(IllegalArgumentException.class, () -> loanService.getLoan(loanId));
		}

		@Test
		@DisplayName("✅ getLoanWithItemInfo should return Loan and Item")
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
	}

	// =====================================================================================
	// calculateUserTotalFines() tests
	// =====================================================================================
	@Nested
	@DisplayName("calculateUserTotalFines() Tests")
	class TotalFinesTests {

		@Test
		@DisplayName("✅ Should sum fines only for overdue loans")
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
	}

	// =====================================================================================
	// extendLoan() tests
	// =====================================================================================
	@Nested
	@DisplayName("extendLoan() Tests")
	class ExtendLoanTests {

		@Test
		@DisplayName("✅ Should extend loan when admin and loan active")
		void shouldExtendLoanWhenAdminAndActive() throws PermissionDeniedException {
			UUID loanId = UUID.randomUUID();
			Loan loan = mock(Loan.class);

			when(loanRepo.findById(loanId)).thenReturn(Optional.of(loan));
			when(loan.isActive()).thenReturn(true);

			boolean result = loanService
					.extendLoan(new UserDTO(UUID.randomUUID(), "admin", "Ahmad", "Salameh", Role.ADMIN), loanId, 5);

			assertTrue(result);
		}

		@Test
		@DisplayName("❌ Should reject extendLoan when user is not admin")
		void shouldRejectExtendLoanForNonAdmin() {
			UUID loanId = UUID.randomUUID();

			assertThrows(PermissionDeniedException.class, () -> loanService.extendLoan(memberUser, loanId, 5));
		}

		@Test
		@DisplayName("❌ Should throw when additional days is not positive")
		void shouldThrowWhenAdditionalDaysNotPositive() {
			UUID loanId = UUID.randomUUID();

			assertThrows(IllegalArgumentException.class, () -> loanService
					.extendLoan(new UserDTO(UUID.randomUUID(), "admin", "Ahmad", "Salameh", Role.ADMIN), loanId, 0));
		}

		@Test
		@DisplayName("❌ Should throw when extending completed loan")
		void shouldThrowWhenExtendingCompletedLoan() {
			UUID loanId = UUID.randomUUID();
			Loan loan = mock(Loan.class);

			when(loanRepo.findById(loanId)).thenReturn(Optional.of(loan));
			when(loan.isActive()).thenReturn(false);

			assertThrows(IllegalStateException.class, () -> loanService
					.extendLoan(new UserDTO(UUID.randomUUID(), "admin", "Ahmad", "Salameh", Role.ADMIN), loanId, 3));
		}
	}

	// =====================================================================================
	// getLoanStatistics() tests
	// =====================================================================================
	@Nested
	@DisplayName("getLoanStatistics() Tests")
	class LoanStatisticsTests {

		@Test
		@DisplayName("✅ Should compute loan statistics correctly")
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

			assertEquals(3, stats[0], "total loans");
			assertEquals(2, stats[1], "active loans");
			assertEquals(1, stats[2], "overdue loans");
		}
	}

	// =====================================================================================
	// getUserActiveLoansWithBooks() tests
	// =====================================================================================
	@Nested
	@DisplayName("getUserActiveLoansWithBooks() Tests")
	class ActiveLoansWithBooksTests {

		@Test
		@DisplayName("✅ Should return only loans that have existing books")
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

			assertEquals(1, result.size(), "Only one loan should have a book");
			assertSame(loan1, result.get(0)[0]);
			assertSame(book1, result.get(0)[1]);
		}
	}
}
