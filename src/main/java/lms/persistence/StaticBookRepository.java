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
 * <p>Stores books in a static list with support for CRUD operations and search by ID or ISBN.
 * Ensures unique ISBN values and validates all input. This implementation is not thread-safe
 * and intended for testing purposes.</p>
 *
 * @author Ahmad Salameh
 * @version 2.1
 */
public final class StaticBookRepository implements BookRepository {

    private static final StaticBookRepository INSTANCE = new StaticBookRepository();
    private static final List<Book> books = new ArrayList<>();

    private StaticBookRepository() {}

    /**
     * Returns the singleton instance of the repository.
     *
     * @return the shared StaticBookRepository instance
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

    /**
     * Validates a book object before persistence operations.
     *
     * @param book the book to validate
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
     * Checks if an ISBN already exists in the repository.
     *
     * @param isbn the ISBN to check
     * @return true if ISBN exists, false otherwise
     */
    private boolean isbnExists(String isbn) {
        return books.stream()
				.anyMatch(b -> b.getIsbn().equalsIgnoreCase(isbn));
    }

    /**
     * Adds a new book to the repository.
     *
     * @param book the book to add
     * @return true if added successfully
     * @throws IllegalArgumentException if book is invalid or ISBN already exists
     */
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

    /**
     * Retrieves a book by its unique identifier.
     *
     * @param bookId the book ID
     * @return an Optional containing the book if found, otherwise empty
     */
    @Override
    public Optional<Book> getBookById(UUID bookId) {
        if (bookId == null)
            return Optional.empty();

        return books.stream()
                .filter(b -> b.getId().equals(bookId))
                .findFirst();
    }

    /**
     * Retrieves a book by its ISBN.
     *
     * @param isbn the ISBN to search for
     * @return an Optional containing the book if found, otherwise empty
     */
    @Override
    public Optional<Book> getBookByIsbn(String isbn) {
        if (isbn == null)
            return Optional.empty();

        return books.stream()
                .filter(b -> b.getIsbn().equalsIgnoreCase(isbn))
                .findFirst();
    }

    /**
     * Updates an existing book in the repository.
     *
     * @param updatedBook the book with updated information
     * @return true if updated successfully, false if book not found
     * @throws IllegalArgumentException if book is invalid or ISBN conflicts with another book
     */
    @Override
    public boolean updateBook(Book updatedBook) {
        validate(updatedBook);

        for (int i = 0; i < books.size(); i++) {
            Book current = books.get(i);

            if (current.getId().equals(updatedBook.getId())) {

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

    /**
     * Deletes a book from the repository.
     *
     * @param bookId the ID of the book to delete
     * @return true if deleted successfully, false if book not found
     * @throws IllegalArgumentException if bookId is null
     */
    @Override
    public boolean deleteBook(UUID bookId) {
        if (bookId == null)
            throw new IllegalArgumentException("Book ID cannot be null.");

        return books.removeIf(b -> b.getId().equals(bookId));
    }

    /**
     * Retrieves all books in the repository.
     *
     * @return an immutable list of all books
     */
    @Override
    public List<Book> getAllBooks() {
        return List.copyOf(books);
    }
}
