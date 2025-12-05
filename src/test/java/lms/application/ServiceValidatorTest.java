package lms.application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServiceValidatorTest {

    private BookService mockBookService;
    private CDService mockCDService;
    private JournalService mockJournalService;
    private LoanService mockLoanService;
    private LoanStatsService mockLoanStatsService;
    private LoanQueryService mockLoanQueryService;
    private UserService mockUserService;
    private AuthService mockAuthService;
    private ItemManagementServices mockItemServices;
    private LoanManagementServices mockLoanServices;
    private UserManagementServices mockUserServices;

    @BeforeEach
    void setUp() {
        mockBookService = mock(BookService.class);
        mockCDService = mock(CDService.class);
        mockJournalService = mock(JournalService.class);
        mockLoanService = mock(LoanService.class);
        mockLoanStatsService = mock(LoanStatsService.class);
        mockLoanQueryService = mock(LoanQueryService.class);
        mockUserService = mock(UserService.class);
        mockAuthService = mock(AuthService.class);
        mockItemServices = mock(ItemManagementServices.class);
        mockLoanServices = mock(LoanManagementServices.class);
        mockUserServices = mock(UserManagementServices.class);
    }

    @Test
    void givenNonNullService_whenRequireNonNull_thenNoException() {
        Object validService = new Object();
        
        assertDoesNotThrow(() -> ServiceValidator.requireNonNull(validService, "TestService"));
    }
    
    @Test
    void givenNullService_whenRequireNonNull_thenThrowIllegalArgument() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> ServiceValidator.requireNonNull(null, "TestService"));
        
        assertEquals("TestService cannot be null", exception.getMessage());
    }
    
    @Test
    void givenNullService_whenRequireNonNull_thenMessageIncludesServiceName() {
        String serviceName = "CustomServiceName";
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> ServiceValidator.requireNonNull(null, serviceName));
        
        assertEquals(serviceName + " cannot be null", exception.getMessage());
    }

    @Test
    void givenAllNonNullServices_whenRequireAllNonNull_thenNoException() {
        Object service1 = new Object();
        Object service2 = new Object();
        Object service3 = new Object();
        
        assertDoesNotThrow(() -> ServiceValidator.requireAllNonNull(service1, service2, service3));
    }
    
    @Test
    void givenNullArray_whenRequireAllNonNull_thenThrowIllegalArgument() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> ServiceValidator.requireAllNonNull((Object[]) null));
        
        assertEquals("Services array cannot be null", exception.getMessage());
    }
    
    @Test
    void givenOneNullService_whenRequireAllNonNull_thenThrowIllegalArgument() {
        Object service1 = new Object();
        Object service2 = null;
        Object service3 = new Object();
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> ServiceValidator.requireAllNonNull(service1, service2, service3));
        
        assertEquals("All services must be non-null", exception.getMessage());
    }
    
    @Test
    void givenFirstServiceNull_whenRequireAllNonNull_thenThrowIllegalArgument() {
        Object service1 = null;
        Object service2 = new Object();
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> ServiceValidator.requireAllNonNull(service1, service2));
        
        assertEquals("All services must be non-null", exception.getMessage());
    }
    
    @Test
    void givenLastServiceNull_whenRequireAllNonNull_thenThrowIllegalArgument() {
        Object service1 = new Object();
        Object service2 = new Object();
        Object service3 = null;
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> ServiceValidator.requireAllNonNull(service1, service2, service3));
        
        assertEquals("All services must be non-null", exception.getMessage());
    }
    
    @Test
    void givenEmptyArray_whenRequireAllNonNull_thenNoException() {
        assertDoesNotThrow(() -> ServiceValidator.requireAllNonNull());
    }
    
    @Test
    void givenSingleService_whenRequireAllNonNull_thenNoException() {
        Object service = new Object();
        
        assertDoesNotThrow(() -> ServiceValidator.requireAllNonNull(service));
    }

    @Test
    void givenAllValidServices_whenValidateItemServices_thenNoException() {
        assertDoesNotThrow(() -> 
            ServiceValidator.validateItemServices(mockBookService, mockCDService, mockJournalService));
    }
    
    @Test
    void givenNullBookService_whenValidateItemServices_thenThrowIllegalArgument() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> ServiceValidator.validateItemServices(null, mockCDService, mockJournalService));
        
        assertEquals("BookService cannot be null", exception.getMessage());
    }
    
    @Test
    void givenNullCDService_whenValidateItemServices_thenThrowIllegalArgument() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> ServiceValidator.validateItemServices(mockBookService, null, mockJournalService));
        
        assertEquals("CDService cannot be null", exception.getMessage());
    }
    
    @Test
    void givenNullJournalService_whenValidateItemServices_thenThrowIllegalArgument() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> ServiceValidator.validateItemServices(mockBookService, mockCDService, null));
        
        assertEquals("JournalService cannot be null", exception.getMessage());
    }
    
    @Test
    void givenAllNullServices_whenValidateItemServices_thenThrowIllegalArgument() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> ServiceValidator.validateItemServices(null, null, null));
        
        assertEquals("BookService cannot be null", exception.getMessage());
    }

    @Test
    void givenAllValidServices_whenValidateLoanServices_thenNoException() {
        assertDoesNotThrow(() -> 
            ServiceValidator.validateLoanServices(mockLoanService, mockLoanStatsService, mockLoanQueryService));
    }
    
    @Test
    void givenNullLoanService_whenValidateLoanServices_thenThrowIllegalArgument() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> ServiceValidator.validateLoanServices(null, mockLoanStatsService, mockLoanQueryService));
        
        assertEquals("LoanService cannot be null", exception.getMessage());
    }
    
    @Test
    void givenNullLoanStatsService_whenValidateLoanServices_thenThrowIllegalArgument() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> ServiceValidator.validateLoanServices(mockLoanService, null, mockLoanQueryService));
        
        assertEquals("LoanStatsService cannot be null", exception.getMessage());
    }
    
    @Test
    void givenNullLoanQueryService_whenValidateLoanServices_thenThrowIllegalArgument() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> ServiceValidator.validateLoanServices(mockLoanService, mockLoanStatsService, null));
        
        assertEquals("LoanQueryService cannot be null", exception.getMessage());
    }

    @Test
    void givenAllValidServices_whenValidateUserServices_thenNoException() {
        assertDoesNotThrow(() -> 
            ServiceValidator.validateUserServices(mockUserService, mockAuthService));
    }
    
    @Test
    void givenNullUserService_whenValidateUserServices_thenThrowIllegalArgument() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> ServiceValidator.validateUserServices(null, mockAuthService));
        
        assertEquals("UserService cannot be null", exception.getMessage());
    }
    
    @Test
    void givenNullAuthService_whenValidateUserServices_thenThrowIllegalArgument() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> ServiceValidator.validateUserServices(mockUserService, null));
        
        assertEquals("AuthService cannot be null", exception.getMessage());
    }
    
    @Test
    void givenBothServicesNull_whenValidateUserServices_thenThrowIllegalArgument() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> ServiceValidator.validateUserServices(null, null));
        
        assertEquals("UserService cannot be null", exception.getMessage());
    }

    @Test
    void givenAllValidGroups_whenValidateServiceGroups_thenNoException() {
        assertDoesNotThrow(() -> 
            ServiceValidator.validateServiceGroups(mockItemServices, mockLoanServices, mockUserServices));
    }
    
    @Test
    void givenNullItemServices_whenValidateServiceGroups_thenThrowIllegalArgument() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> ServiceValidator.validateServiceGroups(null, mockLoanServices, mockUserServices));
        
        assertEquals("ItemManagementServices cannot be null", exception.getMessage());
    }
    
    @Test
    void givenNullLoanServices_whenValidateServiceGroups_thenThrowIllegalArgument() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> ServiceValidator.validateServiceGroups(mockItemServices, null, mockUserServices));
        
        assertEquals("LoanManagementServices cannot be null", exception.getMessage());
    }
    
    @Test
    void givenNullUserServices_whenValidateServiceGroups_thenThrowIllegalArgument() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> ServiceValidator.validateServiceGroups(mockItemServices, mockLoanServices, null));
        
        assertEquals("UserManagementServices cannot be null", exception.getMessage());
    }
    
    @Test
    void givenAllNullGroups_whenValidateServiceGroups_thenThrowIllegalArgument() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> ServiceValidator.validateServiceGroups(null, null, null));
        
        assertEquals("ItemManagementServices cannot be null", exception.getMessage());
    }
}
