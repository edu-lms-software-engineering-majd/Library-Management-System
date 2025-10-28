package lms.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lms.domain.exception.UserNotFoundException;

/**
 * Repository interface for managing {@link User} entities.
 * 
 * <p>
 * This interface defines the core operations for accessing and managing users,
 * such as checking existence, retrieving users, and performing CRUD operations.
 * Implementations may vary (in-memory, JDBC, JPA, etc.).
 * </p>
 * 
 * <p>
 * Implementations should handle the persistence mechanism internally and
 * provide consistent behavior for the defined methods.
 * </p>
 * 
 * @author Majd Awwad
 * @version 1.1
 */
public interface UserRepository {

	/**
	 * Checks whether a user with the given username exists.
	 * 
	 * @param userName the username to check
	 * @return {@code true} if a user with the username exists, {@code false}
	 *         otherwise
	 */
	
	boolean isExist(String userName);

	/**
	 * Retrieves a user by their username.
	 * 
	 * @param userName the username of the user
	 * @return An {@code Optional} containing the {@link User} object if found, or
	 *         an empty {@code Optional} if no user with the given ID exists.
	 */
	Optional<User> getByUserName(String userName);

	/**
	 * Adds a new user to the repository.
	 * 
	 * @param user the {@link User} object to add
	 * @return {@code true} if the user was added successfully, {@code false}
	 *         otherwise
	 */
	boolean add(User user);

	/**
	 * Updates an existing user in the repository.
	 * 
	 * @param user the {@link User} object with updated information
	 * @return {@code true} if the update was successful, {@code false} if the user
	 *         does not exist
	 */
	boolean update(User user);

	/**
	 * Deletes a user from the repository by their username.
	 * 
	 * @param userName the username of the user to delete
	 * @return {@code true} if deletion was successful, {@code false} if the user
	 *         was not found
	 * @throws {@link UserNotFoundException} if the username is not exist.
	 */
	boolean delete(String userName) throws UserNotFoundException;

	/**
	 * Retrieves a user by their unique identifier.
	 * 
	 * @param userID the {@link UUID} of the user
	 * @return An {@code Optional} containing the {@link User} object if found, or
	 *         an empty {@code Optional} if no user with the given ID exists.
	 */
	Optional<User> getByID(UUID userID);

	/**
	 * Returns a list of all users in the repository. Implementations may return an
	 * unmodifiable list.
	 * 
	 * @return list of all {@link User} objects
	 */
	List<User> getAllUsers();
}
