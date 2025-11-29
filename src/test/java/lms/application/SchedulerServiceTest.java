package lms.application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.TimerTask;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SchedulerServiceTest {

	@Mock
	private LoanService loanService;

	@Mock
	private SchedulerLogger logger;

	private SchedulerService schedulerService;

	@BeforeEach
	void setUp() {
		schedulerService = new SchedulerService(loanService, logger);
	}

	@Test
	void givenNullLoanService_whenConstruct_thenThrowException() {
		assertThrows(IllegalArgumentException.class, () -> new SchedulerService(null, logger));
	}

	@Test
	void whenStartScheduler_thenTaskRunsAndLogs() throws Exception {
		schedulerService.startOverdueCheckScheduler();
		assertTrue(schedulerService.isRunning());

		TimerTask task = Arrays.stream(schedulerService.getClass().getDeclaredMethods())
				.filter(m -> m.getName().contains("createOverdueCheckTask")).findFirst().map(m -> {
					try {
						m.setAccessible(true);
						return (TimerTask) m.invoke(schedulerService);
					} catch (Exception e) {
						fail(e);
						return null;
					}
				}).orElseThrow();

		task.run();

		InOrder inOrder = inOrder(logger, loanService);
		inOrder.verify(logger).logTaskStart();
		inOrder.verify(loanService).checkAndNotifyOverdueLoans();
		inOrder.verify(logger).logTaskComplete();

		schedulerService.stop();
	}

	@Test
	void whenStartSchedulerTwice_thenThrowIllegalStateException() {
		schedulerService.startOverdueCheckScheduler();
		assertThrows(IllegalStateException.class, () -> schedulerService.startOverdueCheckScheduler());
		schedulerService.stop();
	}

	@Test
	void whenStartWithCustomValidDelay_thenSchedulerStarts() {
		schedulerService.startOverdueCheckScheduler(1000L, 2000L);
		assertTrue(schedulerService.isRunning());
		verify(logger).logSchedulerStart(contains("Overdue loan checker scheduled"));
		schedulerService.stop();
	}

	@Test
	void givenNegativeDelay_whenStartScheduler_thenThrowIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> schedulerService.startOverdueCheckScheduler(-1, 5000));
	}

	@Test
	void givenZeroPeriod_whenStartScheduler_thenThrowIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> schedulerService.startOverdueCheckScheduler(1000, 0));
	}

	@Test
	void givenTooShortPeriod_whenStartScheduler_thenThrowIllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> schedulerService.startOverdueCheckScheduler(1000, 500));
	}
	

	@Test
	void whenStopWithoutRunning_thenDoesNothing() {
		assertDoesNotThrow(() -> schedulerService.stop());
		verify(logger, never()).logSchedulerStop();
	}

	@Test
	void whenSchedulerStops_thenIsRunningReturnsFalse() {
		schedulerService.startOverdueCheckScheduler();
		schedulerService.stop();
		assertFalse(schedulerService.isRunning());
		verify(logger).logSchedulerStop();
	}
}
