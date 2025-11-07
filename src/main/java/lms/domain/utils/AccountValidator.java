package lms.domain.utils;

/**
 * Validator class for Account entity operations.
 * 
 * <p>
 * Provides validation methods for account-related operations such as adding
 * fines and processing payments to ensure data integrity and business rules are
 * enforced.
 * </p>
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class AccountValidator {

	/**
	 * Validates a fine amount before adding it to an account.
	 * 
	 * @param amount the fine amount to validate
	 * @throws IllegalArgumentException if amount is not positive
	 */
	public void validateFineAmount(double amount) {
		if (amount <= 0) {
			throw new IllegalArgumentException("Fine amount must be positive");
		}
	}

	/**
	 * Validates a payment amount before processing.
	 * 
	 * @param amount      the payment amount to validate
	 * @param totalFines  the current total fines owed
	 * @throws IllegalArgumentException if amount is not positive or exceeds total
	 *                                  fines
	 */
	public void validatePaymentAmount(double amount, double totalFines) {
		if (amount <= 0) {
			throw new IllegalArgumentException("Amount must be positive");
		}
		if (amount > totalFines) {
			throw new IllegalArgumentException("Amount exceeds total fines");
		}
	}
}
