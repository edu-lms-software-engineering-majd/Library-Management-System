package lms.domain.exception;

/**
 * Thrown when a user attempts an action without the required permissions.
 *
 * <p>This exception is typically raised during role-based access control
 * checks, such as when a non-admin tries to add or remove resources.</p>
 *
 * @author Majd Awwad
 * @version 1.0
 */
public class PermissionDeniedException extends Exception {
    
    /**
     * Creates a new exception with the specified detail message.
     *
     * @param message explanation of why the permission was denied
     */
    public PermissionDeniedException(String message) {
        super(message);
    }
}
