package lms.application;

import lms.domain.Role;
import lms.domain.exception.PermissionDeniedException;

/**
 * Utility class providing authorization checks for the application.
 *
 * This class enforces role-based access control (RBAC).
 */
public final class AuthorizationService {

	private AuthorizationService() {
		throw new UnsupportedOperationException("Utility class cannot be instantiated");
	}

	/**
	 * Internal reusable role-check method that throws exceptions when unauthorized
	 */
	private static void ensureRole(UserDTO user, Role requiredRole) throws PermissionDeniedException {
		if (user == null)
			throw new PermissionDeniedException("No authenticated user found.");

		if (user.role() != requiredRole)
			throw new PermissionDeniedException("Action requires " + requiredRole + " privileges.");
	}

	/** Ensures ADMIN access (exception version) */
	public static void ensureAdmin(UserDTO user) throws PermissionDeniedException {
		ensureRole(user, Role.ADMIN);
	}

	/** Ensures LIBRARIAN access (exception version) */
	public static void ensureLibrarian(UserDTO user) throws PermissionDeniedException {
		ensureRole(user, Role.LIBRARIAN);
	}

	// =============================================================
	// BOOLEAN VERSIONS (USED IN IF CONDITIONS)
	// =============================================================

	/**
	 * Returns true if the user is an ADMIN.
	 */
	public static boolean isAdmin(UserDTO user) {
		return user != null && user.role() == Role.ADMIN;
	}

	/**
	 * Returns true if the user is a LIBRARIAN.
	 */
	public static boolean isLibrarian(UserDTO user) {
		return user != null && user.role() == Role.LIBRARIAN;
	}
}
