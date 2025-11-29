package lms.application.search;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lms.domain.Book;

class SearchByAuthorStrategyTest {

	private SearchByAuthorStrategy strategy;
	private List<Book> books;

	@BeforeEach
	void setup() {
		strategy = new SearchByAuthorStrategy();

		books = List.of(
				new Book("Clean Code", "Robert Martin", "123456", "Prentice Hall", 2008, "Programming", 5, "English",
						"A1"),

				new Book("Effective Java", "Joshua Bloch", "654321", "Addison-Wesley", 2018, "Programming", 3,
						"English", "A2"),

				new Book("Algorithms", "Robert Sedgewick", "987654", "Pearson", 2011, "Computer Science", 4, "English",
						"B1"));
	}

	@Test
	void shouldReturnBooksMatchingAuthor_caseInsensitive() {
		List<Book> result = strategy.execute(books, "robert");

 		assertEquals(2, result.size());
		assertTrue(result.stream().anyMatch(b -> b.getAuthor().equals("Robert Martin")));
		assertTrue(result.stream().anyMatch(b -> b.getAuthor().equals("Robert Sedgewick")));
	}

	@Test
	void shouldReturnExactMatch() {
		List<Book> result = strategy.execute(books, "Joshua Bloch");

		assertEquals(1, result.size());
		assertEquals("Joshua Bloch", result.get(0).getAuthor());
	}

	@Test
	void shouldReturnEmptyListWhenNoMatch() {
		List<Book> result = strategy.execute(books, "Unknown Author");

		assertTrue(result.isEmpty());
	}

	@Test
	void shouldHandlePartialMatch() {
		List<Book> result = strategy.execute(books, "blo");

		assertEquals(1, result.size());
		assertEquals("Joshua Bloch", result.get(0).getAuthor());
	}

	@Test
	void shouldHandleEmptySearchTerm() {
		List<Book> result = strategy.execute(books, "");

 		assertEquals(3, result.size());
	}
}
