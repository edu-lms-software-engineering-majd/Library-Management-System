package lms.domain.exception;

/**
 * Thrown when a user provides an incorrect password.
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class InvalidPasswordException extends Exception {

	/**
	 * Creates a new exception with the specified message.
	 * 
	 * @param message the detail message
	 */
	public InvalidPasswordException(String message) {
		super(message);

	}
}
