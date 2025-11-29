package lms.application;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import lms.domain.Journal;
import lms.domain.JournalsRepository;
import lms.domain.Role;
import lms.domain.UserRepository;
import lms.domain.exception.PermissionDeniedException;

@ExtendWith(MockitoExtension.class)
class JournalServiceUpdateTest {

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
		adminUser = new UserDTO(UUID.randomUUID(), "ahmad", "Ahmad", "Salameh", Role.ADMIN);
		memberUser = new UserDTO(UUID.randomUUID(), "majd", "Majd", "Awwad", Role.MEMBER);
	}

	@Test
	void shouldUpdateJournal() throws PermissionDeniedException {
		UUID id = UUID.randomUUID();
		Journal journal = org.mockito.Mockito.mock(Journal.class);

		when(journalRepo.getJournalById(id)).thenReturn(Optional.of(journal));
		when(journal.getAvailableCopies()).thenReturn(2);
		when(journalRepo.updateJournal(journal)).thenReturn(true);

		Journal result = journalService.updateJournal(adminUser, id, "New Title", "New Author", 5);

		assertNotNull(result);
		verify(journalRepo).updateJournal(journal);
	}

	@Test
	void shouldRejectUpdateForMember() {
		UUID id = UUID.randomUUID();

		assertThrows(PermissionDeniedException.class, () -> journalService.updateJournal(memberUser, id, "T", "A", 5));
	}

	
	@Test
	void shouldThrowWhenUpdateTargetMissing() {
		UUID id = UUID.randomUUID();
		when(journalRepo.getJournalById(id)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () -> journalService.updateJournal(adminUser, id, "T", "A", 5));
	}

	@Test
	void shouldThrowWhenNewTotalLessThanAvailable() {
		UUID id = UUID.randomUUID();
		Journal journal = org.mockito.Mockito.mock(Journal.class);

		when(journalRepo.getJournalById(id)).thenReturn(Optional.of(journal));
		when(journal.getAvailableCopies()).thenReturn(5);

		assertThrows(IllegalArgumentException.class, () -> journalService.updateJournal(adminUser, id, null, null, 3));
	}

	@Test
	void shouldThrowWhenRepoUpdateFails() {
		UUID id = UUID.randomUUID();
		Journal journal = org.mockito.Mockito.mock(Journal.class);

		when(journalRepo.getJournalById(id)).thenReturn(Optional.of(journal));
		when(journal.getAvailableCopies()).thenReturn(1);
		when(journalRepo.updateJournal(journal)).thenReturn(false);

		assertThrows(IllegalStateException.class, () -> journalService.updateJournal(adminUser, id, "T", "A", 5));
	}
}
