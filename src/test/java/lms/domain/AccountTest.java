package lms.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AccountTest {

	private static final double SUSPENSION_THRESHOLD = 100.0;
	private static final double SMALL_FINE = 10.0;
	private static final double MEDIUM_FINE = 50.0;
	private static final double LARGE_FINE = 150.0;
	private static final double ABOVE_THRESHOLD = 101.0;
	private static final String LATE_RETURN_REASON = "Late return";
	private static final String MULTIPLE_VIOLATIONS_REASON = "Multiple violations";

	private UUID testUserId;
	private Account account;

	@BeforeEach
	void setUp() {
		testUserId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
		account = new Account(testUserId);
	}

	@AfterEach
	void tearDown() {
		account = null;
	}

	@Test
	void givenNewAccount_whenCreated_thenFieldsAreInitializedCorrectly() {
		assertNotNull(account.getAccountId());
		assertEquals(testUserId, account.getUserId());
		assertEquals(0.0, account.getTotalFines());
		assertEquals(AccountStatus.ACTIVE, account.getStatus());
		assertNotNull(account.getCreatedAt());
		assertNotNull(account.getUpdatedAt());
		assertTrue(account.getFineTransactions().isEmpty());
	}

	@Test
	void givenValidAmount_whenAddFine_thenFineIsAssigned() {
		account.addFine(SMALL_FINE, "due date of 10 days");

		assertEquals(SMALL_FINE, account.getTotalFines());
		assertEquals(1, account.getFineTransactions().size());
		assertEquals(AccountStatus.ACTIVE, account.getStatus());
	}

	@Test
	void givenValidPayment_whenPayFine_thenFineIsReduced() {

		account.addFine(MEDIUM_FINE, LATE_RETURN_REASON);
		account.payFine(30);

		assertEquals(20.0, account.getTotalFines());
		assertEquals(2, account.getFineTransactions().size());
		assertEquals(AccountStatus.ACTIVE, account.getStatus());
	}

	@Test
	void givenFullPayment_whenPayFine_thenTotalFinesIsZero() {

		account.addFine(MEDIUM_FINE, LATE_RETURN_REASON);
		account.payFine(MEDIUM_FINE);

		assertEquals(0.0, account.getTotalFines());
		assertEquals(2, account.getFineTransactions().size());
	}

	@Test
	void givenFineExceedsThreshold_whenAddFine_thenAccountIsSuspended() {
		account.addFine(ABOVE_THRESHOLD, "Multiple late returns");

		assertEquals(ABOVE_THRESHOLD, account.getTotalFines());
		assertEquals(AccountStatus.SUSPENDED, account.getStatus());
	}

	@Test
	void givenSuspendedAccount_whenPayAllFines_thenAccountIsActivated() {

		account.addFine(LARGE_FINE, MULTIPLE_VIOLATIONS_REASON);
		assertEquals(AccountStatus.SUSPENDED, account.getStatus());

		account.payFine(LARGE_FINE);

		assertEquals(0.0, account.getTotalFines());
		assertEquals(AccountStatus.ACTIVE, account.getStatus());
	}

	@Test
	void givenSuspendedAccount_whenPartialPayment_thenAccountRemainsSuspended() {

		account.addFine(LARGE_FINE, MULTIPLE_VIOLATIONS_REASON);
		assertEquals(AccountStatus.SUSPENDED, account.getStatus());

		account.payFine(MEDIUM_FINE);

		assertEquals(100.0, account.getTotalFines());
		assertEquals(AccountStatus.SUSPENDED, account.getStatus());
	}

	@Test
	void givenAccount_whenGetFineTransactions_thenReturnsUnmodifiableList() {

		account.addFine(MEDIUM_FINE, LATE_RETURN_REASON);

		assertThrows(UnsupportedOperationException.class, () -> {
			account.getFineTransactions().clear();
		});
	}

	@Test
	void givenMultipleFines_whenAdded_thenTotalFinesAccumulates() {

		account.addFine(10, "Fine 1");
		account.addFine(20, "Fine 2");
		account.addFine(30, "Fine 3");

		assertEquals(60.0, account.getTotalFines());
		assertEquals(3, account.getFineTransactions().size());
	}

	@Test
	void givenMultiplePayments_whenMade_thenTotalFinesDecreasesCorrectly() {

		account.addFine(100, "Large fine");
		account.payFine(20);
		account.payFine(30);
		account.payFine(25);

		assertEquals(25.0, account.getTotalFines());
		assertEquals(4, account.getFineTransactions().size());
	}

	@Test
	void givenAccount_whenSetStatus_thenStatusIsUpdated() {

		assertEquals(AccountStatus.ACTIVE, account.getStatus());

		account.setStatus(AccountStatus.BLACKLISTED);

		assertEquals(AccountStatus.BLACKLISTED, account.getStatus());
	}

	@Test
	void givenAccount_whenToString_thenReturnsFormattedString() {
		account = new Account(testUserId);
		account.setStatus(AccountStatus.ACTIVE);
		account.addFine(1.00, "test");
		account.addFine(49.50, "test2");

		String result = account.toString();

		assertNotNull(result);
		assertTrue(result.contains("Account"));

		assertTrue(result.contains("Balance"));

		assertTrue(result.contains("ACTIVE"));
	}

	@Test
	void givenExactThresholdAmount_whenAddFine_thenAccountRemainsActive() {
		account.addFine(SUSPENSION_THRESHOLD, "Threshold fine");

		assertEquals(SUSPENSION_THRESHOLD, account.getTotalFines());
		assertEquals(AccountStatus.ACTIVE, account.getStatus());
	}

	@Test
	void givenJustAboveThreshold_whenAddFine_thenAccountIsSuspended() {
		account.addFine(100.01, "Just above threshold");

		assertEquals(100.01, account.getTotalFines());
		assertEquals(AccountStatus.SUSPENDED, account.getStatus());
	}

	@Test
	void givenActiveAccountWithFines_whenAddMoreFines_thenStillActive() {
		account.addFine(30.0, "First fine");
		assertEquals(AccountStatus.ACTIVE, account.getStatus());

		account.addFine(40.0, "Second fine");

		assertEquals(70.0, account.getTotalFines());
		assertEquals(AccountStatus.ACTIVE, account.getStatus());
	}

	@Test
	void givenAccountWithFinesBelowThreshold_whenAddFineExceedingThreshold_thenAccountSuspended() {
		account.addFine(80.0, "First fine");
		assertEquals(AccountStatus.ACTIVE, account.getStatus());

		account.addFine(30.0, "Second fine");

		assertEquals(110.0, account.getTotalFines());
		assertEquals(AccountStatus.SUSPENDED, account.getStatus());
	}

	@Test
	void givenSuspendedAccount_whenPayPartialLeavingAboveZero_thenStillSuspended() {
		account.addFine(150.0, "Large fine");
		assertEquals(AccountStatus.SUSPENDED, account.getStatus());

		account.payFine(50.0);

		assertEquals(100.0, account.getTotalFines());
		assertEquals(AccountStatus.SUSPENDED, account.getStatus());
	}

	// -----------------------------------------
	// MISC TESTS
	// -----------------------------------------

	@Test
	void givenMultipleAccounts_whenCreated_thenEachHasUniqueId() {
		Account a1 = new Account(UUID.randomUUID());
		Account a2 = new Account(UUID.randomUUID());
		Account a3 = new Account(UUID.randomUUID());

		assertNotEquals(a1.getAccountId(), a2.getAccountId());
		assertNotEquals(a2.getAccountId(), a3.getAccountId());
		assertNotEquals(a1.getAccountId(), a3.getAccountId());
	}

	@Test
	void givenAccountWithZeroFines_whenNoActivity_thenRemainsActive() {
		assertEquals(0.0, account.getTotalFines());
		assertEquals(AccountStatus.ACTIVE, account.getStatus());
	}

	@Test
	void givenActiveAccountWithZeroFines_whenCheckCanBorrow_thenReturnsTrue() {
		assertTrue(account.canBorrow());
		assertTrue(account.canBorrowBooks());

		account.addFine(10.0, "Small fine");
		assertFalse(account.canBorrow());
	}

	@Test
	void givenAccountWithFines_whenCheckOutstandingBalance_thenReturnsTrue() {
		assertFalse(account.hasOutstandingBalance());

		account.addFine(25.0, "Fine");
		assertTrue(account.hasOutstandingBalance());
	}

	@Test
	void givenAccount_whenSuspendWithReason_thenAddsTransactionAndSuspends() {
		account.suspendAccount("Manual suspension");

		assertEquals(AccountStatus.SUSPENDED, account.getStatus());
		assertFalse(account.getFineTransactions().isEmpty());
	}

	@Test
	void givenSuspendedAccount_whenActivate_thenStatusBecomesActive() {
		account.suspendAccount("Test");
		assertEquals(AccountStatus.SUSPENDED, account.getStatus());

		account.activateAccount();
		assertEquals(AccountStatus.ACTIVE, account.getStatus());
	}

	@Test
	void givenAccount_whenGetBalance_thenReturnsCorrectAmount() {
		account.addFine(75.0, "Test fine");

		assertEquals(account.getTotalFines(), account.getBalance());
		assertEquals(75.0, account.getBalance());
	}

	@Test
	void givenNegativeFineAmount_whenAddFine_thenThrowsIllegalArgumentException() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			account.addFine(-10.0, "Invalid fine");
		});

		assertEquals("Fine amount must be positive", exception.getMessage());
	}

	@Test
	void givenZeroFineAmount_whenAddFine_thenThrowsIllegalArgumentException() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			account.addFine(0.0, "Zero fine");
		});

		assertEquals("Fine amount must be positive", exception.getMessage());
	}

	@Test
	void givenVerySmallNegativeFine_whenAddFine_thenThrowsIllegalArgumentException() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			account.addFine(-0.01, "Small negative fine");
		});

		assertEquals("Fine amount must be positive", exception.getMessage());
	}

	@Test
	void givenNegativePaymentAmount_whenPayFine_thenThrowsIllegalArgumentException() {
		account.addFine(50.0, "Initial fine");

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			account.payFine(-10.0);
		});

		assertEquals("Amount must be positive", exception.getMessage());
	}

	@Test
	void givenZeroPaymentAmount_whenPayFine_thenThrowsIllegalArgumentException() {
		account.addFine(50.0, "Initial fine");

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			account.payFine(0.0);
		});

		assertEquals("Amount must be positive", exception.getMessage());
	}

	@Test
	void givenPaymentExceedsTotalFines_whenPayFine_thenThrowsIllegalArgumentException() {
		account.addFine(50.0, "Initial fine");

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			account.payFine(100.0);
		});

		assertEquals("Amount exceeds total fines", exception.getMessage());
	}

	@Test
	void givenPaymentSlightlyExceedsTotalFines_whenPayFine_thenThrowsIllegalArgumentException() {
		account.addFine(50.0, "Initial fine");

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			account.payFine(50.01);
		});

		assertEquals("Amount exceeds total fines", exception.getMessage());
	}

	@Test
	void givenNoFines_whenPayFine_thenThrowsIllegalArgumentException() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			account.payFine(10.0);
		});

		assertEquals("Amount exceeds total fines", exception.getMessage());
	}

	@Test
	void givenInvalidFineAmount_whenAddFine_thenAccountStateUnchanged() {
		double initialFines = account.getTotalFines();
		int initialTransactionCount = account.getFineTransactions().size();

		try {
			account.addFine(-10.0, "Invalid");
		} catch (IllegalArgumentException e) {
		}

		assertEquals(initialFines, account.getTotalFines());
		assertEquals(initialTransactionCount, account.getFineTransactions().size());
		assertEquals(AccountStatus.ACTIVE, account.getStatus());
	}

	@Test
	void givenInvalidPaymentAmount_whenPayFine_thenAccountStateUnchanged() {
		account.addFine(50.0, "Initial fine");
		double initialFines = account.getTotalFines();
		int initialTransactionCount = account.getFineTransactions().size();

		try {
			account.payFine(100.0);
		} catch (IllegalArgumentException e) {
		}

		assertEquals(initialFines, account.getTotalFines());
		assertEquals(initialTransactionCount, account.getFineTransactions().size());
	}

	@Test
	void givenAccountWithSmallFine_whenCanBorrowBooks_thenReturnsFalse() {
		account.addFine(5.0, "Small fine");

		assertFalse(account.canBorrowBooks());
		assertFalse(account.canBorrow());
	}

	@Test
	void givenSuspendedAccountWithoutFines_whenCanBorrowBooks_thenReturnsFalse() {
		account.suspendAccount("Manual suspension");

		assertFalse(account.canBorrowBooks());
		assertFalse(account.canBorrow());
		assertEquals(0.0, account.getTotalFines());
	}

	@Test
	void givenActiveAccountWithZeroFines_whenCanBorrowBooks_thenReturnsTrue() {
		assertTrue(account.canBorrowBooks());
		assertTrue(account.canBorrow());
		assertEquals(AccountStatus.ACTIVE, account.getStatus());
		assertEquals(0.0, account.getTotalFines());

	}
}
