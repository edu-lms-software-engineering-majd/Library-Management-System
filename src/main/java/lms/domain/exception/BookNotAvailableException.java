package lms.domain.exception;

/**
 * Thrown when a book is currently unavailable for borrowing.
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class BookNotAvailableException extends Exception {

	/**
	 * Creates a new exception with the specified message.
	 * 
	 * @param message explanation of why the book is unavailable
	 */
	public BookNotAvailableException(String message) {
		super(message);
	}
}
