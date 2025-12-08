package lms.application;

import lms.domain.Role;
import lms.domain.exception.PermissionDeniedException;



/**
 * Utility class providing role-based authorization checks for the Library Management System.
 *
 * <p>
 * This class enforces role-based access control (RBAC) by validating user permissions
 * before allowing access to restricted operations. It provides both exception-throwing
 * methods for enforcing permissions and boolean methods for checking permissions.
 * </p>
 *
 * <p>
 * Key features:
 * </p>
 * <ul>
 * <li>Enforce admin and librarian access requirements</li>
 * <li>Check user roles without throwing exceptions</li>
 * <li>Centralized authorization logic for consistent security</li>
 * </ul>
 *
 * @author Majd Awwad
 * @version 1.0
 */
public final class AuthorizationService {

	private AuthorizationService() {
		throw new UnsupportedOperationException("Utility class cannot be instantiated");
	}

	/**
	 * Validates that a user has the required role.
	 *
	 * @param user the user to validate
	 * @param requiredRole the role required for access
	 * @throws PermissionDeniedException if the user lacks the required role
	 */
	private static void ensureRole(UserDTO user, Role requiredRole) throws PermissionDeniedException {
		if (user == null)
			throw new PermissionDeniedException("No authenticated user found.");

		if (user.role() != requiredRole)
			throw new PermissionDeniedException("Action requires " + requiredRole + " privileges.");
	}

	/**
	 * Ensures the user has ADMIN privileges.
	 *
	 * @param user the user to validate
	 * @throws PermissionDeniedException if the user is not an admin
	 */
	public static void ensureAdmin(UserDTO user) throws PermissionDeniedException {
		ensureRole(user, Role.ADMIN);
	}

	/**
	 * Ensures the user has LIBRARIAN privileges.
	 *
	 * @param user the user to validate
	 * @throws PermissionDeniedException if the user is not a librarian
	 */
	public static void ensureLibrarian(UserDTO user) throws PermissionDeniedException {
		ensureRole(user, Role.LIBRARIAN);
	}

	/**
	 * Checks if the user is an ADMIN.
	 *
	 * @param user the user to check
	 * @return {@code true} if the user is an admin, {@code false} otherwise
	 */
	public static boolean isAdmin(UserDTO user) {
		return user != null && user.role() == Role.ADMIN;
	}

	/**
	 * Checks if the user is a LIBRARIAN.
	 *
	 * @param user the user to check
	 * @return {@code true} if the user is a librarian, {@code false} otherwise
	 */
	public static boolean isLibrarian(UserDTO user) {
		return user != null && user.role() == Role.LIBRARIAN;
	}
}
