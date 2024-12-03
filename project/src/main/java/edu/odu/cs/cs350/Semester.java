package edu.odu.cs.cs350;

import java.io.File;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

class Semester implements Iterable<Snapshot> {

    private static final Logger logger = Logger.getLogger(Semester.class.getName());
    private final File path;
    private final String name;
    private final LocalDate preRegDate;
    private final LocalDate addDeadline;
    private final List<File> csvFiles;
    private final TreeMap<LocalDate, Snapshot> snapshots;
    private final DateReader dateReader;
    /**
     * Constructs a Semester object by initializing its path, name, csvFiles,
     * preRegDate, addDeadline, and snapshots.
     * 
     * @param path the file path to initialize the Semester object
     */
    public Semester(String path) {
        this.path = new File(path);
        this.name = this.path.getName();
        this.csvFiles = this.path.isDirectory() ? Arrays.asList(this.path.listFiles()) : Collections.emptyList();
        this.dateReader = new DateReader(path);
        this.preRegDate = dateReader.getPreregistrationDate();
        this.addDeadline = dateReader.getDeadlineDate();
        this.snapshots = new TreeMap<>();
    }

  

    /**
     * Adds a snapshot to the collection of snapshots.
     *
     * @param snapshot the Snapshot object to be added
     */
    public void addSnapshot(Snapshot snapshot) {
        snapshots.put(snapshot.getDate(), snapshot);
    }
    
    /**
     * Retrieves a Snapshot object corresponding to the specified date.
     *
     * @param date the LocalDate for which the Snapshot is to be retrieved
     * @return the Snapshot object associated with the given date, or null if no Snapshot exists for that date
     */
    public Snapshot getSnapshotByDate(LocalDate date) {
        return snapshots.get(date);
    }

    /**
     * Retrieves the name of the semester.
     *
     * @return the name of the semester as a String.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the pre-registration date for the semester.
     *
     * @return the pre-registration date as a LocalDate object
     */
    public LocalDate getPreRegDate() {
        return preRegDate;
    }

    /**
     * Retrieves the deadline for adding courses.
     *
     * @return the date by which courses must be added.
     */
    public LocalDate getAddDeadline() {
        return addDeadline;
    }

    /**
     * Retrieves the list of CSV files.
     *
     * @return a list of File objects representing the CSV files.
     */
    public List<File> getCsvFiles() {
        return csvFiles;
    }

    /**
     * Returns an iterator over the snapshots.
     */
    @Override
    public Iterator<Snapshot> iterator() {
        return snapshots.values().iterator();
    }

    /**
     * Normalizes the given date within the range of pre-registration date and add deadline.
     */
    public double normalizeDate(LocalDate date) {
        long totalDays = ChronoUnit.DAYS.between(preRegDate, addDeadline);
        long daysFromStart = ChronoUnit.DAYS.between(preRegDate, date);
        return totalDays > 0 ? (double) daysFromStart / totalDays : 0.0;
    }
}