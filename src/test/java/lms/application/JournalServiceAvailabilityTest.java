package lms.application;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
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
		journalService = new JournalService(journalRepo);
	}

	@Test
	void returnsTrueWhenJournalIsAvailable() {
		UUID journalId = UUID.randomUUID();
		Journal journal = mock(Journal.class);
		when(journal.isBorrowed()).thenReturn(false);
		when(journalRepo.getJournalById(journalId)).thenReturn(Optional.of(journal));

		boolean result = journalService.isAvailableJournal(journalId);

		assertTrue(result);
	}

	@Test
	void returnsFalseWhenJournalIsBorrowed() {
		UUID journalId = UUID.randomUUID();
		Journal journal = mock(Journal.class);
		when(journal.isBorrowed()).thenReturn(true);
		when(journalRepo.getJournalById(journalId)).thenReturn(Optional.of(journal));

		boolean result = journalService.isAvailableJournal(journalId);

		assertFalse(result);
	}

	@Test
	void returnsFalseWhenJournalDoesNotExist() {
		UUID journalId = UUID.randomUUID();
		when(journalRepo.getJournalById(journalId)).thenReturn(Optional.empty());

		boolean result = journalService.isAvailableJournal(journalId);

		assertFalse(result);
	}

	@Test
	void returnsTrueWhenJournalExists() {
		UUID journalId = UUID.randomUUID();
		Journal journal = mock(Journal.class);
		when(journalRepo.getJournalById(journalId)).thenReturn(Optional.of(journal));

		boolean result = journalService.isValidJournal(journalId);

		assertTrue(result);
	}

	@Test
	void returnsFalseWhenJournalDoesNotExistForValidation() {
		UUID journalId = UUID.randomUUID();
		when(journalRepo.getJournalById(journalId)).thenReturn(Optional.empty());

		boolean result = journalService.isValidJournal(journalId);

		assertFalse(result);
	}
}