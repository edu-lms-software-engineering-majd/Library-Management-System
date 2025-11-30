package lms.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lms.application.UserDTO;
import lms.domain.exception.PasswordReuseException;
import lms.domain.utils.PasswordUtils;

class UserTest {

    private User user;
    private String validPassword;

    @BeforeEach
    void setUp() {
        validPassword = "StrongPass1!";
        user = new User("Majd", "Awwad", "majd@gmail.com", "majdawwad", 
                       PasswordUtils.hashPassword(validPassword), Role.ADMIN);
    }

    @Test
    void shouldCreateUserWithValidData() {
        assertNotNull(user);
        assertEquals("Majd", user.getFirstName());
        assertEquals("Awwad", user.getLastName());
        assertEquals("majd@gmail.com", user.getEmail());
        assertEquals("majdawwad", user.getUsername());
        assertEquals(Role.ADMIN, user.getRole());
        assertNotNull(user.getUserID());
        assertNotNull(user.getRegistrationDate());
        assertNotNull(user.getAccount());
    }

    @Test
    void shouldThrowExceptionWhenFirstNameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> 
            new User(null, "Awwad", "email@example.com", "user", PasswordUtils.hashPassword("Pass123!"), Role.MEMBER)
        );
    }

    @Test
    void shouldThrowExceptionWhenFirstNameIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> 
            new User("", "Awwad", "email@example.com", "user", PasswordUtils.hashPassword("Pass123!"), Role.MEMBER)
        );
    }

    @Test
    void shouldThrowExceptionWhenLastNameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> 
            new User("Majd", null, "email@example.com", "user", PasswordUtils.hashPassword("Pass123!"), Role.MEMBER)
        );
    }

    @Test
    void shouldThrowExceptionWhenLastNameIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> 
            new User("Majd", "", "email@example.com", "user", PasswordUtils.hashPassword("Pass123!"), Role.MEMBER)
        );
    }

    @Test
    void shouldThrowExceptionWhenEmailIsNull() {
        assertThrows(IllegalArgumentException.class, () -> 
            new User("Majd", "Awwad", null, "user", PasswordUtils.hashPassword("Pass123!"), Role.MEMBER)
        );
    }

    @Test
    void shouldThrowExceptionWhenEmailIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> 
            new User("Majd", "Awwad", "invalidemail", "user", PasswordUtils.hashPassword("Pass123!"), Role.MEMBER)
        );
    }

    @Test
    void shouldThrowExceptionWhenUsernameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> 
            new User("Majd", "Awwad", "email@example.com", null, PasswordUtils.hashPassword("Pass123!"), Role.MEMBER)
        );
    }

    @Test
    void shouldThrowExceptionWhenUsernameIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> 
            new User("Majd", "Awwad", "email@example.com", "", PasswordUtils.hashPassword("Pass123!"), Role.MEMBER)
        );
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsNull() {
        assertThrows(IllegalArgumentException.class, () -> 
            new User("Majd", "Awwad", "email@example.com", "user", null, Role.MEMBER)
        );
    }

    @Test
    void shouldThrowExceptionWhenRoleIsNull() {
        assertThrows(IllegalArgumentException.class, () -> 
            new User("Majd", "Awwad", "email@example.com", "user", PasswordUtils.hashPassword("Pass123!"), null)
        );
    }

    @Test
    void shouldReturnCorrectFullName() {
        assertEquals("Majd Awwad", user.getFullName());
    }

    @Test
    void shouldReturnRegistrationDateAsToday() {
        assertEquals(LocalDate.now(), user.getRegistrationDate());
    }

    @Test
    void shouldConvertToDTO() {
        UserDTO dto = user.toDTO();
        
        assertNotNull(dto);
        assertEquals(user.getUserID(), dto.userID());
        assertEquals(user.getUsername(), dto.username());
        assertEquals(user.getFirstName(), dto.firstName());
        assertEquals(user.getLastName(), dto.lastName());
        assertEquals(user.getRole(), dto.role());
    }

    @Test
    void shouldChangePasswordSuccessfully() {
        String newPassword = "NewStrongPass2@";
        
        assertDoesNotThrow(() -> user.changePassword(newPassword));
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsTooShort() {
        assertThrows(IllegalArgumentException.class, () -> 
            user.changePassword("short")
        );
    }

    @Test
    void shouldThrowExceptionWhenChangingPasswordToNull() {
        assertThrows(IllegalArgumentException.class, () -> 
            user.changePassword(null)
        );
    }

    @Test
    void shouldThrowExceptionWhenReusingPassword() {
        assertThrows(PasswordReuseException.class, () -> 
            user.changePassword(validPassword)
        );
    }

    @Test
    void shouldChangeRoleSuccessfully() {
        user.changeRole(Role.ADMIN);
        
        assertEquals(Role.ADMIN, user.getRole());
    }

    @Test
    void shouldThrowExceptionWhenRoleIsNullInChangeRole() {
        assertThrows(IllegalArgumentException.class, () -> 
            user.changeRole(null)
        );
    }

    @Test
    void shouldChangeEmailSuccessfully() {
        String newEmail = "newemail@example.com";
        
        user.changeEmail(newEmail);
        
        assertEquals(newEmail, user.getEmail());
    }

    @Test
    void shouldThrowExceptionWhenChangingToInvalidEmail() {
        assertThrows(IllegalArgumentException.class, () -> 
            user.changeEmail("invalidemail")
        );
    }

    @Test
    void shouldThrowExceptionWhenChangingToNullEmail() {
        assertThrows(IllegalArgumentException.class, () -> 
            user.changeEmail(null)
        );
    }

    @Test
    void shouldUpdateNameSuccessfully() {
        user.updateName("Ahmad", "Salem");
        
        assertEquals("Ahmad", user.getFirstName());
        assertEquals("Salem", user.getLastName());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingWithNullFirstName() {
        assertThrows(IllegalArgumentException.class, () -> 
            user.updateName(null, "Salem")
        );
    }

    @Test
    void shouldThrowExceptionWhenUpdatingWithNullLastName() {
        assertThrows(IllegalArgumentException.class, () -> 
            user.updateName("Ahmad", null)
        );
    }

    @Test
    void shouldReturnFalseForHasFineWhenNoFines() {
        assertFalse(user.hasFine());
    }

    @Test
    void shouldReturnTrueForCanBorrowWhenNoLoansOrFines() {
        assertTrue(user.canBorrow());
    }

    @Test
    void shouldReturnFalseForCanBorrowWhenAtBorrowLimit() {
        for (int i = 0; i < 10; i++) {
            Loan loan = new Loan(user.getUserID(), UUID.randomUUID(), "book", LocalDate.now());
            user.addLoan(loan);
        }
        
        assertFalse(user.canBorrow());
    }

    @Test
    void shouldAddLoanSuccessfully() {
        Loan loan = new Loan(user.getUserID(), UUID.randomUUID(), "book", LocalDate.now());
        
        user.addLoan(loan);
        
        assertEquals(1, user.getActiveLoanCount());
        assertTrue(user.getLoans().contains(loan));
    }

    @Test
    void shouldThrowExceptionWhenAddingNullLoan() {
        assertThrows(IllegalArgumentException.class, () -> 
            user.addLoan(null)
        );
    }

    @Test
    void shouldRemoveLoanSuccessfully() {
        Loan loan = new Loan(user.getUserID(), UUID.randomUUID(), "book", LocalDate.now());
        user.addLoan(loan);
        
        user.removeLoan(loan);
        
        assertEquals(0, user.getActiveLoanCount());
        assertFalse(user.getLoans().contains(loan));
    }

    @Test
    void shouldReturnImmutableLoansList() {
        List<Loan> loans = user.getLoans();
        
        assertThrows(UnsupportedOperationException.class, () -> 
            loans.add(new Loan(user.getUserID(), UUID.randomUUID(), "book", LocalDate.now()))
        );
    }

    @Test
    void shouldReturnZeroForActiveLoanCountWhenNoLoans() {
        assertEquals(0, user.getActiveLoanCount());
    }

    @Test
    void shouldReturnCorrectActiveLoanCount() {
        user.addLoan(new Loan(user.getUserID(), UUID.randomUUID(), "book", LocalDate.now()));
        user.addLoan(new Loan(user.getUserID(), UUID.randomUUID(), "book", LocalDate.now()));
        
        assertEquals(2, user.getActiveLoanCount());
    }

    @Test
    void shouldReturnFalseForIsAtBorrowLimitWhenNoLoans() {
        assertFalse(user.isAtBorrowLimit());
    }

    @Test
    void shouldReturnTrueForIsAtBorrowLimitWhenAtLimit() {
        for (int i = 0; i < 10; i++) {
            user.addLoan(new Loan(user.getUserID(), UUID.randomUUID(), "book", LocalDate.now()));
        }
        
        assertTrue(user.isAtBorrowLimit());
    }

    @Test
    void shouldAddNotificationSuccessfully() {
        Notification notification = new Notification("Test message", UUID.randomUUID(), NotificationType.OVERDUE);
        
        user.addNotification(notification);
        
        assertEquals(1, user.getUnreadNotificationCount());
        assertTrue(user.getUnreadNotifications().contains(notification));
    }

    @Test
    void shouldThrowExceptionWhenAddingNullNotification() {
        assertThrows(IllegalArgumentException.class, () -> 
            user.addNotification(null)
        );
    }

    @Test
    void shouldMarkNotificationAsRead() {
        Notification notification = new Notification("Test message", UUID.randomUUID(), NotificationType.OVERDUE);
        user.addNotification(notification);
        
        user.markAsRead(notification);
        
        assertEquals(0, user.getUnreadNotificationCount());
        assertTrue(user.getReadNotifications().contains(notification));
        assertFalse(user.getUnreadNotifications().contains(notification));
    }

    @Test
    void shouldReturnEmptyListForUnreadNotificationsWhenNone() {
        assertTrue(user.getUnreadNotifications().isEmpty());
    }

    @Test
    void shouldReturnEmptyListForReadNotificationsWhenNone() {
        assertTrue(user.getReadNotifications().isEmpty());
    }

    @Test
    void shouldReturnImmutableUnreadNotificationsList() {
        List<Notification> notifications = user.getUnreadNotifications();
        
        assertThrows(UnsupportedOperationException.class, () -> 
            notifications.add(new Notification("Test", UUID.randomUUID(), NotificationType.OVERDUE))
        );
    }

    @Test
    void shouldReturnImmutableReadNotificationsList() {
        List<Notification> notifications = user.getReadNotifications();
        
        assertThrows(UnsupportedOperationException.class, () -> 
            notifications.add(new Notification("Test", UUID.randomUUID(), NotificationType.OVERDUE))
        );
    }

    @Test
    void shouldReturnZeroForUnreadNotificationCountWhenNone() {
        assertEquals(0, user.getUnreadNotificationCount());
    }

    @Test
    void shouldReturnCorrectUnreadNotificationCount() {
        user.addNotification(new Notification("Test1", UUID.randomUUID(), NotificationType.OVERDUE));
        user.addNotification(new Notification("Test2", UUID.randomUUID(), NotificationType.DUE_SOON));
        
        assertEquals(2, user.getUnreadNotificationCount());
    }

    @Test
    void shouldReturnFalseForHasUnreadNotificationsWhenNone() {
        assertFalse(user.hasUnreadNotifications());
    }

    @Test
    void shouldReturnTrueForHasUnreadNotificationsWhenPresent() {
        user.addNotification(new Notification("Test", UUID.randomUUID(), NotificationType.OVERDUE));
        
        assertTrue(user.hasUnreadNotifications());
    }

    @Test
    void shouldSetEmailThroughSetter() {
        String newEmail = "updated@example.com";
        
        user.setEmail(newEmail);
        
        assertEquals(newEmail, user.getEmail());
    }

    @Test
    void shouldSetFirstNameThroughSetter() {
        user.setFirstName("NewFirstName");
        
        assertEquals("NewFirstName", user.getFirstName());
    }

    @Test
    void shouldSetLastNameThroughSetter() {
        user.setLastName("NewLastName");
        
        assertEquals("NewLastName", user.getLastName());
    }

    @Test
    void shouldCreateUserWithLoansAndAccount() {
        Account account = new Account(UUID.randomUUID());
        Loan loan = new Loan(user.getUserID(), UUID.randomUUID(), "book", LocalDate.now());
        List<Loan> loans = List.of(loan);
        
        User userWithData = new User("Sara", "Ali", "sara@example.com", "saraali", 
                                     PasswordUtils.hashPassword("SecurePass3#"), Role.MEMBER, loans, account);
        
        assertNotNull(userWithData);
        assertEquals(account, userWithData.getAccount());
    }

    @Test
    void shouldNotMarkNotificationAsReadIfNotInUnreadList() {
        Notification notification = new Notification("Test", UUID.randomUUID(), NotificationType.OVERDUE);
        
        user.markAsRead(notification);
        
        assertFalse(user.getReadNotifications().contains(notification));
    }

    @Test
    void shouldMaintainConsistentStateAfterMultipleOperations() {
        user.addLoan(new Loan(user.getUserID(), UUID.randomUUID(), "book", LocalDate.now()));
        user.addNotification(new Notification("Test", UUID.randomUUID(), NotificationType.OVERDUE));
        user.changeEmail("new@example.com");
        user.updateName("UpdatedFirst", "UpdatedLast");
        user.changeRole(Role.LIBRARIAN);
        
        assertEquals(1, user.getActiveLoanCount());
        assertEquals(1, user.getUnreadNotificationCount());
        assertEquals("new@example.com", user.getEmail());
        assertEquals("UpdatedFirst", user.getFirstName());
        assertEquals("UpdatedLast", user.getLastName());
        assertEquals(Role.LIBRARIAN, user.getRole());
    }
}