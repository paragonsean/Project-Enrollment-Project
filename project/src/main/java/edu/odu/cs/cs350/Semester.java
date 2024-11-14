package edu.odu.cs.cs350;

import java.io.File;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

    @Override
    public Iterator<Snapshot> iterator() {
        return snapshots.values().iterator();
    }
}
