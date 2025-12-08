package lms.presentation;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utility class providing reusable CLI helper methods for formatting and user interaction.
 * 
 * <p>
 * Contains static methods for printing headers, messages, handling user input,
 * and displaying search results. Used by {@link UserCLI}, {@link AdminCLI}, and
 * other CLI classes to maintain consistent formatting.
 * </p>
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
    
    /**
     * Prints a formatted header with the given title.
     * 
     * @param title the header title to display
     */
    public static void printHeader(String title) {
        CLILogger.info(SEPARATOR);
        CLILogger.info(title);
        CLILogger.info(SEPARATOR);
    }
    
    /**
     * Prints an informational message.
     * 
     * @param message the message to display
     */
    public static void printMessage(String message) {
        CLILogger.info(message);
    }
    
    /**
     * Prints an error message with ERROR prefix.
     * 
     * @param message the error message to display
     */
    public static void printError(String message) {
        CLILogger.warning("ERROR: " + message);
    }
    
    /**
     * Prints a success message with SUCCESS prefix.
     * 
     * @param message the success message to display
     */
    public static void printSuccess(String message) {
        CLILogger.info("SUCCESS: " + message);
    }
    
    /**
     * Prints a warning message with WARNING prefix.
     * 
     * @param message the warning message to display
     */
    public static void printWarning(String message) {
        CLILogger.warning("WARNING: " + message);
    }
    
    /**
     * Truncates text to the specified maximum length, appending ".." if truncated.
     * 
     * @param text the text to truncate
     * @param maxLength the maximum length allowed
     * @return the truncated text, or original if shorter than maxLength
     */
    public static String truncate(String text, int maxLength) {
        if (text == null) {
            return EMPTY_STRING;
        }
        return text.length() > maxLength ? text.substring(0, maxLength - 2) + ".." : text;
    }
    
    /**
     * Capitalizes the first letter of text and lowercases the rest.
     * 
     * @param text the text to capitalize
     * @return the capitalized text
     */
    public static String capitalizeFirst(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }
    
    /**
     * Prompts the user for yes/no confirmation.
     * 
     * @param scanner the scanner for reading user input
     * @param prompt the confirmation prompt to display
     * @return true if user confirms with "yes" or "y", false otherwise
     */
    public static boolean confirmAction(Scanner scanner, String prompt) {
        CLILogger.info(prompt + " (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        return "yes".equals(confirm) || "y".equals(confirm);
    }
    
    /**
     * Gets integer input from the user within the specified range.
     * 
     * @param scanner the scanner for reading user input
     * @param prompt the prompt message to display
     * @param min the minimum acceptable value
     * @param max the maximum acceptable value
     * @return the validated integer input, or -1 if invalid
     */
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
    
    /**
     * Gets double input from the user.
     * 
     * @param scanner the scanner for reading user input
     * @param prompt the prompt message to display
     * @return the parsed double value, or -1.0 if invalid
     */
    public static double getDoubleInput(Scanner scanner, String prompt) {
        CLILogger.info(prompt);
        try {
            return Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            CLILogger.warning("Invalid number format");
            return -1.0;
        }
    }
    
    /**
     * Finds an item from a list by searching through title, author, or ID.
     * Handles exact matches, partial matches, and multiple match selection.
     * 
     * @param <T> the type of items to search
     * @param items the list of items to search through
     * @param searchTerm the term to search for
     * @param itemType the type name for display purposes
     * @param titleGetter function to extract title from item
     * @param authorGetter function to extract author from item
     * @param idGetter function to extract ID from item
     * @param displayFunction function to display list of items
     * @param scanner the scanner for reading user input
     * @return the found item, or null if not found or cancelled
     */
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
    
    /**
     * Displays search results with appropriate messaging.
     * 
     * @param <T> the type of items in the results
     * @param results the list of search results
     * @param searchTerm the original search term
     * @param itemType the type name for display purposes
     * @param displayFunction function to display the results
     */
    public static <T> void displaySearchResults(List<T> results, String searchTerm, String itemType, 
                                                Consumer<List<T>> displayFunction) {
        if (results.isEmpty()) {
            CLILogger.info("No " + itemType + "s found matching '" + searchTerm + "'");
        } else {
            CLILogger.info("Found " + results.size() + " " + itemType + "(s) matching '" + searchTerm + "':");
            displayFunction.accept(results);
        }
    }
    
    /**
     * Generic method for handling the item borrowing workflow.
     * Displays available items, searches for selection, confirms, and processes the loan.
     * 
     * @param <T> the type of item being borrowed
     * @param availableItems the list of available items
     * @param itemType the type name for display purposes
     * @param dueDays the due date description
     * @param displayFunction function to display items
     * @param findFunction function to find item by search term
     * @param descriptionFunction function to get item description
     * @param idFunction function to extract item ID
     * @param scanner the scanner for reading user input
     * @param loanFunction function to process the loan
     */
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
