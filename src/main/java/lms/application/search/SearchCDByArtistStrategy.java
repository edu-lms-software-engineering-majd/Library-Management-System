package lms.application.search;

import java.util.List;
import lms.domain.CD;

/**
 * Strategy for searching CDs by artist (case-insensitive partial match).
 */
public class SearchCDByArtistStrategy extends CDSearchStrategy {
    
    public SearchCDByArtistStrategy() {
        super("Search by Artist");
    }
    
    @Override
    public List<CD> execute(List<CD> items, String searchTerm) {
        String lowerSearchTerm = searchTerm.toLowerCase();
        return items.stream()
                .filter(cd -> cd.getArtist().toLowerCase().contains(lowerSearchTerm))
                .toList();
    }
}
