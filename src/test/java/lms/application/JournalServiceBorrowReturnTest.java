package lms.application;

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
import lms.domain.UserRepository;

@ExtendWith(MockitoExtension.class)
class JournalServiceBorrowReturnTest {

	@Mock
	private JournalsRepository journalRepo;

	@Mock
	private UserRepository userRepo;

	private JournalService journalService;
	private UserDTO adminUser;

	@BeforeEach
	void setUp() {
		journalService = new JournalService(journalRepo, userRepo);
		adminUser = new UserDTO(UUID.randomUUID(), "admin", "Admin", "User", lms.domain.Role.ADMIN);
	}

	@Test
	void shouldBorrowJournal() {
		UUID id = UUID.randomUUID();
		Journal journal = org.mockito.Mockito.mock(Journal.class);

		when(journalRepo.getJournalById(id)).thenReturn(Optional.of(journal));
		when(journal.isAvailable()).thenReturn(true);
		when(journalRepo.updateJournal(journal)).thenReturn(true);

		journalService.borrowJournal(adminUser, id);

		verify(journalRepo).updateJournal(journal);
	}

	@Test
	void shouldThrowWhenBorrowingUnavailable() {
		UUID id = UUID.randomUUID();
		Journal journal = org.mockito.Mockito.mock(Journal.class);

		when(journalRepo.getJournalById(id)).thenReturn(Optional.of(journal));
		when(journal.isAvailable()).thenReturn(false);

		assertThrows(IllegalStateException.class, () -> journalService.borrowJournal(adminUser, id));
	}

	@Test
	void shouldThrowWhenBorrowUpdateFails() {
		UUID id = UUID.randomUUID();
		Journal journal = org.mockito.Mockito.mock(Journal.class);

		when(journalRepo.getJournalById(id)).thenReturn(Optional.of(journal));
		when(journal.isAvailable()).thenReturn(true);
		when(journalRepo.updateJournal(journal)).thenReturn(false);

		assertThrows(IllegalStateException.class, () -> journalService.borrowJournal(adminUser, id));
	}

	@Test
	void shouldReturnJournalSuccessfully() {
		UUID id = UUID.randomUUID();
		Journal journal = org.mockito.Mockito.mock(Journal.class);

		when(journalRepo.getJournalById(id)).thenReturn(Optional.of(journal));
		when(journal.getAvailableCopies()).thenReturn(1);
		when(journal.getTotalCopies()).thenReturn(5);
		when(journalRepo.updateJournal(journal)).thenReturn(true);

		journalService.returnJournal(adminUser, id);

		verify(journalRepo).updateJournal(journal);
	}

	@Test
	void shouldThrowWhenAllCopiesReturned() {
		UUID id = UUID.randomUUID();
		Journal journal = org.mockito.Mockito.mock(Journal.class);

		when(journalRepo.getJournalById(id)).thenReturn(Optional.of(journal));
		when(journal.getAvailableCopies()).thenReturn(5);
		when(journal.getTotalCopies()).thenReturn(5);

		assertThrows(IllegalStateException.class, () -> journalService.returnJournal(adminUser, id));
	}

	@Test
	void shouldThrowWhenReturnUpdateFails() {
		UUID id = UUID.randomUUID();
		Journal journal = org.mockito.Mockito.mock(Journal.class);

		when(journalRepo.getJournalById(id)).thenReturn(Optional.of(journal));
		when(journal.getAvailableCopies()).thenReturn(1);
		when(journal.getTotalCopies()).thenReturn(5);
		when(journalRepo.updateJournal(journal)).thenReturn(false);

		assertThrows(IllegalStateException.class, () -> journalService.returnJournal(adminUser, id));
	}
}
