package lms.presentation;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import lms.application.BookService;
import lms.application.LoanService;
import lms.application.UserDTO;
import lms.domain.Book;
import lms.application.AuthService;
import java.util.UUID;
import lms.domain.Loan;

/**
 * Command-Line Interface (CLI) for regular library users.
 *
 * <p>
 * The {@code UserCLI} provides a text-based interface where non-admin users can
 * interact with the Library Management System. It presents a role-specific menu
 * after login and allows typical user operations.
 * </p>
 *
 * <h2>Responsibilities:</h2>
 * <ul>
 * <li>Display the user menu with available operations</li>
 * <li>Allow searching for books</li>
 * <li>Allow borrowing and returning of books</li>
 * <li>Allow users to pay fines</li>
 * <li>Provide a logout option</li>
 * </ul>
 *
 * <h2>Current Status:</h2>
 * <p>
 * The current implementation is a <b>simulation</b>: operations only print
 * console messages. Later, these handlers should be connected with real
 * services (e.g. {@code BookService}, {@code UserService}) in the application
 * layer.
 * </p>
 *
 * <h2>Example Usage:</h2>
 * 
 * <pre>{@code
 * UserCLI userCLI = new UserCLI();
 * userCLI.start();
 * }</pre>
 *
 * <p>
 * This class belongs to the <b>presentation layer</b>. It does not directly
 * interact with persistence or domain objects, ensuring separation of concerns.
 * </p>
 *
 * @author Majd Awwad
 * @version 2.0
 * 
 */
public class UserCLI implements CLI {

	/** Scanner for reading user input from the console */
	private final Scanner scanner = new Scanner(System.in);

	private final BookService bookService;// constructor to BookService
	private final LoanService loanService;
	public UserCLI(BookService bookService, LoanService loanService) {
		this.bookService = bookService;
		this.loanService = loanService;
	}

	/**
	 * Starts the user menu loop.
	 *
	 * <p>
	 * Options available:
	 * </p>
	 * <ul>
	 * <li>1 - Search for a Book</li>
	 * <li>2 - Borrow a Book</li>
	 * <li>3 - Return a Book</li>
	 * <li>4 - Pay Fine</li>
	 * <li>5 - Logout</li>
	 * </ul>
	 *
	 * The loop continues until the user chooses to log out.
	 */
	public void start() {
		UserDTO currentUser = AuthService.getCurrentUser();
	    System.out.println("\n=========================================");
	    System.out.println("    Welcome, " + currentUser.firstName() + " " + currentUser.lastName());
	    System.out.println("    Role: " + currentUser.role());
	    System.out.println("=========================================");
		
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
		        handleViewMyLoans();
		        break;

			case 6:
				System.out.println("Logging out...");
				return;
			default:
				System.out.println("Invalid choice! Please try again.");
			}
		}
	}
	
	
	/**
	 * Displays the user's current active loans.
	 */
	private void handleViewMyLoans() {
	    System.out.println("\n=== My Active Loans ===");
	    
	    UUID userId = AuthService.getCurrentUser().userID();
	    List<Loan> activeLoans = loanService.getUserActiveLoans(userId);

	    if (activeLoans.isEmpty()) {
	        System.out.println("You have no active loans.");
	        return;
	    }

<<<<<<< HEAD
	    displayActiveLoans(activeLoans);
	}
	
	
	private void displayActiveLoans(List<Loan> loans) {
	 
		// TODO : Active Loans Implementation now is Limited To Books, Need re-implementation
		
	    System.out.println("  No \t\t Book Title \t \t Borrowed \t\t Due Date\t\tStatus");
	    
||||||| 7160386
        // Defensive check
        if (keyword.isEmpty()) {
            System.out.println("⚠️ Keyword cannot be empty. Please try again.");
            return;
        }
=======
	    displayActiveLoans(activeLoans);
	}
	
	
	private void displayActiveLoans(List<Loan> loans) {
	 
 
		// TODO : Active Loans Implementation now is Limited To Books, Need re-implementation
		
 
	    System.out.println("  No \t\t Book Title \t \t Borrowed \t\t Due Date\t\tStatus");
	    
>>>>>>> ahmad-salameh

<<<<<<< HEAD
	    for (int i = 0; i < loans.size(); i++) {
	        Loan loan = loans.get(i);
	        Book book = bookService.getAllBooks().stream()
	                .filter(b -> b.getBookId().equals(loan.getItemId()))
	                .findFirst()
	                .orElse(null);
||||||| 7160386
        List<Book> allBooks = bookService.getAllBooks();
        List<Book> results = new ArrayList<>();
=======
	    for (int i = 0; i < loans.size(); i++) {
	        Loan loan = loans.get(i);
	        Book book = bookService.getAllBooks().stream()
 
	                .filter(b -> b.getBookId().equals(loan.getItemId()))
 
	                .findFirst()
	                .orElse(null);
>>>>>>> ahmad-salameh

	        String title = (book != null) ? 
	                (book.getTitle().length() > 28 ? book.getTitle().substring(0, 25) + "..." : book.getTitle()) 
	                : "Unknown Book";
	        
	        String status = loan.isOverdue() ? "OVERDUE " : "Active ";

	        System.out.printf("│ %-3d │ %-28s │ %-12s │ %-10s │ %-8s │\n",
	                i + 1, title, loan.getBorrowDate(), loan.getDueDate(), status);
	    }
	    System.out.println("****************************************************");
	}
	
	

	/** Displays the menu options for the user. */
	private void showUserMenu() {
		System.out.println("\n===== User Menu =====");
		System.out.println("1. Search for a Book");
		System.out.println("2. Borrow a Book");
		System.out.println("3. Return a Book");
		System.out.println("4.View My Loans");
		System.out.println("5. Pay Fine");
		System.out.println("6. Logout");
		System.out.print("Choose: ");
	}

	/**
	 * Handles the search book process.
	 * <p>
	 * Currently simulated by printing a message with the keyword entered.
	 * </p>
	 */
	private void handleSearchBook() {
		System.out.println("\n=== Search Book Menu ===");
		System.out.println("1. Search by Title");
		System.out.println("2. Search by Author");
		System.out.println("3. Search by ISBN");
		System.out.println("4. Search by Publisher");
		System.out.println("5. Search by Publishing Year");
		System.out.print("Choose an option (1-5): ");

		String choice = scanner.nextLine().trim();
		System.out.print("Enter keyword: ");
		String keyword = scanner.nextLine().trim().toLowerCase();

		// Defensive check
		if (keyword.isEmpty()) {
			System.out.println("⚠️ Keyword cannot be empty. Please try again.");
			return;
		}

		List<Book> allBooks = bookService.getAllBooks();
		List<Book> results = new ArrayList<>();

		switch (choice) {
		case "1": // Title
			results = allBooks.stream().filter(b -> b.getTitle().toLowerCase().contains(keyword))
					.collect(Collectors.toList());
			break;

		case "2": // Author
			results = allBooks.stream().filter(b -> b.getAuthor().toLowerCase().contains(keyword))
					.collect(Collectors.toList());
			break;

		case "3": // ISBN
			results = allBooks.stream().filter(b -> b.getIsbn().toLowerCase().contains(keyword))
					.collect(Collectors.toList());
			break;

		case "4": // Publisher
			results = allBooks.stream().filter(b -> b.getPublisher().toLowerCase().contains(keyword))
					.collect(Collectors.toList());
			break;

		case "5": // Publishing Year
			try {
				int year = Integer.parseInt(keyword);
				results = allBooks.stream().filter(b -> b.getPublicationYear() == year).collect(Collectors.toList());
			} catch (NumberFormatException e) {
				System.out.println("⚠️ Invalid year format. Please enter a numeric year.");
				return;
			}
			break;

		default:
			System.out.println("❌ Invalid choice. Please select a number between 1 and 5.");
			return;
		}

		if (results.isEmpty()) {
			System.out.println("❌ No books found matching your search.");
		} else {
			System.out.println("\n✅ Found " + results.size() + " book(s):");
			results.forEach(book -> {
				System.out.println("- " + book.getTitle() + " by " + book.getAuthor() + " (" + book.getPublicationYear()
						+ "), ISBN: " + book.getIsbn());
			});
		}
	}

	/**
	 * Handles the borrow book process.
	 * <p>
	 * Currently simulated by printing a message with the entered book ID.
	 * </p>
	 */
	private void handleBorrowBook() {
		// TODO: write the implementation of this method

		System.out.print("Enter book ID to borrow: ");
		String bookId = scanner.nextLine();
		System.out.println("Book with ID " + bookId + " borrowed successfully (simulation).");
	}

	/**
	 * Handles the return book process.
	 * <p>
	 * Currently simulated by printing a message with the entered book ID.
	 * </p>
	 */
	private void handleReturnBook() {
		// TODO: write the implementation of this method

		System.out.print("Enter book ID to return: ");
		String bookId = scanner.nextLine();
		System.out.println("Book with ID " + bookId + " returned successfully (simulation).");
	}

	/**
	 * Handles the process of paying fines.
	 * <p>
	 * Currently simulated by printing a confirmation message with the entered
	 * amount.
	 * </p>
	 */
	private void handlePayFine() {
		// TODO: write the implementation of this method

		System.out.print("Enter amount to pay: ");
		double amount = scanner.nextDouble();
		scanner.nextLine(); // consume newline
		System.out.println("Fine of $" + amount + " paid successfully (simulation).");
	}
}
