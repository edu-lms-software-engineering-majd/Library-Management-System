package lms.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
		adminUser = new UserDTO(UUID.randomUUID(), "admin", "Admin", "User", Role.ADMIN);
		memberUser = new UserDTO(UUID.randomUUID(), "member", "Member", "User", Role.MEMBER);
	}

	@Test
	void givenAdminUser_whenAddCD_thenCDIsCreatedAndSaved() throws PermissionDeniedException {
		when(cdRepo.addCD(any(CD.class))).thenReturn(true);

		CD result = cdService.addCD(adminUser, "Abbey Road", "The Beatles");

		assertNotNull(result);
		assertEquals("Abbey Road", result.getTitle());
		assertEquals("The Beatles", result.getArtist());
		verify(cdRepo).addCD(any(CD.class));
	}

	@Test
	void givenMemberUser_whenAddCD_thenThrowPermissionDeniedException() {
		assertThrows(PermissionDeniedException.class, () ->
			cdService.addCD(memberUser, "Abbey Road", "The Beatles")
		);
		verify(cdRepo, never()).addCD(any(CD.class));
	}

	@Test
	void givenNullUser_whenAddCD_thenThrowPermissionDeniedException() {
		assertThrows(PermissionDeniedException.class, () ->
			cdService.addCD(null, "Abbey Road", "The Beatles")
		);
		verify(cdRepo, never()).addCD(any(CD.class));
	}

	@Test
	void givenInvalidTitle_whenAddCD_thenThrowIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () ->
			cdService.addCD(adminUser, "", "The Beatles")
		);
		verify(cdRepo, never()).addCD(any(CD.class));
	}

	@Test
	void givenInvalidArtist_whenAddCD_thenThrowIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () ->
			cdService.addCD(adminUser, "Abbey Road", null)
		);
		verify(cdRepo, never()).addCD(any(CD.class));
	}

	@Test
	void givenRepositoryFailure_whenAddCD_thenThrowIllegalStateException() {
		when(cdRepo.addCD(any(CD.class))).thenReturn(false);

		assertThrows(IllegalStateException.class, () ->
			cdService.addCD(adminUser, "Abbey Road", "The Beatles")
		);
	}

	@Test
	void whenGetAllCDs_thenReturnAllCDs() {
		CD cd1 = new CD("Album 1", "Artist 1");
		CD cd2 = new CD("Album 2", "Artist 2");
		List<CD> cds = Arrays.asList(cd1, cd2);
		when(cdRepo.getAllCDs()).thenReturn(cds);

		List<CD> result = cdService.getAllCDs();

		assertEquals(2, result.size());
		verify(cdRepo).getAllCDs();
	}

	@Test
	void givenExistingCD_whenGetCDById_thenReturnCD() {
		UUID cdId = UUID.randomUUID();
		CD cd = new CD("Abbey Road", "The Beatles");
		when(cdRepo.getCDById(cdId)).thenReturn(Optional.of(cd));

		CD result = cdService.getCDById(cdId);

		assertNotNull(result);
		assertEquals("Abbey Road", result.getTitle());
		verify(cdRepo).getCDById(cdId);
	}

	@Test
	void givenNonExistingCD_whenGetCDById_thenThrowIllegalArgumentException() {
		UUID cdId = UUID.randomUUID();
		when(cdRepo.getCDById(cdId)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () ->
			cdService.getCDById(cdId)
		);
	}

	@Test
	void givenAdminUser_whenUpdateCD_thenCDIsUpdated() throws PermissionDeniedException {
		UUID cdId = UUID.randomUUID();
		CD cd = new CD("Old Title", "Old Artist");
		when(cdRepo.getCDById(cdId)).thenReturn(Optional.of(cd));
		when(cdRepo.updateCD(cd)).thenReturn(true);

		CD result = cdService.updateCD(adminUser, cdId, "New Title", "New Artist");

		assertEquals("New Title", result.getTitle());
		assertEquals("New Artist", result.getArtist());
		verify(cdRepo).updateCD(cd);
	}

	@Test
	void givenAdminUser_whenUpdateCDWithNullTitle_thenOnlyArtistIsUpdated() throws PermissionDeniedException {
		UUID cdId = UUID.randomUUID();
		CD cd = new CD("Original Title", "Old Artist");
		when(cdRepo.getCDById(cdId)).thenReturn(Optional.of(cd));
		when(cdRepo.updateCD(cd)).thenReturn(true);

		CD result = cdService.updateCD(adminUser, cdId, null, "New Artist");

		assertEquals("Original Title", result.getTitle());
		assertEquals("New Artist", result.getArtist());
	}

	@Test
	void givenMemberUser_whenUpdateCD_thenThrowPermissionDeniedException() {
		UUID cdId = UUID.randomUUID();

		assertThrows(PermissionDeniedException.class, () ->
			cdService.updateCD(memberUser, cdId, "New Title", "New Artist")
		);
		verify(cdRepo, never()).updateCD(any(CD.class));
	}

	@Test
	void givenNonExistingCD_whenUpdateCD_thenThrowIllegalArgumentException() {
		UUID cdId = UUID.randomUUID();
		when(cdRepo.getCDById(cdId)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () ->
			cdService.updateCD(adminUser, cdId, "New Title", "New Artist")
		);
	}

	@Test
	void givenRepositoryFailure_whenUpdateCD_thenThrowIllegalStateException() {
		UUID cdId = UUID.randomUUID();
		CD cd = new CD("Title", "Artist");
		when(cdRepo.getCDById(cdId)).thenReturn(Optional.of(cd));
		when(cdRepo.updateCD(cd)).thenReturn(false);

		assertThrows(IllegalStateException.class, () ->
			cdService.updateCD(adminUser, cdId, "New Title", "New Artist")
		);
	}

	@Test
	void givenAdminUser_whenDeleteCD_thenCDIsDeleted() throws PermissionDeniedException {
		UUID cdId = UUID.randomUUID();
		when(cdRepo.deleteCD(cdId)).thenReturn(true);

		cdService.deleteCD(adminUser, cdId);

		verify(cdRepo).deleteCD(cdId);
	}

	@Test
	void givenMemberUser_whenDeleteCD_thenThrowPermissionDeniedException() {
		UUID cdId = UUID.randomUUID();

		assertThrows(PermissionDeniedException.class, () ->
			cdService.deleteCD(memberUser, cdId)
		);
		verify(cdRepo, never()).deleteCD(any(UUID.class));
	}

	@Test
	void givenNonExistingCD_whenDeleteCD_thenThrowIllegalArgumentException() {
		UUID cdId = UUID.randomUUID();
		when(cdRepo.deleteCD(cdId)).thenReturn(false);

		assertThrows(IllegalArgumentException.class, () ->
			cdService.deleteCD(adminUser, cdId)
		);
	}

	@Test
	void givenKeyword_whenSearchCDs_thenReturnMatchingCDs() {
		CD cd1 = new CD("Rock Album", "Band");
		List<CD> cds = Arrays.asList(cd1);
		when(cdRepo.searchCDs("Rock")).thenReturn(cds);

		List<CD> result = cdService.searchCDs("Rock");

		assertEquals(1, result.size());
		verify(cdRepo).searchCDs("Rock");
	}

	@Test
	void givenNullKeyword_whenSearchCDs_thenReturnAllCDs() {
		CD cd1 = new CD("Album 1", "Artist 1");
		CD cd2 = new CD("Album 2", "Artist 2");
		List<CD> cds = Arrays.asList(cd1, cd2);
		when(cdRepo.getAllCDs()).thenReturn(cds);

		List<CD> result = cdService.searchCDs(null);

		assertEquals(2, result.size());
		verify(cdRepo).getAllCDs();
	}

	@Test
	void givenBlankKeyword_whenSearchCDs_thenReturnAllCDs() {
		CD cd1 = new CD("Album 1", "Artist 1");
		List<CD> cds = Arrays.asList(cd1);
		when(cdRepo.getAllCDs()).thenReturn(cds);

		List<CD> result = cdService.searchCDs("   ");

		assertEquals(1, result.size());
		verify(cdRepo).getAllCDs();
	}

	@Test
	void givenAvailableCD_whenIsAvailableCD_thenReturnTrue() {
		UUID cdId = UUID.randomUUID();
		CD cd = new CD("Album", "Artist");
		when(cdRepo.getCDById(cdId)).thenReturn(Optional.of(cd));

		boolean result = cdService.isAvailableCD(cdId);

		assertTrue(result);
	}

	@Test
	void givenBorrowedCD_whenIsAvailableCD_thenReturnFalse() {
		UUID cdId = UUID.randomUUID();
		CD cd = new CD("Album", "Artist");
		cd.setBorrowed(true);
		when(cdRepo.getCDById(cdId)).thenReturn(Optional.of(cd));

		boolean result = cdService.isAvailableCD(cdId);

		assertFalse(result);
	}

	@Test
	void givenNonExistingCD_whenIsAvailableCD_thenReturnFalse() {
		UUID cdId = UUID.randomUUID();
		when(cdRepo.getCDById(cdId)).thenReturn(Optional.empty());

		boolean result = cdService.isAvailableCD(cdId);

		assertFalse(result);
	}

	@Test
	void givenExistingCD_whenIsValidCD_thenReturnTrue() {
		UUID cdId = UUID.randomUUID();
		CD cd = new CD("Album", "Artist");
		when(cdRepo.getCDById(cdId)).thenReturn(Optional.of(cd));

		boolean result = cdService.isValidCD(cdId);

		assertTrue(result);
	}

	@Test
	void givenNonExistingCD_whenIsValidCD_thenReturnFalse() {
		UUID cdId = UUID.randomUUID();
		when(cdRepo.getCDById(cdId)).thenReturn(Optional.empty());

		boolean result = cdService.isValidCD(cdId);

		assertFalse(result);
	}

	@Test
	void givenAvailableCD_whenBorrowCD_thenCDIsBorrowed() {
		UUID cdId = UUID.randomUUID();
		CD cd = new CD("Album", "Artist");
		when(cdRepo.getCDById(cdId)).thenReturn(Optional.of(cd));
		when(cdRepo.updateCD(cd)).thenReturn(true);

		cdService.borrowCD(memberUser, cdId);

		assertTrue(cd.isBorrowed());
		verify(cdRepo).updateCD(cd);
	}

	@Test
	void givenBorrowedCD_whenBorrowCD_thenThrowIllegalStateException() {
		UUID cdId = UUID.randomUUID();
		CD cd = new CD("Album", "Artist");
		cd.setBorrowed(true);
		when(cdRepo.getCDById(cdId)).thenReturn(Optional.of(cd));

		assertThrows(IllegalStateException.class, () ->
			cdService.borrowCD(memberUser, cdId)
		);
	}

	@Test
	void givenNonExistingCD_whenBorrowCD_thenThrowIllegalArgumentException() {
		UUID cdId = UUID.randomUUID();
		when(cdRepo.getCDById(cdId)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () ->
			cdService.borrowCD(memberUser, cdId)
		);
	}

	@Test
	void givenBorrowedCD_whenReturnCD_thenCDIsReturned() {
		UUID cdId = UUID.randomUUID();
		CD cd = new CD("Album", "Artist");
		cd.setBorrowed(true);
		when(cdRepo.getCDById(cdId)).thenReturn(Optional.of(cd));
		when(cdRepo.updateCD(cd)).thenReturn(true);

		cdService.returnCD(memberUser, cdId);

		assertFalse(cd.isBorrowed());
		verify(cdRepo).updateCD(cd);
	}

	@Test
	void givenAvailableCD_whenReturnCD_thenThrowIllegalStateException() {
		UUID cdId = UUID.randomUUID();
		CD cd = new CD("Album", "Artist");
		when(cdRepo.getCDById(cdId)).thenReturn(Optional.of(cd));

		assertThrows(IllegalStateException.class, () ->
			cdService.returnCD(memberUser, cdId)
		);
	}

	@Test
	void givenNonExistingCD_whenReturnCD_thenThrowIllegalArgumentException() {
		UUID cdId = UUID.randomUUID();
		when(cdRepo.getCDById(cdId)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () ->
			cdService.returnCD(memberUser, cdId)
		);
	}
}
