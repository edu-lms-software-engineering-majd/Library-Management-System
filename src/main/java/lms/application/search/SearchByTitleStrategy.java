package lms.application.search;

import java.util.List;
import lms.domain.Book;

/**
 * Strategy for searching books by title (case-insensitive partial match).
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class SearchByTitleStrategy extends BookSearchStrategy {
    
    public SearchByTitleStrategy() {
        super("Search by Title");
    }
    
    @Override
    public List<Book> execute(List<Book> books, String searchTerm) {
        String lowerSearchTerm = searchTerm.toLowerCase();
        return books.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(lowerSearchTerm))
                .toList();
    }
}
