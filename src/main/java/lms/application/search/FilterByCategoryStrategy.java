package lms.application.search;

import java.util.List;
import lms.domain.Book;

/**
 * Strategy for filtering books by category/genre (case-insensitive partial match).
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class FilterByCategoryStrategy extends BookSearchStrategy {
    
    public FilterByCategoryStrategy() {
        super("Filter by Category");
    }
    
    @Override
    public List<Book> execute(List<Book> books, String searchTerm) {
        String lowerSearchTerm = searchTerm.toLowerCase();
        return books.stream()
                .filter(book -> book.getCategory().toLowerCase().contains(lowerSearchTerm))
                .toList();
    }
}
