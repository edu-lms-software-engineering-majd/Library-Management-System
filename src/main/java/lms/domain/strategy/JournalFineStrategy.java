package lms.domain.strategy;

/**
 * Fine calculation strategy for Journals.
 */
public class JournalFineStrategy implements FineStrategy {

	private static final double JOURNAL_FINE_RATE = 15.0;

	@Override
	public double calculateFine(long daysOverdue) {
		return daysOverdue * JOURNAL_FINE_RATE;
	}
}
