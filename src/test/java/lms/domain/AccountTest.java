package lms.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class AccountTest {

	@Test
	void givenNegativeAmount_whenAddFine_ThrowsIllegalArgumentException() {
		User user = Mockito.mock(User.class);
		Mockito.when(user.getUserID()).thenReturn(UUID.randomUUID());
		
		assertThrows(IllegalArgumentException.class, () -> {
			Account account = new Account(user.getUserID());
			account.addFine(-10, "due date of 10 days");
		});
	}
	
	@Test
	void givenZeroFineAmount_whenAddFine_ThrowsIllegalArgumentException() {
		User user = Mockito.mock(User.class);
		Mockito.when(user.getUserID()).thenReturn(UUID.randomUUID());
		
		assertThrows(IllegalArgumentException.class, () -> {
			Account account = new Account(user.getUserID());
			account.addFine(0, "due date of 10 days");
		});
	}
	
	@Test
	void givenValidAmount_whenAddFine_thenFineIsAssigned() {
		User user = Mockito.mock(User.class);
		Mockito.when(user.getUserID()).thenReturn(UUID.randomUUID());
		
		Account account = new Account(user.getUserID());
		account.addFine(10, "due date of 10 days");
		
		assertEquals(10.0, account.getTotalFines());
		assertEquals(1, account.getFineTransactions().size());
		assertEquals(AccountStatus.ACTIVE, account.getStatus());
	}
	
	@Test
	void givenNegativeAmount_whenPayFine_ThrowsIllegalArgumentException() {
		User user = Mockito.mock(User.class);
		Mockito.when(user.getUserID()).thenReturn(UUID.randomUUID());
		
		assertThrows(IllegalArgumentException.class, () -> {
			Account account = new Account(user.getUserID());
			account.payFine(-10);
		});
	}
	
	@Test
	void givenZeroAmount_whenPayFine_ThrowsIllegalArgumentException() {
		User user = Mockito.mock(User.class);
		Mockito.when(user.getUserID()).thenReturn(UUID.randomUUID());
		
		assertThrows(IllegalArgumentException.class, () -> {
			Account account = new Account(user.getUserID());
			account.payFine(0);
		});
	}
	
	@Test
	void givenAmountExceedsTotalFines_whenPayFine_ThrowsIllegalArgumentException() {
		User user = Mockito.mock(User.class);
		Mockito.when(user.getUserID()).thenReturn(UUID.randomUUID());
		
		assertThrows(IllegalArgumentException.class, () -> {
			Account account = new Account(user.getUserID());
			account.addFine(10, "Late return");
			account.payFine(20);
		});
	}
	
	@Test
	void givenValidPayment_whenPayFine_thenFineIsReduced() {
		User user = Mockito.mock(User.class);
		Mockito.when(user.getUserID()).thenReturn(UUID.randomUUID());
		
		Account account = new Account(user.getUserID());
		account.addFine(50, "Late return");
		account.payFine(30);
		
		assertEquals(20.0, account.getTotalFines());
		assertEquals(2, account.getFineTransactions().size());
		assertEquals(AccountStatus.ACTIVE, account.getStatus());
	}
	
	@Test
	void givenFullPayment_whenPayFine_thenTotalFinesIsZero() {
		User user = Mockito.mock(User.class);
		Mockito.when(user.getUserID()).thenReturn(UUID.randomUUID());
		
		Account account = new Account(user.getUserID());
		account.addFine(50, "Late return");
		account.payFine(50);
		
		assertEquals(0.0, account.getTotalFines());
		assertEquals(2, account.getFineTransactions().size());
	}
	
	@Test
	void givenFineExceedsThreshold_whenAddFine_thenAccountIsSuspended() {
		User user = Mockito.mock(User.class);
		Mockito.when(user.getUserID()).thenReturn(UUID.randomUUID());
		
		Account account = new Account(user.getUserID());
		account.addFine(101, "Multiple late returns");
		
		assertEquals(101.0, account.getTotalFines());
		assertEquals(AccountStatus.SUSPENDED, account.getStatus());
	}
	
	@Test
	void givenSuspendedAccount_whenPayAllFines_thenAccountIsActivated() {
		User user = Mockito.mock(User.class);
		Mockito.when(user.getUserID()).thenReturn(UUID.randomUUID());
		
		Account account = new Account(user.getUserID());
		account.addFine(150, "Multiple violations");
		assertEquals(AccountStatus.SUSPENDED, account.getStatus());
		
		account.payFine(150);
		
		assertEquals(0.0, account.getTotalFines());
		assertEquals(AccountStatus.ACTIVE, account.getStatus());
	}
	
	@Test
	void givenSuspendedAccount_whenPartialPayment_thenAccountRemainsSuspended() {
		User user = Mockito.mock(User.class);
		Mockito.when(user.getUserID()).thenReturn(UUID.randomUUID());
		
		Account account = new Account(user.getUserID());
		account.addFine(150, "Multiple violations");
		assertEquals(AccountStatus.SUSPENDED, account.getStatus());
		
		account.payFine(50);
		
		assertEquals(100.0, account.getTotalFines());
		assertEquals(AccountStatus.SUSPENDED, account.getStatus());
	}
	
	@Test
	void givenNewAccount_whenCreated_thenFieldsAreInitializedCorrectly() {
		UUID userId = UUID.randomUUID();
		Account account = new Account(userId);
		
		assertNotNull(account.getAccountId());
		assertEquals(userId, account.getUserId());
		assertEquals(0.0, account.getTotalFines());
		assertEquals(AccountStatus.ACTIVE, account.getStatus());
		assertNotNull(account.getCreatedAt());
		assertNotNull(account.getUpdatedAt());
		assertTrue(account.getFineTransactions().isEmpty());
	}
	
	@Test
	void givenAccount_whenGetFineTransactions_thenReturnsUnmodifiableList() {
		User user = Mockito.mock(User.class);
		Mockito.when(user.getUserID()).thenReturn(UUID.randomUUID());
		
		Account account = new Account(user.getUserID());
		account.addFine(50, "Late return");
		
		assertThrows(UnsupportedOperationException.class, () -> {
			account.getFineTransactions().clear();
		});
	}
	
	@Test
	void givenMultipleFines_whenAdded_thenTotalFinesAccumulates() {
		
		User user = Mockito.mock(User.class);
		Mockito.when(user.getUserID()).thenReturn(UUID.randomUUID());
		
		Account account = new Account(user.getUserID());
		account.addFine(10, "Fine 1");
		account.addFine(20, "Fine 2");
		account.addFine(30, "Fine 3");
		
		assertEquals(60.0, account.getTotalFines());
		assertEquals(3, account.getFineTransactions().size());
	}
	
	@Test
	void givenMultiplePayments_whenMade_thenTotalFinesDecreasesCorrectly() {
		
		User user = Mockito.mock(User.class);
		Mockito.when(user.getUserID()).thenReturn(UUID.randomUUID());
		
		Account account = new Account(user.getUserID());
		account.addFine(100, "Large fine");
		account.payFine(20);
		account.payFine(30);
		account.payFine(25);
		
		assertEquals(25.0, account.getTotalFines());
		assertEquals(4, account.getFineTransactions().size());
	}
	
	@Test
	void givenAccount_whenSetStatus_thenStatusIsUpdated() {
		
		User user = Mockito.mock(User.class);
		Mockito.when(user.getUserID()).thenReturn(UUID.randomUUID());
		
		Account account = new Account(user.getUserID());
		assertEquals(AccountStatus.ACTIVE, account.getStatus());
		
		account.setStatus(AccountStatus.BLACKLISTED);
		
		assertEquals(AccountStatus.BLACKLISTED, account.getStatus());
	}
	
	@Test
	void givenAccount_whenToString_thenReturnsFormattedString() {
		
		User user = Mockito.mock(User.class);
		Mockito.when(user.getUserID()).thenReturn(UUID.randomUUID());
		
		Account account = new Account(user.getUserID());
		account.addFine(50.50, "Late return");
		
		String result = account.toString();
		
		assertNotNull(result);
		assertTrue(result.contains("Account"));
		assertTrue(result.contains("50.50"));
		assertTrue(result.contains("ACTIVE"));
	}
	
	@Test
	void givenExactThresholdAmount_whenAddFine_thenAccountRemainActive() {
		
		User user = Mockito.mock(User.class);
		Mockito.when(user.getUserID()).thenReturn(UUID.randomUUID());
		
		Account account = new Account(user.getUserID());
		account.addFine(100, "Threshold fine");
		
		assertEquals(100.0, account.getTotalFines());
		assertEquals(AccountStatus.ACTIVE, account.getStatus());
	}
	
	@Test
	void givenJustAboveThreshold_whenAddFine_thenAccountIsSuspended() {
		
		User user = Mockito.mock(User.class);
		Mockito.when(user.getUserID()).thenReturn(UUID.randomUUID());
		
		Account account = new Account(user.getUserID());
		account.addFine(100.01, "Just over threshold");
		
		assertEquals(100.01, account.getTotalFines());
		assertEquals(AccountStatus.SUSPENDED, account.getStatus());
	}
	
}
