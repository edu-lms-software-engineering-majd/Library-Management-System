package lms.application.search;

import java.util.List;
import java.util.UUID;
import lms.domain.Journal;

/**
 * Strategy for searching Journals by ID (supports partial matching).
 */
public class SearchJournalByIdStrategy extends JournalSearchStrategy {
    
    public SearchJournalByIdStrategy() {
        super("Search by ID");
    }
    
    @Override
    public List<Journal> execute(List<Journal> items, String searchTerm) {
        String lowerSearchTerm = searchTerm.toLowerCase().trim();
        
        // Try exact UUID match first
        try {
            UUID searchId = UUID.fromString(searchTerm);
            List<Journal> exactMatch = items.stream()
                    .filter(journal -> journal.getId().equals(searchId))
                    .toList();
            if (!exactMatch.isEmpty()) {
                return exactMatch;
            }
        } catch (IllegalArgumentException e) {
            // Not a valid UUID, continue with partial matching
        }
        
        // Fall back to partial matching (case-insensitive)
        return items.stream()
                .filter(journal -> journal.getId().toString().toLowerCase().contains(lowerSearchTerm))
                .toList();
    }
}
