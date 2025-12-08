package lms.domain.strategy;

/**
 * Factory for creating fine calculation strategies based on item type.
 *
 * <p>Returns the appropriate strategy for books, CDs, journals, or a default
 * strategy with 5.0 per day for unknown types.</p>
 *
 * @author Ahmad Salameh
 * @version 1.0
 */
public class FineStrategyFactory {


    public static FineStrategy getStrategy(String itemType) {
        return switch (itemType.toLowerCase()) {
            case "book" -> new BookFineStrategy();
            case "cd" -> new CDFineStrategy();
            case "journal" -> new JournalFineStrategy();
            default -> days -> days * 5.0;
        };
    }
}
