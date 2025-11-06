package lms.application.search;

import java.util.List;
import lms.domain.Book;

/**
 * Strategy for searching books by author (case-insensitive partial match).
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class SearchByAuthorStrategy extends BookSearchStrategy {
    
    public SearchByAuthorStrategy() {
        super("Search by Author");
    }
    
    @Override
    public List<Book> execute(List<Book> books, String searchTerm) {
        String lowerSearchTerm = searchTerm.toLowerCase();
        return books.stream()
                .filter(book -> book.getAuthor().toLowerCase().contains(lowerSearchTerm))
                .toList();
    }
}
