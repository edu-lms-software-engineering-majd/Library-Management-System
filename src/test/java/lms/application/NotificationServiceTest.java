<<<<<<< HEAD
package lms.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lms.domain.Notification;
import lms.domain.NotificationType;
import lms.domain.Role;
import lms.domain.User;
import lms.domain.utils.PasswordUtils;

public class NotificationServiceTest {

	private NotificationService notificationService;
	private User testUser;

	@BeforeEach
	void setUp() throws Exception {
		notificationService = new NotificationService();
		testUser = new User("John", "Doe", "john.doe@example.com", "johndoe",
				PasswordUtils.hashPassword("Password123!"), Role.MEMBER);
	}

	@Test
	void givenUserAndNotification_whenNotify_thenNotificationIsAddedToUser() {

		UUID senderId = UUID.randomUUID();
		Notification notification = new Notification("Test notification", senderId, NotificationType.OVERDUE);

		notificationService.notify(testUser, notification);

		assertEquals(1, testUser.getUnreadNotifications().size());
		assertEquals(notification, testUser.getUnreadNotifications().get(0));
	}

	@Test
	void givenUserAndItemTitle_whenNotifyOverdueItem_thenOverdueNotificationIsAdded() {

		String itemTitle = "Java Programming Book";

		notificationService.notifyOverdueItem(testUser, itemTitle);

		assertEquals(1, testUser.getUnreadNotifications().size());
		Notification notification = testUser.getUnreadNotifications().get(0);
		assertNotNull(notification);
		assertEquals(NotificationType.OVERDUE, notification.getType());
		assertEquals("Your item '" + itemTitle + "' is overdue. Please return it immediately.",
				notification.getNotificationContent());
	}

	@Test
	void givenNullUser_whenNotify_thenThrowsNullPointerException() {

		UUID senderId = UUID.randomUUID();
		Notification notification = new Notification("Test notification", senderId, NotificationType.OVERDUE);

		assertThrows(NullPointerException.class, () -> {
			notificationService.notify(null, notification);
		});
	}

	@Test
	void givenNullNotification_whenNotify_thenThrowsNullPointerException() {

		assertThrows(NullPointerException.class, () -> {
			notificationService.notify(testUser, null);
		});
	}

	@Test
	void givenNullUser_whenNotifyOverdueItem_thenThrowsNullPointerException() {

		assertThrows(NullPointerException.class, () -> {
			notificationService.notifyOverdueItem(null, "Some Book");
		});
	}

	@Test
	void givenMultipleNotifications_whenNotify_thenAllNotificationsAreAdded() {

		UUID senderId = UUID.randomUUID();
		Notification notification1 = new Notification("First notification", senderId, NotificationType.OVERDUE);
		Notification notification2 = new Notification("Second notification", senderId, NotificationType.DUE_SOON);

		notificationService.notify(testUser, notification1);
		notificationService.notify(testUser, notification2);

		assertEquals(2, testUser.getUnreadNotifications().size());
		assertEquals(notification1, testUser.getUnreadNotifications().get(0));
		assertEquals(notification2, testUser.getUnreadNotifications().get(1));
	}

	@Test
	void givenMultipleOverdueItems_whenNotifyOverdueItem_thenAllNotificationsAreAdded() {

		notificationService.notifyOverdueItem(testUser, "Book 1");
		notificationService.notifyOverdueItem(testUser, "Book 2");
		notificationService.notifyOverdueItem(testUser, "Book 3");

		assertEquals(3, testUser.getUnreadNotifications().size());
		assertEquals(NotificationType.OVERDUE, testUser.getUnreadNotifications().get(0).getType());
		assertEquals(NotificationType.OVERDUE, testUser.getUnreadNotifications().get(1).getType());
		assertEquals(NotificationType.OVERDUE, testUser.getUnreadNotifications().get(2).getType());
	}

	@Test
	void givenEmptyItemTitle_whenNotifyOverdueItem_thenNotificationIsCreated() {

		notificationService.notifyOverdueItem(testUser, "");

		assertEquals(1, testUser.getUnreadNotifications().size());
		assertEquals("Your item '' is overdue. Please return it immediately.",
				testUser.getUnreadNotifications().get(0).getNotificationContent());
	}

	@Test
	void givenOverdueNotification_whenNotify_thenSystemIdIsUsed() {

		UUID expectedSystemId = UUID.fromString("00000000-0000-0000-0000-000000000000");
		
		notificationService.notifyOverdueItem(testUser, "Test Book");

		assertEquals(1, testUser.getUnreadNotifications().size());
		assertEquals(expectedSystemId, testUser.getUnreadNotifications().get(0).getSenderID());
	}
}
||||||| 7160386
=======
package lms.application;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NotificationServiceTest {

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
	void test() {
		fail("Not yet implemented");
	}

}
>>>>>>> ahmad-salameh
