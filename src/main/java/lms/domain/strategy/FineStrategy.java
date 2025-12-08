package lms.domain.strategy;

/**
 * Strategy interface for calculating overdue fines.
 *
 * <p>Implementing classes provide item-specific fine calculation logic.</p>
 *
 * @author Ahmad Salameh
 * @version 1.0
 */
public interface FineStrategy {

    /**
     * Calculates the fine based on days overdue.
     *
     * @param daysOverdue the number of days overdue
     * @return the calculated fine amount
     */
    double calculateFine(long daysOverdue);
}
