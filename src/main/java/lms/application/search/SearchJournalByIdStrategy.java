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
        return SearchUtils.searchById(items, searchTerm, Journal::getId);
    }
}
