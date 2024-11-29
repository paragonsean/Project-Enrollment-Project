package edu.odu.cs.cs350;

import java.io.File;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Semester implements Iterable<Snapshot> {

    private static final Logger logger = Logger.getLogger(Semester.class.getName());

    private final String name;
    private final LocalDate preRegDate;
    private final LocalDate addDeadline;
    private final List<File> csvFiles;
    private final TreeMap<LocalDate, Snapshot> snapshots;

    // Constructor
    public Semester(String name, LocalDate preRegDate, LocalDate addDeadline, List<File> csvFiles) {
        this.name = name;
        this.preRegDate = preRegDate;
        this.addDeadline = addDeadline;
        this.csvFiles = csvFiles;
        this.snapshots = new TreeMap<>();
    }

    // Factory method for creating a Semester and processing CSV files
    public static Semester createSemester(String name, LocalDate preRegDate, LocalDate addDeadline, List<File> csvFiles) {
        logger.log(Level.INFO, "Creating semester: {0}", name);
        Semester semester = new Semester(name, preRegDate, addDeadline, csvFiles);
        CsvProcessor.processCsvFilesToSnapshots(semester);
        return semester;
    }

    // Add a snapshot to the semester
    public void addSnapshot(Snapshot snapshot) {
        snapshots.put(snapshot.getDate(), snapshot);
    }

    // Retrieve a snapshot by its date
    public Snapshot getSnapshotByDate(LocalDate date) {
        return snapshots.get(date);
    }

    // Retrieve all snapshots
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

    // Iterator implementation to iterate over snapshots
    @Override
    public Iterator<Snapshot> iterator() {
        return snapshots.values().iterator();
    }

    // Helper method: Normalize a date to a value between 0 and 1
    public double normalizeDate(LocalDate date) {
        long totalDays = ChronoUnit.DAYS.between(preRegDate, addDeadline);
        long daysFromStart = ChronoUnit.DAYS.between(preRegDate, date);
        return totalDays > 0 ? (double) daysFromStart / totalDays : 0.0;
    }
}
