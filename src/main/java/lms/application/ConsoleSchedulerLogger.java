package lms.application;

/**
 * Console-based implementation of the {@link SchedulerLogger} interface.
 *
 * <p>
 * This logger is used by the scheduler subsystem to output lifecycle messages
 * directly to the console. It is typically used in development or CLI-based
 * demos where simple console logging is sufficient.
 * </p>
 *
 * <h3>Responsibilities:</h3>
 * <ul>
 * <li>Log scheduler start/stop events</li>
 * <li>Log the beginning and completion of scheduled tasks</li>
 * <li>Provide lightweight, dependency-free logging</li>
 * </ul>
 *
 * <p>
 * Original Author: Majd Refactored by: Ahmad Salameh
 * </p>
 *
 * @version 1.1
 */
public class ConsoleSchedulerLogger implements SchedulerLogger {

	/**
	 * Logs a custom message when the scheduler starts.
	 *
	 * @param message a custom startup message
	 */
	@Override
	public void logSchedulerStart(String message) {
		System.out.println(message);
	}

	/**
	 * Logs the beginning of a scheduled task execution.
	 */
	@Override
	public void logTaskStart() {
		System.out.println("Running scheduled overdue loan check...");
	}

	/**
	 * Logs the successful completion of a scheduled task.
	 */
	@Override
	public void logTaskComplete() {
		System.out.println("Overdue loan check completed");
	}

	/**
	 * Logs that the scheduler has stopped.
	 */
	@Override
	public void logSchedulerStop() {
		System.out.println("Scheduler stopped");
	}
}
