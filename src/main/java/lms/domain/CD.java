package lms.domain;

import java.util.Locale;
import java.util.UUID;

import lms.domain.utils.CDValidator;

/**
 * Represents a CD in the library collection.
 *
 * <p>Contains metadata including title, artist, and copy availability.</p>
 *
 * @author Ahmad Salameh
 * @version 2.0
 */
public class CD implements LoanableItem {
	private final UUID id;
	private String title;
	private String artist;
	private int totalCopies;
	private int availableCopies;

	/**
	 * Creates a new CD with one copy.
	 *
	 * @param title  the CD title
	 * @param artist the CD artist
	 * @throws IllegalArgumentException if title or artist is invalid
	 */
	public CD(String title, String artist) {
		this(title, artist, 1);
	}

	/**
	 * Creates a new CD with the specified number of copies.
	 *
	 * @param title       the CD title
	 * @param artist      the CD artist
	 * @param totalCopies the total number of copies
	 * @throws IllegalArgumentException if any parameter is invalid
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
	 * Sets the CD title.
	 *
	 * @param title the new title
	 * @throws IllegalArgumentException if title is invalid
	 */
	public void setTitle(String title) {
		CDValidator.getInstance().validateTitle(title);
		this.title = title;
	}

	public String getArtist() {
		return artist;
	}

	/**
	 * Sets the CD artist.
	 *
	 * @param artist the new artist
	 * @throws IllegalArgumentException if artist is invalid
	 */
	public void setArtist(String artist) {
		CDValidator.getInstance().validateArtist(artist);
		this.artist = artist;
	}

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
