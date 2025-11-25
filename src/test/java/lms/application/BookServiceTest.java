package lms.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import lms.application.search.SearchStrategy;
import lms.domain.Book;
import lms.domain.BookRepository;
import lms.domain.Role;
import lms.domain.exception.PermissionDeniedException;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookService – High Coverage Tests")
class BookServiceTest {

	@Mock
	private BookRepository bookRepo;

	@Mock
	private SearchStrategy<Book> searchStrategy;

	private BookService bookService;

 	private UserDTO adminUser;
	private UserDTO memberUser;

 	private Book sampleBook;
	private UUID sampleBookId;

	@BeforeEach
	void setUp() {
		bookService = new BookService(bookRepo);

		adminUser = new UserDTO(UUID.randomUUID(), "ahmadsalameh", "Ahmad", "Salameh", Role.ADMIN);

		memberUser = new UserDTO(UUID.randomUUID(), "majdawwad", "Majd", "Awwad", Role.MEMBER);

		sampleBook = new Book("Clean Code", "Robert C. Martin", "9780132350884", "Prentice Hall", 2008,
				"Software Engineering", 5, "English", "A1-01");
		sampleBookId = sampleBook.getId();
	}

	// =====================================================================
	// Constructor
	// =====================================================================

	@Test
	@DisplayName("Constructor – should throw when repository is null")
	void givenNullRepository_whenCreateBookService_thenThrow() {
		assertThrows(IllegalArgumentException.class, () -> new BookService(null));
	}

	@Test
	@DisplayName("Constructor – should create service with valid repository")
	void givenValidRepository_whenCreateBookService_thenSuccess() {
		assertNotNull(bookService);
	}

	// =====================================================================
	// addBook
	// =====================================================================

	@Test
	@DisplayName("addBook – should add book successfully for ADMIN")
	void givenAdmin_whenAddBook_thenBookCreatedAndSaved() throws Exception {
		when(bookRepo.addBook(any(Book.class))).thenReturn(true);

		Book result = bookService.addBook(adminUser, "Domain-Driven Design", "Eric Evans", "9780321125217",
				"Addison-Wesley", 2003, "Software Engineering", 3, "English", "B2-05");

		assertNotNull(result);
		assertEquals("Domain-Driven Design", result.getTitle());
		verify(bookRepo).addBook(any(Book.class));
	}

	@Test
	@DisplayName("addBook – should throw PermissionDeniedException for non-admin user")
	void givenMember_whenAddBook_thenThrowPermissionDenied() {
		assertThrows(PermissionDeniedException.class, () -> {
			bookService.addBook(memberUser, "Test Title", "Test Author", "1111111111", "Some Publisher", 2024,
					"Category", 2, "English", "C1-01");
		});
	}

	@Test
	@DisplayName("addBook – should throw IllegalStateException when repository rejects book (ISBN exists)")
	void givenDuplicateIsbn_whenAddBook_thenThrowIllegalState() throws Exception {
		when(bookRepo.addBook(any(Book.class))).thenReturn(false);

		assertThrows(IllegalStateException.class, () -> {
			bookService.addBook(adminUser, "Clean Architecture", "Robert C. Martin", "9780134494166", "Prentice Hall",
					2017, "Software Engineering", 4, "English", "A1-02");
		});
	}

	// =====================================================================
	// getAllBooks
	// =====================================================================

	@Test
	@DisplayName("getAllBooks – should delegate to repository")
	void whenGetAllBooks_thenReturnListFromRepository() {
		when(bookRepo.getAllBooks()).thenReturn(List.of(sampleBook));

		List<Book> result = bookService.getAllBooks();

		assertEquals(1, result.size());
		assertEquals(sampleBook.getTitle(), result.get(0).getTitle());
		verify(bookRepo).getAllBooks();
	}

	// =====================================================================
	// searchBooks
	// =====================================================================

	@Test
	@DisplayName("searchBooks – should throw IllegalArgumentException when strategy is null")
	void givenNullStrategy_whenSearchBooks_thenThrow() {
		assertThrows(IllegalArgumentException.class, () -> bookService.searchBooks(null, "Clean"));
	}

	@Test
	@DisplayName("searchBooks – should use strategy and repository list")
	void givenValidStrategy_whenSearchBooks_thenStrategyIsCalled() {
		when(bookRepo.getAllBooks()).thenReturn(List.of(sampleBook));
		when(searchStrategy.execute(anyList(), anyString())).thenReturn(List.of(sampleBook));

		List<Book> result = bookService.searchBooks(searchStrategy, "Clean");

		assertEquals(1, result.size());
		assertEquals("Clean Code", result.get(0).getTitle());
		verify(bookRepo).getAllBooks();
		verify(searchStrategy).execute(anyList(), eq("Clean"));
	}

	// =====================================================================
	// getBookById
	// =====================================================================

	@Test
	@DisplayName("getBookById – should throw when id is null")
	void givenNullId_whenGetBookById_thenThrow() {
		assertThrows(IllegalArgumentException.class, () -> bookService.getBookById(null));
	}

	@Test
	@DisplayName("getBookById – should return book when found")
	void givenExistingId_whenGetBookById_thenReturnBook() {
		when(bookRepo.getBookById(sampleBookId)).thenReturn(Optional.of(sampleBook));

		Book result = bookService.getBookById(sampleBookId);

		assertNotNull(result);
		assertEquals(sampleBookId, result.getId());
		verify(bookRepo).getBookById(sampleBookId);
	}

	@Test
	@DisplayName("getBookById – should return null when not found")
	void givenMissingId_whenGetBookById_thenReturnNull() {
		UUID missingId = UUID.randomUUID();
		when(bookRepo.getBookById(missingId)).thenReturn(Optional.empty());

		Book result = bookService.getBookById(missingId);

		assertNull(result);
	}

	// =====================================================================
	// getBookBySubId
	// =====================================================================

	@Test
	@DisplayName("getBookBySubId – should return single matching book")
	void givenUniquePrefix_whenGetBookBySubId_thenReturnBook() {
		Book book1 = mock(Book.class);
		when(book1.getId()).thenReturn(UUID.fromString("11111111-1111-1111-1111-111111111111"));

		when(bookRepo.getAllBooks()).thenReturn(List.of(book1));

		Book result = bookService.getBookBySubId("1111");

		assertNotNull(result);
		assertEquals(book1, result);
	}

	@Test
	@DisplayName("getBookBySubId – should throw when no matches found")
	void givenNoMatchPrefix_whenGetBookBySubId_thenThrow() {
		when(bookRepo.getAllBooks()).thenReturn(List.of(sampleBook));

		assertThrows(IllegalArgumentException.class, () -> bookService.getBookBySubId("XYZ"));
	}

	@Test
	@DisplayName("getBookBySubId – should throw when multiple matches found")
	void givenAmbiguousPrefix_whenGetBookBySubId_thenThrow() {
		Book book1 = mock(Book.class);
		Book book2 = mock(Book.class);

		UUID id1 = UUID.fromString("aaaaaaaa-0000-0000-0000-000000000001");
		UUID id2 = UUID.fromString("aaaaaaaa-0000-0000-0000-000000000002");

		when(book1.getId()).thenReturn(id1);
		when(book2.getId()).thenReturn(id2);

		when(bookRepo.getAllBooks()).thenReturn(List.of(book1, book2));

		assertThrows(IllegalArgumentException.class, () -> bookService.getBookBySubId("aaaaaaaa"));
	}

	// =====================================================================
	// isAvailableBook
	// =====================================================================

	@Test
	@DisplayName("isAvailableBook – should return true when book has available copies")
	void givenBookWithAvailableCopies_whenIsAvailableBook_thenTrue() {
		Book book = mock(Book.class);
		when(book.getAvailableCopies()).thenReturn(3);
		when(bookRepo.getBookById(sampleBookId)).thenReturn(Optional.of(book));

		boolean available = bookService.isAvailableBook(sampleBookId);

		assertTrue(available);
		verify(bookRepo).getBookById(sampleBookId);
	}

	@Test
	@DisplayName("isAvailableBook – should return false when no available copies")
	void givenBookWithoutAvailableCopies_whenIsAvailableBook_thenFalse() {
		Book book = mock(Book.class);
		when(book.getAvailableCopies()).thenReturn(0);
		when(bookRepo.getBookById(sampleBookId)).thenReturn(Optional.of(book));

		boolean available = bookService.isAvailableBook(sampleBookId);

		assertFalse(available);
	}

	@Test
	@DisplayName("isAvailableBook – should return false when book does not exist")
	void givenMissingBook_whenIsAvailableBook_thenFalse() {
		when(bookRepo.getBookById(sampleBookId)).thenReturn(Optional.empty());

		boolean available = bookService.isAvailableBook(sampleBookId);

		assertFalse(available);
	}

	// =====================================================================
	// isValidBook
	// =====================================================================

	@Test
	@DisplayName("isValidBook – should return true when book exists")
	void givenExistingBook_whenIsValidBook_thenTrue() {
		when(bookRepo.getBookById(sampleBookId)).thenReturn(Optional.of(sampleBook));

		assertTrue(bookService.isValidBook(sampleBookId));
	}

	@Test
	@DisplayName("isValidBook – should return false when book does not exist")
	void givenMissingBook_whenIsValidBook_thenFalse() {
		when(bookRepo.getBookById(sampleBookId)).thenReturn(Optional.empty());

		assertFalse(bookService.isValidBook(sampleBookId));
	}

	// =====================================================================
	// updateBook
	// =====================================================================

	@Test
	@DisplayName("updateBook – should throw PermissionDeniedException for non-admin user")
	void givenMember_whenUpdateBook_thenThrowPermissionDenied() {
		assertThrows(PermissionDeniedException.class, () -> bookService.updateBook(memberUser, sampleBookId,
				"New Title", null, null, null, null, null, null, null, null));
	}

	@Test
	@DisplayName("updateBook – should throw when book not found")
	void givenAdminAndMissingBook_whenUpdateBook_thenThrowIllegalArgument() {
		when(bookRepo.getBookById(sampleBookId)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () -> bookService.updateBook(adminUser, sampleBookId, "New Title",
				null, null, null, null, null, null, null, null));
	}

	@Test
	@DisplayName("updateBook – should update fields and persist for admin")
	void givenAdminAndExistingBook_whenUpdateBook_thenFieldsUpdatedAndSaved() throws Exception {
		when(bookRepo.getBookById(sampleBookId)).thenReturn(Optional.of(sampleBook));
		when(bookRepo.updateBook(sampleBook)).thenReturn(true);

		boolean result = bookService.updateBook(adminUser, sampleBookId, "Refactored Code", "Ahmad Salameh",
				"9780132350884", "Najah Press", 2025, "Computer Engineering", 7, "Arabic", "C3-10");

		assertTrue(result);
		assertEquals("Refactored Code", sampleBook.getTitle());
		assertEquals("Ahmad Salameh", sampleBook.getAuthor());
		assertEquals("Najah Press", sampleBook.getPublisher());
		assertEquals(2025, sampleBook.getPublicationYear());
		assertEquals("Computer Engineering", sampleBook.getCategory());
		assertEquals(7, sampleBook.getTotalCopies());
		assertEquals("Arabic", sampleBook.getLanguage());
		assertEquals("C3-10", sampleBook.getShelfLocation());

		verify(bookRepo).updateBook(sampleBook);
	}

	@Test
	@DisplayName("updateBook – should allow partial update when some fields are null")
	void givenPartialData_whenUpdateBook_thenOnlyProvidedFieldsChanged() throws Exception {
		when(bookRepo.getBookById(sampleBookId)).thenReturn(Optional.of(sampleBook));
		when(bookRepo.updateBook(sampleBook)).thenReturn(true);

		String oldAuthor = sampleBook.getAuthor();
		String oldIsbn = sampleBook.getIsbn();

		boolean result = bookService.updateBook(adminUser, sampleBookId, "New Title Only", // title
				null, 
				null, 
				null, 
				null,
				null, 
				null, 
				null, 
				null 
		);

		assertTrue(result);
		assertEquals("New Title Only", sampleBook.getTitle());
		assertEquals(oldAuthor, sampleBook.getAuthor());
		assertEquals(oldIsbn, sampleBook.getIsbn());
	}

	@Test
	@DisplayName("updateBook – should throw when new totalCopies < availableCopies")
	void givenLowerTotalCopiesThanAvailable_whenUpdateBook_thenThrow() {
		// sampleBook created with total=5, available=5
		when(bookRepo.getBookById(sampleBookId)).thenReturn(Optional.of(sampleBook));

		assertThrows(IllegalArgumentException.class,
				() -> bookService.updateBook(adminUser, sampleBookId, null, null, null, null, null, null, 4, // أقل من
						// availableCopies
						// = 5
						null, null));
	}

	// =====================================================================
	// deleteBook
	// =====================================================================

	@Test
	@DisplayName("deleteBook – should throw PermissionDeniedException for non-admin")
	void givenMember_whenDeleteBook_thenThrowPermissionDenied() {
		assertThrows(PermissionDeniedException.class, () -> bookService.deleteBook(memberUser, sampleBookId));
	}

	@Test
	@DisplayName("deleteBook – should throw when book not found")
	void givenAdminAndMissingBook_whenDeleteBook_thenThrowIllegalArgument() {
		when(bookRepo.getBookById(sampleBookId)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () -> bookService.deleteBook(adminUser, sampleBookId));
	}

	@Test
	@DisplayName("deleteBook – should delete existing book and return true")
	void givenAdminAndExistingBook_whenDeleteBook_thenSuccess() throws Exception {
		when(bookRepo.getBookById(sampleBookId)).thenReturn(Optional.of(sampleBook));
		when(bookRepo.deleteBook(sampleBookId)).thenReturn(true);

		boolean result = bookService.deleteBook(adminUser, sampleBookId);

		assertTrue(result);
		verify(bookRepo).deleteBook(sampleBookId);
	}

	@Test
	@DisplayName("deleteBook – should return false when repository delete returns false")
	void givenAdminAndExistingBook_whenDeleteFails_thenReturnFalse() throws Exception {
		when(bookRepo.getBookById(sampleBookId)).thenReturn(Optional.of(sampleBook));
		when(bookRepo.deleteBook(sampleBookId)).thenReturn(false);

		boolean result = bookService.deleteBook(adminUser, sampleBookId);

		assertFalse(result);
		verify(bookRepo).deleteBook(sampleBookId);
	}
}
