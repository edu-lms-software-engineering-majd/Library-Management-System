package lms.application.search;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

/**
 * Utility class providing common search operations for different entity types.
 *
 * This updated version fully supports: - Exact UUID match - Prefix match -
 * Partial match - Handling empty search term - Throwing exception if multiple
 * matches found
 */
public class SearchUtils {

	private SearchUtils() {
		throw new UnsupportedOperationException("Utility class cannot be instantiated");
	}

	/**
	 * Unified ID search with strict rules:
	 *
	 * Rules: 1) searchTerm = null or blank → return all items 2) Exact UUID match →
	 * return exactly 1 result 3) Prefix match (startsWith) 4) If: - 0 matches →
	 * return empty list - 1 match → return it - > 1 match → throw
	 * IllegalArgumentException
	 */
	public static <T> List<T> searchById(List<T> items, String searchTerm, Function<T, UUID> idExtractor) {

		if (searchTerm == null || searchTerm.isBlank()) {
			return items;
		}

		String lower = searchTerm.toLowerCase().trim();

		// Try exact UUID match
		try {
			UUID exactId = UUID.fromString(searchTerm);
			List<T> exactMatch = items.stream().filter(item -> idExtractor.apply(item).equals(exactId)).toList();

			if (!exactMatch.isEmpty()) {
				return exactMatch;
			}

		} catch (IllegalArgumentException ignored) {
			// Not a full UUID → continue to partial matching
		}

		// Partial / Prefix match
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
