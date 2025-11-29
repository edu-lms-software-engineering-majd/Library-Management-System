package lms.application;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import lms.domain.UserRepository;

@ExtendWith(MockitoExtension.class)
class JournalServiceAvailabilityTest {

	@Mock
	private JournalsRepository journalRepo;

	@Mock
	private UserRepository userRepo;

	private JournalService journalService;

	@BeforeEach
	void setUp() {
		journalService = new JournalService(journalRepo, userRepo);
	}

	@Test
	void shouldReturnTrueWhenAvailable() {
		UUID id = UUID.randomUUID();
		Journal journal = org.mockito.Mockito.mock(Journal.class);

		when(journal.isAvailable()).thenReturn(true);
		when(journalRepo.getJournalById(id)).thenReturn(Optional.of(journal));

		assertTrue(journalService.isAvailableJournal(id));
	}

	@Test
	void shouldReturnFalseWhenBorrowed() {
		UUID id = UUID.randomUUID();
		Journal journal = org.mockito.Mockito.mock(Journal.class);

		when(journal.isAvailable()).thenReturn(false);
		when(journalRepo.getJournalById(id)).thenReturn(Optional.of(journal));

		assertFalse(journalService.isAvailableJournal(id));
	}

	@Test
	void shouldReturnFalseWhenMissing() {
		UUID id = UUID.randomUUID();

		when(journalRepo.getJournalById(id)).thenReturn(Optional.empty());

		assertFalse(journalService.isAvailableJournal(id));
	}

	@Test
	void shouldValidateExistingJournal() {
		UUID id = UUID.randomUUID();

		when(journalRepo.getJournalById(id)).thenReturn(Optional.of(org.mockito.Mockito.mock(Journal.class)));

		assertTrue(journalService.isValidJournal(id));
	}

	@Test
	void shouldInvalidateMissingJournal() {
		UUID id = UUID.randomUUID();

		when(journalRepo.getJournalById(id)).thenReturn(Optional.empty());

		assertFalse(journalService.isValidJournal(id));
	}
}
