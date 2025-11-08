package lms.application.search;

import lms.domain.Journal;

/**
 * Abstract base class for Journal search strategies.
 * 
 * @author System
 * @version 1.0
 */
public abstract class JournalSearchStrategy implements SearchStrategy<Journal> {
    
    private final String description;
    
    protected JournalSearchStrategy(String description) {
        this.description = description;
    }
    
    @Override
    public String getDescription() {
        return description;
    }
}
