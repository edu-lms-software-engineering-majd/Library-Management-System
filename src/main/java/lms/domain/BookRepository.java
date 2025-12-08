package lms.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing Book entities.
 *
 * <p>Defines operations for book persistence and retrieval.</p>
 *
 * @author Majd Awwad
 * @version 1.0
 */
public interface BookRepository {

	/**
	 * Adds a new book.
	 *
	 * @param book the book to add
	 * @return true if added, false if already exists
	 */
	boolean addBook(Book book);

	/**
	 * Retrieves a book by ID.
	 *
	 * @param bookId the book ID
	 * @return Optional containing the book if found
	 */
	Optional<Book> getBookById(UUID bookId);

	/**
	 * Retrieves a book by ISBN.
	 *
	 * @param isbn the ISBN
	 * @return Optional containing the book if found
	 */
	Optional<Book> getBookByIsbn(String isbn);

	/**
	 * Updates an existing book in the repository.
	 *
	 * @param updatedBook the {@link Book} with updated fields
	 * @return {@code true} if the update was successful, {@code false} if the book
	 *         does not exist
	 */
	boolean updateBook(Book updatedBook);

	/**
	 * Deletes a book by its ID.
	 *
	 * @param bookId the {@link UUID} of the book to delete
	 * @return {@code true} if deletion was successful, {@code false} if book not
	 *         found
	 */
	boolean deleteBook(UUID bookId);

	/**
	 * Returns all books stored in the repository.
	 *
	 * @return list of {@link Book} objects
	 */
	List<Book> getAllBooks();
}
