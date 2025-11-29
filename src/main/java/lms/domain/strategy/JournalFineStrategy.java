package lms.domain.strategy;

/**
 * Fine calculation strategy for Journals.
 */
public class JournalFineStrategy implements FineStrategy {

	private static final double JOURNAL_FINE_RATE = 1.00;

	@Override
	public double calculateFine(long daysOverdue) {
		if (daysOverdue < 0) {
			throw new IllegalArgumentException("Days overdue cannot be negative");
		}
		return daysOverdue * JOURNAL_FINE_RATE;
	}
}
