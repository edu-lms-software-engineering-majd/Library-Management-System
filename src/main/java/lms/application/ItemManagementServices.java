package lms.application;

/**
 * Groups all services related to managing library items.
 * This includes books, CDs, and journals.
 * 
 * By grouping these related services together, we keep constructor parameters
 * manageable and make the code easier to maintain.
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class ItemManagementServices {
    private final BookService bookService;
    private final CDService cdService;
    private final JournalService journalService;

    public ItemManagementServices(BookService bookService, CDService cdService, 
                                 JournalService journalService) {
        ServiceValidator.validateItemServices(bookService, cdService, journalService);
        this.bookService = bookService;
        this.cdService = cdService;
        this.journalService = journalService;
    }

    public BookService getBookService() {
        return bookService;
    }

    public CDService getCdService() {
        return cdService;
    }

    public JournalService getJournalService() {
        return journalService;
    }
}
