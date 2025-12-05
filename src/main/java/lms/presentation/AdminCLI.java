package lms.presentation;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import lms.application.AuthService;
import lms.application.BookDTO;
import lms.application.BookService;
import lms.application.CDService;
import lms.application.JournalService;
import lms.application.LoanQueryService;
import lms.application.LoanService;
import lms.application.LoanStatsService;
import lms.application.ServiceContext;
import lms.application.UserDTO;
import lms.application.UserService;
import lms.application.search.SearchCriteria;
import lms.application.search.SearchStrategy;
import lms.domain.Book;
import lms.domain.CD;
import lms.domain.Journal;
import lms.domain.Loan;
import lms.domain.Role;
import lms.domain.exception.PermissionDeniedException;
import lms.domain.exception.UserNotFoundException;

/**
 * Administrator command-line interface for the Library Management System.
 * 
 * This interface provides administrators with all the tools they need to manage
 * the library, including adding/updating/deleting books, CDs, and journals,
 * managing user accounts, handling loans, and viewing system reports.
 * 
 * The class has been refactored to use a ServiceContext object instead of
 * receiving 8 individual service parameters. This makes the code cleaner
 * and meets SonarQube's recommendation of keeping constructor parameters at 7 or fewer.
 * 
 * @author Majd Awwad
 * @version 3.1
 */
public class AdminCLI implements CLI {

    private static final String BOOK_TYPE = "book";
    private static final String CD_TYPE = "cd";
    private static final String JOURNAL_TYPE = "journal";
    
    // UI Messages
    private static final String INVALID_CHOICE = "Invalid choice";
    private static final String CHOOSE_OPTION = "Choose an option: ";
    private static final String ENTER_TOTAL_COPIES = "Enter total copies: ";
    private static final String COPIES = "Copies";
    private static final String TITLE = "Title";
    private static final String TOTAL_COPIES_IN_LIBRARY = "Total Copies in Library: ";
    private static final String AVAILABLE_COPIES = "Available Copies: ";
    private static final String INVALID_CRITERIA_CHOICE = "Invalid criteria choice";
    private static final String SEARCH_TERM_EMPTY = "Search term cannot be empty";
    private static final String ERROR_DURING_SEARCH = "Error during search/filter";
    private static final String TIP_PARTIAL_ID = "Tip: You can enter partial ID (e.g., first 6-8 characters)";
    private static final String ENTER_TITLE_SEARCH = "Enter title to search: ";
    private static final String SEARCH_FILTER_RESULTS = "Search/Filter Results";
    private static final String TOTAL_RESULTS = "Total results: ";
    private static final String ENTER_NEW_VALUES = "Enter new values (leave blank to keep current value):";
    private static final String NEW_TITLE_PREFIX = "New title [";
    private static final String NEW_TOTAL_COPIES_PREFIX = "New total copies [";
    private static final String INVALID_COPIES_FORMAT = "Invalid copies format, keeping current value";
    private static final String TITLE_LABEL = "Title: ";
    private static final String COPIES_LABEL = "Copies: ";
    private static final String DELETION_CANCELLED = "Deletion cancelled.";
    private static final String FORMAT_TABLE_4COL = "%-10s %-40s %-30s %-15s";
    private static final String TIP_PARTIAL_MATCHES = "Tip: Partial matches work";
    private static final String FORMAT_TABLE_4COL_WIDE = "%-10s %-50s %-40s %-15s";
    
    private final Scanner scanner = new Scanner(System.in);
    private final UserService userService;
    private final BookService bookService;
    private final AuthService authService;
    private final LoanService loanService;
    private final CDService cdService;
    private final JournalService journalService;
    private final LoanStatsService loanStatsService;
    private final LoanQueryService loanQueryService;

    /**
     * Creates a new AdminCLI with access to all necessary services.
     * 
     * By using a ServiceContext object, we keep the constructor simple
     * with just one parameter instead of passing 8 separate services.
     * 
     * @param context contains all the services needed for admin operations
     */
    public AdminCLI(ServiceContext context) {
        this.userService = context.getUserService();
        this.bookService = context.getBookService();
        this.authService = context.getAuthService();
        this.loanService = context.getLoanService();
        this.cdService = context.getCdService();
        this.journalService = context.getJournalService();
        this.loanStatsService = context.getLoanStatsService();
        this.loanQueryService = context.getLoanQueryService();
    }

    /**
     * Old constructor kept for backward compatibility.
     * 
     * This constructor is deprecated because it violates SonarQube's guideline
     * of having no more than 7 parameters. Please use the new ServiceContext-based
     * constructor instead.
     * 
     * @deprecated Use {@link #AdminCLI(ServiceContext)} instead
     */
    @Deprecated
    public AdminCLI(UserService userService, BookService bookService, AuthService authService,
                    LoanService loanService, CDService cdService, JournalService journalService,
                    LoanStatsService loanStatsService, LoanQueryService loanQueryService) {
        this.userService = userService;
        this.bookService = bookService;
        this.authService = authService;
        this.loanService = loanService;
        this.cdService = cdService;
        this.journalService = journalService;
        this.loanStatsService = loanStatsService;
        this.loanQueryService = loanQueryService;
    }

    @Override
    public void start() throws IllegalAccessException {
        if (AuthService.getInstance().getCurrentUser().role() != Role.ADMIN) {
            throw new IllegalAccessException("Only administrators can access this menu.");
        }

        boolean running = true;
        while (running) {
            showAdminMenu();
            String choice = scanner.nextLine().trim();
            running = handleMenuChoice(choice);
        }
    }

    private boolean handleMenuChoice(String choice) {
        switch (choice) {
            case "1": handleAddItem(); break;
            case "2": handleViewAllItems(); break;
            case "3": handleSearchAndFilter(); break;
            case "4": handleUpdateItem(); break;
            case "5": handleDeleteItem(); break;
            case "6": handleAddUser(); break;
            case "7": handleViewAllUsers(); break;
            case "8": handleUpdateUser(); break;
            case "9": handleDeleteUser(); break;
            case "10": handleViewReports(); break;
            case "11": handleLoanManagement(); break;
            case "12": authService.logout(); return false;
            default: CLIHelper.printError(INVALID_CHOICE); break;
        }
        return true;
    }

    private void showAdminMenu() {
        CLIHelper.printHeader("ADMIN MENU");
        CLILogger.info("Item Management:");
        CLILogger.info("1. Add Item");
        CLILogger.info("2. View All Items");
        CLILogger.info("3. Search & Filter Items");
        CLILogger.info("4. Update Item");
        CLILogger.info("5. Delete Item");
        CLILogger.info("");
        CLILogger.info("User Management:");
        CLILogger.info("6. Add User");
        CLILogger.info("7. View All Users");
        CLILogger.info("8. Update User");
        CLILogger.info("9. Delete User");
        CLILogger.info("");
        CLILogger.info("System:");
        CLILogger.info("10. View Reports");
        CLILogger.info("11. Loan Management");
        CLILogger.info("12. Logout");
        CLILogger.info("Enter your choice: ");
    }

    private void handleAddItem() {
        CLILogger.info("Choose the Type of Item to Add:");
        showItemsMenu();
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1": handleAddBook(); break;
            case "2": handleAddCD(); break;
            case "3": handleAddJournal(); break;
            default: CLIHelper.printError(INVALID_CHOICE); break;
        }
    }

    private void handleViewAllItems() {
        CLILogger.info("Choose item type to view:");
        showItemsMenu();
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1": handleViewAllBooks(); break;
            case "2": handleViewAllCDs(); break;
            case "3": handleViewAllJournals(); break;
            default: CLIHelper.printError(INVALID_CHOICE); break;
        }
    }

    private void handleSearchAndFilter() {
        CLILogger.info("Choose item type to search/filter:");
        showItemsMenu();
        String itemChoice = scanner.nextLine().trim();

        displaySearchCriteriaMenu();
        String criteriaChoice = scanner.nextLine().trim();
        
        switch (itemChoice) {
            case "1": searchFilterBooks(criteriaChoice); break;
            case "2": searchFilterCDs(criteriaChoice); break;
            case "3": searchFilterJournals(criteriaChoice); break;
            default: CLIHelper.printError("Invalid item type"); break;
        }
    }

    private void displaySearchCriteriaMenu() {
        CLILogger.info("Search/Filter by:");
        SearchCriteria[] criteria = SearchCriteria.values();
        for (int i = 0; i < criteria.length; i++) {
            CLILogger.info((i + 1) + ". " + criteria[i].getDisplayName());
        }
        CLILogger.info("Choose criteria: ");
    }

    private void handleUpdateItem() {
        CLILogger.info("Choose item type to update:");
        showItemsMenu();
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1": handleUpdateBook(); break;
            case "2": handleUpdateCD(); break;
            case "3": handleUpdateJournal(); break;
            default: CLIHelper.printError(INVALID_CHOICE); break;
        }
    }

    private void handleDeleteItem() {
        CLILogger.info("Choose item type to delete:");
        showItemsMenu();
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1": handleDeleteBook(); break;
            case "2": handleDeleteCD(); break;
            case "3": handleDeleteJournal(); break;
            default: CLIHelper.printError(INVALID_CHOICE); break;
        }
    }

    private void showItemsMenu() {
        CLIHelper.printHeader("Items Menu");
        CLILogger.info("1. Book");
        CLILogger.info("2. CD");
        CLILogger.info("3. Journal");
        CLILogger.info(CHOOSE_OPTION);
    }

    private void handleAddBook() {
        try {
            CLIHelper.printHeader("Add a New Book");

            CLILogger.info("Enter book title: ");
            String title = scanner.nextLine().trim();

            CLILogger.info("Enter author: ");
            String author = scanner.nextLine().trim();

            CLILogger.info("Enter ISBN: ");
            String isbn = scanner.nextLine().trim();

            CLILogger.info("Enter publisher: ");
            String publisher = scanner.nextLine().trim();

            CLILogger.info("Enter publication year: ");
            int year = Integer.parseInt(scanner.nextLine().trim());

            CLILogger.info("Enter category: ");
            String category = scanner.nextLine().trim();

            CLILogger.info(ENTER_TOTAL_COPIES);
            int totalCopies = Integer.parseInt(scanner.nextLine().trim());

            CLILogger.info("Enter language: ");
            String language = scanner.nextLine().trim();

            CLILogger.info("Enter shelf location: ");
            String shelfLocation = scanner.nextLine().trim();

            CLILogger.info("Enter description: ");
            String description = scanner.nextLine().trim();

            BookDTO bookDTO = new BookDTO(title, author, isbn, publisher, year, category, totalCopies, language, description, shelfLocation);
            Book book = bookService.addBook(
                AuthService.getInstance().getCurrentUser(),
                bookDTO
            );

            if (book != null) {
                CLIHelper.printSuccess("Book '" + title + "' added successfully");
            } else {
                CLIHelper.printError("Failed to add book");
            }

        } catch (NumberFormatException e) {
            CLIHelper.printError("Invalid number format. Please enter numeric values.");
        } catch (PermissionDeniedException e) {
            CLIHelper.printError("You do not have permission to add books. Admin only.");
        } catch (Exception e) {
            CLILogger.error("Error adding book", e);
        }
    }

    private void handleViewAllBooks() {
        CLIHelper.printHeader("All Books");

        List<Book> books = bookService.getAllBooks();

        if (books.isEmpty()) {
            CLILogger.info("No books available in the system.");
            return;
        }

        CLILogger.info(String.format("%-10s %-30s %-20s %-15s %-10s %-10s %-10s",
            "ID", "Title", "Author", "ISBN", "Year", "Copies", "Category"));

        for (Book book : books) {
            String displayId = book.getId().toString().substring(0, Math.min(8, book.getId().toString().length()));
            String displayTitle = CLIHelper.truncate(book.getTitle(), 28);
            String displayAuthor = CLIHelper.truncate(book.getAuthor(), 18);
            
            CLILogger.info(String.format("%-10s %-30s %-20s %-15s %-10d %-10s %-10s",
                displayId, displayTitle, displayAuthor,
                book.getIsbn(), book.getPublicationYear(),
                book.getAvailableCopies() + "/" + book.getTotalCopies(), book.getCategory()));
        }

        CLILogger.info("Total Books: " + books.size());
        
        long availableBooks = books.stream().filter(Book::isAvailable).count();
        int totalCopiesCount = books.stream().mapToInt(Book::getTotalCopies).sum();
        int availableCopiesCount = books.stream().mapToInt(Book::getAvailableCopies).sum();
        
        CLILogger.info("Books with Available Copies: " + availableBooks + "/" + books.size());
        CLILogger.info(TOTAL_COPIES_IN_LIBRARY + totalCopiesCount);
        CLILogger.info(AVAILABLE_COPIES + availableCopiesCount + "/" + totalCopiesCount);
    }

    private void searchFilterBooks(String criteriaChoice) {
        try {
            SearchCriteria criterion = SearchCriteria.fromChoice(criteriaChoice);
            
            if (criterion == null) {
                CLIHelper.printError(INVALID_CRITERIA_CHOICE);
                return;
            }
            
            displaySearchHint(criterion);
            
            String promptMessage = getSearchPromptMessage(criterion);
            CLILogger.info(promptMessage);
            String searchTerm = scanner.nextLine().trim();
            
            if (searchTerm.isEmpty()) {
                CLIHelper.printError(SEARCH_TERM_EMPTY);
                return;
            }
            
            SearchStrategy<Book> strategy = criterion.createStrategy();
            List<Book> results = bookService.searchBooks(strategy, searchTerm);
            
            displayBookResults(results);
            
        } catch (Exception e) {
            CLILogger.error(ERROR_DURING_SEARCH, e);
        }
    }

    private void displaySearchHint(SearchCriteria criterion) {
        String hint = switch (criterion) {
            case ID -> TIP_PARTIAL_ID;
            case TITLE -> "Tip: Partial matches work (e.g., 'Clean' finds 'Clean Code')";
            case AUTHOR -> "Tip: Partial matches work (e.g., 'Martin' finds all Martin authors)";
            case ISBN -> "Tip: You can enter partial ISBN numbers";
            case CATEGORY -> "Tip: Partial matches work (e.g., 'Eng' finds 'Engineering')";
            case YEAR -> "Tip: Enter exact year (e.g., 2020)";
            case AVAILABILITY -> "Tip: Enter 'y' or 'yes' to show only available books";
        };
        CLILogger.info(hint);
    }

    private String getSearchPromptMessage(SearchCriteria criterion) {
        return switch (criterion) {
            case ID -> "Enter Book ID: ";
            case TITLE -> ENTER_TITLE_SEARCH;
            case AUTHOR -> "Enter author to search: ";
            case ISBN -> "Enter ISBN: ";
            case YEAR -> "Enter publication year: ";
            case CATEGORY -> "Enter category/genre: ";
            case AVAILABILITY -> "Show only available books? (y/n): ";
        };
    }

    private void displayBookResults(List<Book> books) {
        if (books.isEmpty()) {
            CLILogger.info("No books found matching the criteria.");
            return;
        }

        CLIHelper.printHeader(SEARCH_FILTER_RESULTS);
        CLILogger.info(String.format("%-10s %-30s %-20s %-15s %-10s %-10s %-10s",
            "ID", "Title", "Author", "ISBN", "Year", "Copies", "Category"));

        for (Book book : books) {
            String displayId = book.getId().toString().substring(0, Math.min(8, book.getId().toString().length()));
            String displayTitle = CLIHelper.truncate(book.getTitle(), 28);
            String displayAuthor = CLIHelper.truncate(book.getAuthor(), 18);
            
            CLILogger.info(String.format("%-10s %-30s %-20s %-15s %-10d %-10s %-10s",
                displayId, displayTitle, displayAuthor,
                book.getIsbn(), book.getPublicationYear(),
                book.getAvailableCopies() + "/" + book.getTotalCopies(), book.getCategory()));
        }
        CLILogger.info(TOTAL_RESULTS + books.size());
    }

    private void handleUpdateBook() {
        CLILogger.info("Enter the Book ID (full or partial): ");
        String bookIdStr = scanner.nextLine().trim();

        try {
            Book existingBook = findBookByIdOrSubId(bookIdStr);

            if (existingBook == null) {
                CLIHelper.printError("No book found with ID: " + bookIdStr);
                return;
            }

            CLIHelper.printHeader("Current Book Details");
            displaySingleBook(existingBook);
            
            CLILogger.info(ENTER_NEW_VALUES);

            CLILogger.info(NEW_TITLE_PREFIX + existingBook.getTitle() + "]: ");
            String title = scanner.nextLine().trim();
            if (title.isBlank()) {
                title = null;
            }

            CLILogger.info("New author [" + existingBook.getAuthor() + "]: ");
            String author = scanner.nextLine().trim();
            if (author.isBlank()) {
                author = null;
            }

            CLILogger.info("New ISBN [" + existingBook.getIsbn() + "]: ");
            String isbn = scanner.nextLine().trim();
            if (isbn.isBlank()) {
                isbn = null;
            }

            CLILogger.info("New publisher [" + existingBook.getPublisher() + "]: ");
            String publisher = scanner.nextLine().trim();
            if (publisher.isBlank()) {
                publisher = null;
            }

            CLILogger.info("New publication year [" + existingBook.getPublicationYear() + "]: ");
            String yearStr = scanner.nextLine().trim();
            Integer year = null;
            if (!yearStr.isBlank()) {
                try {
                    year = Integer.parseInt(yearStr);
                } catch (NumberFormatException e) {
                    CLIHelper.printWarning("Invalid year format, keeping current value");
                }
            }

            CLILogger.info("New category [" + existingBook.getCategory() + "]: ");
            String category = scanner.nextLine().trim();
            if (category.isBlank()) {
                category = null;
            }

            CLILogger.info(NEW_TOTAL_COPIES_PREFIX + existingBook.getTotalCopies() + "]: ");
            String copiesStr = scanner.nextLine().trim();
            Integer totalCopies = null;
            if (!copiesStr.isBlank()) {
                try {
                    totalCopies = Integer.parseInt(copiesStr);
                } catch (NumberFormatException e) {
                    CLIHelper.printWarning(INVALID_COPIES_FORMAT);
                }
            }

            CLILogger.info("New language [" + existingBook.getLanguage() + "]: ");
            String language = scanner.nextLine().trim();
            if (language.isBlank()) {
                language = null;
            }

            CLILogger.info("New shelf location [" + existingBook.getShelfLocation() + "]: ");
            String shelfLocation = scanner.nextLine().trim();
            if (shelfLocation.isBlank()) {
                shelfLocation = null;
            }

            BookDTO bookDTO = new BookDTO(title, author, isbn, publisher, year, category, totalCopies, language, null, shelfLocation);
            boolean updated = bookService.updateBook(
                AuthService.getInstance().getCurrentUser(),
                existingBook.getId(),
                bookDTO
            );

            if (updated) {
                CLIHelper.printSuccess("Book updated successfully");
                Book updatedBook = bookService.getBookById(existingBook.getId());
                displaySingleBook(updatedBook);
            } else {
                CLIHelper.printError("Failed to update book");
            }

        } catch (PermissionDeniedException e) {
            CLIHelper.printError("You do not have permission to update books. Admin only.");
        } catch (Exception e) {
            CLILogger.error("Error updating book", e);
        }
    }

    private void displaySingleBook(Book book) {
        CLIHelper.printHeader("Book Details");
        CLILogger.info("ID: " + book.getId());
        CLILogger.info(TITLE_LABEL + book.getTitle());
        CLILogger.info("Author: " + book.getAuthor());
        CLILogger.info("ISBN: " + book.getIsbn());
        CLILogger.info("Publisher: " + book.getPublisher());
        CLILogger.info("Publication Year: " + book.getPublicationYear());
        CLILogger.info("Category: " + book.getCategory());
        CLILogger.info("Language: " + book.getLanguage());
        CLILogger.info(COPIES_LABEL + book.getAvailableCopies() + "/" + book.getTotalCopies());
        CLILogger.info("Shelf Location: " + book.getShelfLocation());
    }

    private void handleDeleteBook() {
        CLILogger.info("Enter the Book ID (full or partial): ");
        String bookIdStr = scanner.nextLine().trim();

        try {
            Book existingBook = findBookByIdOrSubId(bookIdStr);

            if (existingBook == null) {
                CLIHelper.printError("No book found with ID: " + bookIdStr);
                return;
            }

            displayBookForDeletion(existingBook);

            if (!confirmDeletion(BOOK_TYPE, existingBook.getAvailableCopies(), existingBook.getTotalCopies())) {
                CLILogger.info(DELETION_CANCELLED);
                return;
            }

            boolean deleted = bookService.deleteBook(AuthService.getInstance().getCurrentUser(), existingBook.getId());

            if (deleted) {
                CLIHelper.printSuccess("Book deleted successfully");
            } else {
                CLIHelper.printError("Failed to delete book");
            }

        } catch (PermissionDeniedException e) {
            CLIHelper.printError("You do not have permission to delete books. Admin only.");
        } catch (Exception e) {
            CLILogger.error("Error deleting book", e);
        }
    }

    private void displayBookForDeletion(Book book) {
        CLIHelper.printHeader("Book to be Deleted");
        CLILogger.info("ID: " + book.getId());
        CLILogger.info(TITLE_LABEL + book.getTitle());
        CLILogger.info("Author: " + book.getAuthor());
        CLILogger.info(AVAILABLE_COPIES + book.getAvailableCopies() + "/" + book.getTotalCopies());
    }

    private boolean confirmDeletion(String itemType, int availableCopies, int totalCopies) {
        int loanedCopies = totalCopies - availableCopies;
        
        if (loanedCopies > 0) {
            CLIHelper.printWarning("This " + itemType + " has " + loanedCopies + " copies currently on loan");
        }
        
        return CLIHelper.confirmAction(scanner, "Are you sure you want to delete this " + itemType + "?");
    }

    private Book findBookByIdOrSubId(String bookIdStr) {
        List<Book> books = bookService.getAllBooks();
        
        for (Book book : books) {
            if (book.getId().toString().equals(bookIdStr) || 
                book.getId().toString().startsWith(bookIdStr)) {
                return book;
            }
        }
        
        return null;
    }

    private void handleAddCD() {
        try {
            CLIHelper.printHeader("Add a New CD");

            CLILogger.info("Enter CD title: ");
            String title = scanner.nextLine().trim();

            CLILogger.info("Enter artist: ");
            String artist = scanner.nextLine().trim();

            CLILogger.info(ENTER_TOTAL_COPIES);
            int totalCopies = Integer.parseInt(scanner.nextLine().trim());

            CD cd = cdService.addCD(AuthService.getInstance().getCurrentUser(), title, artist, totalCopies);

            if (cd != null) {
                CLIHelper.printSuccess("CD '" + title + "' by " + artist + " added successfully");
            } else {
                CLIHelper.printError("Failed to add CD");
            }

        } catch (NumberFormatException e) {
            CLIHelper.printError("Invalid number format. Please enter numeric values for copies.");
        } catch (PermissionDeniedException e) {
            CLIHelper.printError("You do not have permission to add CDs. Admin only.");
        } catch (Exception e) {
            CLILogger.error("Error adding CD", e);
        }
    }

    private void handleViewAllCDs() {
        CLIHelper.printHeader("All CDs");

        List<CD> cds = cdService.getAllCDs();

        if (cds.isEmpty()) {
            CLILogger.info("No CDs available in the system.");
            return;
        }

        CLILogger.info(String.format("%-10s %-40s %-30s %-15s", "ID", "Title", "Artist", "Copies"));

        for (CD cd : cds) {
            String displayId = cd.getId().toString().substring(0, Math.min(8, cd.getId().toString().length()));
            String displayTitle = CLIHelper.truncate(cd.getTitle(), 38);
            String displayArtist = CLIHelper.truncate(cd.getArtist(), 28);
            String copies = cd.getAvailableCopies() + "/" + cd.getTotalCopies();
            
            CLILogger.info(String.format("%-10s %-40s %-30s %-15s",
                displayId, displayTitle, displayArtist, copies));
        }

        CLILogger.info("Total CDs: " + cds.size());
        
        long availableCDs = cds.stream().filter(CD::isAvailable).count();
        int totalCopiesCount = cds.stream().mapToInt(CD::getTotalCopies).sum();
        int availableCopiesCount = cds.stream().mapToInt(CD::getAvailableCopies).sum();
        
        CLILogger.info("CDs with Available Copies: " + availableCDs + "/" + cds.size());
        CLILogger.info(TOTAL_COPIES_IN_LIBRARY + totalCopiesCount);
        CLILogger.info(AVAILABLE_COPIES + availableCopiesCount + "/" + totalCopiesCount);
    }

    private void searchFilterCDs(String criteriaChoice) {
        try {
            SearchStrategy<CD> strategy = getCDSearchStrategy(criteriaChoice);
            
            if (strategy == null) {
                CLIHelper.printError(INVALID_CRITERIA_CHOICE);
                return;
            }
            
            displayCDSearchHint(criteriaChoice);
            
            String promptMessage = getCDSearchPromptMessage(criteriaChoice);
            CLILogger.info(promptMessage);
            String searchTerm = scanner.nextLine().trim();
            
            if (searchTerm.isEmpty()) {
                CLIHelper.printError(SEARCH_TERM_EMPTY);
                return;
            }
            
            List<CD> results = cdService.searchCDs(strategy, searchTerm);
            displayCDResults(results);
            
        } catch (Exception e) {
            CLILogger.error(ERROR_DURING_SEARCH, e);
        }
    }

    private SearchStrategy<CD> getCDSearchStrategy(String choice) {
        return switch (choice) {
            case "1" -> new lms.application.search.SearchCDByIdStrategy();
            case "2" -> new lms.application.search.SearchCDByTitleStrategy();
            case "3" -> new lms.application.search.SearchCDByArtistStrategy();
            case "7" -> new lms.application.search.FilterCDByAvailabilityStrategy();
            default -> null;
        };
    }

    private void displayCDSearchHint(String choice) {
        String hint = switch (choice) {
            case "1" -> TIP_PARTIAL_ID;
            case "2" -> TIP_PARTIAL_MATCHES;
            case "3" -> TIP_PARTIAL_MATCHES;
            case "7" -> "Tip: Enter 'y' or 'yes' to show only available CDs";
            default -> "Tip: Enter your search criteria";
        };
        CLILogger.info(hint);
    }

    private String getCDSearchPromptMessage(String choice) {
        return switch (choice) {
            case "1" -> "Enter CD ID: ";
            case "2" -> ENTER_TITLE_SEARCH;
            case "3" -> "Enter artist to search: ";
            case "7" -> "Show only available CDs? (y/n): ";
            default -> "Enter search term: ";
        };
    }

    private void displayCDResults(List<CD> cds) {
        if (cds.isEmpty()) {
            CLILogger.info("No CDs found matching the criteria.");
            return;
        }

        CLIHelper.printHeader(SEARCH_FILTER_RESULTS);
        CLILogger.info(String.format(FORMAT_TABLE_4COL, "ID", TITLE, "Artist", COPIES));

        for (CD cd : cds) {
            String displayId = cd.getId().toString().substring(0, Math.min(8, cd.getId().toString().length()));
            String displayTitle = CLIHelper.truncate(cd.getTitle(), 38);
            String displayArtist = CLIHelper.truncate(cd.getArtist(), 28);
            String copies = cd.getAvailableCopies() + "/" + cd.getTotalCopies();
            
            CLILogger.info(String.format(FORMAT_TABLE_4COL,
                displayId, displayTitle, displayArtist, copies));
        }
        CLILogger.info(TOTAL_RESULTS + cds.size());
    }

    private void handleUpdateCD() {
        CLILogger.info("Enter the CD ID (full or partial): ");
        String cdIdStr = scanner.nextLine().trim();

        try {
            CD existingCD = findCDByIdOrSubId(cdIdStr);

            if (existingCD == null) {
                CLIHelper.printError("No CD found with ID: " + cdIdStr);
                return;
            }

            CLIHelper.printHeader("Current CD Details");
            displaySingleCD(existingCD);
            
            CLILogger.info(ENTER_NEW_VALUES);

            CLILogger.info(NEW_TITLE_PREFIX + existingCD.getTitle() + "]: ");
            String title = scanner.nextLine().trim();
            if (title.isBlank()) {
                title = null;
            }

            CLILogger.info("New artist [" + existingCD.getArtist() + "]: ");
            String artist = scanner.nextLine().trim();
            if (artist.isBlank()) {
                artist = null;
            }

            CLILogger.info(NEW_TOTAL_COPIES_PREFIX + existingCD.getTotalCopies() + "]: ");
            String copiesStr = scanner.nextLine().trim();
            Integer totalCopies = null;
            if (!copiesStr.isBlank()) {
                try {
                    totalCopies = Integer.parseInt(copiesStr);
                } catch (NumberFormatException e) {
                    CLIHelper.printWarning(INVALID_COPIES_FORMAT);
                }
            }

            CD updated = cdService.updateCD(
                AuthService.getInstance().getCurrentUser(),
                existingCD.getId(),
                title, artist, totalCopies
            );

            if (updated != null) {
                CLIHelper.printSuccess("CD updated successfully");
                displaySingleCD(updated);
            } else {
                CLIHelper.printError("Failed to update CD");
            }

        } catch (PermissionDeniedException e) {
            CLIHelper.printError("You do not have permission to update CDs. Admin only.");
        } catch (Exception e) {
            CLILogger.error("Error updating CD", e);
        }
    }

    private void displaySingleCD(CD cd) {
        CLIHelper.printHeader("CD Details");
        CLILogger.info("ID: " + cd.getId());
        CLILogger.info(TITLE_LABEL + cd.getTitle());
        CLILogger.info("Artist: " + cd.getArtist());
        CLILogger.info(COPIES_LABEL + cd.getAvailableCopies() + "/" + cd.getTotalCopies());
    }

    private void handleDeleteCD() {
        CLILogger.info("Enter the CD ID (full or partial): ");
        String cdIdStr = scanner.nextLine().trim();

        try {
            CD existingCD = findCDByIdOrSubId(cdIdStr);

            if (existingCD == null) {
                CLIHelper.printError("No CD found with ID: " + cdIdStr);
                return;
            }

            displayCDForDeletion(existingCD);

            if (!confirmDeletion(CD_TYPE, existingCD.getAvailableCopies(), existingCD.getTotalCopies())) {
                CLILogger.info(DELETION_CANCELLED);
                return;
            }

            boolean deleted = cdService.deleteCD(AuthService.getInstance().getCurrentUser(), existingCD.getId());

            if (deleted) {
                CLIHelper.printSuccess("CD deleted successfully");
            } else {
                CLIHelper.printError("Failed to delete CD");
            }

        } catch (PermissionDeniedException e) {
            CLIHelper.printError("You do not have permission to delete CDs. Admin only.");
        } catch (Exception e) {
            CLILogger.error("Error deleting CD", e);
        }
    }

    private void displayCDForDeletion(CD cd) {
        CLIHelper.printHeader("CD to be Deleted");
        CLILogger.info("ID: " + cd.getId());
        CLILogger.info(TITLE_LABEL + cd.getTitle());
        CLILogger.info("Artist: " + cd.getArtist());
        CLILogger.info(AVAILABLE_COPIES + cd.getAvailableCopies() + "/" + cd.getTotalCopies());
    }

    private CD findCDByIdOrSubId(String cdIdStr) {
        List<CD> cds = cdService.getAllCDs();
        
        for (CD cd : cds) {
            if (cd.getId().toString().equals(cdIdStr) || 
                cd.getId().toString().startsWith(cdIdStr)) {
                return cd;
            }
        }
        
        return null;
    }

    private void handleAddJournal() {
        try {
            CLIHelper.printHeader("Add a New Journal");

            CLILogger.info("Enter journal title: ");
            String title = scanner.nextLine().trim();

            CLILogger.info("Enter author/publisher: ");
            String author = scanner.nextLine().trim();

            CLILogger.info(ENTER_TOTAL_COPIES);
            int totalCopies = Integer.parseInt(scanner.nextLine().trim());

            Journal journal = journalService.addJournal(AuthService.getInstance().getCurrentUser(), title, author, totalCopies);

            if (journal != null) {
                CLIHelper.printSuccess("Journal '" + title + "' by " + author + " added successfully");
            } else {
                CLIHelper.printError("Failed to add journal");
            }

        } catch (NumberFormatException e) {
            CLIHelper.printError("Invalid number format. Please enter numeric values for copies.");
        } catch (PermissionDeniedException e) {
            CLIHelper.printError("You do not have permission to add journals. Admin only.");
        } catch (Exception e) {
            CLILogger.error("Error adding journal", e);
        }
    }

    private void handleViewAllJournals() {
        CLIHelper.printHeader("All Journals");

        List<Journal> journals = journalService.getAllJournals();

        if (journals.isEmpty()) {
            CLILogger.info("No journals available in the system.");
            return;
        }

        CLILogger.info(String.format("%-10s %-50s %-40s %-15s", "ID", "Title", "Author/Publisher", "Copies"));

        for (Journal journal : journals) {
            String displayId = journal.getId().toString().substring(0, Math.min(8, journal.getId().toString().length()));
            String displayTitle = CLIHelper.truncate(journal.getTitle(), 48);
            String displayAuthor = CLIHelper.truncate(journal.getAuthor(), 38);
            String copies = journal.getAvailableCopies() + "/" + journal.getTotalCopies();
            
            CLILogger.info(String.format("%-10s %-50s %-40s %-15s",
                displayId, displayTitle, displayAuthor, copies));
        }

        CLILogger.info("Total Journals: " + journals.size());
        
        long availableJournals = journals.stream().filter(Journal::isAvailable).count();
        int totalCopiesCount = journals.stream().mapToInt(Journal::getTotalCopies).sum();
        int availableCopiesCount = journals.stream().mapToInt(Journal::getAvailableCopies).sum();
        
        CLILogger.info("Journals with Available Copies: " + availableJournals + "/" + journals.size());
        CLILogger.info(TOTAL_COPIES_IN_LIBRARY + totalCopiesCount);
        CLILogger.info(AVAILABLE_COPIES + availableCopiesCount + "/" + totalCopiesCount);
    }

    private void searchFilterJournals(String criteriaChoice) {
        try {
            SearchStrategy<Journal> strategy = getJournalSearchStrategy(criteriaChoice);
            
            if (strategy == null) {
                CLIHelper.printError(INVALID_CRITERIA_CHOICE);
                return;
            }
            
            displayJournalSearchHint(criteriaChoice);
            
            String promptMessage = getJournalSearchPromptMessage(criteriaChoice);
            CLILogger.info(promptMessage);
            String searchTerm = scanner.nextLine().trim();
            
            if (searchTerm.isEmpty()) {
                CLIHelper.printError(SEARCH_TERM_EMPTY);
                return;
            }
            
            List<Journal> results = journalService.searchJournals(strategy, searchTerm);
            displayJournalResults(results);
            
        } catch (Exception e) {
            CLILogger.error(ERROR_DURING_SEARCH, e);
        }
    }

    private SearchStrategy<Journal> getJournalSearchStrategy(String choice) {
        return switch (choice) {
            case "1" -> new lms.application.search.SearchJournalByIdStrategy();
            case "2" -> new lms.application.search.SearchJournalByTitleStrategy();
            case "3" -> new lms.application.search.SearchJournalByAuthorStrategy();
            case "7" -> new lms.application.search.FilterJournalByAvailabilityStrategy();
            default -> null;
        };
    }

    private void displayJournalSearchHint(String choice) {
        String hint = switch (choice) {
            case "1" -> TIP_PARTIAL_ID;
            case "2" -> TIP_PARTIAL_MATCHES;
            case "3" -> TIP_PARTIAL_MATCHES;
            case "7" -> "Tip: Enter 'y' or 'yes' to show only available journals";
            default -> "Tip: Enter your search criteria";
        };
        CLILogger.info(hint);
    }

    private String getJournalSearchPromptMessage(String choice) {
        return switch (choice) {
            case "1" -> "Enter Journal ID: ";
            case "2" -> ENTER_TITLE_SEARCH;
            case "3" -> "Enter author/publisher to search: ";
            case "7" -> "Show only available journals? (y/n): ";
            default -> "Enter search term: ";
        };
    }

    private void displayJournalResults(List<Journal> journals) {
        if (journals.isEmpty()) {
            CLILogger.info("No journals found matching the criteria.");
            return;
        }

        CLIHelper.printHeader(SEARCH_FILTER_RESULTS);
        CLILogger.info(String.format(FORMAT_TABLE_4COL_WIDE, "ID", TITLE, "Author/Publisher", COPIES));

        for (Journal journal : journals) {
            String displayId = journal.getId().toString().substring(0, Math.min(8, journal.getId().toString().length()));
            String displayTitle = CLIHelper.truncate(journal.getTitle(), 48);
            String displayAuthor = CLIHelper.truncate(journal.getAuthor(), 38);
            String copies = journal.getAvailableCopies() + "/" + journal.getTotalCopies();
            
            CLILogger.info(String.format(FORMAT_TABLE_4COL_WIDE,
                displayId, displayTitle, displayAuthor, copies));
        }
        CLILogger.info(TOTAL_RESULTS + journals.size());
    }

    private void handleUpdateJournal() {
        CLILogger.info("Enter the Journal ID (full or partial): ");
        String journalIdStr = scanner.nextLine().trim();

        try {
            Journal existingJournal = findJournalByIdOrSubId(journalIdStr);

            if (existingJournal == null) {
                CLIHelper.printError("No journal found with ID: " + journalIdStr);
                return;
            }

            CLIHelper.printHeader("Current Journal Details");
            displaySingleJournal(existingJournal);
            
            CLILogger.info(ENTER_NEW_VALUES);

            CLILogger.info(NEW_TITLE_PREFIX + existingJournal.getTitle() + "]: ");
            String title = scanner.nextLine().trim();
            if (title.isBlank()) {
                title = null;
            }

            CLILogger.info("New author/publisher [" + existingJournal.getAuthor() + "]: ");
            String author = scanner.nextLine().trim();
            if (author.isBlank()) {
                author = null;
            }

            CLILogger.info(NEW_TOTAL_COPIES_PREFIX + existingJournal.getTotalCopies() + "]: ");
            String copiesStr = scanner.nextLine().trim();
            Integer totalCopies = null;
            if (!copiesStr.isBlank()) {
                try {
                    totalCopies = Integer.parseInt(copiesStr);
                } catch (NumberFormatException e) {
                    CLIHelper.printWarning(INVALID_COPIES_FORMAT);
                }
            }

            Journal updated = journalService.updateJournal(
                AuthService.getInstance().getCurrentUser(),
                existingJournal.getId(),
                title, author, totalCopies
            );

            if (updated != null) {
                CLIHelper.printSuccess("Journal updated successfully");
                displaySingleJournal(updated);
            } else {
                CLIHelper.printError("Failed to update journal");
            }

        } catch (PermissionDeniedException e) {
            CLIHelper.printError("You do not have permission to update journals. Admin only.");
        } catch (Exception e) {
            CLILogger.error("Error updating journal", e);
        }
    }

    private void displaySingleJournal(Journal journal) {
        CLIHelper.printHeader("Journal Details");
        CLILogger.info("ID: " + journal.getId());
        CLILogger.info(TITLE_LABEL + journal.getTitle());
        CLILogger.info("Author/Publisher: " + journal.getAuthor());
        CLILogger.info(COPIES_LABEL + journal.getAvailableCopies() + "/" + journal.getTotalCopies());
    }

    private void handleDeleteJournal() {
        CLILogger.info("Enter the Journal ID (full or partial): ");
        String journalIdStr = scanner.nextLine().trim();

        try {
            Journal existingJournal = findJournalByIdOrSubId(journalIdStr);

            if (existingJournal == null) {
                CLIHelper.printError("No journal found with ID: " + journalIdStr);
                return;
            }

            displayJournalForDeletion(existingJournal);

            if (!confirmDeletion(JOURNAL_TYPE, existingJournal.getAvailableCopies(), existingJournal.getTotalCopies())) {
                CLILogger.info(DELETION_CANCELLED);
                return;
            }

            boolean deleted = journalService.deleteJournal(AuthService.getInstance().getCurrentUser(), existingJournal.getId());

            if (deleted) {
                CLIHelper.printSuccess("Journal deleted successfully");
            } else {
                CLIHelper.printError("Failed to delete journal");
            }

        } catch (PermissionDeniedException e) {
            CLIHelper.printError("You do not have permission to delete journals. Admin only.");
        } catch (Exception e) {
            CLILogger.error("Error deleting journal", e);
        }
    }

    private void displayJournalForDeletion(Journal journal) {
        CLIHelper.printHeader("Journal to be Deleted");
        CLILogger.info("ID: " + journal.getId());
        CLILogger.info(TITLE_LABEL + journal.getTitle());
        CLILogger.info("Author/Publisher: " + journal.getAuthor());
        CLILogger.info(AVAILABLE_COPIES + journal.getAvailableCopies() + "/" + journal.getTotalCopies());
    }

    private Journal findJournalByIdOrSubId(String journalIdStr) {
        List<Journal> journals = journalService.getAllJournals();
        
        for (Journal journal : journals) {
            if (journal.getId().toString().equals(journalIdStr) || 
                journal.getId().toString().startsWith(journalIdStr)) {
                return journal;
            }
        }
        
        return null;
    }

    private void handleAddUser() {
        try {
            CLIHelper.printHeader("Add a New User");

            CLILogger.info("Enter username: ");
            String username = scanner.nextLine().trim();

            CLILogger.info("Enter password: ");
            String password = scanner.nextLine().trim();

            CLILogger.info("Enter first name: ");
            String firstName = scanner.nextLine().trim();

            CLILogger.info("Enter last name: ");
            String lastName = scanner.nextLine().trim();

            CLILogger.info("Enter email: ");
            String email = scanner.nextLine().trim();

            CLILogger.info("Choose role (1=MEMBER, 2=LIBRARIAN, 3=ADMIN): ");
            String roleChoice = scanner.nextLine().trim();
            
            Role role = switch (roleChoice) {
                case "1" -> Role.MEMBER;
                case "2" -> Role.LIBRARIAN;
                case "3" -> Role.ADMIN;
                default -> Role.MEMBER;
            };

            UserDTO newUser = userService.registerUser(username, password, firstName, lastName, email, role);
            
            CLIHelper.printSuccess("User '" + newUser.username() + "' created successfully");

        } catch (IllegalArgumentException e) {
            CLIHelper.printError(e.getMessage());
        } catch (Exception e) {
            CLILogger.error("Error creating user", e);
        }
    }

    private void handleViewAllUsers() {
        CLIHelper.printHeader("All Users");

        List<UserDTO> users = userService.getAllUsers();

        if (users.isEmpty()) {
            CLILogger.info("No users in the system.");
            return;
        }

        CLILogger.info(String.format("%-20s %-25s %-30s %-15s", "Username", "Name", "Email", "Role"));

        for (UserDTO user : users) {
            String fullName = user.firstName() + " " + user.lastName();
            CLILogger.info(String.format("%-20s %-25s %-30s %-15s",
                user.username(),
                CLIHelper.truncate(fullName, 23),
                CLIHelper.truncate(user.email(), 28),
                user.role()));
        }

        CLILogger.info("Total Users: " + users.size());
    }

    private void handleUpdateUser() {
        CLILogger.info("Enter the username of the user to update: ");
        String username = scanner.nextLine().trim();

        try {
            UserDTO existingUser = userService.getUserByUsername(username);
            
            CLIHelper.printHeader("Current User Details");
            CLILogger.info("Username: " + existingUser.username());
            CLILogger.info("Name: " + existingUser.firstName() + " " + existingUser.lastName());
            CLILogger.info("Email: " + existingUser.email());
            CLILogger.info("Role: " + existingUser.role());

            CLILogger.info("Enter new values (leave blank to keep current value):");

            CLILogger.info("New first name [" + existingUser.firstName() + "]: ");
            String firstName = scanner.nextLine().trim();
            if (firstName.isBlank()) {
                firstName = null;
            }

            CLILogger.info("New last name [" + existingUser.lastName() + "]: ");
            String lastName = scanner.nextLine().trim();
            if (lastName.isBlank()) {
                lastName = null;
            }

            CLILogger.info("New email [" + existingUser.email() + "]: ");
            String email = scanner.nextLine().trim();
            if (email.isBlank()) {
                email = null;
            }

            CLILogger.info("New password (leave blank to keep current): ");
            String password = scanner.nextLine().trim();
            if (password.isBlank()) {
                password = null;
            }

            CLILogger.info("New role (1=MEMBER, 2=LIBRARIAN, 3=ADMIN) [" + existingUser.role() + "]: ");
            String roleChoice = scanner.nextLine().trim();
            Role role = null;
            if (!roleChoice.isBlank()) {
                role = switch (roleChoice) {
                    case "1" -> Role.MEMBER;
                    case "2" -> Role.LIBRARIAN;
                    case "3" -> Role.ADMIN;
                    default -> null;
                };
            }

            userService.updateUser(existingUser, existingUser.userID(), firstName, email, password, role);
            CLIHelper.printSuccess("User updated successfully");

        } catch (UserNotFoundException e) {
            CLIHelper.printError("No user found with username: " + username);
        } catch (Exception e) {
            CLILogger.error("Error updating user", e);
        }
    }

    private void handleDeleteUser() {
        CLILogger.info("Enter the username of the user to delete: ");
        String username = scanner.nextLine().trim();

        try {
            UserDTO user = userService.getUserByUsername(username);
            CLILogger.info("User found: " + user.username());

            if (CLIHelper.confirmAction(scanner, "Are you sure you want to delete this user?")) {
                userService.deleteByUsername(user.username());
                CLIHelper.printSuccess("User deleted successfully");
            } else {
                CLILogger.info(DELETION_CANCELLED);
            }
        } catch (UserNotFoundException e) {
            CLIHelper.printError("No user found with username: " + username);
        } catch (Exception e) {
            CLILogger.error("Error deleting user", e);
        }
    }

    private void handleViewReports() {
        CLIHelper.printHeader("System Reports");
        CLILogger.info("1. Library Statistics");
        CLILogger.info("2. User Statistics");
        CLILogger.info("3. Loan Statistics");
        CLILogger.info("0. Back");
        CLILogger.info(CHOOSE_OPTION);
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1": displayLibraryStatistics(); break;
            case "2": displayUserStatistics(); break;
            case "3": displayLoanStatistics(); break;
            case "0": return;
            default: CLIHelper.printError(INVALID_CHOICE); break;
        }
    }

    private void displayLibraryStatistics() {
        CLIHelper.printHeader("Library Statistics");
        
        List<Book> books = bookService.getAllBooks();
        List<CD> cds = cdService.getAllCDs();
        List<Journal> journals = journalService.getAllJournals();
        
        int totalItems = books.size() + cds.size() + journals.size();
        int totalCopies = books.stream().mapToInt(Book::getTotalCopies).sum() +
                         cds.stream().mapToInt(CD::getTotalCopies).sum() +
                         journals.stream().mapToInt(Journal::getTotalCopies).sum();
        int availableCopies = books.stream().mapToInt(Book::getAvailableCopies).sum() +
                             cds.stream().mapToInt(CD::getAvailableCopies).sum() +
                             journals.stream().mapToInt(Journal::getAvailableCopies).sum();
        
        CLILogger.info("Total Items: " + totalItems);
        CLILogger.info("Books: " + books.size());
        CLILogger.info("CDs: " + cds.size());
        CLILogger.info("Journals: " + journals.size());
        CLILogger.info("Total Copies: " + totalCopies);
        CLILogger.info("Available Copies: " + availableCopies);
        CLILogger.info("On Loan: " + (totalCopies - availableCopies));
    }

    private void displayUserStatistics() {
        CLIHelper.printHeader("User Statistics");
        
        List<UserDTO> users = userService.getAllUsers();
        
        long adminCount = users.stream().filter(u -> u.role() == Role.ADMIN).count();
        long librarianCount = users.stream().filter(u -> u.role() == Role.LIBRARIAN).count();
        long memberCount = users.stream().filter(u -> u.role() == Role.MEMBER).count();
        
        CLILogger.info("Total Users: " + users.size());
        CLILogger.info("Administrators: " + adminCount);
        CLILogger.info("Librarians: " + librarianCount);
        CLILogger.info("Members: " + memberCount);
    }

    private void displayLoanStatistics() {
        CLIHelper.printHeader("Loan Statistics");
        
        long totalLoans = loanStatsService.countTotalLoans();
        long activeLoans = loanStatsService.countActiveLoans();
        long returnedLoans = loanStatsService.countReturnedLoans();
        long overdueLoans = loanStatsService.countOverdueLoans();
        long dueSoon = loanStatsService.countLoansDueSoon(7);
        
        CLILogger.info("Total Loans: " + totalLoans);
        CLILogger.info("Active Loans: " + activeLoans);
        CLILogger.info("Returned Loans: " + returnedLoans);
        CLILogger.info("Overdue Loans: " + overdueLoans);
        CLILogger.info("Due Soon (7 days): " + dueSoon);
        CLILogger.info("");
        
        long bookLoans = loanStatsService.countLoansByItemType("book");
        long cdLoans = loanStatsService.countLoansByItemType("cd");
        long journalLoans = loanStatsService.countLoansByItemType("journal");
        
        CLILogger.info("Loans by Item Type:");
        CLILogger.info("  Books: " + bookLoans);
        CLILogger.info("  CDs: " + cdLoans);
        CLILogger.info("  Journals: " + journalLoans);
        CLILogger.info("");
        
        double avgDuration = loanStatsService.getAverageLoanDuration();
        CLILogger.info("Average Loan Duration: " + String.format("%.1f", avgDuration) + " days");
    }

    private void handleLoanManagement() {
        CLIHelper.printHeader("Loan Management");
        CLILogger.info("1. View All Active Loans");
        CLILogger.info("2. View Overdue Loans");
        CLILogger.info("3. View Returned Loans");
        CLILogger.info("4. Search Loans by User");
        CLILogger.info("5. Search Loans by Item Type");
        CLILogger.info("6. View Loans Due Soon");
        CLILogger.info("7. Create Loan for User (Admin)");
        CLILogger.info("8. Force Return Item (Admin)");
        CLILogger.info("0. Back");
        CLILogger.info(CHOOSE_OPTION);
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1": viewActiveLoans(); break;
            case "2": viewOverdueLoans(); break;
            case "3": viewReturnedLoans(); break;
            case "4": searchLoansByUser(); break;
            case "5": searchLoansByItemType(); break;
            case "6": viewLoansDueSoon(); break;
            case "7": createLoanForUser(); break;
            case "8": forceReturnItem(); break;
            case "0": return;
            default: CLIHelper.printError(INVALID_CHOICE); break;
        }
    }
    
    private void viewActiveLoans() {
        CLIHelper.printHeader("Active Loans");
        List<Loan> loans = loanQueryService.getActiveLoans();
        
        if (loans.isEmpty()) {
            CLILogger.info("No active loans found.");
            return;
        }
        
        CLILogger.info("Total Active Loans: " + loans.size());
        for (Loan loan : loans) {
            displayLoanInfo(loan);
        }
    }
    
    private void viewOverdueLoans() {
        CLIHelper.printHeader("Overdue Loans");
        List<Loan> loans = loanQueryService.getOverdueLoans();
        
        if (loans.isEmpty()) {
            CLILogger.info("No overdue loans found.");
            return;
        }
        
        CLILogger.info("Total Overdue Loans: " + loans.size());
        for (Loan loan : loans) {
            displayLoanInfo(loan);
            CLILogger.info("  Days Overdue: " + loan.getDaysOverdue());
            CLILogger.info("  Fine: $" + String.format("%.2f", loan.calculateFine()));
        }
    }
    
    private void viewReturnedLoans() {
        CLIHelper.printHeader("Returned Loans");
        List<Loan> loans = loanQueryService.getReturnedLoans();
        
        if (loans.isEmpty()) {
            CLILogger.info("No returned loans found.");
            return;
        }
        
        CLILogger.info("Total Returned Loans: " + loans.size());
        for (Loan loan : loans) {
            displayLoanInfo(loan);
            CLILogger.info("  Return Date: " + loan.getReturnDate());
        }
    }
    
    private void searchLoansByUser() {
        CLIHelper.printHeader("Search Loans by User");
        CLILogger.info("Enter User ID: ");
        String userIdStr = scanner.nextLine().trim();
        
        try {
            UUID userId = UUID.fromString(userIdStr);
            List<Loan> loans = loanQueryService.getLoansByUser(userId);
            
            if (loans.isEmpty()) {
                CLILogger.info("No loans found for this user.");
                return;
            }
            
            CLILogger.info("Total Loans for User: " + loans.size());
            for (Loan loan : loans) {
                displayLoanInfo(loan);
            }
        } catch (IllegalArgumentException e) {
            CLIHelper.printError("Invalid User ID format.");
        }
    }
    
    private void searchLoansByItemType() {
        CLIHelper.printHeader("Search Loans by Item Type");
        CLILogger.info("1. Books");
        CLILogger.info("2. CDs");
        CLILogger.info("3. Journals");
        CLILogger.info("Choose item type: ");
        
        String choice = scanner.nextLine().trim();
        String itemType = "";
        
        switch (choice) {
            case "1": itemType = "book"; break;
            case "2": itemType = "cd"; break;
            case "3": itemType = JOURNAL_TYPE; break;
            default: 
                CLIHelper.printError(INVALID_CHOICE);
                return;
        }
        
        List<Loan> loans = loanQueryService.getLoansByItemType(itemType);
        
        if (loans.isEmpty()) {
            CLILogger.info("No loans found for " + itemType + "s.");
            return;
        }
        
        CLILogger.info("Total Loans for " + itemType + "s: " + loans.size());
        for (Loan loan : loans) {
            displayLoanInfo(loan);
        }
    }
    
    private void viewLoansDueSoon() {
        CLIHelper.printHeader("Loans Due Soon (Next 7 Days)");
        List<Loan> loans = loanQueryService.getLoansDueSoon(7);
        
        if (loans.isEmpty()) {
            CLILogger.info("No loans due soon.");
            return;
        }
        
        CLILogger.info("Total Loans Due Soon: " + loans.size());
        for (Loan loan : loans) {
            displayLoanInfo(loan);
        }
    }
    
    private void displayLoanInfo(Loan loan) {
        CLILogger.info("---");
        CLILogger.info("Loan ID: " + loan.getId());
        CLILogger.info("User ID: " + loan.getUserId());
        CLILogger.info("Item ID: " + loan.getItemId());
        CLILogger.info("Item Type: " + loan.getItemType());
        CLILogger.info("Borrow Date: " + loan.getBorrowDate());
        CLILogger.info("Due Date: " + loan.getDueDate());
        CLILogger.info("Status: " + (loan.isReturned() ? "Returned" : (loan.isOverdue() ? "Overdue" : "Active")));
    }
    
    private void createLoanForUser() {
        try {
            CLIHelper.printHeader("Create Loan for User (Admin)");
            
            CLILogger.info("Enter User ID: ");
            String userIdStr = scanner.nextLine().trim();
            UUID userId = UUID.fromString(userIdStr);
            

            UserDTO user = userService.getUserByUsername(
                userService.getAllUsers().stream()
                    .filter(u -> u.userID().equals(userId))
                    .findFirst()
                    .orElseThrow(() -> new UserNotFoundException("User not found"))
                    .username()
            );
            
            CLILogger.info("Creating loan for: " + user.firstName() + " " + user.lastName());
            
            CLILogger.info("Choose item type:");
            CLILogger.info("1. Book");
            CLILogger.info("2. CD");
            CLILogger.info("3. Journal");
            String itemTypeChoice = scanner.nextLine().trim();
            
            String itemType = switch (itemTypeChoice) {
                case "1" -> "book";
                case "2" -> "cd";
                case "3" -> "journal";
                default -> throw new IllegalArgumentException("Invalid item type");
            };
            
            CLILogger.info("Enter Item ID (or partial ID): ");
            String itemIdStr = scanner.nextLine().trim();
            UUID itemId;
            

            if (itemType.equals("book")) {
                Book book = findBookByIdOrSubId(itemIdStr);
                if (book == null) throw new Exception("Book not found");
                itemId = book.getId();
            } else if (itemType.equals("cd")) {
                CD cd = findCDByIdOrSubId(itemIdStr);
                if (cd == null) throw new Exception("CD not found");
                itemId = cd.getId();
            } else {
                Journal journal = findJournalByIdOrSubId(itemIdStr);
                if (journal == null) throw new Exception("Journal not found");
                itemId = journal.getId();
            }
            

            Loan loan = loanService.loanItem(user, itemId, itemType);
            
            CLIHelper.printSuccess("Loan created successfully!");
            CLILogger.info("Loan ID: " + loan.getId());
            CLILogger.info("Due Date: " + loan.getDueDate());
            
        } catch (UserNotFoundException e) {
            CLIHelper.printError("User not found: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            CLIHelper.printError("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            CLIHelper.printError("Error creating loan: " + e.getMessage());
            CLILogger.error("Error creating loan", e);
        }
    }
    
    private void forceReturnItem() {
        try {
            CLIHelper.printHeader("Force Return Item (Admin)");
            
            CLILogger.info("Enter Loan ID: ");
            String loanIdStr = scanner.nextLine().trim();
            UUID loanId = UUID.fromString(loanIdStr);
            
            Loan loan = loanQueryService.getActiveLoans().stream()
                .filter(l -> l.getId().equals(loanId))
                .findFirst()
                .orElseThrow(() -> new Exception("Loan not found or already returned"));
            
            CLILogger.info("Loan Details:");
            displayLoanInfo(loan);
            
            CLILogger.info("\nAre you sure you want to force return this item? (yes/no): ");
            String confirmation = scanner.nextLine().trim();
            
            if (!confirmation.equalsIgnoreCase("yes")) {
                CLILogger.info("Operation cancelled.");
                return;
            }
            
            boolean success = loanService.returnItem(loan.getUserId(), loanId);
            
            if (success) {
                CLIHelper.printSuccess("Item returned successfully!");
                if (loan.isOverdue()) {
                    CLILogger.info("Late fine was applied to user's account.");
                }
            } else {
                CLIHelper.printError("Failed to return item.");
            }
            
        } catch (IllegalArgumentException e) {
            CLIHelper.printError("Invalid Loan ID format.");
        } catch (Exception e) {
            CLIHelper.printError("Error returning item: " + e.getMessage());
            CLILogger.error("Error returning item", e);
        }
    }
}
