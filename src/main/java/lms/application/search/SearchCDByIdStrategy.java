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
        String lowerSearchTerm = searchTerm.toLowerCase().trim();
        
        // Try exact UUID match first
        try {
            UUID searchId = UUID.fromString(searchTerm);
            List<CD> exactMatch = items.stream()
                    .filter(cd -> cd.getId().equals(searchId))
                    .toList();
            if (!exactMatch.isEmpty()) {
                return exactMatch;
            }
        } catch (IllegalArgumentException e) {
            // Not a valid UUID, continue with partial matching
        }
        
        // Fall back to partial matching (case-insensitive)
        return items.stream()
                .filter(cd -> cd.getId().toString().toLowerCase().contains(lowerSearchTerm))
                .toList();
    }
}
