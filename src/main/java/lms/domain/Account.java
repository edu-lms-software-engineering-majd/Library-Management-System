package lms.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Account {

	private final UUID accountId;
	private final UUID userId;
	private double totalFines;
	private AccountStatus status;
	private final LocalDate createdAt;
	private LocalDate updatedAt;
	private List<FineTransaction> fineTransactions;
	private static final double SUSPENSION_THRESHOLD = 100.0;

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

	public double getTotalFines() {
		return totalFines;
	}

	public List<FineTransaction> getFineTransactions() {
		return Collections.unmodifiableList(fineTransactions);
	}
 
	public boolean canBorrow() {
		return status == AccountStatus.ACTIVE;
	}

	public double getBalance() {
		return totalFines;
	}

	public void addFine(double amount, String reason) {
		if (amount <= 0)
			throw new IllegalArgumentException("Fine amount must be positive");
		if (reason == null || reason.trim().isEmpty())
			throw new IllegalArgumentException("Reason cannot be null or empty");

		totalFines += amount;
		updatedAt = LocalDate.now();
		fineTransactions.add(new FineTransaction(amount, reason, TransactionType.FINE));

		if (totalFines > SUSPENSION_THRESHOLD) {
			suspendAccount("Automatic suspension due to fines exceeding threshold");
		}
	}

	public void payFine(double amount) {
		if (amount <= 0)
			throw new IllegalArgumentException("Payment amount must be positive");

		totalFines -= amount;
		updatedAt = LocalDate.now();
		fineTransactions.add(new FineTransaction(amount, "Fine payment", TransactionType.PAYMENT));

		if (totalFines <= 0 && status == AccountStatus.SUSPENDED) {
			activateAccount();
		}
	}

	public void suspendAccount() {
		suspendAccount("Automatic suspension");
	}

	public void suspendAccount(String reason) {
		status = AccountStatus.SUSPENDED;
		updatedAt = LocalDate.now();
	}

	public void activateAccount() {
		status = AccountStatus.ACTIVE;
		updatedAt = LocalDate.now();
	}
}
