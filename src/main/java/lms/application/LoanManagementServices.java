package lms.application;

/**
 * Groups all services related to managing loans in the library system.
 * This includes creating loans, viewing loan statistics, and querying loan data.
 * 
 * Grouping these services together makes it easier to manage dependencies
 * and keeps our constructors clean with fewer parameters.
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class LoanManagementServices {
    private final LoanService loanService;
    private final LoanStatsService loanStatsService;
    private final LoanQueryService loanQueryService;

    public LoanManagementServices(LoanService loanService, 
                                 LoanStatsService loanStatsService,
                                 LoanQueryService loanQueryService) {
        ServiceValidator.validateLoanServices(loanService, loanStatsService, loanQueryService);
        this.loanService = loanService;
        this.loanStatsService = loanStatsService;
        this.loanQueryService = loanQueryService;
    }

    public LoanService getLoanService() {
        return loanService;
    }

    public LoanStatsService getLoanStatsService() {
        return loanStatsService;
    }

    public LoanQueryService getLoanQueryService() {
        return loanQueryService;
    }
}
