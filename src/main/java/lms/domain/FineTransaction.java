package lms.domain;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents a fine-related financial transaction in the library system.
 * 
 * <p>
 * This class tracks fines, payments, and refunds linked to user accounts and loans.
 * </p>
 * 
 * <ul>
 *   <li><b>FINE</b> – when a user is charged for overdue or damaged items</li>
 *   <li><b>PAYMENT</b> – when a user pays part or all of a fine</li>
 *   <li><b>REFUND</b> – when the library refunds a previously charged fine</li>
 * </ul>
 * 
 * @author
 * @version 1.0
 */
public class FineTransaction {

    private final UUID transactionId;
    private final double amount;
    private final String description;
    private final LocalDate transactionDate;
    private final TransactionType type;


    /**
     * Creates a new fine transaction record.
     *
     * @param accountId   the account ID associated with the transaction
     * @param loanId      the loan ID related to this fine or payment (nullable)
     * @param amount      the transaction amount (positive for credit, negative for debit)
     * @param description short description or reason for the transaction
     * @param type        the type of transaction (FINE, PAYMENT, REFUND)
     */
    public FineTransaction(double amount, String description, TransactionType type) {
        this.transactionId = UUID.randomUUID();
        this.amount = amount;
        this.description = description;
        this.transactionDate = LocalDate.now();
        this.type = type;
    }

    public UUID getTransactionId() { return transactionId; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
    public LocalDate getTransactionDate() { return transactionDate; }
    public TransactionType getType() { return type; }

    /**
     * Returns a formatted summary string for display or logs.
     *
     * @return a human-readable summary of the transaction
     */
    
    public String getSummary() {
        String sign = amount >= 0 ? "+" : "";
        return String.format("%s%.2f NIS - %s", sign, amount, description);
    }
}
