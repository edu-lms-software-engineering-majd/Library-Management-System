package lms.domain;

/**
 * Represents the status of a user account.
 * 
 * <p>Account status determines borrowing privileges and user standing.</p>
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public enum AccountStatus {

	/** Account is active and can borrow items */
	ACTIVE, 
	
	/** Account is suspended due to excessive fines */
	SUSPENDED, 
	
	/** Account is inactive */
	INACTIVE, 
	
	/** Account is permanently blacklisted */
	BLACKLISTED
}
