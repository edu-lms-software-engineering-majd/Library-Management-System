package lms.domain.strategy;

/**
 * Fine calculation strategy for journals.
 *
 * <p>Journals have a fine rate of 15.0 per day overdue.</p>
 *
 * @author Ahmad Salameh
 * @version 1.0
 */
public class JournalFineStrategy implements FineStrategy {

	private static final double JOURNAL_FINE_RATE = 15.0;

	@Override
	public double calculateFine(long daysOverdue) {
		if (daysOverdue < 0) {
			throw new IllegalArgumentException("Days overdue cannot be negative");
		}
		if (daysOverdue == 0) {
			return 0.0;
		}
		return daysOverdue * JOURNAL_FINE_RATE;
	}
}
