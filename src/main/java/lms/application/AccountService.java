package lms.application;

import java.util.UUID;

import lms.domain.Account;
import lms.domain.AccountStatus;
import lms.domain.User;
import lms.domain.UserRepository;

/**
 * Service class for managing user accounts, fines, and borrowing permissions.
 * Author: Ahmad Salameh
 */
public class AccountService {

	private final UserRepository userRepo;

	public AccountService(UserRepository userRepo) {
		if (userRepo == null)
			throw new IllegalArgumentException("UserRepository cannot be null");
		this.userRepo = userRepo;
	}

	public Account getOrCreateAccount(UUID userId) {
		if (userId == null)
			throw new IllegalArgumentException("User ID cannot be null");

		User user = userRepo.getByID(userId)
				.orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " does not exist."));
		return user.getAccount();
	}

	public void addFineToUser(UUID userId, double amount, String reason) {
		Account account = getOrCreateAccount(userId);
		account.addFine(amount, reason);

		User user = userRepo.getByID(userId)
				.orElseThrow(() -> new IllegalStateException("User not found after account creation"));
		userRepo.update(user);
	}

	public void payUserFine(UUID userId, double amount) {
		Account account = getOrCreateAccount(userId);
		account.payFine(amount);

		User user = userRepo.getByID(userId)
				.orElseThrow(() -> new IllegalStateException("User not found after account access"));
		userRepo.update(user);
	}

	public AccountStatus getUserAccountStatus(UUID userId) {
		return getOrCreateAccount(userId).getStatus();
	}

	public boolean canUserBorrow(UUID userId) {
		return getOrCreateAccount(userId).canBorrow();
	}

	public double getUserBalance(UUID userId) {
		return getOrCreateAccount(userId).getBalance();
	}

	public void suspendUserAccount(UUID userId, String reason) {
		if (reason == null || reason.trim().isEmpty())
			throw new IllegalArgumentException("Suspension reason cannot be null or empty");

		Account account = getOrCreateAccount(userId);
		account.suspendAccount(reason);

		User user = userRepo.getByID(userId).orElseThrow(() -> new IllegalStateException("User not found"));
		userRepo.update(user);
	}

	public void activateUserAccount(UUID userId) {
		Account account = getOrCreateAccount(userId);
		account.activateAccount();

		User user = userRepo.getByID(userId).orElseThrow(() -> new IllegalStateException("User not found"));
		userRepo.update(user);
	}

	public double calculateTotalFinesForAllUsers() {
		return userRepo.getAllUsers().stream().mapToDouble(user -> user.getAccount().getTotalFines()).sum();
	}

	public int getUsersWithFinesCount() {
		return (int) userRepo.getAllUsers().stream().filter(user -> user.getAccount().getTotalFines() > 0).count();
	}
}
