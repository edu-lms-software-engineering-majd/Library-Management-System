package lms.domain;

import java.util.UUID;

import lms.domain.strategy.FineStrategy;

public interface LoanableItem {

	boolean isAvailable();

	void decrementAvailableCopies();

	UUID getId();

	void incrementAvailableCopies();

	String getTitle();

	 

}
