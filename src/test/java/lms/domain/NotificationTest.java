package lms.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class NotificationTest {

	private static final String VALID_CONTENT = "Test notification";
	private static final UUID VALID_SENDER_ID = UUID.randomUUID();

	@Test
	void createsNotificationSuccessfully() {
		String content = "Your book is overdue";
		UUID senderId = UUID.randomUUID();
		NotificationType type = NotificationType.OVERDUE;

		Notification notification = new Notification(content, senderId, type);

		assertEquals(content, notification.getNotificationContent());
		assertEquals(senderId, notification.getSenderID());
		assertEquals(type, notification.getType());
		assertNotNull(notification.getTimestamp());
	}

	@Test
	void setsTimestampWithinReasonableTimeframe() {
		Instant before = Instant.now();

		Notification notification = new Notification(VALID_CONTENT, VALID_SENDER_ID, NotificationType.OVERDUE);

		Instant after = Instant.now();
		assertFalse(notification.getTimestamp().isBefore(before));
		assertFalse(notification.getTimestamp().isAfter(after));
	}

	@Test
	void createsNotificationWithOverdueType() {
		Notification notification = new Notification(VALID_CONTENT, VALID_SENDER_ID, NotificationType.OVERDUE);

		assertEquals(NotificationType.OVERDUE, notification.getType());
	}

	@Test
	void createsNotificationWithDueSoonType() {
		Notification notification = new Notification(VALID_CONTENT, VALID_SENDER_ID, NotificationType.DUE_SOON);

		assertEquals(NotificationType.DUE_SOON, notification.getType());
	}

	@Test
	void createsNotificationWithLoanApprovedType() {
		Notification notification = new Notification(VALID_CONTENT, VALID_SENDER_ID, NotificationType.LOAN_APPROVED);

		assertEquals(NotificationType.LOAN_APPROVED, notification.getType());
	}

	@Test
	void createsNotificationWithLoanRejectedType() {
		Notification notification = new Notification(VALID_CONTENT, VALID_SENDER_ID, NotificationType.LOAN_REJECTED);

		assertEquals(NotificationType.LOAN_REJECTED, notification.getType());
	}

	@Test
	void throwsExceptionWhenContentIsNull() {
		assertThrows(IllegalArgumentException.class, 
			() -> new Notification(null, VALID_SENDER_ID, NotificationType.OVERDUE));
	}

	@Test
	void throwsExceptionWhenContentIsEmpty() {
		assertThrows(IllegalArgumentException.class, 
			() -> new Notification("", VALID_SENDER_ID, NotificationType.OVERDUE));
	}

	@Test
	void throwsExceptionWhenSenderIdIsNull() {
		assertThrows(IllegalArgumentException.class, 
			() -> new Notification(VALID_CONTENT, null, NotificationType.OVERDUE));
	}

	@Test
	void throwsExceptionWhenTypeIsNull() {
		assertThrows(IllegalArgumentException.class, 
			() -> new Notification(VALID_CONTENT, VALID_SENDER_ID, null));
	}
}