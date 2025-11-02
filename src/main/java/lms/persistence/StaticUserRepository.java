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
<<<<<<< HEAD
 * In-memory implementation of {@link UserRepository} for testing and simple usage.
||||||| 7160386
 * In-memory implementation of {@link UserRepo} for testing and simple usage.
=======
 * In-memory implementation of {@link UserRepository} for testing and simple
 * usage.
>>>>>>> ahmad-salameh
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
<<<<<<< HEAD
public class StaticUserRepository implements UserRepository {

	private static StaticUserRepository instance = null;
	
||||||| 7160386
public class StaticUserRepo implements UserRepo
{
	
=======

public class StaticUserRepository implements UserRepository {

	private static StaticUserRepository instance = null;

>>>>>>> ahmad-salameh
	/** Internal list storing all users */
	private static final List<User> users = new ArrayList<>();

	// 🔹 Initialize with demo users
<<<<<<< HEAD
	static {
		users.add(new User("Admin", "System", "admin@test.com", "admin", PasswordUtils.hashPassword("admi123"),
||||||| 7160386
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
=======
	static {
		users.add(new User("Majd", "Awwad", "majdawwad@gmail.com", "majd04", PasswordUtils.hashPassword("majd123"),
>>>>>>> ahmad-salameh
				Role.ADMIN));
<<<<<<< HEAD

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
||||||| 7160386
=======

		users.add(new User("Ahmad", "Salameh", "ahmadsalameh@gmail.com", "ahmad04",
				PasswordUtils.hashPassword("ahmad123"), Role.LIBRARIAN));

	}

	public static StaticUserRepository getInstance() {
		if (instance == null) {
			instance = new StaticUserRepository();
		}
		return instance;
>>>>>>> ahmad-salameh
	}

	/**
	 * Checks whether a user with the given username exists.
	 * 
	 * @param userName the username to check
	 * @return {@code true} if a user exists, {@code false} otherwise
	 */
	@Override
	public boolean isExist(String userName) {
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
<<<<<<< HEAD
	public Optional<User> getByUserName(String userName) {
		Optional<User> user = users.stream().filter(u -> u.getUsername().equalsIgnoreCase(userName)).findFirst();
||||||| 7160386
	public Optional<User> getUserByUserName(String userName)
	{
		Optional<User> user = users.stream()
				.filter(u -> u.getUsername().equalsIgnoreCase(userName))
				.findFirst();
=======

	public Optional<User> getByUserName(String userName) {

		Optional<User> user = users.stream().filter(u -> u.getUsername().equalsIgnoreCase(userName)).findFirst();
>>>>>>> ahmad-salameh
		return user;
	}

	/**
	 * Adds a new user to the repository.
	 * 
	 * @param user the {@link User} object to add
	 * @return {@code true} if the user was added, {@code false} if username already
	 *         exists
	 */
	@Override
<<<<<<< HEAD
	public boolean add(User user) {
		if (isExist(user.getUsername())) {
||||||| 7160386
	public boolean addUser(User user) 
	{
		if (isExist(user.getUsername())) 
		{
=======

	public boolean add(User user) {

		if (isExist(user.getUsername())) {
>>>>>>> ahmad-salameh
			return false;
		}
		return users.add(user);
	}

	/**
	 * Updates an existing user in the repository.
	 * 
	 * @param updatedUser the {@link User} object with updated data
	 * @return {@code true} if update was successful, {@code false} if user does not
	 *         exist
	 */
	@Override
<<<<<<< HEAD
	public boolean update(User updatedUser) {

		for (int i = 0; i < users.size(); i++) {

			if (users.get(i).getUserID().equals(updatedUser.getUserID())) {

||||||| 7160386
	public boolean updateUser(User updatedUser) 
	{
		 
		for (int i = 0; i < users.size(); i++)
		{
			
			
			if (users.get(i).getUserID().equals(updatedUser.getUserID())) 
			{
				
=======

	public boolean update(User updatedUser) {

		for (int i = 0; i < users.size(); i++) {

			if (users.get(i).getUserID().equals(updatedUser.getUserID())) {

>>>>>>> ahmad-salameh
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
	 * @return {@code true} if deletion was successful, {@code false} if user not
	 *         found
	 */
	@Override
<<<<<<< HEAD
	public boolean delete(String userName) {

||||||| 7160386
	public boolean deleteUser(String userName) {
		
=======

	public boolean delete(String userName) {

>>>>>>> ahmad-salameh
		return users.removeIf(u -> u.getUsername().equalsIgnoreCase(userName));
	}

	/**
	 * Returns an unmodifiable list of all users in the repository.
	 * 
	 * @return list of all {@link User} objects
	 */
	@Override
	public List<User> getAllUsers() {

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
<<<<<<< HEAD
	public Optional<User> getByID(UUID userID) {

||||||| 7160386
	public Optional<User> getUserByID(UUID userID) 
	{
		
=======

	public Optional<User> getByID(UUID userID) {

>>>>>>> ahmad-salameh
		return Optional.of(users.stream().filter(u -> u.getUserID().equals(userID)).findFirst().orElse(null));
	}
}
