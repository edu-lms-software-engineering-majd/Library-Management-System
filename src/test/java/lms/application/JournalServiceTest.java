package lms.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import lms.domain.Journal;
import lms.domain.JournalRepository;
import lms.domain.Role;
import lms.domain.UserRepository;
import lms.domain.exception.PermissionDeniedException;

@ExtendWith(MockitoExtension.class)
class JournalServiceTest {

	@Mock
	private JournalRepository journalRepo;

	@Mock
	private UserRepository userRepo;

	private JournalService journalService;
	private UserDTO adminUser;
	private UserDTO memberUser;

	@BeforeEach
	void setUp() {
		journalService = new JournalService(journalRepo, userRepo);
		adminUser = new UserDTO(UUID.randomUUID(), "admin", "Admin", "User", Role.ADMIN);
		memberUser = new UserDTO(UUID.randomUUID(), "member", "Member", "User", Role.MEMBER);
	}

	@Test
	void givenAdminUser_whenAddJournal_thenJournalIsCreatedAndSaved() throws PermissionDeniedException {
		when(journalRepo.addJournal(any(Journal.class))).thenReturn(true);

		Journal result = journalService.addJournal(adminUser, "Nature", "John Smith");

		assertNotNull(result);
		assertEquals("Nature", result.getTitle());
		assertEquals("John Smith", result.getAuthor());
		verify(journalRepo).addJournal(any(Journal.class));
	}

	@Test
	void givenMemberUser_whenAddJournal_thenThrowPermissionDeniedException() {
		assertThrows(PermissionDeniedException.class, () ->
			journalService.addJournal(memberUser, "Nature", "John Smith")
		);
		verify(journalRepo, never()).addJournal(any(Journal.class));
	}

	@Test
	void givenNullUser_whenAddJournal_thenThrowPermissionDeniedException() {
		assertThrows(PermissionDeniedException.class, () ->
			journalService.addJournal(null, "Nature", "John Smith")
		);
		verify(journalRepo, never()).addJournal(any(Journal.class));
	}

	@Test
	void givenInvalidTitle_whenAddJournal_thenThrowIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () ->
			journalService.addJournal(adminUser, "", "John Smith")
		);
		verify(journalRepo, never()).addJournal(any(Journal.class));
	}

	@Test
	void givenInvalidAuthor_whenAddJournal_thenThrowIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () ->
			journalService.addJournal(adminUser, "Nature", null)
		);
		verify(journalRepo, never()).addJournal(any(Journal.class));
	}

	@Test
	void givenRepositoryFailure_whenAddJournal_thenThrowIllegalStateException() {
		when(journalRepo.addJournal(any(Journal.class))).thenReturn(false);

		assertThrows(IllegalStateException.class, () ->
			journalService.addJournal(adminUser, "Nature", "John Smith")
		);
	}

	@Test
	void whenGetAllJournals_thenReturnAllJournals() {
		Journal journal1 = new Journal("Journal 1", "Author 1");
		Journal journal2 = new Journal("Journal 2", "Author 2");
		List<Journal> journals = Arrays.asList(journal1, journal2);
		when(journalRepo.getAllJournals()).thenReturn(journals);

		List<Journal> result = journalService.getAllJournals();

		assertEquals(2, result.size());
		verify(journalRepo).getAllJournals();
	}

	@Test
	void givenExistingJournal_whenGetJournalById_thenReturnJournal() {
		UUID journalId = UUID.randomUUID();
		Journal journal = new Journal("Nature", "John Smith");
		when(journalRepo.getJournalById(journalId)).thenReturn(Optional.of(journal));

		Journal result = journalService.getJournalById(journalId);

		assertNotNull(result);
		assertEquals("Nature", result.getTitle());
		verify(journalRepo).getJournalById(journalId);
	}

	@Test
	void givenNonExistingJournal_whenGetJournalById_thenThrowIllegalArgumentException() {
		UUID journalId = UUID.randomUUID();
		when(journalRepo.getJournalById(journalId)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () ->
			journalService.getJournalById(journalId)
		);
	}

	@Test
	void givenAdminUser_whenUpdateJournal_thenJournalIsUpdated() throws PermissionDeniedException {
		UUID journalId = UUID.randomUUID();
		Journal journal = new Journal("Old Title", "Old Author");
		when(journalRepo.getJournalById(journalId)).thenReturn(Optional.of(journal));
		when(journalRepo.updateJournal(journal)).thenReturn(true);

		Journal result = journalService.updateJournal(adminUser, journalId, "New Title", "New Author");

		assertEquals("New Title", result.getTitle());
		assertEquals("New Author", result.getAuthor());
		verify(journalRepo).updateJournal(journal);
	}

	@Test
	void givenAdminUser_whenUpdateJournalWithNullTitle_thenOnlyAuthorIsUpdated() throws PermissionDeniedException {
		UUID journalId = UUID.randomUUID();
		Journal journal = new Journal("Original Title", "Old Author");
		when(journalRepo.getJournalById(journalId)).thenReturn(Optional.of(journal));
		when(journalRepo.updateJournal(journal)).thenReturn(true);

		Journal result = journalService.updateJournal(adminUser, journalId, null, "New Author");

		assertEquals("Original Title", result.getTitle());
		assertEquals("New Author", result.getAuthor());
	}

	@Test
	void givenMemberUser_whenUpdateJournal_thenThrowPermissionDeniedException() {
		UUID journalId = UUID.randomUUID();

		assertThrows(PermissionDeniedException.class, () ->
			journalService.updateJournal(memberUser, journalId, "New Title", "New Author")
		);
		verify(journalRepo, never()).updateJournal(any(Journal.class));
	}

	@Test
	void givenNonExistingJournal_whenUpdateJournal_thenThrowIllegalArgumentException() {
		UUID journalId = UUID.randomUUID();
		when(journalRepo.getJournalById(journalId)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () ->
			journalService.updateJournal(adminUser, journalId, "New Title", "New Author")
		);
	}

	@Test
	void givenRepositoryFailure_whenUpdateJournal_thenThrowIllegalStateException() {
		UUID journalId = UUID.randomUUID();
		Journal journal = new Journal("Title", "Author");
		when(journalRepo.getJournalById(journalId)).thenReturn(Optional.of(journal));
		when(journalRepo.updateJournal(journal)).thenReturn(false);

		assertThrows(IllegalStateException.class, () ->
			journalService.updateJournal(adminUser, journalId, "New Title", "New Author")
		);
	}

	@Test
	void givenAdminUser_whenDeleteJournal_thenJournalIsDeleted() throws PermissionDeniedException {
		UUID journalId = UUID.randomUUID();
		when(journalRepo.deleteJournal(journalId)).thenReturn(true);

		journalService.deleteJournal(adminUser, journalId);

		verify(journalRepo).deleteJournal(journalId);
	}

	@Test
	void givenMemberUser_whenDeleteJournal_thenThrowPermissionDeniedException() {
		UUID journalId = UUID.randomUUID();

		assertThrows(PermissionDeniedException.class, () ->
			journalService.deleteJournal(memberUser, journalId)
		);
		verify(journalRepo, never()).deleteJournal(any(UUID.class));
	}

	@Test
	void givenNonExistingJournal_whenDeleteJournal_thenThrowIllegalArgumentException() {
		UUID journalId = UUID.randomUUID();
		when(journalRepo.deleteJournal(journalId)).thenReturn(false);

		assertThrows(IllegalArgumentException.class, () ->
			journalService.deleteJournal(adminUser, journalId)
		);
	}

	@Test
	void givenKeyword_whenSearchJournals_thenReturnMatchingJournals() {
		Journal journal1 = new Journal("Science Journal", "Author");
		List<Journal> journals = Arrays.asList(journal1);
		when(journalRepo.searchJournals("Science")).thenReturn(journals);

		List<Journal> result = journalService.searchJournals("Science");

		assertEquals(1, result.size());
		verify(journalRepo).searchJournals("Science");
	}

	@Test
	void givenNullKeyword_whenSearchJournals_thenReturnAllJournals() {
		Journal journal1 = new Journal("Journal 1", "Author 1");
		Journal journal2 = new Journal("Journal 2", "Author 2");
		List<Journal> journals = Arrays.asList(journal1, journal2);
		when(journalRepo.getAllJournals()).thenReturn(journals);

		List<Journal> result = journalService.searchJournals(null);

		assertEquals(2, result.size());
		verify(journalRepo).getAllJournals();
	}

	@Test
	void givenBlankKeyword_whenSearchJournals_thenReturnAllJournals() {
		Journal journal1 = new Journal("Journal 1", "Author 1");
		List<Journal> journals = Arrays.asList(journal1);
		when(journalRepo.getAllJournals()).thenReturn(journals);

		List<Journal> result = journalService.searchJournals("   ");

		assertEquals(1, result.size());
		verify(journalRepo).getAllJournals();
	}

	@Test
	void givenAvailableJournal_whenIsAvailableJournal_thenReturnTrue() {
		UUID journalId = UUID.randomUUID();
		Journal journal = new Journal("Journal", "Author");
		when(journalRepo.getJournalById(journalId)).thenReturn(Optional.of(journal));

		boolean result = journalService.isAvailableJournal(journalId);

		assertTrue(result);
	}

	@Test
	void givenBorrowedJournal_whenIsAvailableJournal_thenReturnFalse() {
		UUID journalId = UUID.randomUUID();
		Journal journal = new Journal("Journal", "Author");
		journal.setBorrowed(true);
		when(journalRepo.getJournalById(journalId)).thenReturn(Optional.of(journal));

		boolean result = journalService.isAvailableJournal(journalId);

		assertFalse(result);
	}

	@Test
	void givenNonExistingJournal_whenIsAvailableJournal_thenReturnFalse() {
		UUID journalId = UUID.randomUUID();
		when(journalRepo.getJournalById(journalId)).thenReturn(Optional.empty());

		boolean result = journalService.isAvailableJournal(journalId);

		assertFalse(result);
	}

	@Test
	void givenExistingJournal_whenIsValidJournal_thenReturnTrue() {
		UUID journalId = UUID.randomUUID();
		Journal journal = new Journal("Journal", "Author");
		when(journalRepo.getJournalById(journalId)).thenReturn(Optional.of(journal));

		boolean result = journalService.isValidJournal(journalId);

		assertTrue(result);
	}

	@Test
	void givenNonExistingJournal_whenIsValidJournal_thenReturnFalse() {
		UUID journalId = UUID.randomUUID();
		when(journalRepo.getJournalById(journalId)).thenReturn(Optional.empty());

		boolean result = journalService.isValidJournal(journalId);

		assertFalse(result);
	}

	@Test
	void givenAvailableJournal_whenBorrowJournal_thenJournalIsBorrowed() {
		UUID journalId = UUID.randomUUID();
		Journal journal = new Journal("Journal", "Author");
		when(journalRepo.getJournalById(journalId)).thenReturn(Optional.of(journal));
		when(journalRepo.updateJournal(journal)).thenReturn(true);

		journalService.borrowJournal(memberUser, journalId);

		assertTrue(journal.isBorrowed());
		verify(journalRepo).updateJournal(journal);
	}

	@Test
	void givenBorrowedJournal_whenBorrowJournal_thenThrowIllegalStateException() {
		UUID journalId = UUID.randomUUID();
		Journal journal = new Journal("Journal", "Author");
		journal.setBorrowed(true);
		when(journalRepo.getJournalById(journalId)).thenReturn(Optional.of(journal));

		assertThrows(IllegalStateException.class, () ->
			journalService.borrowJournal(memberUser, journalId)
		);
	}

	@Test
	void givenNonExistingJournal_whenBorrowJournal_thenThrowIllegalArgumentException() {
		UUID journalId = UUID.randomUUID();
		when(journalRepo.getJournalById(journalId)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () ->
			journalService.borrowJournal(memberUser, journalId)
		);
	}

	@Test
	void givenBorrowedJournal_whenReturnJournal_thenJournalIsReturned() {
		UUID journalId = UUID.randomUUID();
		Journal journal = new Journal("Journal", "Author");
		journal.setBorrowed(true);
		when(journalRepo.getJournalById(journalId)).thenReturn(Optional.of(journal));
		when(journalRepo.updateJournal(journal)).thenReturn(true);

		journalService.returnJournal(memberUser, journalId);

		assertFalse(journal.isBorrowed());
		verify(journalRepo).updateJournal(journal);
	}

	@Test
	void givenAvailableJournal_whenReturnJournal_thenThrowIllegalStateException() {
		UUID journalId = UUID.randomUUID();
		Journal journal = new Journal("Journal", "Author");
		when(journalRepo.getJournalById(journalId)).thenReturn(Optional.of(journal));

		assertThrows(IllegalStateException.class, () ->
			journalService.returnJournal(memberUser, journalId)
		);
	}

	@Test
	void givenNonExistingJournal_whenReturnJournal_thenThrowIllegalArgumentException() {
		UUID journalId = UUID.randomUUID();
		when(journalRepo.getJournalById(journalId)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () ->
			journalService.returnJournal(memberUser, journalId)
		);
	}
}
