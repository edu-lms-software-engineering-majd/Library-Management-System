package lms.application;

public interface SchedulerLogger {

	void logSchedulerStart(String message);

	void logTaskStart();

	void logTaskComplete();

	void logSchedulerStop();
}
