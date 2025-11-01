package lms.domain.strategy;

/**
 * Factory class for selecting the correct fine strategy based on the type of
 * library item.
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
