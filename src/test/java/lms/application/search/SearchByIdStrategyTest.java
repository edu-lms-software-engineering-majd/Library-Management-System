package lms.application.search;

import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lms.domain.Book;

class SearchByIdStrategyTest {

	private SearchByIdStrategy strategy;
	private List<Book> books;

	private Book book1;
	private Book book2;
	private Book book3;
	

	@BeforeEach
	void setUp() {
		strategy = new SearchByIdStrategy();

		book1 = new Book("Java", "Author1", "111", "Pub1", 2020, "CS", 5, "EN", "A1");
		book2 = new Book("Python", "Author2", "222", "Pub2", 2021, "CS", 3, "EN", "A2");
		book3 = new Book("C++", "Author3", "333", "Pub3", 2019, "CS", 4, "EN", "A3");

		books = List.of(book1, book2, book3);
	}

	@Test
	void shouldReturnSingleBookWhenExactIdMatches() {
		String id = book1.getId().toString();
		List<Book> result = strategy.execute(books, id);

		assertEquals(1, result.size());
		assertEquals(book1, result.get(0));
	}

	@Test
	void shouldReturnBooksWhenPrefixMatches() {
		String prefix = book1.getId().toString().substring(0, 4);
		List<Book> result = strategy.execute(books, prefix);

		assertTrue(result.contains(book1));
	}

	@Test
	void shouldReturnMultipleMatchesWhenIdsStartWithSamePrefix() {
		// Create a fake book with the same prefix
		String prefix = book1.getId().toString().substring(0, 3);

		Book fakeBook = new Book("Fake", "X", "999", "Y", 2000, "Test", 1, "EN", "Z") {
			@Override
			public UUID getId() {
				return UUID.fromString(book1.getId().toString().replaceFirst(".{3}", prefix));
			}
		};

		List<Book> list = List.of(book1, fakeBook);

		List<Book> result = strategy.execute(list, prefix);

		assertEquals(2, result.size());
	}

	@Test
	void shouldHandleEmptySearchTerm() {
		List<Book> result = strategy.execute(books, "");

		// Empty string matches ALL IDs
		assertEquals(books.size(), result.size());
	}

	@Test
	void shouldReturnEmptyListWhenNoMatch() {
		List<Book> result = strategy.execute(books, "ZZZ");

		assertTrue(result.isEmpty());
	}

	@Test
	void shouldIgnoreCase() {
		String upper = book2.getId().toString().substring(0, 4).toUpperCase();
		List<Book> result = strategy.execute(books, upper);

		assertTrue(result.contains(book2));
	}
}
