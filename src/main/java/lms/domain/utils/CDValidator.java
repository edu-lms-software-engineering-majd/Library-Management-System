package lms.domain.utils;

/**
 * Validator class for CD entity fields.
 * 
 * <p>
 * Provides validation methods for all CD constructor parameters to ensure
 * data integrity and business rules are enforced before object creation.
 * </p>
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class CDValidator {

	/**
	 * Validates all CD fields at once.
	 * 
	 * @param title       the title of the CD
	 * @param artist      the artist of the CD
	 * @param totalCopies the total number of copies owned by the library
	 * @throws IllegalArgumentException if any field fails validation
	 */
	public void validate(String title, String artist, int totalCopies) {
		validateTitle(title);
		validateArtist(artist);
		validateTotalCopies(totalCopies);
	}

	/**
	 * Validates the CD title.
	 * 
	 * @param title the title to validate
	 * @throws IllegalArgumentException if title is null, empty, or blank
	 */
	public void validateTitle(String title) {
		if (title == null || title.isBlank()) {
			throw new IllegalArgumentException("CD title cannot be empty");
		}
	}

	/**
	 * Validates the CD artist.
	 * 
	 * @param artist the artist to validate
	 * @throws IllegalArgumentException if artist is null, empty, or blank
	 */
	public void validateArtist(String artist) {
		if (artist == null || artist.isBlank()) {
			throw new IllegalArgumentException("CD artist cannot be empty");
		}
	}

	/**
	 * Validates the total copies count.
	 * 
	 * @param totalCopies the total copies to validate
	 * @throws IllegalArgumentException if total copies is less than 1
	 */
	public void validateTotalCopies(int totalCopies) {
		if (totalCopies < 1) {
			throw new IllegalArgumentException("Total copies must be at least 1");
		}
	}
}
