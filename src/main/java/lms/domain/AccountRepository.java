package lms.domain;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing Account entities.
 * 
 * <p>Defines CRUD operations for user accounts. Implementations handle
 * the persistence mechanism.</p>
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public interface AccountRepository {

	/**
	 * Finds an account by user ID.
	 * 
	 * @param userId the user ID
	 * @return Optional containing the account if found
	 */
	Optional<Account> findByUserId(UUID userId);

	/**
	 * Finds an account by account ID.
	 * 
	 * @param accountId the account ID
	 * @return Optional containing the account if found
	 */
	Optional<Account> findById(UUID accountId);

	/**
	 * Saves a new account.
	 * 
	 * @param account the account to save
	 * @return true if saved successfully, false if already exists
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
