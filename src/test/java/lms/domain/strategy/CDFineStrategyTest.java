package lms.domain.strategy;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class CDFineStrategyTest {

	
	@Test
	void givenPositiveDays_whenCalculateFine_thenCorrectResult() {
		CDFineStrategy strategy = new CDFineStrategy();
		assertEquals(60, strategy.calculateFine(3));  
	}
	

	@Test
	void givenZeroDays_whenCalculateFine_thenZeroFine() {
		CDFineStrategy strategy = new CDFineStrategy();
		assertEquals(0.0, strategy.calculateFine(0));
	}

	@Test
	void givenNegativeDays_whenCalculateFine_thenThrowException() {
		CDFineStrategy strategy = new CDFineStrategy();
		assertThrows(IllegalArgumentException.class, () -> strategy.calculateFine(-1));
	}
}
