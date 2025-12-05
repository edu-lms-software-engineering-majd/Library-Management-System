package lms.domain.strategy;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class BookFineStrategyTest {

	@Test
	void givenPositiveDays_whenCalculateFine_thenCorrectResult() {
		BookFineStrategy strategy = new BookFineStrategy();
		assertEquals(1.5, strategy.calculateFine(3)); 
	}

	@Test
	void givenZeroDays_whenCalculateFine_thenZeroFine() {
		BookFineStrategy strategy = new BookFineStrategy();
		assertEquals(0.0, strategy.calculateFine(0));
	}

	@Test
	void givenNegativeDays_whenCalculateFine_thenThrowException() {
		BookFineStrategy strategy = new BookFineStrategy();
		assertThrows(IllegalArgumentException.class, () -> strategy.calculateFine(-1));
	}
}
