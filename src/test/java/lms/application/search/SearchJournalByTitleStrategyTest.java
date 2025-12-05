package lms.application.search;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lms.domain.Journal;

class SearchJournalByTitleStrategyTest {

	private SearchJournalByTitleStrategy strategy;
	private List<Journal> journals;

	@BeforeEach
	void setUp() {

		strategy = new SearchJournalByTitleStrategy();

		journals = List.of(new Journal("AI Research", "Publisher1", 5),
				new Journal("Machine Learning Advances", "Publisher2", 3),
				new Journal("Deep Learning Journal", "Publisher3", 4),
				new Journal("AI Weekly Review", "Publisher4", 2));
	}

	@Test
	 
	void shouldMatchExactTitle() {
		var result = strategy.execute(journals, "AI Research");

		assertEquals(1, result.size());
		assertEquals("AI Research", result.get(0).getTitle());
	}

	@Test
	 
	void shouldMatchPartial() {
		var result = strategy.execute(journals, "AI");

		assertEquals(2, result.size());
		assertTrue(result.stream().anyMatch(j -> j.getTitle().contains("AI")));
	}

	@Test
	 
	void shouldReturnEmptyWhenNoMatch() {
		var result = strategy.execute(journals, "Biology");

		assertTrue(result.isEmpty());
	}

	@Test
	 
	void shouldIgnoreCase() {
		var result = strategy.execute(journals, "machine");

		assertEquals(1, result.size());
		assertEquals("Machine Learning Advances", result.get(0).getTitle());
	}

	@Test
	 
	void shouldHandleEmptySearchTerm() {
		var result = strategy.execute(journals, "");

		assertEquals(journals.size(), result.size());
	}
}
