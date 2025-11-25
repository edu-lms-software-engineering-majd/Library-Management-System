package lms.application.search;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lms.domain.CD;

class SearchCDByIdStrategyTest {

	private SearchCDByIdStrategy strategy;

	private CD cd1;
	private CD cd2;
	private CD cd3;

	@BeforeEach
	void setUp() {
		strategy = new SearchCDByIdStrategy();

		cd1 = new CD("A", "Artist1");
		cd2 = new CD("B", "Artist2");
		cd3 = new CD("C", "Artist3");
	}

	@Test
	void shouldReturnSingleMatch() {
		List<CD> cds = List.of(cd1, cd2, cd3);

		String prefix = cd1.getId().toString().substring(0, 6);

		List<CD> result = strategy.execute(cds, prefix);

		assertEquals(1, result.size());
		assertEquals(cd1, result.get(0));
	}

	@Test
	void shouldReturnEmptyWhenNoMatch() {
		List<CD> cds = List.of(cd1, cd2, cd3);

		List<CD> result = strategy.execute(cds, "XYZ");

		assertTrue(result.isEmpty());
	}

	@Test
	void shouldThrowWhenMultipleMatches() {

		CD fake1 = new CD("X1", "Ar1") {
			@Override
			public UUID getId() {
				return UUID.fromString("aaaaaaaa-0000-0000-0000-000000000001");
			}
		};

		CD fake2 = new CD("X2", "Ar2") {
			@Override
			public UUID getId() {
				return UUID.fromString("aaaaaaaa-0000-0000-0000-000000000002");
			}
		};

		List<CD> cds = List.of(fake1, fake2);

		assertThrows(IllegalArgumentException.class, () -> strategy.execute(cds, "aaaaaa"));
	}

	@Test
	void shouldHandleEmptySearchTerm() {
		List<CD> cds = List.of(cd1, cd2, cd3);

		List<CD> result = strategy.execute(cds, "");

		assertEquals(3, result.size());
	}

	@Test
	void shouldHandleExactFullIdMatch() {
		List<CD> cds = List.of(cd1, cd2, cd3);

		List<CD> result = strategy.execute(cds, cd2.getId().toString());

		assertEquals(1, result.size());
		assertEquals(cd2, result.get(0));
	}
}
