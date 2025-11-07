package lms.domain.utils;

import java.time.Year;

/**
 * Validator class for Book entity fields.
 * 
 * <p>
 * Provides validation methods for all Book constructor parameters to ensure
 * data integrity and business rules are enforced before object creation.
 * </p>
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class BookValidator {

	/**
	 * Validates all book fields at once.
	 * 
	 * @param title           the title of the book
	 * @param author          the author of the book
	 * @param isbn            the unique ISBN identifier
	 * @param publisher       the publisher of the book
	 * @param publicationYear the year the book was published
	 * @param category        the category or genre of the book
	 * @param totalCopies     the total number of copies owned by the library
	 * @param language        the language the book is written in
	 * @param shelfLocation   the physical location of the book in the library
	 * @throws IllegalArgumentException if any field fails validation
	 */
	public void validate(String title, String author, String isbn, String publisher, int publicationYear,
			String category, int totalCopies, String language, String shelfLocation) {

		validateTitle(title);
		validateAuthor(author);
		validateIsbn(isbn);
		validatePublisher(publisher);
		validatePublicationYear(publicationYear);
		validateCategory(category);
		validateTotalCopies(totalCopies);
		validateLanguage(language);
		validateShelfLocation(shelfLocation);
	}

	/**
	 * Validates the book title.
	 * 
	 * @param title the title to validate
	 * @throws IllegalArgumentException if title is null, empty, or blank
	 */
	public void validateTitle(String title) {
		if (title == null || title.isBlank()) {
			throw new IllegalArgumentException("Book title cannot be empty");
		}
	}

	/**
	 * Validates the book author.
	 * 
	 * @param author the author to validate
	 * @throws IllegalArgumentException if author is null, empty, or blank
	 */
	public void validateAuthor(String author) {
		if (author == null || author.isBlank()) {
			throw new IllegalArgumentException("Book Auther cannot be empty");
		}
	}

	/**
	 * Validates the book ISBN.
	 * 
	 * @param isbn the ISBN to validate
	 * @throws IllegalArgumentException if ISBN is null, empty, or blank
	 */
	public void validateIsbn(String isbn) {
		if (isbn == null || isbn.isBlank()) {
			throw new IllegalArgumentException("Book ISBN cannot be empty");
		}
	}

	/**
	 * Validates the book publisher.
	 * 
	 * @param publisher the publisher to validate
	 * @throws IllegalArgumentException if publisher is null, empty, or blank
	 */
	public void validatePublisher(String publisher) {
		if (publisher == null || publisher.isBlank()) {
			throw new IllegalArgumentException("Book publisher cannot be empty");
		}
	}

	/**
	 * Validates the publication year.
	 * 
	 * @param publicationYear the year to validate
	 * @throws IllegalArgumentException if year is in the future
	 */
	public void validatePublicationYear(int publicationYear) {
		if (publicationYear > Year.now().getValue()) {
			throw new IllegalArgumentException("Publication year cannot be in the future");
		}
	}

	/**
	 * Validates the book category.
	 * 
	 * @param category the category to validate
	 * @throws IllegalArgumentException if category is null, empty, or blank
	 */
	public void validateCategory(String category) {
		if (category == null || category.isBlank()) {
			throw new IllegalArgumentException("Book category cannot be empty");
		}
	}

	/**
	 * Validates the total copies count.
	 * 
	 * @param totalCopies the total copies to validate
	 * @throws IllegalArgumentException if total copies is negative
	 */
	public void validateTotalCopies(int totalCopies) {
		if (totalCopies < 0) {
			throw new IllegalArgumentException("Total copies cannot be negative");
		}
	}

	/**
	 * Validates the book language.
	 * 
	 * @param language the language to validate
	 * @throws IllegalArgumentException if language is null, empty, or blank
	 */
	public void validateLanguage(String language) {
		if (language == null || language.isBlank()) {
			throw new IllegalArgumentException("Book language cannot be empty");
		}
	}

	/**
	 * Validates the shelf location.
	 * 
	 * @param shelfLocation the shelf location to validate
	 * @throws IllegalArgumentException if shelf location is null, empty, or blank
	 */
	public void validateShelfLocation(String shelfLocation) {
		if (shelfLocation == null || shelfLocation.isBlank()) {
			throw new IllegalArgumentException("Book shelf location cannot be empty");
		}
	}
}
