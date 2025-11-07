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

	/**
	 * Creates a new admin CLI.
	 *
	 * @param userService service for managing users
	 * @param bookService service for managing books
	 * @param authService service for authentication and authorization
	 */
	public AdminCLI(UserService userService, BookService bookService, AuthService authService,
			LoanService loanService) {
		this.userService = userService;
		this.bookService = bookService;
		this.authService = authService;
		this.loanService = loanService;
	}

	/**
	 * Starts the admin menu loop.
	 * <p>
	 * Displays the menu, handles user input, and executes actions until the admin
	 * chooses to log out. Access is restricted to users with {@link Role#ADMIN}.
	 * </p>
	 *
	 * @throws IllegalAccessException if a non-admin tries to start the CLI
	 */
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
				handleViewAllItems();
				break;
			case "3":
				handleSearchAndFilter();
				break;
			case "4":
				handleUpdateItem();
				break;
			case "5":
				handleDeleteItem();
				break;
			case "6":
				handleAddUser();
				break;
			case "7":
				handleViewAllUsers();
				break;
			case "8":
				handleUpdateUser();
				break;
			case "9":
				handleDeleteUser();
				break;
			case "10":
				handleViewReports();
				break;
			case "11":
				handleLoanManagement();
				break;
			case "12":
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
        	handleAddJournal();
            break;
        default:
            System.out.println("Invalid choice!");
    	}
	}

	private void handleViewAllItems() {
		System.out.println("\nChoose item type to view:");
		showItemsMenu();
		String choice = scanner.nextLine().trim();

		switch (choice) {
		case "1":
			handleViewAllBooks();
			break;
		case "2":
			handleViewAllCDs();
			break;
		case "3":
			handleViewAllJournals();
			break;
		default:
			System.out.println("Invalid choice!");
		}
	}

	private void handleSearchAndFilter() {
		System.out.println("\nChoose item type to search/filter:");
		showItemsMenu();
		String itemChoice = scanner.nextLine().trim();

		displaySearchCriteriaMenu();
		String criteriaChoice = scanner.nextLine().trim();
		
		switch (itemChoice) {
		case "1":
			searchFilterBooks(criteriaChoice);
			break;
		case "2":
			searchFilterCDs(criteriaChoice);
			break;
		case "3":
			searchFilterJournals(criteriaChoice);
			break;
		default:
			System.out.println("Invalid item type!");
		}
	}

	/**
	 * Displays the search/filter criteria menu using the SearchCriteria enum.
	 */
	private void displaySearchCriteriaMenu() {
		System.out.println("\nSearch/Filter by:");
		lms.application.search.SearchCriteria[] criteria = lms.application.search.SearchCriteria.values();
		for (int i = 0; i < criteria.length; i++) {
			System.out.println((i + 1) + ". " + criteria[i].getDisplayName());
		}
		System.out.print("Choose criteria: ");
	}

	private void handleUpdateItem() {
		System.out.println("\nChoose item type to update:");
		showItemsMenu();
		String choice = scanner.nextLine().trim();

		switch (choice) {
		case "1":
			handleUpdateBook();
			break;
		case "2":
			handleUpdateCD();
			break;
		case "3":
			handleUpdateJournal();
			break;
		default:
			System.out.println("Invalid choice!");
		}
	}

	private void handleDeleteItem() {
		System.out.println("\nChoose item type to delete:");
		showItemsMenu();
		String choice = scanner.nextLine().trim();

		switch (choice) {
		case "1":
			handleDeleteBook();
			break;
		case "2":
			handleDeleteCD();
			break;
		case "3":
			handleDeleteJournal();
			break;
		default:
			System.out.println("Invalid choice!");
		}
	}

	private void showItemsMenu() {
	    System.out.println("\n===== Items Menu =====");
	    System.out.println("1. Book");
	    System.out.println("2. CD");
	    System.out.println("3. Journal");
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
				userService.deleteUserByUsername(user.username()); // new updet
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

	/**
	 * Searches and filters books using the Strategy pattern.
	 * 
	 * <p>
	 * This method demonstrates the Strategy pattern in action. Instead of using
	 * multiple if-else or switch statements, it delegates to the appropriate
	 * strategy based on the user's choice. This makes the code more maintainable
	 * and follows the Single Responsibility Principle.
	 * </p>
	 * 
	 * @param criteriaChoice the user's choice of search criteria
	 */
	private void searchFilterBooks(String criteriaChoice) {
		try {
			// Get the appropriate search criterion from the enum
			lms.application.search.SearchCriteria criterion = 
				lms.application.search.SearchCriteria.fromChoice(criteriaChoice);
			
			if (criterion == null) {
				System.out.println("Invalid criteria choice!");
				return;
			}
			
			// Prompt for search term based on the criterion
			String promptMessage = getSearchPromptMessage(criterion);
			System.out.print(promptMessage);
			String searchTerm = scanner.nextLine().trim();
			
			// Create the strategy and execute the search
			lms.application.search.SearchStrategy<Book> strategy = criterion.createStrategy();
			lms.application.search.SearchContext<Book> searchContext = 
				new lms.application.search.SearchContext<>(strategy);
			
			List<Book> results = bookService.searchBooks(strategy, searchTerm);
			
			// Display results
			displayBookResults(results);
			
		} catch (Exception e) {
			System.out.println("Error during search/filter: " + e.getMessage());
		}
	}

	/**
	 * Gets the appropriate prompt message for each search criterion.
	 * 
	 * @param criterion the search criterion
	 * @return the prompt message
	 */
	private String getSearchPromptMessage(lms.application.search.SearchCriteria criterion) {
		return switch (criterion) {
			case ID -> "Enter Book ID: ";
			case TITLE -> "Enter title to search: ";
			case AUTHOR -> "Enter author to search: ";
			case ISBN -> "Enter ISBN: ";
			case YEAR -> "Enter publication year: ";
			case CATEGORY -> "Enter category/genre: ";
			case AVAILABILITY -> "Show only available books? (y/n): ";
		};
	}

	private void searchFilterCDs(String criteria) {
		// TODO: Implement CD search/filter once CDService is available
		System.out.println("CD search/filter functionality coming soon!");
	}

	private void searchFilterJournals(String criteria) {
		// TODO: Implement Journal search/filter once JournalService is available
		System.out.println("Journal search/filter functionality coming soon!");
	}

	private void displaySingleBook(Book book) {
		System.out.println("\n===== Book Details =====");
		System.out.println("ID: " + book.getId());
		System.out.println("Title: " + book.getTitle());
		System.out.println("Author: " + book.getAuthor());
		System.out.println("ISBN: " + book.getIsbn());
		System.out.println("Publisher: " + book.getPublisher());
		System.out.println("Publication Year: " + book.getPublicationYear());
		System.out.println("Category: " + book.getCategory());
		System.out.println("Language: " + book.getLanguage());
		System.out.println("Copies: " + book.getAvailableCopies() + "/" + book.getTotalCopies());
		System.out.println("Shelf Location: " + book.getShelfLocation());
		System.out.println("========================\n");
	}

	private void displayBookResults(List<Book> books) {
		if (books.isEmpty()) {
			System.out.println("No books found matching the criteria.");
			return;
		}

		System.out.println("\n===== Search/Filter Results =====");
		System.out.printf("%-10s %-30s %-20s %-15s %-10s %-10s %-10s\n", "ID", "Title", "Author", "ISBN", "Year",
				"Copies", "Category");
		System.out.println("─".repeat(105));

		for (Book book : books) {
			String bookId = book.getId().toString();
			String displayId = bookId.length() > 8 ? bookId.substring(0, 8) : bookId;
			String displayTitle = book.getTitle().length() > 28 ? book.getTitle().substring(0, 28) + ".." : book.getTitle();
			String displayAuthor = book.getAuthor().length() > 18 ? book.getAuthor().substring(0, 18) + ".." : book.getAuthor();
			
			System.out.printf("%-10s %-30s %-20s %-15s %-10d %-10s %-10s\n", displayId, displayTitle, displayAuthor,
					book.getIsbn(), book.getPublicationYear(),
					book.getAvailableCopies() + "/" + book.getTotalCopies(), book.getCategory());
		}
		System.out.println("Total results: " + books.size());
		System.out.println("=================================\n");
	}

	private void handleDeleteBook() {
		// TODO Implement this method
	}

	private void handleUpdateBook() {
		// TODO Implement this method
	}

	private void handleAddCD() {
		// TODO: Implement CD addition once CD domain class and CDService are available
		System.out.println("CD addition functionality coming soon!");
	}

	private void handleViewAllCDs() {
		// TODO: Implement CD viewing once CDService is available
		System.out.println("CD viewing functionality coming soon!");
	}

	private void handleUpdateCD() {
		// TODO: Implement CD update once CDService is available
		System.out.println("CD update functionality coming soon!");
	}

	private void handleDeleteCD() {
		// TODO: Implement CD deletion once CDService is available
		System.out.println("CD deletion functionality coming soon!");
	}

	private void handleAddJournal() {
		// TODO: Implement Journal addition once Journal domain class and JournalService are available
		System.out.println("Journal addition functionality coming soon!");
	}

	private void handleViewAllJournals() {
		// TODO: Implement Journal viewing once JournalService is available
		System.out.println("Journal viewing functionality coming soon!");
	}

	private void handleUpdateJournal() {
		// TODO: Implement Journal update once JournalService is available
		System.out.println("Journal update functionality coming soon!");
	}

	private void handleDeleteJournal() {
		// TODO: Implement Journal deletion once JournalService is available
		System.out.println("Journal deletion functionality coming soon!");
	}

	private void showAdminMenu() {
		System.out.println("\n===== Admin Panel =====");
		System.out.println("Items Management:");
		System.out.println("  1. Add Item");
		System.out.println("  2. View All Items");
		System.out.println("  3. Search & Filter Items");
		System.out.println("  4. Update Item");
		System.out.println("  5. Delete Item");

		System.out.println("Users Management:");
		System.out.println("  6. Add User");
		System.out.println("  7. View All Users");
		System.out.println("  8. Update User");
		System.out.println("  9. Delete User");

		System.out.println("Reports and Misc:");
		System.out.println("  10. View Reports");
		System.out.println("  11. Manage Loans");
		System.out.println("  12. Logout");

		System.out.print("Choose an option: ");
	}

	/**
	 * Handles loan management operations for administrators.
	 */
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

	

/**
 * Displays comprehensive loan statistics.
 */
private void handleViewLoanStats() {
    System.out.println("\n===== Loan Statistics =====");
    int[] stats = loanService.getLoanStatistics();
    System.out.println("Total Loans: " + stats[0]);
    System.out.println("Active Loans: " + stats[1]);
    System.out.println("Overdue Loans: " + stats[2]);
    System.out.println("Overdue Rate: " + String.format("%.1f%%", (stats[2] * 100.0 / stats[0])));
}

	
	
	
	/**
	 * Displays all overdue loans in the system.
	 */
	private void handleViewOverdueLoans() {
	    System.out.println("\n===== Overdue Loans =====");
	    var overdueLoans = loanService.getOverdueLoans();
	    
	    if (overdueLoans.isEmpty()) {
	        System.out.println("No overdue loans found.");
	        return;
	    }

	    System.out.printf("%-10s %-15s %-15s %-12s %-8s %-10s\n", 
	        "Loan ID", "User ID", "Book ID", "Due Date", "Days Late", "Fine");
	    
	    for (var loan : overdueLoans) {
	        System.out.printf("%-10s %-15s %-15s %-12s %-8d %-10.2f\n",
	            loan.getLoanId().toString().substring(0, 8),
	            loan.getUserId().toString().substring(0, 8),
	            loan.getItemId().toString().substring(0, 8),
	            loan.getDueDate(),
	            loan.getDaysOverdue(),
	            loan.calculateFine());
	    }
	}

	
	
	/** Handles the process of adding a book via {@link BookService}. */
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
					book.getId().toString().substring(0, 5), book.getTitle(), book.getAuthor(), book.getIsbn(),
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

	private void handleViewAllUsers() {
		System.out.println("\n===== All Users =====");
		List<UserDTO> users = userService.getAllUsers();

		if (users.isEmpty()) {
			System.out.println("No users in the system.");
			return;
		}

		System.out.printf("%-10s %-20s %-25s %-15s\n", "User ID", "Username", "Name", "Role");
		System.out.println("─".repeat(70));

		for (UserDTO user : users) {
			String userId = user.userID().toString();
			String displayId = userId.length() > 8 ? userId.substring(0, 8) : userId;
			String fullName = user.firstName() + " " + user.lastName();
			System.out.printf("%-10s %-20s %-25s %-15s\n", 
				displayId, user.username(), fullName, user.role());
		}
		System.out.println("Total users: " + users.size());
		System.out.println("====================\n");
	}

	private void handleViewReports() {
		/*// TODO: write the implementation of this method
	 	System.out.println("Reports: (simulation)");
		 System.out.println("Total Books: 100");
		 System.out.println("Total Users: 25");*/
		
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
	    
	    // Calculate available books
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
