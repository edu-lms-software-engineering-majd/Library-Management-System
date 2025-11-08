package lms.application.search;

import java.util.List;
import lms.domain.CD;

/**
 * Strategy for filtering CDs by availability status.
 */
public class FilterCDByAvailabilityStrategy extends CDSearchStrategy {
    
    public FilterCDByAvailabilityStrategy() {
        super("Filter by Availability");
    }
    
    @Override
    public List<CD> execute(List<CD> items, String searchTerm) {
        boolean showOnlyAvailable = searchTerm.toLowerCase().startsWith("y");
        
        if (showOnlyAvailable) {
            return items.stream()
                    .filter(CD::isAvailable)
                    .toList();
        }
        
        return items;
    }
}
