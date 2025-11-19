package lms.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lms.domain.Book;
import lms.domain.BookRepository;

/**
 * In-memory implementation of {@link BookRepository} for simple usage, testing,
 * or prototyping.
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
 * {@code JdbcBookRepository}).</li>
 * </ul>
 */
public class StaticBookRepository implements BookRepository {

	/** Internal list storing all books */
	private static final List<Book> books = new ArrayList<>();

	/** Singleton instance */
	private static final StaticBookRepository INSTANCE = new StaticBookRepository();

	static {
		books.add(new Book("Clean Code", "Robert C. Martin", "9780132350884", "Prentice Hall", 2008,
				"Software Engineering", 5, "English", "Shelf A1"));
		books.add(new Book("Effective Java", "Joshua Bloch", "9780134685991", "Addison-Wesley", 2018, "Programming", 3,
				"English", "Shelf B2"));
		books.add(new Book("Design Patterns: Elements of Reusable Object-Oriented Software",
				"Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides", "9780201633610", "Addison-Wesley", 1994,
				"Software Design", 2, "English", "Shelf C3"));
	}

	/** Private constructor to enforce singleton pattern */
	private StaticBookRepository() {
	}

	/**
	 * Returns the single shared instance of the repository.
	 *
	 * @return the singleton {@link StaticBookRepository} instance
	 */
	public static StaticBookRepository getInstance() {
		return INSTANCE;
	}

	@Override
	public boolean addBook(Book book) {
		if (getBookByIsbn(book.getIsbn()).isPresent()) {
			return false;
		}
		return books.add(book);
	}

	@Override
	public Optional<Book> getBookById(UUID bookId) {
		return books.stream().filter(b -> b.getId().equals(bookId)).findFirst();
	}

	@Override
	public Optional<Book> getBookByIsbn(String isbn) {
		return books.stream().filter(b -> b.getIsbn().equalsIgnoreCase(isbn)).findFirst();
	}

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

	@Override
	public boolean deleteBook(UUID bookId) {
		return books.removeIf(b -> b.getId().equals(bookId));
	}

	@Override
	public List<Book> getAllBooks() {
		return Collections.unmodifiableList(books);
	}
}
