package lms.application;

import lms.domain.User;
import lms.domain.UserRepository;
import lms.domain.exception.InvalidPasswordException;
import lms.domain.exception.UserNotFoundException;
import lms.persistence.StaticUserRepository;

/**
 * Singleton service managing user authentication and session state in the Library Management System.
 *
 * <p>
 * This service handles user login, logout, and session management. It validates credentials
 * against the user repository and maintains the currently authenticated user as a {@link UserDTO}.
 * </p>
 *
 * <p>
 * Key responsibilities:
 * </p>
 * <ul>
 * <li>Authenticate users by validating username and password</li>
 * <li>Maintain active user session in memory</li>
 * <li>Provide access to current user information via DTO</li>
 * <li>Handle user logout and session cleanup</li>
 * </ul>
 *
 * <p>
 * <b>Note:</b> This singleton implementation is not thread-safe and is intended for
 * single-threaded CLI applications.
 * </p>
 *
 * @author Majd Awwad
 * @version 2.2
 * @see UserDTO
 * @see UserRepository
 */
public class AuthService {
	
	/**
	 * The singleton instance of the authentication service.
	 * Initialized lazily on first call to {@link #getInstance(UserRepository)}.
	 * 
	 * @see #getInstance(UserRepository)
	 * @see #getInstance()
	 */
	private static AuthService instance;

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
	 * Lazily initializes with {@link StaticUserRepository} on first call.
	 *
	 * @return the singleton {@code AuthService} instance
	 */
	public static AuthService getInstance() {
		if (instance == null) {
			instance = new AuthService(StaticUserRepository.getInstance());
		}
		return instance;
	}	
	

	/**
	 * Authenticates a user with the provided credentials.
	 *
	 * <p>
	 * Verifies the username and password, then creates an active session by storing
	 * the authenticated user as a {@link UserDTO}. Any existing session is replaced.
	 * </p>
	 *
	 * @param userName    the username to authenticate
	 * @param rawPassword the plain text password
	 * @return {@code true} if authentication succeeds
	 * @throws UserNotFoundException    if the username does not exist
	 * @throws InvalidPasswordException if the password is incorrect
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
	 * Logs out the current user and clears the session.
	 *
	 * @throws IllegalStateException if no user is currently logged in
	 */
	public void logout() {
		if (currentUser == null)
			throw new IllegalStateException("No user is currently logged in.");

		currentUser = null;
	}

	
	/**
	 * Returns the currently authenticated user as a DTO.
	 *
	 * @return the current {@link UserDTO}, or {@code null} if no user is logged in
	 */
	public UserDTO getCurrentUser() {
		return currentUser;
	}
}
