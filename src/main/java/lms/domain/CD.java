package lms.domain;

import java.util.Locale;
import java.util.UUID;

import lms.domain.utils.CDValidator;

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
 * @author Ahmad & Majd Awwad
 * @version 2.0
 */
public class CD implements LoanableItem {
	private final UUID id;
	private String title;
	private String artist;
	private int totalCopies;
	private int availableCopies;

	/**
	 * Creates a new {@code CD} with the required fields. The {@code id} is
	 * automatically generated and {@code availableCopies} is initialized to
	 * {@code totalCopies}.
	 *
	 * @param title  the title of the CD
	 * @param artist the artist of the CD
	 * @throws IllegalArgumentException if title or artist is null or blank
	 */
	public CD(String title, String artist) {
		this(title, artist, 1);
	}

	/**
	 * Creates a new {@code CD} with the required fields and specified number of
	 * copies. The {@code id} is automatically generated and {@code availableCopies}
	 * is initialized to {@code totalCopies}.
	 *
	 * @param title       the title of the CD
	 * @param artist      the artist of the CD
	 * @param totalCopies the total number of copies owned by the library
	 * @throws IllegalArgumentException if title or artist is null or blank, or if
	 *                                  totalCopies is less than 1
	 */
	public CD(String title, String artist, int totalCopies) {

		CDValidator.getInstance().validate(title, artist, totalCopies);

		this.id = UUID.randomUUID();
		this.title = title;
		this.artist = artist;
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
	 * Sets the title of the CD.
	 *
	 * @param title the new title
	 * @throws IllegalArgumentException if title is null or blank
	 */
	public void setTitle(String title) {
		CDValidator.getInstance().validateTitle(title);
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
		CDValidator.getInstance().validateArtist(artist);
		this.artist = artist;
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
		CDValidator.getInstance().validateTotalCopies(totalCopies);
		this.totalCopies = totalCopies;
	}

	/**
	 * @return the number of copies currently available for borrowing
	 */
	public int getAvailableCopies() {
		return availableCopies;
	}

	// NOTE: Available copies managed through domain methods
	// (decrementAvailableCopies/incrementAvailableCopies)

	/**
	 * Checks if any copy is currently borrowed.
	 * 
	 * @return true if at least one copy is borrowed, false otherwise
	 */
	public boolean isBorrowed() {
		return availableCopies < totalCopies;
	}

	/**
	 * Checks if the CD is available for borrowing.
	 *
	 * @return true if there are available copies, false otherwise
	 */
	@Override
	public boolean isAvailable() {
		return availableCopies > 0;
	}

	/**
	 * Marks the CD as borrowed by decrementing available copies.
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
	 * Marks the CD as returned by incrementing available copies.
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
		return String.format(Locale.US, "%s by %s (%d/%d)", title, artist, availableCopies, totalCopies);
	}

	public void setAvailableCopies(int availableCopies) {
		if (availableCopies < 0 || availableCopies > this.totalCopies)
			throw new IllegalArgumentException("Invalid available copies");
		this.availableCopies = availableCopies;
	}

}
