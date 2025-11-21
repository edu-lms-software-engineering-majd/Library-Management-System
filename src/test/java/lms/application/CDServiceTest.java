package lms.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
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

import lms.domain.CD;
import lms.domain.CDRepository;
import lms.domain.Role;
import lms.domain.exception.PermissionDeniedException;

@ExtendWith(MockitoExtension.class)
class CDServiceTest {

	@Mock
	private CDRepository cdRepo;

	private CDService cdService;
	private UserDTO adminUser;
	private UserDTO memberUser;

	@BeforeEach
	void setUp() {
		cdService = new CDService(cdRepo);

		adminUser = new UserDTO(UUID.randomUUID(), "ahmad", "Ahmad", "Salameh", Role.ADMIN);
		memberUser = new UserDTO(UUID.randomUUID(), "majd", "Majd", "Awwad", Role.MEMBER);
	}

	// ========================================================================
	// ADD TESTS
	// ========================================================================
	@Nested
	@DisplayName("Add CD Tests")
	class AddTests {

		@Test
		@DisplayName("Should add CD when ADMIN")
		void shouldAddCDWhenAdmin() throws PermissionDeniedException {
			when(cdRepo.addCD(any(CD.class))).thenReturn(true);

			CD cd = cdService.addCD(adminUser, "Album 1", "Artist X");

			assertNotNull(cd);
			verify(cdRepo).addCD(any(CD.class));
		}

		@Test
		@DisplayName("Should add CD with total copies when ADMIN")
		void shouldAddCDWithTotalCopies() throws PermissionDeniedException {
			when(cdRepo.addCD(any(CD.class))).thenReturn(true);

			CD cd = cdService.addCD(adminUser, "Album 2", "Artist Y", 5);

			assertNotNull(cd);
			verify(cdRepo).addCD(any(CD.class));
		}

		@Test
		@DisplayName("Should reject addCD when MEMBER")
		void shouldRejectAddCDForMember() {
			assertThrows(PermissionDeniedException.class, () -> cdService.addCD(memberUser, "Album", "Artist"));
		}

		@Test
		@DisplayName("Should throw if repo fails to add CD")
		void shouldThrowIfRepositoryFails() {
			when(cdRepo.addCD(any(CD.class))).thenReturn(false);

			assertThrows(IllegalStateException.class, () -> cdService.addCD(adminUser, "Album", "Artist"));
		}
	}

	// ========================================================================
	// GET TESTS
	// ========================================================================
	@Nested
	@DisplayName("Get CD Tests")
	class GetCDTests {

		@Test
		@DisplayName("Should return CD by ID")
		void shouldReturnById() {
			UUID id = UUID.randomUUID();
			CD cd = mock(CD.class);

			when(cdRepo.getCDById(id)).thenReturn(Optional.of(cd));

			CD result = cdService.getCDById(id);

			assertNotNull(result);
		}

		@Test
		@DisplayName("Should throw when CD not found by ID")
		void shouldThrowWhenNotFoundById() {
			UUID id = UUID.randomUUID();
			when(cdRepo.getCDById(id)).thenReturn(Optional.empty());

			assertThrows(IllegalArgumentException.class, () -> cdService.getCDById(id));
		}

		@Test
		@DisplayName("Should return CD by unique subId")
		void shouldReturnBySubId() {
			CD cd = mock(CD.class);
			UUID id = UUID.fromString("44444444-0000-0000-0000-000000000001");

			when(cd.getId()).thenReturn(id);
			when(cdRepo.getAllCDs()).thenReturn(List.of(cd));

			CD result = cdService.getCDBySubId("4444");

			assertNotNull(result);
		}

		@Test
		@DisplayName("Should throw when multiple subId matches")
		void shouldThrowWhenMultipleSubIdMatches() {
			CD cd1 = mock(CD.class);
			CD cd2 = mock(CD.class);

			when(cd1.getId()).thenReturn(UUID.fromString("55555555-0000-0000-0000-000000000001"));
			when(cd2.getId()).thenReturn(UUID.fromString("55555555-0000-0000-0000-000000000002"));

			when(cdRepo.getAllCDs()).thenReturn(List.of(cd1, cd2));

			assertThrows(IllegalArgumentException.class, () -> cdService.getCDBySubId("5555"));
		}

		@Test
		@DisplayName("Should throw when no subId match")
		void shouldThrowWhenNoSubIdMatch() {
			when(cdRepo.getAllCDs()).thenReturn(List.of());

			assertThrows(IllegalArgumentException.class, () -> cdService.getCDBySubId("9999"));
		}
	}

	// ========================================================================
	// UPDATE TESTS
	// ========================================================================
	@Nested
	@DisplayName("Update CD Tests")
	class UpdateTests {

		@Test
		@DisplayName("Should update CD when ADMIN")
		void shouldUpdateCD() throws PermissionDeniedException {
			UUID id = UUID.randomUUID();
			CD cd = mock(CD.class);

			when(cdRepo.getCDById(id)).thenReturn(Optional.of(cd));
			when(cdRepo.updateCD(cd)).thenReturn(true);

			CD result = cdService.updateCD(adminUser, id, "New T", "New A", 10);

			assertNotNull(result);
			verify(cdRepo).updateCD(cd);
		}

		@Test
		@DisplayName("Should reject update when MEMBER")
		void shouldRejectUpdateForMember() {
			UUID id = UUID.randomUUID();

			assertThrows(PermissionDeniedException.class, () -> cdService.updateCD(memberUser, id, "T", "A", 5));
		}

		@Test
		@DisplayName("Should throw when CD not found for update")
		void shouldThrowWhenUpdateTargetMissing() {
			UUID id = UUID.randomUUID();

			when(cdRepo.getCDById(id)).thenReturn(Optional.empty());

			assertThrows(IllegalArgumentException.class, () -> cdService.updateCD(adminUser, id, "T", "A", 5));
		}

		@Test
		@DisplayName("Should throw when repo update fails")
		void shouldThrowWhenRepoUpdateFails() {
			UUID id = UUID.randomUUID();
			CD cd = mock(CD.class);

			when(cdRepo.getCDById(id)).thenReturn(Optional.of(cd));
			when(cdRepo.updateCD(cd)).thenReturn(false);

			assertThrows(IllegalStateException.class, () -> cdService.updateCD(adminUser, id, "T", "A", 5));
		}
	}

	// ========================================================================
	// DELETE TESTS
	// ========================================================================
	@Nested
	@DisplayName("Delete CD Tests")
	class DeleteTests {

		@Test
		@DisplayName("Should delete CD when ADMIN")
		void shouldDeleteCD() throws PermissionDeniedException {
			UUID id = UUID.randomUUID();
			CD cd = mock(CD.class);

			when(cdRepo.getCDById(id)).thenReturn(Optional.of(cd));
			when(cdRepo.deleteCD(id)).thenReturn(true);

			assertTrue(cdService.deleteCD(adminUser, id));
		}

		@Test
		@DisplayName("Should reject delete when MEMBER")
		void shouldRejectDeleteForMember() {
			UUID id = UUID.randomUUID();

			assertThrows(PermissionDeniedException.class, () -> cdService.deleteCD(memberUser, id));
		}

		@Test
		@DisplayName("Should throw when CD missing before delete")
		void shouldThrowWhenDeletingMissingCD() {
			UUID id = UUID.randomUUID();
			when(cdRepo.getCDById(id)).thenReturn(Optional.empty());

			assertThrows(IllegalArgumentException.class, () -> cdService.deleteCD(adminUser, id));
		}
	}

	// ========================================================================
	// AVAILABILITY TESTS
	// ========================================================================
	@Nested
	@DisplayName("Availability Tests")
	class AvailabilityTests {

		@Test
		@DisplayName("Should return true when CD available")
		void shouldReturnAvailable() {
			UUID id = UUID.randomUUID();
			CD cd = mock(CD.class);

			when(cd.getAvailableCopies()).thenReturn(3);
			when(cdRepo.getCDById(id)).thenReturn(Optional.of(cd));

			assertTrue(cdService.isAvailableCD(id));
		}

		@Test
		@DisplayName("Should return false when CD unavailable")
		void shouldReturnUnavailable() {
			UUID id = UUID.randomUUID();
			CD cd = mock(CD.class);

			when(cd.getAvailableCopies()).thenReturn(0);
			when(cdRepo.getCDById(id)).thenReturn(Optional.of(cd));

			assertFalse(cdService.isAvailableCD(id));
		}

		@Test
		@DisplayName("Should return false when CD does not exist (availability)")
		void shouldReturnFalseWhenMissingAvailability() {
			UUID id = UUID.randomUUID();
			when(cdRepo.getCDById(id)).thenReturn(Optional.empty());

			assertFalse(cdService.isAvailableCD(id));
		}

		@Test
		@DisplayName("Should validate CD existence")
		void shouldValidateCDExistence() {
			UUID id = UUID.randomUUID();
			when(cdRepo.getCDById(id)).thenReturn(Optional.of(mock(CD.class)));

			assertTrue(cdService.isValidCD(id));
		}

		@Test
		@DisplayName("Should invalidate missing CD")
		void shouldInvalidateMissingCD() {
			UUID id = UUID.randomUUID();
			when(cdRepo.getCDById(id)).thenReturn(Optional.empty());

			assertFalse(cdService.isValidCD(id));
		}
	}

	// ========================================================================
	// BORROW / RETURN TESTS
	// ========================================================================
	@Nested
	@DisplayName("Borrow / Return Tests")
	class BorrowReturnTests {

		@Test
		@DisplayName("Should borrow CD when available")
		void shouldBorrowCD() {
			UUID id = UUID.randomUUID();
			CD cd = mock(CD.class);

			when(cdRepo.getCDById(id)).thenReturn(Optional.of(cd));
			when(cd.isAvailable()).thenReturn(true);
			when(cdRepo.updateCD(cd)).thenReturn(true);

			cdService.borrowCD(adminUser, id);

			verify(cdRepo).updateCD(cd);
		}

		@Test
		@DisplayName("Should throw when borrowing unavailable CD")
		void shouldThrowWhenUnavailableBorrow() {
			UUID id = UUID.randomUUID();
			CD cd = mock(CD.class);

			when(cdRepo.getCDById(id)).thenReturn(Optional.of(cd));
			when(cd.isAvailable()).thenReturn(false);

			assertThrows(IllegalStateException.class, () -> cdService.borrowCD(adminUser, id));
		}

		@Test
		@DisplayName("Should throw when borrow update fails")
		void shouldThrowWhenBorrowUpdateFails() {
			UUID id = UUID.randomUUID();
			CD cd = mock(CD.class);

			when(cdRepo.getCDById(id)).thenReturn(Optional.of(cd));
			when(cd.isAvailable()).thenReturn(true);
			when(cdRepo.updateCD(cd)).thenReturn(false);

			assertThrows(IllegalStateException.class, () -> cdService.borrowCD(adminUser, id));
		}

		@Test
		@DisplayName("Should return CD successfully")
		void shouldReturnCD() {
			UUID id = UUID.randomUUID();
			CD cd = mock(CD.class);

			when(cdRepo.getCDById(id)).thenReturn(Optional.of(cd));
			when(cd.getAvailableCopies()).thenReturn(1);
			when(cd.getTotalCopies()).thenReturn(5);
			when(cdRepo.updateCD(cd)).thenReturn(true);

			cdService.returnCD(adminUser, id);

			verify(cdRepo).updateCD(cd);
		}

		@Test
		@DisplayName("Should throw when returning CD beyond total copies")
		void shouldThrowWhenReturningBeyondTotal() {
			UUID id = UUID.randomUUID();
			CD cd = mock(CD.class);

			when(cdRepo.getCDById(id)).thenReturn(Optional.of(cd));
			when(cd.getAvailableCopies()).thenReturn(5);
			when(cd.getTotalCopies()).thenReturn(5);

			assertThrows(IllegalStateException.class, () -> cdService.returnCD(adminUser, id));
		}

		@Test
		@DisplayName("Should throw when return update fails")
		void shouldThrowWhenReturnUpdateFails() {
			UUID id = UUID.randomUUID();
			CD cd = mock(CD.class);

			when(cdRepo.getCDById(id)).thenReturn(Optional.of(cd));
			when(cd.getAvailableCopies()).thenReturn(1);
			when(cd.getTotalCopies()).thenReturn(5);
			when(cdRepo.updateCD(cd)).thenReturn(false);

			assertThrows(IllegalStateException.class, () -> cdService.returnCD(adminUser, id));
		}
	}

	// ========================================================================
	// SEARCH TESTS
	// ========================================================================
	@Nested
	@DisplayName("Search Tests")
	class SearchTests {

		@Test
		@DisplayName("Should return all CDs when keyword null")
		void shouldReturnAllWhenNullKeyword() {
			CD cd = mock(CD.class);
			when(cdRepo.getAllCDs()).thenReturn(List.of(cd));

			List<CD> result = cdService.searchCDs((String) null);

			assertEquals(1, result.size());
		}

		@Test
		@DisplayName("Should return all CDs when keyword blank")
		void shouldReturnAllWhenBlankKeyword() {
			CD cd = mock(CD.class);
			when(cdRepo.getAllCDs()).thenReturn(List.of(cd));

			List<CD> result = cdService.searchCDs("   ");

			assertEquals(1, result.size());
		}

		@Test
		@DisplayName("Should use repository search for non-empty keyword")
		void shouldSearchUsingRepo() {
			when(cdRepo.searchCDs("rock")).thenReturn(List.of());

			cdService.searchCDs("rock");

			verify(cdRepo).searchCDs("rock");
		}

		@Test
		@DisplayName("Should throw when strategy null")
		void shouldThrowWhenStrategyNull() {
			assertThrows(IllegalArgumentException.class, () -> cdService.searchCDs(null, "term"));
		}

		@Test
		@DisplayName("Should use strategy search")
		void shouldUseStrategySearch() {
			var strategy = mock(lms.application.search.SearchStrategy.class);

			CD cd = mock(CD.class);
			when(cdRepo.getAllCDs()).thenReturn(List.of(cd));
			when(strategy.execute(anyList(), anyString())).thenReturn(List.of(cd));

			List<CD> result = cdService.searchCDs(strategy, "a");

			assertEquals(1, result.size());
		}
	}
}
