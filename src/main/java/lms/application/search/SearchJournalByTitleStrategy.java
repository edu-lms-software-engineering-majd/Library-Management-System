package lms.application.search;

import java.util.List;
import lms.domain.Journal;

/**
 * Strategy for searching Journals by title (case-insensitive partial match).
 */
public class SearchJournalByTitleStrategy extends JournalSearchStrategy {
    
    public SearchJournalByTitleStrategy() {
        super("Search by Title");
    }
    
    @Override
    public List<Journal> execute(List<Journal> items, String searchTerm) {
        String lowerSearchTerm = searchTerm.toLowerCase();
        return items.stream()
                .filter(journal -> journal.getTitle().toLowerCase().contains(lowerSearchTerm))
                .toList();
    }
}
