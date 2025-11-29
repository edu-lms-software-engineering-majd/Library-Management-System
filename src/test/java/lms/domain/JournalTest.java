package lms.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

		assertThrows(IllegalStateException.class, () -> journal.decrementAvailableCopies());
	}

	@Test
	void givenDecrementedCopies_whenIncrementAvailableCopies_thenCopiesIncreased() {
		journal.decrementAvailableCopies();
		int current = journal.getAvailableCopies();

		journal.incrementAvailableCopies();

		assertEquals(current + 1, journal.getAvailableCopies());
	}

	@Test
	void givenAllCopiesAvailable_whenIncrementAvailableCopies_thenThrowIllegalStateException() {
		assertThrows(IllegalStateException.class, () -> journal.incrementAvailableCopies());
	}

	@Test
	void givenJournal_whenGetId_thenReturnNonNullUUID() {
		assertNotNull(journal.getId());
	}

	@Test
	void givenMultipleJournals_whenCreate_thenEachHasUniqueId() {
		Journal j1 = new Journal("Journal 1", "Publisher 1", 1);
		Journal j2 = new Journal("Journal 2", "Publisher 2", 2);
		Journal j3 = new Journal("Journal 3", "Publisher 3", 3);

		assertNotEquals(j1.getId(), j2.getId());
		assertNotEquals(j2.getId(), j3.getId());
		assertNotEquals(j1.getId(), j3.getId());
	}

	@Test
	void givenNewTitle_whenSetTitle_thenTitleIsUpdated() {
		journal.setTitle("New Title");
		assertEquals("New Title", journal.getTitle());
	}

	@Test
	void givenNewAuthor_whenSetAuthor_thenAuthorIsUpdated() {
		journal.setAuthor("Majd");
		assertEquals("Majd", journal.getAuthor());
	}

	@Test
	void givenNewTotalCopies_whenSetTotalCopies_thenTotalCopiesIsUpdated() {
		journal.setTotalCopies(10);
		assertEquals(10, journal.getTotalCopies());
	}

	@Test
	void givenNewAvailableCopies_whenSetAvailableCopies_thenAvailableCopiesIsUpdated() {
		journal.setAvailableCopies(3);
		assertEquals(3, journal.getAvailableCopies());
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
