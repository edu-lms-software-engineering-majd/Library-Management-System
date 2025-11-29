package lms.domain.utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import lms.domain.Role;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

	private UserValidator validator;

	@BeforeEach
	void setUp() {
		validator = UserValidator.getInstance();
	}

	@AfterEach
	void tearDown() {
		validator = null;
	}

	@Test
	void givenNullFirstName_whenValidateFirstName_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateFirstName(null);
		});

		assertEquals("First name cannot be empty", exception.getMessage());
	}

	@Test
	void givenEmptyFirstName_whenValidateFirstName_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateFirstName("");
		});

		assertEquals("First name cannot be empty", exception.getMessage());
	}

	@Test
	void givenBlankFirstName_whenValidateFirstName_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateFirstName("   ");
		});

		assertEquals("First name cannot be empty", exception.getMessage());
	}

	@Test
	void givenValidFirstName_whenValidateFirstName_thenNoExceptionThrown() {

		assertDoesNotThrow(() -> {
			validator.validateFirstName("Majd");
		});
	}

	@Test
	void givenNullLastName_whenValidateLastName_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateLastName(null);
		});

		assertEquals("Last name cannot be empty", exception.getMessage());
	}

	@Test
	void givenEmptyLastName_whenValidateLastName_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateLastName("");
		});

		assertEquals("Last name cannot be empty", exception.getMessage());
	}

	@Test
	void givenBlankLastName_whenValidateLastName_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateLastName("   ");
		});

		assertEquals("Last name cannot be empty", exception.getMessage());
	}

	@Test
	void givenValidLastName_whenValidateLastName_thenNoExceptionThrown() {

		assertDoesNotThrow(() -> {
			validator.validateLastName("Awwad");
		});
	}

	@Test
	void givenNullEmail_whenValidateEmail_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateEmail(null);
		});

		assertEquals("Invalid email", exception.getMessage());
	}

	@Test
	void givenEmailWithoutAtSymbol_whenValidateEmail_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateEmail("invalidemail.com");
		});

		assertEquals("Invalid email", exception.getMessage());
	}

	@Test
	void givenEmptyEmail_whenValidateEmail_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateEmail("");
		});

		assertEquals("Invalid email", exception.getMessage());
	}

	@Test
	void givenValidEmail_whenValidateEmail_thenNoExceptionThrown() {

		assertDoesNotThrow(() -> {
			validator.validateEmail("majd@gmail.com");
		});
	}

	@Test
	void givenNullUsername_whenValidateUsername_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateUsername(null);
		});

		assertEquals("Username cannot be empty", exception.getMessage());
	}

	@Test
	void givenEmptyUsername_whenValidateUsername_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateUsername("");
		});

		assertEquals("Username cannot be empty", exception.getMessage());
	}

	@Test
	void givenBlankUsername_whenValidateUsername_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateUsername("   ");
		});

		assertEquals("Username cannot be empty", exception.getMessage());
	}

	@Test
	void givenValidUsername_whenValidateUsername_thenNoExceptionThrown() {

		assertDoesNotThrow(() -> {
			validator.validateUsername("majdawwad");
		});
	}

	@Test
	void givenNullHashedPassword_whenValidateHashedPassword_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateHashedPassword(null);
		});

		assertEquals("Hashed password cannot be empty", exception.getMessage());
	}

	@Test
	void givenEmptyHashedPassword_whenValidateHashedPassword_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateHashedPassword("");
		});

		assertEquals("Hashed password cannot be empty", exception.getMessage());
	}

	@Test
	void givenBlankHashedPassword_whenValidateHashedPassword_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateHashedPassword("   ");
		});

		assertEquals("Hashed password cannot be empty", exception.getMessage());
	}

	@Test
	void givenValidHashedPassword_whenValidateHashedPassword_thenNoExceptionThrown() {

		assertDoesNotThrow(() -> {
			validator.validateHashedPassword("this$is$hashed$password");
		});
	}

	@Test
	void givenNullRole_whenValidateRole_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateRole(null);
		});

		assertEquals("Role cannot be null", exception.getMessage());
	}

	@Test
	void givenValidRole_whenValidateRole_thenNoExceptionThrown() {

		assertDoesNotThrow(() -> {
			validator.validateRole(Role.MEMBER);
		});
	}

	@Test
	void givenValidAdminRole_whenValidateRole_thenNoExceptionThrown() {

		assertDoesNotThrow(() -> {
			validator.validateRole(Role.ADMIN);
		});
	}

	@Test
	void givenNullNewPassword_whenValidateNewPassword_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateNewPassword(null);
		});

		assertEquals("Password too weak.", exception.getMessage());
	}

	@Test
	void givenTooShortPassword_whenValidateNewPassword_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateNewPassword("short");
		});

		assertEquals("Password too weak.", exception.getMessage());
	}

	@Test
	void givenPasswordWith7Characters_whenValidateNewPassword_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateNewPassword("1234567");
		});

		assertEquals("Password too weak.", exception.getMessage());
	}

	@Test
	void givenPasswordWith8Characters_whenValidateNewPassword_thenNoExceptionThrown() {

		assertDoesNotThrow(() -> {
			validator.validateNewPassword("12345678");
		});
	}

	@Test
	void givenLongPassword_whenValidateNewPassword_thenNoExceptionThrown() {

		assertDoesNotThrow(() -> {
			validator.validateNewPassword("it's a very long password");
		});
	}

	@Test
	void givenAllValidFields_whenValidateUserFields_thenNoExceptionThrown() {

		assertDoesNotThrow(() -> {
			validator.validateUserFields("Majd", "Awwad", "majdawwad@gmail.com", "majdawwad",
					"this$is$hashed$password", Role.ADMIN);
		});
	}

	@Test
	void givenInvalidFirstName_whenValidateUserFields_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateUserFields(null, "Awwad", "majdawwad@gmail.com", "majdawwad",
					"this$is$hashed$password", Role.ADMIN);
		});

		assertEquals("First name cannot be empty", exception.getMessage());
	}

	@Test
	void givenInvalidLastName_whenValidateUserFields_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateUserFields("Majd", "", "majdawwad@gmail.com", "majdawwad",
					"this$is$hashed$password", Role.ADMIN);
		});

		assertEquals("Last name cannot be empty", exception.getMessage());
	}

	@Test
	void givenInvalidEmail_whenValidateUserFields_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateUserFields("Majd", "Awwad", "majdawwadgmailcom", "majdawwad",
					"this$is$hashed$password", Role.ADMIN);
		});

		assertEquals("Invalid email", exception.getMessage());
	}

	@Test
	void givenInvalidUsername_whenValidateUserFields_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateUserFields("Majd", "Awwad", "majdawwad@gmail.com", null,
					"this$is$hashed$password", Role.ADMIN);
		});

		assertEquals("Username cannot be empty", exception.getMessage());
	}

	@Test
	void givenInvalidHashedPassword_whenValidateUserFields_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateUserFields("Majd", "Awwad", "majdawwad@gmail.com", "majdawwad",
					null, Role.ADMIN);
		});

		assertEquals("Hashed password cannot be empty", exception.getMessage());
	}

	@Test
	void givenInvalidRole_whenValidateUserFields_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateUserFields("Majd", "Awwad", "majdawwad@gmail.com", "majdawwad",
					"this$is$hashed$password", null);
		});

		assertEquals("Role cannot be null", exception.getMessage());
	}
}