package lms.domain.strategy;

/**
 * Fine calculation strategy for CDs.
 *
 * <p>CDs have a fine rate of 20.0 per day overdue.</p>
 *
 * @author Ahmad Salameh
 * @version 1.0
 */
public class CDFineStrategy implements FineStrategy {

	private static final double CD_FINE_RATE = 20.0;

	@Override
	public double calculateFine(long daysOverdue) {
		if (daysOverdue < 0) {
			throw new IllegalArgumentException("Days overdue cannot be negative");
		}
		if (daysOverdue == 0) {
			return 0.0;
		}
		return daysOverdue * CD_FINE_RATE;
	}
}