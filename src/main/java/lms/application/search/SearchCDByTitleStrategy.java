package lms.application.search;

import java.util.List;
import lms.domain.CD;

/**
 * Strategy for searching CDs by title (case-insensitive partial match).
 */
public class SearchCDByTitleStrategy extends CDSearchStrategy {
    
    public SearchCDByTitleStrategy() {
        super("Search by Title");
    }
    
    @Override
    public List<CD> execute(List<CD> items, String searchTerm) {
        String lowerSearchTerm = searchTerm.toLowerCase();
        return items.stream()
                .filter(cd -> cd.getTitle().toLowerCase().contains(lowerSearchTerm))
                .toList();
    }
}
