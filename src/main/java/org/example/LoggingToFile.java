package org.example;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggingToFile {
    public static Logger LOGGERFILE = Logger.getLogger(LoggingToFile.class.getName());

    public static void setupLogger() {
        try {
            FileHandler fileHandler = new FileHandler("src/main/java/org/example/all_logs.txt", false);

            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);

            // Add FileHandler to Logger
            LOGGERFILE.addHandler(fileHandler);

            // turn off logs to console
            LOGGERFILE.setUseParentHandlers(false);

        } catch (Exception e) {
            LOGGERFILE.warning("Failed to initialize logger handler.");
        }
    }
}
