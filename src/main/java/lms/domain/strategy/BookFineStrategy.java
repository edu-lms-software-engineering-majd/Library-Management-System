package lms.domain.strategy;

/**
 * Represents the fine calculation strategy for books.
 *
 * <p>
 * According to the library rules, books have a fixed fine rate of 10 NIS for
 * each overdue day. This class implements the {@link FineStrategy} interface
 * and provides the specific calculation logic for book items.
 * </p>
 *
 * <p>
 * This strategy is used by the domain layer when calculating fines for overdue
 * book loans.
 * </p>
 *
 * @author Ahmad Salameh
 * @version 1.0
 */
public class BookFineStrategy implements FineStrategy {

    private static final double BOOK_FINE_RATE = 10.0;

    @Override
    public double calculateFine(long daysOverdue) {
        return daysOverdue * BOOK_FINE_RATE;
    }
}
