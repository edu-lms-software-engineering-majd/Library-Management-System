package lms.domain;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a notification sent to a user in the library management system.
 * <p>
 * Notifications are used to inform users about important events such as:
 * <ul>
 *   <li>Overdue items</li>
 *   <li>Upcoming due dates</li>
 *   <li>Loan approvals/rejections</li>
 * </ul>
 * 
 * @author majd-awwad
 */

public class Notification {

	private Instant timestamp;
	private String notificationContent;
	private UUID senderID;
	private NotificationType type;
	
	/**
     * Constructs a new notification with the specified content and sender.
     * The timestamp is automatically set to the current instant.
     * 
     * @param content the notification message content
     * @param senderID the UUID of the sender (e.g., system or admin)
     * @param type the type of notification
     */
	public Notification(String content, UUID senderID, NotificationType type) {
		
		this.timestamp = Instant.now();
        this.notificationContent = content;
        this.senderID = senderID;
        this.type = type;
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
