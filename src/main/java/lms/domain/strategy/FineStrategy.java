package lms.domain.strategy;

/**
 * Strategy interface for calculating fines based on item type.
 */
public interface FineStrategy {
	/**
	 * Calculates fine amount based on number of overdue days.
	 *
	 * @param daysOverdue number of days overdue
	 * @return total fine amount
	 */
	double calculateFine(long daysOverdue);
}
