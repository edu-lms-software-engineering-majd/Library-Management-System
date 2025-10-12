import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lms.domain.Book;

class BookTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test

	void testValidBookCreation() {
		Book book = new Book("Java", "Author", "123", "Pub", 2020, "Programming", 5, "English", "A1");

		assertNotNull(book.getBookId());
		assertEquals("Java", book.getTitle());
		assertEquals("Author", book.getAuthor());
		assertEquals("123", book.getIsbn());
		assertEquals("Pub", book.getPublisher());
		assertEquals(2020, book.getPublicationYear());
		assertEquals("Programming", book.getCategory());
		assertEquals(5, book.getTotalCopies());
		assertEquals(5, book.getAvailableCopies());
		assertEquals("English", book.getLanguage());
		assertEquals("A1", book.getShelfLocation());
		assertNull(book.getDescription());
	}

	@Test

	void testBookWithDescriptionConstructor() {
		Book book = new Book("Java", "Author", "123", "Pub", 2020, "Programming", 5, "English", "A good book", "A1");
		assertEquals("A good book", book.getDescription());
	}

	@Test

	void testSettersAndGetters() {
		Book b = new Book("Java", "A", "123", "Pub", 2020, "Programming", 5, "English", "A1");

		b.setTitle("New Title");
		b.setAuthor("New Author");
		b.setIsbn("999");
		b.setPublisher("NewPub");
		b.setPublicationYear(2019);
		b.setCategory("Education");
		b.setTotalCopies(10);
		b.setAvailableCopies(8);
		b.setLanguage("Arabic");
		b.setDescription("Updated Description");
		b.setShelfLocation("B2");

		assertEquals("New Title", b.getTitle());
		assertEquals("New Author", b.getAuthor());
		assertEquals("999", b.getIsbn());
		assertEquals("NewPub", b.getPublisher());
		assertEquals(2019, b.getPublicationYear());
		assertEquals("Education", b.getCategory());
		assertEquals(10, b.getTotalCopies());
		assertEquals(8, b.getAvailableCopies());
		assertEquals("Arabic", b.getLanguage());
		assertEquals("Updated Description", b.getDescription());
		assertEquals("B2", b.getShelfLocation());
	}

	@Test
	void testInvalidBookInputs() {
		int currentYear = java.time.Year.now().getValue();

		assertAll(
				// 1-test empty **
				() -> assertThrows(IllegalArgumentException.class,
						() -> new Book("", "Author", "123", "Pub", 2020, "Programming", 5, "English", "A1"),
						"Should throw for empty title"),

				() -> assertThrows(IllegalArgumentException.class,
						() -> new Book("Java", "", "123", "Pub", 2020, "Programming", 5, "English", "A1"),
						"Should throw for empty author"),

				() -> assertThrows(IllegalArgumentException.class,
						() -> new Book("Java", "Author", "", "Pub", 2020, "Programming", 5, "English", "A1"),
						"Should throw for empty ISBN"),

				() -> assertThrows(IllegalArgumentException.class,
						() -> new Book("Java", "Author", "123", "", 2020, "Programming", 5, "English", "A1"),
						"Should throw for empty publisher"),

				() -> assertThrows(IllegalArgumentException.class,
						() -> new Book("Java", "Author", "123", "Pub", 2020, "", 5, "English", "A1"),
						"Should throw for empty category"),

				() -> assertThrows(IllegalArgumentException.class,
						() -> new Book("Java", "Author", "123", "Pub", 2020, "Programming", 5, "", "A1"),
						"Should throw for empty language"),

				() -> assertThrows(IllegalArgumentException.class,
						() -> new Book("Java", "Author", "123", "Pub", 2020, "Programming", 5, "English", ""),
						"Should throw for empty shelf location"),

				// 2-Test null **
				() -> assertThrows(IllegalArgumentException.class,
						() -> new Book(null, "Author", "123", "Pub", 2020, "Programming", 5, "English", "A1"),
						"Should throw for null title"),

				() -> assertThrows(IllegalArgumentException.class,
						() -> new Book("Java", null, "123", "Pub", 2020, "Programming", 5, "English", "A1"),
						"Should throw for null author"),

				() -> assertThrows(IllegalArgumentException.class,
						() -> new Book("Java", "Author", null, "Pub", 2020, "Programming", 5, "English", "A1"),
						"Should throw for null ISBN"),

				() -> assertThrows(IllegalArgumentException.class,
						() -> new Book("Java", "Author", "123", null, 2020, "Programming", 5, "English", "A1"),
						"Should throw for null publisher"),

				() -> assertThrows(IllegalArgumentException.class,
						() -> new Book("Java", "Author", "123", "Pub", 2020, null, 5, "English", "A1"),
						"Should throw for null category"),

				() -> assertThrows(IllegalArgumentException.class,
						() -> new Book("Java", "Author", "123", "Pub", 2020, "Programming", 5, null, "A1"),
						"Should throw for null language"),

				() -> assertThrows(IllegalArgumentException.class,
						() -> new Book("Java", "Author", "123", "Pub", 2020, "Programming", 5, "English", null),
						"Should throw for null shelf location"),

				// 3-test whitespace-only strings
				() -> assertThrows(IllegalArgumentException.class,
						() -> new Book("   ", "Author", "123", "Pub", 2020, "Programming", 5, "English", "A1"),
						"Should throw for whitespace-only title"),

				() -> assertThrows(IllegalArgumentException.class,
						() -> new Book("Java", "   ", "123", "Pub", 2020, "Programming", 5, "English", "A1"),
						"Should throw for whitespace-only author"),

				() -> assertThrows(IllegalArgumentException.class,
						() -> new Book("Java", "Author", "   ", "Pub", 2020, "Programming", 5, "English", "A1"),
						"Should throw for whitespace-only ISBN"),

				() -> assertThrows(IllegalArgumentException.class,
						() -> new Book("Java", "Author", "123", "   ", 2020, "Programming", 5, "English", "A1"),
						"Should throw for whitespace-only publisher"),

				() -> assertThrows(IllegalArgumentException.class,
						() -> new Book("Java", "Author", "123", "Pub", 2020, "   ", 5, "English", "A1"),
						"Should throw for whitespace-only category"),

				() -> assertThrows(IllegalArgumentException.class,
						() -> new Book("Java", "Author", "123", "Pub", 2020, "Programming", 5, "   ", "A1"),
						"Should throw for whitespace-only language"),

				() -> assertThrows(IllegalArgumentException.class,
						() -> new Book("Java", "Author", "123", "Pub", 2020, "Programming", 5, "English", "   "),
						"Should throw for whitespace-only shelf location"),

				// 4- test future publication year
				() -> assertThrows(IllegalArgumentException.class,
						() -> new Book("Java", "Author", "123", "Pub", currentYear + 1, "Programming", 5, "English",
								"A1"),
						"Should throw for future publication year"),

				() -> assertThrows(IllegalArgumentException.class,
						() -> new Book("Java", "Author", "123", "Pub", currentYear + 5, "Programming", 5, "English",
								"A1"),
						"Should throw for publication year 5 years in future"),

				// 5-test negative total copies
				() -> assertThrows(IllegalArgumentException.class,
						() -> new Book("Java", "Author", "123", "Pub", 2020, "Programming", -1, "English", "A1"),
						"Should throw for negative total copies (-1)"),

				() -> assertThrows(IllegalArgumentException.class,
						() -> new Book("Java", "Author", "123", "Pub", 2020, "Programming", -10, "English", "A1"),
						"Should throw for negative total copies (-10)"),

				() -> assertThrows(IllegalArgumentException.class,
						() -> new Book("Java", "Author", "123", "Pub", 2020, "Programming", Integer.MIN_VALUE,
								"English", "A1"),
						"Should throw for minimum integer value total copies"),

				// 6-test edge case: zero total copies (should be allowed based on current
				// validation)
				() -> assertDoesNotThrow(
						() -> new Book("Java", "Author", "123", "Pub", 2020, "Programming", 0, "English", "A1"),
						"Should allow zero total copies"),

				// 7-test edge case: current year (should be allowed)
				() -> assertDoesNotThrow(
						() -> new Book("Java", "Author", "123", "Pub", currentYear, "Programming", 5, "English", "A1"),
						"Should allow current year"),

				// 8-test valid past years
				() -> assertDoesNotThrow(
						() -> new Book("Java", "Author", "123", "Pub", 1990, "Programming", 5, "English", "A1"),
						"Should allow past year (1990)"),

				() -> assertDoesNotThrow(
						() -> new Book("Java", "Author", "123", "Pub", 2000, "Programming", 5, "English", "A1"),
						"Should allow past year (2000)"));
	}
}
