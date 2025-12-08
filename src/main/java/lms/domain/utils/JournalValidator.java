package lms.domain.utils;

/**
 * Validator for Journal entity fields.
 * 
 * <p>Validates journal data before object creation. Uses singleton pattern.</p>
 * 
 * @author Majd Awwad
 * @version 2.0
 */
public class JournalValidator {

	private static final JournalValidator INSTANCE = new JournalValidator();

	private JournalValidator() {
	}

	/**
	 * Returns the singleton instance.
	 * 
	 * @return the validator instance
	 */
	public static JournalValidator getInstance() {
		return INSTANCE;
	}

	/**
	 * Validates all journal fields.
	 * 
	 * @param title       the title
	 * @param author      the author
	 * @param totalCopies the total copies
	 * @throws IllegalArgumentException if any field is invalid
	 */
	public void validate(String title, String author, int totalCopies) {
		validateTitle(title);
		validateAuthor(author);
		validateTotalCopies(totalCopies);
	}

	/**
	 * Validates the journal title.
	 * 
	 * @param title the title
	 * @throws IllegalArgumentException if invalid
	 */
	public void validateTitle(String title) {
		if (title == null || title.isBlank()) {
			throw new IllegalArgumentException("Journal title cannot be empty");
		}
	}

	/**
	 * Validates the journal author.
	 * 
	 * @param author the author
	 * @throws IllegalArgumentException if invalid
	 */
	public void validateAuthor(String author) {
		if (author == null || author.isBlank()) {
			throw new IllegalArgumentException("Journal author cannot be empty");
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
