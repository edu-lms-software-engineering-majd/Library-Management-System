package lms.domain;

import java.util.UUID;

/**
 * Represents a CD in the library collection.
 *
 * <p>
 * This entity contains metadata about a CD, including title, artist, and
 * borrowing status. Each CD is uniquely identified by a generated UUID.
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
public class CD implements LoanableItem {
	private final UUID id;
	private String title;
	private String artist;
	private boolean isBorrowed;

	/**
	 * Creates a new {@code CD} with the required fields. The {@code id} is
	 * automatically generated and {@code isBorrowed} is initialized to false.
	 *
	 * @param title  the title of the CD
	 * @param artist the artist of the CD
	 * @throws IllegalArgumentException if title or artist is null or blank
	 */
	public CD(String title, String artist) {
		this.id = UUID.randomUUID();

		if (title == null || title.isBlank()) {
			throw new IllegalArgumentException("CD title cannot be empty");
		}

		if (artist == null || artist.isBlank()) {
			throw new IllegalArgumentException("CD artist cannot be empty");
		}

		this.title = title;
		this.artist = artist;
		this.isBorrowed = false;
	}

	public UUID getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title of the CD.
	 *
	 * @param title the new title
	 * @throws IllegalArgumentException if title is null or blank
	 */
	public void setTitle(String title) {
		if (title == null || title.isBlank()) {
			throw new IllegalArgumentException("CD title cannot be empty");
		}
		this.title = title;
	}

	public String getArtist() {
		return artist;
	}

	/**
	 * Sets the artist of the CD.
	 *
	 * @param artist the new artist
	 * @throws IllegalArgumentException if artist is null or blank
	 */
	public void setArtist(String artist) {
		if (artist == null || artist.isBlank()) {
			throw new IllegalArgumentException("CD artist cannot be empty");
		}
		this.artist = artist;
	}

	public boolean isBorrowed() {
		return isBorrowed;
	}

	public void setBorrowed(boolean borrowed) {
		this.isBorrowed = borrowed;
	}

	/**
	 * Checks if the CD is available for borrowing.
	 *
	 * @return true if the CD is not currently borrowed, false otherwise
	 */
	@Override
	public boolean isAvailable() {
		return !isBorrowed;
	}

	/**
	 * Marks the CD as borrowed by decrementing available copies.
	 * For CDs, this sets the borrowed status to true.
	 */
	@Override
	public void decrementAvailableCopies() {
		if (isBorrowed) {
			throw new IllegalStateException("CD is already borrowed");
		}
		this.isBorrowed = true;
	}

	/**
	 * Marks the CD as returned by incrementing available copies.
	 * For CDs, this sets the borrowed status to false.
	 */
	@Override
	public void incrementAvailableCopies() {
		if (!isBorrowed) {
			throw new IllegalStateException("CD is not currently borrowed");
		}
		this.isBorrowed = false;
	}

	@Override
	public String toString() {
		return String.format("CD: %s by %s (Borrowed: %s)", title, artist, isBorrowed ? "Yes" : "No");
	}
}
