package lms.application;

import java.util.Timer;
import java.util.TimerTask;

import lms.domain.exception.ItemNotFoundException;

/**
 * Manages scheduled background tasks for the library management system.
 * 
 * <p>
 * This service provides automated periodic execution of maintenance tasks such
 * as checking for overdue loans and triggering user notifications. It uses
 * Java's built-in {@link Timer} mechanism to schedule tasks at fixed intervals.
 * </p>
 * 
 * <p>
 * The scheduler runs as a daemon thread, meaning it will not prevent the JVM
 * from shutting down when the main application exits.
 * </p>
 *
 * @author Majd Awwad
 * @refactoredBy Ahmad Salameh
 * @see LoanService
 * @see Timer
 * @version 1.1
 */
public class SchedulerService {

	private static final long ONE_MINUTE_MS = 60000L;
	private static final long TWENTY_FOUR_HOURS_MS = 86400000L;

	private final LoanService loanService;
	private final SchedulerLogger logger;
	private Timer timer;

	/**
	 * Constructs a new scheduler service with the required dependencies.
	 *
	 * @param loanService the loan service used to check for overdue loans
	 * @param logger      optional logger; if null, a console logger is used
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
	 * Creates the TimerTask that performs overdue-loan checks.
	 *
	 * @return TimerTask instance
	 */
	private TimerTask createOverdueCheckTask() {
		return new TimerTask() {
			@Override
			public void run() {
				logger.logTaskStart();
				try {
					loanService.checkAndNotifyOverdueLoans();
					logger.logTaskComplete();
				} catch (ItemNotFoundException e) {
					e.printStackTrace();
				}
			}
		};
	}

	/**
	 * Starts the scheduler with default intervals (run every 24 hours).
	 *
	 * @throws IllegalStateException if already running
	 */
	public void startOverdueCheckScheduler() {
		if (timer != null) {
			throw new IllegalStateException("Scheduler is already running");
		}

		timer = new Timer(true);
		TimerTask task = createOverdueCheckTask();

		timer.scheduleAtFixedRate(task, ONE_MINUTE_MS, TWENTY_FOUR_HOURS_MS);

		logger.logSchedulerStart("Overdue loan checker scheduled to run every 24 hours");
	}

	/**
	 * Starts the scheduler with custom intervals.
	 *
	 * @param initialDelayMs delay before first run
	 * @param periodMs       interval between runs
	 *
	 * @throws IllegalArgumentException for invalid periods
	 * @throws IllegalStateException    if already running
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
		TimerTask task = createOverdueCheckTask();

		timer.scheduleAtFixedRate(task, initialDelayMs, periodMs);

		logger.logSchedulerStart(String.format("Overdue loan checker scheduled every %d ms (initial delay: %d ms)",
				periodMs, initialDelayMs));
	}

	/**
	 * Stops all scheduled tasks safely.
	 */
	public void stop() {
		if (timer != null) {
			timer.cancel();
			timer = null;
			logger.logSchedulerStop();
		}
	}

	/**
	 * @return true if scheduler is active
	 */
	public boolean isRunning() {
		return timer != null;
	}
}
