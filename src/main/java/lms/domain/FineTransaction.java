package lms.domain;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents a financial transaction related to fines in the library system.
 * 
 * <p>Tracks fines, payments, and refunds with amount, description, and transaction type.</p>
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class FineTransaction {

	private final UUID transactionId;
	private final double amount;
	private final String description;
	private final LocalDate transactionDate;
	private final TransactionType type;

	/**
	 * Creates a new transaction record.
	 *
	 * @param amount      the transaction amount
	 * @param description the reason for the transaction
	 * @param type        the transaction type (FINE or PAYMENT)
	 */
	public FineTransaction(double amount, String description, TransactionType type) {
		this.transactionId = UUID.randomUUID();
		this.amount = amount;
		this.description = description;

		this.transactionDate = LocalDate.now();
		this.type = type;
	}

	public UUID getTransactionId() {
		return transactionId;
	}

	public double getAmount() {
		return amount;
	}

	public String getDescription() {
		return description;
	}

	public LocalDate getTransactionDate() {
		return transactionDate;
	}

	public TransactionType getType() {
		return type;
	}

	/**
	 * Returns a formatted summary of the transaction.
	 *
	 * @return human-readable transaction summary
	 */
	public String getSummary() {
		String sign = amount >= 0 ? "+" : "";
		return String.format(java.util.Locale.US, "%s%.2f NIS - %s", sign, Math.abs(amount), description);

	}
}