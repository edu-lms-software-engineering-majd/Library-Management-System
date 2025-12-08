package lms.application;

import java.util.UUID;

import lms.domain.Account;
import lms.domain.AccountStatus;
import lms.domain.User;
import lms.domain.UserRepository;


/**
 * Application service for managing user financial accounts in the Library Management System.
 *
 * <p>
 * This service coordinates account-related operations including fine management,
 * account status tracking, and borrowing eligibility checks. It acts as a bridge
 * between the presentation layer and the domain layer's {@link Account} entity.
 * </p>
 *
 * <p>
 * Key responsibilities:
 * </p>
 * <ul>
 * <li>Manage user fines (add, pay, calculate totals)</li>
 * <li>Control account status (suspend, activate)</li>
 * <li>Check borrowing eligibility based on account status</li>
 * <li>Provide aggregated financial statistics</li>
 * </ul>
 *
 * @author Majd Awwad
 * @version 1.0
 */
public class AccountService 
{

	
	private final UserRepository userRepo;

	/**
	 * Constructs an AccountService with the required repository.
	 *
	 * @param userRepo the repository for accessing user data
	 * @throws IllegalArgumentException if userRepo is null
	 */
	public AccountService(UserRepository userRepo)
	{
		if (userRepo == null)
			throw new IllegalArgumentException("UserRepository cannot be null");
		this.userRepo = userRepo;
	}
	

	/**
	 * Retrieves a user by ID or throws an exception if not found.
	 *
	 * @param userId the unique identifier of the user
	 * @return the User entity
	 * @throws IllegalArgumentException if userId is null or user doesn't exist
	 */
	private User getUserOrThrow(UUID userId) 
	{
		if (userId == null)
			throw new IllegalArgumentException("User ID cannot be null");

		return userRepo.getByID(userId)
				.orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " does not exist."));
	}

	/**
	 * Retrieves a user's account.
	 *
	 * @param userId the user's unique identifier
	 * @return the user's Account entity
	 */
	private Account getAccount(UUID userId) {
		return getUserOrThrow(userId).getAccount();
	}

	/**
	 * Gets the current status of a user's account.
	 *
	 * @param userId the user's unique identifier
	 * @return the account status (ACTIVE, SUSPENDED, etc.)
	 */
	public AccountStatus getUserAccountStatus(UUID userId)
	{
		return getAccount(userId).getStatus();
	}

	/**
	 * Gets the total outstanding fines for a user.
	 *
	 * @param userId the user's unique identifier
	 * @return the total fine amount
	 */
	public double getUserBalance(UUID userId) {
		return getAccount(userId).getTotalFines();
	}

	/**
	 * Checks if a user is eligible to borrow items.
	 *
	 * @param userId the user's unique identifier
	 * @return {@code true} if the user can borrow, {@code false} otherwise
	 */
	public boolean canUserBorrow(UUID userId) {
		return getAccount(userId).canBorrowBooks();
	}

	/**
	 * Adds a fine to a user's account.
	 *
	 * @param userId the user's unique identifier
	 * @param amount the fine amount (must be positive)
	 * @param reason the reason for the fine
	 * @throws IllegalArgumentException if amount is not positive
	 */
	public void addFineToUser(UUID userId, double amount, String reason) 
	{
		if (amount <= 0)
			throw new IllegalArgumentException("Fine amount must be positive");

		User user = getUserOrThrow(userId);
		user.getAccount().addFine(amount, reason);
		userRepo.update(user);
	}

	/**
	 * Processes a fine payment for a user.
	 *
	 * @param userId the user's unique identifier
	 * @param amount the payment amount (must be positive)
	 * @throws IllegalArgumentException if amount is not positive
	 */
	public void payUserFine(UUID userId, double amount) 
	{
		if (amount <= 0)
			throw new IllegalArgumentException("Payment amount must be positive");

		User user = getUserOrThrow(userId);
		user.getAccount().payFine(amount);
		userRepo.update(user);
	}

	/**
	 * Suspends a user's account with a specified reason.
	 *
	 * @param userId the user's unique identifier
	 * @param reason the reason for suspension (cannot be empty)
	 * @throws IllegalArgumentException if reason is null or blank
	 */
	public void suspendUserAccount(UUID userId, String reason) 
	{
		if (reason == null || reason.isBlank())
			throw new IllegalArgumentException("Suspension reason cannot be empty");

		User user = getUserOrThrow(userId);
		user.getAccount().suspendAccount(reason);
		userRepo.update(user);
	}

	/**
	 * Activates a previously suspended user account.
	 *
	 * @param userId the user's unique identifier
	 */
	public void activateUserAccount(UUID userId) 
	{
		User user = getUserOrThrow(userId);
		user.getAccount().activateAccount();
		userRepo.update(user);
	}

	/**
	 * Calculates the total outstanding fines for all users in the system.
	 *
	 * @return the sum of all user fines
	 */
	public double calculateTotalFinesForAllUsers() 
	{
		return userRepo.getAllUsers().stream().mapToDouble(u -> u.getAccount().getTotalFines()).sum();
	}

	public int getUsersWithFinesCount() 
	{
		return (int) userRepo.getAllUsers().stream().filter(u -> u.getAccount().getTotalFines() > 0).count();
	}

}
