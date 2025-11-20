package lms.domain.utils;

import java.util.UUID;

import lms.domain.NotificationType;

/**
 * Validator class for Notification entity fields.
 * 
 * <p>
 * Provides validation methods for notification-related data such as content,
 * sender ID, and notification type to ensure data integrity and business rules
 * are enforced.
 * </p>
 * 
 * <p><b>Validation Principles Applied:</b></p>
 * <ul>
 * <li><b>Centralize and Reuse Logic:</b> Single instance shared across all Notification entities</li>
 * <li><b>Fail Fast:</b> Validates early before data enters the domain</li>
 * <li><b>Separate Concerns:</b> Stateless, no I/O operations, purely syntactic validation</li>
 * </ul>
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class NotificationValidator {

	private static final NotificationValidator INSTANCE = new NotificationValidator();

	/**
	 * Private constructor to prevent instantiation.
	 */
	private NotificationValidator() {
	}

	/**
	 * Returns the singleton instance of the validator.
	 * 
	 * @return the shared NotificationValidator instance
	 */
	public static NotificationValidator getInstance() {
		return INSTANCE;
	}

	/**
	 * Validates all notification fields at once (Fail Fast principle).
	 * 
	 * @param content the notification message content
	 * @param senderID the UUID of the sender
	 * @param type the type of notification
	 * @throws IllegalArgumentException if any field fails validation
	 */
	public void validate(String content, UUID senderID, NotificationType type) {
		validateContent(content);
		validateSenderId(senderID);
		validateType(type);
	}

	/**
	 * Validates the notification content.
	 * 
	 * @param content the notification content to validate
	 * @throws IllegalArgumentException if content is null or blank
	 */
	public void validateContent(String content) {
		if (content == null || content.isBlank()) {
			throw new IllegalArgumentException("Notification content cannot be null or blank");
		}
	}

	/**
	 * Validates the sender ID.
	 * 
	 * @param senderID the sender ID to validate
	 * @throws IllegalArgumentException if sender ID is null
	 */
	public void validateSenderId(UUID senderID) {
		if (senderID == null) {
			throw new IllegalArgumentException("Sender ID cannot be null");
		}
	}

	/**
	 * Validates the notification type.
	 * 
	 * @param type the notification type to validate
	 * @throws IllegalArgumentException if type is null
	 */
	public void validateType(NotificationType type) {
		if (type == null) {
			throw new IllegalArgumentException("Notification type cannot be null");
		}
	}
}
