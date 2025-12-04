package lms.domain;

import java.time.Instant;
import java.util.UUID;

import lms.domain.utils.NotificationValidator;

/**
 * Represents a notification sent to a user in the library management system.
 * <p>
 * Notifications are used to inform users about important events such as:
 * <ul>
 * <li>Overdue items</li>
 * <li>Upcoming due dates</li>
 * <li>Loan approvals/rejections</li>
 * </ul>
 * 
 * @author Majd Awwad
 */
public class Notification {

	private final Instant timestamp;
	private final String notificationContent;
	private final UUID senderID;
	private final NotificationType type;
	 

	/**
	 * Constructs a new notification with the specified content and sender. The
	 * timestamp is automatically set to the current instant.
	 * 
	 * @param content  the notification message content
	 * @param senderID the UUID of the sender (e.g., system or admin)
	 * @param type     the type of notification
	 * @throws IllegalArgumentException if any parameter is null or content is blank
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
