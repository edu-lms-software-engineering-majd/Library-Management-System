package lms.application;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
class JournalServiceDeleteTest {

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

		adminUser = new UserDTO(UUID.randomUUID(), "ahmad", "Ahmad", "Salameh", "ahmad@example.com", Role.ADMIN);

		memberUser = new UserDTO(UUID.randomUUID(), "majd", "Majd", "Awwad", "majd@example.com", Role.MEMBER);
	}

	@Test
	void shouldDeleteJournal() throws PermissionDeniedException {
		UUID id = UUID.randomUUID();
		Journal journal = org.mockito.Mockito.mock(Journal.class);

		when(journalRepo.getJournalById(id)).thenReturn(Optional.of(journal));
		when(journalRepo.deleteJournal(id)).thenReturn(true);

		boolean result = journalService.deleteJournal(adminUser, id);

		assertTrue(result);
		verify(journalRepo).deleteJournal(id);
	}

	@Test
	void shouldRejectDeleteForMember() {
		UUID id = UUID.randomUUID();
		assertThrows(PermissionDeniedException.class, () -> journalService.deleteJournal(memberUser, id));
	}

	@Test
	void shouldThrowWhenDeletingMissingJournal() {
		UUID id = UUID.randomUUID();
		when(journalRepo.getJournalById(id)).thenReturn(Optional.empty());
		assertThrows(IllegalArgumentException.class, () -> journalService.deleteJournal(adminUser, id));
	}
}
