package lms.application.search;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SearchCriteriaTest {

	@Test
	@DisplayName("Each enum constant should create the correct strategy instance")
	void shouldCreateCorrectStrategy() {
		assertTrue(SearchCriteria.ID.createStrategy() instanceof SearchByIdStrategy);
		assertTrue(SearchCriteria.TITLE.createStrategy() instanceof SearchByTitleStrategy);
		assertTrue(SearchCriteria.AUTHOR.createStrategy() instanceof SearchByAuthorStrategy);
		assertTrue(SearchCriteria.ISBN.createStrategy() instanceof SearchByIsbnStrategy);
		assertTrue(SearchCriteria.YEAR.createStrategy() instanceof FilterByYearStrategy);
		assertTrue(SearchCriteria.CATEGORY.createStrategy() instanceof FilterByCategoryStrategy);
		assertTrue(SearchCriteria.AVAILABILITY.createStrategy() instanceof FilterByAvailabilityStrategy);
	}

	@Test
	@DisplayName("fromChoice(int) should return correct enum for valid choices")
	void shouldReturnEnumFromValidIntChoice() {
		assertEquals(SearchCriteria.ID, SearchCriteria.fromChoice(1));
		assertEquals(SearchCriteria.TITLE, SearchCriteria.fromChoice(2));
		assertEquals(SearchCriteria.AVAILABILITY, SearchCriteria.fromChoice(7));
	}

	@Test
	@DisplayName("fromChoice(int) should return null for invalid numbers")
	void shouldReturnNullForInvalidIntChoices() {
		assertNull(SearchCriteria.fromChoice(0));
		assertNull(SearchCriteria.fromChoice(8));
		assertNull(SearchCriteria.fromChoice(-5));
	}

	@Test
	@DisplayName("fromChoice(String) should parse valid numeric strings")
	void shouldReturnEnumFromValidStringChoice() {
		assertEquals(SearchCriteria.ID, SearchCriteria.fromChoice("1"));
		assertEquals(SearchCriteria.CATEGORY, SearchCriteria.fromChoice("6"));
	}

	@Test
	@DisplayName("fromChoice(String) should return null for invalid strings")
	void shouldReturnNullForInvalidStringChoice() {
		assertNull(SearchCriteria.fromChoice("abc"));
		assertNull(SearchCriteria.fromChoice(""));
		assertNull(SearchCriteria.fromChoice("100"));
		assertNull(SearchCriteria.fromChoice(null));
	}

	@Test
	@DisplayName("DisplayName and Description should not be null")
	void shouldHaveValidDisplayNameAndDescription() {
		for (SearchCriteria c : SearchCriteria.values()) {
			assertNotNull(c.getDisplayName());
			assertNotNull(c.getDescription());
		}
	}
}
