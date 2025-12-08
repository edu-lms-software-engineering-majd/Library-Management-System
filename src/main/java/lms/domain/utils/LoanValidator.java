package lms.domain.utils;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Validator for Loan entity fields.
 * 
 * <p>Validates loan data including IDs, item type, and dates. Uses singleton pattern.</p>
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class LoanValidator {

	private static final LoanValidator INSTANCE = new LoanValidator();

	private LoanValidator() {
	}

	/**
	 * Returns the singleton instance.
	 * 
	 * @return the validator instance
	 */
	public static LoanValidator getInstance() {
		return INSTANCE;
	}

	/**
	 * Validates all loan fields.
	 * 
	 * @param userId the user ID
	 * @param itemId the item ID
	 * @param itemType the item type
	 * @param borrowDate the borrow date
	 * @throws IllegalArgumentException if any field is invalid
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
	 * @param userId the user ID
	 * @throws IllegalArgumentException if null
	 */
	public void validateUserId(UUID userId) {
		if (userId == null) {
			throw new IllegalArgumentException("User ID cannot be null");
		}
	}

	/**
	 * Validates the item ID.
	 * 
	 * @param itemId the item ID
	 * @throws IllegalArgumentException if null
	 */
	public void validateItemId(UUID itemId) {
		if (itemId == null) {
			throw new IllegalArgumentException("Item ID cannot be null");
		}
	}

	/**
	 * Validates the item type.
	 * 
	 * @param itemType the item type
	 * @throws IllegalArgumentException if invalid
	 */
	public void validateItemType(String itemType) {
		if (itemType == null || itemType.isBlank()) {
			throw new IllegalArgumentException("Item type cannot be null or empty");
		}
	}

	/**
	 * Validates the borrow date.
	 * 
	 * @param borrowDate the borrow date
	 * @throws IllegalArgumentException if invalid
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
