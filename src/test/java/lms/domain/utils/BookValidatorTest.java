package lms.domain.utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Year;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BookValidatorTest {

	private BookValidator validator;

	@BeforeEach
	void setUp() {
		validator = new BookValidator();
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

		assertEquals("Book title cannot be empty", exception.getMessage());
	}

	@Test
	void givenEmptyTitle_whenValidateTitle_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateTitle("");
		});

		assertEquals("Book title cannot be empty", exception.getMessage());
	}

	@Test
	void givenBlankTitle_whenValidateTitle_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateTitle("   ");
		});

		assertEquals("Book title cannot be empty", exception.getMessage());
	}

	@Test
	void givenValidTitle_whenValidateTitle_thenNoExceptionThrown() {

		assertDoesNotThrow(() -> {
			validator.validateTitle("Clean Code");
		});
	}

	@Test
	void givenNullAuthor_whenValidateAuthor_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateAuthor(null);
		});

		assertEquals("Book Auther cannot be empty", exception.getMessage());
	}

	@Test
	void givenEmptyAuthor_whenValidateAuthor_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateAuthor("");
		});

		assertEquals("Book Auther cannot be empty", exception.getMessage());
	}

	@Test
	void givenBlankAuthor_whenValidateAuthor_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateAuthor("   ");
		});

		assertEquals("Book Auther cannot be empty", exception.getMessage());
	}

	@Test
	void givenValidAuthor_whenValidateAuthor_thenNoExceptionThrown() {

		assertDoesNotThrow(() -> {
			validator.validateAuthor("Robert Martin");
		});
	}

	@Test
	void givenNullIsbn_whenValidateIsbn_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateIsbn(null);
		});

		assertEquals("Book ISBN cannot be empty", exception.getMessage());
	}

	@Test
	void givenEmptyIsbn_whenValidateIsbn_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateIsbn("");
		});

		assertEquals("Book ISBN cannot be empty", exception.getMessage());
	}

	@Test
	void givenBlankIsbn_whenValidateIsbn_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateIsbn("   ");
		});

		assertEquals("Book ISBN cannot be empty", exception.getMessage());
	}

	@Test
	void givenValidIsbn_whenValidateIsbn_thenNoExceptionThrown() {

		assertDoesNotThrow(() -> {
			validator.validateIsbn("978-0134685991");
		});
	}

	@Test
	void givenNullPublisher_whenValidatePublisher_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validatePublisher(null);
		});

		assertEquals("Book publisher cannot be empty", exception.getMessage());
	}

	@Test
	void givenEmptyPublisher_whenValidatePublisher_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validatePublisher("");
		});

		assertEquals("Book publisher cannot be empty", exception.getMessage());
	}

	@Test
	void givenBlankPublisher_whenValidatePublisher_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validatePublisher("   ");
		});

		assertEquals("Book publisher cannot be empty", exception.getMessage());
	}

	@Test
	void givenValidPublisher_whenValidatePublisher_thenNoExceptionThrown() {

		assertDoesNotThrow(() -> {
			validator.validatePublisher("Pearson");
		});
	}

	@Test
	void givenFutureYear_whenValidatePublicationYear_thenThrowIllegalArgumentException() {

		int futureYear = java.time.Year.now().getValue() + 1;

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validatePublicationYear(futureYear);
		});

		assertEquals("Publication year cannot be in the future", exception.getMessage());
	}

	@Test
	void givenCurrentYear_whenValidatePublicationYear_thenNoExceptionThrown() {

		int currentYear = java.time.Year.now().getValue();

		assertDoesNotThrow(() -> {
			validator.validatePublicationYear(currentYear);
		});
	}

	@Test
	void givenPastYear_whenValidatePublicationYear_thenNoExceptionThrown() {

		assertDoesNotThrow(() -> {
			validator.validatePublicationYear(2020);
		});
	}

	@Test
	void givenVeryOldYear_whenValidatePublicationYear_thenNoExceptionThrown() {

		assertDoesNotThrow(() -> {
			validator.validatePublicationYear(1900);
		});
	}

	@Test
	void givenNullCategory_whenValidateCategory_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateCategory(null);
		});

		assertEquals("Book category cannot be empty", exception.getMessage());
	}

	@Test
	void givenEmptyCategory_whenValidateCategory_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateCategory("");
		});

		assertEquals("Book category cannot be empty", exception.getMessage());
	}

	@Test
	void givenBlankCategory_whenValidateCategory_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateCategory("   ");
		});

		assertEquals("Book category cannot be empty", exception.getMessage());
	}

	@Test
	void givenValidCategory_whenValidateCategory_thenNoExceptionThrown() {

		assertDoesNotThrow(() -> {
			validator.validateCategory("Programming");
		});
	}

	@Test
	void givenNegativeTotalCopies_whenValidateTotalCopies_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateTotalCopies(-1);
		});

		assertEquals("Total copies cannot be negative", exception.getMessage());
	}

	@Test
	void givenZeroTotalCopies_whenValidateTotalCopies_thenNoExceptionThrown() {

		assertDoesNotThrow(() -> {
			validator.validateTotalCopies(0);
		});
	}

	@Test
	void givenPositiveTotalCopies_whenValidateTotalCopies_thenNoExceptionThrown() {

		assertDoesNotThrow(() -> {
			validator.validateTotalCopies(5);
		});
	}

	@Test
	void givenNullLanguage_whenValidateLanguage_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateLanguage(null);
		});

		assertEquals("Book language cannot be empty", exception.getMessage());
	}

	@Test
	void givenEmptyLanguage_whenValidateLanguage_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateLanguage("");
		});

		assertEquals("Book language cannot be empty", exception.getMessage());
	}

	@Test
	void givenBlankLanguage_whenValidateLanguage_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateLanguage("   ");
		});

		assertEquals("Book language cannot be empty", exception.getMessage());
	}

	@Test
	void givenValidLanguage_whenValidateLanguage_thenNoExceptionThrown() {

		assertDoesNotThrow(() -> {
			validator.validateLanguage("English");
		});
	}

	@Test
	void givenNullShelfLocation_whenValidateShelfLocation_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateShelfLocation(null);
		});

		assertEquals("Book shelf location cannot be empty", exception.getMessage());
	}

	@Test
	void givenEmptyShelfLocation_whenValidateShelfLocation_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateShelfLocation("");
		});

		assertEquals("Book shelf location cannot be empty", exception.getMessage());
	}

	@Test
	void givenBlankShelfLocation_whenValidateShelfLocation_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validateShelfLocation("   ");
		});

		assertEquals("Book shelf location cannot be empty", exception.getMessage());
	}

	@Test
	void givenValidShelfLocation_whenValidateShelfLocation_thenNoExceptionThrown() {

		assertDoesNotThrow(() -> {
			validator.validateShelfLocation("A1-101");
		});
	}

	@Test
	void givenAllValidFields_whenValidate_thenNoExceptionThrown() {

		assertDoesNotThrow(() -> {
			validator.validate("Clean Code", "Robert Martin", "978-0134685991", "Pearson", 2020, "Programming", 5,
					"English", "A1-101");
		});
	}

	@Test
	void givenInvalidTitle_whenValidate_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validate(null, "Author", "ISBN", "Publisher", 2020, "Category", 5, "English", "A1");
		});

		assertEquals("Book title cannot be empty", exception.getMessage());
	}

	@Test
	void givenInvalidAuthor_whenValidate_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validate("Title", "", "ISBN", "Publisher", 2020, "Category", 5, "English", "A1");
		});

		assertEquals("Book Auther cannot be empty", exception.getMessage());
	}

	@Test
	void givenInvalidPublicationYear_whenValidate_thenThrowIllegalArgumentException() {

		int futureYear = Year.now().getValue() + 1;

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validate("Title", "Author", "ISBN", "Publisher", futureYear, "Category", 5, "English", "A1");
		});

		assertEquals("Publication year cannot be in the future", exception.getMessage());
	}

	@Test
	void givenInvalidTotalCopies_whenValidate_thenThrowIllegalArgumentException() {

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			validator.validate("Title", "Author", "ISBN", "Publisher", 2020, "Category", -5, "English", "A1");
		});

		assertEquals("Total copies cannot be negative", exception.getMessage());
	}
}
