package lms.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import lms.domain.AccountStatus;
import lms.domain.Role;
import lms.domain.User;
import lms.domain.UserRepository;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

	@Mock
	private UserRepository userRepo;

	private AccountService service;
	private User user;
	private UUID userId;

	@BeforeEach
	void setup() {
		service = new AccountService(userRepo);
		userId = UUID.randomUUID();
		user = new User("Ahmad", "Salameh", "hmeedsalameh2004@gmail.com", "ahmad", "hashed12345", Role.MEMBER);
	}

	@AfterEach
	void clean() {
		reset(userRepo);
	}

	 
	@Test
	void givenValidRepository_whenCreateService_thenInstanceCreated() {
		assertNotNull(service);
	}

	@Test
	void givenNullRepository_whenCreateService_thenThrowException() {
		assertThrows(IllegalArgumentException.class, () -> new AccountService(null));
	}

	 
	@Test
	void givenExistingUser_whenGetStatus_thenReturnActive() {
		when(userRepo.getByID(userId)).thenReturn(Optional.of(user));

		AccountStatus status = service.getUserAccountStatus(userId);

		assertEquals(AccountStatus.ACTIVE, status);
	}

	@Test
	void givenExistingUserWithFines_whenGetBalance_thenReturnCorrectValue() {
		when(userRepo.getByID(userId)).thenReturn(Optional.of(user));
		user.getAccount().addFine(40.0, "Late");

		double balance = service.getUserBalance(userId);

		assertEquals(40.0, balance, 0.001);
	}
 

	@Test
	void givenUserWithNoFines_whenCanBorrow_thenReturnTrue() {
		when(userRepo.getByID(userId)).thenReturn(Optional.of(user));

		assertTrue(service.canUserBorrow(userId));
	}

	@Test
	void givenUserWithFines_whenCanBorrow_thenReturnFalse() {
		when(userRepo.getByID(userId)).thenReturn(Optional.of(user));
		user.getAccount().addFine(20.0, "Late");

		assertFalse(service.canUserBorrow(userId));
	}

	 

	@Test
	void givenValidUserAndAmount_whenAddFine_thenFineAddedAndUserUpdated() {
		when(userRepo.getByID(userId)).thenReturn(Optional.of(user));

		service.addFineToUser(userId, 10.0, "Test");

		assertEquals(10.0, user.getAccount().getTotalFines(), 0.001);
		verify(userRepo).update(user);
	}

	@Test
	void givenNonPositiveAmount_whenAddFine_thenThrowException() {
		assertThrows(IllegalArgumentException.class, () -> service.addFineToUser(userId, 0.0, "invalid"));
		assertThrows(IllegalArgumentException.class, () -> service.addFineToUser(userId, -5.0, "invalid"));
	}

	@Test
	void givenUnknownUser_whenAddFine_thenThrowException() {
		when(userRepo.getByID(userId)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () -> service.addFineToUser(userId, 10.0, "test"));
	}

	 

	@Test
	void givenValidUserAndPayment_whenPayFine_thenFineReducedAndUserUpdated() {
		when(userRepo.getByID(userId)).thenReturn(Optional.of(user));
		user.getAccount().addFine(50.0, "Late");

		service.payUserFine(userId, 20.0);

		assertEquals(30.0, user.getAccount().getTotalFines(), 0.001);
		verify(userRepo).update(user);
	}

	@Test
	void givenNonPositivePayment_whenPayFine_thenThrowException() {
		assertThrows(IllegalArgumentException.class, () -> service.payUserFine(userId, 0.0));
		assertThrows(IllegalArgumentException.class, () -> service.payUserFine(userId, -5.0));
	}

	@Test
	void givenUnknownUser_whenPayFine_thenThrowException() {
		when(userRepo.getByID(userId)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () -> service.payUserFine(userId, 10.0));
	}

	 

	@Test
	void givenExistingUser_whenSuspendAccount_thenStatusBecomesSuspendedAndUserUpdated() {
		when(userRepo.getByID(userId)).thenReturn(Optional.of(user));

		service.suspendUserAccount(userId, "Late books");

		assertEquals(AccountStatus.SUSPENDED, user.getAccount().getStatus());
		verify(userRepo).update(user);
	}

	@Test
	void givenBlankReason_whenSuspendAccount_thenThrowException() {
		when(userRepo.getByID(userId)).thenReturn(Optional.of(user));

		assertThrows(IllegalArgumentException.class, () -> service.suspendUserAccount(userId, "   "));
	}

	@Test
	void givenSuspendedUser_whenActivateAccount_thenStatusBecomesActiveAndUserUpdated() {
		when(userRepo.getByID(userId)).thenReturn(Optional.of(user));
		user.getAccount().suspendAccount("Rules");

		service.activateUserAccount(userId);

		assertEquals(AccountStatus.ACTIVE, user.getAccount().getStatus());
		verify(userRepo).update(user);
	}

	 
	@Test
	void givenUsersWithFines_whenCalculateTotalFines_thenReturnCorrectSum() {
		User u1 = new User("Ahmad", "Salameh", "hmeedsalameh2004@gmail.com", "ahmad2", "h", Role.MEMBER);
		User u2 = new User("Ahmad", "Salameh", "hmeedsalameh2004@gmail.com", "ahmad3", "h", Role.MEMBER);

		u1.getAccount().addFine(30.0, "F1");
		u2.getAccount().addFine(70.0, "F2");

		when(userRepo.getAllUsers()).thenReturn(List.of(u1, u2));

		double total = service.calculateTotalFinesForAllUsers();

		assertEquals(100.0, total, 0.001);
	}

	@Test
	void givenMixedUsers_whenGetUsersWithFinesCount_thenReturnOnlyUsersWithPositiveFines() {
		User u1 = new User("Ahmad", "Salameh", "hmeedsalameh2004@gmail.com", "ahmad4", "h", Role.MEMBER);
		User u2 = new User("Ahmad", "Salameh", "hmeedsalameh2004@gmail.com", "ahmad5", "h", Role.MEMBER);

		u2.getAccount().addFine(25.0, "Late");

		when(userRepo.getAllUsers()).thenReturn(List.of(u1, u2));

		assertEquals(1, service.getUsersWithFinesCount());
	}

	 

	@Test
	void givenUser_whenAddFineAndPayAll_thenBalanceZeroAndStatusActive() {
		when(userRepo.getByID(userId)).thenReturn(Optional.of(user));

		service.addFineToUser(userId, 50.0, "Late");
		assertEquals(50.0, user.getAccount().getTotalFines(), 0.001);

		service.payUserFine(userId, 50.0);
		assertEquals(0.0, user.getAccount().getTotalFines(), 0.001);
		assertEquals(AccountStatus.ACTIVE, user.getAccount().getStatus());
	}
}
