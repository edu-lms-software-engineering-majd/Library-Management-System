package lms.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NotificationTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void givenNullNotification_whenAccessMethods_throwsNullPointerException() {

		assertThrows(NullPointerException.class, () -> {
			Notification notification = null;
			notification.getNotificationContent();
		});
	}

	@Test
	void givenValidParameters_whenCreateNotification_thenAllFieldsAreSet() {

		UUID senderId = UUID.randomUUID();
		String content = "Your book is overdue";
		NotificationType type = NotificationType.OVERDUE;

		Notification notification = new Notification(content, senderId, type);

		assertNotNull(notification);
		assertEquals(content, notification.getNotificationContent());
		assertEquals(senderId, notification.getSenderID());
		assertEquals(type, notification.getType());
		assertNotNull(notification.getTimestamp());
	}

	@Test
	void givenOverdueType_whenCreateNotification_thenTypeIsOverdue() {

		UUID senderId = UUID.randomUUID();
		NotificationType type = NotificationType.OVERDUE;

		Notification notification = new Notification("Your book is overdue", senderId, type);

		assertEquals(NotificationType.OVERDUE, notification.getType());
	}

	@Test
	void givenDueSoonType_whenCreateNotification_thenTypeIsDueSoon() {

		UUID senderId = UUID.randomUUID();
		NotificationType type = NotificationType.DUE_SOON;

		Notification notification = new Notification("Your book is due soon", senderId, type);

		assertEquals(NotificationType.DUE_SOON, notification.getType());
	}

	@Test
	void givenLoanApprovedType_whenCreateNotification_thenTypeIsLoanApproved() {

		UUID senderId = UUID.randomUUID();
		NotificationType type = NotificationType.LOAN_APPROVED;

		Notification notification = new Notification("Your loan request has been approved", senderId, type);

		assertEquals(NotificationType.LOAN_APPROVED, notification.getType());
	}

	@Test
	void givenLoanRejectedType_whenCreateNotification_thenTypeIsLoanRejected() {

		UUID senderId = UUID.randomUUID();
		NotificationType type = NotificationType.LOAN_REJECTED;

		Notification notification = new Notification("Your loan request has been rejected", senderId, type);

		assertEquals(NotificationType.LOAN_REJECTED, notification.getType());
	}

	@Test
	void givenNotification_whenCreated_thenTimestampIsRecent() {

		UUID senderId = UUID.randomUUID();
		NotificationType type = NotificationType.OVERDUE;

		Instant before = Instant.now();
		Notification notification = new Notification("Test notification", senderId, type);
		Instant after = Instant.now();

		assertTrue(notification.getTimestamp().isAfter(before) || notification.getTimestamp().equals(before));
		assertTrue(notification.getTimestamp().isBefore(after) || notification.getTimestamp().equals(after));
	}

	@Test
	void givenNullContent_whenCreateNotification_thenThrowsIllegalArgumentException() {

		UUID senderId = UUID.randomUUID();
		NotificationType type = NotificationType.OVERDUE;

		assertThrows(IllegalArgumentException.class, () -> {
			new Notification(null, senderId, type);
		});
	}

	@Test
	void givenEmptyContent_whenCreateNotification_thenThrowsIllegalArgumentException() {

		UUID senderId = UUID.randomUUID();
		NotificationType type = NotificationType.OVERDUE;

		assertThrows(IllegalArgumentException.class, () -> {
			new Notification("", senderId, type);
		});
	}
}

