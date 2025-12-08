package lms.persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import lms.domain.Account;
import lms.domain.AccountRepository;

/**
 * In-memory implementation of {@link AccountRepository}.
 * 
 * <p>Stores accounts using two maps for efficient lookup by account ID or user ID.
 * This implementation is not thread-safe and intended for testing purposes.</p>
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class StaticAccountRepository implements AccountRepository {
	
	private final Map<UUID, Account> accounts = new HashMap<>();
	private final Map<UUID, UUID> userToAccountMap = new HashMap<>();

	/**
	 * Retrieves an account by the associated user ID.
	 * 
	 * @param userId the unique identifier of the user
	 * @return an Optional containing the account if found, otherwise empty
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
	 * @return an Optional containing the account if found, otherwise empty
	 */
	@Override
	public Optional<Account> findById(UUID accountId) {
		return Optional.ofNullable(accounts.get(accountId));
	}

	/**
	 * Saves a new account to the repository.
	 * 
	 * @param account the account to save
	 * @return true if saved successfully, false if account or user ID already exists
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
	 * @param account the account with updated information
	 * @return true if updated successfully, false if account does not exist
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
	 * Deletes an account from the repository.
	 * 
	 * @param accountId the unique identifier of the account to delete
	 * @return true if deleted successfully, false if account not found
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
	 * Retrieves all accounts in the repository.
	 * 
	 * @return a new map containing all accounts
	 */
	public Map<UUID, Account> getAllAccounts() {
		return new HashMap<>(accounts);
	}
}