package lms.domain;

import java.time.Instant;
import java.util.UUID;

import lms.domain.utils.NotificationValidator;

/**
 * Represents a notification sent to a user.
 * 
 * <p>Used to inform users about events such as overdue items, 
 * due dates, and loan status changes.</p>
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class Notification {

	private final Instant timestamp;
	private final String notificationContent;
	private final UUID senderID;
	private final NotificationType type;
	 

	/**
	 * Creates a new notification with the current timestamp.
	 * 
	 * @param content  the notification message
	 * @param senderID the sender's UUID
	 * @param type     the notification type
	 * @throws IllegalArgumentException if any parameter is invalid
	 */
	public Notification(String content, UUID senderID, NotificationType type) {

		NotificationValidator.getInstance().validate(content, senderID, type);

		this.timestamp = Instant.now();
		this.notificationContent = content;
		this.senderID = senderID;
		this.type = type;
	}

	public String getNotificationContent() {
		return notificationContent;
	}


	public Instant getTimestamp() {
		return timestamp;
	}

	public UUID getSenderID() {
		return senderID;
	}

	public NotificationType getType() {
		return type;
	}


}
