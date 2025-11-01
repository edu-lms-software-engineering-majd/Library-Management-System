package lms.domain;

import java.util.UUID;

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
 * @author Ahmad
 * @version 2.0
 */
public class Journal implements LoanableItem {
	private final UUID id;
	private String title;
	private String author;
	private boolean isBorrowed;

	/**
	 * Creates a new {@code Journal} with the required fields. The {@code id} is
	 * automatically generated and {@code isBorrowed} is initialized to false.
	 *
	 * @param title  the title of the journal
	 * @param author the author of the journal
	 * @throws IllegalArgumentException if title or author is null or blank
	 */
	public Journal(String title, String author) {
		this.id = UUID.randomUUID();

		if (title == null || title.isBlank()) {
			throw new IllegalArgumentException("Journal title cannot be empty");
		}

		if (author == null || author.isBlank()) {
			throw new IllegalArgumentException("Journal author cannot be empty");
		}

		this.title = title;
		this.author = author;
		this.isBorrowed = false;
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
		if (title == null || title.isBlank()) {
			throw new IllegalArgumentException("Journal title cannot be empty");
		}
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
		if (author == null || author.isBlank()) {
			throw new IllegalArgumentException("Journal author cannot be empty");
		}
		this.author = author;
	}

	public boolean isBorrowed() {
		return isBorrowed;
	}

	public void setBorrowed(boolean borrowed) {
		this.isBorrowed = borrowed;
	}

	/**
	 * Checks if the journal is available for borrowing.
	 *
	 * @return true if the journal is not currently borrowed, false otherwise
	 */
	@Override
	public boolean isAvailable() {
		return !isBorrowed;
	}

	/**
	 * Marks the journal as borrowed by decrementing available copies.
	 * For journals, this sets the borrowed status to true.
	 */
	@Override
	public void decrementAvailableCopies() {
		if (isBorrowed) {
			throw new IllegalStateException("Journal is already borrowed");
		}
		this.isBorrowed = true;
	}

	/**
	 * Marks the journal as returned by incrementing available copies.
	 * For journals, this sets the borrowed status to false.
	 */
	@Override
	public void incrementAvailableCopies() {
		if (!isBorrowed) {
			throw new IllegalStateException("Journal is not currently borrowed");
		}
		this.isBorrowed = false;
	}

	@Override
	public String toString() {
		return String.format("Journal: %s by %s (Borrowed: %s)", title, author, isBorrowed ? "Yes" : "No");
	}
}
