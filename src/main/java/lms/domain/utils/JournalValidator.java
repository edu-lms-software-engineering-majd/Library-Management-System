package lms.domain.utils;

/**
 * Validator class for Journal entity fields.
 * 
 * <p>
 * Provides validation methods for all Journal constructor parameters to ensure
 * data integrity and business rules are enforced before object creation.
 * </p>
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class JournalValidator {

	/**
	 * Validates all Journal fields at once.
	 * 
	 * @param title       the title of the journal
	 * @param author      the author/publisher of the journal
	 * @param totalCopies the total number of copies owned by the library
	 * @throws IllegalArgumentException if any field fails validation
	 */
	public void validate(String title, String author, int totalCopies) {
		validateTitle(title);
		validateAuthor(author);
		validateTotalCopies(totalCopies);
	}

	/**
	 * Validates the journal title.
	 * 
	 * @param title the title to validate
	 * @throws IllegalArgumentException if title is null, empty, or blank
	 */
	public void validateTitle(String title) {
		if (title == null || title.isBlank()) {
			throw new IllegalArgumentException("Journal title cannot be empty");
		}
	}

	/**
	 * Validates the journal author/publisher.
	 * 
	 * @param author the author to validate
	 * @throws IllegalArgumentException if author is null, empty, or blank
	 */
	public void validateAuthor(String author) {
		if (author == null || author.isBlank()) {
			throw new IllegalArgumentException("Journal author cannot be empty");
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
