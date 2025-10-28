package lms.domain;

import java.util.UUID;

/**
 * Represents a CD in the library collection.
 */
public class CD {
	private final UUID id;
	private String title;
	private String artist;
	private boolean isBorrowed;

	public CD(String title, String artist) {
		this.id = UUID.randomUUID();
		this.title = title;
		this.artist = artist;
		this.isBorrowed = false;
	}

	public UUID getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getArtist() {
		return artist;
	}

	public boolean isBorrowed() {
		return isBorrowed;
	}

	public void setBorrowed(boolean borrowed) {
		this.isBorrowed = borrowed;
	}

	@Override
	public String toString() {
		return String.format("CD: %s by %s (Borrowed: %s)", title, artist, isBorrowed ? "Yes" : "No");
	}
}
