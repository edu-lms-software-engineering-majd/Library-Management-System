package lms.application.search;

import java.util.List;
import java.util.UUID;
import lms.domain.CD;

/**
 * Strategy for searching CDs by ID (supports partial matching).
 */
public class SearchCDByIdStrategy extends CDSearchStrategy {
    
    public SearchCDByIdStrategy() {
        super("Search by ID");
    }
    
    @Override
    public List<CD> execute(List<CD> items, String searchTerm) {
        return SearchUtils.searchById(items, searchTerm, CD::getId);
    }
}
