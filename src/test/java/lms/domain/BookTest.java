package lms.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BookTest {

    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book("Clean Code", "Robert Martin", "978-0132350884", "Prentice Hall", 
                       2008, "Software Engineering", 5, "English", "A1-101");
    }

    @Test
    void shouldCreateBookWithValidData() {
        assertNotNull(book);
        assertEquals("Clean Code", book.getTitle());
        assertEquals("Robert Martin", book.getAuthor());
        assertEquals("978-0132350884", book.getIsbn());
        assertEquals("Prentice Hall", book.getPublisher());
        assertEquals(2008, book.getPublicationYear());
        assertEquals("Software Engineering", book.getCategory());
        assertEquals(5, book.getTotalCopies());
        assertEquals(5, book.getAvailableCopies());
        assertEquals("English", book.getLanguage());
        assertEquals("A1-101", book.getShelfLocation());
        assertNotNull(book.getId());
    }

    @Test
    void shouldCreateBookWithDescription() {
        Book bookWithDesc = new Book("Design Patterns", "Gang of Four", "978-0201633610", 
                                     "Addison-Wesley", 1994, "Software Design", 3, "English", 
                                     "Classic book on design patterns", "B2-205");
        
        assertNotNull(bookWithDesc);
        assertEquals("Design Patterns", bookWithDesc.getTitle());
        assertEquals("Classic book on design patterns", bookWithDesc.getDescription());
    }

    @Test
    void shouldThrowExceptionWhenTitleIsNull() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Book(null, "Author", "123456789", "Publisher", 2020, "Category", 1, "English", "A1")
        );
    }

    @Test
    void shouldThrowExceptionWhenTitleIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Book("", "Author", "123456789", "Publisher", 2020, "Category", 1, "English", "A1")
        );
    }

    @Test
    void shouldThrowExceptionWhenAuthorIsNull() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Book("Title", null, "123456789", "Publisher", 2020, "Category", 1, "English", "A1")
        );
    }

    @Test
    void shouldThrowExceptionWhenAuthorIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Book("Title", "", "123456789", "Publisher", 2020, "Category", 1, "English", "A1")
        );
    }

    @Test
    void shouldThrowExceptionWhenIsbnIsNull() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Book("Title", "Author", null, "Publisher", 2020, "Category", 1, "English", "A1")
        );
    }

    @Test
    void shouldThrowExceptionWhenIsbnIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Book("Title", "Author", "", "Publisher", 2020, "Category", 1, "English", "A1")
        );
    }

    @Test
    void shouldThrowExceptionWhenPublisherIsNull() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Book("Title", "Author", "123456789", null, 2020, "Category", 1, "English", "A1")
        );
    }

    @Test
    void shouldThrowExceptionWhenPublisherIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Book("Title", "Author", "123456789", "", 2020, "Category", 1, "English", "A1")
        );
    }

    @Test
    void shouldThrowExceptionWhenCategoryIsNull() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Book("Title", "Author", "123456789", "Publisher", 2020, null, 1, "English", "A1")
        );
    }

    @Test
    void shouldThrowExceptionWhenCategoryIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Book("Title", "Author", "123456789", "Publisher", 2020, "", 1, "English", "A1")
        );
    }

    @Test
    void shouldThrowExceptionWhenLanguageIsNull() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Book("Title", "Author", "123456789", "Publisher", 2020, "Category", 1, null, "A1")
        );
    }

    @Test
    void shouldThrowExceptionWhenLanguageIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Book("Title", "Author", "123456789", "Publisher", 2020, "Category", 1, "", "A1")
        );
    }

    @Test
    void shouldThrowExceptionWhenShelfLocationIsNull() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Book("Title", "Author", "123456789", "Publisher", 2020, "Category", 1, "English", null)
        );
    }

    @Test
    void shouldThrowExceptionWhenShelfLocationIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Book("Title", "Author", "123456789", "Publisher", 2020, "Category", 1, "English", "")
        );
    }

    @Test
    void shouldThrowExceptionWhenTotalCopiesIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Book("Title", "Author", "123456789", "Publisher", 2020, "Category", -1, "English", "A1")
        );
    }

    @Test
    void shouldAllowZeroTotalCopies() {
        Book b = new Book("Title", "Author", "123456789", "Publisher", 2020, "Category", 0, "English", "A1");
        assertEquals(0, b.getTotalCopies());
        assertEquals(0, b.getAvailableCopies());
    }


    @Test
    void shouldReturnTrueForIsAvailableWhenCopiesAvailable() {
        assertTrue(book.isAvailable());
    }

    @Test
    void shouldReturnFalseForIsAvailableWhenNoCopiesAvailable() {
        for (int i = 0; i < 5; i++) {
            book.decrementAvailableCopies();
        }
        
        assertFalse(book.isAvailable());
    }

    @Test
    void shouldDecrementAvailableCopiesSuccessfully() {
        book.decrementAvailableCopies();
        
        assertEquals(4, book.getAvailableCopies());
    }

    @Test
    void shouldThrowExceptionWhenDecrementingWithNoCopiesAvailable() {
        for (int i = 0; i < 5; i++) {
            book.decrementAvailableCopies();
        }
        
        assertThrows(IllegalStateException.class, () -> 
            book.decrementAvailableCopies()
        );
    }

    @Test
    void shouldIncrementAvailableCopiesSuccessfully() {
        book.decrementAvailableCopies();
        book.incrementAvailableCopies();
        
        assertEquals(5, book.getAvailableCopies());
    }

    @Test
    void shouldThrowExceptionWhenIncrementingBeyondTotalCopies() {
        assertThrows(IllegalStateException.class, () -> 
            book.incrementAvailableCopies()
        );
    }

    @Test
    void shouldSetTitleSuccessfully() {
        book.setTitle("Refactoring");
        
        assertEquals("Refactoring", book.getTitle());
    }

    @Test
    void shouldThrowExceptionWhenSettingNullTitle() {
        assertThrows(IllegalArgumentException.class, () -> 
            book.setTitle(null)
        );
    }

    @Test
    void shouldThrowExceptionWhenSettingEmptyTitle() {
        assertThrows(IllegalArgumentException.class, () -> 
            book.setTitle("")
        );
    }

    @Test
    void shouldSetAuthorSuccessfully() {
        book.setAuthor("Martin Fowler");
        
        assertEquals("Martin Fowler", book.getAuthor());
    }

    @Test
    void shouldThrowExceptionWhenSettingNullAuthor() {
        assertThrows(IllegalArgumentException.class, () -> 
            book.setAuthor(null)
        );
    }

    @Test
    void shouldThrowExceptionWhenSettingEmptyAuthor() {
        assertThrows(IllegalArgumentException.class, () -> 
            book.setAuthor("")
        );
    }

    @Test
    void shouldSetPublisherSuccessfully() {
        book.setPublisher("O'Reilly Media");
        
        assertEquals("O'Reilly Media", book.getPublisher());
    }

    @Test
    void shouldThrowExceptionWhenSettingNullPublisher() {
        assertThrows(IllegalArgumentException.class, () -> 
            book.setPublisher(null)
        );
    }

    @Test
    void shouldThrowExceptionWhenSettingEmptyPublisher() {
        assertThrows(IllegalArgumentException.class, () -> 
            book.setPublisher("")
        );
    }

    @Test
    void shouldSetPublicationYearSuccessfully() {
        book.setPublicationYear(2010);
        
        assertEquals(2010, book.getPublicationYear());
    }

    @Test
    void shouldThrowExceptionWhenSettingFuturePublicationYear() {
        assertThrows(IllegalArgumentException.class, () -> 
            book.setPublicationYear(2030)
        );
    }

    @Test
    void shouldSetCategorySuccessfully() {
        book.setCategory("Programming");
        
        assertEquals("Programming", book.getCategory());
    }

    @Test
    void shouldThrowExceptionWhenSettingNullCategory() {
        assertThrows(IllegalArgumentException.class, () -> 
            book.setCategory(null)
        );
    }

    @Test
    void shouldThrowExceptionWhenSettingEmptyCategory() {
        assertThrows(IllegalArgumentException.class, () -> 
            book.setCategory("")
        );
    }

    @Test
    void shouldSetLanguageSuccessfully() {
        book.setLanguage("Arabic");
        
        assertEquals("Arabic", book.getLanguage());
    }

    @Test
    void shouldThrowExceptionWhenSettingNullLanguage() {
        assertThrows(IllegalArgumentException.class, () -> 
            book.setLanguage(null)
        );
    }

    @Test
    void shouldThrowExceptionWhenSettingEmptyLanguage() {
        assertThrows(IllegalArgumentException.class, () -> 
            book.setLanguage("")
        );
    }

    @Test
    void shouldSetShelfLocationSuccessfully() {
        book.setShelfLocation("C3-303");
        
        assertEquals("C3-303", book.getShelfLocation());
    }

    @Test
    void shouldThrowExceptionWhenSettingNullShelfLocation() {
        assertThrows(IllegalArgumentException.class, () -> 
            book.setShelfLocation(null)
        );
    }

    @Test
    void shouldThrowExceptionWhenSettingEmptyShelfLocation() {
        assertThrows(IllegalArgumentException.class, () -> 
            book.setShelfLocation("")
        );
    }

    @Test
    void shouldSetDescriptionSuccessfully() {
        book.setDescription("A comprehensive guide to software craftsmanship");
        
        assertEquals("A comprehensive guide to software craftsmanship", book.getDescription());
    }

    @Test
    void shouldSetTotalCopiesSuccessfully() {
        book.setTotalCopies(10);
        
        assertEquals(10, book.getTotalCopies());
    }

    @Test
    void shouldThrowExceptionWhenSettingNegativeTotalCopies() {
        assertThrows(IllegalArgumentException.class, () -> 
            book.setTotalCopies(-1)
        );
    }

    @Test
    void shouldAllowSettingZeroTotalCopies() {
        book.setTotalCopies(0);
        assertEquals(0, book.getTotalCopies());
    }


    @Test
    void shouldGenerateUniqueId() {
        Book book1 = new Book("Title1", "Author1", "ISBN1", "Publisher1", 2020, "Cat1", 1, "English", "A1");
        Book book2 = new Book("Title2", "Author2", "ISBN2", "Publisher2", 2021, "Cat2", 1, "English", "A2");
        
        assertNotEquals(book1.getId(), book2.getId());
    }

    @Test
    void shouldMaintainConsistentStateAfterMultipleOperations() {
        book.setTitle("Updated Title");
        book.setAuthor("Updated Author");
        book.decrementAvailableCopies();
        book.decrementAvailableCopies();
        book.incrementAvailableCopies();
        
        assertEquals("Updated Title", book.getTitle());
        assertEquals("Updated Author", book.getAuthor());
        assertEquals(4, book.getAvailableCopies());
        assertEquals(5, book.getTotalCopies());
    }
}