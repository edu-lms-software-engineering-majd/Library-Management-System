package lms.domain.strategy;

/**
 * Fine calculation strategy for CDs.
 */
public class CDFineStrategy implements FineStrategy {

	private static final double CD_FINE_RATE = 0.75;

	@Override
	public double calculateFine(long daysOverdue) {
		if (daysOverdue < 0) {
			throw new IllegalArgumentException("Days overdue cannot be negative");
		}
		return daysOverdue * CD_FINE_RATE;
	}
}
