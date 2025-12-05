package lms.application;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
class JournalServiceAddTest {

	@Mock
	private JournalsRepository journalRepo;

	@Mock
	private UserRepository userRepo;

	private JournalService journalService;

	private UserDTO adminUser;
	private UserDTO memberUser;

	@BeforeEach
	void setUp() {
		journalService = new JournalService(journalRepo);

		adminUser = new UserDTO(UUID.randomUUID(), "ahmad", "Ahmad", "Salameh", "ahmad@example.com", Role.ADMIN);

		memberUser = new UserDTO(UUID.randomUUID(), "majd", "Majd", "Awwad", "majd@example.com", Role.MEMBER);
	}

	@Test
	void shouldAddJournalWhenAdmin() throws PermissionDeniedException {
		when(journalRepo.addJournal(any(Journal.class))).thenReturn(true);

		Journal journal = journalService.addJournal(adminUser, "Title 1", "Author 1");

		assertNotNull(journal);
		verify(journalRepo).addJournal(any(Journal.class));
	}

	@Test
	void shouldAddJournalWithTotalCopies() throws PermissionDeniedException {
		when(journalRepo.addJournal(any(Journal.class))).thenReturn(true);

		Journal journal = journalService.addJournal(adminUser, "Title 2", "Author 2", 5);

		assertNotNull(journal);
		verify(journalRepo).addJournal(any(Journal.class));
	}

	@Test
	void shouldRejectAddJournalForMember() {
		assertThrows(PermissionDeniedException.class,
				() -> journalService.addJournal(memberUser, "Title 3", "Author 3"));
	}

	@Test
	void shouldThrowWhenRepoFailsToAdd() {
		when(journalRepo.addJournal(any(Journal.class))).thenReturn(false);

		assertThrows(IllegalStateException.class, () -> journalService.addJournal(adminUser, "Title 4", "Author 4"));
	}
}
