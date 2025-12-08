package lms.domain.exception;

/**
 * Thrown when a user cannot be found in the system.
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class UserNotFoundException extends Exception {

	/**
	 * Creates a new exception with the specified message.
	 * 
	 * @param message the detail message
	 */
	public UserNotFoundException(String message) {
		super(message);
	}

}
