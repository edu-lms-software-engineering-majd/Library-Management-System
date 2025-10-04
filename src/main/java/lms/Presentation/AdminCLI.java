package lms.Presentation;

import java.util.Scanner;

/**
 * Command-Line Interface (CLI) for administrator operations in the Library Management System.
 *
 * <p>This class provides a simple text-based menu that allows an administrator
 * to perform actions such as adding books, adding users, and viewing reports.</p>
 *
 * <p><b>Example usage:</b></p>
 * <pre>
 * AdminCLI adminCLI = new AdminCLI();
 * adminCLI.start();
 * </pre>
 *
 * <p>Note: This is a simulation and currently does not integrate with real services
 * like BookService or UserService. Instead, it prints success messages to the console.</p>
 *
 * @author Ahmed
 * @version 1.0
 */
public class AdminCLI {

    /** Scanner for reading input from the console */
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Starts the admin menu loop.
     * Displays the menu, handles user input, and executes actions
     * until the admin chooses to log out.
     */
    public void start() {
        while (true) {
            showAdminMenu();
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    handleAddBook();
                    break;
                case 2:
                    handleAddUser();
                    break;
                case 3:
                    handleViewReports();
                    break;
                case 4:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    /**
     * Displays the administrator menu options.
     */
    private void showAdminMenu() {
        System.out.println("\n===== Admin Panel =====");
        System.out.println("1. Add Book");
        System.out.println("2. Add User");
        System.out.println("3. View Reports");
        System.out.println("4. Logout");
        System.out.print("Choose: ");
    }

    /**
     * Handles the process of adding a book.
     * Currently only simulates the process by asking for input and printing confirmation.
     */
    private void handleAddBook() {
        System.out.print("Enter book title: ");
        String title = scanner.nextLine();

        System.out.print("Enter author: ");
        String author = scanner.nextLine();

        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine();

        System.out.println("Book '" + title + "' by " + author + " (ISBN: " + isbn + ") added successfully (simulation).");
    }

    /**
     * Handles the process of adding a new user.
     * Currently only simulates the process by asking for input and printing confirmation.
     */
    private void handleAddUser() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        System.out.println("User '" + username + "' with email " + email + " added successfully (simulation).");
    }

    /**
     * Displays a report of books and users.
     * Currently only prints simulated values to the console.
     */
    private void handleViewReports() {
        System.out.println("Reports: (simulation)");
        System.out.println("Total Books: 100");
        System.out.println("Total Users: 25");
    }
}
