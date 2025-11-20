package lms.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConsoleSchedulerLoggerTest {

	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final PrintStream originalOut = System.out;
	private ConsoleSchedulerLogger logger;

	@BeforeEach
	void setUp() {
		System.setOut(new PrintStream(outContent));
		logger = new ConsoleSchedulerLogger();
	}

	@AfterEach
	void restoreStreams() {
		System.setOut(originalOut);
	}

	@Test
	void testLogSchedulerStart() {
		String message = "Scheduler started successfully";
		logger.logSchedulerStart(message);
		assertEquals(message + System.lineSeparator(), outContent.toString());
	}

	@Test
	void testLogTaskStart() {
		logger.logTaskStart();
		assertEquals("Running scheduled overdue loan check..." + System.lineSeparator(), outContent.toString());
	}

	@Test
	void testLogTaskComplete() {
		logger.logTaskComplete();
		assertEquals("Overdue loan check completed" + System.lineSeparator(), outContent.toString());
	}

	@Test
	void testLogSchedulerStop() {
		logger.logSchedulerStop();
		assertEquals("Scheduler stopped" + System.lineSeparator(), outContent.toString());
	}
}
