package lms.domain.exception;

/**
 * Thrown when a user attempts an action without required permissions.
 *
 * @author Majd Awwad
 * @version 1.0
 */
public class PermissionDeniedException extends Exception {

	/**
	 * Creates a new exception with the specified message.
	 *
	 * @param message explanation of the permission denial
	 */
	public PermissionDeniedException(String message) {
		super(message);
	}
}
