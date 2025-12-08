package lms.domain.utils;

/**
 * Validator for Account entity operations.
 * 
 * <p>Provides validation for fines and payments. Uses singleton pattern.</p>
 * 
 * @author Majd Awwad
 * @version 2.0
 */
public class AccountValidator {

	private static final AccountValidator INSTANCE = new AccountValidator();

	public AccountValidator() {} 
	
	/**
	 * Returns the singleton instance.
	 * 
	 * @return the validator instance
	 */
	public static AccountValidator getInstance() {
		return INSTANCE;
	}

	/**
	 * Validates a fine amount.
	 * 
	 * @param amount the fine amount
	 * @throws IllegalArgumentException if amount is not positive
	 */
	public void validateFineAmount(double amount) {
		if (amount <= 0) {
			throw new IllegalArgumentException("Fine amount must be positive");
		}
	}

	/**
	 * Validates a payment amount.
	 * 
	 * @param amount      the payment amount
	 * @param totalFines  the current total fines
	 * @throws IllegalArgumentException if amount is invalid
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
