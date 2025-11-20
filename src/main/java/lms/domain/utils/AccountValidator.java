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
 * <p><b>Validation Principles Applied:</b></p>
 * <ul>
 * <li><b>Centralize and Reuse Logic:</b> Single instance shared across all Account entities</li>
 * <li><b>Fail Fast:</b> Validates early before data enters the domain</li>
 * <li><b>Separate Concerns:</b> Stateless, no I/O operations, purely syntactic validation</li>
 * </ul>
 * 
 * @author Majd Awwad
 * @version 2.0
 */
public class AccountValidator {

	private static final AccountValidator INSTANCE = new AccountValidator();

	/**
	 * Private constructor to prevent instantiation.
	 */
	private AccountValidator() {
	}

	/**
	 * Returns the singleton instance of the validator.
	 * 
	 * @return the shared AccountValidator instance
	 */
	public static AccountValidator getInstance() {
		return INSTANCE;
	}

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
