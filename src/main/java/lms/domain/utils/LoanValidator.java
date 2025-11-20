package lms.domain.utils;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Validator class for Loan entity fields and operations.
 * 
 * <p>
 * Provides validation methods for loan-related data such as user ID, item ID,
 * item type, borrow date, and extension days to ensure data integrity and 
 * business rules are enforced.
 * </p>
 * 
 * <p><b>Validation Principles Applied:</b></p>
 * <ul>
 * <li><b>Centralize and Reuse Logic:</b> Single instance shared across all Loan entities</li>
 * <li><b>Fail Fast:</b> Validates early before data enters the domain</li>
 * <li><b>Separate Concerns:</b> Stateless, no I/O operations, purely syntactic validation</li>
 * </ul>
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class LoanValidator {

	private static final LoanValidator INSTANCE = new LoanValidator();

	/**
	 * Private constructor to prevent instantiation.
	 */
	private LoanValidator() {
	}

	/**
	 * Returns the singleton instance of the validator.
	 * 
	 * @return the shared LoanValidator instance
	 */
	public static LoanValidator getInstance() {
		return INSTANCE;
	}

	/**
	 * Validates all loan fields at once (Fail Fast principle).
	 * 
	 * @param userId the borrower's user ID
	 * @param itemId the borrowed item's ID
	 * @param itemType the type of item (book, cd, journal)
	 * @param borrowDate the date the item was borrowed
	 * @throws IllegalArgumentException if any field fails validation
	 */
	public void validate(UUID userId, UUID itemId, String itemType, LocalDate borrowDate) {
		validateUserId(userId);
		validateItemId(itemId);
		validateItemType(itemType);
		validateBorrowDate(borrowDate);
	}

	/**
	 * Validates the user ID.
	 * 
	 * @param userId the user ID to validate
	 * @throws IllegalArgumentException if user ID is null
	 */
	public void validateUserId(UUID userId) {
		if (userId == null) {
			throw new IllegalArgumentException("User ID cannot be null");
		}
	}

	/**
	 * Validates the item ID.
	 * 
	 * @param itemId the item ID to validate
	 * @throws IllegalArgumentException if item ID is null
	 */
	public void validateItemId(UUID itemId) {
		if (itemId == null) {
			throw new IllegalArgumentException("Item ID cannot be null");
		}
	}

	/**
	 * Validates the item type.
	 * 
	 * @param itemType the item type to validate
	 * @throws IllegalArgumentException if item type is null or blank
	 */
	public void validateItemType(String itemType) {
		if (itemType == null || itemType.isBlank()) {
			throw new IllegalArgumentException("Item type cannot be null or empty");
		}
	}

	/**
	 * Validates the borrow date.
	 * 
	 * @param borrowDate the borrow date to validate
	 * @throws IllegalArgumentException if borrow date is null or in the future
	 */
	public void validateBorrowDate(LocalDate borrowDate) {
		if (borrowDate == null) {
			throw new IllegalArgumentException("Borrow date cannot be null");
		}
		if (borrowDate.isAfter(LocalDate.now())) {
			throw new IllegalArgumentException("Borrow date cannot be in the future");
		}
	}

	/**
	 * Validates the extension days for a loan.
	 * 
	 * @param days the number of days to extend
	 * @throws IllegalArgumentException if days is not positive
	 */
	public void validateExtensionDays(int days) {
		if (days <= 0) {
			throw new IllegalArgumentException("Extension days must be positive");
		}
	}

	/**
	 * Validates that a loan can be extended (not overdue or returned).
	 * 
	 * @param isActive whether the loan is still active
	 * @param isOverdue whether the loan is overdue
	 * @throws IllegalStateException if loan cannot be extended
	 */
	public void validateCanExtend(boolean isActive, boolean isOverdue) {
		if (!isActive) {
			throw new IllegalStateException("Cannot extend a returned loan");
		}
		if (isOverdue) {
			throw new IllegalStateException("Cannot extend an overdue loan");
		}
	}

	/**
	 * Validates that an item can be returned (not already returned).
	 * 
	 * @param returnDate the current return date
	 * @throws IllegalStateException if item is already returned
	 */
	public void validateCanReturn(LocalDate returnDate) {
		if (returnDate != null) {
			throw new IllegalStateException("Item already returned");
		}
	}
}
