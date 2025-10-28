package lms.domain;

/**
 * Observer interface for the notification system.
 * Classes implementing this interface will be notified of important events.
 */
public interface NotificationObserver {
    
	/**
     * Called when a notification needs to be sent.
     * 
     * @param user the user to notify
     * @param notification the notification to send
     */
    void notify(User user, Notification notification);
}
