package lms.application;

import java.util.List;
import java.util.UUID;

import lms.application.search.SearchStrategy;
import lms.domain.Book;
import lms.domain.BookRepository;
import lms.domain.exception.PermissionDeniedException;

/**
 * Application-level service responsible for coordinating book-related use
 * cases.
 *
 * <p>
 * This service acts as the orchestrator between the presentation/UI layer and
 * the domain + persistence layers. It handles:
 * </p>
 *
 * <ul>
 * <li>Authorization (admin-only operations)</li>
 * <li>Delegating validation rules to the {@link Book} entity itself</li>
 * <li>Communicating with the {@link BookRepository} for persistence</li>
 * </ul>
 *
 * <p>
 * Following clean architecture principles: <i>Services coordinate, domain
 * entities validate, repositories persist.</i>
 * </p>
 *
 * @author Majd
 * @version 2.1
 */
public class BookService {

	private final BookRepository bookRepo;

	/**
	 * Constructs a {@code BookService} with a required {@link BookRepository}.
	 *
	 * @param bookRepo the repository used to store and retrieve books
	 * @throws IllegalArgumentException if repository is null
	 */
	public BookService(BookRepository bookRepo) {
		if (bookRepo == null)
			throw new IllegalArgumentException("BookRepository cannot be null");
		this.bookRepo = bookRepo;
	}

	
	/**
	 * Creates and persists a new book if the user has admin privileges.
	 *
	 * @throws PermissionDeniedException if the user is not an admin
	 */
	public Book addBook(UserDTO userDTO, String title, String author, String isbn, String publisher,
			int publicationYear, String category, int totalCopies, String language, String shelfLocation)
			throws PermissionDeniedException {

		AuthorizationService.ensureAdmin(userDTO);

		Book book = new Book(title, author, isbn, publisher, publicationYear, category, totalCopies, language,
				shelfLocation);

		boolean added = bookRepo.addBook(book);
		if (!added)
			throw new IllegalStateException("Book with ISBN already exists: " + isbn);

		return book;
	}

	/** Returns all books in the system. */
	public List<Book> getAllBooks() {
		return bookRepo.getAllBooks();
	}

	/** Executes a search operation using a provided strategy. */
	public List<Book> searchBooks(SearchStrategy<Book> strategy, String searchTerm) {
		if (strategy == null)
			throw new IllegalArgumentException("Search strategy cannot be null");

		return strategy.execute(bookRepo.getAllBooks(), searchTerm);
	}

	/** Retrieves a book by ID, or null if nonexistent. */
	public Book getBookById(UUID bookId) {
		if (bookId == null)
			throw new IllegalArgumentException("Book ID cannot be null");

		return bookRepo.getBookById(bookId).orElse(null);
	}

	/**
	 * Retrieves a book using an ID prefix (substring).
	 *
	 * @throws IllegalArgumentException if no match or multiple matches exist
	 */
	public Book getBookBySubId(String subId) {
		List<Book> matches = bookRepo.getAllBooks().stream().filter(b -> b.getId().toString().startsWith(subId))
				.toList();

		if (matches.isEmpty())
			throw new IllegalArgumentException("No book found with ID starting with: " + subId);

		if (matches.size() > 1)
			throw new IllegalArgumentException(
					"Multiple books found with ID starting with: " + subId + ". Please provide more characters.");

		return matches.get(0);
	}

	/** Checks if the book exists and has available copies. */
	public boolean isAvailableBook(UUID bookId) {
		return bookRepo.getBookById(bookId).map(b -> b.getAvailableCopies() > 0).orElse(false);
	}

	/** Checks whether a book exists. */
	public boolean isValidBook(UUID bookId) {
		return bookRepo.getBookById(bookId).isPresent();
	}

	/**
	 * Updates an existing book's fields (admin-only).
	 *
	 * @throws PermissionDeniedException if user is not admin
	 */
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
			if (totalCopies < book.getAvailableCopies())
				throw new IllegalArgumentException("New total copies (" + totalCopies
						+ ") cannot be less than available copies (" + book.getAvailableCopies() + ")");
			book.setTotalCopies(totalCopies);
		}

		if (language != null)
			book.setLanguage(language);
		if (shelfLocation != null)
			book.setShelfLocation(shelfLocation);

		return bookRepo.updateBook(book);
	}

	/**
	 * Deletes a book (admin-only).
	 *
	 * @throws PermissionDeniedException if user is not admin
	 */
	public boolean deleteBook(UserDTO userDTO, UUID bookId) throws PermissionDeniedException {

		AuthorizationService.ensureAdmin(userDTO);

		bookRepo.getBookById(bookId)
				.orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + bookId));

		return bookRepo.deleteBook(bookId);
	}
}
