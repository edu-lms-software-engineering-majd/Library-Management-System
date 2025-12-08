package lms.domain.utils;

import java.util.UUID;

import lms.domain.NotificationType;

/**
 * Validator for Notification entity fields.
 * 
 * <p>Validates notification data before object creation. Uses singleton pattern.</p>
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class NotificationValidator {

	private static final NotificationValidator INSTANCE = new NotificationValidator();

	private NotificationValidator() {
	}

	/**
	 * Returns the singleton instance.
	 * 
	 * @return the validator instance
	 */
	public static NotificationValidator getInstance() {
		return INSTANCE;
	}

	/**
	 * Validates all notification fields.
	 * 
	 * @param content the content
	 * @param senderID the sender ID
	 * @param type the notification type
	 * @throws IllegalArgumentException if any field is invalid
	 */
	public void validate(String content, UUID senderID, NotificationType type) {
		validateContent(content);
		validateSenderId(senderID);
		validateType(type);
	}

	/**
	 * Validates the notification content.
	 * 
	 * @param content the content
	 * @throws IllegalArgumentException if invalid
	 */
	public void validateContent(String content) {
		if (content == null || content.isBlank()) {
			throw new IllegalArgumentException("Notification content cannot be null or blank");
		}
	}

	/**
	 * Validates the sender ID.
	 * 
	 * @param senderID the sender ID
	 * @throws IllegalArgumentException if null
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
