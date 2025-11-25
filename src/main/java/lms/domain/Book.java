package lms.domain;

import java.util.UUID;

import lms.domain.utils.BookValidator;

/**
 * Represents a book in the Library Management System.
 *
 * <p>
 * This entity contains metadata and inventory information about a book,
 * including title, author, ISBN, publisher, category, language, number of
 * copies, and shelf location. Each book is uniquely identified by a generated
 * UUID.
 * </p>
 *
 * <p>
 * Business operations such as borrowing and returning copies can be implemented
 * as additional methods in this class.
 * </p>
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class Book implements LoanableItem {

	private final UUID bookId;
	private String title;
	private String author;
	private String isbn;
	private String publisher;
	private int publicationYear;
	private String category;
	private int totalCopies;
	private int availableCopies;
	private String language;
	private String description;
	private String shelfLocation;

	/**
	 * Creates a new {@code Book} with the required fields. The {@code bookId} is
	 * automatically generated and {@code availableCopies} is initialized to
	 * {@code totalCopies}.
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
	 */

	
	public Book(String title, String author, String isbn, String publisher, int publicationYear, String category,
			int totalCopies, String language, String shelfLocation) {
		BookValidator.getInstance().validate(title, author, isbn, publisher, publicationYear, category, totalCopies,
				language, shelfLocation);

		this.bookId = UUID.randomUUID();
		this.title = title;
		this.author = author;
		this.isbn = isbn;
		this.publisher = publisher;
		this.publicationYear = publicationYear;
		this.category = category;
		this.totalCopies = totalCopies;
		this.availableCopies = totalCopies;
		this.language = language;
		this.description = null;
		this.shelfLocation = shelfLocation;
	}

	/**
	 * Creates a new {@code Book} with an optional description.
	 *
	 * @param title           the title of the book
	 * @param author          the author of the book
	 * @param isbn            the unique ISBN identifier
	 * @param publisher       the publisher of the book
	 * @param publicationYear the year the book was published
	 * @param category        the category or genre of the book
	 * @param totalCopies     the total number of copies owned by the library
	 * @param language        the language the book is written in
	 * @param description     a short description or summary of the book
	 * @param shelfLocation   the physical location of the book in the library
	 */
	
	
	public String getIsbn() {
		return isbn;
	}

	
	
	public Book(String title, String author, String isbn, String publisher, int publicationYear, String category,
			int totalCopies, String language, String description, String shelfLocation) {
		this(title, author, isbn, publisher, publicationYear, category, totalCopies, language, shelfLocation);
		this.description = description;
	}

	public boolean isAvailable() {

		return availableCopies > 0;
	}

	@Override
	public UUID getId() {
		return bookId;
	}

	@Override
	public void decrementAvailableCopies() throws IllegalStateException {

		if (availableCopies <= 0)
			throw new IllegalStateException("No copies available to borrow.");

		availableCopies--;
	}

	@Override
	public void incrementAvailableCopies() {

		if (availableCopies >= getTotalCopies())
			throw new IllegalStateException("You Already Have All Copies of this Book");

		availableCopies++;

	}

	/** @return the title of the book */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title of the book.
	 * 
	 * @param title the new title of the book
	 * @throws IllegalArgumentException if title is null or blank
	 */
	public void setTitle(String title) {
		BookValidator.getInstance().validateTitle(title);
		this.title = title;
	}

	/** @return the author of the book */
	public String getAuthor() {
		return author;
	}

	/**
	 * Sets the author of the book.
	 * 
	 * @param author the new author of the book
	 * @throws IllegalArgumentException if author is null or blank
	 */
	public void setAuthor(String author) {
		BookValidator.getInstance().validateAuthor(author);
		this.author = author;
	}

	/** @return the ISBN of the book */
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	// NOTE: ISBN is immutable - no setter provided as it's a unique identifier

	/** @return the publisher of the book */
	public String getPublisher() {
		return publisher;
	}

	/**
	 * Sets the publisher of the book.
	 * 
	 * @param publisher the new publisher of the book
	 * @throws IllegalArgumentException if publisher is null or blank
	 */
	public void setPublisher(String publisher) {
		BookValidator.getInstance().validatePublisher(publisher);
		this.publisher = publisher;
	}

	/** @return the year the book was published */
	public int getPublicationYear() {
		return publicationYear;
	}

	/**
	 * Sets the publication year of the book.
	 * 
	 * @param publicationYear the new publication year of the book
	 * @throws IllegalArgumentException if year is in the future
	 */
	public void setPublicationYear(int publicationYear) {
		BookValidator.getInstance().validatePublicationYear(publicationYear);
		this.publicationYear = publicationYear;
	}

	/** @return the category or genre of the book */
	public String getCategory() {
		return category;
	}

	/**
	 * Sets the category of the book.
	 * 
	 * @param category the new category or genre of the book
	 * @throws IllegalArgumentException if category is null or blank
	 */
	public void setCategory(String category) {
		BookValidator.getInstance().validateCategory(category);
		this.category = category;
	}

	/** @return the total number of copies owned by the library */
	public int getTotalCopies() {
		return totalCopies;
	}

	/**
	 * Sets the total number of copies.
	 * 
	 * @param totalCopies the new total number of copies
	 * @throws IllegalArgumentException if total copies is negative
	 */
	public void setTotalCopies(int totalCopies) {
		BookValidator.getInstance().validateTotalCopies(totalCopies);
		this.totalCopies = totalCopies;
	}

	/** @return the number of copies currently available for borrowing */
	public int getAvailableCopies() {
		return availableCopies;
	}

	/**
	 * Checks if at least one copy of the book is available for borrowing.
	 * 
	 * @return true if available copies > 0
	 */
	public boolean hasAvailableCopies() {
		return availableCopies > 0;
	}

	/**
	 * Checks if all copies of the book are currently borrowed.
	 * 
	 * @return true if no copies are available
	 */
	public boolean isFullyBorrowed() {
		return availableCopies == 0;
	}

	/** @return the language of the book */
	public String getLanguage() {
		return language;
	}

	/**
	 * Sets the language of the book.
	 * 
	 * @param language the new language of the book
	 * @throws IllegalArgumentException if language is null or blank
	 */
	public void setLanguage(String language) {
		BookValidator.getInstance().validateLanguage(language);
		this.language = language;
	}

	/** @return the description of the book */
	public String getDescription() {
		return description;
	}

	/** @param description the new description of the book */
	public void setDescription(String description) {
		this.description = description;
	}

	/** @return the physical shelf location of the book */
	public String getShelfLocation() {
		return shelfLocation;
	}

	/**
	 * Sets the shelf location of the book.
	 * 
	 * @param shelfLocation the new shelf location of the book
	 * @throws IllegalArgumentException if shelf location is null or blank
	 */
	public void setShelfLocation(String shelfLocation) {
		BookValidator.getInstance().validateShelfLocation(shelfLocation);
		this.shelfLocation = shelfLocation;

	}
	
	
	
	public void setAvailableCopies(int availableCopies) {
	    if (availableCopies < 0 || availableCopies > this.totalCopies)
	        throw new IllegalArgumentException("Invalid available copies");
	    this.availableCopies = availableCopies;
	}

}
