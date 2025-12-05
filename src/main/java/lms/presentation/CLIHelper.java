package lms.presentation;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Reusable CLI helper utilities for clean, professional interface.
 * Can be used by UserCLI, AdminCLI, and other CLI classes.
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public final class CLIHelper {
    
    private static final String SEPARATOR = "=".repeat(100);
    private static final String EMPTY_STRING = "";
    
    private CLIHelper() {
        throw new IllegalStateException("Utility class");
    }
    
    public static void printHeader(String title) {
        CLILogger.info(SEPARATOR);
        CLILogger.info(title);
        CLILogger.info(SEPARATOR);
    }
    
    public static void printMessage(String message) {
        CLILogger.info(message);
    }
    
    public static void printError(String message) {
        CLILogger.warning("ERROR: " + message);
    }
    
    public static void printSuccess(String message) {
        CLILogger.info("SUCCESS: " + message);
    }
    
    public static void printWarning(String message) {
        CLILogger.warning("WARNING: " + message);
    }
    
    public static String truncate(String text, int maxLength) {
        if (text == null) {
            return EMPTY_STRING;
        }
        return text.length() > maxLength ? text.substring(0, maxLength - 2) + ".." : text;
    }
    
    public static String capitalizeFirst(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }
    
    public static boolean confirmAction(Scanner scanner, String prompt) {
        CLILogger.info(prompt + " (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        return "yes".equals(confirm) || "y".equals(confirm);
    }
    
    public static int getIntInput(Scanner scanner, String prompt, int min, int max) {
        CLILogger.info(prompt);
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice >= min && choice <= max) {
                return choice;
            }
            CLILogger.warning("Invalid selection. Must be between " + min + " and " + max);
        } catch (NumberFormatException e) {
            CLILogger.warning("Invalid number format");
        }
        return -1;
    }
    
    public static double getDoubleInput(Scanner scanner, String prompt) {
        CLILogger.info(prompt);
        try {
            return Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            CLILogger.warning("Invalid number format");
            return -1.0;
        }
    }
    
    public static <T> T findItemBySearchTerm(
            List<T> items,
            String searchTerm,
            String itemType,
            Function<T, String> titleGetter,
            Function<T, String> authorGetter,
            Function<T, UUID> idGetter,
            Consumer<List<T>> displayFunction,
            Scanner scanner) {
        
        String lowerSearch = searchTerm.toLowerCase();
        
        for (T item : items) {
            if (titleGetter.apply(item).equalsIgnoreCase(searchTerm) || 
                authorGetter.apply(item).equalsIgnoreCase(searchTerm) ||
                idGetter.apply(item).toString().equalsIgnoreCase(searchTerm)) {
                return item;
            }
        }
        
        List<T> matches = items.stream()
                .filter(i -> titleGetter.apply(i).toLowerCase().contains(lowerSearch) ||
                           authorGetter.apply(i).toLowerCase().contains(lowerSearch) ||
                           idGetter.apply(i).toString().toLowerCase().contains(lowerSearch))
                .collect(Collectors.toList());
        
        if (matches.isEmpty()) {
            return null;
        }
        
        if (matches.size() == 1) {
            return matches.get(0);
        }
        
        CLILogger.info("Multiple matches found:");
        displayFunction.accept(matches);
        int choice = getIntInput(scanner, "Enter the number of the " + itemType + " (1-" + matches.size() + "): ", 1, matches.size());
        
        if (choice >= 1 && choice <= matches.size()) {
            return matches.get(choice - 1);
        }
        
        return null;
    }
    
    public static <T> void displaySearchResults(List<T> results, String searchTerm, String itemType, 
                                                Consumer<List<T>> displayFunction) {
        if (results.isEmpty()) {
            CLILogger.info("No " + itemType + "s found matching '" + searchTerm + "'");
        } else {
            CLILogger.info("Found " + results.size() + " " + itemType + "(s) matching '" + searchTerm + "':");
            displayFunction.accept(results);
        }
    }
    
    public static <T> void borrowItemGeneric(
            List<T> availableItems,
            String itemType,
            String dueDays,
            Consumer<List<T>> displayFunction,
            BiFunction<List<T>, String, T> findFunction,
            Function<T, String> descriptionFunction,
            Function<T, UUID> idFunction,
            Scanner scanner,
            Consumer<UUID> loanFunction) {
        
        if (availableItems.isEmpty()) {
            CLILogger.info("Sorry, no " + itemType + "s are currently available for borrowing.");
            return;
        }
        
        CLILogger.info("Available " + capitalizeFirst(itemType) + "s:");
        displayFunction.accept(availableItems);
        
        CLILogger.info("Enter " + itemType + " title, author, or ID (partial match works): ");
        String searchTerm = scanner.nextLine().trim();
        
        if (searchTerm.isEmpty()) {
            printError("Search term cannot be empty");
            return;
        }
        
        T selectedItem = findFunction.apply(availableItems, searchTerm);
        
        if (selectedItem == null) {
            printError("No " + itemType + " found matching '" + searchTerm + "'");
            return;
        }
        
        CLILogger.info("You selected: " + descriptionFunction.apply(selectedItem));
        
        if (!confirmAction(scanner, "Confirm borrow?")) {
            CLILogger.info("Borrow cancelled.");
            return;
        }
        
        try {
            loanFunction.accept(idFunction.apply(selectedItem));
            printSuccess(capitalizeFirst(itemType) + " borrowed successfully! Due date: " + dueDays + " from today.");
        } catch (Exception e) {
            printError("Error borrowing " + itemType + ": " + e.getMessage());
        }
    }
}
