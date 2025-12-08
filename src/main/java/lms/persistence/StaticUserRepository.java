package lms.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lms.domain.Role;
import lms.domain.User;
import lms.domain.UserRepository;
import lms.domain.utils.PasswordUtils;

/**
 * In-memory implementation of {@link UserRepository} using a static list.
 * 
 * <p>Stores users in a list with support for CRUD operations and search by username or ID.
 * Ensures unique usernames and email addresses. Pre-loaded with default admin and test users.
 * This implementation is not thread-safe and intended for testing purposes.</p>
 * 
 * @author Majd Awwad
 * @version 2.0
 */
public class StaticUserRepository implements UserRepository {

	private final static StaticUserRepository INSTANCE = new StaticUserRepository();
	private static final List<User> users = new ArrayList<>();

	private StaticUserRepository() {
	}

	/**
	 * Returns the singleton instance of the repository.
	 * 
	 * @return the shared StaticUserRepository instance
	 */
	public static StaticUserRepository getInstance() {
		return INSTANCE;
	}
	
	static {
		users.add(new User("Admin", "System", "admin@test.com", "admin", PasswordUtils.hashPassword("admi123"),
				Role.ADMIN));

		users.add(new User("John", "Doe", "user@test.com", "user", PasswordUtils.hashPassword("user123"),
				Role.LIBRARIAN));
		users.add(new User("Majd", "Awwad", "majdawwad@gmail.com", "majd04", PasswordUtils.hashPassword("majd123"),
				Role.ADMIN));
	}

	/**
	 * Validates a user object before persistence operations.
	 *
	 * @param user the user to validate
	 * @throws IllegalArgumentException if validation fails
	 */
	private void validateUser(User user) {
		if (user == null)
			throw new IllegalArgumentException("User cannot be null");

		if (user.getUserID() == null)
			throw new IllegalArgumentException("User ID cannot be null");

		if (user.getUsername() == null || user.getUsername().isBlank())
			throw new IllegalArgumentException("Username cannot be empty");

		if (user.getEmail() == null || user.getEmail().isBlank())
			throw new IllegalArgumentException("Email cannot be empty");
	}

	/**
	 * Checks if an email already exists in the repository.
	 *
	 * @param email the email to check
	 * @return true if email exists, false otherwise
	 */
	private boolean emailExists(String email) {
		return users.stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
	}

	/**
	 * Checks if a username already exists in the repository.
	 *
	 * @param username the username to check
	 * @return true if username exists, false otherwise
	 */
	private boolean usernameExists(String username) {
		return users.stream().anyMatch(u -> u.getUsername().equalsIgnoreCase(username));
	}

	/**
	 * Adds a new user to the repository.
	 *
	 * @param user the user to add
	 * @return true if added successfully
	 * @throws IllegalArgumentException if user is invalid, username or email already exists
	 */
	@Override
	public boolean add(User user) {
		validateUser(user);

		if (usernameExists(user.getUsername()))
			throw new IllegalArgumentException("Username already exists: " + user.getUsername());

		if (emailExists(user.getEmail()))
			throw new IllegalArgumentException("Email already exists: " + user.getEmail());

		return users.add(user);
	}

	/**
	 * Updates an existing user in the repository.
	 *
	 * @param updatedUser the user with updated information
	 * @return true if updated successfully, false if user not found
	 * @throws IllegalArgumentException if user is invalid or username/email conflicts with another user
	 */
	@Override
	public boolean update(User updatedUser) {
		validateUser(updatedUser);

		for (int i = 0; i < users.size(); i++) {
			if (users.get(i).getUserID().equals(updatedUser.getUserID())) {

				if (!users.get(i).getUsername().equalsIgnoreCase(updatedUser.getUsername())
						&& usernameExists(updatedUser.getUsername())) {
					throw new IllegalArgumentException("Updated username already exists.");
				}

				if (!users.get(i).getEmail().equalsIgnoreCase(updatedUser.getEmail())
						&& emailExists(updatedUser.getEmail())) {
					throw new IllegalArgumentException("Updated email already exists.");
				}

				users.set(i, updatedUser);
				return true;
			}
		}

		return false;
	}

	/**
	 * Deletes a user from the repository by username.
	 *
	 * @param username the username of the user to delete
	 * @return true if deleted successfully, false if user not found
	 * @throws IllegalArgumentException if username is null or empty
	 */
	@Override
	public boolean delete(String username) {
		if (username == null || username.isBlank())
			throw new IllegalArgumentException("Username cannot be empty");

		return users.removeIf(u -> u.getUsername().equalsIgnoreCase(username));
	}

	/**
	 * Retrieves a user by username.
	 *
	 * @param username the username to search for
	 * @return an Optional containing the user if found, otherwise empty
	 */
	@Override
	public Optional<User> getByUserName(String username) {
		if (username == null)
			return Optional.empty();

		return users.stream().filter(u -> u.getUsername().equalsIgnoreCase(username)).findFirst();
	}

	/**
	 * Retrieves a user by their unique identifier.
	 *
	 * @param userID the user ID
	 * @return an Optional containing the user if found, otherwise empty
	 */
	@Override
	public Optional<User> getByID(UUID userID) {
		if (userID == null)
			return Optional.empty();

		return users.stream().filter(u -> u.getUserID().equals(userID)).findFirst();
	}

	/**
	 * Checks if a user exists with the given username.
	 *
	 * @param username the username to check
	 * @return true if user exists, false otherwise
	 */
	@Override
	public boolean isExist(String username) {
		return usernameExists(username);
	}

	/**
	 * Retrieves all users in the repository.
	 *
	 * @return an immutable list of all users
	 */
	@Override
	public List<User> getAllUsers() {
		return Collections.unmodifiableList(users);
	}

}
