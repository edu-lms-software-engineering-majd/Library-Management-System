package lms.application.search;

import java.util.List;

/**
 * Context class for the Search Strategy pattern.
 * 
 * <p>
 * This class maintains a reference to a {@link SearchStrategy} and delegates
 * the search/filter operation to the strategy. It allows switching between
 * different search algorithms at runtime.
 * </p>
 * 
 * <p>
 * Usage example:
 * </p>
 * <pre>
 *     SearchContext&lt;Book&gt; context = new SearchContext&lt;&gt;();
 *     context.setStrategy(new SearchByTitleStrategy());
 *     List&lt;Book&gt; results = context.executeSearch(allBooks, "Java");
 * </pre>
 * 
 * @param <T> the type of item to search
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class SearchContext<T> {
    
    private SearchStrategy<T> strategy;
    
    /**
     * Default constructor.
     */
    public SearchContext() {
    }
    
    /**
     * Constructor with initial strategy.
     * 
     * @param strategy the initial search strategy
     */
    public SearchContext(SearchStrategy<T> strategy) {
        this.strategy = strategy;
    }
    
    /**
     * Sets the search strategy to be used.
     * 
     * @param strategy the search strategy
     */
    public void setStrategy(SearchStrategy<T> strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("Strategy cannot be null");
        }
        this.strategy = strategy;
    }
    
    /**
     * Executes the search using the current strategy.
     * 
     * @param items the list of items to search
     * @param searchTerm the search term
     * @return the filtered list of items
     * @throws IllegalStateException if no strategy has been set
     */
    public List<T> executeSearch(List<T> items, String searchTerm) {
        if (strategy == null) {
            throw new IllegalStateException("No search strategy has been set");
        }
        return strategy.execute(items, searchTerm);
    }
    
    /**
     * Gets the current strategy's description.
     * 
     * @return the strategy description, or null if no strategy is set
     */
    public String getStrategyDescription() {
        return strategy != null ? strategy.getDescription() : null;
    }
}
