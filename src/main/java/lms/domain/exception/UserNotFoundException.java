package lms.domain.exception;

/**
 * Thrown when a user cannot be found in the system.
 * 
 * <p>This exception should be used by the authentication service
 * or any user-related operations to signal that a user with
 * a given identifier (username, email, etc.) does not exist.</p>
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class UserNotFoundException extends Exception {

    /**
     * Constructs a new {@code UserNotFoundException} with the specified detail message.
     * 
     * @param message the detail message explaining why the exception was thrown
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}
