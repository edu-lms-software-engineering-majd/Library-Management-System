package lms.Presentation;

import lms.application.AuthService;
import lms.persistence.StaticUserRepo;

/**
 * Entry point for the Library Management System (LMS).
 *
 * <p>This class initializes the required components such as the
 * {@link StaticUserRepo} and {@link AuthService}, and then
 * launches the main command-line interface {@link LibraryCLI}.</p>
 *
 * <p><b>Usage:</b></p>
 * <pre>
 *   java lms.Presentation.LibraryApp
 * </pre>
 *
 * <p>After execution, the user will see the main menu and can
 * log in as an admin or a user depending on credentials.</p>
 *
 * @author Ahmed
 * @version 1.0
 */
public class LibraryApp {

    /**
     * Main method that starts the Library Management System.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        // create repository and auth service
        StaticUserRepo repo = new StaticUserRepo();
        AuthService authService = new AuthService(repo);

        // start main menu
        LibraryCLI cli = new LibraryCLI(authService);
        cli.start();
    }
}
