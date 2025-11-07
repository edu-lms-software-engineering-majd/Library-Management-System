package lms.application.search;

import java.util.List;
import lms.domain.Book;

/**
 * Strategy for searching books by ISBN (partial match).
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class SearchByIsbnStrategy extends BookSearchStrategy {
    
    public SearchByIsbnStrategy() {
        super("Search by ISBN");
    }
    
    @Override
    public List<Book> execute(List<Book> books, String searchTerm) {
        return books.stream()
                .filter(book -> book.getIsbn().contains(searchTerm))
                .toList();
    }
}
