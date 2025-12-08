package lms.domain.exception;

/**
 * Thrown when a library item is currently unavailable for borrowing.
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class ItemNotAvailableException extends Exception {

	/**
	 * Creates a new exception with the specified message.
	 * 
	 * @param message explanation of why the item is unavailable
	 */
	public ItemNotAvailableException(String message) {
		super(message);
	}

}
