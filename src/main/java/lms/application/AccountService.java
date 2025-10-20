package lms.application;

import java.util.UUID;
import lms.domain.Account;
import lms.persistence.AccountRepo;

/**
 * Service class responsible for managing user accounts.
 */
public class AccountService {
    private final AccountRepo accountRepo;

    public AccountService(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }

    /**
     * Gets the user's account if it exists, or creates a new one if it does not.
     *
     * @param userId the user ID
     * @return the existing or newly created account
     */
    public Account getOrCreateAccount(UUID userId) {
        return accountRepo.findByUserId(userId).orElseGet(() -> {
            Account newAccount = new Account(userId);
            accountRepo.save(newAccount);
            return newAccount;
        });
    }

    /**
     * Checks whether a user is allowed to borrow items.
     *
     * @param userId the user ID
     * @return true if the user can borrow, false otherwise
     */
    public boolean canUserBorrow(UUID userId) {
        Account account = getOrCreateAccount(userId);
        return account.canBorrow();
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
        accountRepo.update(account);
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
        accountRepo.update(account);
    }

    /**
     * Gets the user's current account balance.
     *
     * @param userId the user ID
     * @return the user's balance
     */
    public double getUserBalance(UUID userId) {
        Account account = getOrCreateAccount(userId);
        return account.getBalance();
    }

    /**
     * Gets the current status of the user's account.
     *
     * @param userId the user ID
     * @return the account status
     */
    public Account.AccountStatus getUserAccountStatus(UUID userId) {
        Account account = getOrCreateAccount(userId);
        return account.getStatus();
    }

    /**
     * Sets the maximum number of items a user can borrow.
     *
     * @param userId   the user ID
     * @param maxLimit the maximum borrow limit
     */
    public void setUserBorrowLimit(UUID userId, int maxLimit) {
        Account account = getOrCreateAccount(userId);
        account.setMaxBorrowLimit(maxLimit);
        accountRepo.update(account);
    }

    /**
     * Gets the current number of items borrowed by the user.
     *
     * @param userId the user ID
     * @return the count of currently borrowed items
     */
    public int getUserCurrentBorrowedCount(UUID userId) {
        Account account = getOrCreateAccount(userId);
        return account.getCurrentBorrowedCount();
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
