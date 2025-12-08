package lms.domain;

/**
 * Represents types of items available in the library.
 * 
 * <p>Different types have different loan periods and fine strategies.</p>
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public enum ItemType {
    /** Physical book */
    BOOK,
    
    /** Compact disc */
    CD,
    
    /** Academic or professional journal */
    JOURNAL,
    
    /** Newspaper */
    NEWSPAPER,
    
    /** Electronic book */
    EBOOK
}

