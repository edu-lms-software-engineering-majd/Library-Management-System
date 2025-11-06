package lms.application.search;

import java.util.List;
import lms.domain.Book;

/**
 * Abstract base class for Book search strategies.
 * 
 * <p>
 * This class provides common functionality for all book search strategies
 * and follows the Template Method pattern in conjunction with Strategy pattern.
 * </p>
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public abstract class BookSearchStrategy implements SearchStrategy<Book> {
    
    private final String description;
    
    protected BookSearchStrategy(String description) {
        this.description = description;
    }
    
    @Override
    public String getDescription() {
        return description;
    }
}
