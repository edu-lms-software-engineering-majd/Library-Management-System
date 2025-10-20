package lms.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing {@link Book} entities.
 *
 * <p>
 * This defines the contract for book persistence and retrieval operations,
 * without specifying the storage mechanism. Implementations may use in-memory
 * storage, relational databases, or any other persistence strategy.
 * </p>
 *
 * <p>
 * Following the Repository pattern, this interface abstracts access to the
 * domain model and allows the application layer to interact with books in a
 * consistent way.
 * </p>
 *
 * @author Majd Awwad
 * @version 1.0
 */
public interface BookRepo {

	/**
	 * Adds a new book to the repository.
	 *
	 * @param book the {@link Book} to add
	 * @return {@code true} if the book was added, {@code false} if it already
	 *         exists
	 */
	boolean addBook(Book book);

    /**
     * Retrieves a book by its unique ID.
     *
     * @param bookId the {@link UUID} of the book
     * @return the {@link Book} if found, otherwise {@code null}
     */
    Optional<Book> getBookById(UUID bookId);

	/**
	 * Retrieves a book by its ISBN.
	 *
	 * @param isbn the ISBN string of the book
	 * @return the {@link Book} if found, otherwise {@code null}
	 */
	Book getBookByIsbn(String isbn);

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
