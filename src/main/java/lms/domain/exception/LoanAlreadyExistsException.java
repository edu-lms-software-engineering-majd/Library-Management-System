package lms.domain.exception;

/**
 * Thrown when attempting to create a duplicate loan.
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class LoanAlreadyExistsException extends Exception {

	/**
	 * Creates a new exception with a default message.
	 */
	public LoanAlreadyExistsException() {
		super("Loan already exists for this user and item");
	}

	/**
	 * Creates a new exception with the specified message.
	 * 
	 * @param message the detail message
	 */
	public LoanAlreadyExistsException(String message) {
		super(message);
	}

}
