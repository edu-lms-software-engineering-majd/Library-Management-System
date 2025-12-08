package lms.presentation;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Centralized logger utility for CLI operations.
 * 
 * <p>
 * Provides static methods for logging informational, warning, and error messages
 * throughout the presentation layer. Uses Java's logging framework configured
 * for console output.
 * </p>
 * 
 * @author Majd Awwad
 * @version 1.0
 */
public final class CLILogger {
    
    private static final Logger LOGGER = Logger.getLogger("LMS-CLI");
    
    static {
        LOGGER.setLevel(Level.INFO);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter());
        LOGGER.addHandler(handler);
        LOGGER.setUseParentHandlers(false);
    }
    
    private CLILogger() {
        throw new IllegalStateException("Utility class");
    }
    
    /**
     * Logs an informational message.
     * 
     * @param message the message to log
     */
    public static void info(String message) {
        LOGGER.info(message);
    }
    
    /**
     * Logs a warning message.
     * 
     * @param message the warning message to log
     */
    public static void warning(String message) {
        LOGGER.warning(message);
    }
    
    /**
     * Logs an error message.
     * 
     * @param message the error message to log
     */
    public static void error(String message) {
        LOGGER.severe(message);
    }
    
    /**
     * Logs an error message with exception details.
     * 
     * @param message the error message to log
     * @param e the exception that caused the error
     */
    public static void error(String message, Exception e) {
        LOGGER.log(Level.SEVERE, message, e);
    }
}
