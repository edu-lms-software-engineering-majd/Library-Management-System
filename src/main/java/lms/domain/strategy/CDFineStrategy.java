package lms.domain.strategy;

/**
 * Represents the fine calculation strategy for CDs.
 *
 * <p>
 * According to the library rules, CDs have a fixed fine rate of 20 NIS for
 * each overdue day. This class implements the {@link FineStrategy} interface
 * and provides the specific calculation logic for CD items.
 * </p>
 *
 * <p>
 * This strategy is used by the domain layer when calculating fines for overdue
 * CD loans.
 * </p>
 *
 * @author Ahmad Salameh
 * @version 1.0
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