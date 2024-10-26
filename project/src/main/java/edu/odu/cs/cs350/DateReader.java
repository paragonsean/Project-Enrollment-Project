package edu.odu.cs.cs350;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//To be used for Parsing dates.txt for pre-registration and add deadline dates 
//and extracting dates from the current semester dir to determine if snapshot files are needed in the 
// next semester dir or previous semester dir

public class DateReader {
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Reads pre-registration and add deadline dates from dates.txt.
     *
     * @param semesterDir Path to the semester directory
     * @return Array of LocalDate [preRegistrationDate, addDeadlineDate]
     * @throws IOException if dates.txt is missing or improperly formatted
     */
    public LocalDate[] getRegistrationDates(Path semesterDir) throws IOException {
        Path datesFile = semesterDir.resolve("dates.txt");

        if (!Files.exists(datesFile)) {
            throw new IOException("Missing dates.txt in " + semesterDir.getFileName());
        }

        List<String> lines = Files.readAllLines(datesFile);

        if (lines.size() < 2) {
            throw new IOException("dates.txt must contain at least two dates in " + semesterDir);
        }

        try {
            LocalDate preRegistrationDate = LocalDate.parse(lines.get(0).trim(), dateFormatter);
            LocalDate addDeadlineDate = LocalDate.parse(lines.get(1).trim(), dateFormatter);
            return new LocalDate[]{preRegistrationDate, addDeadlineDate};
        } catch (DateTimeParseException e) {
            throw new IOException("Invalid date format in dates.txt for " + semesterDir, e);
        }
    }

    /**
     * Extracts the semester start and end dates from CSV filenames in a directory.
     *
     * @param semesterDir Path to the semester directory containing snapshot files
     * @return Array of LocalDate [startDate, endDate] representing the earliest and latest dates
     * @throws IOException if an I/O error occurs when reading directory contents
     */
    public LocalDate[] extractDatesFromCsvFiles(Path semesterDir) throws IOException {
        List<LocalDate> dates = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(semesterDir, "*.csv")) {
            for (Path file : stream) {
                LocalDate fileDate = extractDateFromCsvFileName(file);
                dates.add(fileDate);
            }
        }

        if (dates.isEmpty()) {
            throw new IOException("No CSV files found in directory " + semesterDir);
        }

        Collections.sort(dates);
        return new LocalDate[]{dates.get(0), dates.get(dates.size() - 1)};
    }

    /**
     * Extracts the date from a CSV filename in the format YYYY-MM-DD.csv.
     *
     * @param csvFile Path to the CSV file
     * @return LocalDate representing the date in the CSV file name
     */
    public LocalDate extractDateFromCsvFileName(Path csvFile) {
        String fileName = csvFile.getFileName().toString().replace(".csv", "");
        return LocalDate.parse(fileName, dateFormatter);
    }

    /**
     * Calculates the previous semester code for a given semester code.
     *
     * @param currentSemesterCode The current semester code in YYYYTT format
     * @return The previous semester code as a String
     */
    public String parsePreviousSemesterCode(String currentSemesterCode) {
        int year = parseYearFromSemesterCode(currentSemesterCode);
        int term = parseTermFromSemesterCode(currentSemesterCode);

        term -= 10;
        if (term < 10) { // Wrap to previous academic year
            term = 30;
            year--;
        }

        return String.format("%04d%02d", year, term);
    }

    /**
     * Calculates the next semester code for a given semester code.
     *
     * @param currentSemesterCode The current semester code in YYYYTT format
     * @return The next semester code as a String (YYYYTT format) representing the next academic year
     */
    public String parseNextSemesterCode(String currentSemesterCode) {
        int year = parseYearFromSemesterCode(currentSemesterCode);
        int term = parseTermFromSemesterCode(currentSemesterCode);

        term += 10;
        if (term > 30) { // Wrap to next academic year
            term = 10;
            year++;
        }

        return String.format("%04d%02d", year, term);
    }

    /**
     * Parses the year from a semester code.
     *
     * @param folderName The semester code in YYYYTT format
     * @return The year as an integer (YYYY) representing the academic year
     */
    public int parseYearFromSemesterCode(String folderName) {
        if (!folderName.matches("\\d{6}")) {
            throw new IllegalArgumentException("Invalid semester code format: " + folderName);
        }
        return Integer.parseInt(folderName.substring(0, 4));
    }

    /**
     * Parses the term from a semester code.
     *
     * @param folderName The semester code in YYYYTT format
     * @return The term as an integer (10, 20, or 30) representing Fall, Spring, or Summer
     */
    public int parseTermFromSemesterCode(String folderName) {
        if (!folderName.matches("\\d{6}")) {
            throw new IllegalArgumentException("Invalid semester code format: " + folderName);
        }
        return Integer.parseInt(folderName.substring(4));
    }
}
