package lms.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JournalTest {

	Journal journal;

	@BeforeEach
	void setUp() {
		journal = new Journal("Nature", "John Smith");
	}

	@AfterEach
	void tearDown() {
		journal = null;
	}

	@Test
	void givenValidInputs_whenJournalCreated_thenFieldsAreInitialized() {
		assertNotNull(journal.getId());
		assertEquals("Nature", journal.getTitle());
		assertEquals("John Smith", journal.getAuthor());
		assertFalse(journal.isBorrowed());
	}

	@Test
	void givenEmptyTitle_whenCreateJournal_thenThrowException() {
		assertThrows(IllegalArgumentException.class, () ->
			new Journal("", "Author")
		);
	}

	@Test
	void givenNullTitle_whenCreateJournal_thenThrowException() {
		assertThrows(IllegalArgumentException.class, () ->
			new Journal(null, "Author")
		);
	}

	@Test
	void givenBlankTitle_whenCreateJournal_thenThrowException() {
		assertThrows(IllegalArgumentException.class, () ->
			new Journal("   ", "Author")
		);
	}

	@Test
	void givenEmptyAuthor_whenCreateJournal_thenThrowException() {
		assertThrows(IllegalArgumentException.class, () ->
			new Journal("Title", "")
		);
	}

	@Test
	void givenNullAuthor_whenCreateJournal_thenThrowException() {
		assertThrows(IllegalArgumentException.class, () ->
			new Journal("Title", null)
		);
	}

	@Test
	void givenBlankAuthor_whenCreateJournal_thenThrowException() {
		assertThrows(IllegalArgumentException.class, () ->
			new Journal("Title", "   ")
		);
	}

	@Test
	void givenValidTitle_whenSetTitle_thenTitleIsUpdated() {
		journal.setTitle("Science Journal");
		assertEquals("Science Journal", journal.getTitle());
	}

	@Test
	void givenNullTitle_whenSetTitle_thenThrowException() {
		assertThrows(IllegalArgumentException.class, () ->
			journal.setTitle(null)
		);
	}

	@Test
	void givenEmptyTitle_whenSetTitle_thenThrowException() {
		assertThrows(IllegalArgumentException.class, () ->
			journal.setTitle("")
		);
	}

	@Test
	void givenBlankTitle_whenSetTitle_thenThrowException() {
		assertThrows(IllegalArgumentException.class, () ->
			journal.setTitle("   ")
		);
	}

	@Test
	void givenValidAuthor_whenSetAuthor_thenAuthorIsUpdated() {
		journal.setAuthor("Jane Doe");
		assertEquals("Jane Doe", journal.getAuthor());
	}

	@Test
	void givenNullAuthor_whenSetAuthor_thenThrowException() {
		assertThrows(IllegalArgumentException.class, () ->
			journal.setAuthor(null)
		);
	}

	@Test
	void givenEmptyAuthor_whenSetAuthor_thenThrowException() {
		assertThrows(IllegalArgumentException.class, () ->
			journal.setAuthor("")
		);
	}

	@Test
	void givenBlankAuthor_whenSetAuthor_thenThrowException() {
		assertThrows(IllegalArgumentException.class, () ->
			journal.setAuthor("   ")
		);
	}

	@Test
	void givenNewJournal_whenIsAvailable_thenReturnTrue() {
		assertTrue(journal.isAvailable());
	}

	@Test
	void givenBorrowedJournal_whenIsAvailable_thenReturnFalse() {
		journal.setBorrowed(true);
		assertFalse(journal.isAvailable());
	}

	@Test
	void givenAvailableJournal_whenDecrementAvailableCopies_thenJournalIsBorrowed() {
		journal.decrementAvailableCopies();
		assertTrue(journal.isBorrowed());
		assertFalse(journal.isAvailable());
	}

	@Test
	void givenBorrowedJournal_whenDecrementAvailableCopies_thenThrowException() {
		journal.setBorrowed(true);
		assertThrows(IllegalStateException.class, () ->
			journal.decrementAvailableCopies()
		);
	}

	@Test
	void givenBorrowedJournal_whenIncrementAvailableCopies_thenJournalIsReturned() {
		journal.setBorrowed(true);
		journal.incrementAvailableCopies();
		assertFalse(journal.isBorrowed());
		assertTrue(journal.isAvailable());
	}

	@Test
	void givenAvailableJournal_whenIncrementAvailableCopies_thenThrowException() {
		assertThrows(IllegalStateException.class, () ->
			journal.incrementAvailableCopies()
		);
	}

	@Test
	void givenJournal_whenSetBorrowed_thenBorrowedStatusChanges() {
		assertFalse(journal.isBorrowed());
		journal.setBorrowed(true);
		assertTrue(journal.isBorrowed());
		journal.setBorrowed(false);
		assertFalse(journal.isBorrowed());
	}

	@Test
	void givenJournal_whenToString_thenReturnsFormattedString() {
		String result = journal.toString();
		assertNotNull(result);
		assertTrue(result.contains("Nature"));
		assertTrue(result.contains("John Smith"));
		assertTrue(result.contains("No"));
	}

	@Test
	void givenBorrowedJournal_whenToString_thenShowsBorrowedStatus() {
		journal.setBorrowed(true);
		String result = journal.toString();
		assertTrue(result.contains("Yes"));
	}

	@Test
	void givenMultipleJournals_whenCreated_thenEachHasUniqueId() {
		Journal journal1 = new Journal("Journal 1", "Author 1");
		Journal journal2 = new Journal("Journal 2", "Author 2");
		Journal journal3 = new Journal("Journal 3", "Author 3");

		assertNotNull(journal1.getId());
		assertNotNull(journal2.getId());
		assertNotNull(journal3.getId());
		assertFalse(journal1.getId().equals(journal2.getId()));
		assertFalse(journal2.getId().equals(journal3.getId()));
		assertFalse(journal1.getId().equals(journal3.getId()));
	}
}
