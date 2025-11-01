package lms.domain;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

class BookTest {

    Book book;

    @BeforeEach
    void setUp() {
        book = new Book("Java", "Author", "123", "Pub", 2020, "Programming", 5, "English", "A1");
    }

    @AfterEach
    void tearDown() {
        book = null;
    }

    @Test
    void givenValidInputs_whenBookCreated_thenFieldsAreInitialized() {
        assertNotNull(book.getBookId());
        assertEquals("Java", book.getTitle());
        assertEquals(5, book.getTotalCopies());
        assertEquals(5, book.getAvailableCopies());
    }

    @Test
    void givenInvalidEmptyTitle_whenCreateBook_thenThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
            new Book("", "Author", "123", "Pub", 2020, "Programming", 5, "English", "A1")
        );
    }

    @Test
    void givenNullAuthor_whenCreateBook_thenThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
            new Book("Java", null, "123", "Pub", 2020, "Programming", 5, "English", "A1")
        );
    }

    @Test
    void givenFutureYear_whenCreateBook_thenThrowException() {
        int nextYear = java.time.Year.now().getValue() + 1;
        assertThrows(IllegalArgumentException.class, () ->
            new Book("Java", "Author", "123", "Pub", nextYear, "Programming", 5, "English", "A1")
        );
    }

    @Test
    void givenNegativeCopies_whenCreateBook_thenThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
            new Book("Java", "Author", "123", "Pub", 2020, "Programming", -5, "English", "A1")
        );
    }

    @Test
    void givenValidSetters_whenUpdateFields_thenValuesChange() {
        book.setTitle("New Title");
        book.setPublisher("OReilly");
        book.setTotalCopies(10);
        book.setAvailableCopies(8);

        assertEquals("New Title", book.getTitle());
        assertEquals("OReilly", book.getPublisher());
        assertEquals(10, book.getTotalCopies());
        assertEquals(8, book.getAvailableCopies());
    }
}
