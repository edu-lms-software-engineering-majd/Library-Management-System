package lms.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import lms.domain.Role;
import lms.domain.User;
import lms.domain.UserRepository;
import lms.domain.exception.UserNotFoundException;

/**
 * Application service for managing users in the Library Management System.
 *
 * <p>
 * This service acts as an intermediary between the domain layer and the
 * presentation layer, providing methods to retrieve, update, and manage user
 * information while enforcing business rules and access control.
 * </p>
 *
 * <p>
 * Responsibilities of this class include:
 * </p>
 * <ul>
 * <li>Retrieving all users or specific users by username.</li>
 * <li>Updating user information with proper authorization checks.</li>
 * <li>Returning data in the form of {@link UserDTO} to avoid exposing domain
 * entities outside the domain layer.</li>
 * </ul>
 * 
 * <p>
 * Example usage:
 * </p>
 * 
 * <pre>
 * UserService userService = new UserService(userRepo);
 * List&lt;UserDTO&gt; users = userService.getAllUsers();
 * UserDTO user = userService.getUserByUsername("john_doe");
 * userService.updateUser(currentUser, userId, "newName", "newEmail@example.com", "newPass123", Role.MEMBER);
 * </pre>
 * 
 * @author Majd Awwad
 * @version 1.0
 */

public class UserService {

	/** Repository used for accessing and managing users */
	private final UserRepository userRepo;

	/**
	 * Constructs a {@code UserService} with the given {@link UserRepository}.
	 *
	 * @param userRepo the repository used for persisting and retrieving users
	 */

	public UserService(UserRepository userRepo) {
		this.userRepo = userRepo;
	}

	/**
	 * Retrieves all users in the system as a list of {@link UserDTO}.
	 *
	 * <p>
	 * The returned list is unmodifiable to prevent external modification of the
	 * internal data.
	 * </p>
	 *
	 * @return an unmodifiable list of all users as {@link UserDTO} objects
	 */

	public List<UserDTO> getAllUsers() {
		List<User> actualUsers = userRepo.getAllUsers();
		List<UserDTO> users = new ArrayList<>();
		users = actualUsers.stream().map(User::toDTO).collect(Collectors.toList());
		return Collections.unmodifiableList(users);
	}

	/**
	 * Retrieves a user by their username.
	 *
	 * @param username the username of the user to retrieve
	 * @return the corresponding {@link UserDTO} object
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
	 * Updates an existing user's information.
	 *
	 * <p>
	 * Only the user themselves or an admin with the correct ID can perform the
	 * update. Fields that are {@code null} are ignored.
	 * </p>
	 *
	 * @param currentUser the currently authenticated user performing the update
	 * @param userID      the ID of the user to update
	 * @param newUsername new username (or {@code null} to keep unchanged)
	 * @param newEmail    new email (or {@code null} to keep unchanged)
	 * @param newPassword new password (or {@code null} to keep unchanged)
	 * @param newRole     new role (or {@code null} to keep unchanged)
	 * @return {@code true} if the update was successful, {@code false} otherwise
	 * @throws IllegalAccessException if the current user is not authorized to
	 *                                update the specified user
	 * @throws UserNotFoundException  if no user exists with the specified ID
	 */

	public boolean updateUser(UserDTO currentUser, UUID userID, String newUsername, String newEmail, String newPassword,
			Role newRole) throws IllegalAccessException, UserNotFoundException {

		if (currentUser.role() != Role.ADMIN || currentUser.userID() != userID)
			throw new IllegalAccessException("You are not allowed to update this user");

		Optional<User> userOptional = userRepo.getByID(userID);
		if (userOptional.isEmpty()) {
			throw new UserNotFoundException("No such user with this username");
		}

		User user = userOptional.get();

		if (newUsername != null)
			user.setUsername(newUsername);
		if (newEmail != null)
			user.changeEmail(newEmail);
		if (newPassword != null)
			user.changePassword(newPassword);
		if (newRole != null)
			user.changeRole(newRole);

		return userRepo.update(user);
	}

	public boolean deleteUserByUsername(String username) throws UserNotFoundException {
		return userRepo.delete(username);

	}

	public boolean canBorrow(UUID userID) throws UserNotFoundException {

		User user = userRepo.getByID(userID)
				.orElseThrow(() -> new UserNotFoundException("user with id:" + userID + " is not found"));

		return user.canBorrow();
	}

}
