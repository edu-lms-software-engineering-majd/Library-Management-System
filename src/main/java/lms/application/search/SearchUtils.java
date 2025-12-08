package lms.application.search;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

/**
 * Utility class providing common search operations for library items.
 *
 * <p>
 * This class offers reusable search functionality that can be applied to different
 * entity types (Books, CDs, Journals). It supports exact UUID matching, prefix matching,
 * and handles ambiguous search results.
 * </p>
 *
 * @author Majd Awwad
 * @version 1.0
 */
public class SearchUtils {

	private SearchUtils() {
		throw new UnsupportedOperationException("Utility class cannot be instantiated");
	}

	/**
	 * Performs a flexible ID-based search with multiple matching strategies.
	 *
	 * <p>
	 * Search behavior:
	 * </p>
	 * <ol>
	 * <li>If searchTerm is null or blank, returns all items</li>
	 * <li>Attempts exact UUID match first</li>
	 * <li>Falls back to prefix matching if exact match fails</li>
	 * <li>Throws exception if multiple matches are found (ambiguous)</li>
	 * </ol>
	 *
	 * @param <T> the type of items being searched
	 * @param items the list of items to search through
	 * @param searchTerm the search term (UUID or prefix)
	 * @param idExtractor function to extract UUID from an item
	 * @return list of matching items (empty if no matches)
	 * @throws IllegalArgumentException if multiple matches are found
	 */
	public static <T> List<T> searchById(List<T> items, String searchTerm, Function<T, UUID> idExtractor) {

		if (searchTerm == null || searchTerm.isBlank()) {
			return items;
		}

		String lower = searchTerm.toLowerCase().trim();

		 
		try {
			UUID exactId = UUID.fromString(searchTerm);
			List<T> exactMatch = items.stream().filter(item -> idExtractor.apply(item).equals(exactId)).toList();

			if (!exactMatch.isEmpty()) {
				return exactMatch;
			}

		} catch (IllegalArgumentException ignored) {
			 
		}

		 
		List<T> matches = items.stream()
				.filter(item -> idExtractor.apply(item).toString().toLowerCase().startsWith(lower)).toList();

		if (matches.isEmpty()) {
			return List.of();
		}

		if (matches.size() > 1) {
			throw new IllegalArgumentException("Multiple matches found for ID prefix: " + searchTerm);
		}

		return matches;
	}
}
