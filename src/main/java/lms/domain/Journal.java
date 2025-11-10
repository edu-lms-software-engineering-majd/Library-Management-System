package lms.domain;

import java.util.UUID;

import lms.domain.utils.JournalValidator;

/**
 * Represents a Journal in the library collection.
 *
 * <p>
 * This entity contains metadata about a journal, including title, author, and
 * borrowing status. Each journal is uniquely identified by a generated UUID.
 * </p>
 *
 * <p>
 * The constructor enforces validation rules to ensure data integrity. All
 * required fields must be non-null and non-blank.
 * </p>
 *
 * @author Ahmad & Majd Awwad
 * @version 2.0
 */
public class Journal implements LoanableItem {
	private final UUID id;
	private String title;
	private String author;
	private int totalCopies;
	private int availableCopies;

	/**
	 * Creates a new {@code Journal} with the required fields. The {@code id} is
	 * automatically generated and {@code availableCopies} is initialized to
	 * {@code totalCopies}.
	 *
	 * @param title  the title of the journal
	 * @param author the author of the journal
	 * @throws IllegalArgumentException if title or author is null or blank
	 */
	public Journal(String title, String author) {
		this(title, author, 1);
	}

	/**
	 * Creates a new {@code Journal} with the required fields and specified number of copies.
	 * The {@code id} is automatically generated and {@code availableCopies} is 
	 * initialized to {@code totalCopies}.
	 *
	 * @param title  the title of the journal
	 * @param author the author of the journal
	 * @param totalCopies the total number of copies owned by the library
	 * @throws IllegalArgumentException if title or author is null or blank, or if totalCopies is less than 1
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
	 * Sets the title of the journal.
	 *
	 * @param title the new title
	 * @throws IllegalArgumentException if title is null or blank
	 */
	public void setTitle(String title) {
		JournalValidator.getInstance().validateTitle(title);
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	/**
	 * Sets the author of the journal.
	 *
	 * @param author the new author
	 * @throws IllegalArgumentException if author is null or blank
	 */
	public void setAuthor(String author) {
		JournalValidator.getInstance().validateAuthor(author);
		this.author = author;
	}

	/**
	 * @return the total number of copies owned by the library
	 */
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
	 * @param availableCopies the new number of available copies
	 */
	public void setAvailableCopies(int availableCopies) {
		this.availableCopies = availableCopies;
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
		return String.format("Journal: %s by %s (Available: %d/%d)", title, author, availableCopies, totalCopies);
	}
}
