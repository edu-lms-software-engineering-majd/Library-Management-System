package lms.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class AccountTest {

	private static final double SUSPENSION_THRESHOLD = 100.0;
	private static final double SMALL_FINE = 10.0;
	private static final double MEDIUM_FINE = 50.0;
	private static final double LARGE_FINE = 150.0;
	private static final double ABOVE_THRESHOLD = 101.0;
	private static final double JUST_ABOVE_THRESHOLD = 100.01;
	private static final String LATE_RETURN_REASON = "Late return";
	private static final String MULTIPLE_VIOLATIONS_REASON = "Multiple violations";

	private User mockUser;
	private UUID testUserId;
	private Account account;

	@BeforeEach
	void setUp() {
		mockUser = Mockito.mock(User.class);
		testUserId = UUID.randomUUID();
		Mockito.when(mockUser.getUserID()).thenReturn(testUserId);
		account = new Account(testUserId);
	}

	@AfterEach
	void tearDown() {
		account = null;
		mockUser = null;
	}

	@Test
	void givenNegativeAmount_whenAddFine_thenThrowsIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> 
			account.addFine(-10, "due date of 10 days"));
	}

	@Test
	void givenZeroFineAmount_whenAddFine_thenThrowsIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> 
			account.addFine(0, "due date of 10 days"));
	}

	@Test
	void givenValidAmount_whenAddFine_thenFineIsAssigned() {
		account.addFine(SMALL_FINE, "due date of 10 days");

		assertEquals(SMALL_FINE, account.getTotalFines());
		assertEquals(1, account.getFineTransactions().size());
		assertEquals(AccountStatus.ACTIVE, account.getStatus());
	}

	@Test
	void givenNegativeAmount_whenPayFine_thenThrowsIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> 
			account.payFine(-10));
	}

	@Test
	void givenZeroAmount_whenPayFine_thenThrowsIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> 
			account.payFine(0));
	}

	@Test
	void givenAmountExceedsTotalFines_whenPayFine_thenThrowsIllegalArgumentException() {
		account.addFine(SMALL_FINE, LATE_RETURN_REASON);

		assertThrows(IllegalArgumentException.class, () -> 
			account.payFine(20));
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
	void givenAccount_whenGetFineTransactions_thenReturnsUnmodifiableList() {
		account.addFine(MEDIUM_FINE, LATE_RETURN_REASON);

		assertThrows(UnsupportedOperationException.class, () -> 
			account.getFineTransactions().clear());
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
		account.addFine(50.50, LATE_RETURN_REASON);

		String result = account.toString();

		assertNotNull(result);
		assertTrue(result.contains("Account"));
		assertTrue(result.contains("50.50"));
		assertTrue(result.contains("ACTIVE"));
	}

	@Test
	void givenExactThresholdAmount_whenAddFine_thenAccountRemainActive() {
		account.addFine(SUSPENSION_THRESHOLD, "Threshold fine");

		assertEquals(SUSPENSION_THRESHOLD, account.getTotalFines());
		assertEquals(AccountStatus.ACTIVE, account.getStatus());
	}

	@Test
	void givenJustAboveThreshold_whenAddFine_thenAccountIsSuspended() {
		account.addFine(JUST_ABOVE_THRESHOLD, "Just over threshold");

		assertEquals(JUST_ABOVE_THRESHOLD, account.getTotalFines());
		assertEquals(AccountStatus.SUSPENDED, account.getStatus());
	}
}
