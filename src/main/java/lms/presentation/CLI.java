package lms.presentation;

/**
 * Common interface for all Command Line Interface (CLI) components in the
 * Library Management System.
 *
 * <p>
 * Any class implementing {@code CLI} must provide a {@code start} method, which
 * serves as the entry point for displaying menus and handling user
 * interactions.
 * </p>
 *
 * <p>
 * Typical implementations include:
 * </p>
 * <ul>
 * <li>{@link LibraryCLI} – main application menu for login and navigation</li>
 * <li>{@link UserCLI} – menu for regular users to search, borrow, return books,
 * and pay fines</li>
 * <li>{@link AdminCLI} – menu for administrators to manage users, books, and
 * system settings</li>
 * </ul>
 * 
 * <p>
 * Example usage:
 * </p>
 * 
 * <pre>
 * CLI cli = new LibraryCLI(authService, userService, bookService);
 * cli.start();
 * </pre>
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public interface CLI {

	/**
	 * Starts the command-line interface and handles user interactions.
	 *
	 * @throws IllegalAccessException if the current user is not authorized to
	 *                                access this CLI
	 */
	void start() throws IllegalAccessException;
}
