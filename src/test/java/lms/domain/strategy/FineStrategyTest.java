package lms.domain.strategy;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class FineStrategyInterfaceTest {

	@Test
	void givenAnonymousStrategy_whenCalculateFine_thenWorksCorrectly() {
		FineStrategy strategy = days -> days * 2.0;
		assertEquals(6.0, strategy.calculateFine(3));
	}
}
