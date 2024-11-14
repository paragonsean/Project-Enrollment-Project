package edu.odu.cs.cs350;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public class Semester implements Iterable<Snapshot> {

    private static final Logger logger = Logger.getLogger(Semester.class.getName());
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private String name;
    private LocalDate preRegDate;
    private LocalDate addDeadline;
    private List<File> csvFiles;
    private Map<LocalDate, Snapshot> snapshots;

    // Constructor
    public Semester(String name, LocalDate preRegDate, LocalDate addDeadline, List<File> csvFiles) {
        FileLogging.configureFileLogging(logger);  // Set up file logging for this logger if needed
        this.name = name;
        this.preRegDate = preRegDate;
        this.addDeadline = addDeadline;
        this.csvFiles = csvFiles;
        this.snapshots = new HashMap<>();
    }

    public static Semester createSemester(String name, LocalDate preRegDate, LocalDate addDeadline, List<File> csvFiles) {
        Semester semester = new Semester(name, preRegDate, addDeadline, csvFiles);
        logger.log(Level.INFO, "Creating semester: {0}", name);
        semester.processCsvFilesToSnapshots();
        return semester;
    }

    protected void processCsvFilesToSnapshots() {
        logger.log(Level.INFO, "Starting CSV processing for semester: {0}", name);
        Pattern datePattern = Pattern.compile("(\\d{4}-\\d{2}-\\d{2})\\.csv");

        for (File csvFile : csvFiles) {
            LocalDate fileDate = extractDateFromFilename(csvFile.getName(), datePattern);

            if (fileDate == null || (preRegDate != null && fileDate.isBefore(preRegDate)) || 
                (addDeadline != null && fileDate.isAfter(addDeadline))) {
                logger.log(Level.INFO, "Skipping file outside date range: {0}", csvFile.getName());
                continue;
            }

            Snapshot snapshot = snapshots.computeIfAbsent(fileDate, date -> new Snapshot(date, new ArrayList<>()));
            try {
                addCsvToSnapshot(snapshot, csvFile);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error adding CSV to snapshot for file {0}: {1}", 
                           new Object[]{csvFile.getName(), e.getMessage()});
            }
        }
    }

    protected void addCsvToSnapshot(Snapshot snapshot, File csvFile) throws Exception {
        logger.log(Level.INFO, "Reading CSV file: {0}", csvFile.getName());

        try (FileReader reader = new FileReader(csvFile); CSVReader csvReader = new CSVReader(reader)) {
            List<String[]> rows = csvReader.readAll();
            if (rows.isEmpty()) {
                logger.log(Level.WARNING, "CSV file {0} is empty.", csvFile.getName());
                return;
            }
            String[] headers = rows.remove(0); // Remove the header row and store it

            for (String[] row : rows) {
                Course course = mapFieldsToCourse(row, headers, snapshot.getDate());
                if (course != null) {
                    snapshot.addCourse(course);
                    logger.log(Level.INFO, "Added course: {0}", course);
                }
            }
        } catch (IOException | CsvValidationException e) {
            logger.log(Level.SEVERE, "Error reading CSV file: {0} - {1}", new Object[]{csvFile.getName(), e.getMessage()});
        }
    }

    protected Course mapFieldsToCourse(String[] fields, String[] headers, LocalDate date) {
        try {
            int subjIndex = getColumnIndex(headers, "SUBJ");
            int crseIndex = getColumnIndex(headers, "CRSE");
            int crnIndex = getColumnIndex(headers, "CRN");
            int xlstGroupIndex = getColumnIndex(headers, "XLST GROUP");
            int sectionCapIndex = getColumnIndex(headers, "XLST CAP");
            int sectionEnrIndex = getColumnIndex(headers, "ENR");
            int overallCapIndex = getColumnIndex(headers, "OVERALL CAP");
            int overallEnrIndex = getColumnIndex(headers, "OVERALL ENR");
            int linkIndex = getColumnIndex(headers, "LINK");
            int instructorIndex = getColumnIndex(headers, "INSTRUCTOR");
    
            String subject = fields[subjIndex];
            String courseNumber = fields[crseIndex];
            String crn = fields[crnIndex];
            String xlstGroup = fields[xlstGroupIndex];
            int sectionCapacity = parseOrDefault(fields, sectionCapIndex, 0);
            int sectionEnrollment = parseOrDefault(fields, sectionEnrIndex, 0);
            int offeringCapacity = parseOrDefault(fields, overallCapIndex, 0);
            int offeringEnrollment = parseOrDefault(fields, overallEnrIndex, 0);
            String link = fields[linkIndex];
            String instructor = fields[instructorIndex];
            Course course = new Course(subject, courseNumber); 
            course.addOfferingsAndSections(crn, xlstGroup, sectionCapacity, sectionEnrollment, offeringCapacity, offeringEnrollment, link);
            return course;
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.log(Level.WARNING, "Error processing row: {0} - {1}", new Object[]{String.join(", ", fields), e.getMessage()});
            return null;
        }
    }
    
    protected int parseOrDefault(String[] fields, int index, int defaultValue) {
        try {
            return (index >= 0 && index < fields.length && !fields[index].isEmpty()) 
                    ? Integer.parseInt(fields[index]) 
                    : defaultValue;
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Failed to parse integer for field index {0}, defaulting to {1}", new Object[]{index, defaultValue});
            return defaultValue;
        }
    }
    
    protected int getColumnIndex(String[] headers, String columnName) {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].trim().equalsIgnoreCase(columnName)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Column '" + columnName + "' not found in headers.");
    }

    protected LocalDate extractDateFromFilename(String filename, Pattern datePattern) {
        Matcher matcher = datePattern.matcher(filename);
        if (matcher.find()) {
            try {
                return LocalDate.parse(matcher.group(1), DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                logger.log(Level.WARNING, "Error parsing date from filename {0}: {1}", new Object[]{filename, e.getMessage()});
            }
        } else {
            logger.log(Level.WARNING, "No date found in filename: {0}", filename);
        }
        return null;
    }

    public void addSnapshot(Snapshot snapshot) {
        snapshots.put(snapshot.getDate(), snapshot);
    }

    public Snapshot getSnapshotByDate(LocalDate date) {
        return snapshots.get(date);
    }

    public Collection<Snapshot> getSnapshots() {
        return snapshots.values();
    }

    public String getName() {
        return name;
    }

    public LocalDate getPreRegDate() {
        return preRegDate;
    }

    public LocalDate getAddDeadline() {
        return addDeadline;
    }

    public List<File> getCsvFiles() {
        return csvFiles;
    }

    @Override
    public Iterator<Snapshot> iterator() {
        return snapshots.values().iterator();
    }
}
