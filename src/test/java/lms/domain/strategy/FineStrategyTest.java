package lms.domain.strategy;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class FineStrategyInterfaceTest {

	@Test
	void givenAnonymousStrategy_whenCalculateFine_thenWorksCorrectly() {
	    FineStrategy strategy = days -> days * 5.0;  
	    assertEquals(25.0, strategy.calculateFine(5));
	}

	
	
	@Test
	void givenCDOverdue_whenCalculateFine_thenReturn20PerDay() {
	    FineStrategy strategy = new CDFineStrategy();
	    assertEquals(40.0, strategy.calculateFine(2));  
	}
	
	
	
	@Test
	void givenJournalOverdue_whenCalculateFine_thenReturn15PerDay() {
	    FineStrategy strategy = new JournalFineStrategy();
	    assertEquals(60.0, strategy.calculateFine(4)); 
	}

}

