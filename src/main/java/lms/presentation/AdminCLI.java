package lms.presentation;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import lms.application.AuthService;
import lms.application.BookService;
import lms.application.CDService;
import lms.application.JournalService;
import lms.application.LoanService;
import lms.application.UserDTO;
import lms.application.UserService;
import lms.application.search.SearchContext;
import lms.application.search.SearchCriteria;
import lms.application.search.SearchStrategy;
import lms.domain.Book;
import lms.domain.CD;
import lms.domain.Journal;
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
	private final CDService cdService;
	private final JournalService journalService;

	/**
	 * Creates a new admin CLI.
	 *
	 * @param userService service for managing users
	 * @param bookService service for managing books
	 * @param authService service for authentication and authorization
	 * @param loanService service for managing loans
	 * @param cdService service for managing CDs
	 * @param journalService service for managing journals
	 */
	public AdminCLI(UserService userService, BookService bookService, AuthService authService,
			LoanService loanService, CDService cdService, JournalService journalService) {
		this.userService = userService;
		this.bookService = bookService;
		this.authService = authService;
		this.loanService = loanService;
		this.cdService = cdService;
		this.journalService = journalService;
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
		SearchCriteria[] criteria = SearchCriteria.values();
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
				userService.deleteByUsername(user.username());
				System.out.println("User deleted successfully.");
			} else {
				System.out.println("Deletion cancelled.");
			}
		} catch (UserNotFoundException e) {
			System.out.println("No user found with username: " + username);
		} catch (Exception e) {
			System.out.println("An error occurred: " + e.getMessage());
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
	 * <p>
	 * Supports partial matching for flexible searches - users can enter incomplete
	 * values like the first few characters of an ID, partial title, etc.
	 * </p>
	 * 
	 * @param criteriaChoice the user's choice of search criteria
	 */
	private void searchFilterBooks(String criteriaChoice) {
		try {
			SearchCriteria criterion = 
				SearchCriteria.fromChoice(criteriaChoice);
			
			if (criterion == null) {
				System.out.println("Invalid criteria choice!");
				return;
			}
			displaySearchHint(criterion);
			
			String promptMessage = getSearchPromptMessage(criterion);
			System.out.print(promptMessage);
			String searchTerm = scanner.nextLine().trim();
			
			if (searchTerm.isEmpty()) {
				System.out.println("Search term cannot be empty!");
				return;
			}
			
			SearchStrategy<Book> strategy = criterion.createStrategy();
			SearchContext<Book> searchContext = new SearchContext<>(strategy);

			List<Book> results = bookService.searchBooks(strategy, searchTerm);
			
			displayBookResults(results);
			
		} catch (Exception e) {
			System.out.println("Error during search/filter: " + e.getMessage());
		}
	}
	
	/**
	 * Displays helpful hints about how the search works for each criterion.
	 * 
	 * @param criterion the search criterion
	 */
	private void displaySearchHint(SearchCriteria criterion) {
		String hint = switch (criterion) {
			case ID -> "Tip: You can enter partial ID (e.g., first 6-8 characters)";
			case TITLE -> "Tip: Partial matches work (e.g., 'Clean' finds 'Clean Code')";
			case AUTHOR -> "Tip: Partial matches work (e.g., 'Martin' finds all Martin authors)";
			case ISBN -> "Tip: You can enter partial ISBN numbers";
			case CATEGORY -> "Tip: Partial matches work (e.g., 'Eng' finds 'Engineering')";
			case YEAR -> "Tip: Enter exact year (e.g., 2020)";
			case AVAILABILITY -> "Tip: Enter 'y' or 'yes' to show only available books";
		};
		System.out.println(hint);
	}

	/**
	 * Gets the appropriate prompt message for each search criterion.
	 * 
	 * @param criterion the search criterion
	 * @return the prompt message
	 */
	private String getSearchPromptMessage(SearchCriteria criterion) {
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

	private void searchFilterCDs(String criteriaChoice) {
		try {
			// Map criteria choice to appropriate CD search strategy
			SearchStrategy<CD> strategy = getCDSearchStrategy(criteriaChoice);
			
			if (strategy == null) {
				System.out.println("Invalid criteria choice!");
				return;
			}
			
			// Display search hint
			displayCDSearchHint(criteriaChoice);
			
			String promptMessage = getCDSearchPromptMessage(criteriaChoice);
			System.out.print(promptMessage);
			String searchTerm = scanner.nextLine().trim();
			
			if (searchTerm.isEmpty()) {
				System.out.println("Search term cannot be empty!");
				return;
			}
			
			List<CD> results = cdService.searchCDs(strategy, searchTerm);
			displayCDResults(results);
			
		} catch (Exception e) {
			System.out.println("Error during search/filter: " + e.getMessage());
		}
	}
	
	/**
	 * Gets the appropriate CD search strategy based on user choice.
	 */
	private SearchStrategy<CD> getCDSearchStrategy(String choice) {
		return switch (choice) {
			case "1" -> new lms.application.search.SearchCDByIdStrategy();
			case "2" -> new lms.application.search.SearchCDByTitleStrategy();
			case "3" -> new lms.application.search.SearchCDByArtistStrategy();
			case "7" -> new lms.application.search.FilterCDByAvailabilityStrategy();
			default -> null;
		};
	}
	
	/**
	 * Displays helpful hints for CD search.
	 */
	private void displayCDSearchHint(String choice) {
		String hint = switch (choice) {
			case "1" -> "Tip: You can enter partial ID (e.g., first 6-8 characters)";
			case "2" -> "Tip: Partial matches work (e.g., 'Thriller' finds 'Thriller')";
			case "3" -> "Tip: Partial matches work (e.g., 'Jackson' finds all Jackson artists)";
			case "7" -> "Tip: Enter 'y' or 'yes' to show only available CDs";
			default -> "Tip: Enter your search criteria";
		};
		System.out.println(hint);
	}
	
	/**
	 * Gets the appropriate prompt message for CD search.
	 */
	private String getCDSearchPromptMessage(String choice) {
		return switch (choice) {
			case "1" -> "Enter CD ID: ";
			case "2" -> "Enter title to search: ";
			case "3" -> "Enter artist to search: ";
			case "7" -> "Show only available CDs? (y/n): ";
			default -> "Enter search term: ";
		};
	}
	
	/**
	 * Displays CD search results in a formatted table.
	 */
	private void displayCDResults(List<CD> cds) {
		if (cds.isEmpty()) {
			System.out.println("No CDs found matching the criteria.");
			return;
		}

		System.out.println("\n===== Search/Filter Results =====");
		System.out.printf("%-10s %-40s %-30s %-15s\n", "ID", "Title", "Artist", "Copies");
		System.out.println("─".repeat(95));

		for (CD cd : cds) {
			String cdId = cd.getId().toString();
			String displayId = cdId.length() > 8 ? cdId.substring(0, 8) : cdId;
			String displayTitle = cd.getTitle().length() > 38 ? cd.getTitle().substring(0, 38) + ".." : cd.getTitle();
			String displayArtist = cd.getArtist().length() > 28 ? cd.getArtist().substring(0, 28) + ".." : cd.getArtist();
			String copies = cd.getAvailableCopies() + "/" + cd.getTotalCopies();
			
			System.out.printf("%-10s %-40s %-30s %-15s\n",
					displayId, displayTitle, displayArtist, copies);
		}
		System.out.println("Total results: " + cds.size());
		System.out.println("=================================\n");
	}

	private void searchFilterJournals(String criteriaChoice) {
		try {
			// Map criteria choice to appropriate Journal search strategy
			SearchStrategy<Journal> strategy = getJournalSearchStrategy(criteriaChoice);
			
			if (strategy == null) {
				System.out.println("Invalid criteria choice!");
				return;
			}
			
			// Display search hint
			displayJournalSearchHint(criteriaChoice);
			
			String promptMessage = getJournalSearchPromptMessage(criteriaChoice);
			System.out.print(promptMessage);
			String searchTerm = scanner.nextLine().trim();
			
			if (searchTerm.isEmpty()) {
				System.out.println("Search term cannot be empty!");
				return;
			}
			
			List<Journal> results = journalService.searchJournals(strategy, searchTerm);
			displayJournalResults(results);
			
		} catch (Exception e) {
			System.out.println("Error during search/filter: " + e.getMessage());
		}
	}
	
	/**
	 * Gets the appropriate Journal search strategy based on user choice.
	 */
	private SearchStrategy<Journal> getJournalSearchStrategy(String choice) {
		return switch (choice) {
			case "1" -> new lms.application.search.SearchJournalByIdStrategy();
			case "2" -> new lms.application.search.SearchJournalByTitleStrategy();
			case "3" -> new lms.application.search.SearchJournalByAuthorStrategy();
			case "7" -> new lms.application.search.FilterJournalByAvailabilityStrategy();
			default -> null;
		};
	}
	
	/**
	 * Displays helpful hints for Journal search.
	 */
	private void displayJournalSearchHint(String choice) {
		String hint = switch (choice) {
			case "1" -> "Tip: You can enter partial ID (e.g., first 6-8 characters)";
			case "2" -> "Tip: Partial matches work (e.g., 'Nature' finds journals with 'Nature')";
			case "3" -> "Tip: Partial matches work (e.g., 'Springer' finds all Springer journals)";
			case "7" -> "Tip: Enter 'y' or 'yes' to show only available journals";
			default -> "Tip: Enter your search criteria";
		};
		System.out.println(hint);
	}
	
	/**
	 * Gets the appropriate prompt message for Journal search.
	 */
	private String getJournalSearchPromptMessage(String choice) {
		return switch (choice) {
			case "1" -> "Enter Journal ID: ";
			case "2" -> "Enter title to search: ";
			case "3" -> "Enter author/publisher to search: ";
			case "7" -> "Show only available journals? (y/n): ";
			default -> "Enter search term: ";
		};
	}
	
	/**
	 * Displays Journal search results in a formatted table.
	 */
	private void displayJournalResults(List<Journal> journals) {
		if (journals.isEmpty()) {
			System.out.println("No journals found matching the criteria.");
			return;
		}

		System.out.println("\n===== Search/Filter Results =====");
		System.out.printf("%-10s %-50s %-40s %-15s\n", "ID", "Title", "Author/Publisher", "Copies");
		System.out.println("─".repeat(115));

		for (Journal journal : journals) {
			String journalId = journal.getId().toString();
			String displayId = journalId.length() > 8 ? journalId.substring(0, 8) : journalId;
			String displayTitle = journal.getTitle().length() > 48 ? journal.getTitle().substring(0, 48) + ".." : journal.getTitle();
			String displayAuthor = journal.getAuthor().length() > 38 ? journal.getAuthor().substring(0, 38) + ".." : journal.getAuthor();
			String copies = journal.getAvailableCopies() + "/" + journal.getTotalCopies();
			
			System.out.printf("%-10s %-50s %-40s %-15s\n",
					displayId, displayTitle, displayAuthor, copies);
		}
		System.out.println("Total results: " + journals.size());
		System.out.println("=================================\n");
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
		System.out.print("Enter the Book ID (full or partial): ");
		String bookIdStr = scanner.nextLine().trim();

		try {
			Book existingBook = findBookByIdOrSubId(bookIdStr);

			if (existingBook == null) {
				System.out.println("No book found with ID: " + bookIdStr);
				return;
			}

			displayBookForDeletion(existingBook);

			if (!confirmDeletion("book", existingBook.getAvailableCopies(), existingBook.getTotalCopies())) {
				System.out.println("Deletion cancelled.");
				return;
			}

			boolean deleted = bookService.deleteBook(AuthService.getCurrentUser(), existingBook.getId());

			System.out.println(deleted ? "Book deleted successfully." : "Failed to delete book.");

		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
		} catch (PermissionDeniedException e) {
			System.out.println("You do not have permission to delete books. Admin only.");
		} catch (Exception e) {
			System.out.println("An error occurred: " + e.getMessage());
		}
	}

	private void handleUpdateBook() {
		System.out.print("Enter the Book ID (full or partial): ");
		String bookIdStr = scanner.nextLine().trim();

		try {
			Book existingBook = findBookByIdOrSubId(bookIdStr);

			if (existingBook == null) {
				System.out.println("No book found with ID: " + bookIdStr);
				return;
			}

			System.out.println("\n=== Current Book Details ===");
			displaySingleBook(existingBook);
			
			System.out.println("Enter new values (leave blank to keep current value):");

			System.out.print("New title [" + existingBook.getTitle() + "]: ");
			String title = scanner.nextLine().trim();
			if (title.isBlank()) title = null;

			System.out.print("New author [" + existingBook.getAuthor() + "]: ");
			String author = scanner.nextLine().trim();
			if (author.isBlank()) author = null;

			System.out.print("New ISBN [" + existingBook.getIsbn() + "]: ");
			String isbn = scanner.nextLine().trim();
			if (isbn.isBlank()) isbn = null;

			System.out.print("New publisher [" + existingBook.getPublisher() + "]: ");
			String publisher = scanner.nextLine().trim();
			if (publisher.isBlank()) publisher = null;

			System.out.print("New publication year [" + existingBook.getPublicationYear() + "]: ");
			String yearStr = scanner.nextLine().trim();
			Integer year = null;
			if (!yearStr.isBlank()) {
				try {
					year = Integer.parseInt(yearStr);
				} catch (NumberFormatException e) {
					System.out.println("Invalid year format, keeping current value.");
				}
			}

			System.out.print("New category [" + existingBook.getCategory() + "]: ");
			String category = scanner.nextLine().trim();
			if (category.isBlank()) category = null;

			System.out.print("New total copies [" + existingBook.getTotalCopies() + "]: ");
			String copiesStr = scanner.nextLine().trim();
			Integer totalCopies = null;
			if (!copiesStr.isBlank()) {
				try {
					totalCopies = Integer.parseInt(copiesStr);
				} catch (NumberFormatException e) {
					System.out.println("Invalid copies format, keeping current value.");
				}
			}

			System.out.print("New language [" + existingBook.getLanguage() + "]: ");
			String language = scanner.nextLine().trim();
			if (language.isBlank()) language = null;

			System.out.print("New shelf location [" + existingBook.getShelfLocation() + "]: ");
			String shelfLocation = scanner.nextLine().trim();
			if (shelfLocation.isBlank()) shelfLocation = null;

			boolean updated = bookService.updateBook(
				AuthService.getCurrentUser(),
				existingBook.getId(),
				title,
				author,
				isbn,
				publisher,
				year,
				category,
				totalCopies,
				language,
				shelfLocation
			);

			if (updated) {
				System.out.println("Book updated successfully.");
				Book updatedBook = bookService.getBookById(existingBook.getId());
				displaySingleBook(updatedBook);
			} else {
				System.out.println("Failed to update book.");
			}

		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
		} catch (PermissionDeniedException e) {
			System.out.println("You do not have permission to update books. Admin only.");
		} catch (Exception e) {
			System.out.println("An error occurred: " + e.getMessage());
		}
	}

	private void handleAddCD() {
		try {
			System.out.println("=== Add a New CD ===");

			System.out.print("Enter CD title: ");
			String title = scanner.nextLine().trim();

			System.out.print("Enter artist: ");
			String artist = scanner.nextLine().trim();

			System.out.print("Enter total copies: ");
			int totalCopies = Integer.parseInt(scanner.nextLine().trim());

			CD cd = cdService.addCD(AuthService.getCurrentUser(), title, artist, totalCopies);

			if (cd != null) {
				System.out.println("CD '" + title + "' by " + artist + " added successfully.");
			} else {
				System.out.println("Error: Failed to add CD.");
			}

		} catch (NumberFormatException e) {
			System.out.println("Invalid number format. Please enter numeric values for copies.");
		} catch (PermissionDeniedException e) {
			System.out.println("You do not have permission to add CDs. Admin only.");
		} catch (IllegalArgumentException | IllegalStateException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	private void handleViewAllCDs() {
		System.out.println("\n===== All CDs =====");

		List<CD> cds = cdService.getAllCDs();

		if (cds.isEmpty()) {
			System.out.println("No CDs available in the system.");
			return;
		}

		System.out.printf("%-10s %-40s %-30s %-15s\n", "ID", "Title", "Artist", "Copies");
		System.out.println("─".repeat(95));

		for (CD cd : cds) {
			String cdId = cd.getId().toString();
			String displayId = cdId.length() > 8 ? cdId.substring(0, 8) : cdId;
			String displayTitle = cd.getTitle().length() > 38 ? cd.getTitle().substring(0, 38) + ".." : cd.getTitle();
			String displayArtist = cd.getArtist().length() > 28 ? cd.getArtist().substring(0, 28) + ".." : cd.getArtist();
			String copies = cd.getAvailableCopies() + "/" + cd.getTotalCopies();
			
			System.out.printf("%-10s %-40s %-30s %-15s\n",
					displayId, displayTitle, displayArtist, copies);
		}

		System.out.println("\nTotal CDs: " + cds.size());
		
		long availableCDs = cds.stream().filter(CD::isAvailable).count();
		int totalCopiesCount = cds.stream().mapToInt(CD::getTotalCopies).sum();
		int availableCopiesCount = cds.stream().mapToInt(CD::getAvailableCopies).sum();
		
		System.out.println("CDs with Available Copies: " + availableCDs + "/" + cds.size());
		System.out.println("Total Copies in Library: " + totalCopiesCount);
		System.out.println("Available Copies: " + availableCopiesCount + "/" + totalCopiesCount);
		System.out.println("==============================\n");
	}

	private void handleUpdateCD() {
		System.out.print("Enter the CD ID (full or partial): ");
		String cdIdStr = scanner.nextLine().trim();

		try {
			CD existingCD = findCDByIdOrSubId(cdIdStr);

			System.out.println("\n=== Current CD Details ===");
			displaySingleCD(existingCD);
			
			System.out.println("Enter new values (leave blank to keep current value):");

			System.out.print("New title [" + existingCD.getTitle() + "]: ");
			String title = scanner.nextLine().trim();
			if (title.isBlank()) title = null;

			System.out.print("New artist [" + existingCD.getArtist() + "]: ");
			String artist = scanner.nextLine().trim();
			if (artist.isBlank()) artist = null;

			System.out.print("New total copies [" + existingCD.getTotalCopies() + "]: ");
			String copiesStr = scanner.nextLine().trim();
			Integer totalCopies = null;
			if (!copiesStr.isBlank()) {
				try {
					totalCopies = Integer.parseInt(copiesStr);
				} catch (NumberFormatException e) {
					System.out.println("Invalid copies format, keeping current value.");
				}
			}

			CD updated = cdService.updateCD(
				AuthService.getCurrentUser(),
				existingCD.getId(),
				title,
				artist,
				totalCopies
			);

			if (updated != null) {
				System.out.println("CD updated successfully.");
				displaySingleCD(updated);
			} else {
				System.out.println("Failed to update CD.");
			}

		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
		} catch (PermissionDeniedException e) {
			System.out.println("You do not have permission to update CDs. Admin only.");
		} catch (Exception e) {
			System.out.println("An error occurred: " + e.getMessage());
		}
	}

	private void displaySingleCD(CD cd) {
		System.out.println("\n===== CD Details =====");
		System.out.println("ID: " + cd.getId());
		System.out.println("Title: " + cd.getTitle());
		System.out.println("Artist: " + cd.getArtist());
		System.out.println("Copies: " + cd.getAvailableCopies() + "/" + cd.getTotalCopies());
		System.out.println("========================\n");
	}

	private void handleDeleteCD() {
		System.out.print("Enter the CD ID (full or partial): ");
		String cdIdStr = scanner.nextLine().trim();

		try {
			CD existingCD = findCDByIdOrSubId(cdIdStr);

			if (existingCD == null) {
				System.out.println("No CD found with ID: " + cdIdStr);
				return;
			}

			displayCDForDeletion(existingCD);

			if (!confirmDeletion("CD", existingCD.getAvailableCopies(), existingCD.getTotalCopies())) {
				System.out.println("Deletion cancelled.");
				return;
			}

			boolean deleted = cdService.deleteCD(AuthService.getCurrentUser(), existingCD.getId());

			System.out.println(deleted ? "CD deleted successfully." : "Failed to delete CD.");

		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
		} catch (PermissionDeniedException e) {
			System.out.println("You do not have permission to delete CDs. Admin only.");
		} catch (Exception e) {
			System.out.println("An error occurred: " + e.getMessage());
		}
	}

	private void handleAddJournal() {
		try {
			System.out.println("=== Add a New Journal ===");

			System.out.print("Enter journal title: ");
			String title = scanner.nextLine().trim();

			System.out.print("Enter author/publisher: ");
			String author = scanner.nextLine().trim();

			System.out.print("Enter total copies: ");
			int totalCopies = Integer.parseInt(scanner.nextLine().trim());

			Journal journal = journalService.addJournal(AuthService.getCurrentUser(), title, author, totalCopies);

			if (journal != null) {
				System.out.println("Journal '" + title + "' by " + author + " added successfully.");
			} else {
				System.out.println("Error: Failed to add journal.");
			}

		} catch (NumberFormatException e) {
			System.out.println("Invalid number format. Please enter numeric values for copies.");
		} catch (PermissionDeniedException e) {
			System.out.println("You do not have permission to add journals. Admin only.");
		} catch (IllegalArgumentException | IllegalStateException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	private void handleViewAllJournals() {
		System.out.println("\n===== All Journals =====");

		List<Journal> journals = journalService.getAllJournals();

		if (journals.isEmpty()) {
			System.out.println("No journals available in the system.");
			return;
		}

		System.out.printf("%-38s %-50s %-50s %-15s\n", "ID", "Title", "Author/Publisher", "Copies");
		System.out.println("─".repeat(153));

		for (Journal journal : journals) {
			String journalId = journal.getId().toString();
			String displayId = journalId.length() > 8 ? journalId.substring(0, 8) : journalId;
			String displayTitle = journal.getTitle().length() > 48 ? journal.getTitle().substring(0, 48) + ".." : journal.getTitle();
			String displayAuthor = journal.getAuthor().length() > 48 ? journal.getAuthor().substring(0, 48) + ".." : journal.getAuthor();
			String copies = journal.getAvailableCopies() + "/" + journal.getTotalCopies();
			
			System.out.printf("%-38s %-50s %-50s %-15s\n",
					displayId, displayTitle, displayAuthor, copies);
		}

		System.out.println("\nTotal Journals: " + journals.size());
		
		long availableJournals = journals.stream().filter(Journal::isAvailable).count();
		int totalCopiesCount = journals.stream().mapToInt(Journal::getTotalCopies).sum();
		int availableCopiesCount = journals.stream().mapToInt(Journal::getAvailableCopies).sum();
		
		System.out.println("Journals with Available Copies: " + availableJournals + "/" + journals.size());
		System.out.println("Total Copies in Library: " + totalCopiesCount);
		System.out.println("Available Copies: " + availableCopiesCount + "/" + totalCopiesCount);
		System.out.println("==============================\n");
	}

	private void handleUpdateJournal() {
		System.out.print("Enter the Journal ID (full or partial): ");
		String journalIdStr = scanner.nextLine().trim();

		try {
			Journal existingJournal = findJournalByIdOrSubId(journalIdStr);

			System.out.println("\n=== Current Journal Details ===");
			displaySingleJournal(existingJournal);
			
			System.out.println("Enter new values (leave blank to keep current value):");

			System.out.print("New title [" + existingJournal.getTitle() + "]: ");
			String title = scanner.nextLine().trim();
			if (title.isBlank()) title = null;

			System.out.print("New author/publisher [" + existingJournal.getAuthor() + "]: ");
			String author = scanner.nextLine().trim();
			if (author.isBlank()) author = null;

			System.out.print("New total copies [" + existingJournal.getTotalCopies() + "]: ");
			String copiesStr = scanner.nextLine().trim();
			Integer totalCopies = null;
			if (!copiesStr.isBlank()) {
				try {
					totalCopies = Integer.parseInt(copiesStr);
				} catch (NumberFormatException e) {
					System.out.println("Invalid copies format, keeping current value.");
				}
			}

			Journal updated = journalService.updateJournal(
				AuthService.getCurrentUser(),
				existingJournal.getId(),
				title,
				author,
				totalCopies
			);

			if (updated != null) {
				System.out.println("Journal updated successfully.");
				displaySingleJournal(updated);
			} else {
				System.out.println("Failed to update journal.");
			}

		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
		} catch (PermissionDeniedException e) {
			System.out.println("You do not have permission to update journals. Admin only.");
		} catch (Exception e) {
			System.out.println("An error occurred: " + e.getMessage());
		}
	}

	private void displaySingleJournal(Journal journal) {
		System.out.println("\n===== Journal Details =====");
		System.out.println("ID: " + journal.getId());
		System.out.println("Title: " + journal.getTitle());
		System.out.println("Author/Publisher: " + journal.getAuthor());
		System.out.println("Copies: " + journal.getAvailableCopies() + "/" + journal.getTotalCopies());
		System.out.println("============================\n");
	}

	private void handleDeleteJournal() {
		System.out.print("Enter the Journal ID (full or partial): ");
		String journalIdStr = scanner.nextLine().trim();

		try {
			Journal existingJournal = findJournalByIdOrSubId(journalIdStr);

			if (existingJournal == null) {
				System.out.println("No journal found with ID: " + journalIdStr);
				return;
			}

			displayJournalForDeletion(existingJournal);

			if (!confirmDeletion("journal", existingJournal.getAvailableCopies(), existingJournal.getTotalCopies())) {
				System.out.println("Deletion cancelled.");
				return;
			}

			boolean deleted = journalService.deleteJournal(AuthService.getCurrentUser(), existingJournal.getId());

			System.out.println(deleted ? "Journal deleted successfully." : "Failed to delete journal.");

		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
		} catch (PermissionDeniedException e) {
			System.out.println("You do not have permission to delete journals. Admin only.");
		} catch (Exception e) {
			System.out.println("An error occurred: " + e.getMessage());
		}
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
	            loan.getId().toString().substring(0, 8),
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

		System.out.printf("%-10s %-35s %-25s %-15s %-8s %-12s %-15s %-12s\n", 
				"ID", "Title", "Author", "ISBN", "Year", "Copies", "Category", "Shelf");
		System.out.println("─".repeat(132));

		for (Book book : books) {
			String bookId = book.getId().toString();
			String displayId = bookId.length() > 8 ? bookId.substring(0, 8) : bookId;
			String displayTitle = book.getTitle().length() > 33 ? book.getTitle().substring(0, 33) + ".." : book.getTitle();
			String displayAuthor = book.getAuthor().length() > 23 ? book.getAuthor().substring(0, 23) + ".." : book.getAuthor();
			String displayCategory = book.getCategory().length() > 13 ? book.getCategory().substring(0, 13) + ".." : book.getCategory();
			String displayShelf = book.getShelfLocation().length() > 10 ? book.getShelfLocation().substring(0, 10) + ".." : book.getShelfLocation();
			String copies = book.getAvailableCopies() + "/" + book.getTotalCopies();
			
			System.out.printf("%-10s %-35s %-25s %-15s %-8d %-12s %-15s %-12s\n",
					displayId, displayTitle, displayAuthor, book.getIsbn(),
					book.getPublicationYear(), copies, displayCategory, displayShelf);
		}

		System.out.println("\nTotal Books: " + books.size());
		
		long availableBooks = books.stream().filter(Book::isAvailable).count();
		int totalCopiesCount = books.stream().mapToInt(Book::getTotalCopies).sum();
		int availableCopiesCount = books.stream().mapToInt(Book::getAvailableCopies).sum();
		
		System.out.println("Books with Available Copies: " + availableBooks + "/" + books.size());
		System.out.println("Total Copies in Library: " + totalCopiesCount);
		System.out.println("Available Copies: " + availableCopiesCount + "/" + totalCopiesCount);
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
			System.out.println("No user found with username: " + username);
		} catch (IllegalAccessException e) {
			System.out.println("You do not have permission to update this user.");
		} catch (Exception e) {
			System.out.println("An unexpected error occurred: " + e.getMessage());
		}
	}

	/**
	 * Validates if a string is a valid UUID format.
	 * 
	 * @param str the string to validate
	 * @return true if valid UUID format, false otherwise
	 */
	private boolean isValidUUID(String str) {
		try {
			UUID.fromString(str);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	/**
	 * Helper method to find a book by full UUID or partial ID.
	 * Tries full UUID first, then falls back to partial ID search.
	 * 
	 * @param idStr the ID string (full or partial)
	 * @return the matching Book
	 * @throws IllegalArgumentException if no book found
	 */
	private Book findBookByIdOrSubId(String idStr) {
		// Try as full UUID first (more efficient if user provides full ID)
		if (isValidUUID(idStr)) {
			UUID bookId = UUID.fromString(idStr);
			return bookService.getBookById(bookId);
		}
		
		// Fall back to partial ID search
		return bookService.getBookBySubId(idStr);
	}

	/**
	 * Helper method to find a CD by full UUID or partial ID.
	 * Tries full UUID first, then falls back to partial ID search.
	 * 
	 * @param idStr the ID string (full or partial)
	 * @return the matching CD
	 * @throws IllegalArgumentException if no CD found
	 */
	private CD findCDByIdOrSubId(String idStr) {
		// Try as full UUID first (more efficient if user provides full ID)
		if (isValidUUID(idStr)) {
			UUID cdId = UUID.fromString(idStr);
			return cdService.getCDById(cdId);
		}
		
		// Fall back to partial ID search
		return cdService.getCDBySubId(idStr);
	}

	/**
	 * Helper method to find a journal by full UUID or partial ID.
	 * Tries full UUID first, then falls back to partial ID search.
	 * 
	 * @param idStr the ID string (full or partial)
	 * @return the matching Journal
	 * @throws IllegalArgumentException if no journal found
	 */
	private Journal findJournalByIdOrSubId(String idStr) {
		// Try as full UUID first (more efficient if user provides full ID)
		if (isValidUUID(idStr)) {
			UUID journalId = UUID.fromString(idStr);
			return journalService.getJournalById(journalId);
		}
		
		// Fall back to partial ID search
		return journalService.getJournalBySubId(idStr);
	}

	/**
	 * Displays book details before deletion.
	 * 
	 * @param book the book to display
	 */
	private void displayBookForDeletion(Book book) {
		System.out.println("\n=== Book to Delete ===");
		displaySingleBook(book);
	}

	/**
	 * Displays CD details before deletion.
	 * 
	 * @param cd the CD to display
	 */
	private void displayCDForDeletion(CD cd) {
		System.out.println("\n=== CD to Delete ===");
		displaySingleCD(cd);
	}

	/**
	 * Displays journal details before deletion.
	 * 
	 * @param journal the journal to display
	 */
	private void displayJournalForDeletion(Journal journal) {
		System.out.println("\n=== Journal to Delete ===");
		displaySingleJournal(journal);
	}

	/**
	 * Confirms deletion with the user, showing a warning if there are active loans.
	 * 
	 * @param itemType the type of item (e.g., "book", "CD", "journal")
	 * @param availableCopies the number of available copies
	 * @param totalCopies the total number of copies
	 * @return true if user confirms deletion, false otherwise
	 */
	private boolean confirmDeletion(String itemType, int availableCopies, int totalCopies) {
		int loanedCopies = totalCopies - availableCopies;

		if (loanedCopies > 0) {
			System.out.println("Warning: This " + itemType + " has " + loanedCopies
					+ " copies currently on loan.");
			System.out.print("Are you sure you want to delete it? This will affect active loans. (y/n): ");
		} else {
			System.out.print("Are you sure you want to delete this " + itemType + "? (y/n): ");
		}

		String confirmation = scanner.nextLine().trim().toLowerCase();
		return "y".equals(confirmation) || "yes".equals(confirmation);
	}
}
