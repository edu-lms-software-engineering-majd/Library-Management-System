package lms.domain.utils;

/**
 * Validator class for Journal entity fields.
 * 
 * <p>
 * Provides validation methods for all Journal constructor parameters to ensure
 * data integrity and business rules are enforced before object creation.
 * This validator is stateless and uses a singleton pattern for reuse.
 * </p>
 * 
 * <p><b>Validation Principles Applied:</b></p>
 * <ul>
 * <li><b>Centralize and Reuse Logic:</b> Single instance shared across all Journal entities</li>
 * <li><b>Fail Fast:</b> Validates early before data enters the domain</li>
 * <li><b>Separate Concerns:</b> Stateless, no I/O operations, purely syntactic validation</li>
 * </ul>
 * 
 * @author Majd Awwad
 * @version 2.0
 */
public class JournalValidator {

	private static final JournalValidator INSTANCE = new JournalValidator();

	/**
	 * Private constructor to prevent instantiation.
	 */
	private JournalValidator() {
	}

	/**
	 * Returns the singleton instance of the validator.
	 * 
	 * @return the shared JournalValidator instance
	 */
	public static JournalValidator getInstance() {
		return INSTANCE;
	}

	/**
	 * Validates all Journal fields at once (Fail Fast principle).
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
