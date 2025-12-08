package lms.application;

/**
 * Context object that bundles all dependencies required by {@link LoanService}.
 * 
 * <p>
 * This class follows the Context pattern to reduce constructor parameter count
 * and improve maintainability. Instead of passing multiple repositories and
 * services individually to LoanService, they are grouped into this context object.
 * </p>
 * 
 * <p><b>Benefits:</b></p>
 * <ul>
 * <li>Reduces LoanService constructor parameters from 6+ to just 1</li>
 * <li>Makes it easier to add new dependencies without breaking signatures</li>
 * <li>Improves code readability and maintainability</li>
 * <li>Follows SonarQube best practices for constructor parameter count</li>
 * </ul>
 * 
 * @author Majd Awwad
 * @version 1.0
 * @see LoanService
 * @see RepositoryContext
 * @see NotificationService
 */
public class LoanServiceContext {

	private final RepositoryContext repositories;
	private final NotificationService notificationService;

	/**
	 * Constructs a new context with all required dependencies.
	 * 
	 * @param repositories the repository context containing all data repositories
	 * @param notificationService the service for sending notifications to users
	 * @throws IllegalArgumentException if any dependency is null
	 */
	public LoanServiceContext(RepositoryContext repositories, NotificationService notificationService) {
		if (repositories == null || notificationService == null) {
			throw new IllegalArgumentException("All dependencies must be non-null");
		}
		this.repositories = repositories;
		this.notificationService = notificationService;
	}

	/**
	 * Gets the repository context containing all data repositories.
	 * 
	 * @return the repository context
	 */
	public RepositoryContext getRepositories() {
		return repositories;
	}

	/**
	 * Gets the notification service.
	 * 
	 * @return the notification service
	 */
	public NotificationService getNotificationService() {
		return notificationService;
	}
}
