package lms.domain;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lms.domain.exception.PasswordReuseException;
import lms.domain.utils.PasswordUtils;

class UserTest {

	User user;

	@BeforeEach
	void setUp() {
		user = new User("Majd", "Awwad", "majd@gmail.com", "majdawwad", PasswordUtils.hashPassword("StrongPass1!"),
				Role.ADMIN);
	}

	@AfterEach
	void tearDown() {
		user = null;
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
	void givenNullRole_whenChangeRoll_thenThrowIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> {
			user.changeRole(null);
		});
	}

	@Test

	void givenValidRole_whenChangeRoll_thenRoleIsUpdated() {

		user.changeRole(Role.ADMIN);
		assertEquals(Role.ADMIN, user.getRole());
	}

	@Test
	void givinNullEmail_whenChangeEmail_thenTrowIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> {
			user.changeEmail(null);
		});
	}

	@Test
	void givinInvalidEmail_whenChangeEmail_thenTrowIllegalArgumentException() {

		assertThrows(IllegalArgumentException.class, () -> {
			String invalidEmail = "email.com";
			user.changeEmail(invalidEmail);
		});
	}

	@Test
	void givinValidEmail_whenChangeEmail_thenEmailIsUpdated() {

		String validEmail = "mawwad223@gmail.com";
		user.changeEmail(validEmail);
		assertEquals(validEmail, user.getEmail());
	}
}
