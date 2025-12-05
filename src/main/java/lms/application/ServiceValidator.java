package lms.application;

/**
 * Validates service dependencies to ensure they are properly initialized.
 * 
 * This class centralizes validation logic following the Single Responsibility Principle.
 * Instead of having validation scattered across multiple constructors, all service
 * validation is handled here in one place.
 * 
 * This makes the code easier to maintain and test, and ensures consistent
 * validation behavior across all service classes.
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class ServiceValidator {
    
    private ServiceValidator() {
        // Private constructor to prevent instantiation
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * Validates that a single service is not null.
     * 
     * @param service the service to validate
     * @param serviceName the name of the service for error messages
     * @throws IllegalArgumentException if the service is null
     */
    public static void requireNonNull(Object service, String serviceName) {
        if (service == null) {
            throw new IllegalArgumentException(serviceName + " cannot be null");
        }
    }
    
    /**
     * Validates that multiple services are all non-null.
     * 
     * @param services array of services to validate
     * @throws IllegalArgumentException if any service is null
     */
    public static void requireAllNonNull(Object... services) {
        if (services == null) {
            throw new IllegalArgumentException("Services array cannot be null");
        }
        
        for (Object service : services) {
            if (service == null) {
                throw new IllegalArgumentException("All services must be non-null");
            }
        }
    }
    
    /**
     * Validates item management services.
     * 
     * @param bookService the book service
     * @param cdService the CD service
     * @param journalService the journal service
     * @throws IllegalArgumentException if any service is null
     */
    public static void validateItemServices(BookService bookService, 
                                            CDService cdService, 
                                            JournalService journalService) {
        requireNonNull(bookService, "BookService");
        requireNonNull(cdService, "CDService");
        requireNonNull(journalService, "JournalService");
    }
    
    /**
     * Validates loan management services.
     * 
     * @param loanService the loan service
     * @param loanStatsService the loan statistics service
     * @param loanQueryService the loan query service
     * @throws IllegalArgumentException if any service is null
     */
    public static void validateLoanServices(LoanService loanService,
                                           LoanStatsService loanStatsService,
                                           LoanQueryService loanQueryService) {
        requireNonNull(loanService, "LoanService");
        requireNonNull(loanStatsService, "LoanStatsService");
        requireNonNull(loanQueryService, "LoanQueryService");
    }
    
    /**
     * Validates user management services.
     * 
     * @param userService the user service
     * @param authService the authentication service
     * @throws IllegalArgumentException if any service is null
     */
    public static void validateUserServices(UserService userService, 
                                           AuthService authService) {
        requireNonNull(userService, "UserService");
        requireNonNull(authService, "AuthService");
    }
    
    /**
     * Validates service group components.
     * 
     * @param itemServices the item management services
     * @param loanServices the loan management services
     * @param userServices the user management services
     * @throws IllegalArgumentException if any service group is null
     */
    public static void validateServiceGroups(ItemManagementServices itemServices,
                                            LoanManagementServices loanServices,
                                            UserManagementServices userServices) {
        requireNonNull(itemServices, "ItemManagementServices");
        requireNonNull(loanServices, "LoanManagementServices");
        requireNonNull(userServices, "UserManagementServices");
    }
}
