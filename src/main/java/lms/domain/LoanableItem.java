package lms.domain;

import java.util.UUID;

public interface LoanableItem {
    
	boolean isAvailable();
    
	void decrementAvailableCopies();
    
	UUID getId();

	void incrementAvailableCopies();

	String getTitle();
}
