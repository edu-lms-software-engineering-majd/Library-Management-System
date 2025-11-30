package lms.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JournalTest {

    private Journal journal;

    @BeforeEach
    void setUp() {
        journal = new Journal("Nature", "Multiple Authors", 4);
    }

    @Test
    void shouldCreateJournalWithValidData() {
        assertNotNull(journal);
        assertEquals("Nature", journal.getTitle());
        assertEquals("Multiple Authors", journal.getAuthor());
        assertEquals(4, journal.getTotalCopies());
        assertEquals(4, journal.getAvailableCopies());
        assertNotNull(journal.getId());
    }

    @Test
    void shouldCreateJournalWithDefaultCopies() {
        Journal journalDefault = new Journal("Science", "Editorial Board");
        
        assertNotNull(journalDefault);
        assertEquals("Science", journalDefault.getTitle());
        assertEquals("Editorial Board", journalDefault.getAuthor());
        assertEquals(1, journalDefault.getTotalCopies());
        assertEquals(1, journalDefault.getAvailableCopies());
    }

    @Test
    void shouldThrowExceptionWhenTitleIsNull() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Journal(null, "Author")
        );
    }

    @Test
    void shouldThrowExceptionWhenTitleIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Journal("", "Author")
        );
    }

    @Test
    void shouldThrowExceptionWhenTitleIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Journal("   ", "Author")
        );
    }

    @Test
    void shouldThrowExceptionWhenAuthorIsNull() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Journal("Title", null)
        );
    }

    @Test
    void shouldThrowExceptionWhenAuthorIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Journal("Title", "")
        );
    }

    @Test
    void shouldThrowExceptionWhenAuthorIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Journal("Title", "   ")
        );
    }

    @Test
    void shouldThrowExceptionWhenTotalCopiesIsZero() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Journal("Title", "Author", 0)
        );
    }

    @Test
    void shouldThrowExceptionWhenTotalCopiesIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Journal("Title", "Author", -1)
        );
    }

    @Test
    void shouldReturnTrueForIsAvailableWhenCopiesAvailable() {
        assertTrue(journal.isAvailable());
    }

    @Test
    void shouldReturnFalseForIsAvailableWhenNoCopiesAvailable() {
        journal.decrementAvailableCopies();
        journal.decrementAvailableCopies();
        journal.decrementAvailableCopies();
        journal.decrementAvailableCopies();
        
        assertFalse(journal.isAvailable());
    }

    @Test
    void shouldReturnFalseForIsBorrowedWhenAllCopiesAvailable() {
        assertFalse(journal.isBorrowed());
    }

    @Test
    void shouldReturnTrueForIsBorrowedWhenAtLeastOneCopyBorrowed() {
        journal.decrementAvailableCopies();
        
        assertTrue(journal.isBorrowed());
    }

    @Test
    void shouldDecrementAvailableCopiesSuccessfully() {
        journal.decrementAvailableCopies();
        
        assertEquals(3, journal.getAvailableCopies());
    }

    @Test
    void shouldThrowExceptionWhenDecrementingWithNoCopiesAvailable() {
        journal.decrementAvailableCopies();
        journal.decrementAvailableCopies();
        journal.decrementAvailableCopies();
        journal.decrementAvailableCopies();
        
        assertThrows(IllegalStateException.class, () -> 
            journal.decrementAvailableCopies()
        );
    }

    @Test
    void shouldIncrementAvailableCopiesSuccessfully() {
        journal.decrementAvailableCopies();
        journal.incrementAvailableCopies();
        
        assertEquals(4, journal.getAvailableCopies());
    }

    @Test
    void shouldThrowExceptionWhenIncrementingBeyondTotalCopies() {
        assertThrows(IllegalStateException.class, () -> 
            journal.incrementAvailableCopies()
        );
    }

    @Test
    void shouldSetTitleSuccessfully() {
        journal.setTitle("The Lancet");
        
        assertEquals("The Lancet", journal.getTitle());
    }

    @Test
    void shouldThrowExceptionWhenSettingNullTitle() {
        assertThrows(IllegalArgumentException.class, () -> 
            journal.setTitle(null)
        );
    }

    @Test
    void shouldThrowExceptionWhenSettingEmptyTitle() {
        assertThrows(IllegalArgumentException.class, () -> 
            journal.setTitle("")
        );
    }

    @Test
    void shouldThrowExceptionWhenSettingBlankTitle() {
        assertThrows(IllegalArgumentException.class, () -> 
            journal.setTitle("   ")
        );
    }

    @Test
    void shouldSetAuthorSuccessfully() {
        journal.setAuthor("Research Team");
        
        assertEquals("Research Team", journal.getAuthor());
    }

    @Test
    void shouldThrowExceptionWhenSettingNullAuthor() {
        assertThrows(IllegalArgumentException.class, () -> 
            journal.setAuthor(null)
        );
    }

    @Test
    void shouldThrowExceptionWhenSettingEmptyAuthor() {
        assertThrows(IllegalArgumentException.class, () -> 
            journal.setAuthor("")
        );
    }

    @Test
    void shouldThrowExceptionWhenSettingBlankAuthor() {
        assertThrows(IllegalArgumentException.class, () -> 
            journal.setAuthor("   ")
        );
    }

    @Test
    void shouldSetTotalCopiesSuccessfully() {
        journal.setTotalCopies(6);
        
        assertEquals(6, journal.getTotalCopies());
    }

    @Test
    void shouldThrowExceptionWhenSettingZeroTotalCopies() {
        assertThrows(IllegalArgumentException.class, () -> 
            journal.setTotalCopies(0)
        );
    }

    @Test
    void shouldThrowExceptionWhenSettingNegativeTotalCopies() {
        assertThrows(IllegalArgumentException.class, () -> 
            journal.setTotalCopies(-1)
        );
    }

    @Test
    void shouldSetAvailableCopiesSuccessfully() {
        journal.setAvailableCopies(2);
        
        assertEquals(2, journal.getAvailableCopies());
    }

    @Test
    void shouldThrowExceptionWhenSettingNegativeAvailableCopies() {
        assertThrows(IllegalArgumentException.class, () -> 
            journal.setAvailableCopies(-1)
        );
    }

    @Test
    void shouldThrowExceptionWhenSettingAvailableCopiesBeyondTotal() {
        assertThrows(IllegalArgumentException.class, () -> 
            journal.setAvailableCopies(6)
        );
    }

    @Test
    void shouldReturnCorrectToStringFormat() {
        String result = journal.toString();
        
        assertTrue(result.contains("Nature"));
        assertTrue(result.contains("Multiple Authors"));
        assertTrue(result.contains("4/4"));
    }

    @Test
    void shouldGenerateUniqueId() {
        Journal journal1 = new Journal("Title1", "Author1");
        Journal journal2 = new Journal("Title2", "Author2");
        
        assertNotEquals(journal1.getId(), journal2.getId());
    }

    @Test
    void shouldMaintainConsistentStateAfterMultipleOperations() {
        journal.setTitle("Updated Journal");
        journal.setAuthor("Updated Author");
        journal.decrementAvailableCopies();
        journal.decrementAvailableCopies();
        journal.incrementAvailableCopies();
        
        assertEquals("Updated Journal", journal.getTitle());
        assertEquals("Updated Author", journal.getAuthor());
        assertEquals(3, journal.getAvailableCopies());
        assertEquals(4, journal.getTotalCopies());
        assertTrue(journal.isAvailable());
        assertTrue(journal.isBorrowed());
    }
}