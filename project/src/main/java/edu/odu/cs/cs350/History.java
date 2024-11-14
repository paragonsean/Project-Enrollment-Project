package edu.odu.cs.cs350;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

public class History {

    private static final Logger logger = LoggerFactory.getLogger(History.class);
    private Map<String, Semester> semesters;  // Store semesters by semester name or code

    // Constructor
    public History() {
        this.semesters = new HashMap<>();
    }


    /**
     * Scans a list of directories, reads `dates.txt` to determine the date range, and creates
     * `Semester` instances for each directory using the factory method.
     *
     * @param directories List of directories representing semesters.
     * @param formatter   The DateTimeFormatter used for parsing dates.
     * @throws IOException if an I/O error occurs.
     */
    public void loadSemestersFromDirectories(List<File> directories, DateTimeFormatter formatter) throws IOException {
        for (File directory : directories) {
            if (!directory.exists() || !directory.isDirectory()) {
                logger.warn("Directory does not exist or is not a directory: {}", directory.getPath());
                continue;
            }

            try {
                // Use DateReader to load semester dates
                DateReader dateReader = new DateReader(directory.getAbsolutePath());
                LocalDate preRegDate = dateReader.getPreregistrationDate();
                LocalDate addDeadline = dateReader.getDeadlineDate();

                // Filter CSV files within the date range specified in `dates.txt`
                List<File> csvFiles = filterCsvFilesByDate(directory, preRegDate, addDeadline, formatter);
                if (csvFiles.size() < 2) {
                    throw new IOException("Not enough CSV files in directory: " + directory.getName());
                }

                // Use the factory method to create and initialize a Semester
                String semesterName = directory.getName();
                Semester semester = Semester.createSemester(semesterName, preRegDate, addDeadline, csvFiles);
                addSemester(semester);
            } catch (IOException e) {
                logger.error("Error processing directory {}: {}", directory.getPath(), e.getMessage());
            }
        }
    }

    // Add a semester to the history
    public void addSemester(Semester semester) {
        if (semesters.containsKey(semester.getName())) {
            logger.warn("Semester with name {} already exists. Overwriting.", semester.getName());
        }
        semesters.put(semester.getName(), semester);
    }

    // Retrieve a semester by its name or code
    public Semester getSemester(String semesterName) {
        return semesters.get(semesterName);
    }

    // Get all semester names
    public Set<String> getSemesterNames() {
        return semesters.keySet();
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
    protected List<File> filterCsvFilesByDate(File directory, LocalDate preRegDate, LocalDate addDeadline, DateTimeFormatter formatter) {
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

    /**
     * Collects CSV files by extracting dates from filenames across all semesters.
     *
     * @param formatter The DateTimeFormatter used to parse dates.
     * @return A ListMultimap where the key is the LocalDate and the value is the absolute path of the CSV file.
     * @throws IOException if an I/O error occurs.
     */
    public ListMultimap<LocalDate, String> collectCsvFilesByDate(DateTimeFormatter formatter) throws IOException {
        ListMultimap<LocalDate, String> csvFileDateMap = ArrayListMultimap.create();
        Pattern datePattern = Pattern.compile(".*(\\d{4}-\\d{2}-\\d{2})\\.csv$");

        // Iterate through each semester and collect CSV files by date
        for (Semester semester : semesters.values()) {
            for (File csvFile : semester.getCsvFiles()) {
                String fileName = csvFile.getName();
                try {
                    Matcher matcher = datePattern.matcher(fileName);
                    if (matcher.find()) {
                        String dateString = matcher.group(1);
                        LocalDate fileDate = LocalDate.parse(dateString, formatter);
                        csvFileDateMap.put(fileDate, csvFile.getAbsolutePath());
                    } else {
                        logger.warn("Filename does not match expected format: {}", fileName);
                    }
                } catch (DateTimeParseException ex) {
                    logger.error("Error parsing date from filename: {}. Date string: '{}'. Error message: {}", fileName, fileName, ex.getMessage());
                }
            }
        }

        return csvFileDateMap;
    }

    // Compare enrollments for a specific course across two semesters
    public void compareCourseEnrollments(String courseKey, String semester1Name, String semester2Name) {
        Semester semester1 = semesters.get(semester1Name);
        Semester semester2 = semesters.get(semester2Name);

        if (semester1 == null || semester2 == null) {
            logger.warn("One or both of the specified semesters not found.");
            return;
        }

        System.out.printf("Comparison of enrollments for course '%s' between %s and %s:\n", courseKey, semester1Name, semester2Name);

        for (Snapshot snapshot1 : semester1.getSnapshots()) {
            Course course1 = snapshot1.getCourse(courseKey);
            if (course1 == null) continue;

            for (Snapshot snapshot2 : semester2.getSnapshots()) {
                if (snapshot1.getDate().isEqual(snapshot2.getDate())) {
                    Course course2 = snapshot2.getCourse(courseKey);
                    if (course2 != null) {
                        int enrollmentDifference = course1.getTotalSectionEnrollment() - course2.getTotalSectionEnrollment();
                        System.out.printf("Date: %s | Enrollment Difference: %d\n", snapshot1.getDate(), enrollmentDifference);
                    }
                }
            }
        }
    }

    // Get total enrollment across all semesters for a specific date range
    public int getTotalEnrollmentAcrossSemesters(LocalDate startDate, LocalDate endDate) {
        int totalEnrollment = 0;
        for (Semester semester : semesters.values()) {
            for (Snapshot snapshot : semester.getSnapshots()) {
                if (!snapshot.getDate().isBefore(startDate) && !snapshot.getDate().isAfter(endDate)) {
                    totalEnrollment += snapshot.getTotalSectionEnrollment();
                }
            }
        }
        return totalEnrollment;
    }

    // Generate a summary report of enrollments by semester
    public void generateEnrollmentReport() {
        System.out.println("Enrollment Report by Semester:");
        for (Map.Entry<String, Semester> entry : semesters.entrySet()) {
            String semesterName = entry.getKey();
            Semester semester = entry.getValue();

            int totalEnrollment = semester.getSnapshots().stream()
                    .mapToInt(Snapshot::getTotalSectionEnrollment)
                    .sum();

            System.out.printf("Semester: %s | Total Enrollment: %d\n", semesterName, totalEnrollment);
        }
    }

    // Get enrollment history of a specific course across all semesters
    public Map<LocalDate, Integer> getCourseEnrollmentHistory(String courseKey) {
        Map<LocalDate, Integer> enrollmentHistory = new TreeMap<>();
        for (Semester semester : semesters.values()) {
            for (Snapshot snapshot : semester.getSnapshots()) {
                Course course = snapshot.getCourse(courseKey);
                if (course != null) {
                    enrollmentHistory.put(snapshot.getDate(), course.getTotalSectionEnrollment());
                }
            }
        }
        return enrollmentHistory;
    }

    // Compare total enrollments across two semesters by snapshot date
    public void compareSemesters(String semester1Name, String semester2Name) {
        Semester semester1 = semesters.get(semester1Name);
        Semester semester2 = semesters.get(semester2Name);

        if (semester1 == null || semester2 == null) {
            logger.warn("One or both of the specified semesters not found.");
            return;
        }

        System.out.printf("Comparison Report between %s and %s:\n", semester1Name, semester2Name);

        Set<LocalDate> allDates = new TreeSet<>();
        semester1.getSnapshots().forEach(snapshot -> allDates.add(snapshot.getDate()));
        semester2.getSnapshots().forEach(snapshot -> allDates.add(snapshot.getDate()));

        for (LocalDate date : allDates) {
            int semester1Enrollment = semester1.getSnapshots().stream()
                    .filter(snapshot -> snapshot.getDate().isEqual(date))
                    .mapToInt(Snapshot::getTotalSectionEnrollment)
                    .sum();

            int semester2Enrollment = semester2.getSnapshots().stream()
                    .filter(snapshot -> snapshot.getDate().isEqual(date))
                    .mapToInt(Snapshot::getTotalSectionEnrollment)
                    .sum();

            int difference = semester1Enrollment - semester2Enrollment;
            System.out.printf("Date: %s | %s Enrollment: %d | %s Enrollment: %d | Difference: %d\n",
                    date, semester1Name, semester1Enrollment, semester2Name, semester2Enrollment, difference);
        }
    }
}
