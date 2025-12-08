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
	 * @param userDTO the user attempting to add the book
	 * @param bookDTO the book data transfer object containing all book information
	 * @return the created Book entity
	 * @throws PermissionDeniedException if the user is not an admin
	 */
	public Book addBook(UserDTO userDTO, BookDTO bookDTO)
			throws PermissionDeniedException {

		AuthorizationService.ensureAdmin(userDTO);

		Book book = new Book(bookDTO.title(), bookDTO.author(), bookDTO.isbn(), bookDTO.publisher(), 
				bookDTO.publicationYear(), bookDTO.category(), bookDTO.totalCopies(), bookDTO.language(),
				bookDTO.description(), bookDTO.shelfLocation());

		boolean added = bookRepo.addBook(book);
		if (!added)
			throw new IllegalStateException("Book with ISBN already exists: " + bookDTO.isbn());

		return book;
	}

	/**
	 * Retrieves all books in the system.
	 *
	 * @return a list of all books
	 */
	public List<Book> getAllBooks() {
		return bookRepo.getAllBooks();
	}

	/**
	 * Searches for books using a specified search strategy.
	 *
	 * @param strategy the search strategy to apply
	 * @param searchTerm the search term to filter by
	 * @return a list of books matching the search criteria
	 * @throws IllegalArgumentException if strategy is null
	 */
	public List<Book> searchBooks(SearchStrategy<Book> strategy, String searchTerm) {
		if (strategy == null)
			throw new IllegalArgumentException("Search strategy cannot be null");

		return strategy.execute(bookRepo.getAllBooks(), searchTerm);
	}

	/**
	 * Retrieves a book by its unique identifier.
	 *
	 * @param bookId the book's unique identifier
	 * @return the Book entity, or null if not found
	 * @throws IllegalArgumentException if bookId is null
	 */
	public Book getBookById(UUID bookId) {
		if (bookId == null)
			throw new IllegalArgumentException("Book ID cannot be null");

		return bookRepo.getBookById(bookId).orElse(null);
	}

	/**
	 * Retrieves a book using a partial ID match (prefix).
	 *
	 * @param subId the ID prefix to search for
	 * @return the matching Book entity
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

	/**
	 * Checks if a book exists and has available copies for borrowing.
	 *
	 * @param bookId the book's unique identifier
	 * @return {@code true} if the book exists and has available copies
	 */
	public boolean isAvailableBook(UUID bookId) {
		return bookRepo.getBookById(bookId).map(b -> b.getAvailableCopies() > 0).orElse(false);
	}

	/**
	 * Checks whether a book exists in the system.
	 *
	 * @param bookId the book's unique identifier
	 * @return {@code true} if the book exists
	 */
	public boolean isValidBook(UUID bookId) {
		return bookRepo.getBookById(bookId).isPresent();
	}

	/**
	 * Updates an existing book's fields (admin-only).
	 *
	 * @param userDTO the user attempting to update the book
	 * @param bookId the ID of the book to update
	 * @param bookDTO the book data transfer object containing updated information (null fields are ignored)
	 * @return true if the book was successfully updated
	 * @throws PermissionDeniedException if user is not admin
	 */
	public boolean updateBook(UserDTO userDTO, UUID bookId, BookDTO bookDTO)
			throws PermissionDeniedException {

		AuthorizationService.ensureAdmin(userDTO);

		Book book = bookRepo.getBookById(bookId)
				.orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + bookId));

		if (bookDTO.title() != null)
			book.setTitle(bookDTO.title());
		if (bookDTO.author() != null)
			book.setAuthor(bookDTO.author());
		if (bookDTO.isbn() != null)
			book.setIsbn(bookDTO.isbn());
		if (bookDTO.publisher() != null)
			book.setPublisher(bookDTO.publisher());
		if (bookDTO.publicationYear() != null)
			book.setPublicationYear(bookDTO.publicationYear());
		if (bookDTO.category() != null)
			book.setCategory(bookDTO.category());

		if (bookDTO.totalCopies() != null) {
			if (bookDTO.totalCopies() < book.getAvailableCopies())
				throw new IllegalArgumentException("New total copies (" + bookDTO.totalCopies()
						+ ") cannot be less than available copies (" + book.getAvailableCopies() + ")");
			book.setTotalCopies(bookDTO.totalCopies());
		}

		if (bookDTO.language() != null)
			book.setLanguage(bookDTO.language());
		if (bookDTO.shelfLocation() != null)
			book.setShelfLocation(bookDTO.shelfLocation());

		return bookRepo.updateBook(book);
	}

	/**
	 * Deletes a book from the system (admin-only operation).
	 *
	 * @param userDTO the user attempting to delete the book
	 * @param bookId the ID of the book to delete
	 * @return {@code true} if deletion succeeded
	 * @throws PermissionDeniedException if user is not admin
	 * @throws IllegalArgumentException if book doesn't exist
	 */
	public boolean deleteBook(UserDTO userDTO, UUID bookId) throws PermissionDeniedException {

		AuthorizationService.ensureAdmin(userDTO);

		bookRepo.getBookById(bookId)
				.orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + bookId));

		return bookRepo.deleteBook(bookId);
	}
}
