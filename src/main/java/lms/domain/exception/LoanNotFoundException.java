package lms.domain.exception;

/**
 * Thrown when a requested loan cannot be found.
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class LoanNotFoundException extends Exception {

	/**
	 * Creates a new exception with the specified message.
	 * 
	 * @param message the detail message
	 */
	public LoanNotFoundException(String message) {
		super(message);
	}

}
