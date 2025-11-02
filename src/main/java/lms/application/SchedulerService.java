package lms.application;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Manages scheduled background tasks for the library management system.
 * <p>
 * This service provides automated periodic execution of maintenance tasks such
 * as checking for overdue loans and triggering user notifications. It uses
 * Java's built-in {@link Timer} mechanism to schedule tasks at fixed intervals.
 * </p>
 * <p>
 * The scheduler runs as a daemon thread, which means it will not prevent the
 * JVM from shutting down when the main application exits.
 * </p>
 *
 * @author majd-awwad
 * @see LoanService
 * @see Timer
 */
public class SchedulerService {

	private static final long ONE_MINUTE_MS = 60000L;
	private static final long ONE_HOUR_MS = 3600000L;
	private static final long TWENTY_FOUR_HOURS_MS = 86400000L;

	private final LoanService loanService;
	private final SchedulerLogger logger;
	private Timer timer;

	/**
	 * Constructs a new scheduler service with the specified loan service.
	 *
	 * @param loanService the loan service used to check for overdue loans
	 * @throws IllegalArgumentException if loanService is null
	 */
	public SchedulerService(LoanService loanService, SchedulerLogger logger) {
		if (loanService == null) {
			throw new IllegalArgumentException("LoanService cannot be null");
		}
		this.loanService = loanService;
		this.logger = (logger != null) ? logger : new ConsoleSchedulerLogger();
	}

	/**
	 * Creates a timer task for checking overdue loans.
	 * 
	 * @return a new TimerTask that checks for overdue loans
	 */
	private TimerTask createOverdueCheckTask() {
		return new TimerTask() {
			@Override
			public void run() {
				logger.logTaskStart();
				loanService.checkAndNotifyOverdueLoans();
				logger.logTaskComplete();
			}
		};
	}

	/**
	 * Starts the automated overdue loan checker that runs at fixed intervals.
	 * <p>
	 * The task performs the following operations:
	 * <ul>
	 * <li>Identifies all loans that have exceeded their due date</li>
	 * <li>Sends notifications to users with overdue items</li>
	 * <li>Logs the execution status to the console</li>
	 * </ul>
	 * </p>
	 * <p>
	 * The first execution occurs one minute after this method is called, followed
	 * by subsequent executions every 24 hours.
	 * </p>
	 *
	 * @throws IllegalStateException if the scheduler is already running
	 */
	public void startOverdueCheckScheduler() {
		if (timer != null) {
			throw new IllegalStateException("Scheduler is already running");
		}

		timer = new Timer(true);
		TimerTask timerTask = createOverdueCheckTask();

		timer.scheduleAtFixedRate(timerTask, ONE_MINUTE_MS, TWENTY_FOUR_HOURS_MS);

		logger.logSchedulerStart("Overdue loan checker scheduled to run every 24 hours");
	}

	/**
	 * Starts the automated overdue loan checker with custom scheduling intervals.
	 * <p>
	 * This method allows for flexible configuration of when and how often the
	 * overdue check task should execute.
	 * </p>
	 *
	 * @param initialDelayMs the delay before the first execution in milliseconds
	 * @param periodMs       the interval between successive executions in
	 *                       milliseconds
	 * @throws IllegalArgumentException if initialDelayMs or periodMs is negative
	 * @throws IllegalStateException    if the scheduler is already running
	 */
	public void startOverdueCheckScheduler(long initialDelayMs, long periodMs) {

		if (initialDelayMs < 0) {
			throw new IllegalArgumentException("Initial delay cannot be negative: " + initialDelayMs);
		}
		if (periodMs <= 0) {
			throw new IllegalArgumentException("Period must be positive: " + periodMs);
		}
		if (periodMs < 1000) {
			throw new IllegalArgumentException("Period too short (min 1 second): " + periodMs);
		}
		if (timer != null) {
			throw new IllegalStateException("Scheduler is already running");
		}

		timer = new Timer(true);
		TimerTask timerTask = createOverdueCheckTask();

		timer.scheduleAtFixedRate(timerTask, initialDelayMs, periodMs);

		logger.logSchedulerStart(
				String.format("Overdue loan checker scheduled to run every %d ms after an initial delay of %d ms",
						periodMs, initialDelayMs));
	}

	/**
	 * Stops the scheduler and cancels all pending scheduled tasks.
	 * <p>
	 * After calling this method, no further scheduled tasks will execute. To
	 * restart scheduling, call {@link #startOverdueCheckScheduler()} again.
	 * </p>
	 * <p>
	 * This method is idempotent; calling it multiple times has no additional
	 * effect.
	 * </p>
	 */
	public void stop() {
		if (timer != null) {
			timer.cancel();
			timer = null;
			logger.logSchedulerStop();
		}
	}

	/**
	 * Checks whether the scheduler is currently running.
	 *
	 * @return {@code true} if the scheduler is active, {@code false} otherwise
	 */
	public boolean isRunning() {
		return timer != null;
	}
}
