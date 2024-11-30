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

    public List<File> convertStringToFiles(String directory) {
        List<File> files = new ArrayList<>();
        File dir = new File(directory);
        if (dir.exists() && dir.isDirectory()) {
            File[] fileArray = dir.listFiles();
            if (fileArray != null) {
                files.addAll(Arrays.asList(fileArray));
            }
        }
        return files;
    }

    public List<Semester> loadSemestersFromDirectories(String directory) throws IOException {
        return loadSemestersFromDirectories(convertStringToFiles(directory));
    }

    public List<Semester> loadSemestersFromDirectories(List<File> directories) throws IOException {
        return loadSemestersFromDirectoriesWithCutoff(directories, Optional.empty());
    }

    public List<Semester> loadSemestersFromDirectoriesWithCutoff(List<File> directories, Optional<LocalDate> cutoffDate) throws IOException {
        List<Semester> semesters = new ArrayList<>();

        for (File directory : directories) {
            if (!directory.exists() || !directory.isDirectory()) {
                logger.warn("Directory does not exist or is not a directory: {}", directory.getPath());
                continue;
            }

            try {
                DateReader dateReader = new DateReader(directory.getAbsolutePath());
                LocalDate preRegDate = dateReader.getPreregistrationDate();
                LocalDate addDeadline = dateReader.getDeadlineDate();

                List<File> csvFiles = filterCsvFilesByDate(directory, preRegDate, addDeadline, cutoffDate);
                if (csvFiles.isEmpty()) {
                    logger.warn("No valid CSV files in directory: {}", directory.getName());
                    continue;
                }

                String semesterName = directory.getName();
                Semester semester = Semester.createSemester(semesterName, preRegDate, addDeadline, csvFiles, directory.getAbsolutePath());
                semesters.add(semester);

                logger.info("Successfully created semester: {}", semesterName);
            } catch (RuntimeException e) {
                logger.error("Error processing directory {}: {}", directory.getPath(), e.getMessage());
            }
        }

        return semesters;
    }

    public Semester processDirectory(File directory, Optional<LocalDate> cutoffDate) throws IOException {
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException("Invalid directory: " + directory.getAbsolutePath());
        }

        DateReader dateReader = new DateReader(directory.getAbsolutePath());
        LocalDate preRegDate = dateReader.getPreregistrationDate();
        LocalDate addDeadline = dateReader.getDeadlineDate();

        List<File> csvFiles = filterCsvFilesByDate(directory, preRegDate, addDeadline, cutoffDate);
        if (csvFiles.isEmpty()) {
            throw new IOException("No valid CSV files in directory: " + directory.getName());
        }

        String semesterName = directory.getName();
        return Semester.createSemester(semesterName, preRegDate, addDeadline, csvFiles, directory.getAbsolutePath());
    }

    public List<File> filterCsvFilesByDate(File directory, LocalDate preRegDate, LocalDate addDeadline, Optional<LocalDate> cutoffDate) {
        List<File> filteredCsvFiles = new ArrayList<>();
        Pattern datePattern = Pattern.compile(".*(\\d{4}-\\d{2}-\\d{2})\\.csv$");
    
        // List all files in the directory
        File[] files = Objects.requireNonNull(directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv")));
        int totalFiles = files.length;  // Total number of files to process
    
        // Loop over each file and process it
        for (int i = 0; i < totalFiles; i++) {
            File file = files[i];
            String fileName = file.getName();
            Matcher matcher = datePattern.matcher(fileName);
    
            // Log progress: x out of y files
            logger.info("Processing file {}/{}: {}", (i + 1), totalFiles, fileName);
    
            if (matcher.find()) {
                try {
                    LocalDate fileDate = LocalDate.parse(matcher.group(1), DEFAULT_FORMATTER);
                    boolean withinDateRange = (preRegDate == null || !fileDate.isBefore(preRegDate)) &&
                            (addDeadline == null || !fileDate.isAfter(addDeadline));
                    boolean withinCutoff = cutoffDate.map(date -> !fileDate.isAfter(date)).orElse(true);
    
                    // Check if the file is within the valid date range and cutoff
                    if (withinDateRange && withinCutoff) {
                        filteredCsvFiles.add(file);
                    } else {
                        logger.info("Skipping file {} outside date range or cutoff date.", file.getName());
                    }
                } catch (DateTimeParseException ex) {
                    logger.error("Error parsing date from filename {}: {}", fileName, ex.getMessage());
                }
            } else {
                logger.warn("Filename does not match expected date format: {}", fileName);
            }
        }
    
        logger.info("Completed processing {} out of {} files.", filteredCsvFiles.size(), totalFiles);
        return filteredCsvFiles;
    }
    


    public List<Semester> loadSemestersFromDirectories(List<String> historicDirs, Optional<LocalDate> cutoffDate) throws IOException {
        List<File> directories = new ArrayList<>();
        for (String dir : historicDirs) {
            File directory = new File(dir);
            if (directory.exists() && directory.isDirectory()) {
                directories.add(directory);
            } else {
                logger.warn("Invalid directory path: {}", dir);
            }
        }

        return loadSemestersFromDirectoriesWithCutoff(directories, cutoffDate);
    }
}
