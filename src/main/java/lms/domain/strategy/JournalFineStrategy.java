package lms.domain.strategy;

/**
 * Represents the fine calculation strategy for journals.
 *
 * <p>
 * According to the library policy, journals have a fixed fine rate of
 * 15 NIS for each overdue day. This class implements the
 * {@link FineStrategy} interface and provides the calculation logic
 * specific to journal items.
 * </p>
 *
 * <p>
 * This strategy is used by the domain layer when calculating fines
 * for overdue journal loans.
 * </p>
 *
 * @author Ahmad Salameh
 * @version 1.0
 */
public class JournalFineStrategy implements FineStrategy {

    private static final double JOURNAL_FINE_RATE = 1.0;

    @Override
    public double calculateFine(long daysOverdue) {
        return daysOverdue * JOURNAL_FINE_RATE;
    }
}
