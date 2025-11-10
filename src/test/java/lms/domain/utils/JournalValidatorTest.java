package lms.domain.utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JournalValidatorTest {

	private JournalValidator validator;

	@BeforeEach
	void setUp() {
		validator = JournalValidator.getInstance();
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	void givenNullTitle_whenValidateTitle_thenThrowIllegalArgumentException() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateTitle(null);
		});
		assertEquals("Journal title cannot be empty", exception.getMessage());
	}

	@Test
	void givenEmptyTitle_whenValidateTitle_thenThrowIllegalArgumentException() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateTitle("");
		});
		assertEquals("Journal title cannot be empty", exception.getMessage());
	}

	@Test
	void givenBlankTitle_whenValidateTitle_thenThrowIllegalArgumentException() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateTitle("   ");
		});
		assertEquals("Journal title cannot be empty", exception.getMessage());
	}

	@Test
	void givenValidTitle_whenValidateTitle_thenNoExceptionThrown() {
		assertDoesNotThrow(() -> {
			validator.validateTitle("Nature");
		});
	}

	@Test
	void givenNullAuthor_whenValidateAuthor_thenThrowIllegalArgumentException() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateAuthor(null);
		});
		assertEquals("Journal author cannot be empty", exception.getMessage());
	}

	@Test
	void givenEmptyAuthor_whenValidateAuthor_thenThrowIllegalArgumentException() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateAuthor("");
		});
		assertEquals("Journal author cannot be empty", exception.getMessage());
	}

	@Test
	void givenBlankAuthor_whenValidateAuthor_thenThrowIllegalArgumentException() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateAuthor("   ");
		});
		assertEquals("Journal author cannot be empty", exception.getMessage());
	}

	@Test
	void givenValidAuthor_whenValidateAuthor_thenNoExceptionThrown() {
		assertDoesNotThrow(() -> {
			validator.validateAuthor("Springer Nature");
		});
	}

	@Test
	void givenZeroCopies_whenValidateTotalCopies_thenThrowIllegalArgumentException() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateTotalCopies(0);
		});
		assertEquals("Total copies must be at least 1", exception.getMessage());
	}

	@Test
	void givenNegativeCopies_whenValidateTotalCopies_thenThrowIllegalArgumentException() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateTotalCopies(-1);
		});
		assertEquals("Total copies must be at least 1", exception.getMessage());
	}

	@Test
	void givenValidCopies_whenValidateTotalCopies_thenNoExceptionThrown() {
		assertDoesNotThrow(() -> {
			validator.validateTotalCopies(1);
		});
		assertDoesNotThrow(() -> {
			validator.validateTotalCopies(5);
		});
	}

	@Test
	void givenAllValidFields_whenValidate_thenNoExceptionThrown() {
		assertDoesNotThrow(() -> {
			validator.validate("Nature", "Springer Nature", 5);
		});
	}

	@Test
	void givenInvalidTitle_whenValidate_thenThrowIllegalArgumentException() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validate("", "Springer Nature", 1);
		});
		assertEquals("Journal title cannot be empty", exception.getMessage());
	}

	@Test
	void givenInvalidAuthor_whenValidate_thenThrowIllegalArgumentException() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validate("Nature", "", 1);
		});
		assertEquals("Journal author cannot be empty", exception.getMessage());
	}

	@Test
	void givenInvalidCopies_whenValidate_thenThrowIllegalArgumentException() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validate("Nature", "Springer Nature", 0);
		});
		assertEquals("Total copies must be at least 1", exception.getMessage());
	}
}
