package lms.domain;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lms.domain.Role;
import lms.domain.User;
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
		String validNewPassword = "ThisIsNewPass123!";
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
			String newPassword = "weak";
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
		user.changeRole(Role.MEMBER);
		assertEquals(Role.MEMBER, user.getRole());
	}

	@Test
	void givenNullEmail_whenChangeEmail_thenThrowIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> {
			user.changeEmail(null);
		});
	}

	@Test
	void givenInvalidEmail_whenChangeEmail_thenThrowIllegalArgumentException() {
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
}
