package lms.application.search;

import java.util.List;
import java.util.UUID;
import lms.domain.Book;

/**
 * Strategy for searching books by ID.
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class SearchByIdStrategy extends BookSearchStrategy {
    
    public SearchByIdStrategy() {
        super("Search by ID");
    }
    
    @Override
    public List<Book> execute(List<Book> books, String searchTerm) {
        String lowerSearchTerm = searchTerm.toLowerCase().trim();
        
        try {
            UUID searchId = UUID.fromString(searchTerm);
            List<Book> exactMatch = books.stream()
                    .filter(book -> book.getId().equals(searchId))
                    .toList();
            if (!exactMatch.isEmpty()) {
                return exactMatch;
            }
        } catch (IllegalArgumentException e) {}
        
        return books.stream()
                .filter(book -> book.getId().toString().toLowerCase().contains(lowerSearchTerm))
                .toList();
    }
}
