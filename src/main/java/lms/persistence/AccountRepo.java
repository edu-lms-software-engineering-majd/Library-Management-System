package lms.persistence;

import java.util.Optional;
import java.util.UUID;

import lms.domain.Account;

public interface AccountRepo {
	Optional<Account> findByUserId(UUID userId);

	Optional<Account> findById(UUID accountId);

	boolean save(Account account);

	boolean update(Account account);

	boolean delete(UUID accountId);
}