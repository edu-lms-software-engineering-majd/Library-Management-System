package lms.domain.exception;

/**
 * Thrown when a user attempts to reuse a previous password.
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class PasswordReuseException extends RuntimeException {

	/**
	 * Creates a new exception with the specified message.
	 * 
	 * @param message explanation of the password reuse violation
	 */
	public PasswordReuseException(String message) {
		super(message);
	}
}
