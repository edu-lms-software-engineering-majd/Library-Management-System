package lms.domain.exception;

/**
 * Thrown when an invalid or unsupported item type is referenced.
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class ItemTypeNotFoundException extends Exception {

	/**
	 * Creates a new exception with a default message.
	 */
	public ItemTypeNotFoundException() {
		super("Item type not found");
	}

	/**
	 * Creates a new exception with the specified message.
	 * 
	 * @param message the detail message
	 */
	public ItemTypeNotFoundException(String message) {
		super(message);
	}

}
