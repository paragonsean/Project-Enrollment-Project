package edu.odu.cs.cs350;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

class Semester implements Iterable<Snapshot> {

    private static final Logger logger = Logger.getLogger(Semester.class.getName());
    private final File path;
    private final String name;
    private final LocalDate preRegDate;
    private final LocalDate addDeadline;
    private final List<File> csvFiles;
    private final TreeMap<LocalDate, Snapshot> snapshots;
    private final DateReader dateReader;

    public Semester(String path) {
        this.path = new File(path);
        FileProcessor fileProcessor = new FileProcessor();
        this.name = new File(path).getName();
        this.csvFiles = Collections.unmodifiableList(convertStringToFiles(path));
        this.dateReader = new DateReader(this.path.getAbsolutePath());
        this.preRegDate = dateReader.getPreregistrationDate() != null ? dateReader.getPreregistrationDate() : LocalDate.now();
        this.addDeadline = dateReader.getDeadlineDate() != null ? dateReader.getDeadlineDate() : LocalDate.now().plusMonths(1);
        this.snapshots = new TreeMap<>();
        CsvProcessor.processCsvFilesToSnapshots(this);
    }

    public Semester(String name, String path, LocalDate preRegDate, LocalDate addDeadline, List<File> csvFiles, DateReader dateReader) {
        this.path = new File(path);
        this.name = name;
        this.preRegDate = preRegDate != null ? preRegDate : LocalDate.now();
        this.addDeadline = addDeadline != null ? addDeadline : LocalDate.now().plusMonths(1);
        this.csvFiles = Collections.unmodifiableList(csvFiles);
        this.snapshots = new TreeMap<>();
        this.dateReader = dateReader;
    }

    public static Semester createSemester(String semesterName, LocalDate preRegDate, LocalDate addDeadline, List<File> csvFiles, String path) {
        FileProcessor fileProcessor = new FileProcessor();
        String name = new File(path).getName();
        List<File> files = csvFiles != null ? csvFiles : fileProcessor.convertStringToFiles(path);
        DateReader dateReader = new DateReader(path);
        Semester semester = new Semester(name, path, preRegDate, addDeadline, files, dateReader);
        semester.processCsvFilesToSnapshots();
        return semester;
    }
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

    public double normalizeDate(LocalDate date) {
        if (date == null) {
            logger.severe("Provided date is null for normalization in semester: " + name);
            throw new IllegalArgumentException("Date cannot be null");
        }
        if (preRegDate == null || addDeadline == null) {
            logger.severe("Pre-registration date or add deadline is null for semester: " + name);
            throw new IllegalStateException("Pre-registration date or add deadline cannot be null");
        }
        if (date.isBefore(preRegDate) || date.isAfter(addDeadline)) {
            throw new IllegalArgumentException("Date is out of range");
        }
        long totalDays = ChronoUnit.DAYS.between(preRegDate, addDeadline);
        long daysFromStart = ChronoUnit.DAYS.between(preRegDate, date);
        return totalDays > 0 ? (double) daysFromStart / totalDays : 0.0;
    }

    public void processCsvFilesToSnapshots() {
        logger.info("Starting CSV processing for semester: " + this.name);
        for (File csvFile : this.csvFiles) {
            CsvProcessor.processCsvFile(this, csvFile);
        }
        logger.info("Processed " + snapshots.size() + " snapshots for semester: " + this.name);
    }
}
