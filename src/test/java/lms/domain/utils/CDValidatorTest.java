package lms.domain.utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CDValidatorTest {

	private CDValidator validator;

	@BeforeEach
	void setUp() {
		validator = new CDValidator();
	}

	@AfterEach
	void tearDown() {
		validator = null;
	}

	@Test
	void givenNullTitle_whenValidateTitle_thenThrowIllegalArgumentException() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateTitle(null);
		});
		assertEquals("CD title cannot be empty", exception.getMessage());
	}

	@Test
	void givenEmptyTitle_whenValidateTitle_thenThrowIllegalArgumentException() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateTitle("");
		});
		assertEquals("CD title cannot be empty", exception.getMessage());
	}

	@Test
	void givenBlankTitle_whenValidateTitle_thenThrowIllegalArgumentException() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateTitle("   ");
		});
		assertEquals("CD title cannot be empty", exception.getMessage());
	}

	@Test
	void givenValidTitle_whenValidateTitle_thenNoExceptionThrown() {
		assertDoesNotThrow(() -> {
			validator.validateTitle("Thriller");
		});
	}

	@Test
	void givenNullArtist_whenValidateArtist_thenThrowIllegalArgumentException() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateArtist(null);
		});
		assertEquals("CD artist cannot be empty", exception.getMessage());
	}

	@Test
	void givenEmptyArtist_whenValidateArtist_thenThrowIllegalArgumentException() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateArtist("");
		});
		assertEquals("CD artist cannot be empty", exception.getMessage());
	}

	@Test
	void givenBlankArtist_whenValidateArtist_thenThrowIllegalArgumentException() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateArtist("   ");
		});
		assertEquals("CD artist cannot be empty", exception.getMessage());
	}

	@Test
	void givenValidArtist_whenValidateArtist_thenNoExceptionThrown() {
		assertDoesNotThrow(() -> {
			validator.validateArtist("Michael Jackson");
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
			validator.validate("Thriller", "Michael Jackson", 3);
		});
	}

	@Test
	void givenInvalidTitle_whenValidate_thenThrowIllegalArgumentException() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validate("", "Michael Jackson", 1);
		});
		assertEquals("CD title cannot be empty", exception.getMessage());
	}

	@Test
	void givenInvalidArtist_whenValidate_thenThrowIllegalArgumentException() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validate("Thriller", "", 1);
		});
		assertEquals("CD artist cannot be empty", exception.getMessage());
	}

	@Test
	void givenInvalidCopies_whenValidate_thenThrowIllegalArgumentException() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validate("Thriller", "Michael Jackson", 0);
		});
		assertEquals("Total copies must be at least 1", exception.getMessage());
	}
}
