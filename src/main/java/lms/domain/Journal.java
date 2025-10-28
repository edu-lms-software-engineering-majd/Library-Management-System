package lms.domain;

import java.util.UUID;

/**
 * Represents a Journal in the library collection.
 */
public class Journal {
	private final UUID id;
	private String title;
	private String author;
	private boolean isBorrowed;

	public Journal(String title, String author) {
		this.id = UUID.randomUUID();
		this.title = title;
		this.author = author;
		this.isBorrowed = false;
	}

	public UUID getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getAuthor() {
		return author;
	}

	public boolean isBorrowed() {
		return isBorrowed;
	}

	public void setBorrowed(boolean borrowed) {
		this.isBorrowed = borrowed;
	}

	@Override
	public String toString() {
		return String.format("Journal: %s by %s (Borrowed: %s)", title, author, isBorrowed ? "Yes" : "No");
	}
}
