package lms.domain;

/**
 * Enum representing the type of fine transaction.
 */
public enum TransactionType {
    /** Fine issued to a user (debit). */
    FINE,
    /** Payment made by a user (credit). */
    PAYMENT,
}
