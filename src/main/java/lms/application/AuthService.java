package lms.application;

import lms.domain.User;
import lms.domain.UserRepository;
import lms.domain.exception.InvalidPasswordException;
import lms.domain.exception.UserNotFoundException;

/**
 * Application-level service responsible for handling authentication operations
 * within the Library Management System (LMS).
 *
 * <p>
 * The {@code AuthService} validates user credentials, maintains the current
 * authentication state, and exposes a safe {@link UserDTO} for use in the
 * application layer and presentation layer.
 * </p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 * <li>Authenticate users based on username and password</li>
 * <li>Maintain the currently authenticated user in memory</li>
 * <li>Provide access to the authenticated user's information as a DTO</li>
 * <li>Handle logout and clear authentication state</li>
 * </ul>
 *
 * <h2>Architectural Notes:</h2>
 * <p>
 * This class belongs to the <strong>application layer</strong> and bridges
 * between the domain repository and the presentation/UI layer.
 * </p>
 *
 * <p>
 * <b>Limitation:</b> Session state is stored statically inside the JVM, which
 * means this implementation is not thread-safe and is suitable only for CLI
 * simulations or single-user environments.
 * </p>
 *
 * <h2>Example:</h2>
 * 
 * <pre>{@code
 * AuthService auth = new AuthService(userRepo);
 * auth.login("john", "12345");
 * UserDTO current = AuthService.getCurrentUser();
 * System.out.println(current.username());
 * auth.logout();
 * }</pre>
 *
 * @author Majd Awwad
 * @version 2.0
 */
public class AuthService {

	/** Repository used to access domain-level user data */
	private final UserRepository userRepo;

	/** Currently logged-in user (null if no active session) */
	private static UserDTO currentUser;

	/**
	 * Creates a new authentication service using the provided user repository.
	 *
	 * @param userRepo the repository used to retrieve user data
	 * @throws IllegalArgumentException if the repository is null
	 */
	public AuthService(UserRepository userRepo) {
		if (userRepo == null)
			throw new IllegalArgumentException("UserRepository cannot be null");

		this.userRepo = userRepo;
	}

	/**
	 * Attempts to authenticate a user using the provided username and password.
	 *
	 * @param userName    the username entered by the user
	 * @param rawPassword the password provided in plain text
	 * @return {@code true} if authentication succeeds
	 *
	 * @throws UserNotFoundException    if the username does not exist
	 * @throws InvalidPasswordException if the password is incorrect
	 */
	public boolean login(String userName, String rawPassword) throws UserNotFoundException, InvalidPasswordException {

		User user = userRepo.getByUserName(userName)
				.orElseThrow(() -> new UserNotFoundException("User '" + userName + "' does not exist."));

		if (!user.verifyPassword(rawPassword)) {
			throw new InvalidPasswordException("Incorrect password.");
		}

		 
		AuthService.currentUser = user.toDTO();
		return true;
	}

	/**
	 * Logs out the currently authenticated user.
	 *
	 * @throws IllegalStateException if there is no user currently logged in
	 */
	public void logout() {
		if (currentUser == null)
			throw new IllegalStateException("No user is currently logged in.");

		currentUser = null;
	}

	
	/**
	 * Retrieves the currently authenticated user as a DTO.
	 *
	 * @return the current {@link UserDTO}, or {@code null} if no user is logged in
	 */
	public static UserDTO getCurrentUser() {
		return currentUser;
	}
}
