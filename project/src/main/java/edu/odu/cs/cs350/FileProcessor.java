package edu.odu.cs.cs350;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileProcessor {

    private static final Logger logger = LoggerFactory.getLogger(FileProcessor.class);

    // Default DateTimeFormatter
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Scans a list of directories, reads `dates.txt` to determine the date range, and creates
     * `Semester` instances for each directory using the factory method.
     *
     * @param directories List of directories representing semesters.
     * @return A list of created `Semester` instances.
     * @throws IOException if an I/O error occurs.
     */
    public List<Semester> loadSemestersFromDirectories(List<File> directories) throws IOException {
        return loadSemestersFromDirectories(directories, DEFAULT_FORMATTER);
    }

    /**
     * Scans a list of directories, reads `dates.txt` to determine the date range, and creates
     * `Semester` instances for each directory using the factory method.
     *
     * @param directories List of directories representing semesters.
     * @param formatter   The DateTimeFormatter used for parsing dates.
     * @return A list of created `Semester` instances.
     * @throws IOException if an I/O error occurs.
     */
    public List<Semester> loadSemestersFromDirectories(List<File> directories, DateTimeFormatter formatter) throws IOException {
        List<Semester> semesters = new ArrayList<>();

        for (File directory : directories) {
            if (!directory.exists() || !directory.isDirectory()) {
                logger.warn("Directory does not exist or is not a directory: {}", directory.getPath());
                continue;
            }

            try {
                // Read the dates from the `dates.txt` file in the directory
                DateReader dateReader = new DateReader(directory.getAbsolutePath());
                LocalDate preRegDate = dateReader.getPreregistrationDate();
                LocalDate addDeadline = dateReader.getDeadlineDate();

                // Filter CSV files based on the date range
                List<File> csvFiles = filterCsvFilesByDate(directory, preRegDate, addDeadline, formatter);
                if (csvFiles.size() < 2) {
                    logger.warn("Not enough CSV files in directory: {}", directory.getName());
                    continue;
                }

                // Create the Semester instance
                String semesterName = directory.getName();
                Semester semester = Semester.createSemester(semesterName, preRegDate, addDeadline, csvFiles);
                semesters.add(semester);

                logger.info("Successfully created semester: {}", semesterName);
            } catch (RuntimeException e) {
                logger.error("Error processing directory {}: {}", directory.getPath(), e.getMessage());
            }
        }

        return semesters;
    }

    /**
     * Filters CSV files in a directory based on a specified date range.
     *
     * @param directory   The directory containing CSV files.
     * @param preRegDate  The start date from `dates.txt`.
     * @param addDeadline The end date from `dates.txt`.
     * @param formatter   The DateTimeFormatter for parsing dates in filenames.
     * @return A list of CSV files within the specified date range.
     */
    public List<File> filterCsvFilesByDate(File directory, LocalDate preRegDate, LocalDate addDeadline, DateTimeFormatter formatter) {
        List<File> filteredCsvFiles = new ArrayList<>();
        Pattern datePattern = Pattern.compile(".*(\\d{4}-\\d{2}-\\d{2})\\.csv$");

        for (File file : Objects.requireNonNull(directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv")))) {
            String fileName = file.getName();
            Matcher matcher = datePattern.matcher(fileName);
            if (matcher.find()) {
                try {
                    LocalDate fileDate = LocalDate.parse(matcher.group(1), formatter);
                    if ((preRegDate == null || !fileDate.isBefore(preRegDate)) && (addDeadline == null || !fileDate.isAfter(addDeadline))) {
                        filteredCsvFiles.add(file);
                    } else {
                        logger.info("Skipping file {} outside date range.", file.getName());
                    }
                } catch (DateTimeParseException ex) {
                    logger.error("Error parsing date from filename {}: {}", fileName, ex.getMessage());
                }
            } else {
                logger.warn("Filename does not match expected date format: {}", fileName);
            }
        }

        return filteredCsvFiles;
    }
}