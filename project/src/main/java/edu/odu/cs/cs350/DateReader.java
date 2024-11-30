package edu.odu.cs.cs350;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A utility class for reading and processing date-related information
 * for semesters
 */
public class DateReader {
    private static final Logger LOGGER = Logger.getLogger(DateReader.class.getName());
    private static final String ERROR_MISSING_DATES_FILE = "Missing dates.txt in semester directory: ";
    private static final String ERROR_INVALID_DATE_FORMAT = "Invalid date format: ";
    private static final String ERROR_EXPECTED_FORMAT = ". Expected format: yyyy-MM-dd";
    private static final String ERROR_INSUFFICIENT_DATES = "dates.txt must contain at least two valid dates in semester directory: ";
    private static final String ERROR_PREREGISTRATION_AFTER_ADD_DEADLINE = "Preregistration date must be before add deadline date.";
    private static final String ERROR_CURRENT_DATE_OUT_OF_BOUNDS = "Current date must be between preregistration date and add deadline date.";
    private static final String ERROR_SAME_PREREGISTRATION_ADD_DEADLINE = "Preregistration date and add deadline date cannot be the same.";
    private static final String INVALID_DATE_FORMAT_ERROR_MESSAGE = "Invalid date format in file name: ";
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String CSV_EXTENSION = ".csv";
    private LocalDate preregistrationDate;
    private LocalDate addDeadlineDate;
    private final Map<String, Double> cachedElapsedPercentages = new HashMap<>();
    private static final String dateFile = "dates.txt";
    private Path datesFile;
    private Path semesterPath;

    public DateReader(String semesterDir) {
        try {
            initialize(semesterDir);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize DateReader with directory: {0} - {1}",
                    new Object[] { semesterDir, e.getMessage() });
            throw new RuntimeException("Failed to initialize DateReader: " + e.getMessage(), e);
        }
    }

    private void initialize(String semesterDir) throws IOException {
        // Initialize semesterPath and datesFile correctly
        this.semesterPath = Paths.get(semesterDir);
        this.datesFile = semesterPath.resolve(dateFile);
        this.loadDates(); // IOException will propagate if loading fails
    }

    /**
     * Retrieves the add deadline date from a specified directory containing a dates
     * file.
     *
     * @return the add deadline date as a LocalDate from the dates file
     */
    public LocalDate getDeadlineDate() {
        return this.addDeadlineDate; // Adding 1 day to the original addDeadlineDate
    }

    /**
     * Retrieves the preregistration date from a specified directory containing a
     * dates file.
     *
     * @return the preregistration date as a LocalDate from the dates file
     */
    public LocalDate getPreregistrationDate() {
        return this.preregistrationDate;
    }

    /**
     * Converts a date string to a LocalDate object.
     *
     * @param dateString the date string in "yyyy-MM-dd" format
     * @return the corresponding LocalDate object
     * @throws IllegalArgumentException if the date string is not in the expected
     *                                  format
     */
    public static LocalDate convertStringToDate(String dateString) {
        try {
            return LocalDate.parse(dateString, dateFormat);
        } catch (DateTimeParseException e) {
            LOGGER.log(Level.WARNING, "Failed to parse date: {0} - {1}", new Object[] { dateString, e.getMessage() });
            throw new IllegalArgumentException(ERROR_INVALID_DATE_FORMAT + dateString + ERROR_EXPECTED_FORMAT, e);
        }
    }

    /**
     * Validates if a date string is in the correct format.
     *
     * @param dateString the date string to validate
     * @return true if the date string is in the valid format, false otherwise
     */
    private boolean isValidDate(String dateString) {
        try {
            LocalDate.parse(dateString, dateFormat);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Extracts a date from a CSV filename.
     *
     * @param fileName String representing the CSV file name
     * @return LocalDate representing the date in the CSV file name
     */
    public static LocalDate extractDateFromFileName(String fileName) {
        String datePart = fileName.replace(CSV_EXTENSION, "");
        try {
            return LocalDate.parse(datePart, dateFormat);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(INVALID_DATE_FORMAT_ERROR_MESSAGE + fileName, e);
        }
    }

    /**
     * Loads dates from the dates.txt file and sets the preregistration and add
     * deadline dates.
     *
     * @throws IOException if the dates.txt file is missing or doesn't have enough
     *                     dates throws an IOException if the dates.txt plus
     *                     semester code
     */
    public void loadDates() throws IOException {
        List<LocalDate> dates = new ArrayList<>();
        if (!Files.exists(this.datesFile)) {
            throw new IOException(ERROR_MISSING_DATES_FILE + semesterPath.getFileName());
        }

        try (BufferedReader reader = Files.newBufferedReader(this.datesFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (isValidDate(line)) {
                    LocalDate parsedDate = LocalDate.parse(line, dateFormat);
                    dates.add(parsedDate);
                } else {
                    LOGGER.log(Level.WARNING, "Skipping invalid date format in dates.txt: {0}", new Object[] { line });
                }
            }
        }

        if (dates.size() < 2) {
            throw new IOException(ERROR_INSUFFICIENT_DATES + semesterPath.getFileName());
        }

        // Set preregistration and add deadline dates
        this.preregistrationDate = dates.get(0);
        this.addDeadlineDate = dates.get(1);
    }

}