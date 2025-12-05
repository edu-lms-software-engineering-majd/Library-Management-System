package lms.presentation;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Logger utility for CLI operations.
 * Provides centralized logging for all CLI classes.
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
    
    public static void info(String message) {
        LOGGER.info(message);
    }
    
    public static void warning(String message) {
        LOGGER.warning(message);
    }
    
    public static void error(String message) {
        LOGGER.severe(message);
    }
    
    public static void error(String message, Exception e) {
        LOGGER.log(Level.SEVERE, message, e);
    }
}
