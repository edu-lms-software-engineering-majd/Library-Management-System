package lms.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Represents a user account in the Library Management System.
 * 
 * <p>
 * The Account class manages financial transactions, fines, and account status
 * for library users. It tracks fine payments, accumulates charges, and
 * automatically handles account suspension when fine thresholds are exceeded.
 * </p>
 * 
 * <h2>Key Features:</h2>
 * <ul>
 * <li>Track total fines owed by a user</li>
 * <li>Maintain transaction history for fines and payments</li>
 * <li>Automatic account suspension when fines exceed threshold</li>
 * <li>Account reactivation upon full payment of fines</li>
 * </ul>
 * 
 * <h2>Account Status Management:</h2>
 * <p>
 * Accounts are automatically suspended when total fines exceed the
 * {@code SUSPENSION_THRESHOLD} (100.0). The account is automatically
 * reactivated when all fines are paid.
 * </p>
 * 
 * @author Library Management System
 * @version 1.0
 * @since 2025-10-22
 */
public class Account {
    
	/** The unique identifier for this account. */
	private final UUID accountId;
	
	/** The unique identifier of the user who owns this account. */
	private final UUID userId;
	
	/** The total amount of fines owed by the user. */
    private double totalFines;
    
    /** The current status of the account (ACTIVE or SUSPENDED). */
    private AccountStatus status;
    
    /** The date when this account was created. */
    private final LocalDate createdAt;
    
    /** The date when this account was last updated. */
    private LocalDate updatedAt;
    
    /** The fine amount threshold that triggers automatic account suspension. */
    private static final double SUSPENSION_THRESHOLD = 100.0;
    
    /** List of all fine transactions (fines and payments) for this account. */
    private List<FineTransaction> fineTransactions;
    
    /**
     * Constructs a new Account for a user.
     * 
     * <p>
     * Initializes the account with:
     * <ul>
     * <li>A unique account ID</li>
     * <li>Zero total fines</li>
     * <li>ACTIVE status</li>
     * <li>Current date as creation and update date</li>
     * <li>An empty transaction list</li>
     * </ul>
     * </p>
     * 
     * @param userID the unique identifier of the user who owns this account
     */
    public Account(UUID userID) {
    	this.userId = userID;
        this.accountId = UUID.randomUUID();
        this.totalFines = 0.0;
        this.status = AccountStatus.ACTIVE;
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
        this.fineTransactions = new ArrayList<>();
    }
    
    /**
     * Gets the unique account identifier.
     * 
     * @return the account ID
     */
    public UUID getAccountId() { return accountId; }
    
    /**
     * Gets the user ID associated with this account.
     * 
     * @return the user ID
     */
    public UUID getUserId() { return userId; }
    
    /**
     * Gets the current status of the account.
     * 
     * @return the account status (ACTIVE or SUSPENDED)
     */
    public AccountStatus getStatus() { return status; }
    
    /**
     * Gets the date when the account was created.
     * 
     * @return the creation date
     */
    public LocalDate getCreatedAt() { return createdAt; }
    
    /**
     * Gets the date when the account was last updated.
     * 
     * @return the last update date
     */
    public LocalDate getUpdatedAt() { return updatedAt; }
    
    /**
     * Gets the total amount of fines owed.
     * 
     * @return the total fines amount
     */
    public double getTotalFines() { return totalFines; }
    
    /**
     * Gets an unmodifiable list of all fine transactions.
     * 
     * <p>
     * This includes both fines added and payments made.
     * The returned list cannot be modified to maintain data integrity.
     * </p>
     * 
     * @return an unmodifiable list of fine transactions
     */
    public List<FineTransaction> getFineTransactions() { return Collections.unmodifiableList(fineTransactions); }
    
    /**
     * Adds a fine to the account.
     * 
     * <p>
     * This method:
     * <ol>
     * <li>Adds the fine amount to the total fines</li>
     * <li>Creates a new fine transaction record</li>
     * <li>Updates the account's last update date</li>
     * <li>Automatically suspends the account if total fines exceed the threshold</li>
     * </ol>
     * </p>
     * 
     * @param amount the fine amount to add
     * @param reason a description of why the fine was applied
     */
    public void addFine(double amount, String reason) throws IllegalArgumentException {
    	
    	if (amount <= 0) {
            throw new IllegalArgumentException("Fine amount must be positive");
        }
    	
    	this.totalFines += amount;
        this.updatedAt = LocalDate.now();
        
         FineTransaction fine = new FineTransaction(
		 		amount, 
		 		reason, 
		 		TransactionType.FINE);
		 
		 this.fineTransactions.add(fine);
        
        if (this.totalFines > SUSPENSION_THRESHOLD) {
			suspendAccount();
		}
    }
    
    /**
     * Processes a fine payment.
     * 
     * <p>
     * This method:
     * <ol>
     * <li>Validates that the payment amount is positive and doesn't exceed total fines</li>
     * <li>Deducts the payment from the total fines</li>
     * <li>Creates a payment transaction record</li>
     * <li>Updates the account's last update date</li>
     * <li>Automatically reactivates the account if all fines are paid and account was suspended</li>
     * </ol>
     * </p>
     * 
     * @param amount the payment amount
     * @throws IllegalArgumentException if amount is not positive or exceeds total fines
     */
    public void payFine(double amount) {
        
    	if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (amount > this.totalFines) {
			throw new IllegalArgumentException("Amount exceeds total fines");
		}
        
        this.totalFines -= amount;
        this.updatedAt = LocalDate.now();
        
        FineTransaction payment = new FineTransaction(
		 		amount, 
		 		"Fine payment", 
		 		TransactionType.PAYMENT);
         
 		 this.fineTransactions.add(payment);
        
        if (this.totalFines == 0 && this.status == AccountStatus.SUSPENDED) {
			activateAccount();
		}
    }
    
    /**
     * Sets the account status.
     * 
     * <p>
     * Updates the account status and records the modification time.
     * </p>
     * 
     * @param status the new account status
     */
    public void setStatus(AccountStatus status) {
        this.status = status;
        this.updatedAt = LocalDate.now();
    }
    
    /**
     * Suspends the account.
     * 
     * <p>
     * Changes the account status to SUSPENDED and updates the modification time.
     * This is called automatically when total fines exceed the suspension threshold.
     * </p>
     */
    private void suspendAccount() {
        setStatus(AccountStatus.SUSPENDED);
        this.updatedAt = LocalDate.now();
    }
    
    /**
     * Activates the account.
     * 
     * <p>
     * Changes the account status to ACTIVE and updates the modification time.
     * This is called automatically when a suspended account's fines are fully paid.
     * </p>
     */
    private void activateAccount() {
    	setStatus(AccountStatus.ACTIVE);
        this.updatedAt = LocalDate.now();
    }
    
    /**
     * Returns a string representation of the account.
     * 
     * <p>
     * Provides a summary of the account including truncated ID, total fines, and status.
     * </p>
     * 
     * @return a formatted string representation of the account
     */
    @Override
    public String toString() {
    	
        return String.format("Account[ID: %s, Balance: %.2f, Status: %s", 
            accountId.toString().substring(0, 8), 
            totalFines, status);
    }
}
