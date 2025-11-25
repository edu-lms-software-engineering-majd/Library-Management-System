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

class CDTest {

	private CD cd;

	@BeforeEach
	void setUp() {
		cd = new CD("Thriller", "Michael Jackson", 3);
	}
	

	@AfterEach
	void tearDown() {
		cd = null;
	}

	@Test
	void givenValidParameters_whenCreateCD_thenCDIsInitializedCorrectly() {
		assertNotNull(cd);
		assertNotNull(cd.getId());
		assertEquals("Thriller", cd.getTitle());
		assertEquals("Michael Jackson", cd.getArtist());
		assertEquals(3, cd.getTotalCopies());
		assertEquals(3, cd.getAvailableCopies());
	}

	@Test
	void givenDefaultConstructor_whenCreateCD_thenCDHasOneCopy() {
		CD singleCD = new CD("Greatest Hits", "The Beatles");
		
		assertNotNull(singleCD);
		assertEquals(1, singleCD.getTotalCopies());
		assertEquals(1, singleCD.getAvailableCopies());
	}

	@Test
	void givenCDWithAvailableCopies_whenCheckIsAvailable_thenReturnTrue() {
		assertTrue(cd.isAvailable());
	}

	@Test
	void givenCDWithNoAvailableCopies_whenCheckIsAvailable_thenReturnFalse() {
		cd.setAvailableCopies(0);
		
		assertFalse(cd.isAvailable());
	}

	@Test
	void givenAvailableCopies_whenDecrementAvailableCopies_thenCopiesDecreased() {
		int initialCopies = cd.getAvailableCopies();
		
		cd.decrementAvailableCopies();
		
		assertEquals(initialCopies - 1, cd.getAvailableCopies());
	}

	@Test
	void givenMultipleCopies_whenDecrementMultipleTimes_thenCopiesDecreasedCorrectly() {
		cd.decrementAvailableCopies();
		cd.decrementAvailableCopies();
		
		assertEquals(1, cd.getAvailableCopies());
	}

	@Test
	void givenNoCopiesAvailable_whenDecrementAvailableCopies_thenThrowIllegalStateException() {
		cd.setAvailableCopies(0);
		
		assertThrows(IllegalStateException.class, () -> {
			cd.decrementAvailableCopies();
		});
	}

	@Test
	void givenDecrementedCopies_whenIncrementAvailableCopies_thenCopiesIncreased() {
		cd.decrementAvailableCopies();
		int currentCopies = cd.getAvailableCopies();
		
		cd.incrementAvailableCopies();
		
		assertEquals(currentCopies + 1, cd.getAvailableCopies());
	}

	@Test
	void givenAllCopiesAvailable_whenIncrementAvailableCopies_thenThrowIllegalStateException() {
		assertThrows(IllegalStateException.class, () -> {
			cd.incrementAvailableCopies();
		});
	}

	@Test
	void givenCD_whenGetId_thenReturnNonNullUUID() {
		UUID id = cd.getId();
		
		assertNotNull(id);
	}

	@Test
	void givenMultipleCDs_whenCreate_thenEachHasUniqueId() {
		CD cd1 = new CD("Album 1", "Artist 1", 1);
		CD cd2 = new CD("Album 2", "Artist 2", 2);
		CD cd3 = new CD("Album 3", "Artist 3", 3);
		
		assertFalse(cd1.getId().equals(cd2.getId()));
		assertFalse(cd2.getId().equals(cd3.getId()));
		assertFalse(cd1.getId().equals(cd3.getId()));
	}

	@Test
	void givenNewTitle_whenSetTitle_thenTitleIsUpdated() {
		String newTitle = "Hello";
		cd.setTitle(newTitle);
		
		assertEquals(newTitle, cd.getTitle());
	}

	@Test
	void givenNewArtist_whenSetArtist_thenArtistIsUpdated() {
		String newArtist = "Majd";
		cd.setArtist(newArtist);
		
		assertEquals(newArtist, cd.getArtist());
	}

	@Test
	void givenNewTotalCopies_whenSetTotalCopies_thenTotalCopiesIsUpdated() {
		int newTotal = 5;
		cd.setTotalCopies(newTotal);
		
		assertEquals(newTotal, cd.getTotalCopies());
	}

	@Test
	void givenNewAvailableCopies_whenSetAvailableCopies_thenAvailableCopiesIsUpdated() {
		int newAvailable = 2;
		cd.setAvailableCopies(newAvailable);
		
		assertEquals(newAvailable, cd.getAvailableCopies());
	}

	@Test
	
	void givenCDWithAllCopiesAvailable_whenCheckIsBorrowed_thenReturnFalse() {
		assertFalse(cd.isBorrowed());
	}

	@Test
	void givenCDWithSomeCopiesBorrowed_whenCheckIsBorrowed_thenReturnTrue() {
		cd.decrementAvailableCopies();
		
		assertTrue(cd.isBorrowed());
	}

	@Test
	void givenCDWithAllCopiesBorrowed_whenCheckIsBorrowed_thenReturnTrue() {
		cd.setAvailableCopies(0);
		
		assertTrue(cd.isBorrowed());
	}

	@Test
	void givenCD_whenToString_thenReturnsFormattedString() {
		String result = cd.toString();
		
		assertNotNull(result);
		assertTrue(result.contains("Thriller"));
		assertTrue(result.contains("Michael Jackson"));
		assertTrue(result.contains("3/3"));
	}

	@Test
	void givenBorrowedCD_whenToString_thenShowsCorrectCopiesStatus() {
		cd.decrementAvailableCopies();
		String result = cd.toString();
		
		assertTrue(result.contains("2/3"));
	}
}
