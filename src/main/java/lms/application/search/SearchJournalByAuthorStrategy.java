package lms.application.search;

import java.util.List;
import lms.domain.Journal;

/**
 * Strategy for searching Journals by author/publisher (case-insensitive partial match).
 */
public class SearchJournalByAuthorStrategy extends JournalSearchStrategy {
    
    public SearchJournalByAuthorStrategy() {
        super("Search by Author/Publisher");
    }
    
    @Override
    public List<Journal> execute(List<Journal> items, String searchTerm) {
        String lowerSearchTerm = searchTerm.toLowerCase();
        return items.stream()
                .filter(journal -> journal.getAuthor().toLowerCase().contains(lowerSearchTerm))
                .toList();
    }
}
