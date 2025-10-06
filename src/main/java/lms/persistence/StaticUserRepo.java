package lms.persistence;

import lms.domain.Role;
import lms.domain.User;
import lms.domain.UserRepo;
import lms.domain.utils.PasswordUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * In-memory implementation of {@link UserRepo} for testing and simple usage.
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
 *   <li>Admin → username: <b>admin</b>, password: <b>admi123</b></li>
 *   <li>User  → username: <b>user</b>, password: <b>user123</b></li>
 * </ul>
 * </p>
 * 
 * @author Majd
 * @version 1.1
 */
public class StaticUserRepo implements UserRepo
{
	
	/** Internal list storing all users */
	private static final List<User> users = new ArrayList<>();

	// 🔹 Initialize with demo users
	static
	{
		users.add(new User(
				"Admin", "System",
				"admin@test.com",
		        "admin",
		        PasswordUtils.hashPassword("admi123"), 
		        Role.ADMIN));

		users.add(new User(
				"John", "Doe",
				"user@test.com",
		        "user",
		        PasswordUtils.hashPassword("user123"),
		        Role.LIBRARIAN));

		users.add(new User(
				"Majd", "Awwad",
				"majdawwad@gmail.com",
				"majd04",
				PasswordUtils.hashPassword("majd123"),
				Role.ADMIN));
	}

	/**
	 * Checks whether a user with the given username exists.
	 * 
	 * @param userName the username to check
	 * @return {@code true} if a user exists, {@code false} otherwise
	 */
	@Override
	public boolean isExist(String userName) 
	{
		return users.stream().anyMatch(u -> u.getUsername().equalsIgnoreCase(userName));
	}

	/**
	 * Retrieves a user by their username.
	 * 
	 * @param userName the username of the user
	 * @return An {@code Optional} containing the {@link User} object if found, or
	 *         an empty {@code Optional} if no user with the given ID exists.
	 */
	@Override
	public Optional<User> getUserByUserName(String userName)
	{
		Optional<User> user = users.stream()
				.filter(u -> u.getUsername().equalsIgnoreCase(userName))
				.findFirst();
		return user;
	}
	
	/**
	 * Adds a new user to the repository.
	 * 
	 * @param user the {@link User} object to add
	 * @return {@code true} if the user was added, {@code false} if username already exists
	 */
	@Override
	public boolean addUser(User user) 
	{
		if (isExist(user.getUsername())) 
		{
			return false;
		}
		return users.add(user);
	}

	/**
	 * Updates an existing user in the repository.
	 * 
	 * @param updatedUser the {@link User} object with updated data
	 * @return {@code true} if update was successful, {@code false} if user does not exist
	 */
	@Override
	public boolean updateUser(User updatedUser) 
	{
		 
		for (int i = 0; i < users.size(); i++)
		{
			
			
			if (users.get(i).getUserID().equals(updatedUser.getUserID())) 
			{
				
				users.set(i, updatedUser);
				return true;
			}
		}
		return false;
	}

	/**
	 * Deletes a user from the repository by their username.
	 * 
	 * @param userName the username of the user to delete
	 * @return {@code true} if deletion was successful, {@code false} if user not found
	 */
	@Override
	public boolean deleteUser(String userName) {
		
		return users.removeIf(u -> u.getUsername().equalsIgnoreCase(userName));
	}

	/**
	 * Returns an unmodifiable list of all users in the repository.
	 * 
	 * @return list of all {@link User} objects
	 */
	@Override
	public List<User> getAllUsers()
	{
		
		return Collections.unmodifiableList(users);
	}

	/**
	 * Finds a user by their unique ID.
	 * 
	 * @param userID the {@link UUID} of the user
	 * @return An {@code Optional} containing the {@link User} object if found, or
	 *         an empty {@code Optional} if no user with the given ID exists.
	 */
	@Override
	public Optional<User> getUserByID(UUID userID) 
	{
		
		return Optional.of(users.stream().filter(u -> u.getUserID().equals(userID)).findFirst().orElse(null));
	}
}
