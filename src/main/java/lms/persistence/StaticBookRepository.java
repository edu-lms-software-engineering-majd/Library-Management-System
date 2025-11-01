package lms.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lms.domain.Book;
import lms.domain.BookRepo;
import lms.domain.BookRepository;

/**
<<<<<<< HEAD
 * In-memory implementation of {@link BookRepository} for simple usage, testing, or
 * prototyping.
||||||| 7160386
 * In-memory implementation of {@link BookRepo} for simple usage, testing, or
 * prototyping.
=======
 * In-memory implementation of {@link BookRepository} for simple usage, testing,
 * or prototyping.
>>>>>>> ahmad-salameh
 *
 * <p>
 * This repository stores {@link Book} objects in a static list and provides
 * basic CRUD operations: create, read, update, delete. It simulates a
 * persistence layer without connecting to an actual database.
 * </p>
 *
 * <p>
 * Important Notes:
 * </p>
 * <ul>
 * <li>Not thread-safe: concurrent access may cause inconsistent behavior.</li>
 * <li>Primarily intended for demos, prototypes, or unit tests.</li>
 * <li>For production use, replace with a database-backed repository (e.g.,
 * {@code JdbcBookRepo}).</li>
 * </ul>
 * 
 * <p>
 * Sample usage:
 * </p>
 * 
 * <pre>
 * BookRepo repo = new StaticBookRepo();
 * Book book = new Book("Title", "Author", "ISBN123", "Publisher", 2023, "Category", 5, "English", "Shelf X1");
 * repo.addBook(book);
 * Book retrieved = repo.getBookByIsbn("ISBN123");
 * </pre>
 * 
 * @author Majd Awwad
 * @version 1.1
 */
public class StaticBookRepository implements BookRepository {

	private static StaticBookRepository instance = null;
	
	/** Internal list storing all books */
	private static final List<Book> books = new ArrayList<>();
	
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
	 * Adds a new book to the repository if the ISBN is unique.
	 *
	 * @param book the {@link Book} to add
	 * @return {@code true} if the book was added successfully, {@code false} if a
	 *         book with the same ISBN already exists
	 */
	
	StaticBookRepository getInstance() {
	
		if (instance == null) {
			instance = new StaticBookRepository();
		}
		return instance;
	}

	@Override
	public boolean addBook(Book book) {
		if (getBookByIsbn(book.getIsbn()) != null) {
			return false;
		}
		return books.add(book);
	}

	/**
	 * Retrieves a book by its unique identifier.
	 *
	 * @param bookId the {@link UUID} of the book
	 * @return the {@link Book} if found, or {@code null} otherwise
	 */

	@Override
	public Optional<Book> getBookById(UUID bookId) {
		
		return books.stream().filter(b -> b.getId().equals(bookId)).findFirst();
	}

	/**
	 * Retrieves a book by its ISBN.
	 *
	 * @param isbn the ISBN of the book
	 * @return the {@link Book} if found, or {@code null} otherwise
	 */

	@Override
	public Optional<Book> getBookByIsbn(String isbn) {
		return books.stream().filter(b -> b.getIsbn().equalsIgnoreCase(isbn)).findFirst();
	}

	/**
	 * Updates an existing book in the repository.
	 *
	 * @param updatedBook the {@link Book} with updated information
	 * @return {@code true} if the update was successful, {@code false} if the book
	 *         does not exist
	 */

	@Override
	public boolean updateBook(Book updatedBook) {
		for (int i = 0; i < books.size(); i++) {
			if (books.get(i).getId().equals(updatedBook.getId())) {
				books.set(i, updatedBook);
				return true;
			}
		}
		return false;
	}

	/**
	 * Deletes a book from the repository by its unique identifier.
	 *
	 * @param bookId the {@link UUID} of the book to delete
	 * @return {@code true} if deletion was successful, {@code false} if the book
	 *         was not found
	 */

	@Override
	public boolean deleteBook(UUID bookId) {
		return books.removeIf(b -> b.getId().equals(bookId));
	}

	/**
	 * Returns an unmodifiable list of all books in the repository.
	 *
	 * @return a list of all {@link Book} objects
	 */

	@Override
	public List<Book> getAllBooks() {
		return Collections.unmodifiableList(books);
	}
}
