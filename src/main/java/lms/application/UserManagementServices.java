package lms.application;

/**
 * Groups services for user and authentication management.
 * This includes handling user accounts and login/logout operations.
 * 
 * These services work closely together since authentication depends on user data,
 * so it makes sense to group them as one unit.
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class UserManagementServices {
    private final UserService userService;
    private final AuthService authService;

    public UserManagementServices(UserService userService, AuthService authService) {
        ServiceValidator.validateUserServices(userService, authService);
        this.userService = userService;
        this.authService = authService;
    }

    public UserService getUserService() {
        return userService;
    }

    public AuthService getAuthService() {
        return authService;
    }
}
