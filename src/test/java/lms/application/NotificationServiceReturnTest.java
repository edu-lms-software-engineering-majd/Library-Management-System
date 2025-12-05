package lms.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import lms.application.email.EmailService;
import lms.domain.Notification;
import lms.domain.NotificationType;
import lms.domain.Role;
import lms.domain.User;

@ExtendWith(MockitoExtension.class)
class NotificationServiceReturnTest {

	private static final String USER_EMAIL = "ahmad.salameh@example.com";
	private static final String EMAIL_SUBJECT = "Item Returned Successfully";

	@Mock
	private EmailService emailService;

	private NotificationService notificationService;
	private User testUser;

	@BeforeEach
	void setUp() {
		notificationService = new NotificationService(emailService);
		testUser = new User("Ahmad", "Salameh", USER_EMAIL, "ahmadsalameh", "hashedPassword123", Role.MEMBER);
	}

	@Test
	void shouldNotifyUserWhenBookReturnedWithoutFine() {
		notificationService.notifyItemReturned(testUser, "Clean Code", "book", 0.0);

		Notification notification = testUser.getUnreadNotifications().get(0);
		
		assertEquals(1, testUser.getUnreadNotifications().size());
		assertEquals(NotificationType.ITEM_RETURNED, notification.getType());
		assertEquals("Your book 'Clean Code' has been returned successfully.", 
				notification.getNotificationContent());

		verify(emailService).sendEmail(
				eq(USER_EMAIL),
				eq(EMAIL_SUBJECT),
				eq("Your book 'Clean Code' has been returned successfully.")
		);
	}

	@Test
	void shouldNotifyUserWhenCDReturnedWithFine() {
		notificationService.notifyItemReturned(testUser, "Greatest Hits Album", "CD", 15.0);

		Notification notification = testUser.getUnreadNotifications().get(0);
		String expectedMessage = "Your CD 'Greatest Hits Album' has been returned successfully. " +
				"A late return fine of 15.0 NIS has been applied to your account.";
		
		assertEquals(1, testUser.getUnreadNotifications().size());
		assertEquals(NotificationType.ITEM_RETURNED, notification.getType());
		assertEquals(expectedMessage, notification.getNotificationContent());

		verify(emailService).sendEmail(eq(USER_EMAIL), eq(EMAIL_SUBJECT), eq(expectedMessage));
	}

	@Test
	void shouldNotifyUserWhenJournalReturnedWithFine() {
		notificationService.notifyItemReturned(testUser, "Nature Journal Vol. 5", "journal", 7.5);

		Notification notification = testUser.getUnreadNotifications().get(0);
		String expectedMessage = "Your journal 'Nature Journal Vol. 5' has been returned successfully. " +
				"A late return fine of 7.5 NIS has been applied to your account.";
		
		assertEquals(1, testUser.getUnreadNotifications().size());
		assertEquals(NotificationType.ITEM_RETURNED, notification.getType());
		assertEquals(expectedMessage, notification.getNotificationContent());

		verify(emailService).sendEmail(eq(USER_EMAIL), eq(EMAIL_SUBJECT), eq(expectedMessage));
	}

	@Test
	void shouldHandleZeroFineCorrectly() {
		notificationService.notifyItemReturned(testUser, "Programming in Java", "book", 0.0);

		Notification notification = testUser.getUnreadNotifications().get(0);
		
		assertEquals("Your book 'Programming in Java' has been returned successfully.", 
				notification.getNotificationContent());
	}

	@Test
	void shouldHandleLargeFineCorrectly() {
		notificationService.notifyItemReturned(testUser, "Advanced Mathematics", "book", 150.0);

		Notification notification = testUser.getUnreadNotifications().get(0);
		String expectedMessage = "Your book 'Advanced Mathematics' has been returned successfully. " +
				"A late return fine of 150.0 NIS has been applied to your account.";
		
		assertEquals(expectedMessage, notification.getNotificationContent());
	}
}
