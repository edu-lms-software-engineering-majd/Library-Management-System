package lms.domain.strategy;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class FineStrategyFactoryTest {

	
	@Test
	void givenBookType_whenGetStrategy_thenReturnsBookFineStrategy() {
		FineStrategy strategy = FineStrategyFactory.getStrategy("book");
		assertTrue(strategy instanceof BookFineStrategy);
		assertEquals(2.0 * 0.50, strategy.calculateFine(2));
	}

	@Test
	void givenCDType_whenGetStrategy_thenReturnsCDFineStrategy() {
		FineStrategy strategy = FineStrategyFactory.getStrategy("cd");
		assertTrue(strategy instanceof CDFineStrategy);
		assertEquals(2 * 20.0, strategy.calculateFine(2));
	}

	@Test
	void givenJournalType_whenGetStrategy_thenReturnsJournalFineStrategy() {
		FineStrategy strategy = FineStrategyFactory.getStrategy("journal");
		assertTrue(strategy instanceof JournalFineStrategy);
		assertEquals(3 * 15.0, strategy.calculateFine(3));
	}

	@Test
	void givenUnknownType_whenGetStrategy_thenUsesDefaultFine() {
		FineStrategy strategy = FineStrategyFactory.getStrategy("unknownTypeXYZ");
		double fine = strategy.calculateFine(4);
		assertEquals(4 * 5.0, fine);  
	}
}
