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
 * In-memory implementation of {@link UserRepository} for testing and simple
 * usage.
 * 
 * <p>
 * This repository stores users in a static list and provides basic CRUD
 * operations: create, read, update, delete. It also provides some utility
 * methods for checking existence and listing all users.
 * </p>
 * 
 * <p>
 * Note: This is not thread-safe and intended for demo or testing purposes only.
 * </p>
 * 
 * <p>
 * Added default demo users:
 * <ul>
 * <li>Admin → username: <b>admin</b>, password: <b>admi123</b></li>
 * <li>User → username: <b>user</b>, password: <b>user123</b></li>
 * </ul>
 * </p>
 * 
 * @author Majd
 * @version 1.1
 */
public class StaticUserRepository implements UserRepository {

	private static StaticUserRepository instance = null;
	
	/** Internal list storing all users */
	private static final List<User> users = new ArrayList<>();

	// 🔹 Initialize with demo users
	static {
		users.add(new User("Admin", "System", "admin@test.com", "admin", PasswordUtils.hashPassword("admi123"),
				Role.ADMIN));

		users.add(new User("John", "Doe", "user@test.com", "user", PasswordUtils.hashPassword("user123"),
				Role.LIBRARIAN));
		users.add(new User("Majd", "Awwad", "majdawwad@gmail.com", "majd04", PasswordUtils.hashPassword("majd123"),
				Role.ADMIN));
	}
	
	public static StaticUserRepository getInstance() {
		if (instance == null) {
			instance = new StaticUserRepository();
		}
		return instance;
	}

	@Override
	public boolean isExist(String userName) {
		return users.stream().anyMatch(u -> u.getUsername().equalsIgnoreCase(userName));
	}

	@Override
	public Optional<User> getByUserName(String userName) {
		Optional<User> user = users.stream().filter(u -> u.getUsername().equalsIgnoreCase(userName)).findFirst();
		return user;
	}

	@Override
	public boolean add(User user) {
		if (isExist(user.getUsername())) {
			return false;
		}
		return users.add(user);
	}

	@Override
	public boolean update(User updatedUser) {

		for (int i = 0; i < users.size(); i++) {
			if (users.get(i).getUserID().equals(updatedUser.getUserID())) {

				users.set(i, updatedUser);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean delete(String userName) {

		return users.removeIf(u -> u.getUsername().equalsIgnoreCase(userName));
	}

	@Override
	public List<User> getAllUsers() {
		return Collections.unmodifiableList(users);
	}

	@Override
	public Optional<User> getByID(UUID userID) {

		return Optional.of(users.stream().filter(u -> u.getUserID().equals(userID)).findFirst().orElse(null));
	}
}
