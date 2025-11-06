package lms.application;

import java.util.List;
import java.util.UUID;

import lms.domain.Book;
import lms.domain.BookRepo;
import lms.domain.BookRepository;
import lms.domain.UserRepository;
import lms.domain.exception.PermissionDeniedException;

/**
 * Application service for coordinating book management use cases.
 *
 * <p>
 * This service acts as an orchestrator between the user-facing layer and the
 * domain/persistence layers. It is responsible for:
 * </p>
 * <ul>
 * <li>Enforcing authorization rules (e.g., only admins can add books).</li>
 * <li>Delegating book creation to the {@link Book} domain entity, which
 * encapsulates its own validation rules.</li> <<<<<<< HEAD
 * <li>Interacting with a {@link BookRepository} to persist or retrieve
 * books.</li> ||||||| 7160386
 * <li>Interacting with a {@link BookRepo} to persist or retrieve books.</li>
 * =======
 * <li>Interacting with a {@link BookRepository} to persist or retrieve
 * books.</li> >>>>>>> ahmad-salameh
 * </ul>
 *
 * <p>
 * The service does <b>not</b> contain core validation logic for book fields.
 * Such domain-specific rules (e.g., valid ISBN, non-negative copies) are
 * enforced directly inside the {@link Book} entity itself.
 * </p>
 *
 * <p>
 * This design ensures a clear separation of concerns: <i>services coordinate,
 * entities validate themselves, repositories persist</i>.
 * </p>
 *
 * @author Majd Awwad
 * @version 2.0
 */

public class BookService {

	private final BookRepository bookRepo;
	private final UserRepository userRepo;

	private BookService() {
		// Prevent instantiation without dependencies
		bookRepo = null;
		userRepo = null;
	}

	/**
	 * Creates a new {@code BookService} with the given repository.
	 *
	 * @param bookRepo the repository used for persisting and retrieving books
	 */

	public BookService(BookRepository bookRepo, UserRepository userService) {
		this.bookRepo = bookRepo;
		this.userRepo = userService;
	}

	/**
	 * Adds a new book to the system, if the requesting user has admin privileges.
	 *
	 * <p>
	 * This method performs the following steps:
	 * </p>
	 * <ol>
	 * <li>Verifies that the given user is an administrator.</li>
	 * <li>Constructs a {@link Book}, which performs its own validation.</li>
	 * <li>Attempts to persist the book using {@link BookRepository}.</li>
	 * </ol>
	 *
	 * @param userDTO         the user attempting the action (must be admin)
	 * @param title           book title
	 * @param author          book author
	 * @param isbn            ISBN number (must be unique)
	 * @param publisher       publisher name
	 * @param publicationYear year of publication
	 * @param category        category/genre
	 * @param totalCopies     total number of copies in stock
	 * @param language        language of the book
	 * @param shelfLocation   physical shelf location in the library
	 * @return the newly created {@link Book}
	 * @throws PermissionDeniedException if the user is not an admin
	 * @throws IllegalArgumentException  if {@link Book} validation fails
	 * @throws IllegalStateException     if a book with the same ISBN already exists
	 */

	public Book addBook(UserDTO userDTO, String title, String author, String isbn, String publisher,
			int publicationYear, String category, int totalCopies, String language, String shelfLocation)
			throws PermissionDeniedException, IllegalStateException, IllegalArgumentException {

		AuthorizationService.ensureAdmin(userDTO);

		Book book = new Book(title, author, isbn, publisher, publicationYear, category, totalCopies, language,
				shelfLocation);

		boolean added = bookRepo.addBook(book);
		if (!added) {
			throw new IllegalStateException("Book with ISBN already exists: " + isbn);
		}

		return book;
	}

	/**
	 * Retrieves all books currently in the repository.
	 *
	 * @return a list of all {@link Book} entities
	 */
	public List<Book> getAllBooks() {
		return bookRepo.getAllBooks();
	}

	/**
	 * Searches/filters books using the specified search strategy.
	 * 
	 * <p>
	 * This method implements the Strategy pattern, allowing different search
	 * algorithms to be applied at runtime without modifying the service code.
	 * This follows the Open/Closed Principle - open for extension, closed for modification.
	 * </p>
	 *
	 * @param strategy the search strategy to apply
	 * @param searchTerm the search term or criteria
	 * @return list of books matching the search criteria
	 */
	public List<Book> searchBooks(lms.application.search.SearchStrategy<Book> strategy, String searchTerm) {
		if (strategy == null) {
			throw new IllegalArgumentException("Search strategy cannot be null");
		}
		return strategy.execute(bookRepo.getAllBooks(), searchTerm);
	}

	/**
	 * Retrieves a book by its ID.
	 *
	 * @param bookId the UUID of the book
	 * @return the Book if found, null otherwise
	 */
	public Book getBookById(UUID bookId) {
		return bookRepo.getBookById(bookId).orElse(null);
	}

	private boolean isAvailableBook(UUID bookID) {

		return bookRepo.getBookById(bookID).get().getAvailableCopies() > 0;
	}

	public boolean isValidBook(UUID bookID) {

		return this.bookRepo.getBookById(bookID).isPresent();
	}
}