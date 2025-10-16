package lms.application;

import java.util.Optional;

import lms.domain.User;
import lms.domain.UserRepo;
import lms.domain.exception.InvalidPasswordException;
import lms.domain.exception.UserNotFoundException;

/**
 * Application service for handling authentication in the Library Management
 * System.
 *
 * <p>
 * The {@code AuthService} is responsible for managing login, logout, and
 * retrieving the currently authenticated user. It coordinates with the
 * {@link UserRepo} from the domain layer to validate credentials and maintain
 * the session state.
 * </p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 * <li>Authenticate users by verifying credentials against the repository</li>
 * <li>Maintain the currently logged-in user in memory</li>
 * <li>Provide access to the current user's information as a DTO</li>
 * <li>Handle logout by clearing the current session</li>
 * </ul>
 *
 * <h2>Limitations:</h2>
 * <p>
 * - The implementation stores the current user in a static field, making it
 * <b>not thread-safe</b>. - It assumes a single-user context (suitable for CLI
 * simulations), but should be adapted for real multi-user environments (e.g.
 * web apps).
 * </p>
 *
 * <h2>Example Usage:</h2>
 * 
 * <pre>{@code
 * UserRepo userRepo = new StaticUserRepo();
 * AuthService authService = new AuthService(userRepo);
 *
 * try {
 * 	authService.login("john_doe", "password123");
 * 	UserDTO current = AuthService.getCurrentUser();
 * 	System.out.println("Logged in as: " + current.username());
 * 	authService.logout();
 * } catch (UserNotFoundException | InvalidPasswordException e) {
 * 	System.out.println("Authentication failed: " + e.getMessage());
 * }
 * }</pre>
 *
 * <p>
 * This class belongs to the <b>application layer</b>.
 * </p>
 *
 * @author Majd Awwad
 * @version 2.0
 */

public class AuthService {

	/** Repository used to access and manage users */
	private final UserRepo userRepo;

	/** Currently logged-in user, or null if no user is logged in */
	private static UserDTO currentUser;

	/**
	 * Constructs an {@code AuthService} with the specified user repository.
	 * 
	 * @param userRepo the repository used to retrieve user data
	 */
	
	public AuthService(UserRepo userRepo) {
		this.userRepo = userRepo;
	}

	
	/**
     * Attempts to log in a user with the given username and password.
     *
     * @param userName the username of the user
     * @param rawPassword the plain-text password to verify
     * @return {@code true} if login is successful
     * @throws UserNotFoundException if no user exists with the specified username
     * @throws InvalidPasswordException if the password is incorrect
     */

	public boolean login(String userName, String rawPassword) throws UserNotFoundException, InvalidPasswordException {

		Optional<User> userOptional = userRepo.getUserByUserName(userName);

		if (userOptional.isEmpty()) {
			throw new UserNotFoundException("User '" + userName + "' does not exist.");
		}

		User user = userOptional.get();
		
		if (!user.verifyPassword(rawPassword)) {
			throw new InvalidPasswordException("Incorrect password.");
		}

		UserDTO userDTO = user.toDTO();
		AuthService.currentUser = userDTO;

		return true;
	}

	/**
     * Logs out the currently logged-in user.
     *
     * @throws IllegalStateException if no user is currently logged in
     */
	
	public void logout() throws IllegalStateException {
		if (currentUser == null) {
			throw new IllegalStateException("No user is logged in");
		}
		currentUser = null;
	}

	/**
     * Returns the currently logged-in user as a DTO.
     *
     * @return the {@link UserDTO} of the current user,
     *         or {@code null} if no user is logged in
     */
	
	public static UserDTO getCurrentUser() {
		return currentUser;
	}
}
