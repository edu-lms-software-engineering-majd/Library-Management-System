package lms.application;

import lms.domain.User;
import lms.domain.UserRepo;
import lms.domain.exception.InvalidPasswordException;
import lms.domain.exception.UserNotFoundException;

/**
 * Service responsible for handling authentication operations
 * such as login, logout, and retrieving the currently logged-in user.
 * 
 * <p>Example usage:</p>
 * <pre>
 * AuthService authService = new AuthService(userRepo);
 * authService.login("john_doe", "password123");
 * User current = authService.getCurrentUser();
 * authService.logout();
 * </pre>
 * 
 * <p>Note: This implementation maintains a single current user in memory for each thread, also it's not a thread safe class.</p>
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public class AuthService {
    
    /** Repository used to access and manage users */
    private final UserRepo userRepo;

    /** Currently logged-in user, or null if no user is logged in */
    private User currentUser;

    /**
     * Constructs an {@code AuthService} with the specified user repository.
     * 
     * @param userRepo the repository used to retrieve user data
     */
    public AuthService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Attempts to log in a user with the given username and password.
     * 
     * @param userName the username of the user
     * @param rawPassword the plain text password to verify
     * @return {@code true} if login is successful
     * @throws UserNotFoundException if no user exists with the specified username
     * @throws InvalidPasswordException if the password is incorrect
     */
    public boolean login(String userName, String rawPassword) 
            throws UserNotFoundException, InvalidPasswordException {
        User user = userRepo.getUserByUserName(userName);

        if (user == null) {
            throw new UserNotFoundException("User '" + userName + "' does not exist.");
        }
        

        if (!user.verifyPassword(rawPassword)) {
            throw new InvalidPasswordException("Incorrect password.");
        }
        
        currentUser = user;
        return true;
    }

    /**
     * Logs out the currently logged-in user.
     * 
     * @throws IllegalStateException if no user is currently logged in
     */
    public void logout() throws IllegalStateException {
        if (currentUser == null) {
            throw new IllegalStateException("No user is logged in");
        }
        currentUser = null;
    }

    /**
     * Returns the currently logged-in user.
     * 
     * @return the {@link User} object of the current user, or {@code null} if no user is logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }
}
