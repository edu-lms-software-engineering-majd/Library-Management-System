package lms.application;

import java.util.UUID;

import lms.domain.Notification;
import lms.domain.NotificationObserver;
import lms.domain.NotificationType;
import lms.domain.User;

/**
 * Service for managing and sending notifications to users.
 * 
 * <p>
 * This class implements the Observer pattern to dispatch notification events to
 * users inside the system. It integrates with the {@link User} domain entity
 * and delegates message storage to the user object itself.
 * </p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 * <li>Create and send different types of notifications</li>
 * <li>Integrate with domain observer interface</li>
 * <li>Support system-generated notification events</li>
 * </ul>
 *
 * @author Majd
 * @refactoredBy Ahmad Salameh
 * @version 1.0
 */
public class NotificationService implements NotificationObserver {

	private static final UUID SYSTEM_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

	@Override
	public void notify(User user, Notification notification) {
		if (user == null)
			throw new NullPointerException("User cannot be null");
		if (notification == null)
			throw new NullPointerException("Notification cannot be null");

		user.addNotification(notification);
	}

	/**
	 * Sends an overdue notification regarding the specified item.
	 *
	 * @param user      the user who should receive the notification
	 * @param itemTitle the title of the overdue item
	 */
	public void notifyOverdueItem(User user, String itemTitle) {
		if (user == null)
			throw new NullPointerException("User cannot be null");

		Notification notification = new Notification(
				"Your item '" + itemTitle + "' is overdue. Please return it immediately.", SYSTEM_ID,
				NotificationType.OVERDUE);

		notify(user, notification);
	}
}
