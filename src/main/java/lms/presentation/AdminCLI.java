package lms.presentation;

import java.util.List;
import java.util.Scanner;

import lms.application.AuthService;
import lms.application.BookService;
import lms.application.LoanService;
import lms.application.UserDTO;
import lms.application.UserService;
import lms.domain.Book;
import lms.domain.Role;
import lms.domain.exception.PermissionDeniedException;
import lms.domain.exception.UserNotFoundException;

/**
 * Command-Line Interface (CLI) for administrator operations in the Library
 * Management System.
 *
 * <p>
 * Provides a text-based interface that allows administrators to perform CRUD
 * operations on both books and users. The admin can also view reports and
 * manage system resources.
 * </p>
 */
public class AdminCLI implements CLI {

	private final Scanner scanner = new Scanner(System.in);
	private final UserService userService;
	private final BookService bookService;
	private final AuthService authService;
	private final LoanService loanService;

	public AdminCLI(UserService userService, BookService bookService, AuthService authService,
			LoanService loanService) {
		this.userService = userService;
		this.bookService = bookService;
		this.authService = authService;
		this.loanService = loanService;
	}

	public void start() throws IllegalAccessException {
		if (AuthService.getCurrentUser().role() != Role.ADMIN) {
			throw new IllegalAccessException("Only administrators can access this menu.");
		}

		boolean running = true;
		while (running) {
			showAdminMenu();
			String choice = scanner.nextLine().trim();

			switch (choice) {
			case "1":
				handleAddItem();
				break;
			case "2":
				handleViewAllBooks();
				break;
			case "3":
				handleUpdateBook();
				break;
			case "4":
				handleDeleteBook();
				break;
			case "5":
				handleAddUser();
				break;
			case "6":
				handleViewAllUsers();
				break;
			case "7":
				handleUpdateUser();
				break;
			case "8":
				handleDeleteUser();
				break;
			case "9":
				handleViewReports();
				break;
			case "10":
				handleLoanManagement();
				break;
			case "11":
				authService.logout();
				running = false;
				break;
			default:
				System.out.println("Invalid choice, try again.");
			}
		}
	}

	private void handleAddItem() {
		System.out.println("Choose the Type of Item you Wanna Add");
		showItemsMenu();
		String choice = scanner.nextLine().trim();

		switch (choice) {
		case "1":
			handleAddBook();
			break;
		case "2":
			handleAddCD();
			break;
		case "3":
			handleAddJuernal();
			return;
		default:
			System.out.println("Invalid choice!");
		}
	}

	private void showItemsMenu() {
		System.out.println("\n===== Items Menu =====");
		System.out.println("1. Book");
		System.out.println("2. CD");
		System.out.println("3. Jeurnal");
		System.out.print("Choose an option: ");
	}

	private void handleDeleteUser() {
		System.out.print("Enter the username of the user to delete: ");
		String username = scanner.nextLine().trim();

		try {
			UserDTO user = userService.getUserByUsername(username);
			System.out.println("User found: " + user.username());

			System.out.print("Are you sure you want to delete this user? (y/n): ");
			String confirmation = scanner.nextLine().trim().toLowerCase();

			if ("y".equals(confirmation)) {
				userService.deleteUserByUsername(user.username());
				System.out.println("✅ User deleted successfully.");
			} else {
				System.out.println("❎ Deletion cancelled.");
			}
		} catch (UserNotFoundException e) {
			System.out.println("❌ No user found with username: " + username);
		} catch (Exception e) {
			System.out.println("⚠️ An error occurred: " + e.getMessage());
		}
	}

	private void handleDeleteBook() {
		// TODO Implement this method
	}

	private void handleUpdateBook() {
		// TODO Implement this method
	}

	private void showAdminMenu() {
		System.out.println("\n===== Admin Panel =====");
		System.out.println("Books Management:");
		System.out.println("  1. Add Book");
		System.out.println("  2. View All Books");
		System.out.println("  3. Update Book");
		System.out.println("  4. Delete Book");

		System.out.println("Users Management:");
		System.out.println("  5. Add User");
		System.out.println("  6. View All Users");
		System.out.println("  7. Update User");
		System.out.println("  8. Delete User");

		System.out.println("Reports and Misc:");
		System.out.println("  9. View Reports");
		System.out.println("  10. Manage Loans");
		System.out.println("  11. Logout");

		System.out.print("Choose an option: ");
	}

	private void handleLoanManagement() {
		System.out.println("\n===== Loan Management =====");
		System.out.println("1. View Overdue Loans");
		System.out.println("2. View Loan Statistics");
		System.out.println("3. Back to Main Menu");
		System.out.print("Choose an option: ");

		String choice = scanner.nextLine().trim();
		switch (choice) {
		case "1":
			handleViewOverdueLoans();
			break;
		case "2":
			handleViewLoanStats();
			break;
		case "3":
			return;
		default:
			System.out.println("Invalid choice!");
		}
	}

	private void handleViewLoanStats() {
		System.out.println("\n===== Loan Statistics =====");
		int[] stats = loanService.getLoanStatistics();
		System.out.println("Total Loans: " + stats[0]);
		System.out.println("Active Loans: " + stats[1]);
		System.out.println("Overdue Loans: " + stats[2]);
		System.out.println("Overdue Rate: " + String.format("%.1f%%", (stats[2] * 100.0 / stats[0])));
	}

	private void handleViewOverdueLoans() {
		System.out.println("\n===== Overdue Loans =====");
		var overdueLoans = loanService.getOverdueLoans();

		if (overdueLoans.isEmpty()) {
			System.out.println("No overdue loans found.");
			return;
		}

		System.out.printf("%-10s %-15s %-15s %-12s %-8s %-10s\n", "Loan ID", "User ID", "Book ID", "Due Date",
				"Days Late", "Fine");

		for (var loan : overdueLoans) {
			System.out.printf("%-10s %-15s %-15s %-12s %-8d %-10.2f\n", loan.getLoanId().toString().substring(0, 8),
					loan.getUserId().toString().substring(0, 8), loan.getItemId().toString().substring(0, 8),
					loan.getDueDate(), loan.getDaysOverdue(), loan.calculateFine());
		}
	}

	private void handleAddBook() {
		try {
			System.out.println("=== Add a New Book ===");

			System.out.print("Enter book title: ");
			String title = scanner.nextLine().trim();

			System.out.print("Enter author: ");
			String author = scanner.nextLine().trim();

			System.out.print("Enter ISBN: ");
			String isbn = scanner.nextLine().trim();

			System.out.print("Enter publisher: ");
			String publisher = scanner.nextLine().trim();

			System.out.print("Enter publication year: ");
			int year = Integer.parseInt(scanner.nextLine().trim());

			System.out.print("Enter category/genre: ");
			String category = scanner.nextLine().trim();

			System.out.print("Enter total copies: ");
			int totalCopies = Integer.parseInt(scanner.nextLine().trim());

			System.out.print("Enter language: ");
			String language = scanner.nextLine().trim();

			System.out.print("Enter description (optional): ");
			String description = scanner.nextLine().trim();
			if (description.isEmpty())
				description = null;

			System.out.print("Enter shelf location: ");
			String shelfLocation = scanner.nextLine().trim();

			Book book = bookService.addBook(AuthService.getCurrentUser(), title, author, isbn, publisher, year,
					category, totalCopies, language, shelfLocation);

			if (book != null) {
				System.out.println("Book '" + title + "' by " + author + " (ISBN: " + isbn + ") added successfully.");
			} else {
				System.out.println("Error: A book with ISBN '" + isbn + "' already exists.");
			}

		} catch (NumberFormatException e) {
			System.out.println("Invalid number format. Please enter numeric values for year and copies.");
		} catch (PermissionDeniedException e) {
			System.out.println("You do not have permission to add books. Admin only.");
		} catch (IllegalArgumentException | IllegalStateException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	private void handleViewAllBooks() {
		System.out.println("\n===== All Books =====");

		List<Book> books = bookService.getAllBooks();

		if (books.isEmpty()) {
			System.out.println("No books available in the system.");
			return;
		}

		System.out.printf("%-5s %-30s %-20s %-15s %-10s %-5s %-10s %-10s\n", "ID", "Title", "Author", "ISBN", "Year",
				"Copies", "Category", "Shelf");

		for (Book book : books) {
			System.out.printf("%-5s %-30s %-20s %-15s %-10d %-5s %-10s %-10s\n",
					book.getBookId().toString().substring(0, 5), book.getTitle(), book.getAuthor(), book.getIsbn(),
					book.getPublicationYear(), book.getAvailableCopies() + "/" + book.getTotalCopies(),
					book.getCategory(), book.getShelfLocation());
		}

		System.out.println("==============================\n");
	}

	private void handleAddUser() {
		System.out.print("Enter username: ");
		String username = scanner.nextLine();

		System.out.print("Enter email: ");
		String email = scanner.nextLine();

		System.out.println("User '" + username + "' with email " + email + " added successfully (simulation).");
	}

	private void handleViewReports() {
		System.out.println("\n===== System Reports =====");
		int[] loanStats = loanService.getLoanStatistics();
		List<Book> books = bookService.getAllBooks();
		List<UserDTO> users = userService.getAllUsers();

		System.out.println("Library Statistics:");
		System.out.println("Total Books: " + books.size());
		System.out.println("Total Users: " + users.size());
		System.out.println("Total Loans: " + loanStats[0]);
		System.out.println("Active Loans: " + loanStats[1]);
		System.out.println("Overdue Loans: " + loanStats[2]);
		System.out.println("Overdue Rate: " + String.format("%.1f%%", (loanStats[2] * 100.0 / loanStats[0])));

		long availableBooks = books.stream().filter(book -> book.getAvailableCopies() > 0).count();
		System.out.println("Available Books: " + availableBooks + "/" + books.size());
	}

	private void handleUpdateUser() {
		System.out.print("Enter username of the user to update: ");
		String username = scanner.nextLine();

		try {
			UserDTO existing = userService.getUserByUsername(username);

			System.out.println("Updating user: " + existing.username());
			System.out.println("Leave a field blank to keep the current value.");

			System.out.print("New username(Optional): ");
			String newUsername = scanner.nextLine();
			if (newUsername.isBlank())
				newUsername = null;

			System.out.print("New password(Optional): ");
			String password = scanner.nextLine();
			if (password.isBlank())
				password = null;

			System.out.print("New email(Optional): ");
			String email = scanner.nextLine();
			if (email.isBlank())
				email = null;

			System.out.print("New role (ADMIN / MEMBER / LIBRARIAN)(Optional): ");
			String roleInput = scanner.nextLine();
			Role role = null;
			if (!roleInput.isBlank()) {
				try {
					role = Role.valueOf(roleInput.toUpperCase());
				} catch (IllegalArgumentException e) {
					System.out.println("Invalid role, keeping current.");
				}
			}

			boolean updated = userService.updateUser(AuthService.getCurrentUser(), existing.userID(), newUsername,
					password, email, role);

			if (updated)
				System.out.println("User updated successfully.");
			else
				System.out.println("Failed to update user.");

		} catch (UserNotFoundException e) {
			System.out.println("❌ No user found with username: " + username);
		} catch (IllegalAccessException e) {
			System.out.println("⛔ You do not have permission to update this user.");
		} catch (Exception e) {
			System.out.println("⚠️ An unexpected error occurred: " + e.getMessage());
		}
	}
}
