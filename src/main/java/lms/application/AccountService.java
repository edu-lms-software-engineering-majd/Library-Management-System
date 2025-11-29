package lms.application;

import java.util.UUID;

import lms.domain.Account;
import lms.domain.AccountStatus;
import lms.domain.User;
import lms.domain.UserRepository;


/**
 * Application-level logic for managing user financial accounts. Clean,
 * validated, and aligned with the LMS architecture.
 */
public class AccountService {

	
	private final UserRepository userRepo;

	public AccountService(UserRepository userRepo) {
		if (userRepo == null)
			throw new IllegalArgumentException("UserRepository cannot be null");
		this.userRepo = userRepo;
	}

	/** Fetches a user by ID or throws a clean exception */
	private User getUserOrThrow(UUID userId) {
		if (userId == null)
			throw new IllegalArgumentException("User ID cannot be null");

		return userRepo.getByID(userId)
				.orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " does not exist."));
	}

	/** Returns the user's account safely */
	private Account getAccount(UUID userId) {
		return getUserOrThrow(userId).getAccount();
	}

	public AccountStatus getUserAccountStatus(UUID userId) {
		return getAccount(userId).getStatus();
	}

	public double getUserBalance(UUID userId) {
		return getAccount(userId).getTotalFines();
	}

	public boolean canUserBorrow(UUID userId) {
		return getAccount(userId).canBorrowBooks();
	}

	public void addFineToUser(UUID userId, double amount, String reason) {
		if (amount <= 0)
			throw new IllegalArgumentException("Fine amount must be positive");

		User user = getUserOrThrow(userId);
		user.getAccount().addFine(amount, reason);
		userRepo.update(user);
	}

	public void payUserFine(UUID userId, double amount) {
		if (amount <= 0)
			throw new IllegalArgumentException("Payment amount must be positive");

		User user = getUserOrThrow(userId);
		user.getAccount().payFine(amount);
		userRepo.update(user);
	}

	public void suspendUserAccount(UUID userId, String reason) {
		if (reason == null || reason.isBlank())
			throw new IllegalArgumentException("Suspension reason cannot be empty");

		User user = getUserOrThrow(userId);
		user.getAccount().suspendAccount(reason);
		userRepo.update(user);
	}

	public void activateUserAccount(UUID userId) {
		User user = getUserOrThrow(userId);
		user.getAccount().activateAccount();
		userRepo.update(user);
	}

	/** Aggregation operations */
	public double calculateTotalFinesForAllUsers() {
		return userRepo.getAllUsers().stream().mapToDouble(u -> u.getAccount().getTotalFines()).sum();
	}

	public int getUsersWithFinesCount() {
		return (int) userRepo.getAllUsers().stream().filter(u -> u.getAccount().getTotalFines() > 0).count();
	}

}
