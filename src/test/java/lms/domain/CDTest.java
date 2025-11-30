package lms.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CDTest {

    private CD cd;

    @BeforeEach
    void setUp() {
        cd = new CD("Dark Side of the Moon", "Pink Floyd", 3);
    }

    @Test
    void shouldCreateCDWithValidData() {
        assertNotNull(cd);
        assertEquals("Dark Side of the Moon", cd.getTitle());
        assertEquals("Pink Floyd", cd.getArtist());
        assertEquals(3, cd.getTotalCopies());
        assertEquals(3, cd.getAvailableCopies());
        assertNotNull(cd.getId());
    }

    @Test
    void shouldCreateCDWithDefaultCopies() {
        CD cdDefault = new CD("Abbey Road", "The Beatles");
        
        assertNotNull(cdDefault);
        assertEquals("Abbey Road", cdDefault.getTitle());
        assertEquals("The Beatles", cdDefault.getArtist());
        assertEquals(1, cdDefault.getTotalCopies());
        assertEquals(1, cdDefault.getAvailableCopies());
    }

    @Test
    void shouldThrowExceptionWhenTitleIsNull() {
        assertThrows(IllegalArgumentException.class, () -> 
            new CD(null, "Artist")
        );
    }

    @Test
    void shouldThrowExceptionWhenTitleIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> 
            new CD("", "Artist")
        );
    }

    @Test
    void shouldThrowExceptionWhenTitleIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> 
            new CD("   ", "Artist")
        );
    }

    @Test
    void shouldThrowExceptionWhenArtistIsNull() {
        assertThrows(IllegalArgumentException.class, () -> 
            new CD("Title", null)
        );
    }

    @Test
    void shouldThrowExceptionWhenArtistIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> 
            new CD("Title", "")
        );
    }

    @Test
    void shouldThrowExceptionWhenArtistIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> 
            new CD("Title", "   ")
        );
    }

    @Test
    void shouldThrowExceptionWhenTotalCopiesIsZero() {
        assertThrows(IllegalArgumentException.class, () -> 
            new CD("Title", "Artist", 0)
        );
    }

    @Test
    void shouldThrowExceptionWhenTotalCopiesIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> 
            new CD("Title", "Artist", -1)
        );
    }

    @Test
    void shouldReturnTrueForIsAvailableWhenCopiesAvailable() {
        assertTrue(cd.isAvailable());
    }

    @Test
    void shouldReturnFalseForIsAvailableWhenNoCopiesAvailable() {
        cd.decrementAvailableCopies();
        cd.decrementAvailableCopies();
        cd.decrementAvailableCopies();
        
        assertFalse(cd.isAvailable());
    }

    @Test
    void shouldReturnFalseForIsBorrowedWhenAllCopiesAvailable() {
        assertFalse(cd.isBorrowed());
    }

    @Test
    void shouldReturnTrueForIsBorrowedWhenAtLeastOneCopyBorrowed() {
        cd.decrementAvailableCopies();
        
        assertTrue(cd.isBorrowed());
    }

    @Test
    void shouldDecrementAvailableCopiesSuccessfully() {
        cd.decrementAvailableCopies();
        
        assertEquals(2, cd.getAvailableCopies());
    }

    @Test
    void shouldThrowExceptionWhenDecrementingWithNoCopiesAvailable() {
        cd.decrementAvailableCopies();
        cd.decrementAvailableCopies();
        cd.decrementAvailableCopies();
        
        assertThrows(IllegalStateException.class, () -> 
            cd.decrementAvailableCopies()
        );
    }

    @Test
    void shouldIncrementAvailableCopiesSuccessfully() {
        cd.decrementAvailableCopies();
        cd.incrementAvailableCopies();
        
        assertEquals(3, cd.getAvailableCopies());
    }

    @Test
    void shouldThrowExceptionWhenIncrementingBeyondTotalCopies() {
        assertThrows(IllegalStateException.class, () -> 
            cd.incrementAvailableCopies()
        );
    }

    @Test
    void shouldSetTitleSuccessfully() {
        cd.setTitle("The Wall");
        
        assertEquals("The Wall", cd.getTitle());
    }

    @Test
    void shouldThrowExceptionWhenSettingNullTitle() {
        assertThrows(IllegalArgumentException.class, () -> 
            cd.setTitle(null)
        );
    }

    @Test
    void shouldThrowExceptionWhenSettingEmptyTitle() {
        assertThrows(IllegalArgumentException.class, () -> 
            cd.setTitle("")
        );
    }

    @Test
    void shouldThrowExceptionWhenSettingBlankTitle() {
        assertThrows(IllegalArgumentException.class, () -> 
            cd.setTitle("   ")
        );
    }

    @Test
    void shouldSetArtistSuccessfully() {
        cd.setArtist("Led Zeppelin");
        
        assertEquals("Led Zeppelin", cd.getArtist());
    }

    @Test
    void shouldThrowExceptionWhenSettingNullArtist() {
        assertThrows(IllegalArgumentException.class, () -> 
            cd.setArtist(null)
        );
    }

    @Test
    void shouldThrowExceptionWhenSettingEmptyArtist() {
        assertThrows(IllegalArgumentException.class, () -> 
            cd.setArtist("")
        );
    }

    @Test
    void shouldThrowExceptionWhenSettingBlankArtist() {
        assertThrows(IllegalArgumentException.class, () -> 
            cd.setArtist("   ")
        );
    }

    @Test
    void shouldSetTotalCopiesSuccessfully() {
        cd.setTotalCopies(5);
        
        assertEquals(5, cd.getTotalCopies());
    }

    @Test
    void shouldThrowExceptionWhenSettingZeroTotalCopies() {
        assertThrows(IllegalArgumentException.class, () -> 
            cd.setTotalCopies(0)
        );
    }

    @Test
    void shouldThrowExceptionWhenSettingNegativeTotalCopies() {
        assertThrows(IllegalArgumentException.class, () -> 
            cd.setTotalCopies(-1)
        );
    }

    @Test
    void shouldSetAvailableCopiesSuccessfully() {
        cd.setAvailableCopies(2);
        
        assertEquals(2, cd.getAvailableCopies());
    }

    @Test
    void shouldThrowExceptionWhenSettingNegativeAvailableCopies() {
        assertThrows(IllegalArgumentException.class, () -> 
            cd.setAvailableCopies(-1)
        );
    }

    @Test
    void shouldThrowExceptionWhenSettingAvailableCopiesBeyondTotal() {
        assertThrows(IllegalArgumentException.class, () -> 
            cd.setAvailableCopies(5)
        );
    }

    @Test
    void shouldReturnCorrectToStringFormat() {
        String result = cd.toString();
        
        assertTrue(result.contains("Dark Side of the Moon"));
        assertTrue(result.contains("Pink Floyd"));
        assertTrue(result.contains("3/3"));
    }

    @Test
    void shouldGenerateUniqueId() {
        CD cd1 = new CD("Title1", "Artist1");
        CD cd2 = new CD("Title2", "Artist2");
        
        assertNotEquals(cd1.getId(), cd2.getId());
    }

    @Test
    void shouldMaintainConsistentStateAfterMultipleOperations() {
        cd.setTitle("Updated Album");
        cd.setArtist("Updated Artist");
        cd.decrementAvailableCopies();
        cd.decrementAvailableCopies();
        cd.incrementAvailableCopies();
        
        assertEquals("Updated Album", cd.getTitle());
        assertEquals("Updated Artist", cd.getArtist());
        assertEquals(2, cd.getAvailableCopies());
        assertEquals(3, cd.getTotalCopies());
        assertTrue(cd.isAvailable());
        assertTrue(cd.isBorrowed());
    }
}