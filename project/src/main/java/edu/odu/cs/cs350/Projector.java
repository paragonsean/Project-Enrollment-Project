package edu.odu.cs.cs350;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

public class Projector {

    private static final Logger logger = Logger.getLogger(Projector.class.getName());
    private final SummaryProjectionReport summaryReport;
    private final Map<String, ProjectedCourse> projections;
    private final Set<String> currentSemesterCourses;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DetailedReportGenerator detailedReportGenerator = new DetailedReportGenerator();  
    public Projector() {
        this.summaryReport = new SummaryProjectionReport();
        this.projections = new TreeMap<>();
        this.currentSemesterCourses = new HashSet<>();
    }

    // Process CSV files for a semester
    public void processCsvFilesToSnapshots(Semester semester) {
        logger.log(Level.INFO, "Processing CSV files for semester: {0}", semester.getName());
        semester.getCsvFiles().forEach(csvFile -> {
            LocalDate fileDate = extractDate(csvFile.getName());
            if (fileDate == null || !isFileWithinDateRange(fileDate, semester)) return;

            Snapshot snapshot = semester.getSnapshotByDate(fileDate);
            if (snapshot == null) {
                snapshot = new Snapshot(fileDate, new ArrayList<>());
                semester.addSnapshot(snapshot);
            }
            readCsvAndAddToSnapshot(csvFile, snapshot);
        });
    }

    private void readCsvAndAddToSnapshot(File csvFile, Snapshot snapshot) {
        try (CSVReader csvReader = new CSVReader(new FileReader(csvFile))) {
            List<String[]> rows = csvReader.readAll();
            if (rows.isEmpty()) {
                logger.log(Level.WARNING, "Empty CSV file: {0}", csvFile.getName());
                return;
            }
            Map<String, Integer> headerIndexMap = createHeaderIndexMap(rows.remove(0));
            rows.stream()
                .map(row -> createCourseFromRow(row, headerIndexMap))
                .filter(Objects::nonNull)
                .forEach(snapshot::addCourse);
        } catch (IOException | CsvException e) {
            logger.log(Level.SEVERE, "Error reading CSV file: {0}", csvFile.getName());
        }
    }

    private Map<String, Integer> createHeaderIndexMap(String[] headers) {
        return Arrays.stream(headers)
                .collect(HashMap::new, (map, header) -> map.put(header.trim().toUpperCase(), map.size()), Map::putAll);
    }
    private Course createCourseFromRow(String[] row, Map<String, Integer> headerIndexMap) {
        try {
            // Create the course
            Course course = new Course(
                getValue(row, headerIndexMap, "SUBJ", ""),  // Alternate default is an empty string
                getValue(row, headerIndexMap, "CRSE", "")  // Alternate default is an empty string
            );
    
            // Add offerings and sections
            course.addOfferingsAndSections(
                getValue(row, headerIndexMap, "CRN", ""),                   // Default empty string
                getValue(row, headerIndexMap, "XLST GROUP", ""),            // Default empty string
                Integer.parseInt(getValue(row, headerIndexMap, "XLST CAP", "0")), // Default "0"
                Integer.parseInt(getValue(row, headerIndexMap, "ENR", "0")),      // Default "0"
                Integer.parseInt(getValue(row, headerIndexMap, "OVERALL CAP", "0")), // Default "0"
                Integer.parseInt(getValue(row, headerIndexMap, "OVERALL ENR", "0")), // Default "0"
                getValue(row, headerIndexMap, "LINK", "")                   // Default empty string
            );
    
            return course;
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error mapping row: {0}", Arrays.toString(row));
            return null;
        }
    }

    private String getValue(String[] row, Map<String, Integer> headerIndexMap, String column, String alternateDefault) {
        int index = headerIndexMap.getOrDefault(column, -1);
        String value = (index >= 0 && index < row.length) ? row[index] : null;
        return (value == null || value.trim().isEmpty()) ? alternateDefault : value;
    }

    private boolean isFileWithinDateRange(LocalDate date, Semester semester) {
        return (semester.getPreRegDate() == null || !date.isBefore(semester.getPreRegDate())) &&
               (semester.getAddDeadline() == null || !date.isAfter(semester.getAddDeadline()));
    }

    private LocalDate extractDate(String fileName) {
        try {
            return DateReader.extractDateFromFileName(fileName);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Invalid date in file name: {0}", fileName);
            return null;
        }
    }

    // Add enrollment data to projections
    private void addEnrollment(double normalizedDate, String courseName, int enrollment, int capacity, boolean isCurrent) {
        ProjectedCourse projectedCourse = projections.computeIfAbsent(courseName, ProjectedCourse::new);
        projectedCourse.setCourseCapacity(capacity);
        if (isCurrent) {
            projectedCourse.addCurrentEnrollment(normalizedDate, enrollment);
            currentSemesterCourses.add(courseName);
        } else {
            projectedCourse.addHistoricalEnrollment(normalizedDate, enrollment);
        }
        logger.info((isCurrent ? "Current" : "Historical") + " enrollment added for course: " + courseName);
    }

    // Process semester data
    private void processSemesterData(Semester semester, boolean isCurrent) {
        for (Snapshot snapshots : semester) {
            double normalizedDate = semester.normalizeDate(snapshots.getDate());
            for (Course course : snapshots) {
                int enrollment = course.getTotalEnrollment();
                int capacity = Math.max(course.getTotalOfferingCapacity(), course.getTotalSectionCapacity());
                addEnrollment(normalizedDate, course.getCourseKey(), enrollment, capacity, isCurrent);
            }
        }
    }

    // Generate projections
    private void generateProjections() {
        currentSemesterCourses.forEach(courseName -> {
            ProjectedCourse course = projections.get(courseName);
            if (course != null) {
                course.generateProjectionsForQuarters();
                summaryReport.addCourse(course);
            }
        });
    }

    // Main method
    public static void main(String[] args) {
        try {
            String basePath = "/Users/spocam/Documents/GitHub/350-F24-TA2/SemesterData/";
            Semester currentSem = new Semester(basePath + "202420");
            List<Semester> historicSems = Arrays.asList(
                new Semester(basePath + "202320"),
                new Semester(basePath + "202310"),
                new Semester(basePath + "202330"),
                new Semester(basePath + "202410")
            );

            Projector processor = new Projector();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            // Process data
            processor.processCsvFilesToSnapshots(currentSem);
            historicSems.forEach(sem -> processor.processCsvFilesToSnapshots(sem));
            historicSems.forEach(sem -> processor.processSemesterData(sem, false));
            processor.processSemesterData(currentSem, true);

            // Generate projections and display results
            processor.generateProjections();

            DetailedReportGenerator.generateReport(processor.projections, "detailed.xlsx");
            processor.summaryReport.displayProjectionResults(
                currentSem.getPreRegDate().format(dtf),
                currentSem.getAddDeadline().format(dtf),
                LocalDate.now().format(dtf)
            );
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An error occurred", e);
        }
        
    }
}