package lms.application.search;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lms.domain.CD;

class SearchCDByArtistStrategyTest {

	private SearchCDByArtistStrategy strategy;
	private CD cd1;
	private CD cd2;
	private CD cd3;

	@BeforeEach
	void setUp() {
		strategy = new SearchCDByArtistStrategy();

		cd1 = new CD("Hits 2024", "Majd Awwad");
		cd2 = new CD("Best of 90s", "Ahmad Salameh");
		cd3 = new CD("Chill Mix", "Majd Awwad"); // نفس الفنان – لازم تطلعه
	}

	@Test
	void shouldReturnMatchingCDs() {
		List<CD> cds = List.of(cd1, cd2, cd3);

		List<CD> result = strategy.execute(cds, "majd");

		// المفروض CD1 و CD3
		assertEquals(2, result.size());
		assertTrue(result.contains(cd1));
		assertTrue(result.contains(cd3));
	}

	@Test
	void shouldReturnEmptyListWhenNoMatch() {
		List<CD> cds = List.of(cd1, cd2, cd3);

		List<CD> result = strategy.execute(cds, "xyz");

		assertTrue(result.isEmpty());
	}

	@Test
	void shouldBeCaseInsensitive() {
		List<CD> cds = List.of(cd1, cd2, cd3);

		List<CD> result = strategy.execute(cds, "MAJD");

		assertEquals(2, result.size());
	}

	@Test
	void shouldHandleEmptySearchTerm() {
		List<CD> cds = List.of(cd1, cd2, cd3);

		List<CD> result = strategy.execute(cds, "");

		assertEquals(3, result.size());
	}
}
