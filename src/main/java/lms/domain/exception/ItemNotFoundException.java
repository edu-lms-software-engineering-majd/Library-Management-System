package lms.domain.exception;

/**
 * Thrown when a requested library item cannot be found.
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class ItemNotFoundException extends Exception {

	/**
	 * Creates a new exception with the specified message.
	 * 
	 * @param message the detail message
	 */
	public ItemNotFoundException(String message) {
		super(message);
	}

}
