package lms.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lms.domain.Book;
import lms.domain.BookRepository;

public class StaticBookRepository implements BookRepository {

	private final static StaticBookRepository INSTANCE = new StaticBookRepository();

	/** In-memory book storage */
	private static final List<Book> books = new ArrayList<>();

	private StaticBookRepository() {
	}

	public static StaticBookRepository getInstance() {
		return INSTANCE;
	}

	// ============================================
	// Validation
	// ============================================
	private void validateBook(Book book) {
		if (book == null)
			throw new IllegalArgumentException("Book cannot be null");

		if (book.getTitle() == null || book.getTitle().isBlank())
			throw new IllegalArgumentException("Book title cannot be empty");

		if (book.getIsbn() == null || book.getIsbn().isBlank())
			throw new IllegalArgumentException("ISBN cannot be empty");

		if (book.getTotalCopies() < 0)
			throw new IllegalArgumentException("Total copies cannot be negative");
	}

	private boolean isbnExists(String isbn) {
		return books.stream().anyMatch(b -> b.getIsbn().equalsIgnoreCase(isbn));
	}

	// ============================================
	// CRUD Operations
	// ============================================

	@Override
	public boolean addBook(Book book) {
		validateBook(book);

		if (isbnExists(book.getIsbn()))
			throw new IllegalArgumentException("A book with ISBN already exists: " + book.getIsbn());

		return books.add(book);
	}

	@Override
	public Optional<Book> getBookById(UUID bookId) {
		if (bookId == null)
			return Optional.empty();

		return books.stream().filter(b -> b.getId().equals(bookId)).findFirst();
	}

	@Override
	public Optional<Book> getBookByIsbn(String isbn) {
		if (isbn == null)
			return Optional.empty();

		return books.stream().filter(b -> b.getIsbn().equalsIgnoreCase(isbn)).findFirst();
	}

	@Override
	public boolean updateBook(Book updatedBook) {
		validateBook(updatedBook);

		for (int i = 0; i < books.size(); i++) {
			if (books.get(i).getId().equals(updatedBook.getId())) {

				// Prevent using an existing ISBN for a different book
				if (!books.get(i).getIsbn().equalsIgnoreCase(updatedBook.getIsbn())
						&& isbnExists(updatedBook.getIsbn())) {
					throw new IllegalArgumentException("Updated ISBN already exists for another book.");
				}

				books.set(i, updatedBook);
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean deleteBook(UUID bookId) {
		if (bookId == null)
			throw new IllegalArgumentException("Book ID cannot be null");

		return books.removeIf(b -> b.getId().equals(bookId));
	}

	@Override
	public List<Book> getAllBooks() {
		return Collections.unmodifiableList(books);
	}
}
