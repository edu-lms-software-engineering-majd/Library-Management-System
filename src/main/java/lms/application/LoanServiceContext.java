package lms.application;

public class LoanServiceContext {

	private final RepositoryContext repositories;
	private final NotificationService notificationService;

	public LoanServiceContext(RepositoryContext repositories, NotificationService notificationService) {
		if (repositories == null || notificationService == null) {
			throw new IllegalArgumentException("All dependencies must be non-null");
		}
		this.repositories = repositories;
		this.notificationService = notificationService;
	}

	public RepositoryContext getRepositories() {
		return repositories;
	}

	public NotificationService getNotificationService() {
		return notificationService;
	}
}
