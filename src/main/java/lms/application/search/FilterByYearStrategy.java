package lms.application.search;

import java.util.List;
import lms.domain.Book;

/**
 * Strategy for filtering books by publication year.
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class FilterByYearStrategy extends BookSearchStrategy {
    
    public FilterByYearStrategy() {
        super("Filter by Publication Year");
    }
    
    @Override
    public List<Book> execute(List<Book> books, String searchTerm) {
        try {
            int year = Integer.parseInt(searchTerm);
            return books.stream()
                    .filter(book -> book.getPublicationYear() == year)
                    .toList();
        } catch (NumberFormatException e) {
           
            return List.of();
        }
    }
}
