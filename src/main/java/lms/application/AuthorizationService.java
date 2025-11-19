package lms.application;

import lms.domain.Role;
import lms.domain.exception.PermissionDeniedException;

/**
 * Utility class providing authorization checks for the application.
 *
 * *
 * <p>
 * This class contains static methods to enforce role-based access control
 * (RBAC) across services. It is not intended to be instantiated or extended.
 * </p>
 *
 * <p>
 * Typical usage:
 * </p>
 * 
 * <pre>{@code
 * User currentUser = AuthService.getCurrentUser();
 * AuthorizationService.ensureAdmin(currentUser);
 * }</pre>
 *
 * <p>
 * All methods are static and not thread-safe.
 * </p>
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public final class AuthorizationService {

	private AuthorizationService() {
		throw new UnsupportedOperationException("Utility class cannot be instantiated");
	}

	/**
	 * Ensures that the given user has {@link Role#ADMIN} privileges.
	 *
	 * <p>
	 * If the user is {@code null} or does not have the admin role, a
	 * {@link PermissionDeniedException} is thrown.
	 * </p>
	 *
	 * @param user the user attempting to perform the action
	 * @throws PermissionDeniedException if the user is not an admin
	 */
	public static boolean ensureAdmin(UserDTO user) throws PermissionDeniedException {
		
		if (user == null || user.role() != Role.ADMIN) {
			throw new PermissionDeniedException("Action requires admin privileges.");
		}
		return true;
		
	}
	

	public static boolean ensureLibrarian(UserDTO userID) throws PermissionDeniedException {
		
		if(userID == null || userID.role() != Role.LIBRARIAN) {
			throw new PermissionDeniedException("Action requires librarian privileges.");
		}
		return true;
	}
}
