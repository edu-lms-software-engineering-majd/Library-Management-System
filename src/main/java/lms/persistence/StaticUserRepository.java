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

public class StaticUserRepository implements UserRepository {

	private final static StaticUserRepository INSTANCE = new StaticUserRepository();

	/** Internal in-memory storage */
	private static final List<User> users = new ArrayList<>();

	private StaticUserRepository() {
	}

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

	// ======================================
	// Validation Helpers
	// ======================================
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

	private boolean emailExists(String email) {
		return users.stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
	}

	private boolean usernameExists(String username) {
		return users.stream().anyMatch(u -> u.getUsername().equalsIgnoreCase(username));
	}

	// ======================================
	// CRUD Operations
	// ======================================

	@Override
	public boolean add(User user) {
		validateUser(user);

		if (usernameExists(user.getUsername()))
			throw new IllegalArgumentException("Username already exists: " + user.getUsername());

		if (emailExists(user.getEmail()))
			throw new IllegalArgumentException("Email already exists: " + user.getEmail());

		return users.add(user);
	}

	@Override
	public boolean update(User updatedUser) {
		validateUser(updatedUser);

		for (int i = 0; i < users.size(); i++) {
			if (users.get(i).getUserID().equals(updatedUser.getUserID())) {

				// Check username duplication (different user)
				if (!users.get(i).getUsername().equalsIgnoreCase(updatedUser.getUsername())
						&& usernameExists(updatedUser.getUsername())) {
					throw new IllegalArgumentException("Updated username already exists.");
				}

				// Check email duplication (different user)
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

	@Override
	public boolean delete(String username) {
		if (username == null || username.isBlank())
			throw new IllegalArgumentException("Username cannot be empty");

		return users.removeIf(u -> u.getUsername().equalsIgnoreCase(username));
	}

	// ======================================
	// Retrieval Methods
	// ======================================

	@Override
	public Optional<User> getByUserName(String username) {
		if (username == null)
			return Optional.empty();

		return users.stream().filter(u -> u.getUsername().equalsIgnoreCase(username)).findFirst();
	}

	@Override
	public Optional<User> getByID(UUID userID) {
		if (userID == null)
			return Optional.empty();

		return users.stream().filter(u -> u.getUserID().equals(userID)).findFirst();
	}

	@Override
	public boolean isExist(String username) {
		return usernameExists(username);
	}

	@Override
	public List<User> getAllUsers() {
		return Collections.unmodifiableList(users);
	}

}
