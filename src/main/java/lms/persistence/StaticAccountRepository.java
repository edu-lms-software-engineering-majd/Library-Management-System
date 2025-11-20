package lms.persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import lms.domain.Account;
import lms.domain.AccountRepository;

/**
 * In-memory implementation of {@link AccountRepository} for testing and simple usage.
 * 
 * <p>
 * This repository stores accounts in static maps and provides basic CRUD
 * operations: create, read, update, delete. It maintains two maps:
 * <ul>
 * <li>One for quick account lookup by account ID</li>
 * <li>One for mapping user IDs to account IDs</li>
 * </ul>
 * </p>
 * 
 * <p>
 * <b>Note:</b> This is not thread-safe and intended for demo or testing purposes only.
 * For production use, consider a database-backed implementation.
 * </p>
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class StaticAccountRepository implements AccountRepository {
	
	/** Internal map storing accounts by account ID */
	private final Map<UUID, Account> accounts = new HashMap<>();
	
	/** Internal map for quick lookup: maps user ID to account ID */
	private final Map<UUID, UUID> userToAccountMap = new HashMap<>();

	/**
	 * Retrieves an account by the user ID associated with it.
	 * 
	 * <p>
	 * This method first looks up the account ID using the user ID,
	 * then retrieves the account from the main storage.
	 * </p>
	 * 
	 * @param userId the unique identifier of the user
	 * @return An {@code Optional} containing the {@link Account} object if found, or
	 *         an empty {@code Optional} if no account exists for the given user ID
	 */
	@Override
	public Optional<Account> findByUserId(UUID userId) {
		UUID accountId = userToAccountMap.get(userId);
		if (accountId != null) {
			return Optional.ofNullable(accounts.get(accountId));
		}
		return Optional.empty();
	}

	/**
	 * Retrieves an account by its unique account identifier.
	 * 
	 * @param accountId the unique identifier of the account
	 * @return An {@code Optional} containing the {@link Account} object if found, or
	 *         an empty {@code Optional} if no account with the given ID exists
	 */
	@Override
	public Optional<Account> findById(UUID accountId) {
		return Optional.ofNullable(accounts.get(accountId));
	}

	/**
	 * Saves a new account to the repository.
	 * 
	 * <p>
	 * This method ensures that no duplicate accounts are created. It checks both:
	 * <ul>
	 * <li>Whether an account with the same account ID already exists</li>
	 * <li>Whether an account for the same user ID already exists</li>
	 * </ul>
	 * If either check fails, the save operation is rejected.
	 * </p>
	 * 
	 * @param account the {@link Account} object to save
	 * @return {@code true} if the account was saved successfully, {@code false}
	 *         if an account with the same ID or user ID already exists
	 */
	@Override
	public boolean save(Account account) {
		if (accounts.containsKey(account.getAccountId()) || userToAccountMap.containsKey(account.getUserId())) {
			return false;
		}
		accounts.put(account.getAccountId(), account);
		userToAccountMap.put(account.getUserId(), account.getAccountId());
		return true;
	}

	/**
	 * Updates an existing account in the repository.
	 * 
	 * <p>
	 * This method replaces the stored account with the provided account object.
	 * The account must already exist in the repository for the update to succeed.
	 * </p>
	 * 
	 * @param account the {@link Account} object with updated information
	 * @return {@code true} if the update was successful, {@code false} if the account
	 *         does not exist
	 */
	@Override
	public boolean update(Account account) {
		if (!accounts.containsKey(account.getAccountId())) {
			return false;
		}
		accounts.put(account.getAccountId(), account);
		return true;
	}

	/**
	 * Deletes an account from the repository by its account ID.
	 * 
	 * <p>
	 * This method removes the account from both internal maps:
	 * <ul>
	 * <li>The main accounts map (by account ID)</li>
	 * <li>The user-to-account mapping (by user ID)</li>
	 * </ul>
	 * </p>
	 * 
	 * @param accountId the unique identifier of the account to delete
	 * @return {@code true} if deletion was successful, {@code false} if the account
	 *         was not found
	 */
	@Override
	public boolean delete(UUID accountId) {
		Account account = accounts.get(accountId);
		if (account != null) {
			userToAccountMap.remove(account.getUserId());
			accounts.remove(accountId);
			return true;
		}
		return false;
	}

	/**
	 * Retrieves all accounts stored in the repository.
	 * 
	 * <p>
	 * Returns a copy of the internal accounts map to prevent external
	 * modification of the repository's internal state.
	 * </p>
	 * 
	 * @return A new {@code Map} containing all accounts, keyed by account ID
	 */
	public Map<UUID, Account> getAllAccounts() {
		return new HashMap<>(accounts);
	}
}