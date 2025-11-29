package lms.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import lms.application.email.EmailService;
import lms.domain.Notification;
import lms.domain.NotificationType;
import lms.domain.Role;
import lms.domain.User;
import lms.domain.utils.PasswordUtils;

class NotificationServiceTest {

	private NotificationService notificationService;

	@Mock
	private EmailService emailService;

	private User testUser;
	private static final UUID SYSTEM_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		// NotificationService now REQUIRES EmailService
		notificationService = new NotificationService(emailService);

		testUser = new User("Ahmad", "salameh", "hmeedsalameh2004@gmail.com", "AhmadSalameh",
				PasswordUtils.hashPassword("Password123!"), Role.MEMBER);
	}

	@Test
	void givenUserAndNotification_whenNotify_thenNotificationIsAddedToUser() {
		UUID senderId = UUID.randomUUID();
		Notification notification = new Notification("Test notification", senderId, NotificationType.OVERDUE);

		notificationService.notify(testUser, notification);

		assertEquals(1, testUser.getUnreadNotifications().size());
		assertEquals(notification, testUser.getUnreadNotifications().get(0));

		// Verify email sent
		verify(emailService).sendEmail(eq("hmeedsalameh2004@gmail.com"), anyString(), eq("Test notification"));
	}

	@Test
	void givenUserAndItemTitle_whenNotifyOverdueItem_thenOverdueNotificationIsAdded() {
		String itemTitle = "Java Programming Book";

		notificationService.notifyOverdueItem(testUser, itemTitle);

		Notification notification = testUser.getUnreadNotifications().get(0);

		assertNotNull(notification);
		assertEquals(NotificationType.OVERDUE, notification.getType());
		assertEquals("Your borrowed item '" + itemTitle + "' is overdue. Please return it as soon as possible.",
				notification.getNotificationContent());
		assertEquals(SYSTEM_ID, notification.getSenderID());

		verify(emailService).sendEmail(eq("hmeedsalameh2004@gmail.com"), eq("Overdue Item Notice"), anyString());
	}

	@Test
	void givenNullUserOrNotification_whenNotify_thenThrowsNullPointerException() {
		Notification notification = new Notification("Test", UUID.randomUUID(), NotificationType.OVERDUE);

		assertThrows(NullPointerException.class, () -> notificationService.notify(null, notification));
		assertThrows(NullPointerException.class, () -> notificationService.notify(testUser, null));
	}

	@Test
	void givenNullUser_whenNotifyOverdueItem_thenThrowsNullPointerException() {
		assertThrows(NullPointerException.class, () -> notificationService.notifyOverdueItem(null, "Some Book"));
	}
}
