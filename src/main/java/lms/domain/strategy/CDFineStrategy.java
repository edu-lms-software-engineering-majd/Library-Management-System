package lms.domain.strategy;

/**
 * Fine calculation strategy for CDs.
 */
public class CDFineStrategy implements FineStrategy {

	private static final double CD_FINE_RATE = 20.0;

	@Override
	public double calculateFine(long daysOverdue) {
		return daysOverdue * CD_FINE_RATE;
	}
}
