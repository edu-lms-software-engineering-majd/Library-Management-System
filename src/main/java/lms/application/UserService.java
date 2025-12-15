package lms.application;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import lms.domain.Role;
import lms.domain.User;
import lms.domain.UserRepository;
import lms.domain.exception.UserNotFoundException;
import lms.domain.utils.PasswordUtils;

/**
 * Application service for managing users in the Library Management System.
 *
 * <p>
 * This service coordinates user-related operations including retrieval,
 * updates, registration, and deletion. It enforces authorization rules and
 * returns data as {@link UserDTO} objects to maintain proper layer separation.
 * </p>
 *
 * <p>
 * Key responsibilities:
 * </p>
 * <ul>
 * <li>Retrieve users by username or list all users</li>
 * <li>Update user information with authorization checks</li>
 * <li>Register new users with hashed passwords</li>
 * <li>Check borrowing eligibility</li>
 * </ul>
 * 
 * @author Majd Awwad
 * @version 1.0
 */

public class UserService {

	private final UserRepository userRepo;

	/**
	 * Constructs a UserService with the required repository.
	 *
	 * @param userRepo the repository for accessing user data
	 */
	public UserService(UserRepository userRepo) {
		this.userRepo = userRepo;
	}

	/**
	 * Retrieves all users in the system as DTOs.
	 *
	 * @return an unmodifiable list of all users
	 */
	public List<UserDTO> getAllUsers() {
		List<User> actualUsers = userRepo.getAllUsers();
		List<UserDTO> users = actualUsers.stream().map(User::toDTO).collect(Collectors.toList());
		return Collections.unmodifiableList(users);
	}

	/**
	 * Retrieves a user by their username.
	 *
	 * @param username the username to search for
	 * @return the user DTO
	 * @throws UserNotFoundException if no user exists with the given username
	 */
	public UserDTO getUserByUsername(String username) throws UserNotFoundException {

		Optional<User> userOptional = userRepo.getByUserName(username);

		if (userOptional.isEmpty()) {
			throw new UserNotFoundException("No such user with this username");
		}

		return userOptional.get().toDTO();
	}

	/**
	 * Retrieves the domain User entity by username.
	 *
	 * @param username the username to search for
	 * @return the User domain entity
	 * @throws UserNotFoundException if no user exists with the given username
	 */
	public User getDomainUserByUsername(String username) throws UserNotFoundException {
		return userRepo.getByUserName(username)
				.orElseThrow(() -> new UserNotFoundException("No such user with this username"));
	}

	/**
	 * Updates user information with authorization checks.
	 *
	 * <p>
	 * Only admins or the user themselves can update their information. Username
	 * cannot be changed after creation.
	 * </p>
	 *
	 * @param currentUser the currently authenticated user
	 * @param userID      the ID of the user to update
	 * @param newUsername must be null (username changes not allowed)
	 * @param newEmail    the new email (null to keep unchanged)
	 * @param newPassword the new password (null to keep unchanged)
	 * @param newRole     the new role (null to keep unchanged)
	 * @return {@code true} if update succeeded
	 * @throws IllegalAccessException   if user lacks permission to update
	 * @throws UserNotFoundException    if the target user doesn't exist
	 * @throws IllegalArgumentException if attempting to change username
	 */
	public boolean updateUser(UserDTO currentUser, UUID userID, String newUsername, String newEmail, String newPassword,
			Role newRole) throws IllegalAccessException, UserNotFoundException {

		if (currentUser.role() != Role.ADMIN && !currentUser.userID().equals(userID))
			throw new IllegalAccessException("You are not allowed to update this user");

		User user = userRepo.getByID(userID)
				.orElseThrow(() -> new UserNotFoundException("No user exists with ID: " + userID));

		if (newUsername != null) {
			throw new IllegalArgumentException("Username cannot be changed once created.");
		}

		if (newEmail != null)
			user.changeEmail(newEmail);
		if (newPassword != null)
			user.changePassword(newPassword);
		if (newRole != null)
			user.changeRole(newRole);

		return userRepo.update(user);
	}

	/**
	 * Deletes a user by their username.
	 *
	 * @param username the username of the user to delete
	 * @return {@code true} if deletion succeeded
	 * @throws UserNotFoundException if no user exists with the given username
	 */
	public boolean deleteByUsername(String username) throws UserNotFoundException {
		return userRepo.delete(username);
	}

	/**
	 * Checks if a user is eligible to borrow items.
	 *
	 * @param userID the user's unique identifier
	 * @return {@code true} if the user can borrow, {@code false} otherwise
	 * @throws UserNotFoundException if the user doesn't exist
	 */
	public boolean canBorrow(UUID userID) throws UserNotFoundException {
		User user = userRepo.getByID(userID)
				.orElseThrow(() -> new UserNotFoundException("user with id:" + userID + " is not found"));
		return user.canBorrow();
	}

	/**
	 * Registers a new user in the system.
	 *
	 * @param username    the username (must be unique)
	 * @param rawPassword the plain text password (will be hashed)
	 * @param firstName   the user's first name
	 * @param lastName    the user's last name
	 * @param email       the user's email address
	 * @param role        the user's role
	 * @return the newly created user as a DTO
	 * @throws IllegalArgumentException if username already exists
	 * @throws IllegalStateException    if registration fails
	 */
	public UserDTO registerUser(String username, String rawPassword, String firstName, String lastName, String email,
			Role role) {

		if (userRepo.isExist(username)) {
			throw new IllegalArgumentException("Username already exists");
		}

		String hashedPassword = PasswordUtils.hashPassword(rawPassword);
		User newUser = new User(firstName, lastName, email, username, hashedPassword, role);

		boolean success = userRepo.add(newUser);

		if (!success) {
			throw new IllegalStateException("Failed to register user");
		}

		return newUser.toDTO();
	}

	public void validateEmailFormat(String email) {
		String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
		if (!email.matches(emailRegex)) {
			throw new IllegalArgumentException("Invalid email format");
		}
	}

	public void validatePasswordStrength(String password) {
		if (password.length() < 8) {
			throw new IllegalArgumentException("Password must be at least 8 characters long");
		}
	}
}