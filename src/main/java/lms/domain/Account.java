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
 * @author Majd Awwad Refactored by: Ahmad Salameh
 * @version 1.0
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

	/** @return the account ID */
	public UUID getAccountId() {
		return accountId;
	}

	/** @return the user ID */
	public UUID getUserId() {
		return userId;
	}

	/** @return the account status (ACTIVE or SUSPENDED) */
	public AccountStatus getStatus() {
		return status;
	}

	/** @return the creation date */
	public LocalDate getCreatedAt() {
		return createdAt;
	}

	/** @return the last update date */
	public LocalDate getUpdatedAt() {
		return updatedAt;
	}

	/** @return the total fines amount */
	public double getTotalFines() {
		return totalFines;
	}

	/**
	 * Wrapper used by AccountService#getUserBalance. In this model, "balance" =
	 * total fines.
	 */
	public double getBalance() {
		return totalFines;
	}

	/**
	 * Gets an unmodifiable list of all fine transactions.
	 */
	public List<FineTransaction> getFineTransactions() {
		return Collections.unmodifiableList(fineTransactions);
	}

	/**
	 * Adds a fine to the account.
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

	/** Convenience overload without custom reason. */
	public void suspendAccount() {
		suspendAccount("Exceeded fine limit");
	}

	/**
	 * Processes a fine payment.
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
}
