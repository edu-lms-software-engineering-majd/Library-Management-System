package lms.persistence;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import lms.domain.Account;

public class StaticAccountRepo implements AccountRepos {

	// Maps are final but mutable → safe for in-memory storage
	private final Map<UUID, Account> accounts = new HashMap<>();
	private final Map<UUID, UUID> userToAccountMap = new HashMap<>();

	@Override
	public Optional<Account> findByUserId(UUID userId) {
		if (userId == null)
			return Optional.empty();
		UUID accountId = userToAccountMap.get(userId);
		return Optional.ofNullable(accounts.get(accountId));
	}

	@Override
	public Optional<Account> findById(UUID accountId) {
		if (accountId == null)
			return Optional.empty();
		return Optional.ofNullable(accounts.get(accountId));
	}

	@Override
	public boolean save(Account account) {
		if (account == null)
			return false;

		UUID accountId = account.getAccountId();
		UUID userId = account.getUserId();

		// Avoid duplicates (same user OR same account ID)
		if (accounts.containsKey(accountId) || userToAccountMap.containsKey(userId)) {
			return false;
		}

		accounts.put(accountId, account);
		userToAccountMap.put(userId, accountId);
		return true;
	}

	@Override
	public boolean update(Account account) {
		if (account == null)
			return false;

		UUID accountId = account.getAccountId();
		if (!accounts.containsKey(accountId)) {
			return false;
		}

		accounts.put(accountId, account);
		return true;
	}

	@Override
	public boolean delete(UUID accountId) {
		if (accountId == null)
			return false;

		Account removed = accounts.remove(accountId);
		if (removed != null) {
			userToAccountMap.remove(removed.getUserId());
			return true;
		}
		return false;
	}

	/**
	 * Returns an unmodifiable snapshot of the account storage. Prevents external
	 * modification.
	 */
	public Map<UUID, Account> getAllAccounts() {
		return Collections.unmodifiableMap(new HashMap<>(accounts));
	}
}
