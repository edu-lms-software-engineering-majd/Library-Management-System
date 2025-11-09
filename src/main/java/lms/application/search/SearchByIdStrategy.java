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
        return SearchUtils.searchById(books, searchTerm, Book::getId);
    }
}
