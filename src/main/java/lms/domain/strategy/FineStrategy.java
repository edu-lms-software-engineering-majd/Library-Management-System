package lms.domain.strategy;

/**
 * Defines the strategy interface used for calculating overdue fines.
 *
 * <p>
 * Each item type in the library (Book, CD, Journal, etc.) applies its own fine
 * calculation rule. Implementing classes provide the specific logic for
 * calculating the fine amount based on the number of overdue days.
 * </p>
 *
 * <p>
 * This interface is part of the Strategy Pattern, which allows the system to
 * determine the fine behavior dynamically for different media types.
 * </p>
 *
 * @author Ahmad Salameh
 * @version 1.0
 */
public interface FineStrategy {

    /**
     * Calculates the fine amount based on the number of overdue days.
     *
     * @param daysOverdue the number of days an item is overdue
     * @return the calculated fine amount
     */
    double calculateFine(long daysOverdue);
}
