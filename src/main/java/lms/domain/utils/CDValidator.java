package lms.domain.utils;

/**
 * Validator for CD entity fields.
 * 
 * <p>Validates CD data before object creation. Uses singleton pattern.</p>
 * 
 * @author Majd Awwad
 * @version 2.0
 */
public class CDValidator {

	private static final CDValidator INSTANCE = new CDValidator();

	private CDValidator() {
	}

	/**
	 * Returns the singleton instance.
	 * 
	 * @return the validator instance
	 */
	public static CDValidator getInstance() {
		return INSTANCE;
	}

	/**
	 * Validates all CD fields.
	 * 
	 * @param title       the title
	 * @param artist      the artist
	 * @param totalCopies the total copies
	 * @throws IllegalArgumentException if any field is invalid
	 */
	public void validate(String title, String artist, int totalCopies) {
		validateTitle(title);
		validateArtist(artist);
		validateTotalCopies(totalCopies);
	}

	/**
	 * Validates the CD title.
	 * 
	 * @param title the title
	 * @throws IllegalArgumentException if invalid
	 */
	public void validateTitle(String title) {
		if (title == null || title.isBlank()) {
			throw new IllegalArgumentException("CD title cannot be empty");
		}
	}

	/**
	 * Validates the CD artist.
	 * 
	 * @param artist the artist
	 * @throws IllegalArgumentException if invalid
	 */
	public void validateArtist(String artist) {
		if (artist == null || artist.isBlank()) {
			throw new IllegalArgumentException("CD artist cannot be empty");
		}
	}

	/**
	 * Validates the total copies.
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
