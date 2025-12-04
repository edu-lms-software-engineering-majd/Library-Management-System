package lms.presentation;

import java.util.ArrayList;
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
import lms.domain.LoanableItem;
import lms.domain.Notification;
import lms.domain.User;
import lms.domain.exception.UserNotFoundException;

/**
 * Enhanced User CLI with user-friendly features for the Library Management System.
 * 
 * <p>This CLI provides an intuitive interface for library users to:</p>
 * <ul>
 *   <li>Browse and search for items easily</li>
 *   <li>Borrow items using partial names or IDs</li>
 *   <li>Return items with simple selection</li>
 *   <li>Manage their profile and view notifications</li>
 * </ul>
 * 
 * @author Enhanced by Claude
 * @version 2.0
 */
public class UserCLI implements CLI {

    private final Scanner scanner = new Scanner(System.in);
    private final UserService userService;
    private final BookService bookService;
    private final CDService cdService;
    private final JournalService journalService;
    private final LoanService loanService;
    private final NotificationService notificationService;
    private final AuthService authService;
    private final AccountService accountService;

    public UserCLI(UserService userService, BookService bookService, CDService cdService,
                   JournalService journalService, LoanService loanService,
                   NotificationService notificationService, AuthService authService, AccountService accountService) {
        this.userService = userService;
        this.bookService = bookService;
        this.cdService = cdService;
        this.journalService = journalService;
        this.loanService = loanService;
        this.notificationService = notificationService;
        this.authService = authService;
        this.accountService = accountService;
    }

    @Override
    public void start() {
        UserDTO currentUser = AuthService.getCurrentUser();
        System.out.println("\n╔═══════════════════════════════════════════════════╗");
        System.out.println("║        Library Management System - User          ║");
        System.out.println("╚═══════════════════════════════════════════════════╝");
        System.out.println("    Welcome, " + currentUser.firstName() + " " + currentUser.lastName() + "!");
        
        
        try {
            User user = userService.getDomainUserByUsername(currentUser.username());
            int unreadCount = user.getUnreadNotificationCount();
            if (unreadCount > 0) {
                System.out.println("    📬 You have " + unreadCount + " unread notification(s)!");
            }
        } catch (Exception e) {
           
        }

        boolean running = true;
        while (running) {
            showUserMenu();
            String choice = scanner.nextLine().trim();
            
            boolean isLibrarian = currentUser.role() == lms.domain.Role.LIBRARIAN;

            
            if (isLibrarian) {
              
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
                        System.out.println("\n👋 Logging out... Goodbye!");
                        authService.logout();
                        running = false;
                        break;
                    default:
                        System.out.println("❌ Invalid choice, try again.");
                }
            } else {
           
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
                        System.out.println("\n👋 Logging out... Goodbye!");
                        authService.logout();
                        running = false;
                        break;
                    default:
                        System.out.println("❌ Invalid choice, try again.");
                }
            }
        }
    }

    private void showUserMenu() {
        UserDTO currentUser = AuthService.getCurrentUser();
        boolean isLibrarian = currentUser.role() == lms.domain.Role.LIBRARIAN;
        
        System.out.println("\n╔═══════════════════════════════════════════════════╗");
        System.out.println("║                    USER MENU                      ║");
        System.out.println("╠═══════════════════════════════════════════════════╣");
        System.out.println("║  1. 📚 Browse & Search Items                      ║");
        System.out.println("║  2. 📖 Borrow an Item                             ║");
        
        if (isLibrarian) {
            System.out.println("║  3. 📤 Process Returns (Librarian Only)           ║");
            System.out.println("║  4. 📋 View My Loans                              ║");
            System.out.println("║  5. 💰 My Account & Finances                      ║");
            System.out.println("║  6. 👤 View My Profile                            ║");
            System.out.println("║  7. ✏️  Update My Profile                          ║");
            System.out.println("║  8. 📬 View My Notifications                      ║");
            System.out.println("║  9. 🚪 Logout                                     ║");
        } else {
            System.out.println("║  3. 📋 View My Loans                              ║");
            System.out.println("║  4. 💰 My Account & Finances                      ║");
            System.out.println("║  5. 👤 View My Profile                            ║");
            System.out.println("║  6. ✏️  Update My Profile                          ║");
            System.out.println("║  7. 📬 View My Notifications                      ║");
            System.out.println("║  8. 🚪 Logout                                     ║");
        }
        
        System.out.println("╚═══════════════════════════════════════════════════╝");
        
        if (!isLibrarian) {
            System.out.println("ℹ️  Note: To return items, please visit the librarian desk.");
        }
        
        System.out.print("Enter your choice: ");
    }

    private void handleBrowseAndSearchItems() {
        System.out.println("\n╔═══════════════════════════════════════════════════╗");
        System.out.println("║           Browse & Search Library Items           ║");
        System.out.println("╚═══════════════════════════════════════════════════╝");
        System.out.println("1. 📚 Browse All Books");
        System.out.println("2. 💿 Browse All CDs");
        System.out.println("3. 📰 Browse All Journals");
        System.out.println("4. 🔍 Search Items");
        System.out.println("0. ⬅️  Back to Main Menu");
        System.out.print("Choose an option: ");
        
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
                System.out.println("❌ Invalid choice!");
        }
    }

    private void browseBooks() {
        System.out.println("\n╔═══════════════════════════════════════════════════╗");
        System.out.println("║                  All Books                        ║");
        System.out.println("╚═══════════════════════════════════════════════════╝");
        List<Book> books = bookService.getAllBooks();
        if (books.isEmpty()) {
            System.out.println("📭 No books available in the library.");
            return;
        }
        displayBookResultsWithNumbers(books);
    }

    private void browseCDs() {
        System.out.println("\n╔═══════════════════════════════════════════════════╗");
        System.out.println("║                    All CDs                        ║");
        System.out.println("╚═══════════════════════════════════════════════════╝");
        List<CD> cds = cdService.getAllCDs();
        if (cds.isEmpty()) {
            System.out.println("📭 No CDs available in the library.");
            return;
        }
        displayCDResultsWithNumbers(cds);
    }

    private void browseJournals() {
        System.out.println("\n╔═══════════════════════════════════════════════════╗");
        System.out.println("║                 All Journals                      ║");
        System.out.println("╚═══════════════════════════════════════════════════╝");
        List<Journal> journals = journalService.getAllJournals();
        if (journals.isEmpty()) {
            System.out.println("📭 No journals available in the library.");
            return;
        }
        displayJournalResultsWithNumbers(journals);
    }

    private void handleSearchItems() {
        System.out.println("\n╔═══════════════════════════════════════════════════╗");
        System.out.println("║                 Search Items                      ║");
        System.out.println("╚═══════════════════════════════════════════════════╝");
        System.out.println("Choose item type to search:");
        System.out.println("1. 📚 Books");
        System.out.println("2. 💿 CDs");
        System.out.println("3. 📰 Journals");
        System.out.print("Choose an option: ");
        String itemChoice = scanner.nextLine().trim();

        System.out.println("\nSearch by:");
        System.out.println("1. Title");
        System.out.println("2. Author/Artist");
        System.out.print("Choose criteria: ");
        String criteriaChoice = scanner.nextLine().trim();

        System.out.print("Enter search term (partial match works): ");
        String searchTerm = scanner.nextLine().trim();

        if (searchTerm.isEmpty()) {
            System.out.println("❌ Search term cannot be empty!");
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
                System.out.println("❌ Invalid item type!");
        }
    }

    private void searchBooks(String criteriaChoice, String searchTerm) {
        SearchStrategy<Book> strategy;
        switch (criteriaChoice) {
            case "1":
                strategy = new SearchByTitleStrategy();
                break;
            case "2":
                strategy = new SearchByAuthorStrategy();
                break;
            default:
                System.out.println("❌ Invalid criteria!");
                return;
        }
        List<Book> results = bookService.searchBooks(strategy, searchTerm);
        
        if (results.isEmpty()) {
            System.out.println("\n📭 No books found matching '" + searchTerm + "'");
        } else {
            System.out.println("\n✅ Found " + results.size() + " book(s) matching '" + searchTerm + "':");
            displayBookResultsWithNumbers(results);
        }
    }

    private void searchCDs(String criteriaChoice, String searchTerm) {
        SearchStrategy<CD> strategy;
        switch (criteriaChoice) {
            case "1":
                strategy = new SearchCDByTitleStrategy();
                break;
            case "2":
                strategy = new SearchCDByArtistStrategy();
                break;
            default:
                System.out.println("❌ Invalid criteria!");
                return;
        }
        List<CD> results = cdService.searchCDs(strategy, searchTerm);
        
        if (results.isEmpty()) {
            System.out.println("\n📭 No CDs found matching '" + searchTerm + "'");
        } else {
            System.out.println("\n✅ Found " + results.size() + " CD(s) matching '" + searchTerm + "':");
            displayCDResultsWithNumbers(results);
        }
    }

    private void searchJournals(String criteriaChoice, String searchTerm) {
        SearchStrategy<Journal> strategy;
        switch (criteriaChoice) {
            case "1":
                strategy = new SearchJournalByTitleStrategy();
                break;
            case "2":
                strategy = new SearchJournalByAuthorStrategy();
                break;
            default:
                System.out.println("❌ Invalid criteria!");
                return;
        }
        List<Journal> results = journalService.searchJournals(strategy, searchTerm);
        
        if (results.isEmpty()) {
            System.out.println("\n📭 No journals found matching '" + searchTerm + "'");
        } else {
            System.out.println("\n✅ Found " + results.size() + " journal(s) matching '" + searchTerm + "':");
            displayJournalResultsWithNumbers(results);
        }
    }

    private void handleBorrowItem() {
        System.out.println("\n╔═══════════════════════════════════════════════════╗");
        System.out.println("║                 Borrow an Item                    ║");
        System.out.println("╚═══════════════════════════════════════════════════╝");
        System.out.println("Choose item type to borrow:");
        System.out.println("1. 📚 Book");
        System.out.println("2. 💿 CD");
        System.out.println("3. 📰 Journal");
        System.out.print("Choose an option: ");
        
        String itemChoice = scanner.nextLine().trim();
        String itemType = "";
        
        switch (itemChoice) {
            case "1":
                itemType = "book";
                borrowBook();
                break;
            case "2":
                itemType = "cd";
                borrowCD();
                break;
            case "3":
                itemType = "journal";
                borrowJournal();
                break;
            default:
                System.out.println("❌ Invalid item type!");
        }
    }

    private void borrowBook() {
        List<Book> availableBooks = bookService.getAllBooks().stream()
                .filter(Book::isAvailable)
                .collect(Collectors.toList());
        
        if (availableBooks.isEmpty()) {
            System.out.println("\n📭 Sorry, no books are currently available for borrowing.");
            return;
        }
        
        System.out.println("\n📚 Available Books:");
        displayBookResultsWithNumbers(availableBooks);
        
        System.out.print("\nEnter book title, author, or ID (partial match works): ");
        String searchTerm = scanner.nextLine().trim();
        
        if (searchTerm.isEmpty()) {
            System.out.println("❌ Search term cannot be empty!");
            return;
        }
        
        Book selectedBook = findBookBySearchTerm(availableBooks, searchTerm);
        
        if (selectedBook == null) {
            System.out.println("❌ No book found matching '" + searchTerm + "'");
            return;
        }
        
        System.out.println("\n📖 You selected: " + selectedBook.getTitle() + " by " + selectedBook.getAuthor());
        System.out.print("Confirm borrow? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        
        if (!confirm.equals("yes") && !confirm.equals("y")) {
            System.out.println("❌ Borrow cancelled.");
            return;
        }
        
        try {
            UserDTO currentUser = AuthService.getCurrentUser();
            loanService.loanItem(currentUser, selectedBook.getId(), "book");
            System.out.println("✅ Book borrowed successfully! Due date: 28 days from today.");
        } catch (Exception e) {
            System.out.println("❌ Error borrowing book: " + e.getMessage());
        }
    }

    private void borrowCD() {
        List<CD> availableCDs = cdService.getAllCDs().stream()
                .filter(CD::isAvailable)
                .collect(Collectors.toList());
        
        if (availableCDs.isEmpty()) {
            System.out.println("\n📭 Sorry, no CDs are currently available for borrowing.");
            return;
        }
        
        System.out.println("\n💿 Available CDs:");
        displayCDResultsWithNumbers(availableCDs);
        
        System.out.print("\nEnter CD title, artist, or ID (partial match works): ");
        String searchTerm = scanner.nextLine().trim();
        
        if (searchTerm.isEmpty()) {
            System.out.println("❌ Search term cannot be empty!");
            return;
        }
        
        CD selectedCD = findCDBySearchTerm(availableCDs, searchTerm);
        
        if (selectedCD == null) {
            System.out.println("❌ No CD found matching '" + searchTerm + "'");
            return;
        }
        
        System.out.println("\n💿 You selected: " + selectedCD.getTitle() + " by " + selectedCD.getArtist());
        System.out.print("Confirm borrow? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        
        if (!confirm.equals("yes") && !confirm.equals("y")) {
            System.out.println("❌ Borrow cancelled.");
            return;
        }
        
        try {
            UserDTO currentUser = AuthService.getCurrentUser();
            loanService.loanItem(currentUser, selectedCD.getId(), "cd");
            System.out.println("✅ CD borrowed successfully! Due date: 21 days from today.");
        } catch (Exception e) {
            System.out.println("❌ Error borrowing CD: " + e.getMessage());
        }
    }

    private void borrowJournal() {
        List<Journal> availableJournals = journalService.getAllJournals().stream()
                .filter(Journal::isAvailable)
                .collect(Collectors.toList());
        
        if (availableJournals.isEmpty()) {
            System.out.println("\n📭 Sorry, no journals are currently available for borrowing.");
            return;
        }
        
        System.out.println("\n📰 Available Journals:");
        displayJournalResultsWithNumbers(availableJournals);
        
        System.out.print("\nEnter journal title, author, or ID (partial match works): ");
        String searchTerm = scanner.nextLine().trim();
        
        if (searchTerm.isEmpty()) {
            System.out.println("❌ Search term cannot be empty!");
            return;
        }
        
        Journal selectedJournal = findJournalBySearchTerm(availableJournals, searchTerm);
        
        if (selectedJournal == null) {
            System.out.println("❌ No journal found matching '" + searchTerm + "'");
            return;
        }
        
        System.out.println("\n📰 You selected: " + selectedJournal.getTitle() + " by " + selectedJournal.getAuthor());
        System.out.print("Confirm borrow? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        
        if (!confirm.equals("yes") && !confirm.equals("y")) {
            System.out.println("❌ Borrow cancelled.");
            return;
        }
        
        try {
            UserDTO currentUser = AuthService.getCurrentUser();
            loanService.loanItem(currentUser, selectedJournal.getId(), "journal");
            System.out.println("✅ Journal borrowed successfully! Due date: 7 days from today.");
        } catch (Exception e) {
            System.out.println("❌ Error borrowing journal: " + e.getMessage());
        }
    }

    private Book findBookBySearchTerm(List<Book> books, String searchTerm) {
        String lowerSearch = searchTerm.toLowerCase();
        
      
        for (Book book : books) {
            if (book.getTitle().equalsIgnoreCase(searchTerm) || 
                book.getAuthor().equalsIgnoreCase(searchTerm) ||
                book.getId().toString().equalsIgnoreCase(searchTerm)) {
                return book;
            }
        }
        
       
        List<Book> matches = books.stream()
                .filter(b -> b.getTitle().toLowerCase().contains(lowerSearch) ||
                           b.getAuthor().toLowerCase().contains(lowerSearch) ||
                           b.getId().toString().toLowerCase().contains(lowerSearch))
                .collect(Collectors.toList());
        
        if (matches.isEmpty()) {
            return null;
        }
        
        if (matches.size() == 1) {
            return matches.get(0);
        }
        
         
        System.out.println("\n🔍 Multiple matches found:");
        displayBookResultsWithNumbers(matches);
        System.out.print("Enter the number of the book you want (1-" + matches.size() + "): ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice >= 1 && choice <= matches.size()) {
                return matches.get(choice - 1);
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid number!");
        }
        
        return null;
    }

    private CD findCDBySearchTerm(List<CD> cds, String searchTerm) {
        String lowerSearch = searchTerm.toLowerCase();
        
        
        for (CD cd : cds) {
            if (cd.getTitle().equalsIgnoreCase(searchTerm) || 
                cd.getArtist().equalsIgnoreCase(searchTerm) ||
                cd.getId().toString().equalsIgnoreCase(searchTerm)) {
                return cd;
            }
        }
        
   
        List<CD> matches = cds.stream()
                .filter(c -> c.getTitle().toLowerCase().contains(lowerSearch) ||
                           c.getArtist().toLowerCase().contains(lowerSearch) ||
                           c.getId().toString().toLowerCase().contains(lowerSearch))
                .collect(Collectors.toList());
        
        if (matches.isEmpty()) {
            return null;
        }
        
        if (matches.size() == 1) {
            return matches.get(0);
        }
        
         
        System.out.println("\n🔍 Multiple matches found:");
        displayCDResultsWithNumbers(matches);
        System.out.print("Enter the number of the CD you want (1-" + matches.size() + "): ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice >= 1 && choice <= matches.size()) {
                return matches.get(choice - 1);
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid number!");
        }
        
        return null;
    }

    private Journal findJournalBySearchTerm(List<Journal> journals, String searchTerm) {
        String lowerSearch = searchTerm.toLowerCase();
        
        
        for (Journal journal : journals) {
            if (journal.getTitle().equalsIgnoreCase(searchTerm) || 
                journal.getAuthor().equalsIgnoreCase(searchTerm) ||
                journal.getId().toString().equalsIgnoreCase(searchTerm)) {
                return journal;
            }
        }
        
        
        List<Journal> matches = journals.stream()
                .filter(j -> j.getTitle().toLowerCase().contains(lowerSearch) ||
                           j.getAuthor().toLowerCase().contains(lowerSearch) ||
                           j.getId().toString().toLowerCase().contains(lowerSearch))
                .collect(Collectors.toList());
        
        if (matches.isEmpty()) {
            return null;
        }
        
        if (matches.size() == 1) {
            return matches.get(0);
        }
        
        
        System.out.println("\n🔍 Multiple matches found:");
        displayJournalResultsWithNumbers(matches);
        System.out.print("Enter the number of the journal you want (1-" + matches.size() + "): ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice >= 1 && choice <= matches.size()) {
                return matches.get(choice - 1);
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid number!");
        }
        
        return null;
    }

    private void handleReturnItem() {
        System.out.println("\n╔═══════════════════════════════════════════════════╗");
        System.out.println("║        Process Returns (Librarian Desk)          ║");
        System.out.println("╚═══════════════════════════════════════════════════╝");
        
        System.out.println("Search for user by:");
        System.out.println("1. Username");
        System.out.println("2. View All Users with Active Loans");
        System.out.println("0. Cancel");
        System.out.print("Choose an option: ");
        
        String searchChoice = scanner.nextLine().trim();
        
        switch (searchChoice) {
            case "1":
                processReturnByUsername();
                break;
            case "2":
                processReturnViewAllUsers();
                break;
            case "0":
                System.out.println("❌ Return processing cancelled.");
                return;
            default:
                System.out.println("❌ Invalid choice!");
        }
    }

    private void processReturnByUsername() {
        System.out.print("\nEnter username (partial match works): ");
        String searchTerm = scanner.nextLine().trim();
        
        if (searchTerm.isEmpty()) {
            System.out.println("❌ Username cannot be empty!");
            return;
        }
        
        try {
          
            List<lms.application.UserDTO> allUsers = userService.getAllUsers();
            List<lms.application.UserDTO> matchingUsers = allUsers.stream()
                    .filter(u -> u.username().toLowerCase().contains(searchTerm.toLowerCase()) ||
                               u.firstName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                               u.lastName().toLowerCase().contains(searchTerm.toLowerCase()))
                    .collect(java.util.stream.Collectors.toList());
            
            if (matchingUsers.isEmpty()) {
                System.out.println("❌ No users found matching '" + searchTerm + "'");
                return;
            }
            
            lms.application.UserDTO selectedUser;
            
            if (matchingUsers.size() == 1) {
                selectedUser = matchingUsers.get(0);
            } else {
              
                System.out.println("\n🔍 Multiple users found:");
                System.out.println("═".repeat(80));
                System.out.printf("%-4s %-20s %-25s %-20s%n", "#", "Username", "Name", "Role");
                System.out.println("═".repeat(80));
                
                for (int i = 0; i < matchingUsers.size(); i++) {
                    lms.application.UserDTO user = matchingUsers.get(i);
                    System.out.printf("%-4d %-20s %-25s %-20s%n", 
                        (i + 1), 
                        user.username(),
                        user.firstName() + " " + user.lastName(),
                        user.role());
                }
                System.out.println("═".repeat(80));
                
                System.out.print("\nSelect user number (1-" + matchingUsers.size() + "): ");
                try {
                    int choice = Integer.parseInt(scanner.nextLine().trim());
                    if (choice < 1 || choice > matchingUsers.size()) {
                        System.out.println("❌ Invalid selection!");
                        return;
                    }
                    selectedUser = matchingUsers.get(choice - 1);
                } catch (NumberFormatException e) {
                    System.out.println("❌ Invalid number!");
                    return;
                }
            }
            
         
            processReturnForUser(selectedUser);
            
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private void processReturnViewAllUsers() {
        try {
         
            List<lms.application.UserDTO> allUsers = userService.getAllUsers();
            List<lms.application.UserDTO> usersWithLoans = new ArrayList<>();
            
            for (lms.application.UserDTO user : allUsers) {
                List<Loan> activeLoans = loanService.getUserActiveLoans(user.userID());
                if (!activeLoans.isEmpty()) {
                    usersWithLoans.add(user);
                }
            }
            
            if (usersWithLoans.isEmpty()) {
                System.out.println("\n📭 No users currently have active loans.");
                return;
            }
            
            System.out.println("\n👥 Users with Active Loans:");
            System.out.println("═".repeat(100));
            System.out.printf("%-4s %-20s %-25s %-15s %-15s%n", "#", "Username", "Name", "Active Loans", "Has Overdue");
            System.out.println("═".repeat(100));
            
            for (int i = 0; i < usersWithLoans.size(); i++) {
                lms.application.UserDTO user = usersWithLoans.get(i);
                List<Loan> activeLoans = loanService.getUserActiveLoans(user.userID());
                long overdueCount = activeLoans.stream().filter(Loan::isOverdue).count();
                String overdueStatus = overdueCount > 0 ? "⚠️  Yes (" + overdueCount + ")" : "✅ No";
                
                System.out.printf("%-4d %-20s %-25s %-15d %-15s%n", 
                    (i + 1), 
                    user.username(),
                    user.firstName() + " " + user.lastName(),
                    activeLoans.size(),
                    overdueStatus);
            }
            System.out.println("═".repeat(100));
            
            System.out.print("\nSelect user number (1-" + usersWithLoans.size() + ") or 0 to cancel: ");
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                
                if (choice == 0) {
                    System.out.println("❌ Cancelled.");
                    return;
                }
                
                if (choice < 1 || choice > usersWithLoans.size()) {
                    System.out.println("❌ Invalid selection!");
                    return;
                }
                
                lms.application.UserDTO selectedUser = usersWithLoans.get(choice - 1);
                processReturnForUser(selectedUser);
                
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid number!");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private void processReturnForUser(lms.application.UserDTO user) {
        try {
            System.out.println("\n╔═══════════════════════════════════════════════════╗");
            System.out.println("║  Processing Return for: " + truncate(user.username(), 27) + " ║");
            System.out.println("╚═══════════════════════════════════════════════════╝");
            
            List<Loan> activeLoans = loanService.getUserActiveLoans(user.userID());
            
            if (activeLoans.isEmpty()) {
                System.out.println("📭 This user has no active loans.");
                return;
            }
            
            System.out.println("📋 User: " + user.firstName() + " " + user.lastName() + " (" + user.username() + ")");
            System.out.println("📚 Active Loans: " + activeLoans.size());
            System.out.println();
            
            displayLoansWithItemDetails(activeLoans);
            
            System.out.print("\nEnter the number of the item to return (1-" + activeLoans.size() + "), or 0 to cancel: ");
            String input = scanner.nextLine().trim();
            
            try {
                int choice = Integer.parseInt(input);
                
                if (choice == 0) {
                    System.out.println("❌ Return cancelled.");
                    return;
                }
                
                if (choice < 1 || choice > activeLoans.size()) {
                    System.out.println("❌ Invalid selection!");
                    return;
                }
                
                Loan selectedLoan = activeLoans.get(choice - 1);
                
               
                String itemDetails = getItemDetails(selectedLoan.getItemId(), selectedLoan.getItemType());
                System.out.println("\n📤 Processing return of: " + itemDetails);
                System.out.println("👤 For user: " + user.firstName() + " " + user.lastName());
                
                if (selectedLoan.isOverdue()) {
                    long daysOverdue = selectedLoan.getDaysOverdue();
                    double fine = selectedLoan.calculateFine();
                    System.out.println("\n⚠️  WARNING: This item is " + daysOverdue + " day(s) overdue!");
                    System.out.println("💰 A fine of " + String.format("%.2f", fine) + " NIS will be applied to the user's account.");
                }
                
                System.out.print("\nConfirm return? (yes/no): ");
                String confirm = scanner.nextLine().trim().toLowerCase();
                
                if (!confirm.equals("yes") && !confirm.equals("y")) {
                    System.out.println("❌ Return cancelled.");
                    return;
                }
                
                loanService.returnItem(user.userID(), selectedLoan.getId());
                System.out.println("\n✅ Item returned successfully!");
                
               
                if (selectedLoan.isOverdue()) {
                    User domainUser = userService.getDomainUserByUsername(user.username());
                    double currentBalance = domainUser.getAccount().getTotalFines();
                    System.out.println("💰 User's current balance: " + String.format("%.2f", currentBalance) + " NIS");
                    
                    if (currentBalance > 0) {
                        System.out.println("ℹ️  User should pay fines before borrowing more items.");
                    }
                }
                
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid number format!");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error processing return: " + e.getMessage());
        }
    }

    private String getItemDetails(UUID itemId, String itemType) {
        try {
            switch (itemType.toLowerCase()) {
                case "book":
                    Book book = bookService.getBookById(itemId);
                    return book != null ? book.getTitle() + " by " + book.getAuthor() : "Unknown Book";
                case "cd":
                    CD cd = cdService.getCDById(itemId);
                    return cd != null ? cd.getTitle() + " by " + cd.getArtist() : "Unknown CD";
                case "journal":
                    Journal journal = journalService.getJournalById(itemId);
                    return journal != null ? journal.getTitle() + " by " + journal.getAuthor() : "Unknown Journal";
                default:
                    return "Unknown Item";
            }
        } catch (Exception e) {
            return "Unknown Item";
        }
    }

    private void handleViewMyLoans() {
        System.out.println("\n╔═══════════════════════════════════════════════════╗");
        System.out.println("║                 My Active Loans                   ║");
        System.out.println("╚═══════════════════════════════════════════════════╝");
        
        UserDTO currentUser = AuthService.getCurrentUser();
        
        try {
            List<Loan> activeLoans = loanService.getUserActiveLoans(currentUser.userID());
            
            if (activeLoans.isEmpty()) {
                System.out.println("📭 You have no active loans.");
                return;
            }
            
            System.out.println("📋 Total active loans: " + activeLoans.size());
        
            long overdueCount = activeLoans.stream().filter(Loan::isOverdue).count();
            if (overdueCount > 0) {
                System.out.println("⚠️  You have " + overdueCount + " overdue item(s)!");
            }
            
            System.out.println();
            displayLoansWithItemDetails(activeLoans);
            
        } catch (UserNotFoundException e) {
            System.out.println("❌ Could not retrieve your loans.");
        }
    }

    private void handleAccountManagement() {
        System.out.println("\n╔═══════════════════════════════════════════════════╗");
        System.out.println("║            My Account & Finances                  ║");
        System.out.println("╚═══════════════════════════════════════════════════╝");
        System.out.println("1. 💵 View Account Summary");
        System.out.println("2. 📊 View Transaction History");
        System.out.println("3. 💳 Pay Fines");
        System.out.println("4. 📈 View Account Statistics");
        System.out.println("0. ⬅️  Back to Main Menu");
        System.out.print("Choose an option: ");
        
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
                System.out.println("❌ Invalid choice!");
        }
    }

    private void handleViewAccountSummary() {
        System.out.println("\n╔═══════════════════════════════════════════════════╗");
        System.out.println("║              Account Summary                      ║");
        System.out.println("╚═══════════════════════════════════════════════════╝");
        
        UserDTO currentUser = AuthService.getCurrentUser();
        
        try {
            User user = userService.getDomainUserByUsername(currentUser.username());
            lms.domain.Account account = user.getAccount();
            
            System.out.println("📋 Account ID: " + account.getAccountId().toString().substring(0, 8) + "...");
            System.out.println("📅 Created: " + account.getCreatedAt());
            System.out.println("🔄 Last Updated: " + account.getUpdatedAt());
            
          
            String statusIcon = account.getStatus() == lms.domain.AccountStatus.ACTIVE ? "✅" : "❌";
            System.out.println(statusIcon + " Status: " + account.getStatus());
            
           
            System.out.println("\n💰 Financial Summary:");
            System.out.println("═".repeat(55));
            double totalFines = account.getTotalFines();
            
            if (totalFines > 0) {
                System.out.println("⚠️  Outstanding Balance: " + String.format("%.2f NIS", totalFines));
                
                if (totalFines >= 100.0) {
                    System.out.println("🚫 WARNING: Your account may be suspended due to high fines!");
                    System.out.println("   Please pay your fines to continue borrowing.");
                }
            } else {
                System.out.println("✅ No outstanding balance - Your account is clear!");
            }
            
            
            System.out.println("\n📚 Borrowing Status:");
            System.out.println("═".repeat(55));
            if (user.canBorrow()) {
                System.out.println("✅ You can borrow items");
                System.out.println("📊 Active Loans: " + user.getActiveLoanCount() + "/10");
            } else {
                if (user.hasFine()) {
                    System.out.println("❌ Borrowing suspended due to outstanding fines");
                } else if (user.isAtBorrowLimit()) {
                    System.out.println("❌ You have reached the maximum borrow limit (10 items)");
                } else {
                    System.out.println("❌ Borrowing is currently restricted");
                }
            }
            
        } catch (UserNotFoundException e) {
            System.out.println("❌ Could not retrieve account information.");
        }
    }

    private void handleViewTransactionHistory() {
        System.out.println("\n╔═══════════════════════════════════════════════════╗");
        System.out.println("║           Transaction History                     ║");
        System.out.println("╚═══════════════════════════════════════════════════╝");
        
        UserDTO currentUser = AuthService.getCurrentUser();
        
        try {
            User user = userService.getDomainUserByUsername(currentUser.username());
            lms.domain.Account account = user.getAccount();
            List<lms.domain.FineTransaction> transactions = account.getFineTransactions();
            
            if (transactions.isEmpty()) {
                System.out.println("📭 No transactions found. You have a clean record!");
                return;
            }
            
            System.out.println("📊 Total Transactions: " + transactions.size());
            System.out.println();
            
            
            System.out.println("═".repeat(110));
            System.out.printf("%-4s %-12s %-15s %-15s %-50s%n", 
                "#", "Date", "Type", "Amount", "Description");
            System.out.println("═".repeat(110));
            
            double runningBalance = 0.0;
            for (int i = 0; i < transactions.size(); i++) {
                lms.domain.FineTransaction transaction = transactions.get(i);
                
                String typeIcon = switch (transaction.getType()) {
                    case FINE -> "💸 FINE";
                    case PAYMENT -> "💳 PAYMENT";
                    default -> "📝 OTHER";
                };
                
                double amount = transaction.getAmount();
                String amountStr;
                
                if (transaction.getType() == lms.domain.TransactionType.FINE) {
                    amountStr = String.format("+%.2f NIS", amount);
                    runningBalance += amount;
                } else {
                    amountStr = String.format("-%.2f NIS", amount);
                    runningBalance -= amount;
                }
                
                String description = truncate(transaction.getDescription(), 48);
                
                System.out.printf("%-4d %-12s %-15s %-15s %-50s%n",
                    (i + 1),
                    transaction.getTransactionDate(),
                    typeIcon,
                    amountStr,
                    description);
            }
            System.out.println("═".repeat(110));
            System.out.println("Current Balance: " + String.format("%.2f NIS", account.getTotalFines()));
            
        } catch (UserNotFoundException e) {
            System.out.println("❌ Could not retrieve transaction history.");
        }
    }

    private void handlePayFines() {
        System.out.println("\n╔═══════════════════════════════════════════════════╗");
        System.out.println("║                  Pay Fines                        ║");
        System.out.println("╚═══════════════════════════════════════════════════╝");
        
        UserDTO currentUser = AuthService.getCurrentUser();
        
        try {
            User user = userService.getDomainUserByUsername(currentUser.username());
            lms.domain.Account account = user.getAccount();
            
            double totalFines = account.getTotalFines();
            
            if (totalFines == 0) {
                System.out.println("✅ You have no outstanding fines to pay!");
                return;
            }
            
            System.out.println("💰 Current Outstanding Balance: " + String.format("%.2f NIS", totalFines));
            System.out.println();
            System.out.println("How much would you like to pay?");
            System.out.println("1. Pay Full Amount (" + String.format("%.2f NIS", totalFines) + ")");
            System.out.println("2. Pay Partial Amount");
            System.out.println("0. Cancel");
            System.out.print("Choose an option: ");
            
            String choice = scanner.nextLine().trim();
            double paymentAmount = 0.0;
            
            switch (choice) {
                case "1":
                    paymentAmount = totalFines;
                    break;
                case "2":
                    System.out.print("Enter amount to pay (max " + String.format("%.2f", totalFines) + " NIS): ");
                    try {
                        paymentAmount = Double.parseDouble(scanner.nextLine().trim());
                        if (paymentAmount <= 0) {
                            System.out.println("❌ Payment amount must be positive!");
                            return;
                        }
                        if (paymentAmount > totalFines) {
                            System.out.println("❌ Payment amount cannot exceed outstanding balance!");
                            return;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("❌ Invalid amount format!");
                        return;
                    }
                    break;
                case "0":
                    System.out.println("❌ Payment cancelled.");
                    return;
                default:
                    System.out.println("❌ Invalid choice!");
                    return;
            }
               System.out.println("\n💳 Payment Summary:");
            System.out.println("   Amount to Pay: " + String.format("%.2f NIS", paymentAmount));
            System.out.println("   Remaining Balance: " + String.format("%.2f NIS", totalFines - paymentAmount));
            System.out.print("\nConfirm payment? (yes/no): ");
            
            String confirm = scanner.nextLine().trim().toLowerCase();
            
            if (!confirm.equals("yes") && !confirm.equals("y")) {
                System.out.println("❌ Payment cancelled.");
                return;
            }
            
           
            account.payFine(paymentAmount);
            userService.getDomainUserByUsername(currentUser.username()); // Refresh to persist
            
            System.out.println("\n✅ Payment successful!");
            System.out.println("💳 Amount Paid: " + String.format("%.2f NIS", paymentAmount));
            System.out.println("💰 New Balance: " + String.format("%.2f NIS", account.getTotalFines()));
            
            if (account.getTotalFines() == 0) {
                System.out.println("\n🎉 Congratulations! Your account is now clear!");
                if (account.getStatus() == lms.domain.AccountStatus.ACTIVE) {
                    System.out.println("✅ You can now borrow items from the library.");
                }
            }
            
        } catch (UserNotFoundException e) {
            System.out.println("❌ Could not process payment.");
        } catch (Exception e) {
            System.out.println("❌ Error processing payment: " + e.getMessage());
        }
    }

    private void handleViewAccountStatistics() {
        System.out.println("\n╔═══════════════════════════════════════════════════╗");
        System.out.println("║           Account Statistics                      ║");
        System.out.println("╚═══════════════════════════════════════════════════╝");
        
        UserDTO currentUser = AuthService.getCurrentUser();
        
        try {
            User user = userService.getDomainUserByUsername(currentUser.username());
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
            
            
            System.out.println("📊 Financial Statistics:");
            System.out.println("═".repeat(55));
            System.out.println("💸 Total Fines Incurred: " + String.format("%.2f NIS", totalFinesIncurred));
            System.out.println("💳 Total Payments Made: " + String.format("%.2f NIS", totalFinesPaid));
            System.out.println("💰 Current Balance: " + String.format("%.2f NIS", account.getTotalFines()));
            System.out.println("📝 Number of Fines: " + fineCount);
            System.out.println("💳 Number of Payments: " + paymentCount);
            
            System.out.println("\n📚 Borrowing Statistics:");
            System.out.println("═".repeat(55));
            System.out.println("📖 Total Loans (All Time): " + allLoans.size());
            System.out.println("📋 Active Loans: " + activeLoans.size());
            System.out.println("✅ Completed Loans: " + completedLoans);
            System.out.println("⚠️  Currently Overdue: " + overdueLoans);
            
            if (allLoans.size() > 0) {
                double overdueRate = ((double) overdueLoans / activeLoans.size()) * 100;
                System.out.println("📈 Current Overdue Rate: " + String.format("%.1f%%", overdueRate));
            }
            
            System.out.println("\n⏱️  Account Age:");
            System.out.println("═".repeat(55));
            long daysSinceCreation = java.time.temporal.ChronoUnit.DAYS.between(
                account.getCreatedAt(), java.time.LocalDate.now());
            System.out.println("📅 Member Since: " + account.getCreatedAt() + " (" + daysSinceCreation + " days)");
            
          
            System.out.println("\n⭐ Account Performance:");
            System.out.println("═".repeat(55));
            if (account.getTotalFines() == 0 && overdueLoans == 0) {
                System.out.println("🏆 EXCELLENT - No outstanding fines or overdue items!");
            } else if (account.getTotalFines() < 50 && overdueLoans <= 1) {
                System.out.println("✅ GOOD - Keep up the good work!");
            } else if (account.getTotalFines() < 100) {
                System.out.println("⚠️  FAIR - Please return items on time and pay fines.");
            } else {
                System.out.println("❌ NEEDS ATTENTION - High fines detected. Please settle your account.");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Could not retrieve account statistics: " + e.getMessage());
        }
    }

    private void handleViewMyProfile() {
        System.out.println("\n╔═══════════════════════════════════════════════════╗");
        System.out.println("║                  My Profile                       ║");
        System.out.println("╚═══════════════════════════════════════════════════╝");
        
        UserDTO currentUser = AuthService.getCurrentUser();
        
        try {
            UserDTO user = userService.getUserByUsername(currentUser.username());
            User domainUser = userService.getDomainUserByUsername(currentUser.username());
            
            System.out.println("👤 Username: " + user.username());
            System.out.println("📝 Name: " + user.firstName() + " " + user.lastName());
            System.out.println("📧 Email: " + user.email());
            System.out.println("🎭 Role: " + user.role());
            System.out.println("📅 Registration Date: " + domainUser.getRegistrationDate());
            System.out.println("📚 Active Loans: " + domainUser.getActiveLoanCount());
            System.out.println("💰 Total Fines: " + domainUser.getAccount().getTotalFines() + " NIS");
            
            if (domainUser.hasFine()) {
                System.out.println("⚠️  You have outstanding fines that must be paid before borrowing more items.");
            }
            
        } catch (UserNotFoundException e) {
            System.out.println("❌ Could not retrieve profile information.");
        }
    }

    private void handleUpdateMyProfile() {
        System.out.println("\n╔═══════════════════════════════════════════════════╗");
        System.out.println("║                Update My Profile                  ║");
        System.out.println("╚═══════════════════════════════════════════════════╝");
        
        UserDTO currentUser = AuthService.getCurrentUser();

        System.out.println("📝 Leave fields blank to keep current values\n");

        System.out.print("Enter new email (current: '" + currentUser.email() + "'): ");
        String email = scanner.nextLine().trim();
        if (email.isEmpty()) {
            email = null;
        }

        System.out.print("Enter new password (min 8 characters): ");
        String password = scanner.nextLine().trim();
        if (password.isEmpty()) {
            password = null;
        } else if (password.length() < 8) {
            System.out.println("❌ Password must be at least 8 characters long!");
            return;
        }

        if (email == null && password == null) {
            System.out.println("ℹ️  No changes made.");
            return;
        }

        System.out.print("\nConfirm update? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        
        if (!confirm.equals("yes") && !confirm.equals("y")) {
            System.out.println("❌ Update cancelled.");
            return;
        }

        try {
            userService.updateUser(currentUser, currentUser.userID(), null, email, password, null);
            System.out.println("✅ Profile updated successfully!");
            
            if (password != null) {
                System.out.println("🔐 Your password has been changed. Please use the new password on your next login.");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error updating profile: " + e.getMessage());
        }
    }

    private void handleViewMyNotifications() {
        System.out.println("\n╔═══════════════════════════════════════════════════╗");
        System.out.println("║                My Notifications                   ║");
        System.out.println("╚═══════════════════════════════════════════════════╝");
        
        UserDTO currentUser = AuthService.getCurrentUser();
        
        try {
            User user = userService.getDomainUserByUsername(currentUser.username());
            List<Notification> unreadNotifications = user.getUnreadNotifications();
            List<Notification> readNotifications = user.getReadNotifications();
            
            if (unreadNotifications.isEmpty() && readNotifications.isEmpty()) {
                System.out.println("📭 You have no notifications.");
                return;
            }
            
            if (!unreadNotifications.isEmpty()) {
                System.out.println("\n📬 Unread Notifications (" + unreadNotifications.size() + "):");
                System.out.println("═".repeat(55));
                for (int i = 0; i < unreadNotifications.size(); i++) {
                    Notification notification = unreadNotifications.get(i);
                    System.out.println((i + 1) + ". [" + notification.getType() + "] " + notification.getNotificationContent());
                    System.out.println("   📅 " + notification.getTimestamp());
                    System.out.println();
                }
            }
            
            if (!readNotifications.isEmpty()) {
                System.out.println("\n📭 Read Notifications (" + readNotifications.size() + "):");
                System.out.println("═".repeat(55));
                for (int i = 0; i < Math.min(5, readNotifications.size()); i++) {
                    Notification notification = readNotifications.get(i);
                    System.out.println((i + 1) + ". [" + notification.getType() + "] " + notification.getNotificationContent());
                    System.out.println("   📅 " + notification.getTimestamp());
                    System.out.println();
                }
                if (readNotifications.size() > 5) {
                    System.out.println("... and " + (readNotifications.size() - 5) + " more");
                }
            }
            
        } catch (UserNotFoundException e) {
            System.out.println("❌ Could not retrieve notifications.");
        }
    }

   
    
    private void displayBookResultsWithNumbers(List<Book> books) {
        System.out.println("═".repeat(100));
        System.out.printf("%-4s %-35s %-25s %-15s %-10s%n", "#", "Title", "Author", "ISBN", "Status");
        System.out.println("═".repeat(100));
        
        for (int i = 0; i < books.size(); i++) {
            Book book = books.get(i);
            String title = truncate(book.getTitle(), 33);
            String author = truncate(book.getAuthor(), 23);
            String isbn = truncate(book.getIsbn(), 13);
            String status = book.isAvailable() ? "✅ Available" : "❌ Unavailable";
            
            System.out.printf("%-4d %-35s %-25s %-15s %-10s%n", 
                (i + 1), title, author, isbn, status);
        }
        System.out.println("═".repeat(100));
    }

    private void displayCDResultsWithNumbers(List<CD> cds) {
        System.out.println("═".repeat(90));
        System.out.printf("%-4s %-40s %-30s %-10s%n", "#", "Title", "Artist", "Status");
        System.out.println("═".repeat(90));
        
        for (int i = 0; i < cds.size(); i++) {
            CD cd = cds.get(i);
            String title = truncate(cd.getTitle(), 38);
            String artist = truncate(cd.getArtist(), 28);
            String status = cd.isAvailable() ? "✅ Available" : "❌ Unavailable";
            
            System.out.printf("%-4d %-40s %-30s %-10s%n", 
                (i + 1), title, artist, status);
        }
        System.out.println("═".repeat(90));
    }

    private void displayJournalResultsWithNumbers(List<Journal> journals) {
        System.out.println("═".repeat(100));
        System.out.printf("%-4s %-45s %-35s %-10s%n", "#", "Title", "Author/Publisher", "Status");
        System.out.println("═".repeat(100));
        
        for (int i = 0; i < journals.size(); i++) {
            Journal journal = journals.get(i);
            String title = truncate(journal.getTitle(), 43);
            String author = truncate(journal.getAuthor(), 33);
            String status = journal.isAvailable() ? "✅ Available" : "❌ Unavailable";
            
            System.out.printf("%-4d %-45s %-35s %-10s%n", 
                (i + 1), title, author, status);
        }
        System.out.println("═".repeat(100));
    }

    private void displayLoansWithItemDetails(List<Loan> loans) {
        System.out.println("═".repeat(110));
        System.out.printf("%-4s %-35s %-15s %-15s %-15s %-15s%n", 
            "#", "Item", "Type", "Borrowed", "Due Date", "Status");
        System.out.println("═".repeat(110));
        
        for (int i = 0; i < loans.size(); i++) {
            Loan loan = loans.get(i);
            String itemDetails = truncate(getItemDetails(loan.getItemId(), loan.getItemType()), 33);
            String type = capitalizeFirst(loan.getItemType());
            String status;
            
            if (loan.isOverdue()) {
                long daysOverdue = loan.getDaysOverdue();
                status = "⚠️ OVERDUE (" + daysOverdue + "d)";
            } else {
                status = "✅ Active";
            }
            
            System.out.printf("%-4d %-35s %-15s %-15s %-15s %-15s%n", 
                (i + 1), itemDetails, type, loan.getBorrowDate(), loan.getDueDate(), status);
        }
        System.out.println("═".repeat(110));
    }

    
    
    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        return text.length() > maxLength ? text.substring(0, maxLength - 2) + ".." : text;
    }

    private String capitalizeFirst(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }
}
