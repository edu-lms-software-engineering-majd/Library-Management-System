package lms.domain.exception;

/**
 * Thrown when a user attempts to log in with an incorrect password.
 * 
 * <p>This exception should be used by the authentication service
 * to signal a failed login attempt due to password mismatch.</p>
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class InvalidPasswordException extends Exception {

    /**
     * Constructs a new {@code InvalidPasswordException} with the specified detail message.
     * 
     * @param message the detail message explaining the reason for the exception
     */
    public InvalidPasswordException(String message) {
        super(message);
    }
}
