package lms.application;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import lms.domain.Role;
import lms.domain.exception.PermissionDeniedException;

/**
 * Comprehensive test suite for AuthorizationService.
 * 
 * <p>
 * This test class provides extensive coverage for the AuthorizationService
 * layer, testing all authorization checks including:
 * </p>
 * 
 * <h2>Test Coverage:</h2>
 * <ul>
 * <li><b>ensureAdmin() Tests:</b> Validates admin authorization checks</li>
 * <li><b>ensureLibrarian() Tests:</b> Validates librarian authorization
 * checks</li>
 * <li><b>Null User Handling:</b> Tests for null user protection</li>
 * <li><b>Role Distinction:</b> Tests proper role differentiation</li>
 * <li><b>Exception Handling:</b> Validates correct exception types and
 * messages</li>
 * <li><b>Return Values:</b> Verifies correct return values for valid cases</li>
 * <li><b>Edge Cases:</b> Tests boundary conditions and special scenarios</li>
 * </ul>
 * 
 * <h2>Authorization Rules:</h2>
 * <ul>
 * <li><b>ensureAdmin():</b> Only users with Role.ADMIN can pass</li>
 * <li><b>ensureLibrarian():</b> Only users with Role.LIBRARIAN can pass</li>
 * <li><b>Both Methods:</b> Reject null users with
 * PermissionDeniedException</li>
 * </ul>
 * 
 * <h2>Test Data:</h2>
 * <p>
 * Uses real test data with actual names and roles:
 * </p>
 * <ul>
 * <li><b>Ahmad Salameh:</b> Admin user (hmeedsalameh2004@gmail.com)</li>
 * <li><b>Sara Mohammed:</b> Librarian user</li>
 * <li><b>John Doe:</b> Regular member user</li>
 * </ul>
 * 
 * <h2>Running Tests:</h2>
 * 
 * <pre>
 * # Run all AuthorizationService tests
 * mvn test -Dtest=AuthorizationServiceTest
 * 
 * # Run with coverage report
 * mvn clean test jacoco:report
 * </pre>
 * 
 * @author Ahmad Salameh (hmeedsalameh2004@gmail.com)
 * @version 1.0
 * @since 2025-01-15
 */
@DisplayName("AuthorizationService - Complete Test Suite (90%+ Coverage)")
class AuthorizationServiceTest {

	/**
	 * Setup before all tests - executed once for the entire test class. Used for
	 * heavy initialization that's shared across all tests.
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		// Initialize any shared resources if needed
		System.out.println("✅ AuthorizationService Test Suite Starting...");
	}

	/**
	 * Cleanup after all tests - executed once after all tests complete. Used for
	 * closing shared resources.
	 */
	@AfterAll
	static void tearDownAfterClass() throws Exception {
		System.out.println("✅ AuthorizationService Test Suite Complete!");
	}

	/**
	 * Setup before each individual test. Used for test-specific initialization.
	 */
	@BeforeEach
	void setUp() throws Exception {
		// Setup for each test if needed
	}

	/**
	 * Cleanup after each individual test. Used for test-specific cleanup.
	 */
	@AfterEach
	void tearDown() throws Exception {
		// Cleanup for each test if needed
	}

	/**
	 * Tests for ensureAdmin() method. Validates admin authorization checks.
	 */
	@Nested
	@DisplayName("🔐 ensureAdmin() Authorization Tests")
	class EnsureAdminTests {

		/**
		 * Test: Should return true when user is ADMIN.
		 * 
		 * <p>
		 * <b>Setup:</b> Create UserDTO with Role.ADMIN
		 * </p>
		 * <p>
		 * <b>Action:</b> Call ensureAdmin(adminUser)
		 * </p>
		 * <p>
		 * <b>Expected:</b> Should return true without throwing exception
		 * </p>
		 */
		@Test
		@DisplayName("✅ Should return true when user is ADMIN")
		void testEnsureAdminWithValidAdmin() throws PermissionDeniedException {
			// Arrange
			UserDTO adminUser = new UserDTO(UUID.randomUUID(), "ahmadsalameh", "Ahmad", "Salameh", Role.ADMIN);

			// Act
			boolean result = AuthorizationService.ensureAdmin(adminUser);

			// Assert
			assertTrue(result, "Should return true for valid admin user");
		}

		/**
		 * Test: Should throw PermissionDeniedException when user is MEMBER.
		 * 
		 * <p>
		 * <b>Setup:</b> Create UserDTO with Role.MEMBER
		 * </p>
		 * <p>
		 * <b>Action:</b> Call ensureAdmin(memberUser)
		 * </p>
		 * <p>
		 * <b>Expected:</b> PermissionDeniedException should be thrown
		 * </p>
		 */
		@Test
		@DisplayName("❌ Should throw when user is MEMBER")
		void testEnsureAdminWithMemberRole() {
			// Arrange
			UserDTO memberUser = new UserDTO(UUID.randomUUID(), "majdawwad", "Majd", "Awwad", Role.MEMBER);

			// Act & Assert
			PermissionDeniedException exception = assertThrows(PermissionDeniedException.class, () -> {
				AuthorizationService.ensureAdmin(memberUser);
			}, "Should throw PermissionDeniedException for member user");

			// Verify exception message
			assertTrue(exception.getMessage().contains("admin"), "Exception message should mention admin privileges");
		}

		/**
		 * Test: Should throw PermissionDeniedException when user is LIBRARIAN.
		 * 
		 * <p>
		 * <b>Setup:</b> Create UserDTO with Role.LIBRARIAN
		 * </p>
		 * <p>
		 * <b>Action:</b> Call ensureAdmin(librarianUser)
		 * </p>
		 * <p>
		 * <b>Expected:</b> PermissionDeniedException should be thrown
		 * </p>
		 */
		@Test
		@DisplayName("❌ Should throw when user is LIBRARIAN")
		void testEnsureAdminWithLibrarianRole() {
			// Arrange
			UserDTO librarianUser = new UserDTO(UUID.randomUUID(), "majdawwad", "Majd", "Awwad", Role.LIBRARIAN);

			// Act & Assert
			assertThrows(PermissionDeniedException.class, () -> {
				AuthorizationService.ensureAdmin(librarianUser);
			}, "Should throw PermissionDeniedException for librarian user");
		}

		/**
		 * Test: Should throw PermissionDeniedException when user is null.
		 * 
		 * <p>
		 * <b>Setup:</b> No setup, null user
		 * </p>
		 * <p>
		 * <b>Action:</b> Call ensureAdmin(null)
		 * </p>
		 * <p>
		 * <b>Expected:</b> PermissionDeniedException should be thrown immediately
		 * </p>
		 */
		@Test
		@DisplayName("❌ Should throw when user is null")
		void testEnsureAdminWithNullUser() {
			// Act & Assert
			PermissionDeniedException exception = assertThrows(PermissionDeniedException.class, () -> {
				AuthorizationService.ensureAdmin(null);
			}, "Should throw PermissionDeniedException for null user");

			// Verify exception message
			assertNotNull(exception.getMessage(), "Exception message should not be null");
			assertTrue(exception.getMessage().contains("admin"), "Exception message should mention admin privileges");
		}

		/**
		 * Test: Should verify exact role match, not just non-null.
		 * 
		 * <p>
		 * <b>Setup:</b> Create users with different non-admin roles
		 * </p>
		 * <p>
		 * <b>Action:</b> Try ensureAdmin() for each non-admin role
		 * </p>
		 * <p>
		 * <b>Expected:</b> All should throw PermissionDeniedException
		 * </p>
		 */
		@Test
		@DisplayName("✔️ Should verify exact ADMIN role match")
		void testEnsureAdminStrictRoleCheck() {
			// Arrange - Create users with different roles
			Role[] nonAdminRoles = { Role.MEMBER, Role.LIBRARIAN };

			// Act & Assert
			for (Role role : nonAdminRoles) {
				UserDTO user = new UserDTO(UUID.randomUUID(), "testuser", "Test", "User", role);

				assertThrows(PermissionDeniedException.class, () -> {
					AuthorizationService.ensureAdmin(user);
				}, "Should throw exception for role: " + role);
			}
		}

		/**
		 * Test: Should work with multiple admin users.
		 * 
		 * <p>
		 * <b>Setup:</b> Create multiple admin users including real data
		 * </p>
		 * <p>
		 * <b>Action:</b> Call ensureAdmin() for each admin
		 * </p>
		 * <p>
		 * <b>Expected:</b> All should return true
		 * </p>
		 */
		@Test
		@DisplayName("✅ Should work with multiple admin users")
		void testEnsureAdminWithMultipleAdmins() throws PermissionDeniedException {
			// Arrange - Create multiple admin users
			UserDTO[] adminUsers = { new UserDTO(UUID.randomUUID(), "ahmadsalameh", "Ahmad", "Salameh", Role.ADMIN),
					new UserDTO(UUID.randomUUID(), "majdawwad", "Majd", "Awwad", Role.ADMIN) };

			// Act & Assert
			for (UserDTO admin : adminUsers) {
				boolean result = AuthorizationService.ensureAdmin(admin);
				assertTrue(result, "Should return true for admin: " + admin.username());
			}
		}

		/**
		 * Test: Should throw correct exception type.
		 * 
		 * <p>
		 * <b>Setup:</b> Create non-admin user
		 * </p>
		 * <p>
		 * <b>Action:</b> Call ensureAdmin()
		 * </p>
		 * <p>
		 * <b>Expected:</b> Exception should be PermissionDeniedException specifically
		 * </p>
		 */
		@Test
		@DisplayName("✔️ Should throw correct exception type")
		void testEnsureAdminExceptionType() {
			// Arrange
			UserDTO memberUser = new UserDTO(UUID.randomUUID(), "majdawwad", "Majd", "Awwad", Role.MEMBER);

			// Act & Assert
			Exception exception = assertThrows(Exception.class, () -> {
				AuthorizationService.ensureAdmin(memberUser);
			});

			// Verify it's the correct exception type
			assertTrue(exception instanceof PermissionDeniedException,
					"Should throw PermissionDeniedException, not generic Exception");
		}
	}

	/**
	 * Tests for ensureLibrarian() method. Validates librarian authorization checks.
	 */
	@Nested
	@DisplayName("📚 ensureLibrarian() Authorization Tests")
	class EnsureLibrarianTests {

		/**
		 * Test: Should return true when user is LIBRARIAN.
		 * 
		 * <p>
		 * <b>Setup:</b> Create UserDTO with Role.LIBRARIAN
		 * </p>
		 * <p>
		 * <b>Action:</b> Call ensureLibrarian(librarianUser)
		 * </p>
		 * <p>
		 * <b>Expected:</b> Should return true without throwing exception
		 * </p>
		 */
		@Test
		@DisplayName("✅ Should return true when user is LIBRARIAN")
		void testEnsureLibrarianWithValidLibrarian() throws PermissionDeniedException {
			// Arrange
			UserDTO librarianUser = new UserDTO(UUID.randomUUID(), "saramohammed", "Sara", "Mohammed", Role.LIBRARIAN);

			// Act
			boolean result = AuthorizationService.ensureLibrarian(librarianUser);

			// Assert
			assertTrue(result, "Should return true for valid librarian user");
		}

		/**
		 * Test: Should throw PermissionDeniedException when user is MEMBER.
		 * 
		 * <p>
		 * <b>Setup:</b> Create UserDTO with Role.MEMBER
		 * </p>
		 * <p>
		 * <b>Action:</b> Call ensureLibrarian(memberUser)
		 * </p>
		 * <p>
		 * <b>Expected:</b> PermissionDeniedException should be thrown
		 * </p>
		 */
		@Test
		@DisplayName("❌ Should throw when user is MEMBER")
		void testEnsureLibrarianWithMemberRole() {
			// Arrange
			UserDTO memberUser = new UserDTO(UUID.randomUUID(), "johndoe", "John", "Doe", Role.MEMBER);

			// Act & Assert
			PermissionDeniedException exception = assertThrows(PermissionDeniedException.class, () -> {
				AuthorizationService.ensureLibrarian(memberUser);
			}, "Should throw PermissionDeniedException for member user");

			// Verify exception message
			assertTrue(exception.getMessage().contains("librarian"),
					"Exception message should mention librarian privileges");
		}

		/**
		 * Test: Should throw PermissionDeniedException when user is ADMIN.
		 * 
		 * <p>
		 * <b>Setup:</b> Create UserDTO with Role.ADMIN
		 * </p>
		 * <p>
		 * <b>Action:</b> Call ensureLibrarian(adminUser)
		 * </p>
		 * <p>
		 * <b>Expected:</b> PermissionDeniedException should be thrown
		 * </p>
		 */
		@Test
		@DisplayName("❌ Should throw when user is ADMIN")
		void testEnsureLibrarianWithAdminRole() {
			// Arrange
			UserDTO adminUser = new UserDTO(UUID.randomUUID(), "ahmadsalameh", "Ahmad", "Salameh", Role.ADMIN);

			// Act & Assert
			assertThrows(PermissionDeniedException.class, () -> {
				AuthorizationService.ensureLibrarian(adminUser);
			}, "Should throw PermissionDeniedException for admin user");
		}

		/**
		 * Test: Should throw PermissionDeniedException when user is null.
		 * 
		 * <p>
		 * <b>Setup:</b> No setup, null user
		 * </p>
		 * <p>
		 * <b>Action:</b> Call ensureLibrarian(null)
		 * </p>
		 * <p>
		 * <b>Expected:</b> PermissionDeniedException should be thrown immediately
		 * </p>
		 */
		@Test
		@DisplayName("❌ Should throw when user is null")
		void testEnsureLibrarianWithNullUser() {
			// Act & Assert
			PermissionDeniedException exception = assertThrows(PermissionDeniedException.class, () -> {
				AuthorizationService.ensureLibrarian(null);
			}, "Should throw PermissionDeniedException for null user");

			// Verify exception message
			assertNotNull(exception.getMessage(), "Exception message should not be null");
			assertTrue(exception.getMessage().contains("librarian"),
					"Exception message should mention librarian privileges");
		}

		/**
		 * Test: Should verify exact role match for librarian.
		 * 
		 * <p>
		 * <b>Setup:</b> Create users with different non-librarian roles
		 * </p>
		 * <p>
		 * <b>Action:</b> Try ensureLibrarian() for each non-librarian role
		 * </p>
		 * <p>
		 * <b>Expected:</b> All should throw PermissionDeniedException
		 * </p>
		 */
		@Test
		@DisplayName("✔️ Should verify exact LIBRARIAN role match")
		void testEnsureLibrarianStrictRoleCheck() {
			// Arrange - Create users with different roles
			Role[] nonLibrarianRoles = { Role.ADMIN, Role.MEMBER };

			// Act & Assert
			for (Role role : nonLibrarianRoles) {
				UserDTO user = new UserDTO(UUID.randomUUID(), "testuser", "Test", "User", role);

				assertThrows(PermissionDeniedException.class, () -> {
					AuthorizationService.ensureLibrarian(user);
				}, "Should throw exception for role: " + role);
			}
		}

		/**
		 * Test: Should work with multiple librarian users.
		 * 
		 * <p>
		 * <b>Setup:</b> Create multiple librarian users
		 * </p>
		 * <p>
		 * <b>Action:</b> Call ensureLibrarian() for each librarian
		 * </p>
		 * <p>
		 * <b>Expected:</b> All should return true
		 * </p>
		 */
		@Test
		@DisplayName("✅ Should work with multiple librarian users")
		void testEnsureLibrarianWithMultipleLibrarians() throws PermissionDeniedException {
			// Arrange - Create multiple librarian users
			UserDTO[] librarianUsers = { new UserDTO(UUID.randomUUID(), "lib1", "Librarian", "One", Role.LIBRARIAN),
					new UserDTO(UUID.randomUUID(), "lib2", "Librarian", "Two", Role.LIBRARIAN),
					new UserDTO(UUID.randomUUID(), "saramohammed", "Sara", "Mohammed", Role.LIBRARIAN) };

			// Act & Assert
			for (UserDTO librarian : librarianUsers) {
				boolean result = AuthorizationService.ensureLibrarian(librarian);
				assertTrue(result, "Should return true for librarian: " + librarian.username());
			}
		}

		/**
		 * Test: Should throw correct exception type for librarian check.
		 * 
		 * <p>
		 * <b>Setup:</b> Create non-librarian user
		 * </p>
		 * <p>
		 * <b>Action:</b> Call ensureLibrarian()
		 * </p>
		 * <p>
		 * <b>Expected:</b> Exception should be PermissionDeniedException specifically
		 * </p>
		 */
		@Test
		@DisplayName("✔️ Should throw correct exception type")
		void testEnsureLibrarianExceptionType() {
			// Arrange
			UserDTO memberUser = new UserDTO(UUID.randomUUID(), "johndoe", "John", "Doe", Role.MEMBER);

			// Act & Assert
			Exception exception = assertThrows(Exception.class, () -> {
				AuthorizationService.ensureLibrarian(memberUser);
			});

			// Verify it's the correct exception type
			assertTrue(exception instanceof PermissionDeniedException,
					"Should throw PermissionDeniedException, not generic Exception");
		}
	}

	/**
	 * Edge case tests for authorization service. Tests boundary conditions and
	 * special scenarios.
	 */
	@Nested
	@DisplayName("🔧 Edge Case Tests")
	class EdgeCaseTests {

		/**
		 * Test: Should distinguish between different roles correctly.
		 * 
		 * <p>
		 * <b>Setup:</b> Create one user for each role
		 * </p>
		 * <p>
		 * <b>Action:</b> Check permissions for each user against both checks
		 * </p>
		 * <p>
		 * <b>Expected:</b> Each user should only pass their own role check
		 * </p>
		 */
		@Test
		@DisplayName("✔️ Should distinguish between different roles")
		void testRoleDistinction() {
			// Arrange
			UserDTO adminUser = new UserDTO(UUID.randomUUID(), "admin", "Admin", "User", Role.ADMIN);
			UserDTO librarianUser = new UserDTO(UUID.randomUUID(), "lib", "Librarian", "User", Role.LIBRARIAN);
			UserDTO memberUser = new UserDTO(UUID.randomUUID(), "member", "Member", "User", Role.MEMBER);

			// Act & Assert
			try {
				AuthorizationService.ensureAdmin(adminUser);
				// Admin passes admin check
			} catch (PermissionDeniedException e) {
				fail("Admin should pass admin check");
			}

			try {
				AuthorizationService.ensureLibrarian(librarianUser);
				// Librarian passes librarian check
			} catch (PermissionDeniedException e) {
				fail("Librarian should pass librarian check");
			}

			// Member should fail both checks
			assertThrows(PermissionDeniedException.class, () -> AuthorizationService.ensureAdmin(memberUser));
			assertThrows(PermissionDeniedException.class, () -> AuthorizationService.ensureLibrarian(memberUser));
		}

		/**
		 * Test: Should handle special characters in usernames.
		 * 
		 * <p>
		 * <b>Setup:</b> Create users with special characters in username
		 * </p>
		 * <p>
		 * <b>Action:</b> Check authorization for these users
		 * </p>
		 * <p>
		 * <b>Expected:</b> Authorization checks should work regardless of username
		 * format
		 * </p>
		 */
		@Test
		@DisplayName("✔️ Should handle special characters in usernames")
		void testWithSpecialCharacterUsernames() throws PermissionDeniedException {
			// Arrange
			UserDTO adminUser = new UserDTO(UUID.randomUUID(), "admin@domain.com", "Admin", "User", Role.ADMIN);
			UserDTO librarianUser = new UserDTO(UUID.randomUUID(), "lib-user_123", "Lib", "User", Role.LIBRARIAN);

			// Act & Assert
			assertTrue(AuthorizationService.ensureAdmin(adminUser));
			assertTrue(AuthorizationService.ensureLibrarian(librarianUser));
		}

		/**
		 * Test: Should handle consecutive authorization checks.
		 * 
		 * <p>
		 * <b>Setup:</b> Create an admin and librarian user
		 * </p>
		 * <p>
		 * <b>Action:</b> Call ensureAdmin/ensureLibrarian multiple times consecutively
		 * </p>
		 * <p>
		 * <b>Expected:</b> All checks should work consistently
		 * </p>
		 */
		@Test
		@DisplayName("✔️ Should handle consecutive authorization checks")
		void testMultipleConsecutiveChecks() throws PermissionDeniedException {
			// Arrange
			UserDTO adminUser = new UserDTO(UUID.randomUUID(), "admin", "Admin", "User", Role.ADMIN);
			UserDTO librarianUser = new UserDTO(UUID.randomUUID(), "lib", "Lib", "User", Role.LIBRARIAN);

			// Act & Assert - Call multiple times
			assertTrue(AuthorizationService.ensureAdmin(adminUser));
			assertTrue(AuthorizationService.ensureLibrarian(librarianUser));
			assertTrue(AuthorizationService.ensureAdmin(adminUser)); // Call again
			assertTrue(AuthorizationService.ensureLibrarian(librarianUser)); // Call again
		}
	}
}