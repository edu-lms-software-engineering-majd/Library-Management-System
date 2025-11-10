package lms.application.search;

import java.util.List;
import lms.domain.Journal;

/**
 * Strategy for filtering Journals by availability status.
 */
public class FilterJournalByAvailabilityStrategy extends JournalSearchStrategy {
    
    public FilterJournalByAvailabilityStrategy() {
        super("Filter by Availability");
    }
    
    @Override
    public List<Journal> execute(List<Journal> items, String searchTerm) {
        boolean showOnlyAvailable = searchTerm.toLowerCase().startsWith("y");
        
        if (showOnlyAvailable) {
            return items.stream()
                    .filter(Journal::isAvailable)
                    .toList();
        }
        
        return items;
    }
}
