package lms.application;

import java.util.List;
import java.util.UUID;

import lms.application.search.SearchStrategy;
import lms.domain.Book;
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
 * encapsulates its own validation rules.</li>
 * <li>Interacting with a {@link BookRepository} to persist or retrieve
 * books.</li>
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
 * @author Majd
 * @version 2.0
 */

public class BookService {

	private final BookRepository bookRepo;
	private final UserRepository userRepo;

	private BookService() {
		bookRepo = null;
		userRepo = null;
	}

	public BookService(BookRepository bookRepo, UserRepository userService) {
		this.bookRepo = bookRepo;
		this.userRepo = userService;
	}

	/**
	 * Adds a new book to the system, if the requesting user has admin privileges.
	 */
	public Book addBook(UserDTO userDTO, String title, String author, String isbn, String publisher,
			int publicationYear, String category, int totalCopies, String language, String shelfLocation)
			throws PermissionDeniedException {

		AuthorizationService.ensureAdmin(userDTO);

		Book book = new Book(title, author, isbn, publisher, publicationYear, category, totalCopies, language,
				shelfLocation);

		boolean added = bookRepo.addBook(book);
		if (!added) {
			throw new IllegalStateException("Book with ISBN already exists: " + isbn);
		}

		return book;
	}

	public List<Book> getAllBooks() {
		return bookRepo.getAllBooks();
	}

	public List<Book> searchBooks(SearchStrategy<Book> strategy, String searchTerm) {
		if (strategy == null) {
			throw new IllegalArgumentException("Search strategy cannot be null");
		}
		return strategy.execute(bookRepo.getAllBooks(), searchTerm);
	}

	public Book getBookById(UUID bookId) {
		return bookRepo.getBookById(bookId).orElse(null);
	}

	/**
	 * Retrieves a book by a partial ID match (substring).
	 */
	public Book getBookBySubId(String subId) {
		List<Book> allBooks = getAllBooks();
		List<Book> matches = allBooks.stream().filter(book -> book.getId().toString().startsWith(subId)).toList();

		if (matches.isEmpty()) {
			throw new IllegalArgumentException("No book found with ID starting with: " + subId);
		}

		if (matches.size() > 1) {
			throw new IllegalArgumentException(
					"Multiple books found with ID starting with: " + subId + ". Please provide more characters.");
		}

		return matches.get(0);
	}

	private boolean isAvailableBook(UUID bookID) {
		return bookRepo.getBookById(bookID).get().getAvailableCopies() > 0;
	}

	public boolean isValidBook(UUID bookID) {
		return this.bookRepo.getBookById(bookID).isPresent();
	}

	public boolean updateBook(UserDTO userDTO, UUID bookId, String title, String author, String isbn, String publisher,
			Integer publicationYear, String category, Integer totalCopies, String language, String shelfLocation)
			throws PermissionDeniedException {

		AuthorizationService.ensureAdmin(userDTO);

		Book book = bookRepo.getBookById(bookId)
				.orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + bookId));

		if (title != null)
			book.setTitle(title);

		if (author != null)
			book.setAuthor(author);

		if (isbn != null)
			book.setIsbn(isbn);

		if (publisher != null)
			book.setPublisher(publisher);

		if (publicationYear != null)
			book.setPublicationYear(publicationYear);

		if (category != null)
			book.setCategory(category);

		if (totalCopies != null) {
			if (totalCopies < book.getAvailableCopies()) {
				throw new IllegalArgumentException("New total copies (" + totalCopies
						+ ") cannot be less than available copies (" + book.getAvailableCopies() + ")");
			}
			book.setTotalCopies(totalCopies);
		}

		if (language != null)
			book.setLanguage(language);

		if (shelfLocation != null)
			book.setShelfLocation(shelfLocation);

		return bookRepo.updateBook(book);
	}

	public boolean deleteBook(UserDTO userDTO, UUID bookId) throws PermissionDeniedException {

		AuthorizationService.ensureAdmin(userDTO);

		Book book = bookRepo.getBookById(bookId)
				.orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + bookId));

		return bookRepo.deleteBook(bookId);
	}
}
