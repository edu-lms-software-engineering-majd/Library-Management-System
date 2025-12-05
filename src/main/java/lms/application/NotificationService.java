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

	private final EmailService emailService;

	public NotificationService() {
		this.emailService = EmailService.getInstance();
	}

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
	public void notify(User user, Notification notification) {
		if (user == null)
			throw new NullPointerException("User cannot be null");
		if (notification == null)
			throw new NullPointerException("Notification cannot be null");

		user.addNotification(notification);

		String email = user.getEmail();

		if (email != null && !email.isBlank()) {
			String subject = switch (notification.getType()) {
			case OVERDUE -> "Overdue Item Notice";
			case DUE_SOON -> "Due Soon Reminder";
			case LOAN_APPROVED -> "Loan Approved";
			case LOAN_REJECTED -> "Loan Rejected";
			case ITEM_RETURNED -> "Item Returned Successfully";
			default -> "Library Notification";
			};

			emailService.sendEmail(email, subject, notification.getNotificationContent());
		}
	}

	public void notifyOverdueItem(User user, String itemTitle) {
		StringBuilder message = new StringBuilder();
		message.append("Your borrowed item '")
				.append(itemTitle)
				.append("' is overdue. Please return it as soon as possible.");

		createNotification(user, message.toString(), NotificationType.OVERDUE);
	}

	public void notifyDueSoon(User user, String itemTitle, int daysLeft) {
		StringBuilder message = new StringBuilder();
		message.append("Reminder: Your borrowed item '")
				.append(itemTitle)
				.append("' is due in ")
				.append(daysLeft)
				.append(" day(s).");

		createNotification(user, message.toString(), NotificationType.DUE_SOON);
	}

	public void notifyLoanApproved(User user, String itemTitle) {
		StringBuilder message = new StringBuilder();
		message.append("Loan Approved: You have successfully borrowed '")
				.append(itemTitle)
				.append("'.");

		createNotification(user, message.toString(), NotificationType.LOAN_APPROVED);
	}

	public void notifyItemReturned(User user, String itemTitle, String itemType, double fineAmount) {
		StringBuilder message = new StringBuilder();
		message.append("Your ")
				.append(itemType)
				.append(" '")
				.append(itemTitle)
				.append("' has been returned successfully.");

		if (fineAmount > 0) {
			message.append(" A late return fine of ")
					.append(fineAmount)
					.append(" NIS has been applied to your account.");
		}

		createNotification(user, message.toString(), NotificationType.ITEM_RETURNED);
	}

	public void createNotification(User user, String message, NotificationType type) {
		Notification notification = new Notification(message, SYSTEM_ID, type);
		notify(user, notification);
	}

	public void createNotification(User user, String message) {
		createNotification(user, message, NotificationType.GENERAL);
	}
}