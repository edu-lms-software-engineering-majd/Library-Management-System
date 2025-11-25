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
		// Reserved for future global test setup
	}

	/**
	 * Cleans up test environment after all test methods. Currently not used but
	 * available for future expansion.
	 *
	 * @throws Exception if cleanup fails
	 */
	@AfterAll
	static void tearDownAfterClass() throws Exception {
		// Reserved for future global test cleanup
	}

	/**
	 * Sets up test environment before each test method. Currently not used but
	 * available for future expansion.
	 *
	 * @throws Exception if setup fails
	 */
	@BeforeEach
	void setUp() throws Exception {
		// Reserved for future per-test setup
	}

	/**
	 * Cleans up test environment after each test method. Currently not used but
	 * available for future expansion.
	 *
	 * @throws Exception if cleanup fails
	 */
	@AfterEach
	void tearDown() throws Exception {
		// Reserved for future per-test cleanup
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
	void givenNotification_whenSetNotificationContent_thenContentIsUpdated() {

		UUID senderId = UUID.randomUUID();
		String originalContent = "Original message";
		NotificationType type = NotificationType.OVERDUE;

		Notification notification = new Notification(originalContent, senderId, type);
		assertEquals(originalContent, notification.getNotificationContent());

		String newContent = "Updated message";
		notification.setNotificationContent(newContent);

		assertEquals(newContent, notification.getNotificationContent());
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
	void givenNullContent_whenCreateNotification_thenContentIsNull() {

		UUID senderId = UUID.randomUUID();
		NotificationType type = NotificationType.OVERDUE;

		Notification notification = new Notification(null, senderId, type);

		assertEquals(null, notification.getNotificationContent());
	}

	@Test
	void givenEmptyContent_whenCreateNotification_thenContentIsEmpty() {

		UUID senderId = UUID.randomUUID();
		NotificationType type = NotificationType.OVERDUE;

		Notification notification = new Notification("", senderId, type);

		assertEquals("", notification.getNotificationContent());
	}
}
