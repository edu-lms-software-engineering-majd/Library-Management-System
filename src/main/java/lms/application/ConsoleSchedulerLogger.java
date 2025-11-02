package lms.application;

public class ConsoleSchedulerLogger implements SchedulerLogger {

	@Override
	public void logSchedulerStart(String message) {
		System.out.println(message);
	}

	@Override
	public void logTaskStart() {
		System.out.println("Running scheduled overdue loan check...");
	}

	@Override
	public void logTaskComplete() {
		System.out.println("Overdue loan check completed");
	}

	@Override
	public void logSchedulerStop() {
		System.out.println("Scheduler stopped");
	}

}
