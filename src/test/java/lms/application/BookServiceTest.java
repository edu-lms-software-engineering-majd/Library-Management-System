package lms.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import lms.domain.Book;
import lms.domain.BookRepository;
import lms.domain.Role;
import lms.domain.UserRepository;
import lms.domain.exception.PermissionDeniedException;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

	@Mock
	private BookRepository bookRepo;
	@Mock
	private UserRepository userRepo;

	private BookService bookService;
	private UserDTO adminUser;
	private UserDTO normalUser;
	private Book testBook;
	private UUID testBookId;

	@BeforeEach
	void setUp() {
		bookService = new BookService(bookRepo, userRepo);
		testBookId = UUID.randomUUID();

		adminUser = new UserDTO(UUID.randomUUID(), "admin", "Majd", "Awwad", Role.ADMIN);
		normalUser = new UserDTO(UUID.randomUUID(), "user", "Ahmad", "Salameh", Role.MEMBER);

		testBook = new Book("Java", "Author", "ISBN123", "Publisher", 2023, "Tech", 3, "English", "A1");
	}

	// ====================== ADD BOOK TESTS ======================
	@Test
	void givenAdmin_whenAddBook_thenBookAdded() throws Exception {
		when(bookRepo.addBook(any(Book.class))).thenReturn(true);

		Book result = bookService.addBook(adminUser, "Java", "Author", "ISBN001", "Pub", 2022, "Tech", 5, "EN", "B1");

		assertNotNull(result);
		verify(bookRepo).addBook(any(Book.class));
	}

	@Test
	void givenNonAdmin_whenAddBook_thenThrowsPermissionException() {
		assertThrows(PermissionDeniedException.class,
				() -> bookService.addBook(normalUser, "C++", "Author", "ISBN002", "Pub", 2020, "Tech", 5, "EN", "C2"));
	}

	@Test
	void givenDuplicateBook_whenAddBook_thenThrowsStateException() throws Exception {
		when(bookRepo.addBook(any(Book.class))).thenReturn(false);

		assertThrows(IllegalStateException.class,
				() -> bookService.addBook(adminUser, "C#", "Author", "ISBN003", "Pub", 2021, "Tech", 2, "EN", "D1"));
	}

	// ====================== GET ALL BOOKS ======================
	@Test
	void whenGetAllBooks_thenReturnList() {
		when(bookRepo.getAllBooks()).thenReturn(List.of(testBook));
		List<Book> result = bookService.getAllBooks();

		assertEquals(1, result.size());
		verify(bookRepo).getAllBooks();
	}

	// ====================== GET BOOK BY ID ======================
	@Test
	void whenGetBookById_givenExistingBook_thenReturnBook() {
		when(bookRepo.getBookById(testBookId)).thenReturn(Optional.of(testBook));
		Book result = bookService.getBookById(testBookId);
		assertNotNull(result);
	}

	@Test
	void whenGetBookById_givenMissingBook_thenReturnNull() {
		when(bookRepo.getBookById(testBookId)).thenReturn(Optional.empty());
		Book result = bookService.getBookById(testBookId);
		assertNull(result);
	}

	// ====================== VALIDATE BOOK ======================
	@Test
	void whenIsValidBook_givenExistingBook_thenTrue() {
		when(bookRepo.getBookById(testBookId)).thenReturn(Optional.of(testBook));
		assertTrue(bookService.isValidBook(testBookId));
	}

	@Test
	void whenIsValidBook_givenMissingBook_thenFalse() {
		when(bookRepo.getBookById(testBookId)).thenReturn(Optional.empty());
		assertFalse(bookService.isValidBook(testBookId));
	}

	// ====================== AVAILABLE COPIES ======================
	@Test
	void whenBookHasAvailableCopies_thenIsAvailableTrue() {
		testBook = new Book("Java", "Author", "ISBN123", "Pub", 2023, "Tech", 2, "EN", "A1");
		when(bookRepo.getBookById(testBookId)).thenReturn(Optional.of(testBook));

		boolean isValid = bookService.isValidBook(testBookId);
		assertTrue(isValid, "Book should be valid");

		int availableCopies = bookService.getBookById(testBookId).getAvailableCopies();
		assertTrue(availableCopies > 0, "Book should have available copies");
	}
	/*
	 * ====================== SEARCH BOOKS ======================
	 * 
	 * @Test void givenStrategy_whenSearchBooks_thenStrategyExecuted() {
	 * SearchStrategy<Book> strategy = (books, term) -> List.of(testBook);
	 * List<Book> result = bookService.searchBooks(strategy, "Java");
	 * assertEquals(1, result.size()); assertEquals(testBook, result.get(0)); }
	 */

}
