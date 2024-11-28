package edu.odu.cs.cs350;

import java.io.File;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Semester implements Iterable<Snapshot> {

    private static final Logger logger = Logger.getLogger(Semester.class.getName());
    private String name;
    private LocalDate preRegDate;
    private LocalDate addDeadline;
    private List<File> csvFiles;
    private Map<LocalDate, Snapshot> snapshots;

    // Constructor
    public Semester(String name, LocalDate preRegDate, LocalDate addDeadline, List<File> csvFiles) {
        this.name = name;
        this.preRegDate = preRegDate;
        this.addDeadline = addDeadline;
        this.csvFiles = csvFiles;
        this.snapshots = new HashMap<>();
    }

    public static Semester createSemester(String name, LocalDate preRegDate, LocalDate addDeadline, List<File> csvFiles) {
        Semester semester = new Semester(name, preRegDate, addDeadline, csvFiles);
        logger.log(Level.INFO, "Creating semester: {0}", name);
        CsvProcessor.processCsvFilesToSnapshots(semester);
        return semester;
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

    /**
     * Get the enrollment history of a specific course in this semester.
     *
     * @param courseKey The key representing the course (e.g., course ID or name).
     * @return A map where the key is the normalized date (double between 0 and 1),
     * and the value is the total enrollment for that course on that date.
     */
    public Map<Double, Integer> getCourseEnrollmentHistory(String courseKey) {
        Map<Double, Integer> enrollmentHistory = new TreeMap<>();
        long totalDays = ChronoUnit.DAYS.between(preRegDate, addDeadline);

        for (Snapshot snapshot : snapshots.values()) {
            LocalDate snapshotDate = snapshot.getDate();
            if (!snapshotDate.isBefore(preRegDate) && !snapshotDate.isAfter(addDeadline)) {
                Course course = snapshot.getCourse(courseKey);
                if (course != null) {
                    // Normalize the date to a range of 0-1
                    long daysFromStart = ChronoUnit.DAYS.between(preRegDate, snapshotDate);
                    double normalizedDate = (double) daysFromStart / totalDays;

                    // Add to the map
                    enrollmentHistory.put(normalizedDate, course.getTotalSectionEnrollment());
                }
            } else {
                logger.warning("Snapshot date " + snapshotDate + " is outside the semester range.");
            }
        }
        return enrollmentHistory;
    }
    
    public Map<String, List<Entry<Double, Integer>>> getAllCoursesEnrollmentHistory() {
    Map<String, List<Entry<Double, Integer>>> courseEnrollmentHistory = new HashMap<>();
    long totalDays = ChronoUnit.DAYS.between(preRegDate, addDeadline);

    // Iterate through all snapshots
    for (Snapshot snapshot : snapshots.values()) {
        LocalDate snapshotDate = snapshot.getDate();
        if (!snapshotDate.isBefore(preRegDate) && !snapshotDate.isAfter(addDeadline)) {
            // Iterate through each course in the snapshot
            snapshot.getCourses().forEach((courseName, course) -> {
                // Normalize the snapshot date
                long daysFromStart = ChronoUnit.DAYS.between(preRegDate, snapshotDate);
                double normalizedDate = (double) daysFromStart / totalDays;

                // Add normalized date and enrollment as a pair to the list
                courseEnrollmentHistory
                        .computeIfAbsent(courseName, k -> new ArrayList<>())
                        .add(new SimpleEntry<>(normalizedDate, course.getTotalSectionEnrollment()));
            });
        } else {
            logger.warning("Snapshot date " + snapshotDate + " is outside the semester range.");
        }
    }

    return courseEnrollmentHistory;
}

    @Override
    public Iterator<Snapshot> iterator() {
        return snapshots.values().iterator();
    }
}