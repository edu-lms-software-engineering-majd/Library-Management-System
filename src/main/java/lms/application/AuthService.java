package lms.application;

import lms.domain.User;
import lms.domain.UserRepository;
import lms.domain.exception.InvalidPasswordException;
import lms.domain.exception.UserNotFoundException;
import lms.persistence.StaticUserRepository;

/**
 * Singleton authentication service responsible for managing user authentication
 * and session state within the Library Management System (LMS).
 *
 * <p>
 * The {@code AuthService} implements the Singleton design pattern to ensure a single
 * authentication context across the application. It validates user credentials against
 * the domain model, maintains the current authentication state, and exposes a safe
 * {@link UserDTO} for use in the application and presentation layers.
 * </p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 * <li>Provide a single global instance of the authentication service (Singleton)</li>
 * <li>Authenticate users based on username and password verification</li>
 * <li>Maintain the currently authenticated user session in memory</li>
 * <li>Provide access to the authenticated user's information as a DTO</li>
 * <li>Handle logout operations and clear authentication state</li>
 * </ul>
 *
 * <h2>Design Pattern:</h2>
 * <p>
 * This class uses the <strong>Singleton pattern</strong> with lazy initialization.
 * The instance is created on first access via {@link #getInstance(UserRepository)}
 * and reused for all subsequent calls.
 * </p>
 *
 * <h2>Architectural Notes:</h2>
 * <p>
 * This class belongs to the <strong>application layer</strong> and acts as a bridge
 * between the domain repository ({@link UserRepository}) and the presentation/UI layer.
 * It follows the principle of exposing DTOs rather than domain entities to maintain
 * proper separation of concerns.
 * </p>
 *
 * <h2>Thread Safety:</h2>
 * <p>
 * <b>Warning:</b> This implementation is <strong>not thread-safe</strong>. The singleton
 * instance and session state are stored in memory without synchronization. This design
 * is suitable only for:
 * </p>
 * <ul>
 * <li>Single-threaded CLI applications</li>
 * <li>Single-user desktop applications</li>
 * <li>Testing and prototyping environments</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * 
 * <pre>{@code
 * // Initialize the service (first time only)
 * AuthService auth = AuthService.getInstance(userRepo);
 * 
 * // Login
 * try {
 *     auth.login("john.doe", "securePassword123");
 *     UserDTO currentUser = auth.getCurrentUser();
 *     System.out.println("Welcome, " + currentUser.firstName());
 * } catch (UserNotFoundException | InvalidPasswordException e) {
 *     System.err.println("Authentication failed: " + e.getMessage());
 * }
 * 
 * // Access singleton instance later
 * AuthService auth2 = AuthService.getInstance();
 * UserDTO user = auth2.getCurrentUser();
 * 
 * // Logout
 * auth.logout();
 * }</pre>
 *
 * @author Majd Awwad
 * @version 2.2
 * @see UserDTO
 * @see UserRepository
 * @see lms.domain.User
 */
public class AuthService {
	
	/**
	 * The singleton instance of the authentication service.
	 * Initialized lazily on first call to {@link #getInstance(UserRepository)}.
	 * 
	 * @see #getInstance(UserRepository)
	 * @see #getInstance()
	 */
	private static AuthService INSTANCE;

	/** Repository used to access domain-level user data */
	private final UserRepository userRepo;

	/** Currently logged-in user (null if no active session) */
	private UserDTO currentUser;

	/**
	 * Creates a new authentication service using the provided user repository.
	 *
	 * @param userRepo the repository used to retrieve user data
	 * @throws IllegalArgumentException if the repository is null
	 */
	private AuthService(UserRepository userRepo) {
		if (userRepo == null)
			throw new IllegalArgumentException("UserRepository cannot be null");

		this.userRepo = userRepo;
	}
	
	/**
	 * Returns the singleton instance of the authentication service.
	 * Creates the instance on first invocation (lazy initialization).
	 *
	 * <p>
	 * On first call, this method initializes the authentication service with
	 * {@link StaticUserRepository} as the data source. Subsequent calls return
	 * the existing instance without re-initialization.
	 * </p>
	 *
	 * <p>
	 * <b>Note:</b> This implementation is tightly coupled to {@link StaticUserRepository}.
	 * For better testability and flexibility, consider dependency injection patterns
	 * in future refactoring.
	 * </p>
	 *
	 * @return the singleton {@code AuthService} instance
	 */
	public static AuthService getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new AuthService(StaticUserRepository.getInstance());
		}
		return INSTANCE;
	}	
	

	/**
	 * Attempts to authenticate a user using the provided username and password.
	 *
	 * <p>
	 * This method performs the following steps:
	 * </p>
	 * <ol>
	 * <li>Retrieves the user from the repository by username</li>
	 * <li>Verifies the provided password against the stored credentials</li>
	 * <li>Creates and stores a {@link UserDTO} for the authenticated user</li>
	 * <li>Maintains the session state until logout is called</li>
	 * </ol>
	 *
	 * <p>
	 * <b>Note:</b> This implementation replaces any existing session. If a user
	 * is already logged in, calling this method will overwrite the current session
	 * with the newly authenticated user.
	 * </p>
	 *
	 * @param userName    the username entered by the user (must not be null or empty)
	 * @param rawPassword the password provided in plain text (must not be null)
	 * @return {@code true} if authentication succeeds
	 *
	 * @throws UserNotFoundException    if the username does not exist in the repository
	 * @throws InvalidPasswordException if the password is incorrect for the given username
	 */
	public boolean login(String userName, String rawPassword) throws UserNotFoundException, InvalidPasswordException {

		User user = userRepo.getByUserName(userName)
				.orElseThrow(() -> new UserNotFoundException("User '" + userName + "' does not exist."));

		if (!user.verifyPassword(rawPassword)) {
			throw new InvalidPasswordException("Incorrect password.");
		}

		 
		this.currentUser = user.toDTO();
		return true;
	}

	/**
	 * Logs out the currently authenticated user and clears the session state.
	 *
	 * <p>
	 * This method terminates the current user session by setting the
	 * {@code currentUser} to {@code null}. After calling this method,
	 * {@link #getCurrentUser()} will return {@code null} until a new
	 * successful login occurs.
	 * </p>
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
	 * <p>
	 * This method returns a Data Transfer Object (DTO) representation of the
	 * authenticated user, which is safe to pass to the presentation layer
	 * without exposing domain entity internals.
	 * </p>
	 *
	 * @return the current {@link UserDTO}, or {@code null} if no user is logged in
	 */
	public UserDTO getCurrentUser() {
		return currentUser;
	}
}
