package lms.domain;

import java.util.UUID;

import lms.domain.utils.JournalValidator;

/**
 * Represents a journal in the library collection.
 *
 * <p>Contains metadata including title, author, and copy availability.</p>
 *
 * @author Ahmad Salameh
 * @version 2.0
 */
public class Journal implements LoanableItem {
	private final UUID id;
	private String title;
	private String author;
	private int totalCopies;
	private int availableCopies;

	/**
	 * Creates a new journal with one copy.
	 *
	 * @param title  the journal title
	 * @param author the journal author
	 * @throws IllegalArgumentException if title or author is invalid
	 */
	public Journal(String title, String author) {
		this(title, author, 1);
	}

	/**
	 * Creates a new journal with the specified number of copies.
	 *
	 * @param title       the journal title
	 * @param author      the journal author
	 * @param totalCopies the total number of copies
	 * @throws IllegalArgumentException if any parameter is invalid
	 */
	public Journal(String title, String author, int totalCopies) {
		JournalValidator.getInstance().validate(title, author, totalCopies);

		this.id = UUID.randomUUID();
		this.title = title;
		this.author = author;
		this.totalCopies = totalCopies;
		this.availableCopies = totalCopies;
	}

	public UUID getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	/**
	 * Sets the journal title.
	 *
	 * @param title the new title
	 * @throws IllegalArgumentException if title is invalid
	 */
	public void setTitle(String title) {
		JournalValidator.getInstance().validateTitle(title);
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	/**
	 * Sets the journal author.
	 *
	 * @param author the new author
	 * @throws IllegalArgumentException if author is invalid
	 */
	public void setAuthor(String author) {
		JournalValidator.getInstance().validateAuthor(author);
		this.author = author;
	}

	public int getTotalCopies() {
		return totalCopies;
	}

	/**
	 * @param totalCopies the new total number of copies
	 */
	public void setTotalCopies(int totalCopies) {
		JournalValidator.getInstance().validateTotalCopies(totalCopies);
		this.totalCopies = totalCopies;
	}

	/**
	 * @return the number of copies currently available for borrowing
	 */
	public int getAvailableCopies() {
		return availableCopies;
	}

	 

	/**
	 * Checks if any copy is currently borrowed.
	 * 
	 * @return true if at least one copy is borrowed, false otherwise
	 */
	public boolean isBorrowed() {
		return availableCopies < totalCopies;
	}

	/**
	 * Checks if the journal is available for borrowing.
	 *
	 * @return true if there are available copies, false otherwise
	 */
	@Override
	public boolean isAvailable() {
		return availableCopies > 0;
	}

	/**
	 * Marks the journal as borrowed by decrementing available copies.
	 * 
	 * @throws IllegalStateException if no copies are available to borrow
	 */
	@Override
	public void decrementAvailableCopies() throws IllegalStateException {
		if (availableCopies <= 0) {
			throw new IllegalStateException("No copies available to borrow.");
		}
		availableCopies--;
	}

	/**
	 * Marks the journal as returned by incrementing available copies.
	 * 
	 * @throws IllegalStateException if all copies are already returned
	 */
	@Override
	public void incrementAvailableCopies() {
		if (availableCopies >= totalCopies) {
			throw new IllegalStateException("All copies are already returned");
		}
		availableCopies++;
	}

	@Override
	public String toString() {
		return title + " by " + author + " (" + availableCopies + "/" + totalCopies + ")";
	}

	/**
	 * Sets the number of available copies.
	 *
	 * @param availableCopies the new number of available copies
	 * @throws IllegalArgumentException if availableCopies is negative or greater
	 *                                  than totalCopies
	 */

	public void setAvailableCopies(int availableCopies) {
		if (availableCopies < 0 || availableCopies > this.totalCopies)
			throw new IllegalArgumentException("Invalid available copies");
		this.availableCopies = availableCopies;
	}

}
