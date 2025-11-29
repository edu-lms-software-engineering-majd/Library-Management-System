package lms.application.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lms.domain.Book;

class SearchByTitleStrategyTest {

	private SearchByTitleStrategy strategy;
	private List<Book> books;

	@BeforeEach
	void setup() {
		strategy = new SearchByTitleStrategy();

		books = List.of(
				new Book("Clean Code", "Robert Martin", "123456", "Prentice Hall", 2008, "Programming", 5, "English",
						"A1"),

				new Book("Effective Java", "Joshua Bloch", "654321", "Addison-Wesley", 2018, "Programming", 3,
						"English", "A2"),

				new Book("Algorithms", "Robert Sedgewick", "987654", "Pearson", 2011, "Computer Science", 4, "English",
						"B1"));
	}

	@Test
	void shouldReturnBooksMatchingTitle_caseInsensitive() {
		List<Book> result = strategy.execute(books, "clean");

		assertEquals(1, result.size());
		assertEquals("Clean Code", result.get(0).getTitle());
	}

	@Test
	void shouldReturnMultipleMatches() {
		List<Book> result = strategy.execute(books, "a");

		assertEquals(3, result.size());
	}

	@Test
	void shouldReturnEmptyListWhenNoMatch() {
		List<Book> result = strategy.execute(books, "xyz123");

		assertTrue(result.isEmpty());
	}

	@Test
	void shouldMatchExactTitle() {
		List<Book> result = strategy.execute(books, "Effective Java");

		assertEquals(1, result.size());
		assertEquals("Effective Java", result.get(0).getTitle());
	}

	@Test
	void shouldHandleEmptySearchTerm() {
		List<Book> result = strategy.execute(books, "");

		assertEquals(3, result.size());
	}
}
