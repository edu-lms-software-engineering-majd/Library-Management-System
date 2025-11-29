package lms.application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import lms.domain.Role;
import lms.domain.exception.PermissionDeniedException;

class AuthorizationServiceTest {

	private final UserDTO adminUser = new UserDTO(UUID.randomUUID(), "ahmad", "Ahmad", "Salameh", Role.ADMIN);

	private final UserDTO librarianUser = new UserDTO(UUID.randomUUID(), "majd", "Majd", "Awwad", Role.LIBRARIAN);

	private final UserDTO memberUser = new UserDTO(UUID.randomUUID(), "user1", "Sara", "Mohammed", Role.MEMBER);

	 

	@Test
	void shouldAllowAdmin() {
		assertDoesNotThrow(() -> AuthorizationService.ensureAdmin(adminUser));
	}

	@Test
	void shouldRejectLibrarian() {
		PermissionDeniedException ex = assertThrows(PermissionDeniedException.class,
				() -> AuthorizationService.ensureAdmin(librarianUser));

		assertTrue(ex.getMessage().contains("ADMIN") || ex.getMessage().contains("admin"));
	}

	@Test
	void shouldRejectMember() {
		assertThrows(PermissionDeniedException.class, () -> AuthorizationService.ensureAdmin(memberUser));
	}

	@Test
	void shouldRejectNullUser_admin() {
		assertThrows(PermissionDeniedException.class, () -> AuthorizationService.ensureAdmin(null));
	}

	 

	@Test
	void shouldAllowLibrarian() {
		assertDoesNotThrow(() -> AuthorizationService.ensureLibrarian(librarianUser));
	}

	@Test
	void shouldRejectAdmin_librarian() {
		assertThrows(PermissionDeniedException.class, () -> AuthorizationService.ensureLibrarian(adminUser));
	}

	@Test
	void shouldRejectMember_librarian() {
		assertThrows(PermissionDeniedException.class, () -> AuthorizationService.ensureLibrarian(memberUser));
	}

	@Test
	void shouldRejectNullUser_librarian() {
		assertThrows(PermissionDeniedException.class, () -> AuthorizationService.ensureLibrarian(null));
	}

	 

	@Test
	void shouldReturnTrueForAdmin() {
		assertTrue(AuthorizationService.isAdmin(adminUser));
	}

	@Test
	void shouldReturnFalseForOthers_admin() {
		assertFalse(AuthorizationService.isAdmin(librarianUser));
		assertFalse(AuthorizationService.isAdmin(memberUser));
	}

	@Test
	void shouldReturnFalseForNull_admin() {
		assertFalse(AuthorizationService.isAdmin(null));
	}

	 

	@Test
	void shouldReturnTrueForLibrarian() {
		assertTrue(AuthorizationService.isLibrarian(librarianUser));
	}

	@Test
	void shouldReturnFalseForOthers_librarian() {
		assertFalse(AuthorizationService.isLibrarian(adminUser));
		assertFalse(AuthorizationService.isLibrarian(memberUser));
	}

	@Test
	void shouldReturnFalseForNull_librarian() {
		assertFalse(AuthorizationService.isLibrarian(null));
	}
}
