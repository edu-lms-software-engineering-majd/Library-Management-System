package lms.persistence;

import lms.domain.Book;
import lms.domain.BookRepo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * In-memory implementation of {@link BookRepo} for testing and simple usage.
 *
 * <p>This repository stores books in a static list and provides
 * basic CRUD operations: create, read, update, delete.</p>
 *
 * <p>Note: This is not thread-safe and intended only for demos,
 * prototypes, or testing. For production, use a database-backed
 * implementation like {@code JdbcBookRepo}.</p>
 *
 * @author Majd Awwad
 * @version 1.0
 */
public class StaticBookRepo implements BookRepo {

    /** Internal list storing all books */
    private static final List<Book> books = new ArrayList<>();

    @Override
    public boolean addBook(Book book) {
        if (getBookByIsbn(book.getIsbn()) != null) {
            return false; // duplicate ISBN not allowed
        }
        return books.add(book);
    }

    @Override
    public Book getBookById(UUID bookId) {
        Optional<Book> book = books.stream()
                .filter(b -> b.getBookId().equals(bookId))
                .findFirst();
        return book.orElse(null);
    }

    @Override
    public Book getBookByIsbn(String isbn) {
        Optional<Book> book = books.stream()
                .filter(b -> b.getIsbn().equalsIgnoreCase(isbn))
                .findFirst();
        return book.orElse(null);
    }

    @Override
    public boolean updateBook(Book updatedBook) {
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getBookId().equals(updatedBook.getBookId())) {
                books.set(i, updatedBook);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteBook(UUID bookId) {
        return books.removeIf(b -> b.getBookId().equals(bookId));
    }

    @Override
    public List<Book> getAllBooks() {
        return Collections.unmodifiableList(books);
    }
}
