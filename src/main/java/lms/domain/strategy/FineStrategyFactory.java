package lms.domain.strategy;

/**
 * Factory class responsible for returning the appropriate fine calculation
 * strategy based on the type of library item.
 *
 * <p>
 * This class is part of the Strategy Pattern implementation used in the
 * Library Management System. Each media type (Book, CD, Journal) has its own
 * fine rule, and this factory provides the correct {@link FineStrategy}
 * implementation according to the provided item type.
 * </p>
 *
 * <p>
 * If the item type does not match any supported type, a default fine rate
 * of 5 NIS per overdue day will be applied.
 * </p>
 *
 * <h3>Example:</h3>
 * <pre>{@code
 * FineStrategy strategy = FineStrategyFactory.getStrategy("book");
 * double fine = strategy.calculateFine(3); // returns 30.0
 * }</pre>
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
