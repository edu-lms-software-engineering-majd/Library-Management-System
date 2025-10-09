package lms.presentation;

import java.util.Scanner;

/**
 * Command-Line Interface (CLI) for regular library users.
 *
 * <p>
 * The {@code UserCLI} provides a text-based interface where
 * non-admin users can interact with the Library Management System.
 * It presents a role-specific menu after login and allows
 * typical user operations.
 * </p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 *   <li>Display the user menu with available operations</li>
 *   <li>Allow searching for books</li>
 *   <li>Allow borrowing and returning of books</li>
 *   <li>Allow users to pay fines</li>
 *   <li>Provide a logout option</li>
 * </ul>
 *
 * <h2>Current Status:</h2>
 * <p>
 * The current implementation is a <b>simulation</b>:
 * operations only print console messages. Later, these
 * handlers should be connected with real services
 * (e.g. {@code BookService}, {@code UserService}) in the
 * application layer.
 * </p>
 *
 * <h2>Example Usage:</h2>
 * <pre>{@code
 * UserCLI userCLI = new UserCLI();
 * userCLI.start();
 * }</pre>
 *
 * <p>
 * This class belongs to the <b>presentation layer</b>.
 * It does not directly interact with persistence or
 * domain objects, ensuring separation of concerns.
 * </p>
 *
 * @author Majd Awwad
 * @version 2.0
 */
public class UserCLI implements CLI {

    /** Scanner for reading user input from the console */
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Starts the user menu loop.
     *
     * <p>Options available:</p>
     * <ul>
     *   <li>1 - Search for a Book</li>
     *   <li>2 - Borrow a Book</li>
     *   <li>3 - Return a Book</li>
     *   <li>4 - Pay Fine</li>
     *   <li>5 - Logout</li>
     * </ul>
     *
     * The loop continues until the user chooses to log out.
     */
    public void start() {
        while (true) {
            showUserMenu();
            int choice = scanner.nextInt();
            scanner.nextLine();

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

    /** Displays the menu options for the user.*/
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
		// TODO: write the implementation of this method

        System.out.print("Enter keyword to search: ");
        String keyword = scanner.nextLine();
        System.out.println("Searching for books with keyword: " + keyword + " (simulation).");
    }

    /**
     * Handles the borrow book process.
     * <p>Currently simulated by printing a message with the entered book ID.</p>
     */
    private void handleBorrowBook() {
		// TODO: write the implementation of this method

        System.out.print("Enter book ID to borrow: ");
        String bookId = scanner.nextLine();
        System.out.println("Book with ID " + bookId + " borrowed successfully (simulation).");
    }

    /**
     * Handles the return book process.
     * <p>Currently simulated by printing a message with the entered book ID.</p>
     */
    private void handleReturnBook() {
		// TODO: write the implementation of this method

        System.out.print("Enter book ID to return: ");
        String bookId = scanner.nextLine();
        System.out.println("Book with ID " + bookId + " returned successfully (simulation).");
    }

    /**
     * Handles the process of paying fines.
     * <p>Currently simulated by printing a confirmation message with the entered amount.</p>
     */
    private void handlePayFine() {
		// TODO: write the implementation of this method

        System.out.print("Enter amount to pay: ");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // consume newline
        System.out.println("Fine of $" + amount + " paid successfully (simulation).");
    }
}
