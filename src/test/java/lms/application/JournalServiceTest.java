package lms.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import lms.domain.Journal;
import lms.domain.JournalsRepository;
import lms.domain.Role;
import lms.domain.UserRepository;
import lms.domain.exception.PermissionDeniedException;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class JournalServiceTest {

	@Mock
	private JournalsRepository journalRepo;

	@Mock
	private UserRepository userRepo;

	private JournalService journalService;
	private UserDTO adminUser;
	private UserDTO memberUser;

	@BeforeEach
	void setUp() {
		journalService = new JournalService(journalRepo, userRepo);
		adminUser = mock(UserDTO.class);
		memberUser = mock(UserDTO.class);

		when(adminUser.role()).thenAnswer(i -> Role.ADMIN);
		when(memberUser.role()).thenAnswer(i -> Role.MEMBER);
	}

	@AfterEach
	void tearDown() {
		journalService = null;
		adminUser = null;
		memberUser = null;
	}

	@Test
	void givenAdminUser_whenAddJournal_thenJournalIsCreatedAndSaved() {
		when(journalRepo.addJournal(any(Journal.class))).thenReturn(true);

		try {
			Journal result = journalService.addJournal(adminUser, "Nature", "John Smith");

			assertNotNull(result);
			assertEquals("Nature", result.getTitle());
			assertEquals("John Smith", result.getAuthor());
			assertEquals(1, result.getTotalCopies());
			assertEquals(1, result.getAvailableCopies());
			verify(journalRepo).addJournal(any(Journal.class));
		} catch (PermissionDeniedException e) {
			throw new AssertionError("Should not throw PermissionDeniedException for admin user", e);
		}
	}

	@Test
	void givenMemberUser_whenAddJournal_thenThrowPermissionDeniedException() {
		Exception exception = assertThrows(PermissionDeniedException.class,
				() -> journalService.addJournal(memberUser, "Nature", "John Smith"));

		verify(journalRepo, never()).addJournal(any(Journal.class));
	}

	@Test
	void givenNullUser_whenAddJournal_thenThrowPermissionDeniedException() {
		Exception exception = assertThrows(PermissionDeniedException.class,
				() -> journalService.addJournal(null, "Nature", "John Smith"));

		verify(journalRepo, never()).addJournal(any(Journal.class));
	}

	@Test
	void givenEmptyTitle_whenAddJournal_thenThrowIllegalArgumentException() {
		Exception exception = assertThrows(IllegalArgumentException.class,
				() -> journalService.addJournal(adminUser, "", "John Smith"));

		verify(journalRepo, never()).addJournal(any(Journal.class));
	}

	@Test
	void givenNullAuthor_whenAddJournal_thenThrowIllegalArgumentException() {
		Exception exception = assertThrows(IllegalArgumentException.class,
				() -> journalService.addJournal(adminUser, "Nature", null));

		verify(journalRepo, never()).addJournal(any(Journal.class));
	}

	@Test
	void givenRepositoryFailure_whenAddJournal_thenThrowIllegalStateException() {
		when(journalRepo.addJournal(any(Journal.class))).thenReturn(false);

		Exception exception = assertThrows(IllegalStateException.class,
				() -> journalService.addJournal(adminUser, "Nature", "John Smith"));

		assertTrue(exception.getMessage().contains("Failed to add journal"));
	}

	@Test
	void givenAdminUserAndTotalCopies_whenAddJournal_thenJournalIsCreatedWithCopies() {
		when(journalRepo.addJournal(any(Journal.class))).thenReturn(true);

		try {
			Journal result = journalService.addJournal(adminUser, "Science Weekly", "Jane Doe", 5);

			assertNotNull(result);
			assertEquals("Science Weekly", result.getTitle());
			assertEquals("Jane Doe", result.getAuthor());
			assertEquals(5, result.getTotalCopies());
			assertEquals(5, result.getAvailableCopies());
			verify(journalRepo).addJournal(any(Journal.class));
		} catch (PermissionDeniedException e) {
			throw new AssertionError("Should not throw PermissionDeniedException for admin user", e);
		}
	}

	@Test
	void whenGetAllJournals_thenReturnAllJournals() {
		Journal journal1 = new Journal("Journal 1", "Author 1");
		Journal journal2 = new Journal("Journal 2", "Author 2");
		List<Journal> expectedJournals = Arrays.asList(journal1, journal2);
		when(journalRepo.getAllJournals()).thenReturn(expectedJournals);

		List<Journal> result = journalService.getAllJournals();

		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals("Journal 1", result.get(0).getTitle());
		assertEquals("Journal 2", result.get(1).getTitle());
		verify(journalRepo).getAllJournals();
	}

	@Test
	void whenGetAllJournals_withEmptyRepository_thenReturnEmptyList() {
		when(journalRepo.getAllJournals()).thenReturn(Collections.emptyList());

		List<Journal> result = journalService.getAllJournals();

		assertNotNull(result);
		assertTrue(result.isEmpty());
		verify(journalRepo).getAllJournals();
	}

	@Test
	void givenExistingJournalId_whenGetJournalById_thenReturnJournal() {
		UUID journalId = UUID.randomUUID();
		Journal expectedJournal = new Journal("Nature", "John Smith");
		when(journalRepo.getJournalById(journalId)).thenReturn(Optional.of(expectedJournal));

		Journal result = journalService.getJournalById(journalId);

		assertNotNull(result);
		assertEquals("Nature", result.getTitle());
		assertEquals("John Smith", result.getAuthor());
		verify(journalRepo).getJournalById(journalId);
	}

	@Test
	void givenNonExistingJournalId_whenGetJournalById_thenThrowIllegalArgumentException() {
		UUID journalId = UUID.randomUUID();
		when(journalRepo.getJournalById(journalId)).thenReturn(Optional.empty());

		Exception exception = assertThrows(IllegalArgumentException.class,
				() -> journalService.getJournalById(journalId));

		assertTrue(exception.getMessage().contains("Journal not found"));
		verify(journalRepo).getJournalById(journalId);
	}

	@Test
	void givenAdminUserAndValidData_whenUpdateJournal_thenJournalIsUpdated() {
		UUID journalId = UUID.randomUUID();
		Journal journal = new Journal("Old Title", "Old Author");
		when(journalRepo.getJournalById(journalId)).thenReturn(Optional.of(journal));
		when(journalRepo.updateJournal(journal)).thenReturn(true);

		try {
			Journal result = journalService.updateJournal(adminUser, journalId, "New Title", "New Author", null);

			assertNotNull(result);
			assertEquals("New Title", result.getTitle());
			assertEquals("New Author", result.getAuthor());
			verify(journalRepo).getJournalById(journalId);
			verify(journalRepo).updateJournal(journal);
		} catch (PermissionDeniedException e) {
			throw new AssertionError("Should not throw PermissionDeniedException for admin user", e);
		}
	}

	@Test
	void givenAdminUserAndNullTitle_whenUpdateJournal_thenOnlyAuthorIsUpdated() {
		UUID journalId = UUID.randomUUID();
		Journal journal = new Journal("Original Title", "Old Author");
		when(journalRepo.getJournalById(journalId)).thenReturn(Optional.of(journal));
		when(journalRepo.updateJournal(journal)).thenReturn(true);

		try {
			Journal result = journalService.updateJournal(adminUser, journalId, null, "New Author", null);

			assertEquals("Original Title", result.getTitle());
			assertEquals("New Author", result.getAuthor());
			verify(journalRepo).updateJournal(journal);
		} catch (PermissionDeniedException e) {
			throw new AssertionError("Should not throw PermissionDeniedException for admin user", e);
		}
	}

	@Test
	void givenMemberUser_whenUpdateJournal_thenThrowPermissionDeniedException() {
		UUID journalId = UUID.randomUUID();

		Exception exception = assertThrows(PermissionDeniedException.class,
				() -> journalService.updateJournal(memberUser, journalId, "New Title", "New Author", null));

		verify(journalRepo, never()).getJournalById(any(UUID.class));
		verify(journalRepo, never()).updateJournal(any(Journal.class));
	}

	@Test
	void givenNonExistingJournalId_whenUpdateJournal_thenThrowIllegalArgumentException() {
		UUID journalId = UUID.randomUUID();
		when(journalRepo.getJournalById(journalId)).thenReturn(Optional.empty());

		Exception exception = assertThrows(IllegalArgumentException.class,
				() -> journalService.updateJournal(adminUser, journalId, "New Title", "New Author", null));

		assertTrue(exception.getMessage().contains("Journal not found"));
		verify(journalRepo).getJournalById(journalId);
	}

	@Test
	void givenRepositoryFailure_whenUpdateJournal_thenThrowIllegalStateException() {
		UUID journalId = UUID.randomUUID();
		Journal journal = new Journal("Title", "Author");
		when(journalRepo.getJournalById(journalId)).thenReturn(Optional.of(journal));
		when(journalRepo.updateJournal(journal)).thenReturn(false);

		Exception exception = assertThrows(IllegalStateException.class,
				() -> journalService.updateJournal(adminUser, journalId, "New Title", "New Author", null));

		assertTrue(exception.getMessage().contains("Failed to update"));
	}

	@Test
	void givenAdminUserAndExistingJournal_whenDeleteJournal_thenJournalIsDeleted() {
		UUID journalId = UUID.randomUUID();
		when(journalRepo.deleteJournal(journalId)).thenReturn(true);

		try {
			journalService.deleteJournal(adminUser, journalId);

			verify(journalRepo).deleteJournal(journalId);
		} catch (PermissionDeniedException e) {
			throw new AssertionError("Should not throw PermissionDeniedException for admin user", e);
		}
	}

	@Test
	void givenMemberUser_whenDeleteJournal_thenThrowPermissionDeniedException() {
		UUID journalId = UUID.randomUUID();

		Exception exception = assertThrows(PermissionDeniedException.class,
				() -> journalService.deleteJournal(memberUser, journalId));

		verify(journalRepo, never()).deleteJournal(any(UUID.class));
	}

	@Test
	void givenNonExistingJournalId_whenDeleteJournal_thenThrowIllegalArgumentException() {
		UUID journalId = UUID.randomUUID();
		when(journalRepo.deleteJournal(journalId)).thenReturn(false);

		Exception exception = assertThrows(IllegalArgumentException.class,
				() -> journalService.deleteJournal(adminUser, journalId));

		assertTrue(exception.getMessage().contains("Journal not found"));
		verify(journalRepo).deleteJournal(journalId);
	}

	@Test
	void givenValidKeyword_whenSearchJournals_thenReturnMatchingJournals() {
		Journal journal1 = new Journal("Science Quarterly", "Research Team");
		Journal journal2 = new Journal("Science Today", "Various Authors");
		List<Journal> expectedJournals = Arrays.asList(journal1, journal2);
		when(journalRepo.searchJournals("Science")).thenReturn(expectedJournals);

		List<Journal> result = journalService.searchJournals("Science");

		assertNotNull(result);
		assertEquals(2, result.size());
		assertTrue(result.get(0).getTitle().contains("Science"));
		verify(journalRepo).searchJournals("Science");
		verify(journalRepo, never()).getAllJournals();
	}

	@Test
	void givenNullKeyword_whenSearchJournals_thenReturnAllJournals() {
		Journal journal1 = new Journal("Journal 1", "Author 1");
		Journal journal2 = new Journal("Journal 2", "Author 2");
		List<Journal> allJournals = Arrays.asList(journal1, journal2);
		when(journalRepo.getAllJournals()).thenReturn(allJournals);

		List<Journal> result = journalService.searchJournals(null);

		assertNotNull(result);
		assertEquals(2, result.size());
		verify(journalRepo).getAllJournals();
		verify(journalRepo, never()).searchJournals(any());
	}

	@Test
	void givenBlankKeyword_whenSearchJournals_thenReturnAllJournals() {
		Journal journal1 = new Journal("Journal 1", "Author 1");
		List<Journal> allJournals = Arrays.asList(journal1);
		when(journalRepo.getAllJournals()).thenReturn(allJournals);

		List<Journal> result = journalService.searchJournals("   ");

		assertNotNull(result);
		assertEquals(1, result.size());
		verify(journalRepo).getAllJournals();
		verify(journalRepo, never()).searchJournals(any());
	}

	@Test
	void givenJournalWithAvailableCopies_whenIsAvailableJournal_thenReturnTrue() {
		UUID journalId = UUID.randomUUID();
		Journal journal = new Journal("Journal", "Author", 3);
		when(journalRepo.getJournalById(journalId)).thenReturn(Optional.of(journal));

		boolean result = journalService.isAvailableJournal(journalId);

		assertTrue(result);
		verify(journalRepo).getJournalById(journalId);
	}

	@Test
	void givenJournalWithNoCopiesAvailable_whenIsAvailableJournal_thenReturnFalse() {
		UUID journalId = UUID.randomUUID();
		Journal journal = new Journal("Journal", "Author", 1);
		journal.decrementAvailableCopies();
		when(journalRepo.getJournalById(journalId)).thenReturn(Optional.of(journal));

		boolean result = journalService.isAvailableJournal(journalId);

		assertFalse(result);
		verify(journalRepo).getJournalById(journalId);
	}

	@Test
	void givenNonExistingJournalId_whenIsAvailableJournal_thenReturnFalse() {
		UUID journalId = UUID.randomUUID();
		when(journalRepo.getJournalById(journalId)).thenReturn(Optional.empty());

		boolean result = journalService.isAvailableJournal(journalId);

		assertFalse(result);
		verify(journalRepo).getJournalById(journalId);
	}

	@Test
	void givenExistingJournalId_whenIsValidJournal_thenReturnTrue() {
		UUID journalId = UUID.randomUUID();
		Journal journal = new Journal("Journal", "Author");
		when(journalRepo.getJournalById(journalId)).thenReturn(Optional.of(journal));

		boolean result = journalService.isValidJournal(journalId);

		assertTrue(result);
		verify(journalRepo).getJournalById(journalId);
	}

	@Test
	void givenNonExistingJournalId_whenIsValidJournal_thenReturnFalse() {
		UUID journalId = UUID.randomUUID();
		when(journalRepo.getJournalById(journalId)).thenReturn(Optional.empty());

		boolean result = journalService.isValidJournal(journalId);

		assertFalse(result);
		verify(journalRepo).getJournalById(journalId);
	}

	@Test
	void givenAvailableJournal_whenBorrowJournal_thenCopiesDecremented() {
		UUID journalId = UUID.randomUUID();
		Journal journal = new Journal("Journal", "Author", 3);
		when(journalRepo.getJournalById(journalId)).thenReturn(Optional.of(journal));
		when(journalRepo.updateJournal(journal)).thenReturn(true);

		journalService.borrowJournal(memberUser, journalId);

		assertEquals(2, journal.getAvailableCopies());
		assertTrue(journal.isBorrowed());
		verify(journalRepo).getJournalById(journalId);
		verify(journalRepo).updateJournal(journal);
	}

	@Test
	void givenJournalWithNoCopiesAvailable_whenBorrowJournal_thenThrowIllegalStateException() {
		UUID journalId = UUID.randomUUID();
		Journal journal = new Journal("Journal", "Author", 1);
		journal.decrementAvailableCopies();
		when(journalRepo.getJournalById(journalId)).thenReturn(Optional.of(journal));

		Exception exception = assertThrows(IllegalStateException.class,
				() -> journalService.borrowJournal(memberUser, journalId));

		assertTrue(exception.getMessage().contains("No copies available"));
		verify(journalRepo).getJournalById(journalId);
		verify(journalRepo, never()).updateJournal(any(Journal.class));
	}

	@Test
	void givenNonExistingJournalId_whenBorrowJournal_thenThrowIllegalArgumentException() {
		UUID journalId = UUID.randomUUID();
		when(journalRepo.getJournalById(journalId)).thenReturn(Optional.empty());

		Exception exception = assertThrows(IllegalArgumentException.class,
				() -> journalService.borrowJournal(memberUser, journalId));

		assertTrue(exception.getMessage().contains("Journal not found"));
		verify(journalRepo).getJournalById(journalId);
	}

	@Test
	void givenRepositoryFailure_whenBorrowJournal_thenThrowIllegalStateException() {
		UUID journalId = UUID.randomUUID();
		Journal journal = new Journal("Journal", "Author");
		when(journalRepo.getJournalById(journalId)).thenReturn(Optional.of(journal));
		when(journalRepo.updateJournal(journal)).thenReturn(false);

		Exception exception = assertThrows(IllegalStateException.class,
				() -> journalService.borrowJournal(memberUser, journalId));

		assertTrue(exception.getMessage().contains("Failed to update"));
	}

	@Test
	void givenBorrowedJournal_whenReturnJournal_thenCopiesIncremented() {
		UUID journalId = UUID.randomUUID();
		Journal journal = new Journal("Journal", "Author", 3);
		journal.decrementAvailableCopies();
		when(journalRepo.getJournalById(journalId)).thenReturn(Optional.of(journal));
		when(journalRepo.updateJournal(journal)).thenReturn(true);

		journalService.returnJournal(memberUser, journalId);

		assertEquals(3, journal.getAvailableCopies());
		assertFalse(journal.isBorrowed());
		verify(journalRepo).getJournalById(journalId);
		verify(journalRepo).updateJournal(journal);
	}

	@Test
	void givenJournalWithAllCopiesAvailable_whenReturnJournal_thenThrowIllegalStateException() {
		UUID journalId = UUID.randomUUID();
		Journal journal = new Journal("Journal", "Author");
		when(journalRepo.getJournalById(journalId)).thenReturn(Optional.of(journal));

		Exception exception = assertThrows(IllegalStateException.class,
				() -> journalService.returnJournal(memberUser, journalId));

		assertTrue(exception.getMessage().contains("All copies are already returned"));
		verify(journalRepo).getJournalById(journalId);
		verify(journalRepo, never()).updateJournal(any(Journal.class));
	}

	@Test
	void givenNonExistingJournalId_whenReturnJournal_thenThrowIllegalArgumentException() {
		UUID journalId = UUID.randomUUID();
		when(journalRepo.getJournalById(journalId)).thenReturn(Optional.empty());

		Exception exception = assertThrows(IllegalArgumentException.class,
				() -> journalService.returnJournal(memberUser, journalId));

		assertTrue(exception.getMessage().contains("Journal not found"));
		verify(journalRepo).getJournalById(journalId);
	}

	@Test
	void givenRepositoryFailure_whenReturnJournal_thenThrowIllegalStateException() {
		UUID journalId = UUID.randomUUID();
		Journal journal = new Journal("Journal", "Author", 2);
		journal.decrementAvailableCopies();
		when(journalRepo.getJournalById(journalId)).thenReturn(Optional.of(journal));
		when(journalRepo.updateJournal(journal)).thenReturn(false);

		Exception exception = assertThrows(IllegalStateException.class,
				() -> journalService.returnJournal(memberUser, journalId));

		assertTrue(exception.getMessage().contains("Failed to update"));
	}
}