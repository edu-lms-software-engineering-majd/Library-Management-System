package lms.application;

import java.util.UUID;

import lms.application.email.EmailService;
import lms.domain.Notification;
import lms.domain.NotificationType;
import lms.domain.User;

/**
 * NotificationService
 *
 * <p>
 * Central application service responsible for sending system notifications to
 * users and forwarding them as real email messages.
 * </p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 * <li>Create Notification domain objects</li>
 * <li>Store notifications inside the User entity</li>
 * <li>Send real email messages through {@link EmailService}</li>
 * </ul>
 *
 * <p>
 * <b>Author:</b> Ahmad Salameh
 * </p>
 * <p>
 * <b>Layer:</b> Application Layer
 * </p>
 */
public class NotificationService {

	/** Represents the system user who generates auto-notifications. */
	private static final UUID SYSTEM_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

	/** Email sender service (Gmail SMTP). */
	private final EmailService emailService;

	/**
	 * Constructs a NotificationService.
	 *
	 * @param emailService the email sending service (mandatory)
	 */
	public NotificationService(EmailService emailService) {
		if (emailService == null)
			throw new IllegalArgumentException("EmailService cannot be null");
		this.emailService = emailService;
	}

	/**
	 * Sends internal system notification + email to the user.
	 *
	 * @param user         the target user
	 * @param notification the notification domain object
	 */
	public void notify(User user, Notification notification)

	{

		if (user == null)
			throw new NullPointerException("User cannot be null");
		if (notification == null)
			throw new NullPointerException("Notification cannot be null");

		// 1) Save internal notification in the user entity
		user.addNotification(notification);

		// 2) Send email if user has email
		String email = user.getEmail();

		if (email != null && !email.isBlank()) {

			String subject = switch (notification.getType()) {
			case OVERDUE -> "Overdue Item Notice";
			case DUE_SOON -> "Due Soon Reminder";
			case LOAN_APPROVED -> "Loan Approved";
			case LOAN_REJECTED -> "Loan Rejected";
			default -> "Library Notification";
			};

			emailService.sendEmail(email, subject, notification.getNotificationContent());
		}

	}

	/**
	 * Sends overdue notification + email.
	 *
	 * @param user      the user who borrowed the item
	 * @param itemTitle the item title
	 */
	public void notifyOverdueItem(User user, String itemTitle) {

		String message = "Your borrowed item '" + itemTitle + "' is overdue. "
				+ "Please return it as soon as possible.";

		Notification notification = new Notification(message, SYSTEM_ID, NotificationType.OVERDUE);

		notify(user, notification);
	}

	/**
	 * Sends a "due soon" reminder.
	 *
	 * @param user      the borrower
	 * @param itemTitle the item title
	 * @param daysLeft  days left until due date
	 */
	public void notifyDueSoon(User user, String itemTitle, int daysLeft) {

		String message = "Reminder: Your borrowed item '" + itemTitle + "' is due in " + daysLeft + " day(s).";

		Notification notification = new Notification(message, SYSTEM_ID, NotificationType.DUE_SOON);

		notify(user, notification);
	}

	/**
	 * Sends a notification that a loan was approved.
	 *
	 * @param user      the borrower
	 * @param itemTitle the borrowed item
	 */
	public void notifyLoanApproved(User user, String itemTitle) {

		String message = "Loan Approved: You have successfully borrowed '" + itemTitle + "'.";

		Notification notification = new Notification(message, SYSTEM_ID, NotificationType.LOAN_APPROVED);

		notify(user, notification);
	}
}
