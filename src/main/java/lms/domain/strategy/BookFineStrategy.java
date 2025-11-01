package lms.domain.strategy;

/**
 * Fine calculation strategy for books.
 */
public class BookFineStrategy implements FineStrategy {

	private static final double BOOK_FINE_RATE = 10.0;

	@Override
	public double calculateFine(long daysOverdue) {
		return daysOverdue * BOOK_FINE_RATE;
	}
}
