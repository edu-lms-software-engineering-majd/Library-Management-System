package lms.application.search;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

/**
 * Utility class providing common search operations for different entity types.
 * 
 * <p>
 * This class centralizes reusable search logic to reduce code duplication
 * across various search strategy implementations.
 * </p>
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class SearchUtils {
    
    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private SearchUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * Performs UUID-based search with fallback to partial matching.
     * 
     * <p>
     * This method first attempts to parse the search term as a complete UUID
     * and performs an exact match. If the search term is not a valid UUID,
     * it falls back to partial matching (case-insensitive contains).
     * </p>
     * 
     * @param <T> the type of entity being searched
     * @param items the list of items to search through
     * @param searchTerm the search term (full or partial UUID)
     * @param idExtractor function to extract UUID from an item
     * @return list of matching items
     */
    public static <T> List<T> searchById(List<T> items, String searchTerm, 
                                          Function<T, UUID> idExtractor) {
        String lowerSearchTerm = searchTerm.toLowerCase().trim();
        
        try {
            UUID searchId = UUID.fromString(searchTerm);
            List<T> exactMatch = items.stream()
                    .filter(item -> idExtractor.apply(item).equals(searchId))
                    .toList();
            if (!exactMatch.isEmpty()) {
                return exactMatch;
            }
        } catch (IllegalArgumentException e) {}
        
        return items.stream()
                .filter(item -> idExtractor.apply(item).toString()
                        .toLowerCase().contains(lowerSearchTerm))
                .toList();
    }
}
