package lms.domain;

/**
 * Observer interface for receiving notification events.
 * 
 * <p>Implementing classes are notified when events occur that require
 * user notification.</p>
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public interface NotificationObserver {
    
	/**
     * Sends a notification to the specified user.
     * 
     * @param user         the user to notify
     * @param notification the notification to send
     */
    void notify(User user, Notification notification);
}
