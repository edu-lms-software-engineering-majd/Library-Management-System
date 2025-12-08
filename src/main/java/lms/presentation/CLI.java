package lms.presentation;

/**
 * Common interface for all Command Line Interface (CLI) components in the
 * Library Management System.
 * 
 * <p>
 * Implementations include {@link LibraryCLI} for login, {@link AdminCLI} for
 * administrators, and {@link UserCLI} for regular users and librarians.
 * </p>
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public interface CLI {

	/**
	 * Starts the command-line interface and handles user interactions.
	 *
	 * @throws IllegalAccessException if the user is not authorized to access this CLI
	 */
	void start() throws IllegalAccessException;
}
