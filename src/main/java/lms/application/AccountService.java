package lms.application;

import java.util.UUID;

import lms.domain.Account;
import lms.domain.AccountStatus;
import lms.domain.User;
import lms.domain.UserRepository;

/**
 * Service class responsible for managing user accounts.
 */
public class AccountService {

	private final UserRepository userRepo;
	
    public AccountService(UserRepository userRepo) {
		this.userRepo = userRepo;
    	
    }

    /**
     * Gets the user's account if it exists, or creates a new one if it does not.
     *
     * @param userId the user ID
     * @return the existing or newly created account
     */
    public Account getOrCreateAccount(UUID userId) {
    	
    	User user = userRepo.getByID(userId).orElseThrow(() -> 
			new IllegalArgumentException("User with ID " + userId + " does not exist."));

    	return user.getAccount();
		
    }

    /**
     * Adds a fine to the user's account.
     *
     * @param userId the user ID
     * @param amount the fine amount (must be positive)
     * @param reason the reason for the fine
     * @throws IllegalArgumentException if amount is not positive
     */
    public void addFineToUser(UUID userId, double amount, String reason) {
        
    	if (amount <= 0) {
            throw new IllegalArgumentException("Fine amount must be positive");
        }
    	
        Account account = getOrCreateAccount(userId);
        account.addFine(amount, reason);
        
        userRepo.update(userRepo.getByID(userId).get());
    }

    /**
     * Allows the user to pay a fine.
     *
     * @param userId the user ID
     * @param amount the payment amount (must be positive)
     * @throws IllegalArgumentException if amount is not positive
     */
    public void payUserFine(UUID userId, double amount) {
        
    	if (amount <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }
    	
        Account account = getOrCreateAccount(userId);
        account.payFine(amount);
        
        userRepo.update(userRepo.getByID(userId).get());
    }

    /**
     * Gets the user's current account balance.
     *
     * @param userId the user ID
     * @return the user's balance
     */
    public double getUserTotalFined(UUID userId) {
        
    	Account account = getOrCreateAccount(userId);
        return account.getTotalFines();
    }

    /**
     * Gets the current status of the user's account.
     *
     * @param userId the user ID
     * @return the account status
     */
    public AccountStatus getUserAccountStatus(UUID userId) {
        
    	Account account = getOrCreateAccount(userId);
        return account.getStatus();
    }

    /**
     * Retrieves full account information for a given user.
     *
     * @param userId the user ID
     * @return the user's account
     */
    public Account getAccountInfo(UUID userId) {
        return getOrCreateAccount(userId);
    }
}

