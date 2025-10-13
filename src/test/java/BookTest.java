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

/**
 * Unit tests for the {@link Book} domain entity class.
 * 
 * <p>
 * This test class verifies the functionality and validation rules of the Book
 * class, including constructor validation, getter/setter methods, and edge case
 * handling.
 * </p>
 *
 * <h2>Test Coverage:</h2>
 * <ul>
 * <li>Valid book creation with both constructors</li>
 * <li>Comprehensive input validation for all constructor parameters</li>
 * <li>Getter and setter methods functionality</li>
 * <li>Boundary value analysis for publication year and total copies</li>
 * <li>Null, empty, and whitespace-only string validation</li>
 * </ul>
 *
 * @author Majd Awwad
 * @version 1.0
 * @see Book
 */
class BookTest {

	/**
	 * Sets up test environment before all test methods. Currently not used but
	 * available for future expansion.
	 *
	 * @throws Exception if setup fails
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		// Reserved for future global test setup
	}

	/**
	 * Cleans up test environment after all test methods. Currently not used but
	 * available for future expansion.
	 *
	 * @throws Exception if cleanup fails
	 */
	@AfterAll
	static void tearDownAfterClass() throws Exception {
		// Reserved for future global test cleanup
	}

	/**
	 * Sets up test environment before each test method. Currently not used but
	 * available for future expansion.
	 *
	 * @throws Exception if setup fails
	 */
	@BeforeEach
	void setUp() throws Exception {
		// Reserved for future per-test setup
	}

	/**
	 * Cleans up test environment after each test method. Currently not used but
	 * available for future expansion.
	 *
	 * @throws Exception if cleanup fails
	 */
	@AfterEach
	void tearDown() throws Exception {
		// Reserved for future per-test cleanup
		
	}

	/**
	 * Tests successful creation of a Book instance with valid parameters.
	 * 
	 * <p>
	 * Verifies that:
	 * </p>
	 * <ul>
	 * <li>Book ID is automatically generated and not null</li>
	 * <li>All constructor parameters are correctly assigned</li>
	 * <li>Available copies are initialized to match total copies</li>
	 * <li>Description is null when using the basic constructor</li>
	 * </ul>
	 *
	 * @see Book#Book(String, String, String, String, int, String, int, String,
	 *      String)
	 */
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

	/**
	 * Tests the Book constructor that includes an optional description parameter.
	 * 
	 * <p>
	 * Verifies that the description field is properly set when using the extended
	 * constructor with description.
	 * </p>
	 *
	 * @see Book#Book(String, String, String, String, int, String, int, String,
	 *      String, String)
	 */
	@Test
	void testBookWithDescriptionConstructor() {
		Book book = new Book("Java", "Author", "123", "Pub", 2020, "Programming", 5, "English", "A good book", "A1");
		assertEquals("A good book", book.getDescription());
	}

	/**
	 * Tests all setter and getter methods of the Book class.
	 * 
	 * <p>
	 * Verifies that each setter method correctly updates the corresponding field
	 * and that the getter method returns the updated value.
	 * </p>
	 * 
	 * <p>
	 * Tests all mutable properties including:
	 * </p>
	 * <ul>
	 * <li>String fields: title, author, isbn, publisher, category, language,
	 * description, shelfLocation</li>
	 * <li>Numeric fields: publicationYear, totalCopies, availableCopies</li>
	 * </ul>
	 */
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

	/**
	 * Comprehensive test for Book constructor input validation.
	 * 
	 * <p>
	 * Tests all validation rules defined in the Book constructor using assertAll to
	 * group multiple related assertions together.
	 * </p>
	 *
	 * <h3>Validation Categories Tested:</h3>
	 * <ol>
	 * <li>Empty string validation for all required fields</li>
	 * <li>Null value validation for all required fields</li>
	 * <li>Whitespace-only string validation</li>
	 * <li>Future publication year validation</li>
	 * <li>Negative total copies validation</li>
	 * <li>Valid edge cases (zero copies, current year, past years)</li>
	 * </ol>
	 *
	 * @see Book#Book(String, String, String, String, int, String, int, String,
	 *      String)
	 */
	@Test
	void testInvalidBookInputs() {
		int currentYear = java.time.Year.now().getValue();

		assertAll(
				// 1-test empty strings for all required fields
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

				// 2-Test null values for all required fields
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