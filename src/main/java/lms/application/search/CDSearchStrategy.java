package lms.application.search;

import lms.domain.CD;

/**
 * Abstract base class for CD search strategies.
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public abstract class CDSearchStrategy implements SearchStrategy<CD> {
    
    private final String description;
    
    protected CDSearchStrategy(String description) {
        this.description = description;
    }
    
    @Override
    public String getDescription() {
        return description;
    }
}
