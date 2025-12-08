package lms.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import lms.domain.utils.AccountValidator;

/**
 * Represents a user account in the Library Management System.
 * 
 * <p>Manages financial transactions, fines, and account status for library users.
 * Accounts are automatically suspended when total fines exceed 100.0 and reactivated
 * when fines are fully paid.</p>
 * 
 * @author Ahmad Salameh
 * @version 1.0
 */
public class Account {

	private final UUID accountId;
	private final UUID userId;
	private double totalFines;
	private AccountStatus status;
	private final LocalDate createdAt;
	private LocalDate updatedAt;
	private static final double SUSPENSION_THRESHOLD = 100.0;
	private List<FineTransaction> fineTransactions;

	/**
	 * Creates a new account for the specified user with zero fines and active status.
	 * 
	 * @param userID the user's unique identifier
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

	public UUID getAccountId() {
		return accountId;
	}

	public UUID getUserId() {
		return userId;
	}

	public AccountStatus getStatus() {
		return status;
	}

	public LocalDate getCreatedAt() {
		return createdAt;
	}

	public LocalDate getUpdatedAt() {
		return updatedAt;
	}

	public double getTotalFines() {
		return totalFines;
	}

	/**
	 * Returns the account balance (equivalent to total fines).
	 * 
	 * @return the current balance
	 */
	public double getBalance() {
		return totalFines;
	}

	/**
	 * Returns an unmodifiable view of all fine transactions.
	 * 
	 * @return list of fine transactions
	 */
	public List<FineTransaction> getFineTransactions() {
		return Collections.unmodifiableList(fineTransactions);
	}

	/**
	 * Adds a fine to the account and suspends it if the threshold is exceeded.
	 * 
	 * @param amount the fine amount (must be positive)
	 * @param reason the reason for the fine
	 * @throws IllegalArgumentException if amount is not positive
	 */
	public void addFine(double amount, String reason) throws IllegalArgumentException {

		AccountValidator.getInstance().validateFineAmount(amount);

		this.totalFines += amount;
		this.updatedAt = LocalDate.now();

		FineTransaction fine = new FineTransaction(amount, reason, TransactionType.FINE);
		this.fineTransactions.add(fine);

		if (this.totalFines > SUSPENSION_THRESHOLD) {
			suspendAccount();
		}
	}

	/**
	 * Suspends the account with a default reason.
	 */
	public void suspendAccount() {
		suspendAccount("Exceeded fine limit");
	}

	/**
	 * Processes a payment towards outstanding fines.
	 * 
	 * @param amount the payment amount (must be positive and not exceed total fines)
	 * @throws IllegalArgumentException if amount is invalid
	 */
	public void payFine(double amount) {

		AccountValidator.getInstance().validatePaymentAmount(amount, this.totalFines);

		this.totalFines -= amount;
		this.updatedAt = LocalDate.now();

		FineTransaction payment = new FineTransaction(amount, "Fine payment", TransactionType.PAYMENT);
		this.fineTransactions.add(payment);

		if (this.totalFines == 0 && this.status == AccountStatus.SUSPENDED) {
			activateAccount();
		}
	}

	/** @return true if the account has fines owed */
	public boolean hasOutstandingBalance() {
		return totalFines > 0;
	}

	/**
	 * Determines if the user can borrow books based on account status.
	 * 
	 * @return true if the account is active and has no outstanding fines
	 */
	public boolean canBorrowBooks() {
		return status == AccountStatus.ACTIVE && totalFines == 0;
	}

	/**
	 * Wrapper used by AccountService#canUserBorrow.
	 */
	public boolean canBorrow() {
		return canBorrowBooks();
	}

	/**
	 * Suspends the account with a specific reason.
	 */
	public void suspendAccount(String reason) {
		this.status = AccountStatus.SUSPENDED;
		this.updatedAt = LocalDate.now();

		FineTransaction note = new FineTransaction(0.0, "Account suspended: " + reason, TransactionType.FINE);
		this.fineTransactions.add(note);
	}

	/** Activates the account. */
	public void activateAccount() {
		this.status = AccountStatus.ACTIVE;
		this.updatedAt = LocalDate.now();
	}

	@Override
	public String toString() {
		return String.format("Account[ID: %s, Balance: %.2f, Status: %s]", accountId.toString().substring(0, 8),
				totalFines, status);

	}
	
	public void setStatus(AccountStatus status) {
	    this.status = status;
	    this.updatedAt = LocalDate.now();
	}

	
	
}
