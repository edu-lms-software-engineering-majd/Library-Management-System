package lms.presentation;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;

import lms.application.AccountService;
import lms.application.AuthService;
import lms.application.BookService;
import lms.application.CDService;
import lms.application.JournalService;
import lms.application.LoanService;
import lms.application.NotificationService;
import lms.application.UserDTO;
import lms.application.UserService;
import lms.application.search.SearchByAuthorStrategy;
import lms.application.search.SearchByTitleStrategy;
import lms.application.search.SearchCDByArtistStrategy;
import lms.application.search.SearchCDByTitleStrategy;
import lms.application.search.SearchJournalByAuthorStrategy;
import lms.application.search.SearchJournalByTitleStrategy;
import lms.application.search.SearchStrategy;
import lms.domain.Book;
import lms.domain.CD;
import lms.domain.Journal;
import lms.domain.Loan;
import lms.domain.Notification;
import lms.domain.User;
import lms.domain.exception.UserNotFoundException;

/**
 * User CLI for Library Management System.
 * Provides clean interface for library users to browse, borrow, and manage their account.
 * 
 * @author Majd Awwad
 * @version 3.0
 * @since December 2025
 */
public class UserCLI implements CLI {

    private static final String BOOK_TYPE = "book";
    private static final String CD_TYPE = "cd";
    private static final String JOURNAL_TYPE = "journal";
    private static final int BOOK_DUE_DAYS = 28;
    private static final int CD_DUE_DAYS = 21;
    private static final int JOURNAL_DUE_DAYS = 7;
    
    private final Scanner scanner = new Scanner(System.in);
    private final UserService userService;
    private final BookService bookService;
    private final CDService cdService;
    private final JournalService journalService;
    private final LoanService loanService;
    private final AuthService authService;
    private final AccountService accountService;
    
    private UserDTO currentUser;

    public UserCLI(UserService userService, BookService bookService, CDService cdService,
                   JournalService journalService, LoanService loanService,
                   NotificationService notificationService, AuthService authService, AccountService accountService) {
        this.userService = userService;
        this.bookService = bookService;
        this.cdService = cdService;
        this.journalService = journalService;
        this.loanService = loanService;
        this.authService = authService;
        this.accountService = accountService;
    }
    
    private UserDTO getCurrentUser() {
        if (currentUser == null) {
            currentUser = authService.getCurrentUser();
        }
        return currentUser;
    }
    
    private boolean isLibrarian() {
        return getCurrentUser().role() == lms.domain.Role.LIBRARIAN;
    }

    @Override
    public void start() {
        UserDTO user = getCurrentUser();
        CLIHelper.printHeader("Library Management System - User");
        CLILogger.info("Welcome, " + user.firstName() + " " + user.lastName());
        
        displayUnreadNotificationCount();

        boolean running = true;
        while (running) {
            showUserMenu();
            String choice = scanner.nextLine().trim();
            running = handleMenuChoice(choice);
        }
    }
    
    private void displayUnreadNotificationCount() {
        try {
            User user = userService.getDomainUserByUsername(getCurrentUser().username());
            int unreadCount = user.getUnreadNotificationCount();
            if (unreadCount > 0) {
                CLILogger.info("You have " + unreadCount + " unread notification(s)");
            }
        } catch (Exception e) {
            CLILogger.error("Error fetching notification count", e);
        }
    }
    
    private boolean handleMenuChoice(String choice) {
        if (isLibrarian()) {
            return handleLibrarianMenuChoice(choice);
        }
        return handleMemberMenuChoice(choice);
    }
    
    private boolean handleLibrarianMenuChoice(String choice) {
        switch (choice) {
            case "1":
                handleBrowseAndSearchItems();
                break;
            case "2":
                handleBorrowItem();
                break;
            case "3":
                handleReturnItem();
                break;
            case "4":
                handleViewMyLoans();
                break;
            case "5":
                handleAccountManagement();
                break;
            case "6":
                handleViewMyProfile();
                break;
            case "7":
                handleUpdateMyProfile();
                break;
            case "8":
                handleViewMyNotifications();
                break;
            case "9":
                performLogout();
                return false;
            default:
                CLIHelper.printError("Invalid choice");
                break;
        }
        return true;
    }
    
    private boolean handleMemberMenuChoice(String choice) {
        switch (choice) {
            case "1":
                handleBrowseAndSearchItems();
                break;
            case "2":
                handleBorrowItem();
                break;
            case "3":
                handleViewMyLoans();
                break;
            case "4":
                handleAccountManagement();
                break;
            case "5":
                handleViewMyProfile();
                break;
            case "6":
                handleUpdateMyProfile();
                break;
            case "7":
                handleViewMyNotifications();
                break;
            case "8":
                performLogout();
                return false;
            default:
                CLIHelper.printError("Invalid choice");
                break;
        }
        return true;
    }
    
    private void performLogout() {
        CLILogger.info("Logging out... Goodbye!");
        authService.logout();
        currentUser = null;
    }

    private void showUserMenu() {
        CLIHelper.printHeader("USER MENU");
        CLILogger.info("1. Browse & Search Items");
        CLILogger.info("2. Borrow an Item");
        
        if (isLibrarian()) {
            CLILogger.info("3. Process Returns (Librarian Only)");
            CLILogger.info("4. View My Loans");
            CLILogger.info("5. My Account & Finances");
            CLILogger.info("6. View My Profile");
            CLILogger.info("7. Update My Profile");
            CLILogger.info("8. View My Notifications");
            CLILogger.info("9. Logout");
        } else {
            CLILogger.info("3. View My Loans");
            CLILogger.info("4. My Account & Finances");
            CLILogger.info("5. View My Profile");
            CLILogger.info("6. Update My Profile");
            CLILogger.info("7. View My Notifications");
            CLILogger.info("8. Logout");
            CLILogger.info("Note: To return items, please visit the librarian desk.");
        }
        
        CLILogger.info("Enter your choice: ");
    }

    private void handleBrowseAndSearchItems() {
        CLIHelper.printHeader("Browse & Search Library Items");
        CLILogger.info("1. Browse All Books");
        CLILogger.info("2. Browse All CDs");
        CLILogger.info("3. Browse All Journals");
        CLILogger.info("4. Search Items");
        CLILogger.info("0. Back to Main Menu");
        CLILogger.info("Choose an option: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                browseBooks();
                break;
            case "2":
                browseCDs();
                break;
            case "3":
                browseJournals();
                break;
            case "4":
                handleSearchItems();
                break;
            case "0":
                return;
            default:
                CLIHelper.printError("Invalid choice");
                break;
        }
    }

    private void browseBooks() {
        CLIHelper.printHeader("All Books");
        List<Book> books = bookService.getAllBooks();
        if (books.isEmpty()) {
            CLILogger.info("No books available in the library.");
            return;
        }
        displayBookResultsWithNumbers(books);
    }

    private void browseCDs() {
        CLIHelper.printHeader("All CDs");
        List<CD> cds = cdService.getAllCDs();
        if (cds.isEmpty()) {
            CLILogger.info("No CDs available in the library.");
            return;
        }
        displayCDResultsWithNumbers(cds);
    }

    private void browseJournals() {
        CLIHelper.printHeader("All Journals");
        List<Journal> journals = journalService.getAllJournals();
        if (journals.isEmpty()) {
            CLILogger.info("No journals available in the library.");
            return;
        }
        displayJournalResultsWithNumbers(journals);
    }

    private void handleSearchItems() {
        CLIHelper.printHeader("Search Items");
        CLILogger.info("Choose item type to search:");
        CLILogger.info("1. Books");
        CLILogger.info("2. CDs");
        CLILogger.info("3. Journals");
        CLILogger.info("Choose an option: ");
        String itemChoice = scanner.nextLine().trim();

        CLILogger.info("Search by:");
        CLILogger.info("1. Title");
        CLILogger.info("2. Author/Artist");
        CLILogger.info("Choose criteria: ");
        String criteriaChoice = scanner.nextLine().trim();

        CLILogger.info("Enter search term (partial match works): ");
        String searchTerm = scanner.nextLine().trim();

        if (searchTerm.isEmpty()) {
            CLIHelper.printError("Search term cannot be empty");
            return;
        }

        switch (itemChoice) {
            case "1":
                searchBooks(criteriaChoice, searchTerm);
                break;
            case "2":
                searchCDs(criteriaChoice, searchTerm);
                break;
            case "3":
                searchJournals(criteriaChoice, searchTerm);
                break;
            default:
                CLIHelper.printError("Invalid item type");
                break;
        }
    }

    private void searchBooks(String criteriaChoice, String searchTerm) {
        SearchStrategy<Book> strategy = "1".equals(criteriaChoice) 
            ? new SearchByTitleStrategy() 
            : "2".equals(criteriaChoice) ? new SearchByAuthorStrategy() : null;
        
        if (strategy == null) {
            CLIHelper.printError("Invalid criteria");
            return;
        }
        
        List<Book> results = bookService.searchBooks(strategy, searchTerm);
        CLIHelper.displaySearchResults(results, searchTerm, BOOK_TYPE, this::displayBookResultsWithNumbers);
    }

    private void searchCDs(String criteriaChoice, String searchTerm) {
        SearchStrategy<CD> strategy = "1".equals(criteriaChoice) 
            ? new SearchCDByTitleStrategy() 
            : "2".equals(criteriaChoice) ? new SearchCDByArtistStrategy() : null;
        
        if (strategy == null) {
            CLIHelper.printError("Invalid criteria");
            return;
        }
        
        List<CD> results = cdService.searchCDs(strategy, searchTerm);
        CLIHelper.displaySearchResults(results, searchTerm, CD_TYPE, this::displayCDResultsWithNumbers);
    }

    private void searchJournals(String criteriaChoice, String searchTerm) {
        SearchStrategy<Journal> strategy = "1".equals(criteriaChoice) 
            ? new SearchJournalByTitleStrategy() 
            : "2".equals(criteriaChoice) ? new SearchJournalByAuthorStrategy() : null;
        
        if (strategy == null) {
            CLIHelper.printError("Invalid criteria");
            return;
        }
        
        List<Journal> results = journalService.searchJournals(strategy, searchTerm);
        CLIHelper.displaySearchResults(results, searchTerm, JOURNAL_TYPE, this::displayJournalResultsWithNumbers);
    }

    private void handleBorrowItem() {
        CLIHelper.printHeader("Borrow an Item");
        CLILogger.info("Choose item type to borrow:");
        CLILogger.info("1. Book");
        CLILogger.info("2. CD");
        CLILogger.info("3. Journal");
        CLILogger.info("Choose an option: ");
        
        String itemChoice = scanner.nextLine().trim();
        
        switch (itemChoice) {
            case "1":
                borrowBook();
                break;
            case "2":
                borrowCD();
                break;
            case "3":
                borrowJournal();
                break;
            default:
                CLIHelper.printError("Invalid item type");
                break;
        }
    }

    private void borrowBook() {
        List<Book> availableBooks = bookService.getAllBooks().stream()
                .filter(Book::isAvailable)
                .collect(Collectors.toList());
        
        CLIHelper.borrowItemGeneric(
            availableBooks,
            BOOK_TYPE,
            BOOK_DUE_DAYS + " days",
            this::displayBookResultsWithNumbers,
            this::findBookBySearchTerm,
            b -> b.getTitle() + " by " + b.getAuthor(),
            Book::getId,
            scanner,
            id -> {
                try {
                    loanService.loanItem(getCurrentUser(), id, BOOK_TYPE);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        );
    }

    private void borrowCD() {
        List<CD> availableCDs = cdService.getAllCDs().stream()
                .filter(CD::isAvailable)
                .collect(Collectors.toList());
        
        CLIHelper.borrowItemGeneric(
            availableCDs,
            CD_TYPE,
            CD_DUE_DAYS + " days",
            this::displayCDResultsWithNumbers,
            this::findCDBySearchTerm,
            c -> c.getTitle() + " by " + c.getArtist(),
            CD::getId,
            scanner,
            id -> {
                try {
                    loanService.loanItem(getCurrentUser(), id, CD_TYPE);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        );
    }

    private void borrowJournal() {
        List<Journal> availableJournals = journalService.getAllJournals().stream()
                .filter(Journal::isAvailable)
                .collect(Collectors.toList());
        
        CLIHelper.borrowItemGeneric(
            availableJournals,
            JOURNAL_TYPE,
            JOURNAL_DUE_DAYS + " days",
            this::displayJournalResultsWithNumbers,
            this::findJournalBySearchTerm,
            j -> j.getTitle() + " by " + j.getAuthor(),
            Journal::getId,
            scanner,
            id -> {
                try {
                    loanService.loanItem(getCurrentUser(), id, JOURNAL_TYPE);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        );
    }

    private Book findBookBySearchTerm(List<Book> books, String searchTerm) {
        return CLIHelper.findItemBySearchTerm(books, searchTerm, BOOK_TYPE,
            Book::getTitle, Book::getAuthor, Book::getId,
            this::displayBookResultsWithNumbers, scanner);
    }

    private CD findCDBySearchTerm(List<CD> cds, String searchTerm) {
        return CLIHelper.findItemBySearchTerm(cds, searchTerm, CD_TYPE,
            CD::getTitle, CD::getArtist, CD::getId,
            this::displayCDResultsWithNumbers, scanner);
    }

    private Journal findJournalBySearchTerm(List<Journal> journals, String searchTerm) {
        return CLIHelper.findItemBySearchTerm(journals, searchTerm, JOURNAL_TYPE,
            Journal::getTitle, Journal::getAuthor, Journal::getId,
            this::displayJournalResultsWithNumbers, scanner);
    }

    private void handleReturnItem() {
        CLIHelper.printHeader("Process Returns (Librarian Desk)");
        
        CLILogger.info("Search for user by:");
        CLILogger.info("1. Username");
        CLILogger.info("2. View All Users with Active Loans");
        CLILogger.info("0. Cancel");
        CLILogger.info("Choose an option: ");
        
        String searchChoice = scanner.nextLine().trim();
        
        switch (searchChoice) {
            case "1":
                processReturnByUsername();
                break;
            case "2":
                processReturnViewAllUsers();
                break;
            case "0":
                CLILogger.info("Return processing cancelled.");
                break;
            default:
                CLIHelper.printError("Invalid choice");
                break;
        }
    }

    private void processReturnByUsername() {
        CLILogger.info("Enter username (partial match works): ");
        String searchTerm = scanner.nextLine().trim();
        
        if (searchTerm.isEmpty()) {
            CLIHelper.printError("Username cannot be empty");
            return;
        }
        
        try {
            List<UserDTO> allUsers = userService.getAllUsers();
            List<UserDTO> matchingUsers = allUsers.stream()
                    .filter(u -> u.username().toLowerCase().contains(searchTerm.toLowerCase()) ||
                               u.firstName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                               u.lastName().toLowerCase().contains(searchTerm.toLowerCase()))
                    .collect(Collectors.toList());
            
            if (matchingUsers.isEmpty()) {
                CLILogger.info("No users found matching '" + searchTerm + "'");
                return;
            }
            
            UserDTO selectedUser = selectUserFromList(matchingUsers);
            if (selectedUser != null) {
                processReturnForUser(selectedUser);
            }
            
        } catch (Exception e) {
            CLILogger.error("Error processing return", e);
        }
    }

    private UserDTO selectUserFromList(List<UserDTO> users) {
        if (users.size() == 1) {
            return users.get(0);
        }
        
        CLILogger.info("Multiple users found:");
        CLILogger.info(String.format("%-4s %-20s %-25s %-20s", "#", "Username", "Name", "Role"));
        
        for (int i = 0; i < users.size(); i++) {
            UserDTO user = users.get(i);
            CLILogger.info(String.format("%-4d %-20s %-25s %-20s", 
                (i + 1), 
                user.username(),
                user.firstName() + " " + user.lastName(),
                user.role()));
        }
        
        int choice = CLIHelper.getIntInput(scanner, "Select user number (1-" + users.size() + "): ", 1, users.size());
        
        if (choice >= 1 && choice <= users.size()) {
            return users.get(choice - 1);
        }
        
        return null;
    }

    private void processReturnViewAllUsers() {
        try {
            List<UserDTO> allUsers = userService.getAllUsers();
            List<UserDTO> usersWithLoans = allUsers.stream()
                .filter(user -> {
                    try {
                        return !loanService.getUserActiveLoans(user.userID()).isEmpty();
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
            
            if (usersWithLoans.isEmpty()) {
                CLILogger.info("No users currently have active loans.");
                return;
            }
            
            CLILogger.info("Users with Active Loans:");
            CLILogger.info(String.format("%-4s %-20s %-25s %-15s %-15s", "#", "Username", "Name", "Active Loans", "Has Overdue"));
            
            for (int i = 0; i < usersWithLoans.size(); i++) {
                UserDTO user = usersWithLoans.get(i);
                List<Loan> activeLoans = loanService.getUserActiveLoans(user.userID());
                long overdueCount = activeLoans.stream().filter(Loan::isOverdue).count();
                String overdueStatus = overdueCount > 0 ? "Yes (" + overdueCount + ")" : "No";
                
                CLILogger.info(String.format("%-4d %-20s %-25s %-15d %-15s", 
                    (i + 1), 
                    user.username(),
                    user.firstName() + " " + user.lastName(),
                    activeLoans.size(),
                    overdueStatus));
            }
            
            int choice = CLIHelper.getIntInput(scanner, "Select user number (1-" + usersWithLoans.size() + ") or 0 to cancel: ", 0, usersWithLoans.size());
            
            if (choice == 0) {
                CLILogger.info("Cancelled.");
                return;
            }
            
            if (choice >= 1 && choice <= usersWithLoans.size()) {
                processReturnForUser(usersWithLoans.get(choice - 1));
            }
            
        } catch (Exception e) {
            CLILogger.error("Error processing return", e);
        }
    }

    private void processReturnForUser(UserDTO user) {
        try {
            CLIHelper.printHeader("Processing Return for: " + user.username());
            
            List<Loan> activeLoans = loanService.getUserActiveLoans(user.userID());
            
            if (activeLoans.isEmpty()) {
                CLILogger.info("This user has no active loans.");
                return;
            }
            
            CLILogger.info("User: " + user.firstName() + " " + user.lastName() + " (" + user.username() + ")");
            CLILogger.info("Active Loans: " + activeLoans.size());
            
            displayLoansWithItemDetails(activeLoans);
            
            int choice = CLIHelper.getIntInput(scanner, "Enter the number of the item to return (1-" + activeLoans.size() + "), or 0 to cancel: ", 0, activeLoans.size());
            
            if (choice == 0) {
                CLILogger.info("Return cancelled.");
                return;
            }
            
            if (choice >= 1 && choice <= activeLoans.size()) {
                Loan selectedLoan = activeLoans.get(choice - 1);
                processLoanReturn(user, selectedLoan);
            }
            
        } catch (Exception e) {
            CLILogger.error("Error processing return", e);
        }
    }

    private void processLoanReturn(UserDTO user, Loan loan) {
        try {
            String itemDetails = getItemDetails(loan.getItemId(), loan.getItemType());
            CLILogger.info("Processing return of: " + itemDetails);
            CLILogger.info("For user: " + user.firstName() + " " + user.lastName());
            
            if (loan.isOverdue()) {
                long daysOverdue = loan.getDaysOverdue();
                double fine = loan.calculateFine();
                CLIHelper.printWarning("This item is " + daysOverdue + " day(s) overdue");
                CLILogger.info("A fine of " + String.format("%.2f", fine) + " NIS will be applied to the user's account.");
            }
            
            if (!CLIHelper.confirmAction(scanner, "Confirm return?")) {
                CLILogger.info("Return cancelled.");
                return;
            }
            
            loanService.returnItem(user.userID(), loan.getId());
            CLIHelper.printSuccess("Item returned successfully");
            
            if (loan.isOverdue()) {
                double currentBalance = accountService.getUserBalance(user.userID());
                CLILogger.info("User's current balance: " + String.format("%.2f", currentBalance) + " NIS");
                
                if (currentBalance > 0) {
                    CLILogger.info("User should pay fines before borrowing more items.");
                }
            }
        } catch (Exception e) {
            CLILogger.error("Error processing return", e);
        }
    }

    private String getItemDetails(UUID itemId, String itemType) {
        try {
            switch (itemType.toLowerCase()) {
                case BOOK_TYPE:
                    Book book = bookService.getBookById(itemId);
                    return book != null ? book.getTitle() + " by " + book.getAuthor() : "Unknown Book";
                case CD_TYPE:
                    CD cd = cdService.getCDById(itemId);
                    return cd != null ? cd.getTitle() + " by " + cd.getArtist() : "Unknown CD";
                case JOURNAL_TYPE:
                    Journal journal = journalService.getJournalById(itemId);
                    return journal != null ? journal.getTitle() + " by " + journal.getAuthor() : "Unknown Journal";
                default:
                    return "Unknown Item";
            }
        } catch (Exception e) {
            CLILogger.error("Error getting item details", e);
            return "Unknown Item";
        }
    }

    private void handleViewMyLoans() {
        CLIHelper.printHeader("My Active Loans");
        
        try {
            List<Loan> activeLoans = loanService.getUserActiveLoans(getCurrentUser().userID());
            
            if (activeLoans.isEmpty()) {
                CLILogger.info("You have no active loans.");
                return;
            }
            
            CLILogger.info("Total active loans: " + activeLoans.size());
        
            long overdueCount = activeLoans.stream().filter(Loan::isOverdue).count();
            if (overdueCount > 0) {
                CLIHelper.printWarning("You have " + overdueCount + " overdue item(s)");
            }
            
            displayLoansWithItemDetails(activeLoans);
            
        } catch (UserNotFoundException e) {
            CLILogger.error("Could not retrieve your loans", e);
        }
    }

    private void handleAccountManagement() {
        CLIHelper.printHeader("My Account & Finances");
        CLILogger.info("1. View Account Summary");
        CLILogger.info("2. View Transaction History");
        CLILogger.info("3. Pay Fines");
        CLILogger.info("4. View Account Statistics");
        CLILogger.info("0. Back to Main Menu");
        CLILogger.info("Choose an option: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                handleViewAccountSummary();
                break;
            case "2":
                handleViewTransactionHistory();
                break;
            case "3":
                handlePayFines();
                break;
            case "4":
                handleViewAccountStatistics();
                break;
            case "0":
                return;
            default:
                CLIHelper.printError("Invalid choice");
                break;
        }
    }

    private void handleViewAccountSummary() {
        CLIHelper.printHeader("Account Summary");
        
        try {
            UUID userId = getCurrentUser().userID();
            User user = userService.getDomainUserByUsername(getCurrentUser().username());
            lms.domain.Account account = user.getAccount();
            
            double totalFines = accountService.getUserBalance(userId);
            lms.domain.AccountStatus accountStatus = accountService.getUserAccountStatus(userId);
            boolean canBorrow = accountService.canUserBorrow(userId);
            
            CLILogger.info("Account ID: " + account.getAccountId().toString().substring(0, 8) + "...");
            CLILogger.info("Created: " + account.getCreatedAt());
            CLILogger.info("Last Updated: " + account.getUpdatedAt());
            CLILogger.info("Status: " + accountStatus);
            
            CLILogger.info("Financial Summary:");
            
            if (totalFines > 0) {
                CLIHelper.printWarning("Outstanding Balance: " + String.format("%.2f NIS", totalFines));
                
                if (totalFines >= 100.0) {
                    CLIHelper.printWarning("Your account may be suspended due to high fines");
                    CLILogger.info("Please pay your fines to continue borrowing.");
                }
            } else {
                CLILogger.info("No outstanding balance - Your account is clear");
            }
            
            CLILogger.info("Borrowing Status:");
            if (canBorrow) {
                CLILogger.info("You can borrow items");
                CLILogger.info("Active Loans: " + user.getActiveLoanCount() + "/10");
            } else {
                if (user.hasFine()) {
                    CLILogger.info("Borrowing suspended due to outstanding fines");
                } else if (user.isAtBorrowLimit()) {
                    CLILogger.info("You have reached the maximum borrow limit (10 items)");
                } else {
                    CLILogger.info("Borrowing is currently restricted");
                }
            }
            
        } catch (Exception e) {
            CLILogger.error("Could not retrieve account information", e);
        }
    }

    private void handleViewTransactionHistory() {
        CLIHelper.printHeader("Transaction History");
        
        try {
            User user = userService.getDomainUserByUsername(getCurrentUser().username());
            lms.domain.Account account = user.getAccount();
            List<lms.domain.FineTransaction> transactions = account.getFineTransactions();
            
            if (transactions.isEmpty()) {
                CLILogger.info("No transactions found. You have a clean record");
                return;
            }
            
            CLILogger.info("Total Transactions: " + transactions.size());
            CLILogger.info(String.format("%-4s %-12s %-15s %-15s %-50s", 
                "#", "Date", "Type", "Amount", "Description"));
            
            for (int i = 0; i < transactions.size(); i++) {
                lms.domain.FineTransaction transaction = transactions.get(i);
                
                String typeStr = transaction.getType() == lms.domain.TransactionType.FINE ? "FINE" : "PAYMENT";
                
                double amount = transaction.getAmount();
                String amountStr = transaction.getType() == lms.domain.TransactionType.FINE 
                    ? String.format("+%.2f NIS", amount)
                    : String.format("-%.2f NIS", amount);
                
                String description = CLIHelper.truncate(transaction.getDescription(), 48);
                
                CLILogger.info(String.format("%-4d %-12s %-15s %-15s %-50s",
                    (i + 1),
                    transaction.getTransactionDate(),
                    typeStr,
                    amountStr,
                    description));
            }
            CLILogger.info("Current Balance: " + String.format("%.2f NIS", account.getTotalFines()));
            
        } catch (UserNotFoundException e) {
            CLILogger.error("Could not retrieve transaction history", e);
        }
    }

    private void handlePayFines() {
        CLIHelper.printHeader("Pay Fines");
        
        try {
            UUID userId = getCurrentUser().userID();
            double totalFines = accountService.getUserBalance(userId);
            
            if (totalFines == 0) {
                CLILogger.info("You have no outstanding fines to pay");
                return;
            }
            
            CLILogger.info("Current Outstanding Balance: " + String.format("%.2f NIS", totalFines));
            CLILogger.info("How much would you like to pay?");
            CLILogger.info("1. Pay Full Amount (" + String.format("%.2f NIS", totalFines) + ")");
            CLILogger.info("2. Pay Partial Amount");
            CLILogger.info("0. Cancel");
            CLILogger.info("Choose an option: ");
            
            String choice = scanner.nextLine().trim();
            double paymentAmount = 0.0;
            
            if ("1".equals(choice)) {
                paymentAmount = totalFines;
            } else if ("2".equals(choice)) {
                paymentAmount = CLIHelper.getDoubleInput(scanner, "Enter amount to pay (max " + String.format("%.2f", totalFines) + " NIS): ");
                if (paymentAmount <= 0) {
                    CLIHelper.printError("Payment amount must be positive");
                    return;
                }
                if (paymentAmount > totalFines) {
                    CLIHelper.printError("Payment amount cannot exceed outstanding balance");
                    return;
                }
            } else if ("0".equals(choice)) {
                CLILogger.info("Payment cancelled.");
                return;
            } else {
                CLIHelper.printError("Invalid choice");
                return;
            }
            
            CLILogger.info("Payment Summary:");
            CLILogger.info("Amount to Pay: " + String.format("%.2f NIS", paymentAmount));
            CLILogger.info("Remaining Balance: " + String.format("%.2f NIS", totalFines - paymentAmount));
            
            if (!CLIHelper.confirmAction(scanner, "Confirm payment?")) {
                CLILogger.info("Payment cancelled.");
                return;
            }
            
            accountService.payUserFine(userId, paymentAmount);
            double newBalance = accountService.getUserBalance(userId);
            
            CLIHelper.printSuccess("Payment successful");
            CLILogger.info("Amount Paid: " + String.format("%.2f NIS", paymentAmount));
            CLILogger.info("New Balance: " + String.format("%.2f NIS", newBalance));
            
            if (newBalance == 0) {
                CLILogger.info("Congratulations! Your account is now clear");
                if (accountService.getUserAccountStatus(userId) == lms.domain.AccountStatus.ACTIVE) {
                    CLILogger.info("You can now borrow items from the library.");
                }
            }
            
        } catch (Exception e) {
            CLILogger.error("Error processing payment", e);
        }
    }

    private void handleViewAccountStatistics() {
        CLIHelper.printHeader("Account Statistics");
        
        try {
            User user = userService.getDomainUserByUsername(getCurrentUser().username());
            lms.domain.Account account = user.getAccount();
            List<lms.domain.FineTransaction> transactions = account.getFineTransactions();
             
            double totalFinesPaid = 0.0;
            double totalFinesIncurred = 0.0;
            int fineCount = 0;
            int paymentCount = 0;
            
            for (lms.domain.FineTransaction transaction : transactions) {
                if (transaction.getType() == lms.domain.TransactionType.FINE) {
                    totalFinesIncurred += transaction.getAmount();
                    fineCount++;
                } else if (transaction.getType() == lms.domain.TransactionType.PAYMENT) {
                    totalFinesPaid += transaction.getAmount();
                    paymentCount++;
                }
            }
            
            List<Loan> allLoans = loanService.getUserAllLoans(currentUser.userID());
            List<Loan> activeLoans = loanService.getUserActiveLoans(currentUser.userID());
            long overdueLoans = activeLoans.stream().filter(Loan::isOverdue).count();
            int completedLoans = allLoans.size() - activeLoans.size();
            
            CLILogger.info("Financial Statistics:");
            CLILogger.info("Total Fines Incurred: " + String.format("%.2f NIS", totalFinesIncurred));
            CLILogger.info("Total Payments Made: " + String.format("%.2f NIS", totalFinesPaid));
            CLILogger.info("Current Balance: " + String.format("%.2f NIS", account.getTotalFines()));
            CLILogger.info("Number of Fines: " + fineCount);
            CLILogger.info("Number of Payments: " + paymentCount);
            
            CLILogger.info("Borrowing Statistics:");
            CLILogger.info("Total Loans (All Time): " + allLoans.size());
            CLILogger.info("Active Loans: " + activeLoans.size());
            CLILogger.info("Completed Loans: " + completedLoans);
            CLILogger.info("Currently Overdue: " + overdueLoans);
            
            if (!activeLoans.isEmpty()) {
                double overdueRate = ((double) overdueLoans / activeLoans.size()) * 100;
                CLILogger.info("Current Overdue Rate: " + String.format("%.1f%%", overdueRate));
            }
            
            CLILogger.info("Account Age:");
            long daysSinceCreation = java.time.temporal.ChronoUnit.DAYS.between(
                account.getCreatedAt(), java.time.LocalDate.now());
            CLILogger.info("Member Since: " + account.getCreatedAt() + " (" + daysSinceCreation + " days)");
            
            CLILogger.info("Account Performance:");
            if (account.getTotalFines() == 0 && overdueLoans == 0) {
                CLILogger.info("EXCELLENT - No outstanding fines or overdue items");
            } else if (account.getTotalFines() < 50 && overdueLoans <= 1) {
                CLILogger.info("GOOD - Keep up the good work");
            } else if (account.getTotalFines() < 100) {
                CLILogger.info("FAIR - Please return items on time and pay fines");
            } else {
                CLILogger.info("NEEDS ATTENTION - High fines detected. Please settle your account");
            }
            
        } catch (Exception e) {
            CLILogger.error("Could not retrieve account statistics", e);
        }
    }

    private void handleViewMyProfile() {
        CLIHelper.printHeader("My Profile");
        
        try {
            UserDTO user = getCurrentUser();
            User domainUser = userService.getDomainUserByUsername(user.username());
            
            CLILogger.info("Username: " + user.username());
            CLILogger.info("Name: " + user.firstName() + " " + user.lastName());
            CLILogger.info("Email: " + user.email());
            CLILogger.info("Role: " + user.role());
            CLILogger.info("Registration Date: " + domainUser.getRegistrationDate());
            CLILogger.info("Active Loans: " + domainUser.getActiveLoanCount());
            CLILogger.info("Total Fines: " + domainUser.getAccount().getTotalFines() + " NIS");
            
            if (domainUser.hasFine()) {
                CLIHelper.printWarning("You have outstanding fines that must be paid before borrowing more items");
            }
            
        } catch (UserNotFoundException e) {
            CLILogger.error("Could not retrieve profile information", e);
        }
    }

    private void handleUpdateMyProfile() {
        CLIHelper.printHeader("Update My Profile");
        CLILogger.info("Leave fields blank to keep current values");

        CLILogger.info("Enter new email (current: '" + getCurrentUser().email() + "'): ");
        String email = scanner.nextLine().trim();
        if (email.isEmpty()) {
            email = null;
        }

        CLILogger.info("Enter new password (min 8 characters): ");
        String password = scanner.nextLine().trim();
        if (password.isEmpty()) {
            password = null;
        } else if (password.length() < 8) {
            CLIHelper.printError("Password must be at least 8 characters long");
            return;
        }

        if (email == null && password == null) {
            CLILogger.info("No changes made.");
            return;
        }

        if (!CLIHelper.confirmAction(scanner, "Confirm update?")) {
            CLILogger.info("Update cancelled.");
            return;
        }

        try {
            UserDTO user = getCurrentUser();
            userService.updateUser(user, user.userID(), null, email, password, null);
            CLIHelper.printSuccess("Profile updated successfully");
            
            if (password != null) {
                CLILogger.info("Your password has been changed. Please use the new password on your next login.");
            }
            
        } catch (Exception e) {
            CLILogger.error("Error updating profile", e);
        }
    }

    private void handleViewMyNotifications() {
        CLIHelper.printHeader("My Notifications");
        
        try {
            User user = userService.getDomainUserByUsername(getCurrentUser().username());
            List<Notification> unreadNotifications = user.getUnreadNotifications();
            List<Notification> readNotifications = user.getReadNotifications();
            
            if (unreadNotifications.isEmpty() && readNotifications.isEmpty()) {
                CLILogger.info("You have no notifications.");
                return;
            }
            
            if (!unreadNotifications.isEmpty()) {
                CLILogger.info("Unread Notifications (" + unreadNotifications.size() + "):");
                for (int i = 0; i < unreadNotifications.size(); i++) {
                    Notification notification = unreadNotifications.get(i);
                    CLILogger.info((i + 1) + ". [" + notification.getType() + "] " + notification.getNotificationContent());
                    CLILogger.info("   " + notification.getTimestamp());
                }
                
                if (CLIHelper.confirmAction(scanner, "Mark all as read?")) {
                    for (Notification notification : unreadNotifications) {
                        user.markAsRead(notification);
                    }
                    userService.getDomainUserByUsername(getCurrentUser().username());
                    CLIHelper.printSuccess("All notifications marked as read");
                }
            }
            
            if (!readNotifications.isEmpty()) {
                CLILogger.info("Read Notifications (" + readNotifications.size() + "):");
                int displayCount = Math.min(5, readNotifications.size());
                for (int i = 0; i < displayCount; i++) {
                    Notification notification = readNotifications.get(i);
                    CLILogger.info((i + 1) + ". [" + notification.getType() + "] " + notification.getNotificationContent());
                    CLILogger.info("   " + notification.getTimestamp());
                }
                if (readNotifications.size() > 5) {
                    CLILogger.info("... and " + (readNotifications.size() - 5) + " more");
                }
            }
            
        } catch (UserNotFoundException e) {
            CLILogger.error("Could not retrieve notifications", e);
        }
    }
    
    private void displayBookResultsWithNumbers(List<Book> books) {
        CLILogger.info(String.format("%-4s %-35s %-25s %-15s %-10s", "#", "Title", "Author", "ISBN", "Status"));
        
        for (int i = 0; i < books.size(); i++) {
            Book book = books.get(i);
            String title = CLIHelper.truncate(book.getTitle(), 33);
            String author = CLIHelper.truncate(book.getAuthor(), 23);
            String isbn = CLIHelper.truncate(book.getIsbn(), 13);
            String status = book.isAvailable() ? "Available" : "Unavailable";
            
            CLILogger.info(String.format("%-4d %-35s %-25s %-15s %-10s", 
                (i + 1), title, author, isbn, status));
        }
    }

    private void displayCDResultsWithNumbers(List<CD> cds) {
        CLILogger.info(String.format("%-4s %-40s %-30s %-10s", "#", "Title", "Artist", "Status"));
        
        for (int i = 0; i < cds.size(); i++) {
            CD cd = cds.get(i);
            String title = CLIHelper.truncate(cd.getTitle(), 38);
            String artist = CLIHelper.truncate(cd.getArtist(), 28);
            String status = cd.isAvailable() ? "Available" : "Unavailable";
            
            CLILogger.info(String.format("%-4d %-40s %-30s %-10s", 
                (i + 1), title, artist, status));
        }
    }

    private void displayJournalResultsWithNumbers(List<Journal> journals) {
        CLILogger.info(String.format("%-4s %-45s %-35s %-10s", "#", "Title", "Author/Publisher", "Status"));
        
        for (int i = 0; i < journals.size(); i++) {
            Journal journal = journals.get(i);
            String title = CLIHelper.truncate(journal.getTitle(), 43);
            String author = CLIHelper.truncate(journal.getAuthor(), 33);
            String status = journal.isAvailable() ? "Available" : "Unavailable";
            
            CLILogger.info(String.format("%-4d %-45s %-35s %-10s", 
                (i + 1), title, author, status));
        }
    }

    private void displayLoansWithItemDetails(List<Loan> loans) {
        CLILogger.info(String.format("%-4s %-35s %-15s %-15s %-15s %-15s", 
            "#", "Item", "Type", "Borrowed", "Due Date", "Status"));
        
        for (int i = 0; i < loans.size(); i++) {
            Loan loan = loans.get(i);
            String itemDetails = CLIHelper.truncate(getItemDetails(loan.getItemId(), loan.getItemType()), 33);
            String type = CLIHelper.capitalizeFirst(loan.getItemType());
            String status;
            
            if (loan.isOverdue()) {
                long daysOverdue = loan.getDaysOverdue();
                status = "OVERDUE (" + daysOverdue + "d)";
            } else {
                status = "Active";
            }
            
            CLILogger.info(String.format("%-4d %-35s %-15s %-15s %-15s %-15s", 
                (i + 1), itemDetails, type, loan.getBorrowDate(), loan.getDueDate(), status));
        }
    }
}
