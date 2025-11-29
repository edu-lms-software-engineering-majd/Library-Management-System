package lms.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BookTest {

	private Book book;

	@BeforeEach
	void setUp() {
		book = new Book("Java Programming", "John Doe", "978-0134685991", "Pearson", 2020, "Programming", 5,
				"English", "A1-101");
	}

	@Test
	void givenValidParameters_whenCreateBook_thenBookIsInitializedCorrectly() {
		assertNotNull(book);
		assertNotNull(book.getId());
		assertEquals("Java Programming", book.getTitle());
		assertEquals("John Doe", book.getAuthor());
		assertEquals("978-0134685991", book.getIsbn());
		assertEquals("Pearson", book.getPublisher());
		assertEquals(2020, book.getPublicationYear());
		assertEquals("Programming", book.getCategory());
		assertEquals(5, book.getTotalCopies());
		assertEquals(5, book.getAvailableCopies());
		assertEquals("English", book.getLanguage());
		assertNull(book.getDescription());
		assertEquals("A1-101", book.getShelfLocation());
	}

	@Test
	void givenValidParametersWithDescription_whenCreateBook_thenBookIsInitializedWithDescription() {
		Book bookWithDescription = new Book("Python Basics", "Jane Smith", "978-1234567890", "OReilly", 2021,
				"Programming", 3, "English", "A comprehensive guide to Python", "B2-202");

		assertNotNull(bookWithDescription);
		assertEquals("A comprehensive guide to Python", bookWithDescription.getDescription());
	}

	@Test
	void givenBookWithAvailableCopies_whenCheckIsAvailable_thenReturnTrue() {
		assertTrue(book.isAvailable());
		assertTrue(book.hasAvailableCopies());
	}

	@Test
	void givenBookWithNoAvailableCopies_whenCheckIsAvailable_thenReturnFalse() {
		for (int i = 0; i < 5; i++) {
			book.decrementAvailableCopies();
		}

		assertFalse(book.isAvailable());
		assertFalse(book.hasAvailableCopies());
		assertTrue(book.isFullyBorrowed());
	}

	@Test
	void givenAvailableCopies_whenDecrementAvailableCopies_thenCopiesDecreased() {
		int initialCopies = book.getAvailableCopies();

		book.decrementAvailableCopies();

		assertEquals(initialCopies - 1, book.getAvailableCopies());
	}

	@Test
	void givenMultipleCopies_whenDecrementMultipleTimes_thenCopiesDecreasedCorrectly() {
		book.decrementAvailableCopies();
		book.decrementAvailableCopies();
		book.decrementAvailableCopies();

		assertEquals(2, book.getAvailableCopies());
	}

	@Test
	void givenNoCopiesAvailable_whenDecrementAvailableCopies_thenThrowIllegalStateException() {
		for (int i = 0; i < 5; i++) {
			book.decrementAvailableCopies();
		}

		assertThrows(IllegalStateException.class, () -> {
			book.decrementAvailableCopies();
		});
	}

	@Test
	void givenDecrementedCopies_whenIncrementAvailableCopies_thenCopiesIncreased() {
		book.decrementAvailableCopies();
		int currentCopies = book.getAvailableCopies();

		book.incrementAvailableCopies();

		assertEquals(currentCopies + 1, book.getAvailableCopies());
	}

	@Test
	void givenAllCopiesAvailable_whenIncrementAvailableCopies_thenThrowIllegalStateException() {
		assertThrows(IllegalStateException.class, () -> {
			book.incrementAvailableCopies();
		});
	}

	@Test
	void givenBook_whenGetId_thenReturnNonNullUUID() {
		assertNotNull(book.getId());
	}

	@Test
	void givenMultipleBooks_whenCreate_thenEachHasUniqueId() {
		Book book1 = new Book("Title1", "Author1", "ISBN1", "Pub1", 2020, "Cat1", 1, "English", "A1");
		Book book2 = new Book("Title2", "Author2", "ISBN2", "Pub2", 2021, "Cat2", 2, "English", "B2");
		Book book3 = new Book("Title3", "Author3", "ISBN3", "Pub3", 2022, "Cat3", 3, "English", "C3");

		assertFalse(book1.getId().equals(book2.getId()));
		assertFalse(book2.getId().equals(book3.getId()));
		assertFalse(book1.getId().equals(book3.getId()));
	}

	@Test
	void givenNewTitle_whenSetTitle_thenTitleIsUpdated() {
		book.setTitle("Advanced Java Programming");
		assertEquals("Advanced Java Programming", book.getTitle());
	}

	@Test
	void givenNullTitle_whenSetTitle_thenThrowException() {
		assertThrows(IllegalArgumentException.class, () -> book.setTitle(null));
	}

	@Test
	void givenNewAuthor_whenSetAuthor_thenAuthorIsUpdated() {
		book.setAuthor("Jane Smith");
		assertEquals("Jane Smith", book.getAuthor());
	}

	@Test
	void givenNullAuthor_whenSetAuthor_thenThrowException() {
		assertThrows(IllegalArgumentException.class, () -> book.setAuthor(null));
	}

	@Test
	void givenNewPublisher_whenSetPublisher_thenPublisherIsUpdated() {
		book.setPublisher("OReilly Media");
		assertEquals("OReilly Media", book.getPublisher());
	}

	@Test
	void givenNullPublisher_whenSetPublisher_thenThrowException() {
		assertThrows(IllegalArgumentException.class, () -> book.setPublisher(null));
	}

	@Test
	void givenNewPublicationYear_whenSetPublicationYear_thenYearIsUpdated() {
		book.setPublicationYear(2019);
		assertEquals(2019, book.getPublicationYear());
	}

	@Test
	void givenFutureYear_whenSetPublicationYear_thenThrowException() {
		assertThrows(IllegalArgumentException.class, () -> book.setPublicationYear(2030));
	}

	@Test
	void givenNewCategory_whenSetCategory_thenCategoryIsUpdated() {
		book.setCategory("Computer Science");
		assertEquals("Computer Science", book.getCategory());
	}

	@Test
	void givenNullCategory_whenSetCategory_thenThrowException() {
		assertThrows(IllegalArgumentException.class, () -> book.setCategory(null));
	}

	@Test
	void givenNewTotalCopies_whenSetTotalCopies_thenTotalCopiesIsUpdated() {
		book.setTotalCopies(10);
		assertEquals(10, book.getTotalCopies());
	}

	@Test
	void givenNegativeTotalCopies_whenSetTotalCopies_thenThrowException() {
		assertThrows(IllegalArgumentException.class, () -> book.setTotalCopies(-1));
	}

	@Test
	void givenNewLanguage_whenSetLanguage_thenLanguageIsUpdated() {
		book.setLanguage("Spanish");
		assertEquals("Spanish", book.getLanguage());
	}

	@Test
	void givenNullLanguage_whenSetLanguage_thenThrowException() {
		assertThrows(IllegalArgumentException.class, () -> book.setLanguage(null));
	}

	@Test
	void givenNewDescription_whenSetDescription_thenDescriptionIsUpdated() {
		String description = "An excellent book about Java programming fundamentals";
		book.setDescription(description);
		assertEquals(description, book.getDescription());
	}

	@Test
	void givenNewShelfLocation_whenSetShelfLocation_thenShelfLocationIsUpdated() {
		book.setShelfLocation("B3-205");
		assertEquals("B3-205", book.getShelfLocation());
	}

	@Test
	void givenNullShelfLocation_whenSetShelfLocation_thenThrowException() {
		assertThrows(IllegalArgumentException.class, () -> book.setShelfLocation(null));
	}

	@Test
	void givenBookWithDescription_whenGetDescription_thenReturnCorrectDescription() {
		Book bookWithDesc = new Book("Title", "Author", "ISBN", "Publisher", 2020, "Fiction", 5, "English",
				"A great story", "A1");
		assertEquals("A great story", bookWithDesc.getDescription());
	}

	@Test
	void givenBookWithoutDescription_whenGetDescription_thenReturnNull() {
		assertNull(book.getDescription());
	}

	@Test
	void givenBook_whenBorrowAndReturnCycle_thenCopiesManagesCorrectly() {
		int initial = book.getAvailableCopies();

		book.decrementAvailableCopies();
		assertEquals(initial - 1, book.getAvailableCopies());

		book.incrementAvailableCopies();
		assertEquals(initial, book.getAvailableCopies());
	}

	@Test
	void givenBookWithOneCopy_whenBorrowLastCopy_thenBookNotAvailable() {
		Book singleCopyBook = new Book("Title", "Author", "ISBN", "Publisher", 2020, "Fiction", 1, "English", "A1");
		assertTrue(singleCopyBook.isAvailable());

		singleCopyBook.decrementAvailableCopies();

		assertFalse(singleCopyBook.isAvailable());
	}

	@Test
	void givenBorrowedBook_whenReturnBook_thenBookBecomesAvailable() {
		Book singleCopyBook = new Book("Title", "Author", "ISBN", "Publisher", 2020, "Fiction", 1, "English", "A1");
		singleCopyBook.decrementAvailableCopies();
		assertFalse(singleCopyBook.isAvailable());

		singleCopyBook.incrementAvailableCopies();

		assertTrue(singleCopyBook.isAvailable());
	}

	@Test
	void givenBook_whenDecrementAllCopies_thenAllCopiesBecomeUnavailable() {
		for (int i = 0; i < 5; i++) {
			book.decrementAvailableCopies();
		}

		assertEquals(0, book.getAvailableCopies());
		assertFalse(book.isAvailable());
	}

	@Test
	void givenBookWithAllCopiesBorrowed_whenReturnAllCopies_thenAllCopiesAvailable() {
		for (int i = 0; i < 5; i++) {
			book.decrementAvailableCopies();
		}

		for (int i = 0; i < 5; i++) {
			book.incrementAvailableCopies();
		}

		assertEquals(5, book.getAvailableCopies());
		assertTrue(book.isAvailable());
	}

	@Test
	void givenOldPublicationYear_whenCreateBook_thenBookIsCreatedSuccessfully() {
		Book oldBook = new Book("Classic Literature", "Old Author", "123", "Old Publisher", 1900, "Literature", 2,
				"English", "D4");

		assertNotNull(oldBook);
		assertEquals(1900, oldBook.getPublicationYear());
	}

	@Test
	void givenLongTitle_whenCreateBook_thenTitleIsStoredCorrectly() {
		String longTitle = "This is a very long book title that contains many words and describes the book in detail";
		Book longTitleBook = new Book(longTitle, "Author", "ISBN", "Publisher", 2020, "Fiction", 1, "English", "E5");

		assertEquals(longTitle, longTitleBook.getTitle());
	}

	@Test
	void givenSpecialCharactersInTitle_whenCreateBook_thenTitleIsStoredCorrectly() {
		String specialTitle = "C++ Programming: A Beginner's Guide (2nd Edition)";
		Book specialBook = new Book(specialTitle, "Author", "ISBN", "Publisher", 2020, "Programming", 1, "English",
				"F6");

		assertEquals(specialTitle, specialBook.getTitle());
	}
}

