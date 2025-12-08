package lms.domain;

import java.util.UUID;

/**
 * Defines the contract for items that can be loaned to library users.
 * 
 * <p>Implementing classes (Book, CD, Journal) must provide methods to manage
 * availability and retrieve item information.</p>
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public interface LoanableItem {

	/**
	 * Checks if at least one copy is available for borrowing.
	 * 
	 * @return true if available, false otherwise
	 */
	boolean isAvailable();

	/**
	 * Decrements available copies by one when borrowed.
	 * 
	 * @throws IllegalStateException if no copies available
	 */
	void decrementAvailableCopies();

	/**
	 * Returns the unique identifier of this item.
	 * 
	 * @return the item's UUID
	 */
	UUID getId();

	/**
	 * Increments available copies by one when returned.
	 * 
	 * @throws IllegalStateException if all copies already available
	 */
	void incrementAvailableCopies();

	/**
	 * Returns the title of this item.
	 * 
	 * @return the item title
	 */
	String getTitle();

}
