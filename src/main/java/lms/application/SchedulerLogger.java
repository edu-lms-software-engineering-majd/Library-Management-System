package lms.application;

/**
 * Logging interface for scheduler-related events.
 *
 * <p>
 * This interface defines the contract for logging the status and lifecycle of
 * scheduled background tasks such as overdue-loan checks.
 * </p>
 *
 * <p><b>Responsibilities:</b></p>
 * <ul>
 * <li>Log scheduler start and shutdown messages</li>
 * <li>Log the beginning and completion of scheduled tasks</li>
 * <li>Provide a pluggable logging abstraction that can be implemented by
 * different output mechanisms (console, file, UI, etc.)</li>
 * </ul>
 *
 * <p>
 * This interface belongs to the <b>application layer</b>, providing a simple
 * logging contract used by {@code SchedulerService}.
 * </p>
 *
 * @author Majd
 * @version 1.0
 */
public interface SchedulerLogger {

	/** Logs when the scheduler starts running. */
	void logSchedulerStart(String message);

	/** Logs when a scheduled task begins execution. */
	void logTaskStart();

	/** Logs when a scheduled task finishes execution. */
	void logTaskComplete();

	/** Logs when the scheduler is stopped. */
	void logSchedulerStop();
}
