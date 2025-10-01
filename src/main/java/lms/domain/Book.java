package lms.domain;

import java.util.UUID;

/**
 * Represents a book in the Library Management System.
 *
 * <p>This entity contains metadata and inventory information about a book,
 * including title, author, ISBN, publisher, category, language, number of copies,
 * and shelf location. Each book is uniquely identified by a generated UUID.</p>
 *
 * <p>Business operations such as borrowing and returning copies can be
 * implemented as additional methods in this class.</p>
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class Book {
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
     * Creates a new {@code Book} with the required fields.
     * The {@code bookId} is automatically generated and
     * {@code availableCopies} is initialized to {@code totalCopies}.
     *
     * @param title the title of the book
     * @param author the author of the book
     * @param isbn the unique ISBN identifier
     * @param publisher the publisher of the book
     * @param publicationYear the year the book was published
     * @param category the category or genre of the book
     * @param totalCopies the total number of copies owned by the library
     * @param language the language the book is written in
     * @param shelfLocation the physical location of the book in the library
     */
    public Book(String title, String author, String isbn, String publisher, int publicationYear,
                String category, int totalCopies, String language, String shelfLocation) {
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
     * @param title the title of the book
     * @param author the author of the book
     * @param isbn the unique ISBN identifier
     * @param publisher the publisher of the book
     * @param publicationYear the year the book was published
     * @param category the category or genre of the book
     * @param totalCopies the total number of copies owned by the library
     * @param language the language the book is written in
     * @param description a short description or summary of the book
     * @param shelfLocation the physical location of the book in the library
     */
    public Book(String title, String author, String isbn, String publisher, int publicationYear, String category,
                int totalCopies, String language, String description, String shelfLocation) {
        this(title, author, isbn, publisher, publicationYear, category, totalCopies, language, shelfLocation);
        this.description = description;
    }

    /** @return the unique identifier of the book */
    public UUID getBookId() {
        return bookId;
    }

    /** @return the title of the book */
    public String getTitle() {
        return title;
    }

    /** @param title the new title of the book */
    public void setTitle(String title) {
        this.title = title;
    }

    /** @return the author of the book */
    public String getAuthor() {
        return author;
    }

    /** @param author the new author of the book */
    public void setAuthor(String author) {
        this.author = author;
    }

    /** @return the ISBN of the book */
    public String getIsbn() {
        return isbn;
    }

    /** @param isbn the new ISBN of the book */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /** @return the publisher of the book */
    public String getPublisher() {
        return publisher;
    }

    /** @param publisher the new publisher of the book */
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    /** @return the year the book was published */
    public int getPublicationYear() {
        return publicationYear;
    }

    /** @param publicationYear the new publication year of the book */
    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    /** @return the category or genre of the book */
    public String getCategory() {
        return category;
    }

    /** @param category the new category or genre of the book */
    public void setCategory(String category) {
        this.category = category;
    }

    /** @return the total number of copies owned by the library */
    public int getTotalCopies() {
        return totalCopies;
    }

    /** @param totalCopies the new total number of copies */
    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
    }

    /** @return the number of copies currently available for borrowing */
    public int getAvailableCopies() {
        return availableCopies;
    }

    /** @param availableCopies the new number of available copies */
    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }

    /** @return the language of the book */
    public String getLanguage() {
        return language;
    }

    /** @param language the new language of the book */
    public void setLanguage(String language) {
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

    /** @param shelfLocation the new shelf location of the book */
    public void setShelfLocation(String shelfLocation) {
        this.shelfLocation = shelfLocation;
    }
}
