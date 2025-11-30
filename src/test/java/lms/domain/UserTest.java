package lms.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lms.application.UserDTO;
import lms.domain.exception.PasswordReuseException;
import lms.domain.utils.PasswordUtils;

class UserTest {

	User user;

	@BeforeEach
	void setUp() {
		user = new User("Majd", "Awwad", "majd@gmail.com", "majdawwad", PasswordUtils.hashPassword("StrongPass1!"),
				Role.ADMIN);
	}

	@Test
	void givenValidPassword_whenVerifyPassword_thenReturnTrue() {
		String validPassword = "StrongPass1!";
		assertTrue(user.verifyPassword(validPassword));
	}

	@Test
	void givenInvalidPassword_whenVerifyPassword_thenReturnFalse() {
		String invalidPassword = "invalidPassword!";
		assertFalse(user.verifyPassword(invalidPassword));
	}

	@Test

	void givenValidNewPassword_whenChangePassword_thenPasswordIsUpdated() {

		String validNewPassword = "this is a new Password";
		user.changePassword(validNewPassword);
		assertTrue(user.verifyPassword(validNewPassword));
	}

	@Test
	void givenOldPassword_whenChangePassword_thenThrowPasswordReuseException() {

		assertThrows(PasswordReuseException.class, () -> {
			String oldPassword = "StrongPass1!";
			user.changePassword(oldPassword);
		});
	}

	@Test
	void givenWeakPassword_whenChangePassword_thenThrowIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> {
			String newPassword = "week";
			user.changePassword(newPassword);
		});
	}

	@Test
	void givenNullPassword_whenChangePassword_thenThrowIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> {
			user.changePassword(null);
		});
	}

	@Test
	void givenNullRole_whenChangeRole_thenThrowIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> {
			user.changeRole(null);
		});
	}

	@Test

	void givenValidRole_whenChangeRole_thenRoleIsUpdated() {

		user.changeRole(Role.ADMIN);
		assertEquals(Role.ADMIN, user.getRole());
	}

	@Test
	void givenNullEmail_whenChangeEmail_thenTrowIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> {
			user.changeEmail(null);
		});
	}

	@Test
	void givenInvalidEmail_whenChangeEmail_thenTrowIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> {
			String invalidEmail = "email.com";
			user.changeEmail(invalidEmail);
		});
	}

	@Test
	void givenValidEmail_whenChangeEmail_thenEmailIsUpdated() {

		String validEmail = "mawwad223@gmail.com";
		user.changeEmail(validEmail);
		assertEquals(validEmail, user.getEmail());
	}

	@Test
	void givenUserConstructor_whenCreateUser_thenUserIsCreatedWithCorrectFields() {

		assertNotNull(user);
		assertEquals("Majd", user.getFirstName());
		assertEquals("Awwad", user.getLastName());
		assertEquals("majd@gmail.com", user.getEmail());
		assertEquals("majdawwad", user.getUsername());
		assertEquals(Role.ADMIN, user.getRole());
		assertNotNull(user.getUserID());
		assertNotNull(user.getRegistrationDate());
		assertNotNull(user.getAccount());
		assertNotNull(user.getLoans());
	}

	@Test
	void givenUserWithLoansAndAccount_whenCreateUserWithSecondConstructor_thenUserIsCreatedWithProvidedLoansAndAccount() {

		List<Loan> loans = new ArrayList<>();
		Account account = new Account(user.getUserID());
		User userWithLoans = new User("John", "Doe", "john@gmail.com", "johndoe",
				PasswordUtils.hashPassword("StrongPass1!"), Role.MEMBER, loans, account);

		assertNotNull(userWithLoans);
		assertEquals("John", userWithLoans.getFirstName());
		assertEquals(loans, userWithLoans.getLoans());
		assertEquals(account, userWithLoans.getAccount());
	}

	@Test
	void givenUser_whenToDTO_thenReturnUserDTOWithCorrectFields() {

		UserDTO dto = user.toDTO();

		assertNotNull(dto);
		assertEquals(user.getUserID(), dto.userID());
		assertEquals(user.getUsername(), dto.username());
		assertEquals(user.getFirstName(), dto.firstName());
		assertEquals(user.getLastName(), dto.lastName());
		assertEquals(user.getRole(), dto.role());
	}

	@Test
	void givenUserWithNoFines_whenHasFine_thenReturnFalse() {

		assertFalse(user.hasFine());
	}

	@Test
	void givenUserWithFines_whenHasFine_thenReturnTrue() {

		user.getAccount().addFine(10.0, "Late return");
		assertTrue(user.hasFine());
	}

	@Test
	void givenUserWithNoLoansAndNoFines_whenCanBorrow_thenReturnTrue() {

		assertTrue(user.canBorrow());
	}

	@Test
	void givenUserWithFines_whenCanBorrow_thenReturnFalse() {

		user.getAccount().addFine(10.0, "Late return");
		assertFalse(user.canBorrow());
	}

	@Test
	void givenUserWithMaxLoans_whenCanBorrow_thenReturnFalse() {

		for (int i = 0; i < 10; i++) {
			Loan loan = new Loan(user.getUserID(), UUID.randomUUID(), "book", LocalDate.now());
			user.addLoan(loan);
		}

		assertFalse(user.canBorrow());
	}

	@Test
	void givenLoan_whenAddLoan_thenLoanIsAddedToUserLoans() {

		Loan loan = new Loan(user.getUserID(), UUID.randomUUID(), "book", LocalDate.now());
		int initialSize = user.getLoans().size();

		user.addLoan(loan);

		assertEquals(initialSize + 1, user.getLoans().size());
		assertTrue(user.getLoans().contains(loan));
	}

	@Test
	void givenExistingLoan_whenRemoveLoan_thenLoanIsRemovedFromUserLoans() {

		Loan loan = new Loan(user.getUserID(), UUID.randomUUID(), "book", LocalDate.now());
		user.addLoan(loan);
		int sizeWithLoan = user.getLoans().size();

		user.removeLoan(loan);

		assertEquals(sizeWithLoan - 1, user.getLoans().size());
		assertFalse(user.getLoans().contains(loan));
	}

	@Test
	void givenNotification_whenAddNotification_thenNotificationIsAddedToUnreadList() {

		Notification notification = new Notification("Your book is due soon", user.getUserID(),
				NotificationType.DUE_SOON);
		int initialSize = user.getUnreadNotifications().size();

		user.addNotification(notification);

		assertEquals(initialSize + 1, user.getUnreadNotifications().size());
		assertTrue(user.getUnreadNotifications().contains(notification));
	}

	@Test
	void givenUnreadNotification_whenMarkAsRead_thenNotificationMovesToReadList() {

		Notification notification = new Notification("Your book is due soon", user.getUserID(),
				NotificationType.DUE_SOON);
		user.addNotification(notification);

		user.markAsRead(notification);

		assertFalse(user.getUnreadNotifications().contains(notification));
		assertTrue(user.getReadNotifications().contains(notification));
	}

	@Test
	void givenNotificationNotInUnreadList_whenMarkAsRead_thenReadListRemainsUnchanged() {

		Notification notification = new Notification("Your book is due soon", user.getUserID(),
				NotificationType.DUE_SOON);
		int initialReadSize = user.getReadNotifications().size();

		user.markAsRead(notification);

		assertEquals(initialReadSize, user.getReadNotifications().size());
	}

	@Test
	void givenNewName_whenUpdateName_thenNameIsUpdated() {
		user.updateName("Ahmad", "Ali");

		assertEquals("Ahmad", user.getFirstName());
		assertEquals("Ali", user.getLastName());
	}

	@Test
	void givenNullFirstName_whenUpdateName_thenThrowException() {
		assertThrows(IllegalArgumentException.class, () -> user.updateName(null, "Ali"));
	}

	@Test
	
	void givenNewUsername_whenSetUsername_thenUsernameIsUpdated() {

		String newUsername = "majdnew";
		user.setUsername(newUsername);

		assertEquals(newUsername, user.getUsername());
	}

	@Test
	void givenUser_whenGetUsername_thenReturnOriginalUsername() {
		assertEquals("majdawwad", user.getUsername());
	}

	@Test
	void givenNewEmail_whenChangeEmail_thenEmailIsUpdated() {
		user.changeEmail("newemail@gmail.com");
		assertEquals("newemail@gmail.com", user.getEmail());
	}

	@Test
	void givenInvalidEmail_whenChangeEmail_thenThrowException() {
		assertThrows(IllegalArgumentException.class, () -> user.changeEmail("invalid-email"));
	}

	@Test
	void givenUser_whenGetRegistrationDate_thenReturnCurrentDate() {

		LocalDate today = LocalDate.now();
		assertEquals(today, user.getRegistrationDate());
	}

	@Test
	void givenUser_whenGetUserID_thenReturnNonNullUUID() {

		assertNotNull(user.getUserID());
	}

	@Test
	void givenUser_whenGetAccount_thenReturnNonNullAccount() {

		assertNotNull(user.getAccount());
	}

	@Test
	void givenUser_whenGetLoans_thenReturnUnmodifiableList() {

		List<Loan> loans = user.getLoans();
		assertNotNull(loans);
		assertThrows(UnsupportedOperationException.class, () -> {
			loans.add(new Loan(user.getUserID(), UUID.randomUUID(), "book", LocalDate.now()));
		});
	}

	@Test
	void givenUser_whenGetUnreadNotifications_thenReturnUnmodifiableList() {

		List<Notification> unreadNotifications = user.getUnreadNotifications();
		assertNotNull(unreadNotifications);
		assertThrows(UnsupportedOperationException.class, () -> {
			unreadNotifications.add(new Notification("Test message", user.getUserID(), NotificationType.DUE_SOON));
		});
	}

	@Test
	void givenUser_whenGetReadNotifications_thenReturnUnmodifiableList() {

		List<Notification> readNotifications = user.getReadNotifications();
		assertNotNull(readNotifications);
		assertThrows(UnsupportedOperationException.class, () -> {
			readNotifications.add(new Notification("Test message", user.getUserID(), NotificationType.DUE_SOON));
		});
	}
}

