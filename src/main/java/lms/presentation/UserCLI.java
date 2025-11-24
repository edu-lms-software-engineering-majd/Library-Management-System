package lms.presentation;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;

import lms.application.AuthService;
import lms.application.BookService;
import lms.application.LoanService;
import lms.application.UserDTO;
import lms.domain.Book;
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
 * @author Majd
 * @version 2.0
 */
public class UserCLI implements CLI {

	private final Scanner scanner = new Scanner(System.in);

	private final BookService bookService;
	private final LoanService loanService;

	public UserCLI(BookService bookService, LoanService loanService) {
		this.bookService = bookService;
		this.loanService = loanService;
	}

	public void start() {
		UserDTO currentUser = AuthService.getCurrentUser();
		System.out.println("\n=========================================");
		System.out.println("    Welcome, " + currentUser.firstName() + " " + currentUser.lastName());
		System.out.println("    Role: " + currentUser.role());
		System.out.println("=========================================");

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
				handleViewMyLoans();
				break;
			case 5:
				handlePayFine();
				break;
			case 6:
				System.out.println("Logging out...");
				return;
			default:
				System.out.println("Invalid choice! Please try again.");
			}
		}
	}

	/** Displays the menu options for the user. */
	private void showUserMenu() {
		System.out.println("\n===== User Menu =====");
		System.out.println("1. Search for a Book");
		System.out.println("2. Borrow a Book");
		System.out.println("3. Return a Book");
		System.out.println("4. View My Loans");
		System.out.println("5. Pay Fine");
		System.out.println("6. Logout");
		System.out.print("Choose: ");
	}

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

		if (keyword.isEmpty()) {
			System.out.println("⚠️ Keyword cannot be empty. Please try again.");
			return;
		}

		List<Book> allBooks = bookService.getAllBooks();
		List<Book> results = new ArrayList<>();

		switch (choice) {
		case "1":
			results = allBooks.stream().filter(b -> b.getTitle().toLowerCase().contains(keyword))
					.collect(Collectors.toList());
			break;
		case "2":
			results = allBooks.stream().filter(b -> b.getAuthor().toLowerCase().contains(keyword))
					.collect(Collectors.toList());
			break;
		case "3":
			results = allBooks.stream().filter(b -> b.getIsbn().toLowerCase().contains(keyword))
					.collect(Collectors.toList());
			break;
		case "4":
			results = allBooks.stream().filter(b -> b.getPublisher().toLowerCase().contains(keyword))
					.collect(Collectors.toList());
			break;
		case "5":
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
			results.forEach(book -> System.out.println("- " + book.getTitle() + " by " + book.getAuthor() + " ("
					+ book.getPublicationYear() + "), ISBN: " + book.getIsbn()));
		}
	}

	private void handleBorrowBook() {
		System.out.print("Enter book ID to borrow: ");
		String bookId = scanner.nextLine();
		System.out.println("Book with ID " + bookId + " borrowed successfully (simulation).");
	}

	private void handleReturnBook() {
		System.out.print("Enter book ID to return: ");
		String bookId = scanner.nextLine();
		System.out.println("Book with ID " + bookId + " returned successfully (simulation).");
	}

	private void handlePayFine() {
		System.out.print("Enter amount to pay: ");
		double amount = scanner.nextDouble();
		scanner.nextLine();
		System.out.println("Fine of $" + amount + " paid successfully (simulation).");
	}

	/**
	 * Displays the user's current active loans.
	 */
	private void handleViewMyLoans() {
		System.out.println("\n=== My Active Loans ===");

		UUID userId = AuthService.getCurrentUser().userID();
		try {
			List<Loan> activeLoans = loanService.getUserActiveLoans(userId);

			if (activeLoans.isEmpty()) {
				System.out.println("You have no active loans.");
				return;
			}

			displayActiveLoans(activeLoans);
		} catch (Exception e) {
			System.out.println("Failed to load your loans: " + e.getMessage());
		}
	}

	private void displayActiveLoans(List<Loan> loans) {
		// NOTE: Active loans implementation is currently limited to books
		System.out.println("  No \t\t Book Title \t \t Borrowed \t\t Due Date\t\tStatus");

		for (int i = 0; i < loans.size(); i++) {
			Loan loan = loans.get(i);
			Book book = bookService.getAllBooks().stream().filter(b -> b.getId().equals(loan.getItemId())).findFirst()
					.orElse(null);

			String title = (book != null)
					? (book.getTitle().length() > 28 ? book.getTitle().substring(0, 25) + "..." : book.getTitle())
					: "Unknown Book";

			String status = loan.isOverdue() ? "OVERDUE " : "Active ";

			System.out.printf("│ %-3d │ %-28s │ %-12s │ %-10s │ %-8s │\n", i + 1, title, loan.getBorrowDate(),
					loan.getDueDate(), status);
		}
		System.out.println("****************************************************");
	}
}
