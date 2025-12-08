package lms.domain.exception;

/**
 * Thrown when a user is not allowed to borrow items.
 * 
 * <p>Reasons may include: maximum limit reached, outstanding fines,
 * suspended account, or overdue items.</p>
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class BorrowNotAllowedException extends Exception {

	/**
	 * Creates a new exception with the specified message.
	 * 
	 * @param message explanation of why borrowing is not allowed
	 */
	public BorrowNotAllowedException(String message) {
		super(message);
	}
}
