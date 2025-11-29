package lms.application.search;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lms.domain.Book;

class SearchContextTest {

	private SearchContext<Book> context;
	private List<Book> books;

	@BeforeEach
	void setUp() {
		context = new SearchContext<>();

		books = List.of(new Book("Clean Code", "Robert Martin", "123", "Pub", 2008, "Programming", 5, "EN", "A1"),
				new Book("Clean Architecture", "Robert Martin", "456", "Pub", 2017, "Programming", 4, "EN", "A2"),
				new Book("Java Fundamentals", "Mark Smith", "789", "Pub", 2015, "Programming", 3, "EN", "A3"));
	}

	@Test
	@DisplayName("Should throw when setting null strategy")
	void shouldThrowWhenSettingNullStrategy() {
		assertThrows(IllegalArgumentException.class, () -> context.setStrategy(null));
	}

	@Test
	@DisplayName("Should throw when executing search without setting strategy")
	void shouldThrowWhenExecutingWithoutStrategy() {
		assertThrows(IllegalStateException.class, () -> context.executeSearch(books, "Clean"));
	}

	@Test
	@DisplayName("Should execute search using current strategy")
	void shouldExecuteSearchCorrectly() {
		context.setStrategy(new SearchByTitleStrategy());
		var result = context.executeSearch(books, "Clean");

		assertEquals(2, result.size());
	}

	@Test
	@DisplayName("Should allow changing strategies at runtime")
	void shouldSwitchStrategies() {
		context.setStrategy(new SearchByTitleStrategy());
		var result1 = context.executeSearch(books, "Clean");

		assertEquals(2, result1.size());

		context.setStrategy(new SearchByAuthorStrategy());
		var result2 = context.executeSearch(books, "Robert");

		assertEquals(2, result2.size());
	}

	@Test
	@DisplayName("Should return description of selected strategy")
	void shouldReturnStrategyDescription() {
		context.setStrategy(new SearchByAuthorStrategy());

		assertEquals("Search by Author", context.getStrategyDescription());
	}

	@Test
	@DisplayName("Should return null description if no strategy set")
	void shouldReturnNullWhenNoStrategy() {
		assertNull(context.getStrategyDescription());
	}
}
