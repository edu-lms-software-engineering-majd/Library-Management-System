package lms.application;

import java.util.UUID;

import lms.domain.Notification;
import lms.domain.NotificationObserver;
import lms.domain.NotificationType;
import lms.domain.User;

/**
 * Service for managing and sending notifications to users. Implements the
 * Observer pattern to handle notification events.
 */
public class NotificationService implements NotificationObserver {

	private static final UUID SYSTEM_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

	@Override
	public void notify(User user, Notification notification) {
		user.addNotification(notification);
	}

	/**
	 * Notifies a user about an overdue item.
	 * 
	 * @param user      the user with overdue items
	 * @param itemTitle the title of the overdue item
	 */
	public void notifyOverdueItem(User user, String itemTitle) {
		Notification notification = new Notification(
				"Your item '" + itemTitle + "' is overdue. Please return it immediately.", SYSTEM_ID,
				NotificationType.OVERDUE);
		notify(user, notification);
	}
}
