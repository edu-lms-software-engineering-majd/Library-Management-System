package lms.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lms.domain.Book;
import lms.domain.BookRepository;

/**
 * In-memory implementation of {@link BookRepository}.
 *
 * <p>
 * Stores {@link Book} objects in a static list for lightweight usage,
 * testing, or prototyping. This repository is NOT thread-safe and should
 * not be used in production.
 * </p>
 *
 * <h3>Key Features:</h3>
 * <ul>
 *   <li>Store, update, delete books in memory</li>
 *   <li>Search by ID or ISBN</li>
 *   <li>Prevent duplicate ISBN values</li>
 *   <li>Validation for all input records</li>
 * </ul>
 *
 * @author Ahmad Salameh
 * @version 2.1
 */
public final class StaticBookRepository implements BookRepository {

    /** Singleton instance */
    private static final StaticBookRepository INSTANCE = new StaticBookRepository();

    /** Internal in-memory storage */
    private static final List<Book> books = new ArrayList<>();

    /** Private constructor for Singleton */
    private StaticBookRepository() {}

    /**
     * Returns the Singleton instance of the repository.
     *
     * @return the shared {@link StaticBookRepository} instance
     */
    public static StaticBookRepository getInstance() {
        return INSTANCE;
    }
    
    static {
		books.add(new Book("Clean Code", "Robert C. Martin", "9780132350884", "Prentice Hall", 2008,
				"Software Engineering", 5, "English", "Shelf A1"));
		books.add(new Book("Effective Java", "Joshua Bloch", "9780134685991", "Addison-Wesley", 2018, "Programming", 3,
				"English", "Shelf B2"));
		books.add(new Book("Design Patterns: Elements of Reusable Object-Oriented Software",
				"Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides", "9780201633610", "Addison-Wesley", 1994,
				"Software Design", 2, "English", "Shelf C3"));
	}

    // ============================================================
    // Validation
    // ============================================================

    /**
     * Ensures the provided book is valid and ready for persistence.
     *
     * @param book the book object to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validate(Book book) {
        if (book == null)
            throw new IllegalArgumentException("Book cannot be null.");

        if (book.getTitle() == null || book.getTitle().isBlank())
            throw new IllegalArgumentException("Book title cannot be empty.");

        if (book.getIsbn() == null || book.getIsbn().isBlank())
            throw new IllegalArgumentException("ISBN cannot be empty.");

        if (book.getTotalCopies() < 0)
            throw new IllegalArgumentException("Total copies cannot be negative.");
    }

    /**
     * Checks whether an ISBN is already used by any other book.
     *
     * @param isbn the ISBN to check
     * @return true if exists, false otherwise
     */
    private boolean isbnExists(String isbn) {
        return books.stream()
				.anyMatch(b -> b.getIsbn().equalsIgnoreCase(isbn));
    }

    // ============================================================
    // CRUD Operations
    // ============================================================

    @Override
    public boolean addBook(Book book) {
        validate(book);

        if (isbnExists(book.getIsbn())) {
            throw new IllegalArgumentException(
                "A book with ISBN '" + book.getIsbn() + "' already exists."
            );
        }

        books.add(book);
        return true;
    }

    @Override
    public Optional<Book> getBookById(UUID bookId) {
        if (bookId == null)
            return Optional.empty();

        return books.stream()
                .filter(b -> b.getId().equals(bookId))
                .findFirst();
    }

    @Override
    public Optional<Book> getBookByIsbn(String isbn) {
        if (isbn == null)
            return Optional.empty();

        return books.stream()
                .filter(b -> b.getIsbn().equalsIgnoreCase(isbn))
                .findFirst();
    }

    @Override
    public boolean updateBook(Book updatedBook) {
        validate(updatedBook);

        for (int i = 0; i < books.size(); i++) {
            Book current = books.get(i);

            if (current.getId().equals(updatedBook.getId())) {

                // Prevent using another existing book's ISBN
                boolean isbnChanged = !current.getIsbn().equalsIgnoreCase(updatedBook.getIsbn());
                boolean isbnConflict = isbnChanged && isbnExists(updatedBook.getIsbn());

                if (isbnConflict) {
                    throw new IllegalArgumentException(
                        "Cannot update book — ISBN '" +
                        updatedBook.getIsbn() +
                        "' already belongs to another book."
                    );
                }

                books.set(i, updatedBook);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean deleteBook(UUID bookId) {
        if (bookId == null)
            throw new IllegalArgumentException("Book ID cannot be null.");

        return books.removeIf(b -> b.getId().equals(bookId));
    }

    @Override
    public List<Book> getAllBooks() {
        return List.copyOf(books);
    }
}
