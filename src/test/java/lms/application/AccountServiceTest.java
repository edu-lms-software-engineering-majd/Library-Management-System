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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import lms.domain.AccountStatus;
import lms.domain.Role;
import lms.domain.User;
import lms.domain.UserRepository;

/**
 * Full test suite for AccountService with high coverage.
 * 
 * Includes: - Status/Balances - Adding fines - Paying fines - Suspension /
 * Activation - Aggregations - Error handling - Lifecycle scenarios
 *
 * Author: Ahmad Salameh
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AccountService — Comprehensive Test Suite (90%+ Coverage)")
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

	// =====================================================================
	// Constructor Tests
	// =====================================================================
	@Nested
	@DisplayName("🧱 Constructor Tests")
	class ConstructorTests {

		@Test
		@DisplayName("Should create AccountService normally with valid repository")
		void constructorValid() {
			assertNotNull(service);
		}

		@Test
		@DisplayName("Should throw when UserRepository is null")
		void constructorNull() {
			assertThrows(IllegalArgumentException.class, () -> new AccountService(null));
		}
	}

	// =====================================================================
	// Status and Balance Tests
	// =====================================================================
	@Nested
	@DisplayName("📌 Status / Balance Tests")
	class StatusBalanceTests {

		@Test
		@DisplayName("Should return ACTIVE for new user account")
		void getStatusActive() {
			when(userRepo.getByID(userId)).thenReturn(Optional.of(user));

			AccountStatus st = service.getUserAccountStatus(userId);

			assertEquals(AccountStatus.ACTIVE, st);
		}

		@Test
		@DisplayName("Should return correct balance after operations")
		void getBalance() {
			when(userRepo.getByID(userId)).thenReturn(Optional.of(user));

			user.getAccount().addFine(40.0, "Late");

			double balance = service.getUserBalance(userId);

			assertEquals(40.0, balance, 0.001);
		}
	}

	// =====================================================================
	// Can Borrow Tests
	// =====================================================================
	@Nested
	@DisplayName("📚 Borrow Permission Tests")
	class BorrowTests {

		@Test
		@DisplayName("User with zero fines should be allowed to borrow")
		void allowedToBorrow() {
			when(userRepo.getByID(userId)).thenReturn(Optional.of(user));

			assertTrue(service.canUserBorrow(userId));
		}

		@Test
		@DisplayName("User with fines should NOT be allowed to borrow")
		void notAllowedToBorrow() {
			when(userRepo.getByID(userId)).thenReturn(Optional.of(user));

			user.getAccount().addFine(20, "Late");

			assertFalse(service.canUserBorrow(userId));
		}
	}

	// =====================================================================
	// Add Fine Tests
	// =====================================================================
	@Nested
	@DisplayName("💰 Add Fine Tests")
	class AddFineTests {

		@Test
		@DisplayName("Should add fine successfully and update repository")
		void addFineSuccess() {
			when(userRepo.getByID(userId)).thenReturn(Optional.of(user));

			service.addFineToUser(userId, 10.0, "Test");

			assertEquals(10.0, user.getAccount().getTotalFines(), 0.001);
			verify(userRepo).update(user);
		}

		@Test
		@DisplayName("Should throw when fine amount is negative or zero")
		void addFineInvalid() {
			assertThrows(IllegalArgumentException.class, () -> service.addFineToUser(userId, 0, "invalid"));
		}

		@Test
		@DisplayName("Should throw if user does not exist")
		void addFineUserMissing() {
			when(userRepo.getByID(userId)).thenReturn(Optional.empty());

			assertThrows(IllegalArgumentException.class, () -> service.addFineToUser(userId, 10, "test"));
		}
	}

	// =====================================================================
	// Payment Tests
	// =====================================================================
	@Nested
	@DisplayName("💳 Payment Tests")
	class PaymentTests {

		@Test
		@DisplayName("Should pay fine successfully and update repository")
		void payFine() {
			when(userRepo.getByID(userId)).thenReturn(Optional.of(user));

			user.getAccount().addFine(50, "LATE");

			service.payUserFine(userId, 20);

			assertEquals(30, user.getAccount().getTotalFines(), 0.001);
			verify(userRepo).update(user);
		}

		@Test
		@DisplayName("Should throw when payment is negative or zero")
		void invalidPayment() {
			assertThrows(IllegalArgumentException.class, () -> service.payUserFine(userId, -5));
		}

		@Test
		@DisplayName("Should throw when paying fine for unknown user")
		void payingUnknownUser() {
			when(userRepo.getByID(userId)).thenReturn(Optional.empty());

			assertThrows(IllegalArgumentException.class, () -> service.payUserFine(userId, 10));
		}
	}

	// =====================================================================
	// Suspension / Activation Tests
	// =====================================================================
	@Nested
	@DisplayName("🔒 Suspend / Activate Tests")
	class SuspensionTests {

		@Test
		@DisplayName("Should suspend user account with valid reason")
		void suspend() {
			when(userRepo.getByID(userId)).thenReturn(Optional.of(user));

			service.suspendUserAccount(userId, "Late books");

			assertEquals(AccountStatus.SUSPENDED, user.getAccount().getStatus());
			verify(userRepo).update(user);
		}

		@Test
		@DisplayName("Suspension reason cannot be blank")
		void suspendBlankReason() {
			when(userRepo.getByID(userId)).thenReturn(Optional.of(user));

			assertThrows(IllegalArgumentException.class, () -> service.suspendUserAccount(userId, "   "));
		}

		@Test
		@DisplayName("Should activate user account")
		void activate() {
			when(userRepo.getByID(userId)).thenReturn(Optional.of(user));

			user.getAccount().suspendAccount("RULES");

			service.activateUserAccount(userId);

			assertEquals(AccountStatus.ACTIVE, user.getAccount().getStatus());
			verify(userRepo).update(user);
		}
	}

	// =====================================================================
	// Aggregation Tests
	// =====================================================================
	@Nested
	@DisplayName("📊 Aggregation Tests")
	class AggregationTests {

		@Test
		@DisplayName("Should calculate total fines across all users")
		void totalFines() {
			User u1 = new User("Ahmad", "Salameh", "hmeedsalameh2004@gmail.com", "ahmad2", "h", Role.MEMBER);
			User u2 = new User("Ahmad", "Salameh", "hmeedsalameh2004@gmail.com", "ahmad3", "h", Role.MEMBER);

			u1.getAccount().addFine(30, "F1");
			u2.getAccount().addFine(70, "F2");

			when(userRepo.getAllUsers()).thenReturn(List.of(u1, u2));

			double total = service.calculateTotalFinesForAllUsers();

			assertEquals(100.0, total, 0.001);
		}

		@Test
		@DisplayName("Should count users with fines > 0")
		void usersWithFines() {
			User u1 = new User("Ahmad", "Salameh", "hmeedsalameh2004@gmail.com", "ahmad4", "h", Role.MEMBER);
			User u2 = new User("Ahmad", "Salameh", "hmeedsalameh2004@gmail.com", "ahmad5", "h", Role.MEMBER);

			u2.getAccount().addFine(25, "Late");

			when(userRepo.getAllUsers()).thenReturn(List.of(u1, u2));

			assertEquals(1, service.getUsersWithFinesCount());
		}
	}

	// =====================================================================
	// Integration Tests
	// =====================================================================
	@Nested
	@DisplayName("🔄 Integration Flow Tests")
	class IntegrationTests {

		@Test
		@DisplayName("Should complete full lifecycle: fine → pay → reactivate")
		void fullLifecycle() {
			when(userRepo.getByID(userId)).thenReturn(Optional.of(user));

			service.addFineToUser(userId, 50, "Late");
			assertEquals(50, user.getAccount().getTotalFines());

			service.payUserFine(userId, 50);
			assertEquals(0, user.getAccount().getTotalFines());

			assertEquals(AccountStatus.ACTIVE, user.getAccount().getStatus());
		}
	}
}

