package lms.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import org.junit.jupiter.api.Nested;
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
class JournalServiceTest {

	@Mock
	private JournalsRepository journalRepo;

	@Mock
	private UserRepository userRepo; // موجود فقط لأن الـ Service يحتاجه في الـ constructor

	private JournalService journalService;

	private UserDTO adminUser;
	private UserDTO memberUser;

	@BeforeEach
	void setUp() {
		journalService = new JournalService(journalRepo, userRepo);

		adminUser = new UserDTO(UUID.randomUUID(), "ahmadsalameh", "Ahmad", "Salameh", Role.ADMIN);
		memberUser = new UserDTO(UUID.randomUUID(), "majdawwad", "Majd", "Awwad", Role.MEMBER);
	}

	// =====================================================================
	// ADD JOURNAL TESTS
	// =====================================================================
	@Nested
	@DisplayName("Add Journal Tests")
	class AddJournalTests {

		@Test
		@DisplayName("Should add journal when ADMIN")
		void shouldAddJournalWhenAdmin() throws PermissionDeniedException {
			when(journalRepo.addJournal(any(Journal.class))).thenReturn(true);

			Journal journal = journalService.addJournal(adminUser, "Title 1", "Author 1");

			assertNotNull(journal);
			verify(journalRepo).addJournal(any(Journal.class));
		}

		@Test
		@DisplayName("Should add journal with total copies when ADMIN")
		void shouldAddJournalWithTotalCopies() throws PermissionDeniedException {
			when(journalRepo.addJournal(any(Journal.class))).thenReturn(true);

			Journal journal = journalService.addJournal(adminUser, "Title 2", "Author 2", 5);

			assertNotNull(journal);
			verify(journalRepo).addJournal(any(Journal.class));
		}

		@Test
		@DisplayName("Should reject addJournal when MEMBER")
		void shouldRejectAddJournalForMember() {
			assertThrows(PermissionDeniedException.class,
					() -> journalService.addJournal(memberUser, "Title 3", "Author 3"));
		}

		@Test
		@DisplayName("Should throw IllegalState when repository fails to add")
		void shouldThrowWhenRepoFailsToAdd() {
			when(journalRepo.addJournal(any(Journal.class))).thenReturn(false);

			assertThrows(IllegalStateException.class,
					() -> journalService.addJournal(adminUser, "Title 4", "Author 4"));
		}
	}

	// =====================================================================
	// GET JOURNAL TESTS
	// =====================================================================
	@Nested
	@DisplayName("Get Journal Tests")
	class GetJournalTests {

		@Test
		@DisplayName("Should return journal by ID")
		void shouldReturnJournalById() {
			UUID id = UUID.randomUUID();
			Journal journal = mock(Journal.class);

			when(journalRepo.getJournalById(id)).thenReturn(Optional.of(journal));

			Journal result = journalService.getJournalById(id);

			assertNotNull(result);
		}

		@Test
		@DisplayName("Should throw when journal not found by ID")
		void shouldThrowWhenNotFoundById() {
			UUID id = UUID.randomUUID();
			when(journalRepo.getJournalById(id)).thenReturn(Optional.empty());

			assertThrows(IllegalArgumentException.class, () -> journalService.getJournalById(id));
		}

		@Test
		@DisplayName("Should return journal by unique subId")
		void shouldReturnBySubId() {
			Journal j = mock(Journal.class);
			UUID id = UUID.fromString("66666666-0000-0000-0000-000000000001");
			when(j.getId()).thenReturn(id);
			when(journalRepo.getAllJournals()).thenReturn(List.of(j));

			Journal result = journalService.getJournalBySubId("6666");

			assertNotNull(result);
		}

		@Test
		@DisplayName("Should throw when multiple journals match subId")
		void shouldThrowWhenMultipleSubIdMatches() {
			Journal j1 = mock(Journal.class);
			Journal j2 = mock(Journal.class);

			when(j1.getId()).thenReturn(UUID.fromString("77777777-0000-0000-0000-000000000001"));
			when(j2.getId()).thenReturn(UUID.fromString("77777777-0000-0000-0000-000000000002"));

			when(journalRepo.getAllJournals()).thenReturn(List.of(j1, j2));

			assertThrows(IllegalArgumentException.class, () -> journalService.getJournalBySubId("7777"));
		}

		@Test
		@DisplayName("Should throw when no journal matches subId")
		void shouldThrowWhenNoSubIdMatch() {
			when(journalRepo.getAllJournals()).thenReturn(List.of());

			assertThrows(IllegalArgumentException.class, () -> journalService.getJournalBySubId("9999"));
		}

		@Test
		@DisplayName("Should return all journals")
		void shouldReturnAllJournals() {
			Journal j = mock(Journal.class);
			when(journalRepo.getAllJournals()).thenReturn(List.of(j));

			List<Journal> result = journalService.getAllJournals();

			assertEquals(1, result.size());
			verify(journalRepo).getAllJournals();
		}
	}

	// =====================================================================
	// UPDATE JOURNAL TESTS
	// =====================================================================
	@Nested
	@DisplayName("Update Journal Tests")
	class UpdateJournalTests {

		@Test
		@DisplayName("Should update journal when ADMIN")
		void shouldUpdateJournal() throws PermissionDeniedException {
			UUID id = UUID.randomUUID();
			Journal journal = mock(Journal.class);

			when(journalRepo.getJournalById(id)).thenReturn(Optional.of(journal));
			when(journal.getAvailableCopies()).thenReturn(2);
			when(journalRepo.updateJournal(journal)).thenReturn(true);

			Journal result = journalService.updateJournal(adminUser, id, "New Title", "New Author", 5);

			assertNotNull(result);
			verify(journalRepo).updateJournal(journal);
		}

		@Test
		@DisplayName("Should reject update when MEMBER")
		void shouldRejectUpdateForMember() {
			UUID id = UUID.randomUUID();

			assertThrows(PermissionDeniedException.class,
					() -> journalService.updateJournal(memberUser, id, "T", "A", 5));
		}

		@Test
		@DisplayName("Should throw when journal not found for update")
		void shouldThrowWhenUpdateTargetMissing() {
			UUID id = UUID.randomUUID();
			when(journalRepo.getJournalById(id)).thenReturn(Optional.empty());

			assertThrows(IllegalArgumentException.class,
					() -> journalService.updateJournal(adminUser, id, "T", "A", 5));
		}

		@Test
		@DisplayName("Should throw when new total copies less than available")
		void shouldThrowWhenNewTotalLessThanAvailable() {
			UUID id = UUID.randomUUID();
			Journal journal = mock(Journal.class);

			when(journalRepo.getJournalById(id)).thenReturn(Optional.of(journal));
			when(journal.getAvailableCopies()).thenReturn(5);

			assertThrows(IllegalArgumentException.class,
					() -> journalService.updateJournal(adminUser, id, null, null, 3));
		}

		@Test
		@DisplayName("Should throw when repository update fails")
		void shouldThrowWhenRepoUpdateFails() {
			UUID id = UUID.randomUUID();
			Journal journal = mock(Journal.class);

			when(journalRepo.getJournalById(id)).thenReturn(Optional.of(journal));
			when(journal.getAvailableCopies()).thenReturn(1);
			when(journalRepo.updateJournal(journal)).thenReturn(false);

			assertThrows(IllegalStateException.class, () -> journalService.updateJournal(adminUser, id, "T", "A", 5));
		}
	}

	// =====================================================================
	// DELETE JOURNAL TESTS
	// =====================================================================
	@Nested
	@DisplayName("Delete Journal Tests")
	class DeleteJournalTests {

		@Test
		@DisplayName("Should delete journal when ADMIN")
		void shouldDeleteJournal() throws PermissionDeniedException {
			UUID id = UUID.randomUUID();
			Journal journal = mock(Journal.class);

			when(journalRepo.getJournalById(id)).thenReturn(Optional.of(journal));
			when(journalRepo.deleteJournal(id)).thenReturn(true);

			boolean result = journalService.deleteJournal(adminUser, id);

			assertTrue(result);
			verify(journalRepo).deleteJournal(id);
		}

		@Test
		@DisplayName("Should reject delete when MEMBER")
		void shouldRejectDeleteForMember() {
			UUID id = UUID.randomUUID();

			assertThrows(PermissionDeniedException.class, () -> journalService.deleteJournal(memberUser, id));
		}

		@Test
		@DisplayName("Should throw when deleting non-existing journal")
		void shouldThrowWhenDeletingMissingJournal() {
			UUID id = UUID.randomUUID();
			when(journalRepo.getJournalById(id)).thenReturn(Optional.empty());

			assertThrows(IllegalArgumentException.class, () -> journalService.deleteJournal(adminUser, id));
		}
	}

	// =====================================================================
	// AVAILABILITY / VALIDITY TESTS
	// =====================================================================
	@Nested
	@DisplayName("Availability & Validity Tests")
	class AvailabilityTests {

		@Test
		@DisplayName("Should return true when journal available")
		void shouldReturnTrueWhenAvailable() {
			UUID id = UUID.randomUUID();
			Journal journal = mock(Journal.class);

			when(journal.isBorrowed()).thenReturn(false);
			when(journalRepo.getJournalById(id)).thenReturn(Optional.of(journal));

			assertTrue(journalService.isAvailableJournal(id));
		}

		@Test
		@DisplayName("Should return false when journal borrowed")
		void shouldReturnFalseWhenBorrowed() {
			UUID id = UUID.randomUUID();
			Journal journal = mock(Journal.class);

			when(journal.isBorrowed()).thenReturn(true);
			when(journalRepo.getJournalById(id)).thenReturn(Optional.of(journal));

			assertFalse(journalService.isAvailableJournal(id));
		}

		@Test
		@DisplayName("Should return false when journal not found (availability)")
		void shouldReturnFalseWhenMissingAvailability() {
			UUID id = UUID.randomUUID();
			when(journalRepo.getJournalById(id)).thenReturn(Optional.empty());

			assertFalse(journalService.isAvailableJournal(id));
		}

		@Test
		@DisplayName("Should validate existing journal")
		void shouldValidateExistingJournal() {
			UUID id = UUID.randomUUID();
			when(journalRepo.getJournalById(id)).thenReturn(Optional.of(mock(Journal.class)));

			assertTrue(journalService.isValidJournal(id));
		}

		@Test
		@DisplayName("Should invalidate missing journal")
		void shouldInvalidateMissingJournal() {
			UUID id = UUID.randomUUID();
			when(journalRepo.getJournalById(id)).thenReturn(Optional.empty());

			assertFalse(journalService.isValidJournal(id));
		}
	}

	// =====================================================================
	// BORROW / RETURN TESTS
	// =====================================================================
	@Nested
	@DisplayName("Borrow / Return Tests")
	class BorrowReturnTests {

		@Test
		@DisplayName("Should borrow journal when available")
		void shouldBorrowJournal() {
			UUID id = UUID.randomUUID();
			Journal journal = mock(Journal.class);

			when(journalRepo.getJournalById(id)).thenReturn(Optional.of(journal));
			when(journal.isAvailable()).thenReturn(true);
			when(journalRepo.updateJournal(journal)).thenReturn(true);

			journalService.borrowJournal(adminUser, id);

			verify(journalRepo).updateJournal(journal);
		}

		@Test
		@DisplayName("Should throw when borrowing unavailable journal")
		void shouldThrowWhenBorrowingUnavailable() {
			UUID id = UUID.randomUUID();
			Journal journal = mock(Journal.class);

			when(journalRepo.getJournalById(id)).thenReturn(Optional.of(journal));
			when(journal.isAvailable()).thenReturn(false);

			assertThrows(IllegalStateException.class, () -> journalService.borrowJournal(adminUser, id));
		}

		@Test
		@DisplayName("Should throw when borrow update fails")
		void shouldThrowWhenBorrowUpdateFails() {
			UUID id = UUID.randomUUID();
			Journal journal = mock(Journal.class);

			when(journalRepo.getJournalById(id)).thenReturn(Optional.of(journal));
			when(journal.isAvailable()).thenReturn(true);
			when(journalRepo.updateJournal(journal)).thenReturn(false);

			assertThrows(IllegalStateException.class, () -> journalService.borrowJournal(adminUser, id));
		}

		@Test
		@DisplayName("Should return journal successfully")
		void shouldReturnJournal() {
			UUID id = UUID.randomUUID();
			Journal journal = mock(Journal.class);

			when(journalRepo.getJournalById(id)).thenReturn(Optional.of(journal));
			when(journal.getAvailableCopies()).thenReturn(1);
			when(journal.getTotalCopies()).thenReturn(5);
			when(journalRepo.updateJournal(journal)).thenReturn(true);

			journalService.returnJournal(adminUser, id);

			verify(journalRepo).updateJournal(journal);
		}

		@Test
		@DisplayName("Should throw when all copies already returned")
		void shouldThrowWhenAllCopiesReturned() {
			UUID id = UUID.randomUUID();
			Journal journal = mock(Journal.class);

			when(journalRepo.getJournalById(id)).thenReturn(Optional.of(journal));
			when(journal.getAvailableCopies()).thenReturn(5);
			when(journal.getTotalCopies()).thenReturn(5);

			assertThrows(IllegalStateException.class, () -> journalService.returnJournal(adminUser, id));
		}

		@Test
		@DisplayName("Should throw when return update fails")
		void shouldThrowWhenReturnUpdateFails() {
			UUID id = UUID.randomUUID();
			Journal journal = mock(Journal.class);

			when(journalRepo.getJournalById(id)).thenReturn(Optional.of(journal));
			when(journal.getAvailableCopies()).thenReturn(1);
			when(journal.getTotalCopies()).thenReturn(5);
			when(journalRepo.updateJournal(journal)).thenReturn(false);

			assertThrows(IllegalStateException.class, () -> journalService.returnJournal(adminUser, id));
		}
	}

	// =====================================================================
	// SEARCH TESTS
	// =====================================================================
	@Nested
	@DisplayName("Search Journals Tests")
	class SearchTests {

		@Test
		@DisplayName("Should return all journals when keyword is null")
		void shouldReturnAllWhenKeywordNull() {
			Journal j = mock(Journal.class);
			when(journalRepo.getAllJournals()).thenReturn(List.of(j));

			List<Journal> result = journalService.searchJournals((String) null);

			assertEquals(1, result.size());
		}

		@Test
		@DisplayName("Should return all journals when keyword is blank")
		void shouldReturnAllWhenKeywordBlank() {
			Journal j = mock(Journal.class);
			when(journalRepo.getAllJournals()).thenReturn(List.of(j));

			List<Journal> result = journalService.searchJournals("   ");

			assertEquals(1, result.size());
		}

		@Test
		@DisplayName("Should use repository search when keyword is not blank")
		void shouldSearchUsingRepository() {
			when(journalRepo.searchJournals("ai")).thenReturn(List.of());

			journalService.searchJournals("ai");

			verify(journalRepo).searchJournals("ai");
		}

		@Test
		@DisplayName("Should throw when strategy is null")
		void shouldThrowWhenStrategyNull() {
			assertThrows(IllegalArgumentException.class, () -> journalService.searchJournals(null, "term"));
		}

		@Test
		@DisplayName("Should use strategy-based search")
		@SuppressWarnings("unchecked")
		void shouldUseStrategySearch() {
			var strategy = mock(lms.application.search.SearchStrategy.class);

			Journal j = mock(Journal.class);
			when(journalRepo.getAllJournals()).thenReturn(List.of(j));
			when(strategy.execute(anyList(), anyString())).thenReturn(List.of(j));

			List<Journal> result = journalService.searchJournals(strategy, "test");

			assertEquals(1, result.size());
			verify(strategy).execute(anyList(), eq("test"));
		}
	}
}
