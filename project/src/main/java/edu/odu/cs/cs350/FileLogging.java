package edu.odu.cs.cs350;


import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class FileLogging {
    private static final String LOG_FILE_NAME = "application_logs.log";

    // Static method to configure logging for a given logger
    public static void configureFileLogging(Logger logger) {
        try {
            // Check if the logger already has a FileHandler to avoid duplicate logging
            boolean hasFileHandler = false;
            for (Handler handler : logger.getHandlers()) {
                if (handler instanceof FileHandler) {
                    hasFileHandler = true;
                    break;
                }
            }

            if (!hasFileHandler) {
                // Set up FileHandler
                FileHandler fileHandler = new FileHandler(LOG_FILE_NAME, true); // Append to existing log file
                fileHandler.setFormatter(new SimpleFormatter()); // Use SimpleFormatter for plain text
                fileHandler.setLevel(Level.ALL); // Capture all log levels
                
                // Add the file handler to the logger
                logger.addHandler(fileHandler);
                logger.setLevel(Level.ALL); // Set logger level to capture all messages

                // Optional: Remove ConsoleHandler if you want only file logging
                for (Handler handler : logger.getHandlers()) {
                    if (handler.getClass().getName().contains("ConsoleHandler")) {
                        logger.removeHandler(handler);
                    }
                }

                logger.log(Level.INFO, "File logging configured successfully for logger: {0}", logger.getName());
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to set up file logging", e);
        }
    }
}
