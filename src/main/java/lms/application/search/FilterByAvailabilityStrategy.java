package lms.application.search;

import java.util.List;
import lms.domain.Book;

/**
 * Strategy for filtering books by availability status.
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class FilterByAvailabilityStrategy extends BookSearchStrategy {
    
    public FilterByAvailabilityStrategy() {
        super("Filter by Availability");
    }
    
    @Override
    public List<Book> execute(List<Book> books, String searchTerm) {
        boolean availableOnly = searchTerm.equalsIgnoreCase("y") || 
                                searchTerm.equalsIgnoreCase("yes") ||
                                searchTerm.equalsIgnoreCase("true") ||
                                searchTerm.equals("1");
        
        if (availableOnly) {
            return books.stream()
                    .filter(book -> book.getAvailableCopies() > 0)
                    .toList();
        }
        return books;
    }
}
