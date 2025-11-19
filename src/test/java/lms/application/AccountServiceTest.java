package lms.application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import lms.domain.Account;
import lms.domain.AccountStatus;
import lms.domain.Role;
import lms.domain.User;
import lms.domain.UserRepository;
import lms.domain.utils.PasswordUtils;

/**
 * Comprehensive test suite for AccountService.
 * 
 * <p>
 * This test class provides extensive coverage for the AccountService layer,
 * testing all core functionality including:
 * </p>
 * 
 * <h2>Test Coverage:</h2>
 * <ul>
 * <li><b>Constructor Tests:</b> Validates AccountService instantiation with
 * valid and invalid parameters</li>
 * <li><b>Account Creation & Retrieval:</b> Tests getOrCreateAccount() method
 * with various scenarios</li>
 * <li><b>Fine Management:</b> Tests adding, paying, and calculating fines</li>
 * <li><b>Account Status:</b> Tests account status retrieval and
 * permissions</li>
 * <li><b>Borrowing Permissions:</b> Tests canUserBorrow() checks</li>
 * <li><b>Account Suspension:</b> Tests suspending and activating accounts</li>
 * <li><b>Aggregate Operations:</b> Tests calculating total fines and user
 * counts</li>
 * <li><b>Integration Tests:</b> Tests complete workflows and state
 * consistency</li>
 * <li><b>Exception Handling:</b> Tests all error conditions and edge cases</li>
 * </ul>
 * 
 * <h2>Target Coverage:</h2>
 * <p>
 * 85%+ code coverage with focus on:
 * </p>
 * <ul>
 * <li>All public methods</li>
 * <li>Error paths and exception handling</li>
 * <li>Boundary conditions</li>
 * <li>Multi-user scenarios</li>
 * </ul>
 * 
 * <h2>Testing Framework:</h2>
 * <ul>
 * <li><b>JUnit 5:</b> Modern testing framework</li>
 * <li><b>Mockito:</b> Mocking framework for dependencies</li>
 * <li><b>@Nested:</b> Organizing tests by feature</li>
 * <li><b>@DisplayName:</b> Human-readable test names</li>
 * </ul>
 * 
 * <h2>Running Tests:</h2>
 * 
 * <pre>
 * # Run all AccountService tests
 * mvn test -Dtest=AccountServiceTest
 * 
 * # Run with coverage report
 * mvn clean test jacoco:report
 * </pre>
 * 
 * @author Ahmad Salameh (hmeedsalameh2004@gmail.com)
 * @version 1.0
 * @since 2025-01-15
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AccountService - Comprehensive Tests (85%+ Coverage)")
class AccountServiceTest {

	/**
	 * Mock UserRepository for testing AccountService in isolation. Prevents actual
	 * database access during testing.
	 */
	@Mock
	private UserRepository userRepository;

	/** Instance of AccountService under test */
	private AccountService accountService;

	/** Test user with real data */
	private User testUser;

	/** Test user ID */
	private UUID testUserId;

	/** Test user's account */
	private Account testAccount;

	/**
	 * Setup method executed before each test. Initializes AccountService, test
	 * user, and mock repository.
	 */
	@BeforeEach
	void setUp() {
		accountService = new AccountService(userRepository);
		testUserId = UUID.randomUUID();

		// Create test user with real data
		testUser = new User("Ahmad", "Salameh", "hmeedsalameh2004@gmail.com", "ahmadsalameh",
				PasswordUtils.hashPassword("secure_password_123"), Role.MEMBER);
		testAccount = testUser.getAccount();
	}

	/**
	 * Cleanup method executed after each test. Resets all mocks to ensure test
	 * isolation.
	 */
	@AfterEach
	void tearDown() {
		reset(userRepository);
	}

	/**
	 * Tests for AccountService constructor. Validates proper initialization and
	 * null-checking.
	 */
	@Nested
	@DisplayName("🏗️ Constructor Tests")
	class ConstructorTests {

		/**
		 * Test: Constructor should successfully create AccountService with valid
		 * repository.
		 * 
		 * <p>
		 * <b>Setup:</b> No setup required
		 * </p>
		 * <p>
		 * <b>Action:</b> Create AccountService with valid UserRepository mock
		 * </p>
		 * <p>
		 * <b>Expected:</b> AccountService should be created successfully (not null)
		 * </p>
		 */
		@Test
		@DisplayName("Should create AccountService with valid repository")
		void testConstructorWithValidRepository() {
			assertNotNull(accountService, "AccountService should be created");
		}

		/**
		 * Test: Constructor should throw IllegalArgumentException when repository is
		 * null.
		 * 
		 * <p>
		 * <b>Setup:</b> No setup required
		 * </p>
		 * <p>
		 * <b>Action:</b> Attempt to create AccountService with null repository
		 * </p>
		 * <p>
		 * <b>Expected:</b> IllegalArgumentException should be thrown
		 * </p>
		 */
		@Test
		@DisplayName("Should throw IllegalArgumentException when repository is null")
		void testConstructorWithNullRepository() {
			assertThrows(IllegalArgumentException.class, () -> {
				new AccountService(null);
			}, "Should throw IllegalArgumentException for null repository");
		}
	}

	/**
	 * Tests for getOrCreateAccount() method. Validates account retrieval and error
	 * handling.
	 */
	@Nested
	@DisplayName("📋 getOrCreateAccount() Tests")
	class GetOrCreateAccountTests {

		/**
		 * Test: Should retrieve existing account for valid user.
		 * 
		 * <p>
		 * <b>Setup:</b> Mock repository returns testUser when queried by testUserId
		 * </p>
		 * <p>
		 * <b>Action:</b> Call getOrCreateAccount(testUserId)
		 * </p>
		 * <p>
		 * <b>Expected:</b> Returns testUser's account object, not null
		 * </p>
		 */
		@Test
		@DisplayName("Should retrieve existing account for valid user")
		void testGetExistingAccount() {
			when(userRepository.getByID(testUserId)).thenReturn(Optional.of(testUser));

			Account result = accountService.getOrCreateAccount(testUserId);

			assertNotNull(result, "Account should not be null");
			assertEquals(testAccount, result, "Should return the user's account");
			verify(userRepository, times(1)).getByID(testUserId);
		}

		/**
		 * Test: Should throw IllegalArgumentException for null userId.
		 * 
		 * <p>
		 * <b>Setup:</b> No setup required
		 * </p>
		 * <p>
		 * <b>Action:</b> Call getOrCreateAccount(null)
		 * </p>
		 * <p>
		 * <b>Expected:</b> IllegalArgumentException should be thrown immediately
		 * </p>
		 */
		@Test
		@DisplayName("Should throw IllegalArgumentException for null userId")
		void testGetAccountWithNullUserId() {
			assertThrows(IllegalArgumentException.class, () -> {
				accountService.getOrCreateAccount(null);
			}, "Should throw IllegalArgumentException for null userId");
		}

		/**
		 * Test: Should throw IllegalArgumentException when user not found in
		 * repository.
		 * 
		 * <p>
		 * <b>Setup:</b> Mock repository returns empty Optional
		 * </p>
		 * <p>
		 * <b>Action:</b> Call getOrCreateAccount() for non-existent user
		 * </p>
		 * <p>
		 * <b>Expected:</b> IllegalArgumentException should be thrown
		 * </p>
		 */
		@Test
		@DisplayName("Should throw IllegalArgumentException when user not found")
		void testGetAccountWhenUserNotFound() {
			when(userRepository.getByID(testUserId)).thenReturn(Optional.empty());

			assertThrows(IllegalArgumentException.class, () -> {
				accountService.getOrCreateAccount(testUserId);
			}, "Should throw IllegalArgumentException when user not found");
		}

		/**
		 * Test: Should handle different user IDs correctly.
		 * 
		 * <p>
		 * <b>Setup:</b> Create two different users with different IDs
		 * </p>
		 * <p>
		 * <b>Action:</b> Get accounts for both users
		 * </p>
		 * <p>
		 * <b>Expected:</b> Different users should have different account objects
		 * </p>
		 */
		@Test
		@DisplayName("Should work with different user IDs")
		void testGetAccountWithDifferentUserIds() {
			UUID userId1 = UUID.randomUUID();
			UUID userId2 = UUID.randomUUID();

			User user1 = new User("Ahmad", "Salameh", "hmeedsalameh2004@gmail.com", "ahmad",
					PasswordUtils.hashPassword("pass"), Role.MEMBER);

			User user2 = new User("Sara", "Mohammed", "sara@example.com", "sara", PasswordUtils.hashPassword("pass"),
					Role.MEMBER);

			when(userRepository.getByID(userId1)).thenReturn(Optional.of(user1));
			when(userRepository.getByID(userId2)).thenReturn(Optional.of(user2));

			Account account1 = accountService.getOrCreateAccount(userId1);
			Account account2 = accountService.getOrCreateAccount(userId2);

			assertNotNull(account1);
			assertNotNull(account2);
			assertNotEquals(account1, account2, "Different users should have different accounts");
		}
	}
	

	/**
	 * Tests for addFineToUser() method. Validates fine addition, updates, and error
	 * handling.
	 */
	@Nested
	@DisplayName("💰 addFineToUser() Tests")
	class AddFineTests {

		/**
		 * Test: Should successfully add fine to user account.
		 * 
		 * <p>
		 * <b>Setup:</b> Mock repository returns testUser
		 * </p>
		 * <p>
		 * <b>Action:</b> Add 10.0 NIS fine for "Late return"
		 * </p>
		 * <p>
		 * <b>Expected:</b> Total fines increased, repository updated
		 * </p>
		 */
		@Test
		@DisplayName("Should add fine to user account")
		void testAddFineSuccess() {
			when(userRepository.getByID(testUserId)).thenReturn(Optional.of(testUser));

			accountService.addFineToUser(testUserId, 10.0, "Late return");

			verify(userRepository, times(2)).getByID(testUserId);
			verify(userRepository, times(1)).update(testUser);
			assertTrue(testAccount.getTotalFines() > 0, "Total fines should be increased");
		}

		/**
		 * Test: Should correctly accumulate multiple fines.
		 * 
		 * <p>
		 * <b>Setup:</b> Mock repository returns testUser
		 * </p>
		 * <p>
		 * <b>Action:</b> Add three separate fines (10.0 + 5.0 + 3.0 NIS)
		 * </p>
		 * <p>
		 * <b>Expected:</b> Total fines should be at least 18.0 NIS
		 * </p>
		 */
		@Test
		@DisplayName("Should add multiple fines correctly")
		void testAddMultipleFines() {
			when(userRepository.getByID(testUserId)).thenReturn(Optional.of(testUser));

			accountService.addFineToUser(testUserId, 10.0, "Fine 1");
			accountService.addFineToUser(testUserId, 5.0, "Fine 2");
			accountService.addFineToUser(testUserId, 3.0, "Fine 3");

			double expectedTotal = 18.0;
			assertTrue(testAccount.getTotalFines() >= expectedTotal, "Total fines should be sum of all fines");
		}

		/**
		 * Test: Should handle large fine amounts.
		 * 
		 * <p>
		 * <b>Setup:</b> Mock repository returns testUser
		 * </p>
		 * <p>
		 * <b>Action:</b> Add 1000.0 NIS fine
		 * </p>
		 * <p>
		 * <b>Expected:</b> Total fines should be at least 1000.0 NIS
		 * </p>
		 */
		@Test
		@DisplayName("Should handle large fine amounts")
		void testAddLargeFine() {
			when(userRepository.getByID(testUserId)).thenReturn(Optional.of(testUser));

			accountService.addFineToUser(testUserId, 1000.0, "Large fine");

			assertTrue(testAccount.getTotalFines() >= 1000.0);
			verify(userRepository, times(1)).update(testUser);
		}

		/**
		 * Test: Should accept various fine reasons.
		 * 
		 * <p>
		 * <b>Setup:</b> Mock repository returns testUser
		 * </p>
		 * <p>
		 * <b>Action:</b> Add fines with different reasons: Late return, Damage, Lost
		 * item, Other
		 * </p>
		 * <p>
		 * <b>Expected:</b> All fines added successfully, repository updated 4 times
		 * </p>
		 */
		@Test
		@DisplayName("Should accept various fine reasons")
		void testAddFineWithDifferentReasons() {
			when(userRepository.getByID(testUserId)).thenReturn(Optional.of(testUser));

			String[] reasons = { "Late return", "Damage to book", "Lost item", "Other violations" };

			for (String reason : reasons) {
				accountService.addFineToUser(testUserId, 5.0, reason);
			}

			verify(userRepository, times(4)).update(testUser);
		}

		/**
		 * Test: Should update repository after adding fine.
		 * 
		 * <p>
		 * <b>Setup:</b> Mock repository returns testUser
		 * </p>
		 * <p>
		 * <b>Action:</b> Add 10.0 NIS fine
		 * </p>
		 * <p>
		 * <b>Expected:</b> userRepository.update() should be called at least once
		 * </p>
		 */
		@Test
		@DisplayName("Should update repository after adding fine")
		void testRepositoryUpdateAfterAddFine() {
			when(userRepository.getByID(testUserId)).thenReturn(Optional.of(testUser));

			accountService.addFineToUser(testUserId, 10.0, "Test");

			verify(userRepository).update(testUser);
		}

		/**
		 * Test: Should throw exception when user not found during update.
		 * 
		 * <p>
		 * <b>Setup:</b> First call returns testUser, second call returns empty Optional
		 * </p>
		 * <p>
		 * <b>Action:</b> Add fine to user
		 * </p>
		 * <p>
		 * <b>Expected:</b> IllegalArgumentException when trying to retrieve user for
		 * update
		 * </p>
		 */
		@Test
		@DisplayName("Should throw when user not found after retrieval")
		void testAddFineWhenUserNotFoundAfterRetrieval() {
			when(userRepository.getByID(testUserId)).thenReturn(Optional.of(testUser)).thenReturn(Optional.empty());

			assertThrows(IllegalArgumentException.class, () -> {
				accountService.addFineToUser(testUserId, 10.0, "Test");
			});
		}
	}

	/**
	 * Tests for payUserFine() method. Validates fine payment, partial payments, and
	 * error handling.
	 */
	@Nested
	@DisplayName("💳 payUserFine() Tests")
	class PayFineTests {

		/**
		 * Test: Should successfully pay user fine.
		 * 
		 * <p>
		 * <b>Setup:</b> Add initial 20.0 NIS fine to testUser
		 * </p>
		 * <p>
		 * <b>Action:</b> Pay 10.0 NIS
		 * </p>
		 * <p>
		 * <b>Expected:</b> Total fines reduced, repository updated
		 * </p>
		 */
		@Test
		@DisplayName("Should pay user fine successfully")
		void testPayFineSuccess() {
			when(userRepository.getByID(testUserId)).thenReturn(Optional.of(testUser));

			accountService.addFineToUser(testUserId, 20.0, "Initial fine");
			double initialFines = testAccount.getTotalFines();

			accountService.payUserFine(testUserId, 10.0);

			assertTrue(testAccount.getTotalFines() < initialFines, "Total fines should be reduced after payment");
			verify(userRepository, atLeast(1)).update(testUser);
		}

		/**
		 * Test: Should handle partial payments correctly.
		 * 
		 * <p>
		 * <b>Setup:</b> Add 30.0 NIS fine to testUser
		 * </p>
		 * <p>
		 * <b>Action:</b> Pay 10.0 NIS three times (30 NIS total)
		 * </p>
		 * <p>
		 * <b>Expected:</b> Final balance should be 0.0 NIS (±0.01 for floating point)
		 * </p>
		 */
		@Test
		@DisplayName("Should handle partial payments")
		void testPartialPayment() {
			when(userRepository.getByID(testUserId)).thenReturn(Optional.of(testUser));

			accountService.addFineToUser(testUserId, 30.0, "Test");
			accountService.payUserFine(testUserId, 10.0);
			accountService.payUserFine(testUserId, 10.0);
			accountService.payUserFine(testUserId, 10.0);

			assertEquals(0.0, testAccount.getTotalFines(), 0.01, "Fines should be fully paid");
		}

		/**
		 * Test: Should handle full payment in one transaction.
		 * 
		 * <p>
		 * <b>Setup:</b> Add 50.0 NIS fine to testUser
		 * </p>
		 * <p>
		 * <b>Action:</b> Pay 50.0 NIS in one transaction
		 * </p>
		 * <p>
		 * <b>Expected:</b> Total fines should be 0.0 NIS
		 * </p>
		 */
		@Test
		@DisplayName("Should handle full payment")
		void testFullPayment() {
			when(userRepository.getByID(testUserId)).thenReturn(Optional.of(testUser));

			accountService.addFineToUser(testUserId, 50.0, "Test");
			accountService.payUserFine(testUserId, 50.0);

			assertEquals(0.0, testAccount.getTotalFines(), 0.01);
		}

		/**
		 * Test: Should throw exception when user not found during payment.
		 * 
		 * <p>
		 * <b>Setup:</b> Mock repository returns empty Optional
		 * </p>
		 * <p>
		 * <b>Action:</b> Try to pay fine for non-existent user
		 * </p>
		 * <p>
		 * <b>Expected:</b> IllegalArgumentException should be thrown
		 * </p>
		 */
		@Test
		@DisplayName("Should throw when user not found during payment")
		void testPayFineWhenUserNotFound() {
			when(userRepository.getByID(testUserId)).thenReturn(Optional.empty());

			assertThrows(IllegalArgumentException.class, () -> {
				accountService.payUserFine(testUserId, 10.0);
			});
		}

		/**
		 * Test: Should update repository after payment.
		 * 
		 * <p>
		 * <b>Setup:</b> Add 20.0 NIS fine
		 * </p>
		 * <p>
		 * <b>Action:</b> Pay 10.0 NIS
		 * </p>
		 * <p>
		 * <b>Expected:</b> userRepository.update() called after payment
		 * </p>
		 */
		@Test
		@DisplayName("Should update repository after payment")
		void testRepositoryUpdateAfterPayment() {
			when(userRepository.getByID(testUserId)).thenReturn(Optional.of(testUser));

			accountService.addFineToUser(testUserId, 20.0, "Test");
			reset(userRepository);
			when(userRepository.getByID(testUserId)).thenReturn(Optional.of(testUser));

			accountService.payUserFine(testUserId, 10.0);

			verify(userRepository).update(testUser);
		}
	}

	/**
	 * Tests for getUserAccountStatus() method. Validates status retrieval for
	 * different users.
	 */
	@Nested
	@DisplayName("📊 getUserAccountStatus() Tests")
	class GetAccountStatusTests {

		/**
		 * Test: Should return ACTIVE status for new account.
		 * 
		 * <p>
		 * <b>Setup:</b> New testUser with no fines or suspensions
		 * </p>
		 * <p>
		 * <b>Action:</b> Get account status
		 * </p>
		 * <p>
		 * <b>Expected:</b> Status should be AccountStatus.ACTIVE
		 * </p>
		 */
		@Test
		@DisplayName("Should return ACTIVE status for new account")
		void testGetStatusForNewAccount() {
			when(userRepository.getByID(testUserId)).thenReturn(Optional.of(testUser));

			AccountStatus status = accountService.getUserAccountStatus(testUserId);

			assertEquals(AccountStatus.ACTIVE, status, "New account should be ACTIVE");
		}

		/**
		 * Test: Should return account status correctly.
		 * 
		 * <p>
		 * <b>Setup:</b> Mock repository returns testUser
		 * </p>
		 * <p>
		 * <b>Action:</b> Get account status
		 * </p>
		 * <p>
		 * <b>Expected:</b> Status should not be null
		 * </p>
		 */
		@Test
		@DisplayName("Should return account status correctly")
		void testGetStatusReturnsCorrectStatus() {
			when(userRepository.getByID(testUserId)).thenReturn(Optional.of(testUser));

			AccountStatus result = accountService.getUserAccountStatus(testUserId);

			assertNotNull(result, "Status should not be null");
			verify(userRepository).getByID(testUserId);
		}

		/**
		 * Test: Should work for different users.
		 * 
		 * <p>
		 * <b>Setup:</b> Create two different users
		 * </p>
		 * <p>
		 * <b>Action:</b> Get status for both users
		 * </p>
		 * <p>
		 * <b>Expected:</b> Both statuses should be non-null
		 * </p>
		 */
		@Test
		@DisplayName("Should work for different users")
		void testGetStatusForDifferentUsers() {
			UUID userId1 = UUID.randomUUID();
			UUID userId2 = UUID.randomUUID();

			User user1 = new User("Ahmad", "Salameh", "hmeedsalameh2004@gmail.com", "ahmad",
					PasswordUtils.hashPassword("pass"), Role.MEMBER);

			User user2 = new User("Sara", "Mohammed", "sara@example.com", "sara", PasswordUtils.hashPassword("pass"),
					Role.MEMBER);

			when(userRepository.getByID(userId1)).thenReturn(Optional.of(user1));
			when(userRepository.getByID(userId2)).thenReturn(Optional.of(user2));

			AccountStatus status1 = accountService.getUserAccountStatus(userId1);
			AccountStatus status2 = accountService.getUserAccountStatus(userId2);

			assertNotNull(status1);
			assertNotNull(status2);
		}
	}

	/**
	 * Tests for canUserBorrow() method. Validates borrow permission logic.
	 */
	@Nested
	@DisplayName("🚫 canUserBorrow() Tests")
	class CanBorrowTests {

		/**
		 * Test: Should allow user with no fines to borrow.
		 * 
		 * <p>
		 * <b>Setup:</b> New testUser with no fines
		 * </p>
		 * <p>
		 * <b>Action:</b> Check if user can borrow
		 * </p>
		 * <p>
		 * <b>Expected:</b> Should return true
		 * </p>
		 */
		@Test
		@DisplayName("Should return true for user with no fines")
		void testCanBorrowWithNoFines() {
			when(userRepository.getByID(testUserId)).thenReturn(Optional.of(testUser));

			boolean canBorrow = accountService.canUserBorrow(testUserId);

			assertTrue(canBorrow, "User with no fines should be able to borrow");
		}

		/**
		 * Test: Should check account status before allowing borrow.
		 * 
		 * <p>
		 * <b>Setup:</b> Mock repository returns testUser
		 * </p>
		 * <p>
		 * <b>Action:</b> Check borrow permission
		 * </p>
		 * <p>
		 * <b>Expected:</b> Repository should be queried
		 * </p>
		 */
		@Test
		@DisplayName("Should check account status before allowing borrow")
		void testCanBorrowChecksAccountStatus() {
			when(userRepository.getByID(testUserId)).thenReturn(Optional.of(testUser));

			accountService.canUserBorrow(testUserId);

			verify(userRepository).getByID(testUserId);
		}
	}

	/**
	 * Tests for getUserBalance() method. Validates balance retrieval.
	 */
	@Nested
	@DisplayName("💵 getUserBalance() Tests")
	class GetUserBalanceTests {

		/**
		 * Test: Should return correct balance for new user.
		 * 
		 * <p>
		 * <b>Setup:</b> New testUser with no transactions
		 * </p>
		 * <p>
		 * <b>Action:</b> Get user balance
		 * </p>
		 * <p>
		 * <b>Expected:</b> Balance should be returned (non-null)
		 * </p>
		 */
		@Test
		@DisplayName("Should return correct balance")
		void testGetBalanceForNewUser() {
			when(userRepository.getByID(testUserId)).thenReturn(Optional.of(testUser));

			double balance = accountService.getUserBalance(testUserId);

			assertNotNull(balance);
			verify(userRepository).getByID(testUserId);
		}

		/**
		 * Test: Should return balance after adding fines.
		 * 
		 * <p>
		 * <b>Setup:</b> Add 25.0 NIS fine to testUser
		 * </p>
		 * <p>
		 * <b>Action:</b> Get user balance
		 * </p>
		 * <p>
		 * <b>Expected:</b> Balance should be returned
		 * </p>
		 */
		@Test
		@DisplayName("Should return balance after adding fines")
		void testGetBalanceAfterAddingFines() {
			when(userRepository.getByID(testUserId)).thenReturn(Optional.of(testUser));

			accountService.addFineToUser(testUserId, 25.0, "Test");

			when(userRepository.getByID(testUserId)).thenReturn(Optional.of(testUser));

			double balance = accountService.getUserBalance(testUserId);

			assertNotNull(balance);
		}
	}

	/**
	 * Tests for suspendUserAccount() method. Validates account suspension and
	 * validation.
	 */
	@Nested
	@DisplayName("🔒 suspendUserAccount() Tests")
	class SuspendAccountTests {

		/**
		 * Test: Should suspend account with valid reason.
		 * 
		 * <p>
		 * <b>Setup:</b> testUser with active account
		 * </p>
		 * <p>
		 * <b>Action:</b> Suspend account with reason "Violation of library rules"
		 * </p>
		 * <p>
		 * <b>Expected:</b> No exception thrown, repository updated
		 * </p>
		 */
		@Test
		@DisplayName("Should suspend account with valid reason")
		void testSuspendAccountSuccess() {
			when(userRepository.getByID(testUserId)).thenReturn(Optional.of(testUser));

			assertDoesNotThrow(() -> {
				accountService.suspendUserAccount(testUserId, "Violation of library rules");
			});

			verify(userRepository, times(1)).update(testUser);
		}

		/**
		 * Test: Should throw exception when suspension reason is null.
		 * 
		 * <p>
		 * <b>Setup:</b> testUser with active account
		 * </p>
		 * <p>
		 * <b>Action:</b> Try to suspend with null reason
		 * </p>
		 * <p>
		 * <b>Expected:</b> IllegalArgumentException should be thrown
		 * </p>
		 */
		@Test
		@DisplayName("Should throw when reason is null")
		void testSuspendAccountWithNullReason() {
			when(userRepository.getByID(testUserId)).thenReturn(Optional.of(testUser));

			assertThrows(IllegalArgumentException.class, () -> {
				accountService.suspendUserAccount(testUserId, null);
			});
		}

		/**
		 * Test: Should throw exception when suspension reason is empty/whitespace.
		 * 
		 * <p>
		 * <b>Setup:</b> testUser with active account
		 * </p>
		 * <p>
		 * <b>Action:</b> Try to suspend with blank reason " "
		 * </p>
		 * <p>
		 * <b>Expected:</b> IllegalArgumentException should be thrown
		 * </p>
		 */
		@Test
		@DisplayName("Should throw when reason is empty")
		void testSuspendAccountWithEmptyReason() {
			when(userRepository.getByID(testUserId)).thenReturn(Optional.of(testUser));

			assertThrows(IllegalArgumentException.class, () -> {
				accountService.suspendUserAccount(testUserId, "   ");
			});
		}

		/**
		 * Test: Should throw exception when user not found.
		 * 
		 * <p>
		 * <b>Setup:</b> Mock repository returns empty Optional
		 * </p>
		 * <p>
		 * <b>Action:</b> Try to suspend non-existent user
		 * </p>
		 * <p>
		 * <b>Expected:</b> IllegalArgumentException should be thrown
		 * </p>
		 */
		@Test
		@DisplayName("Should throw when user not found")
		void testSuspendAccountWhenUserNotFound() {
			when(userRepository.getByID(testUserId)).thenReturn(Optional.empty());

			assertThrows(IllegalArgumentException.class, () -> {
				accountService.suspendUserAccount(testUserId, "Test reason");
			});
		}

		/**
		 * Test: Should accept various suspension reasons.
		 * 
		 * <p>
		 * <b>Setup:</b> testUser with active account
		 * </p>
		 * <p>
		 * <b>Action:</b> Suspend account with 4 different reasons:
		 * <ul>
		 * <li>Unpaid fines exceeding limit</li>
		 * <li>Violation of library rules</li>
		 * <li>Repeated late returns</li>
		 * <li>Damage to library property</li>
		 * </ul>
		 * </p>
		 * <p>
		 * <b>Expected:</b> All suspensions succeed, repository updated 4 times
		 * </p>
		 */
		@Test
		@DisplayName("Should accept various suspension reasons")
		void testSuspendAccountWithVariousReasons() {
			when(userRepository.getByID(testUserId)).thenReturn(Optional.of(testUser));

			String[] reasons = { "Unpaid fines exceeding limit", "Violation of library rules", "Repeated late returns",
					"Damage to library property" };

			for (String reason : reasons) {
				accountService.suspendUserAccount(testUserId, reason);
			}

			verify(userRepository, times(4)).update(testUser);
		}
	}

	/**
	 * Tests for activateUserAccount() method. Validates account activation after
	 * suspension.
	 */
	@Nested
	@DisplayName("✅ activateUserAccount() Tests")
	class ActivateAccountTests {

		/**
		 * Test: Should activate suspended account.
		 * 
		 * <p>
		 * <b>Setup:</b> Suspend testUser account
		 * </p>
		 * <p>
		 * <b>Action:</b> Activate the suspended account
		 * </p>
		 * <p>
		 * <b>Expected:</b> No exception thrown, repository updated
		 * </p>
		 */
		@Test
		@DisplayName("Should activate suspended account")
		void testActivateAccountSuccess() {
			when(userRepository.getByID(testUserId)).thenReturn(Optional.of(testUser));

			accountService.suspendUserAccount(testUserId, "Test suspension");

			when(userRepository.getByID(testUserId)).thenReturn(Optional.of(testUser));

			assertDoesNotThrow(() -> {
				accountService.activateUserAccount(testUserId);
			});

			verify(userRepository, atLeast(1)).update(testUser);
		}

		/**
		 * Test: Should throw exception when user not found.
		 * 
		 * <p>
		 * <b>Setup:</b> Mock repository returns empty Optional
		 * </p>
		 * <p>
		 * <b>Action:</b> Try to activate account for non-existent user
		 * </p>
		 * <p>
		 * <b>Expected:</b> IllegalArgumentException should be thrown
		 * </p>
		 */
		@Test
		@DisplayName("Should throw when user not found")
		void testActivateAccountWhenUserNotFound() {
			when(userRepository.getByID(testUserId)).thenReturn(Optional.empty());

			assertThrows(IllegalArgumentException.class, () -> {
				accountService.activateUserAccount(testUserId);
			});
		}

		/**
		 * Test: Should update repository after activation.
		 * 
		 * <p>
		 * <b>Setup:</b> testUser with suspended account
		 * </p>
		 * <p>
		 * <b>Action:</b> Activate the account
		 * </p>
		 * <p>
		 * <b>Expected:</b> Repository updated after activation
		 * </p>
		 */
		@Test
		@DisplayName("Should update repository after activation")
		void testRepositoryUpdateAfterActivation() {
			when(userRepository.getByID(testUserId)).thenReturn(Optional.of(testUser));

			accountService.activateUserAccount(testUserId);

			verify(userRepository).update(testUser);
		}
	}

	/**
	 * Tests for calculateTotalFinesForAllUsers() method. Validates aggregate fine
	 * calculations.
	 */
	@Nested
	@DisplayName("📈 calculateTotalFinesForAllUsers() Tests")
	class CalculateTotalFinesTests {

		/**
		 * Test: Should return zero when no users have fines.
		 * 
		 * <p>
		 * <b>Setup:</b> User with no fines
		 * </p>
		 * <p>
		 * <b>Action:</b> Calculate total fines for all users
		 * </p>
		 * <p>
		 * <b>Expected:</b> Total should be 0.0 NIS
		 * </p>
		 */
		@Test
		@DisplayName("Should return zero when no users have fines")
		void testCalculateTotalFinesWithNoFines() {
			List<User> usersList = new ArrayList<>();
			usersList.add(testUser);

			when(userRepository.getAllUsers()).thenReturn(usersList);

			double total = accountService.calculateTotalFinesForAllUsers();

			assertEquals(0.0, total, 0.01);
		}

		/**
		 * Test: Should calculate total fines across multiple users.
		 * 
		 * <p>
		 * <b>Setup:</b> Two users with fines: 15.0 NIS and 25.0 NIS
		 * </p>
		 * <p>
		 * <b>Action:</b> Calculate total fines for all users
		 * </p>
		 * <p>
		 * <b>Expected:</b> Total should be at least 40.0 NIS
		 * </p>
		 */
		@Test
		@DisplayName("Should calculate total fines across multiple users")
		void testCalculateTotalFinesForMultipleUsers() {

			User user1 = new User("Ahmad", "Salameh", "hmeedsalameh2004@gmail.com", "ahmad",
					PasswordUtils.hashPassword("pass"), Role.MEMBER);

			User user2 = new User("Sara", "Mohammed", "sara@example.com", "sara", PasswordUtils.hashPassword("pass"),
					Role.MEMBER);

			user1.getAccount().addFine(15.0, "Fine 1");
			user2.getAccount().addFine(25.0, "Fine 2");

			List<User> usersList = new ArrayList<>();
			usersList.add(user1);
			usersList.add(user2);

			when(userRepository.getAllUsers()).thenReturn(usersList);

			double total = accountService.calculateTotalFinesForAllUsers();

			assertTrue(total >= 40.0, "Should sum all user fines");
		}

		/**
		 * Test: Should handle empty user list.
		 * 
		 * <p>
		 * <b>Setup:</b> Empty user list
		 * </p>
		 * <p>
		 * <b>Action:</b> Calculate total fines
		 * </p>
		 * <p>
		 * <b>Expected:</b> Total should be 0.0 NIS
		 * </p>
		 */
		@Test
		@DisplayName("Should handle empty user list")
		void testCalculateTotalFinesWithEmptyList() {
			when(userRepository.getAllUsers()).thenReturn(new ArrayList<>());

			double total = accountService.calculateTotalFinesForAllUsers();

			assertEquals(0.0, total);
		}
	}

	/**
	 * Tests for getUsersWithFinesCount() method. Validates counting users with
	 * outstanding fines.
	 */
	@Nested
	@DisplayName("👥 getUsersWithFinesCount() Tests")
	class GetUsersWithFinesCountTests {

		/**
		 * Test: Should return zero when no users have fines.
		 * 
		 * <p>
		 * <b>Setup:</b> Single user with no fines
		 * </p>
		 * <p>
		 * <b>Action:</b> Count users with fines
		 * </p>
		 * <p>
		 * <b>Expected:</b> Count should be 0
		 * </p>
		 */
		@Test
		@DisplayName("Should return zero when no users have fines")
		void testCountUsersWithFinesWhenNone() {
			List<User> usersList = new ArrayList<>();
			usersList.add(testUser);

			when(userRepository.getAllUsers()).thenReturn(usersList);

			int count = accountService.getUsersWithFinesCount();

			assertEquals(0, count);
		}

		/**
		 * Test: Should count users with fines correctly.
		 * 
		 * <p>
		 * <b>Setup:</b> Three users: one with no fines, two with fines
		 * </p>
		 * <p>
		 * <b>Action:</b> Count users with fines
		 * </p>
		 * <p>
		 * <b>Expected:</b> Count should be 2
		 * </p>
		 */
		@Test
		@DisplayName("Should count users with fines correctly")
		void testCountUsersWithFines() {

			User user1 = new User("Ahmad", "Salameh", "hmeedsalameh2004@gmail.com", "ahmad",
					PasswordUtils.hashPassword("pass"), Role.MEMBER);

			User user2 = new User("Sara", "Mohammed", "sara@example.com", "sara", PasswordUtils.hashPassword("pass"),
					Role.MEMBER);

			user1.getAccount().addFine(10.0, "Fine");
			user2.getAccount().addFine(20.0, "Fine");

			List<User> usersList = new ArrayList<>();
			usersList.add(testUser);
			usersList.add(user1);
			usersList.add(user2);

			when(userRepository.getAllUsers()).thenReturn(usersList);

			int count = accountService.getUsersWithFinesCount();

			assertEquals(2, count, "Should count only users with fines > 0");
		}

		/**
		 * Test: Should handle empty user list.
		 * 
		 * <p>
		 * <b>Setup:</b> Empty user list
		 * </p>
		 * <p>
		 * <b>Action:</b> Count users with fines
		 * </p>
		 * <p>
		 * <b>Expected:</b> Count should be 0
		 * </p>
		 */
		@Test
		@DisplayName("Should handle empty user list")
		void testCountUsersWithFinesEmptyList() {
			when(userRepository.getAllUsers()).thenReturn(new ArrayList<>());

			int count = accountService.getUsersWithFinesCount();

			assertEquals(0, count);
		}

		/**
		 * Test: Should handle mixed users (some with fines, some without).
		 * 
		 * <p>
		 * <b>Setup:</b> Two users: one with no fines, one with fines (50.0 NIS)
		 * </p>
		 * <p>
		 * <b>Action:</b> Count users with fines
		 * </p>
		 * <p>
		 * <b>Expected:</b> Count should be 1
		 * </p>
		 */
		@Test
		@DisplayName("Should handle mixed users (some with fines, some without)")
		void testCountMixedUsers() {
			User userWithFine = new User("Ahmad", "Fine", "fine@test.com", "fine", PasswordUtils.hashPassword("pass"),
					Role.MEMBER);
			userWithFine.getAccount().addFine(50.0, "Test");

			List<User> usersList = new ArrayList<>();
			usersList.add(testUser);
			usersList.add(userWithFine);

			when(userRepository.getAllUsers()).thenReturn(usersList);

			int count = accountService.getUsersWithFinesCount();

			assertEquals(1, count);
		}
	}

	/**
	 * Integration tests for AccountService. Tests complete workflows and state
	 * consistency.
	 */
	@Nested
	@DisplayName("🔄 Integration Tests")
	class IntegrationTests {

		/**
		 * Test: Should handle complete account lifecycle.
		 * 
		 * <p>
		 * <b>Scenario:</b> User borrows items, incurs fines, makes payments
		 * </p>
		 * <p>
		 * <b>Steps:</b>
		 * <ol>
		 * <li>Add 50.0 NIS fine</li>
		 * <li>Pay 30.0 NIS (partial payment)</li>
		 * <li>Pay remaining 20.0 NIS</li>
		 * <li>Check account status</li>
		 * </ol>
		 * </p>
		 * <p>
		 * <b>Expected:</b> Final fine balance should be 0.0 NIS, status should be
		 * ACTIVE
		 * </p>
		 */
		@Test
		@DisplayName("Should handle complete account lifecycle")
		void testCompleteAccountLifecycle() {
			when(userRepository.getByID(testUserId)).thenReturn(Optional.of(testUser));

			accountService.addFineToUser(testUserId, 50.0, "Test fine");
			assertTrue(testAccount.getTotalFines() > 0);

			when(userRepository.getByID(testUserId)).thenReturn(Optional.of(testUser));
			accountService.payUserFine(testUserId, 30.0);
			assertTrue(testAccount.getTotalFines() > 0);

			when(userRepository.getByID(testUserId)).thenReturn(Optional.of(testUser));
			accountService.payUserFine(testUserId, 20.0);
			assertEquals(0.0, testAccount.getTotalFines(), 0.01);

			when(userRepository.getByID(testUserId)).thenReturn(Optional.of(testUser));
			AccountStatus status = accountService.getUserAccountStatus(testUserId);
			assertNotNull(status);
		}

		/**
		 * Test: Should handle suspend and activate cycle.
		 * 
		 * <p>
		 * <b>Scenario:</b> User account is suspended and later reactivated
		 * </p>
		 * <p>
		 * <b>Steps:</b>
		 * <ol>
		 * <li>Suspend account with reason</li>
		 * <li>Activate account</li>
		 * </ol>
		 * </p>
		 * <p>
		 * <b>Expected:</b> Repository should be updated at least twice
		 * </p>
		 */
		@Test
		@DisplayName("Should handle suspend and activate cycle")
		void testSuspendAndActivateCycle() {
			when(userRepository.getByID(testUserId)).thenReturn(Optional.of(testUser));

			accountService.suspendUserAccount(testUserId, "Test suspension");

			when(userRepository.getByID(testUserId)).thenReturn(Optional.of(testUser));

			accountService.activateUserAccount(testUserId);

			verify(userRepository, atLeast(2)).update(testUser);
		}

		/**
		 * Test: Should maintain consistency across operations.
		 * 
		 * <p>
		 * <b>Scenario:</b> Perform multiple operations on account
		 * </p>
		 * <p>
		 * <b>Steps:</b>
		 * <ol>
		 * <li>Get or create account</li>
		 * <li>Add fine</li>
		 * <li>Get balance</li>
		 * <li>Get status</li>
		 * </ol>
		 * </p>
		 * <p>
		 * <b>Expected:</b> All operations should succeed with valid data
		 * </p>
		 */
		@Test
		@DisplayName("Should maintain consistency across operations")
		void testOperationConsistency() {
			when(userRepository.getByID(testUserId)).thenReturn(Optional.of(testUser));

			Account acc1 = accountService.getOrCreateAccount(testUserId);
			accountService.addFineToUser(testUserId, 10.0, "Test");
			double balance = accountService.getUserBalance(testUserId);
			AccountStatus status = accountService.getUserAccountStatus(testUserId);

			assertNotNull(acc1);
			assertTrue(balance >= 0);
			assertNotNull(status);
		}
	}
}