package lms.application;

/**
 * Data Transfer Object (DTO) representing book information for use in the
 * application layer.
 *
 * <p>
 * This record encapsulates all book-related data needed for creating or
 * updating books, improving method signatures by reducing parameter count and
 * grouping related data together.
 * </p>
 *
 * @param title            the title of the book
 * @param author           the author of the book
 * @param isbn             the ISBN of the book
 * @param publisher        the publisher of the book
 * @param publicationYear  the year the book was published
 * @param category         the category/genre of the book
 * @param totalCopies      the total number of copies available
 * @param language         the language of the book
 * @param description      a brief description of the book (optional for updates)
 * @param shelfLocation    the physical location of the book in the library
 * 
 * @author Majd
 * @version 1.0
 */
public record BookDTO(
	String title,
	String author,
	String isbn,
	String publisher,
	Integer publicationYear,
	String category,
	Integer totalCopies,
	String language,
	String description,
	String shelfLocation
) {
}
