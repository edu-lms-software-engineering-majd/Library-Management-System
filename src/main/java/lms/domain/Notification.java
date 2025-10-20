package lms.domain;

import java.time.Instant;
import java.util.UUID;

public class Notification {

	private Instant timestamp;
	private String notificationContent;
	private UUID senderID;
	
	public Notification(String content, UUID senderID) {
		
		this.timestamp = Instant.now();
		this.notificationContent = content;
		this.senderID = senderID;
	}

	public String getNotificationContent() {
		return notificationContent;
	}

	public void setNotificationContent(String notificationContent) {
		this.notificationContent = notificationContent;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public UUID getSenderID() {
		return senderID;
	}
	
	
	
}
