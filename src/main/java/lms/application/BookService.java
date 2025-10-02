package lms.application;

import lms.domain.Book;
import lms.domain.BookRepo;
import lms.domain.User;
import lms.domain.exception.PermissionDeniedException;

/**
 * Application service for managing books.
 *
 * <p>Handles business logic related to book management,
 * such as adding (and potentially updating or deleting in future extensions),
 * while enforcing authorization rules.</p>
 *
 * <p>This service depends on a {@link BookRepo} implementation
 * for persistence, making it storage-agnostic (e.g. in-memory, database).</p>
 *
 * @author Majd Awwad
 * @version 1.0
 */

public class BookService {

    private final BookRepo bookRepo;

    private BookService() {
        // Prevent instantiation without dependencies
    	bookRepo = null;
    }
    
    /**
     * Creates a new {@code BookService} with the given repository.
     *
     * @param bookRepo the repository used for persisting and retrieving books
     */

    public BookService(BookRepo bookRepo) {
        this.bookRepo = bookRepo;
    }

    /**
     * Adds a new book to the system, only if the given user has admin privileges.
     *
     * @param user the {@link User} attempting to perform this action
     * @param title book title (required, non-empty)
     * @param author book author
     * @param isbn ISBN number (must be unique, required)
     * @param publisher publisher name
     * @param publicationYear year of publication
     * @param category category/genre
     * @param totalCopies total number of copies in stock
     * @param language language of the book
     * @param shelfLocation physical shelf location in the library
     * @return the newly created {@link Book}
     * @throws PermissionDeniedException if the user is not an admin
     * @throws IllegalArgumentException if validation fails (e.g. missing title or ISBN)
     * @throws IllegalStateException if a book with the same ISBN already exists
     */

    public Book addBook(
    		User user,
            String title,
            String author,
            String isbn,
            String publisher,
            int publicationYear,
            String category,
            int totalCopies,
            String language,
            String shelfLocation
    ) throws PermissionDeniedException {

        AuthorizationService.ensureAdmin(user);

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Book title cannot be empty");
        }
        if (isbn == null || isbn.isBlank()) {
            throw new IllegalArgumentException("Book ISBN cannot be empty");
        }

        Book book = new Book(title, author, isbn, publisher, publicationYear, category, totalCopies, language, shelfLocation);

        boolean added = bookRepo.addBook(book);
        if (!added) {
            throw new IllegalStateException("Book with ISBN already exists: " + isbn);
        }

        return book;
    }
}
