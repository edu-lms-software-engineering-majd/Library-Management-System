package lms.domain.strategy;

/**
 * Fine calculation strategy for books.
 *
 * <p>Books have a fine rate of 0.50 per day overdue.</p>
 *
 * @author Ahmad Salameh
 * @version 1.0
 */
public class BookFineStrategy implements FineStrategy {

	private static final double BOOK_FINE_RATE = 0.50;

	@Override
	public double calculateFine(long daysOverdue) {
		if (daysOverdue < 0) {
			throw new IllegalArgumentException("Days overdue cannot be negative");
		}
		return daysOverdue * BOOK_FINE_RATE;
	}

}
