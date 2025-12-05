package lms.application;

/**
 * Central service container for the Library Management System.
 * 
 * This class solves the problem of having too many constructor parameters
 * by organizing related services into logical groups. Instead of passing
 * 8 individual services to classes like AdminCLI, we now pass just this
 * one context object that provides access to everything needed.
 * 
 * The services are organized into three main categories:
 * - Item management (books, CDs, journals)
 * - Loan management (borrowing, statistics, queries)
 * - User management (accounts and authentication)
 * 
 * @author Majd Awwad
 * @version 2.0
 */
public class ServiceContext {
    private final ItemManagementServices itemServices;
    private final LoanManagementServices loanServices;
    private final UserManagementServices userServices;

    /**
     * Creates a new service context with all the necessary service groups.
     * 
     * This constructor only takes 3 parameters instead of the original 8,
     * which makes it compliant with SonarQube code quality standards.
     * 
     * @param itemServices handles all library item operations
     * @param loanServices handles all loan-related operations
     * @param userServices handles user accounts and authentication
     */
    public ServiceContext(ItemManagementServices itemServices, 
                         LoanManagementServices loanServices,
                         UserManagementServices userServices) {
        ServiceValidator.validateServiceGroups(itemServices, loanServices, userServices);
        this.itemServices = itemServices;
        this.loanServices = loanServices;
        this.userServices = userServices;
    }

    public UserService getUserService() {
        return userServices.getUserService();
    }

    public BookService getBookService() {
        return itemServices.getBookService();
    }

    public CDService getCdService() {
        return itemServices.getCdService();
    }

    public JournalService getJournalService() {
        return itemServices.getJournalService();
    }

    public AuthService getAuthService() {
        return userServices.getAuthService();
    }

    public LoanService getLoanService() {
        return loanServices.getLoanService();
    }

    public LoanStatsService getLoanStatsService() {
        return loanServices.getLoanStatsService();
    }

    public LoanQueryService getLoanQueryService() {
        return loanServices.getLoanQueryService();
    }
}
