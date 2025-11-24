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
 * @refactoredBy Ahmad Salameh
 * @version 1.0
 */

public class UserService {

	private final UserRepository userRepo;

	public UserService(UserRepository userRepo) {
		this.userRepo = userRepo;
	}

	public List<UserDTO> getAllUsers() {
		List<User> actualUsers = userRepo.getAllUsers();
		List<UserDTO> users = actualUsers.stream().map(User::toDTO).collect(Collectors.toList());
		return Collections.unmodifiableList(users);
	}

	public UserDTO getUserByUsername(String username) throws UserNotFoundException {

		Optional<User> userOptional = userRepo.getByUserName(username);

		if (userOptional.isEmpty()) {
			throw new UserNotFoundException("No such user with this username");
		}

		return userOptional.get().toDTO();
	}

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

	public boolean deleteByUsername(String username) throws UserNotFoundException {
		return userRepo.delete(username);
	}

	public boolean canBorrow(UUID userID) throws UserNotFoundException {
		User user = userRepo.getByID(userID)
				.orElseThrow(() -> new UserNotFoundException("user with id:" + userID + " is not found"));
		return user.canBorrow();
	}

}
