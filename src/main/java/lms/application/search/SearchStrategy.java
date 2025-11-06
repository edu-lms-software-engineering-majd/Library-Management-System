package lms.application.search;

import java.util.List;

/**
 * Strategy interface for searching and filtering loanable items.
 * 
 * <p>
 * This interface defines the contract for different search/filter strategies
 * that can be applied to various types of loanable items (Books, CDs, Journals, etc.).
 * Implementations of this interface encapsulate specific search algorithms.
 * </p>
 * 
 * @param <T> the type of item to search (e.g., Book, CD, Journal)
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public interface SearchStrategy<T> {
    
    /**
     * Executes the search/filter operation on a list of items.
     * 
     * @param items the list of items to search through
     * @param searchTerm the search term or criteria to apply
     * @return a filtered list of items matching the search criteria
     */
    List<T> execute(List<T> items, String searchTerm);
    
    /**
     * Returns a human-readable description of this search strategy.
     * 
     * @return the name or description of the strategy
     */
    String getDescription();
}
