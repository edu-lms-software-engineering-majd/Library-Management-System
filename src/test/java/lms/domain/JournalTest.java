package lms.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JournalTest {

	private Journal journal;

	@BeforeEach
	void setUp() {
		journal = new Journal("Nature", "Springer Nature", 5);
	}

	@AfterEach
	void tearDown() {
		journal = null;
	}

	@Test
	void givenValidParameters_whenCreateJournal_thenJournalIsInitializedCorrectly() {
		assertNotNull(journal);
		assertNotNull(journal.getId());
		assertEquals("Nature", journal.getTitle());
		assertEquals("Springer Nature", journal.getAuthor());
		assertEquals(5, journal.getTotalCopies());
		assertEquals(5, journal.getAvailableCopies());
	}

	@Test
	void givenDefaultConstructor_whenCreateJournal_thenJournalHasOneCopy() {
		Journal singleJournal = new Journal("Science", "AAAS");
		
		assertNotNull(singleJournal);
		assertEquals(1, singleJournal.getTotalCopies());
		assertEquals(1, singleJournal.getAvailableCopies());
	}

	@Test
	void givenJournalWithAvailableCopies_whenCheckIsAvailable_thenReturnTrue() {
		assertTrue(journal.isAvailable());
	}

	@Test
	void givenJournalWithNoAvailableCopies_whenCheckIsAvailable_thenReturnFalse() {
		journal.setAvailableCopies(0);
		
		assertFalse(journal.isAvailable());
	}

	@Test
	void givenAvailableCopies_whenDecrementAvailableCopies_thenCopiesDecreased() {
		int initialCopies = journal.getAvailableCopies();
		
		journal.decrementAvailableCopies();
		
		assertEquals(initialCopies - 1, journal.getAvailableCopies());
	}

	@Test
	void givenMultipleCopies_whenDecrementMultipleTimes_thenCopiesDecreasedCorrectly() {
		journal.decrementAvailableCopies();
		journal.decrementAvailableCopies();
		journal.decrementAvailableCopies();
		
		assertEquals(2, journal.getAvailableCopies());
	}

	@Test
	void givenNoCopiesAvailable_whenDecrementAvailableCopies_thenThrowIllegalStateException() {
		journal.setAvailableCopies(0);
		
		assertThrows(IllegalStateException.class, () -> {
			journal.decrementAvailableCopies();
		});
	}
	

	@Test
	void givenDecrementedCopies_whenIncrementAvailableCopies_thenCopiesIncreased() {
		journal.decrementAvailableCopies();
		int currentCopies = journal.getAvailableCopies();
		
		journal.incrementAvailableCopies();
		
		assertEquals(currentCopies + 1, journal.getAvailableCopies());
	}

	@Test
	void givenAllCopiesAvailable_whenIncrementAvailableCopies_thenThrowIllegalStateException() {
		assertThrows(IllegalStateException.class, () -> {
			journal.incrementAvailableCopies();
		});
	}

	@Test
	void givenJournal_whenGetId_thenReturnNonNullUUID() {
		UUID id = journal.getId();
		
		assertNotNull(id);
	}

	@Test
	void givenMultipleJournals_whenCreate_thenEachHasUniqueId() {
		Journal journal1 = new Journal("Journal 1", "Publisher 1", 1);
		Journal journal2 = new Journal("Journal 2", "Publisher 2", 2);
		Journal journal3 = new Journal("Journal 3", "Publisher 3", 3);
		
		assertFalse(journal1.getId().equals(journal2.getId()));
		assertFalse(journal2.getId().equals(journal3.getId()));
		assertFalse(journal1.getId().equals(journal3.getId()));
	}

	@Test
	void givenNewTitle_whenSetTitle_thenTitleIsUpdated() {
		String newTitle = "Hello Title";
		journal.setTitle(newTitle);
		
		assertEquals(newTitle, journal.getTitle());
	}

	@Test
	void givenNewAuthor_whenSetAuthor_thenAuthorIsUpdated() {
		String newAuthor = "Majd Awwad";
		journal.setAuthor(newAuthor);
		
		assertEquals(newAuthor, journal.getAuthor());
	}

	@Test
	void givenNewTotalCopies_whenSetTotalCopies_thenTotalCopiesIsUpdated() {
		int newTotal = 10;
		journal.setTotalCopies(newTotal);
		
		assertEquals(newTotal, journal.getTotalCopies());
	}

	@Test
	void givenNewAvailableCopies_whenSetAvailableCopies_thenAvailableCopiesIsUpdated() {
		int newAvailable = 3;
		journal.setAvailableCopies(newAvailable);
		
		assertEquals(newAvailable, journal.getAvailableCopies());
	}

	@Test
	void givenJournalWithAllCopiesAvailable_whenCheckIsBorrowed_thenReturnFalse() {
		assertFalse(journal.isBorrowed());
	}

	@Test
	void givenJournalWithSomeCopiesBorrowed_whenCheckIsBorrowed_thenReturnTrue() {
		journal.decrementAvailableCopies();
		
		assertTrue(journal.isBorrowed());
	}

	@Test
	void givenJournalWithAllCopiesBorrowed_whenCheckIsBorrowed_thenReturnTrue() {
		journal.setAvailableCopies(0);
		
		assertTrue(journal.isBorrowed());
	}

	@Test
	void givenJournal_whenToString_thenReturnsFormattedString() {
		String result = journal.toString();
		
		assertNotNull(result);
		assertTrue(result.contains("Nature"));
		assertTrue(result.contains("Springer Nature"));
		assertTrue(result.contains("5/5"));
	}

	@Test
	void givenBorrowedJournal_whenToString_thenShowsCorrectCopiesStatus() {
		journal.decrementAvailableCopies();
		journal.decrementAvailableCopies();
		String result = journal.toString();
		
		assertTrue(result.contains("3/5"));
	}
}
