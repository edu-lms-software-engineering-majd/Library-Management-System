package lms.application.search;

import lms.domain.Book;

/**
 * Enumeration of available search and filter criteria for books.
 * 
 * <p>
 * This enum provides a type-safe way to select search strategies and follows
 * the Factory Method pattern to create appropriate strategy instances.
 * Each enum constant knows how to create its corresponding strategy.
 * </p>
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public enum SearchCriteria {
    
    ID("ID", "Search by unique identifier") {
        @Override
        public SearchStrategy<Book> createStrategy() {
            return new SearchByIdStrategy();
        }
    },
    
    TITLE("Title/Name", "Search by title (partial match)") {
        @Override
        public SearchStrategy<Book> createStrategy() {
            return new SearchByTitleStrategy();
        }
    },
    
    AUTHOR("Author/Artist", "Search by author name (partial match)") {
        @Override
        public SearchStrategy<Book> createStrategy() {
            return new SearchByAuthorStrategy();
        }
    },
    
    ISBN("ISBN/Catalog Number", "Search by ISBN") {
        @Override
        public SearchStrategy<Book> createStrategy() {
            return new SearchByIsbnStrategy();
        }
    },
    
    YEAR("Publication/Release Year", "Filter by publication year") {
        @Override
        public SearchStrategy<Book> createStrategy() {
            return new FilterByYearStrategy();
        }
    },
    
    CATEGORY("Category/Genre", "Filter by category or genre") {
        @Override
        public SearchStrategy<Book> createStrategy() {
            return new FilterByCategoryStrategy();
        }
    },
    
    AVAILABILITY("Availability Status", "Filter by availability") {
        @Override
        public SearchStrategy<Book> createStrategy() {
            return new FilterByAvailabilityStrategy();
        }
    };
    
    private final String displayName;
    private final String description;
    
    SearchCriteria(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    /**
     * Creates a new instance of the search strategy for this criterion.
     * 
     * @return a new search strategy instance
     */
    public abstract SearchStrategy<Book> createStrategy();
    
    /**
     * Gets the display name for this search criterion.
     * 
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Gets the description of this search criterion.
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Gets a search criterion by its ordinal position (1-based).
     * 
     * @param choice the choice number (1-based)
     * @return the corresponding SearchCriteria, or null if invalid
     */
    public static SearchCriteria fromChoice(int choice) {
        SearchCriteria[] values = values();
        if (choice < 1 || choice > values.length) {
            return null;
        }
        return values[choice - 1];
    }
    
    /**
     * Gets a search criterion by its choice string.
     * 
     * @param choice the choice string (e.g., "1", "2", etc.)
     * @return the corresponding SearchCriteria, or null if invalid
     */
    public static SearchCriteria fromChoice(String choice) {
        try {
            return fromChoice(Integer.parseInt(choice));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
