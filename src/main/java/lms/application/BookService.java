package lms.application;

import java.util.List;

import lms.domain.Book;
import lms.domain.BookRepo;
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
 * encapsulates its own validation rules.</li>
 * <li>Interacting with a {@link BookRepo} to persist or retrieve books.</li>
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
	 * Adds a new book to the system, if the requesting user has admin privileges.
	 *
	 * <p>
	 * This method performs the following steps:
	 * </p>
	 * <ol>
	 * <li>Verifies that the given user is an administrator.</li>
	 * <li>Constructs a {@link Book}, which performs its own validation.</li>
	 * <li>Attempts to persist the book using {@link BookRepo}.</li>
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
}
