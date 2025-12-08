package lms.domain.exception;

/**
 * Thrown when a requested book cannot be found.
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class BookNotFoundException extends Exception {

	/**
	 * Creates a new exception with the specified message.
	 * 
	 * @param message the detail message
	 */
	public BookNotFoundException(String message){
		super(message);
	}
}
