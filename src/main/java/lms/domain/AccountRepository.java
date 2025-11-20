package lms.domain;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing {@link Account} entities.
 * 
 * <p>
 * This interface defines the core operations for accessing and managing user accounts,
 * including CRUD operations and account lookup by user ID or account ID.
 * Implementations may vary (in-memory, JDBC, JPA, etc.).
 * </p>
 * 
 * <p>
 * Implementations should handle the persistence mechanism internally and
 * provide consistent behavior for the defined methods.
 * </p>
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public interface AccountRepository {

	/**
	 * Retrieves an account by the user ID associated with it.
	 * 
	 * @param userId the unique identifier of the user
	 * @return An {@code Optional} containing the {@link Account} object if found, or
	 *         an empty {@code Optional} if no account exists for the given user ID.
	 */
	Optional<Account> findByUserId(UUID userId);

	/**
	 * Retrieves an account by its unique account identifier.
	 * 
	 * @param accountId the unique identifier of the account
	 * @return An {@code Optional} containing the {@link Account} object if found, or
	 *         an empty {@code Optional} if no account with the given ID exists.
	 */
	Optional<Account> findById(UUID accountId);

	/**
	 * Saves a new account to the repository.
	 * 
	 * @param account the {@link Account} object to save
	 * @return {@code true} if the account was saved successfully, {@code false}
	 *         if an account with the same ID or user ID already exists
	 */
	boolean save(Account account);

	/**
	 * Updates an existing account in the repository.
	 * 
	 * @param account the {@link Account} object with updated information
	 * @return {@code true} if the update was successful, {@code false} if the account
	 *         does not exist
	 */
	boolean update(Account account);

	/**
	 * Deletes an account from the repository by its account ID.
	 * 
	 * @param accountId the unique identifier of the account to delete
	 * @return {@code true} if deletion was successful, {@code false} if the account
	 *         was not found
	 */
	boolean delete(UUID accountId);
}
