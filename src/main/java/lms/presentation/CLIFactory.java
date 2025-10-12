package lms.presentation;

import lms.application.AuthService;
import lms.application.BookService;
import lms.application.UserService;

/**
 * Factory class for creating the appropriate {@link CLI} implementation based
 * on the currently logged-in user's role.
 *
 * <p>
 * This class centralizes the logic for determining which CLI interface to
 * present to a user, ensuring that users see menus appropriate to their role:
 * </p>
 * <ul>
 * <li>ADMIN – {@link AdminCLI}</li>
 * <li>MEMBER or LIBRARIAN – {@link UserCLI}</li>
 * </ul>
 *
 * <p>
 * Example usage:
 * </p>
 * 
 * <pre>
 * CLI cli = CLIFactory.getCLI(authService, userService, bookService);
 * cli.start();
 * </pre>
 * 
 * <p>
 * Note: The factory relies on {@link AuthService#getCurrentUser()} to determine
 * the user's role, and will throw an exception if the role is unsupported.
 * </p>
 * 
 * @author Majd
 * @version 1.0
 */
public class CLIFactory {

	/**
	 * Returns the appropriate CLI implementation based on the current user's role.
	 *
	 * @param authService the authentication service
	 * @param userService the user management service
	 * @param bookService the book management service
	 * @return a {@link CLI} instance for the current user
	 * @throws IllegalStateException if the user's role is unsupported
	 */
	public static CLI getCLI(AuthService authService, UserService userService, BookService bookService) {
		switch (AuthService.getCurrentUser().role()) {
		case ADMIN:
			return new AdminCLI(userService, bookService, authService);
		case MEMBER:
		case LIBRARIAN:
			return new UserCLI();
		default:
			throw new IllegalStateException("Unsupported role: " + AuthService.getCurrentUser().role());
		}
	}

}
