package lms.Presentation;

import java.util.Scanner;

/**
 * Command Line Interface (CLI) for library users.
 *
 * <p>The {@code UserCLI} class provides a simple text-based
 * menu for regular users to interact with the Library Management System.
 * Users can search for books, borrow, return, and pay fines.</p>
 *
 * <p>Currently, the implementation is a simulation with console messages,
 * and should later be connected with real services in the application layer.</p>
 *
 * <p><b>Example usage:</b></p>
 * <pre>
 * UserCLI userCLI = new UserCLI();
 * userCLI.start();
 * </pre>
 *
 * @author أحمد
 * @version 1.0
 */
public class UserCLI {

    /** Scanner for reading user input from the console */
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Starts the user menu loop.
     * <p>Options available:</p>
     * <ul>
     *   <li>1 - Search for a Book</li>
     *   <li>2 - Borrow a Book</li>
     *   <li>3 - Return a Book</li>
     *   <li>4 - Pay Fine</li>
     *   <li>5 - Logout</li>
     * </ul>
     * The loop continues until the user chooses to logout.
     */
    public void start() {
        while (true) {
            showUserMenu();
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    handleSearchBook();
                    break;
                case 2:
                    handleBorrowBook();
                    break;
                case 3:
                    handleReturnBook();
                    break;
                case 4:
                    handlePayFine();
                    break;
                case 5:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    /**
     * Displays the menu options for the user.
     */
    private void showUserMenu() {
        System.out.println("\n===== User Menu =====");
        System.out.println("1. Search for a Book");
        System.out.println("2. Borrow a Book");
        System.out.println("3. Return a Book");
        System.out.println("4. Pay Fine");
        System.out.println("5. Logout");
        System.out.print("Choose: ");
    }

    /**
     * Handles the search book process.
     * <p>Currently simulated by printing a message with the keyword entered.</p>
     */
    private void handleSearchBook() {
        System.out.print("Enter keyword to search: ");
        String keyword = scanner.nextLine();
        System.out.println("Searching for books with keyword: " + keyword + " (simulation).");
    }

    /**
     * Handles the borrow book process.
     * <p>Currently simulated by printing a message with the entered book ID.</p>
     */
    private void handleBorrowBook() {
        System.out.print("Enter book ID to borrow: ");
        String bookId = scanner.nextLine();
        System.out.println("Book with ID " + bookId + " borrowed successfully (simulation).");
    }

    /**
     * Handles the return book process.
     * <p>Currently simulated by printing a message with the entered book ID.</p>
     */
    private void handleReturnBook() {
        System.out.print("Enter book ID to return: ");
        String bookId = scanner.nextLine();
        System.out.println("Book with ID " + bookId + " returned successfully (simulation).");
    }

    /**
     * Handles the process of paying fines.
     * <p>Currently simulated by printing a confirmation message with the entered amount.</p>
     */
    private void handlePayFine() {
        System.out.print("Enter amount to pay: ");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // consume newline
        System.out.println("Fine of $" + amount + " paid successfully (simulation).");
    }
}
