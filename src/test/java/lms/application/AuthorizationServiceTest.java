package lms.application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import lms.domain.Role;
import lms.domain.exception.PermissionDeniedException;

@DisplayName("AuthorizationService - Full Coverage Test Suite")
class AuthorizationServiceTest {

	// Sample test users
	private final UserDTO adminUser = new UserDTO(UUID.randomUUID(), "ahmad", "Ahmad", "Salameh", Role.ADMIN);

	private final UserDTO librarianUser = new UserDTO(UUID.randomUUID(), "majd", "Majd", "Awwad", Role.LIBRARIAN);

	private final UserDTO memberUser = new UserDTO(UUID.randomUUID(), "user1", "Sara", "Mohammed", Role.MEMBER);

	// ============================================================
	// ensureAdmin() Tests
	// ============================================================
	@Nested
	@DisplayName("🔐 ensureAdmin() Tests")
	class EnsureAdminTests {

		@Test
		@DisplayName("Should pass when user is ADMIN")
		void shouldAllowAdmin() throws PermissionDeniedException {
			assertDoesNotThrow(() -> AuthorizationService.ensureAdmin(adminUser));

		}

		@Test
		@DisplayName("Should throw for LIBRARIAN user")
		void shouldRejectLibrarian() {
			PermissionDeniedException ex = assertThrows(PermissionDeniedException.class,
					() -> AuthorizationService.ensureAdmin(librarianUser));

			assertTrue(ex.getMessage().contains("ADMIN") || ex.getMessage().contains("admin"));
		}
		

		@Test
		@DisplayName("Should throw for MEMBER user")
		void shouldRejectMember() {
			assertThrows(PermissionDeniedException.class, () -> AuthorizationService.ensureAdmin(memberUser));
		}

		@Test
		@DisplayName("Should throw for null user")
		void shouldRejectNullUser() {
			assertThrows(PermissionDeniedException.class, () -> AuthorizationService.ensureAdmin(null));
		}
	}

	// ============================================================
	// ensureLibrarian() Tests
	// ============================================================
	@Nested
	@DisplayName("📚 ensureLibrarian() Tests")
	class EnsureLibrarianTests {

		@Test
		@DisplayName("Should pass when user is LIBRARIAN")
		void shouldAllowLibrarian() throws PermissionDeniedException {
		    assertDoesNotThrow(() -> AuthorizationService.ensureLibrarian(librarianUser));
		}


		@Test
		@DisplayName("Should throw for ADMIN user")
		void shouldRejectAdmin() {
			assertThrows(PermissionDeniedException.class, () -> AuthorizationService.ensureLibrarian(adminUser));
		}

		@Test
		@DisplayName("Should throw for MEMBER user")
		void shouldRejectMember() {
			assertThrows(PermissionDeniedException.class, () -> AuthorizationService.ensureLibrarian(memberUser));
		}

		@Test
		@DisplayName("Should throw for null user")
		void shouldRejectNullUser() {
			assertThrows(PermissionDeniedException.class, () -> AuthorizationService.ensureLibrarian(null));
		}
	}

	// ============================================================
	// isAdmin() Tests
	// ============================================================
	@Nested
	@DisplayName("✔️ isAdmin() Tests")
	class IsAdminTests {

		@Test
		@DisplayName("Should return true for ADMIN")
		void shouldReturnTrueForAdmin() {
			assertTrue(AuthorizationService.isAdmin(adminUser));
		}

		@Test
		@DisplayName("Should return false for LIBRARIAN or MEMBER")
		void shouldReturnFalseForOthers() {
			assertFalse(AuthorizationService.isAdmin(librarianUser));
			assertFalse(AuthorizationService.isAdmin(memberUser));
		}

		@Test
		@DisplayName("Should return false for null")
		void shouldReturnFalseForNull() {
			assertFalse(AuthorizationService.isAdmin(null));
		}
	}

	// ============================================================
	// isLibrarian() Tests
	// ============================================================
	@Nested
	@DisplayName("✔️ isLibrarian() Tests")
	class IsLibrarianTests {

		@Test
		@DisplayName("Should return true for LIBRARIAN")
		void shouldReturnTrueForLibrarian() {
			assertTrue(AuthorizationService.isLibrarian(librarianUser));
		}

		@Test
		@DisplayName("Should return false for ADMIN or MEMBER")
		void shouldReturnFalseForOthers() {
			assertFalse(AuthorizationService.isLibrarian(adminUser));
			assertFalse(AuthorizationService.isLibrarian(memberUser));
		}

		@Test
		@DisplayName("Should return false for null")
		void shouldReturnFalseForNull() {
			assertFalse(AuthorizationService.isLibrarian(null));
		}
	}
}
