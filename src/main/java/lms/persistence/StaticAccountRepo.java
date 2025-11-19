package lms.persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lms.domain.Account;

public class StaticAccountRepo implements AccountRepos {
	private final Map<UUID, Account> accounts = new HashMap<>();
	private final Map<UUID, UUID> userToAccountMap = new HashMap<>();

	@Override
	public Optional<Account> findByUserId(UUID userId) {
		UUID accountId = userToAccountMap.get(userId);
		if (accountId != null) {
			return Optional.ofNullable(accounts.get(accountId));
		}
		return Optional.empty();
	}

	@Override
	public Optional<Account> findById(UUID accountId) {
		return Optional.ofNullable(accounts.get(accountId));
	}

	@Override
	public boolean save(Account account) {
		if (accounts.containsKey(account.getAccountId()) || userToAccountMap.containsKey(account.getUserId())) {
			return false;
		}
		accounts.put(account.getAccountId(), account);
		userToAccountMap.put(account.getUserId(), account.getAccountId());
		return true;
	}

	@Override
	public boolean update(Account account) {
		if (!accounts.containsKey(account.getAccountId())) {
			return false;
		}
		accounts.put(account.getAccountId(), account);
		return true;
	}

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

	public Map<UUID, Account> getAllAccounts() {
		return new HashMap<>(accounts);
	}
}