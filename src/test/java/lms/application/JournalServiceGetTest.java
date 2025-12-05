package lms.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
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

import lms.domain.Journal;
import lms.domain.JournalsRepository;
import lms.domain.Role;
import lms.domain.UserRepository;

@ExtendWith(MockitoExtension.class)
class JournalServiceGetTest {

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
	void shouldReturnJournalById() {
		UUID id = UUID.randomUUID();
		Journal journal = mock(Journal.class);

		when(journalRepo.getJournalById(id)).thenReturn(Optional.of(journal));

		Journal result = journalService.getJournalById(id);

		assertNotNull(result);
	}

	@Test
	void shouldThrowWhenNotFoundById() {
		UUID id = UUID.randomUUID();
		when(journalRepo.getJournalById(id)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () -> journalService.getJournalById(id));
	}

	@Test
	void shouldReturnBySubId() {
		Journal j = mock(Journal.class);
		UUID id = UUID.fromString("66666666-0000-0000-0000-000000000001");

		when(j.getId()).thenReturn(id);
		when(journalRepo.getAllJournals()).thenReturn(List.of(j));

		Journal result = journalService.getJournalBySubId("6666");

		assertNotNull(result);
	}

	@Test
	void shouldThrowWhenMultipleSubIdMatches() {
		Journal j1 = mock(Journal.class);
		Journal j2 = mock(Journal.class);

		when(j1.getId()).thenReturn(UUID.fromString("77777777-0000-0000-0000-000000000001"));
		when(j2.getId()).thenReturn(UUID.fromString("77777777-0000-0000-0000-000000000002"));

		when(journalRepo.getAllJournals()).thenReturn(List.of(j1, j2));

		assertThrows(IllegalArgumentException.class, () -> journalService.getJournalBySubId("7777"));
	}

	@Test
	void shouldThrowWhenNoSubIdMatch() {
		when(journalRepo.getAllJournals()).thenReturn(List.of());

		assertThrows(IllegalArgumentException.class, () -> journalService.getJournalBySubId("9999"));
	}

	@Test
	void shouldReturnAllJournals() {
		Journal j = mock(Journal.class);
		when(journalRepo.getAllJournals()).thenReturn(List.of(j));

		List<Journal> result = journalService.getAllJournals();

		assertEquals(1, result.size());
		verify(journalRepo).getAllJournals();
	}
}
