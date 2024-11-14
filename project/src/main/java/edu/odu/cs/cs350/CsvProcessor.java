package edu.odu.cs.cs350;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public class CsvProcessor {

    private static final Logger logger = Logger.getLogger(CsvProcessor.class.getName());
    private static final String SUBJ_COLUMN = "SUBJ";
    private static final String CRSE_COLUMN = "CRSE";
    private static final String CRN_COLUMN = "CRN";
    private static final String XLST_GROUP_COLUMN = "XLST GROUP";
    private static final String SECTION_CAP_COLUMN = "XLST CAP";
    private static final String SECTION_ENR_COLUMN = "ENR";
    private static final String OVERALL_CAP_COLUMN = "OVERALL CAP";
    private static final String OVERALL_ENR_COLUMN = "OVERALL ENR";
    private static final String LINK_COLUMN = "LINK";
    private static final String INSTRUCTOR_COLUMN = "INSTRUCTOR";

    public static void processCsvFilesToSnapshots(Semester semester) {
        logger.log(Level.INFO, "Starting CSV processing for semester: {0}", semester.getName());
        for (File csvFile : semester.getCsvFiles()) {
            processCsvFile(semester, csvFile);
        }
    }

    protected static void addCsvToSnapshot(Snapshot snapshot, File csvFile) throws Exception {
        logger.log(Level.INFO, "Reading CSV file: {0}", csvFile.getName());

        try (FileReader reader = new FileReader(csvFile); CSVReader csvReader = new CSVReader(reader)) {
            List<String[]> rows = csvReader.readAll();
            if (rows.isEmpty()) {
                logger.log(Level.WARNING, "CSV file {0} is empty.", csvFile.getName());
                return;
            }
            String[] headers = rows.remove(0); // Remove the header row and store it
            Map<String, Integer> headerIndexMap = createHeaderIndexMap(headers);

            for (String[] row : rows) {
                Course course = mapFieldsToCourse(row, headerIndexMap, snapshot.getDate());
                if (course != null) {
                    snapshot.addCourse(course);
                    logger.log(Level.INFO, "Added course: {0}", course);
                }
            }
        } catch (IOException | CsvValidationException e) {
            logger.log(Level.SEVERE, "Error reading CSV file: {0} - {1}", new Object[]{csvFile.getName(), e.getMessage()});
        }
    }

    protected static Course mapFieldsToCourse(String[] fields, Map<String, Integer> headerIndexMap, LocalDate date) {
        try {
            String subject = fields[getHeaderIndex(headerIndexMap, SUBJ_COLUMN)];
            String courseNumber = fields[getHeaderIndex(headerIndexMap, CRSE_COLUMN)];
            String crn = fields[getHeaderIndex(headerIndexMap, CRN_COLUMN)];
            String xlstGroup = getFieldValue(fields, headerIndexMap, XLST_GROUP_COLUMN, "");
            int sectionCapacity = parseOrDefault(fields, getHeaderIndex(headerIndexMap, SECTION_CAP_COLUMN), 0);
            int sectionEnrollment = parseOrDefault(fields, getHeaderIndex(headerIndexMap, SECTION_ENR_COLUMN), 0);
            int offeringCapacity = parseOrDefault(fields, getHeaderIndex(headerIndexMap, OVERALL_CAP_COLUMN), 0);
            int offeringEnrollment = parseOrDefault(fields, getHeaderIndex(headerIndexMap, OVERALL_ENR_COLUMN), 0);
            String link = getFieldValue(fields, headerIndexMap, LINK_COLUMN, "");
            String instructor = getFieldValue(fields, headerIndexMap, INSTRUCTOR_COLUMN, "");
            
            Course course = new Course(subject, courseNumber);
            course.addOfferingsAndSections(crn, xlstGroup, sectionCapacity, sectionEnrollment, offeringCapacity, offeringEnrollment, link);
            return course;
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.log(Level.WARNING, "Error processing row: {0} - {1}", new Object[]{String.join(", ", fields), e.getMessage()});
            return null;
        }
    }

    protected static int parseOrDefault(String[] fields, int index, int defaultValue) {
        try {
            return (index >= 0 && index < fields.length && !fields[index].isEmpty())
                    ? Integer.parseInt(fields[index])
                    : defaultValue;
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Failed to parse integer for field index {0}, defaulting to {1}", new Object[]{index, defaultValue});
            return defaultValue;
        }
    }

    protected static void processCsvFile(Semester semester, File csvFile) {
        LocalDate fileDate;
        try {
            fileDate = DateReader.extractDateFromFileName(csvFile.getName());
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Skipping file with invalid date format: {0}", csvFile.getName());
            return;
        }

        if (!isFileWithinDateRange(fileDate, semester)) {
            logger.log(Level.INFO, "Skipping file outside date range: {0}", csvFile.getName());
            return;
        }

        Snapshot snapshot = getOrCreateSnapshot(semester, fileDate);
        try {
            addCsvToSnapshot(snapshot, csvFile);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error adding CSV to snapshot for file {0}: {1}",
                    new Object[]{csvFile.getName(), e.getMessage()});
        }
    }

    protected static boolean isFileWithinDateRange(LocalDate fileDate, Semester semester) {
        return !(semester.getPreRegDate() != null && fileDate.isBefore(semester.getPreRegDate())) &&
                !(semester.getAddDeadline() != null && fileDate.isAfter(semester.getAddDeadline()));
    }

    protected static Snapshot getOrCreateSnapshot(Semester semester, LocalDate fileDate) {
        Snapshot snapshot = semester.getSnapshotByDate(fileDate);
        if (snapshot == null) {
            snapshot = new Snapshot(fileDate, new ArrayList<>());
            semester.addSnapshot(snapshot);
        }
        return snapshot;
    }

    protected static Map<String, Integer> createHeaderIndexMap(String[] headers) {
        Map<String, Integer> headerIndexMap = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            headerIndexMap.put(headers[i].trim().toUpperCase(), i);
        }
        return headerIndexMap;
    }

    protected static String getFieldValue(String[] fields, Map<String, Integer> headerIndexMap, String columnName, String defaultValue) {
        int index = getHeaderIndex(headerIndexMap, columnName);
        return (index >= 0 && index < fields.length) ? fields[index] : defaultValue;
    }

    protected  static int getHeaderIndex(Map<String, Integer> headerIndexMap, String columnName) {
        return headerIndexMap.getOrDefault(columnName.toUpperCase(), -1);
    }
}
