package lms.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import lms.domain.CD;
import lms.domain.CDRepository;
import lms.domain.Role;
import lms.domain.UserRepository;
import lms.domain.exception.PermissionDeniedException;

@ExtendWith(MockitoExtension.class)
class CDServiceTest {

	@Mock
	private CDRepository cdRepo;

	@Mock
	private UserRepository userRepo;

	private CDService cdService;
	private UserDTO adminUser;
	private UserDTO memberUser;

	@BeforeEach
	void setUp() {
		cdService = new CDService(cdRepo, userRepo);
		adminUser = mock(UserDTO.class);
		memberUser = mock(UserDTO.class);
		
		when(adminUser.role()).thenReturn(Role.ADMIN);
		when(memberUser.role()).thenReturn(Role.MEMBER);
	}

	@AfterEach
	void tearDown() {
		cdService = null;
		adminUser = null;
		memberUser = null;
	}

	@Test
	void givenAdminUser_whenAddCD_thenCDIsCreatedAndSaved() {
		when(cdRepo.addCD(any(CD.class))).thenReturn(true);

		try {
			CD result = cdService.addCD(adminUser, "Abbey Road", "The Beatles");

			assertNotNull(result);
			assertEquals("Abbey Road", result.getTitle());
			assertEquals("The Beatles", result.getArtist());
			assertEquals(1, result.getTotalCopies());
			assertEquals(1, result.getAvailableCopies());
			verify(cdRepo).addCD(any(CD.class));
		} catch (PermissionDeniedException e) {
			throw new AssertionError("Should not throw PermissionDeniedException for admin user", e);
		}
	}

	@Test
	void givenMemberUser_whenAddCD_thenThrowPermissionDeniedException() {
		Exception exception = assertThrows(PermissionDeniedException.class, () ->
			cdService.addCD(memberUser, "Abbey Road", "The Beatles")
		);

		verify(cdRepo, never()).addCD(any(CD.class));
	}

	@Test
	void givenNullUser_whenAddCD_thenThrowPermissionDeniedException() {
		Exception exception = assertThrows(PermissionDeniedException.class, () ->
			cdService.addCD(null, "Abbey Road", "The Beatles")
		);

		verify(cdRepo, never()).addCD(any(CD.class));
	}

	@Test
	void givenEmptyTitle_whenAddCD_thenThrowIllegalArgumentException() {
		Exception exception = assertThrows(IllegalArgumentException.class, () ->
			cdService.addCD(adminUser, "", "The Beatles")
		);

		verify(cdRepo, never()).addCD(any(CD.class));
	}

	@Test
	void givenNullArtist_whenAddCD_thenThrowIllegalArgumentException() {
		Exception exception = assertThrows(IllegalArgumentException.class, () ->
			cdService.addCD(adminUser, "Abbey Road", null)
		);

		verify(cdRepo, never()).addCD(any(CD.class));
	}

	@Test
	void givenRepositoryFailure_whenAddCD_thenThrowIllegalStateException() {
		when(cdRepo.addCD(any(CD.class))).thenReturn(false);

		Exception exception = assertThrows(IllegalStateException.class, () ->
			cdService.addCD(adminUser, "Abbey Road", "The Beatles")
		);

		assertTrue(exception.getMessage().contains("Failed to add CD"));
	}

	@Test
	void whenGetAllCDs_thenReturnAllCDs() {
		CD cd1 = new CD("Album 1", "Artist 1");
		CD cd2 = new CD("Album 2", "Artist 2");
		List<CD> expectedCDs = Arrays.asList(cd1, cd2);
		when(cdRepo.getAllCDs()).thenReturn(expectedCDs);

		List<CD> result = cdService.getAllCDs();

		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals("Album 1", result.get(0).getTitle());
		assertEquals("Album 2", result.get(1).getTitle());
		verify(cdRepo).getAllCDs();
	}

	@Test
	void whenGetAllCDs_withEmptyRepository_thenReturnEmptyList() {
		when(cdRepo.getAllCDs()).thenReturn(Collections.emptyList());

		List<CD> result = cdService.getAllCDs();

		assertNotNull(result);
		assertTrue(result.isEmpty());
		verify(cdRepo).getAllCDs();
	}

	@Test
	void givenExistingCDId_whenGetCDById_thenReturnCD() {
		UUID cdId = UUID.randomUUID();
		CD expectedCD = new CD("Abbey Road", "The Beatles");
		when(cdRepo.getCDById(cdId)).thenReturn(Optional.of(expectedCD));

		CD result = cdService.getCDById(cdId);

		assertNotNull(result);
		assertEquals("Abbey Road", result.getTitle());
		assertEquals("The Beatles", result.getArtist());
		verify(cdRepo).getCDById(cdId);
	}

	@Test
	void givenNonExistingCDId_whenGetCDById_thenThrowIllegalArgumentException() {
		UUID cdId = UUID.randomUUID();
		when(cdRepo.getCDById(cdId)).thenReturn(Optional.empty());

		Exception exception = assertThrows(IllegalArgumentException.class, () ->
			cdService.getCDById(cdId)
		);

		assertTrue(exception.getMessage().contains("CD not found"));
		verify(cdRepo).getCDById(cdId);
	}

	@Test
	void givenAdminUserAndValidData_whenUpdateCD_thenCDIsUpdated() {
		UUID cdId = UUID.randomUUID();
		CD cd = new CD("Old Title", "Old Artist");
		when(cdRepo.getCDById(cdId)).thenReturn(Optional.of(cd));
		when(cdRepo.updateCD(cd)).thenReturn(true);

		try {
			CD result = cdService.updateCD(adminUser, cdId, "New Title", "New Artist");

			assertNotNull(result);
			assertEquals("New Title", result.getTitle());
			assertEquals("New Artist", result.getArtist());
			verify(cdRepo).getCDById(cdId);
			verify(cdRepo).updateCD(cd);
		} catch (PermissionDeniedException e) {
			throw new AssertionError("Should not throw PermissionDeniedException for admin user", e);
		}
	}

	@Test
	void givenAdminUserAndNullTitle_whenUpdateCD_thenOnlyArtistIsUpdated() {
		UUID cdId = UUID.randomUUID();
		CD cd = new CD("Original Title", "Old Artist");
		when(cdRepo.getCDById(cdId)).thenReturn(Optional.of(cd));
		when(cdRepo.updateCD(cd)).thenReturn(true);

		try {
			CD result = cdService.updateCD(adminUser, cdId, null, "New Artist");

			assertEquals("Original Title", result.getTitle());
			assertEquals("New Artist", result.getArtist());
			verify(cdRepo).updateCD(cd);
		} catch (PermissionDeniedException e) {
			throw new AssertionError("Should not throw PermissionDeniedException for admin user", e);
		}
	}

	@Test
	void givenAdminUserAndBlankTitle_whenUpdateCD_thenOnlyArtistIsUpdated() {
		UUID cdId = UUID.randomUUID();
		CD cd = new CD("Original Title", "Old Artist");
		when(cdRepo.getCDById(cdId)).thenReturn(Optional.of(cd));
		when(cdRepo.updateCD(cd)).thenReturn(true);

		try {
			CD result = cdService.updateCD(adminUser, cdId, "   ", "New Artist");

			assertEquals("Original Title", result.getTitle());
			assertEquals("New Artist", result.getArtist());
		} catch (PermissionDeniedException e) {
			throw new AssertionError("Should not throw PermissionDeniedException for admin user", e);
		}
	}

	@Test
	void givenMemberUser_whenUpdateCD_thenThrowPermissionDeniedException() {
		UUID cdId = UUID.randomUUID();

		Exception exception = assertThrows(PermissionDeniedException.class, () ->
			cdService.updateCD(memberUser, cdId, "New Title", "New Artist")
		);

		verify(cdRepo, never()).getCDById(any(UUID.class));
		verify(cdRepo, never()).updateCD(any(CD.class));
	}

	@Test
	void givenNonExistingCDId_whenUpdateCD_thenThrowIllegalArgumentException() {
		UUID cdId = UUID.randomUUID();
		when(cdRepo.getCDById(cdId)).thenReturn(Optional.empty());

		Exception exception = assertThrows(IllegalArgumentException.class, () ->
			cdService.updateCD(adminUser, cdId, "New Title", "New Artist")
		);

		assertTrue(exception.getMessage().contains("CD not found"));
		verify(cdRepo).getCDById(cdId);
	}

	@Test
	void givenRepositoryFailure_whenUpdateCD_thenThrowIllegalStateException() {
		UUID cdId = UUID.randomUUID();
		CD cd = new CD("Title", "Artist");
		when(cdRepo.getCDById(cdId)).thenReturn(Optional.of(cd));
		when(cdRepo.updateCD(cd)).thenReturn(false);

		Exception exception = assertThrows(IllegalStateException.class, () ->
			cdService.updateCD(adminUser, cdId, "New Title", "New Artist")
		);

		assertTrue(exception.getMessage().contains("Failed to update"));
	}

	@Test
	void givenAdminUserAndExistingCD_whenDeleteCD_thenCDIsDeleted() {
		UUID cdId = UUID.randomUUID();
		when(cdRepo.deleteCD(cdId)).thenReturn(true);

		try {
			cdService.deleteCD(adminUser, cdId);

			verify(cdRepo).deleteCD(cdId);
		} catch (PermissionDeniedException e) {
			throw new AssertionError("Should not throw PermissionDeniedException for admin user", e);
		}
	}

	@Test
	void givenMemberUser_whenDeleteCD_thenThrowPermissionDeniedException() {
		UUID cdId = UUID.randomUUID();

		Exception exception = assertThrows(PermissionDeniedException.class, () ->
			cdService.deleteCD(memberUser, cdId)
		);

		verify(cdRepo, never()).deleteCD(any(UUID.class));
	}

	@Test
	void givenNonExistingCDId_whenDeleteCD_thenThrowIllegalArgumentException() {
		UUID cdId = UUID.randomUUID();
		when(cdRepo.deleteCD(cdId)).thenReturn(false);

		Exception exception = assertThrows(IllegalArgumentException.class, () ->
			cdService.deleteCD(adminUser, cdId)
		);

		assertTrue(exception.getMessage().contains("CD not found"));
		verify(cdRepo).deleteCD(cdId);
	}

	@Test
	void givenValidKeyword_whenSearchCDs_thenReturnMatchingCDs() {
		CD cd1 = new CD("Rock Album", "Rock Band");
		CD cd2 = new CD("Rock Classics", "Various Artists");
		List<CD> expectedCDs = Arrays.asList(cd1, cd2);
		when(cdRepo.searchCDs("Rock")).thenReturn(expectedCDs);

		List<CD> result = cdService.searchCDs("Rock");

		assertNotNull(result);
		assertEquals(2, result.size());
		assertTrue(result.get(0).getTitle().contains("Rock"));
		verify(cdRepo).searchCDs("Rock");
		verify(cdRepo, never()).getAllCDs();
	}

	@Test
	void givenNullKeyword_whenSearchCDs_thenReturnAllCDs() {
		CD cd1 = new CD("Album 1", "Artist 1");
		CD cd2 = new CD("Album 2", "Artist 2");
		List<CD> allCDs = Arrays.asList(cd1, cd2);
		when(cdRepo.getAllCDs()).thenReturn(allCDs);

		List<CD> result = cdService.searchCDs(null);

		assertNotNull(result);
		assertEquals(2, result.size());
		verify(cdRepo).getAllCDs();
		verify(cdRepo, never()).searchCDs(any());
	}

	@Test
	void givenBlankKeyword_whenSearchCDs_thenReturnAllCDs() {
		CD cd1 = new CD("Album 1", "Artist 1");
		List<CD> allCDs = Arrays.asList(cd1);
		when(cdRepo.getAllCDs()).thenReturn(allCDs);

		List<CD> result = cdService.searchCDs("   ");

		assertNotNull(result);
		assertEquals(1, result.size());
		verify(cdRepo).getAllCDs();
		verify(cdRepo, never()).searchCDs(any());
	}

	@Test
	void givenCDWithAvailableCopies_whenIsAvailableCD_thenReturnTrue() {
		UUID cdId = UUID.randomUUID();
		CD cd = new CD("Album", "Artist", 3);
		when(cdRepo.getCDById(cdId)).thenReturn(Optional.of(cd));

		boolean result = cdService.isAvailableCD(cdId);

		assertTrue(result);
		verify(cdRepo).getCDById(cdId);
	}

	@Test
	void givenCDWithNoCopiesAvailable_whenIsAvailableCD_thenReturnFalse() {
		UUID cdId = UUID.randomUUID();
		CD cd = new CD("Album", "Artist", 1);
		cd.decrementAvailableCopies();
		when(cdRepo.getCDById(cdId)).thenReturn(Optional.of(cd));

		boolean result = cdService.isAvailableCD(cdId);

		assertFalse(result);
		verify(cdRepo).getCDById(cdId);
	}

	@Test
	void givenNonExistingCDId_whenIsAvailableCD_thenReturnFalse() {
		UUID cdId = UUID.randomUUID();
		when(cdRepo.getCDById(cdId)).thenReturn(Optional.empty());

		boolean result = cdService.isAvailableCD(cdId);

		assertFalse(result);
		verify(cdRepo).getCDById(cdId);
	}

	@Test
	void givenExistingCDId_whenIsValidCD_thenReturnTrue() {
		UUID cdId = UUID.randomUUID();
		CD cd = new CD("Album", "Artist");
		when(cdRepo.getCDById(cdId)).thenReturn(Optional.of(cd));

		boolean result = cdService.isValidCD(cdId);

		assertTrue(result);
		verify(cdRepo).getCDById(cdId);
	}

	@Test
	void givenNonExistingCDId_whenIsValidCD_thenReturnFalse() {
		UUID cdId = UUID.randomUUID();
		when(cdRepo.getCDById(cdId)).thenReturn(Optional.empty());

		boolean result = cdService.isValidCD(cdId);

		assertFalse(result);
		verify(cdRepo).getCDById(cdId);
	}

	@Test
	void givenAvailableCD_whenBorrowCD_thenCopiesDecremented() {
		UUID cdId = UUID.randomUUID();
		CD cd = new CD("Album", "Artist", 3);
		when(cdRepo.getCDById(cdId)).thenReturn(Optional.of(cd));
		when(cdRepo.updateCD(cd)).thenReturn(true);

		cdService.borrowCD(memberUser, cdId);

		assertEquals(2, cd.getAvailableCopies());
		assertTrue(cd.isBorrowed());
		verify(cdRepo).getCDById(cdId);
		verify(cdRepo).updateCD(cd);
	}

	@Test
	void givenCDWithNoCopiesAvailable_whenBorrowCD_thenThrowIllegalStateException() {
		UUID cdId = UUID.randomUUID();
		CD cd = new CD("Album", "Artist", 1);
		cd.decrementAvailableCopies();
		when(cdRepo.getCDById(cdId)).thenReturn(Optional.of(cd));

		Exception exception = assertThrows(IllegalStateException.class, () ->
			cdService.borrowCD(memberUser, cdId)
		);

		assertTrue(exception.getMessage().contains("No copies available"));
		verify(cdRepo).getCDById(cdId);
		verify(cdRepo, never()).updateCD(any(CD.class));
	}

	@Test
	void givenNonExistingCDId_whenBorrowCD_thenThrowIllegalArgumentException() {
		UUID cdId = UUID.randomUUID();
		when(cdRepo.getCDById(cdId)).thenReturn(Optional.empty());

		Exception exception = assertThrows(IllegalArgumentException.class, () ->
			cdService.borrowCD(memberUser, cdId)
		);

		assertTrue(exception.getMessage().contains("CD not found"));
		verify(cdRepo).getCDById(cdId);
	}

	@Test
	void givenRepositoryFailure_whenBorrowCD_thenThrowIllegalStateException() {
		UUID cdId = UUID.randomUUID();
		CD cd = new CD("Album", "Artist");
		when(cdRepo.getCDById(cdId)).thenReturn(Optional.of(cd));
		when(cdRepo.updateCD(cd)).thenReturn(false);

		Exception exception = assertThrows(IllegalStateException.class, () ->
			cdService.borrowCD(memberUser, cdId)
		);

		assertTrue(exception.getMessage().contains("Failed to update"));
	}

	@Test
	void givenBorrowedCD_whenReturnCD_thenCopiesIncremented() {
		UUID cdId = UUID.randomUUID();
		CD cd = new CD("Album", "Artist", 3);
		cd.decrementAvailableCopies();
		when(cdRepo.getCDById(cdId)).thenReturn(Optional.of(cd));
		when(cdRepo.updateCD(cd)).thenReturn(true);

		cdService.returnCD(memberUser, cdId);

		assertEquals(3, cd.getAvailableCopies());
		assertFalse(cd.isBorrowed());
		verify(cdRepo).getCDById(cdId);
		verify(cdRepo).updateCD(cd);
	}

	@Test
	void givenCDWithAllCopiesAvailable_whenReturnCD_thenThrowIllegalStateException() {
		UUID cdId = UUID.randomUUID();
		CD cd = new CD("Album", "Artist");
		when(cdRepo.getCDById(cdId)).thenReturn(Optional.of(cd));

		Exception exception = assertThrows(IllegalStateException.class, () ->
			cdService.returnCD(memberUser, cdId)
		);

		assertTrue(exception.getMessage().contains("All copies are already returned"));
		verify(cdRepo).getCDById(cdId);
		verify(cdRepo, never()).updateCD(any(CD.class));
	}

	@Test
	void givenNonExistingCDId_whenReturnCD_thenThrowIllegalArgumentException() {
		UUID cdId = UUID.randomUUID();
		when(cdRepo.getCDById(cdId)).thenReturn(Optional.empty());

		Exception exception = assertThrows(IllegalArgumentException.class, () ->
			cdService.returnCD(memberUser, cdId)
		);

		assertTrue(exception.getMessage().contains("CD not found"));
		verify(cdRepo).getCDById(cdId);
	}

	@Test
	void givenRepositoryFailure_whenReturnCD_thenThrowIllegalStateException() {
		UUID cdId = UUID.randomUUID();
		CD cd = new CD("Album", "Artist", 2);
		cd.decrementAvailableCopies();
		when(cdRepo.getCDById(cdId)).thenReturn(Optional.of(cd));
		when(cdRepo.updateCD(cd)).thenReturn(false);

		Exception exception = assertThrows(IllegalStateException.class, () ->
			cdService.returnCD(memberUser, cdId)
		);

		assertTrue(exception.getMessage().contains("Failed to update"));
	}
}
