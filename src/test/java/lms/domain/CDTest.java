package lms.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CDTest {

	CD cd;

	@BeforeEach
	void setUp() {
		cd = new CD("Greatest Hits", "The Beatles");
	}

	@AfterEach
	void tearDown() {
		cd = null;
	}

	@Test
	void givenValidInputs_whenCDCreated_thenFieldsAreInitialized() {
		assertNotNull(cd.getId());
		assertEquals("Greatest Hits", cd.getTitle());
		assertEquals("The Beatles", cd.getArtist());
		assertFalse(cd.isBorrowed());
	}

	@Test
	void givenEmptyTitle_whenCreateCD_thenThrowException() {
		assertThrows(IllegalArgumentException.class, () ->
			new CD("", "Artist")
		);
	}

	@Test
	void givenNullTitle_whenCreateCD_thenThrowException() {
		assertThrows(IllegalArgumentException.class, () ->
			new CD(null, "Artist")
		);
	}

	@Test
	void givenBlankTitle_whenCreateCD_thenThrowException() {
		assertThrows(IllegalArgumentException.class, () ->
			new CD("   ", "Artist")
		);
	}

	@Test
	void givenEmptyArtist_whenCreateCD_thenThrowException() {
		assertThrows(IllegalArgumentException.class, () ->
			new CD("Title", "")
		);
	}

	@Test
	void givenNullArtist_whenCreateCD_thenThrowException() {
		assertThrows(IllegalArgumentException.class, () ->
			new CD("Title", null)
		);
	}

	@Test
	void givenBlankArtist_whenCreateCD_thenThrowException() {
		assertThrows(IllegalArgumentException.class, () ->
			new CD("Title", "   ")
		);
	}

	@Test
	void givenValidTitle_whenSetTitle_thenTitleIsUpdated() {
		cd.setTitle("New Album");
		assertEquals("New Album", cd.getTitle());
	}

	@Test
	void givenNullTitle_whenSetTitle_thenThrowException() {
		assertThrows(IllegalArgumentException.class, () ->
			cd.setTitle(null)
		);
	}

	@Test
	void givenEmptyTitle_whenSetTitle_thenThrowException() {
		assertThrows(IllegalArgumentException.class, () ->
			cd.setTitle("")
		);
	}

	@Test
	void givenBlankTitle_whenSetTitle_thenThrowException() {
		assertThrows(IllegalArgumentException.class, () ->
			cd.setTitle("   ")
		);
	}

	@Test
	void givenValidArtist_whenSetArtist_thenArtistIsUpdated() {
		cd.setArtist("Led Zeppelin");
		assertEquals("Led Zeppelin", cd.getArtist());
	}

	@Test
	void givenNullArtist_whenSetArtist_thenThrowException() {
		assertThrows(IllegalArgumentException.class, () ->
			cd.setArtist(null)
		);
	}

	@Test
	void givenEmptyArtist_whenSetArtist_thenThrowException() {
		assertThrows(IllegalArgumentException.class, () ->
			cd.setArtist("")
		);
	}

	@Test
	void givenBlankArtist_whenSetArtist_thenThrowException() {
		assertThrows(IllegalArgumentException.class, () ->
			cd.setArtist("   ")
		);
	}

	@Test
	void givenNewCD_whenIsAvailable_thenReturnTrue() {
		assertTrue(cd.isAvailable());
	}

	@Test
	void givenBorrowedCD_whenIsAvailable_thenReturnFalse() {
		cd.setBorrowed(true);
		assertFalse(cd.isAvailable());
	}

	@Test
	void givenAvailableCD_whenDecrementAvailableCopies_thenCDIsBorrowed() {
		cd.decrementAvailableCopies();
		assertTrue(cd.isBorrowed());
		assertFalse(cd.isAvailable());
	}

	@Test
	void givenBorrowedCD_whenDecrementAvailableCopies_thenThrowException() {
		cd.setBorrowed(true);
		assertThrows(IllegalStateException.class, () ->
			cd.decrementAvailableCopies()
		);
	}

	@Test
	void givenBorrowedCD_whenIncrementAvailableCopies_thenCDIsReturned() {
		cd.setBorrowed(true);
		cd.incrementAvailableCopies();
		assertFalse(cd.isBorrowed());
		assertTrue(cd.isAvailable());
	}

	@Test
	void givenAvailableCD_whenIncrementAvailableCopies_thenThrowException() {
		assertThrows(IllegalStateException.class, () ->
			cd.incrementAvailableCopies()
		);
	}

	@Test
	void givenCD_whenSetBorrowed_thenBorrowedStatusChanges() {
		assertFalse(cd.isBorrowed());
		cd.setBorrowed(true);
		assertTrue(cd.isBorrowed());
		cd.setBorrowed(false);
		assertFalse(cd.isBorrowed());
	}

	@Test
	void givenCD_whenToString_thenReturnsFormattedString() {
		String result = cd.toString();
		assertNotNull(result);
		assertTrue(result.contains("Greatest Hits"));
		assertTrue(result.contains("The Beatles"));
		assertTrue(result.contains("No"));
	}

	@Test
	void givenBorrowedCD_whenToString_thenShowsBorrowedStatus() {
		cd.setBorrowed(true);
		String result = cd.toString();
		assertTrue(result.contains("Yes"));
	}

	@Test
	void givenMultipleCDs_whenCreated_thenEachHasUniqueId() {
		CD cd1 = new CD("Album 1", "Artist 1");
		CD cd2 = new CD("Album 2", "Artist 2");
		CD cd3 = new CD("Album 3", "Artist 3");

		assertNotNull(cd1.getId());
		assertNotNull(cd2.getId());
		assertNotNull(cd3.getId());
		assertFalse(cd1.getId().equals(cd2.getId()));
		assertFalse(cd2.getId().equals(cd3.getId()));
		assertFalse(cd1.getId().equals(cd3.getId()));
	}
}
