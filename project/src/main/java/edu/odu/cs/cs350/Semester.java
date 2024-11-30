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

    /**
     * Constructs a Semester object by initializing its path, name, csvFiles, dateReader,
     * preRegDate, addDeadline, and snapshots. It also processes CSV files to snapshots.
     *
     * @param path the file path to initialize the Semester object
     */
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

    /**
     * Constructs a Semester object with the specified parameters.
     *
     * @param name the name of the semester
     * @param path the file path associated with the semester
     * @param preRegDate the pre-registration date for the semester; if null, the current date is used
     * @param addDeadline the add deadline date for the semester; if null, one month from the current date is used
     * @param csvFiles a list of CSV files associated with the semester
     * @param dateReader the DateReader object used to read dates from the CSV files
     */
    public Semester(String name, String path, LocalDate preRegDate, LocalDate addDeadline, List<File> csvFiles, DateReader dateReader) {
        this.path = new File(path);
        this.name = name;
        this.preRegDate = preRegDate != null ? preRegDate : LocalDate.now();
        this.addDeadline = addDeadline != null ? addDeadline : LocalDate.now().plusMonths(1);
        this.csvFiles = Collections.unmodifiableList(csvFiles);
        this.snapshots = new TreeMap<>();
        this.dateReader = dateReader;
    }

    /**
     * Creates a new Semester object.
     *
     * @param semesterName the name of the semester
     * @param preRegDate the pre-registration date for the semester
     * @param addDeadline the add deadline date for the semester
     * @param csvFiles a list of CSV files to be processed, can be null
     * @param path the file path to be processed if csvFiles is null
     * @return a new Semester object
     */
    public static Semester createSemester(String semesterName, LocalDate preRegDate, LocalDate addDeadline, List<File> csvFiles, String path) {
        FileProcessor fileProcessor = new FileProcessor();
        String name = new File(path).getName();
        List<File> files = csvFiles != null ? csvFiles : fileProcessor.convertStringToFiles(path);
        DateReader dateReader = new DateReader(path);
        Semester semester = new Semester(name, path, preRegDate, addDeadline, files, dateReader);
        semester.processCsvFilesToSnapshots();
        return semester;
    }
    
    /**
     * Converts a directory path string to a list of File objects representing
     * the files in the specified directory.
     *
     * @param directory the path of the directory to be converted to a list of files
     * @return a list of File objects representing the files in the specified directory,
     *         or an empty list if the directory does not exist or is not a directory
     */
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
     * Retrieves a collection of Snapshot objects.
     *
     * @return a collection containing all Snapshot objects.
     */
    public Collection<Snapshot> getSnapshots() {
        return snapshots.values();
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
     * Returns an iterator over the elements in this collection of snapshots.
     * This method allows the use of the enhanced for-loop to iterate over
     * the snapshots in this collection.
     *
     * @return an Iterator over the Snapshot objects in this collection
     */
    @Override
    public Iterator<Snapshot> iterator() {
        return snapshots.values().iterator();
    }

    /**
     * Normalizes the given date within the range of pre-registration date and add deadline.
     * The normalization is done by calculating the ratio of the number of days from the 
     * pre-registration date to the given date over the total number of days between the 
     * pre-registration date and the add deadline.
     *
     * @param date the date to be normalized; must not be null and must be within the range 
     *             of pre-registration date and add deadline.
     * @return a double value representing the normalized date, where 0.0 represents the 
     *         pre-registration date and 1.0 represents the add deadline.
     * @throws IllegalArgumentException if the provided date is null or out of range.
     * @throws IllegalStateException if the pre-registration date or add deadline is null.
     */
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

    /**
     * Processes all CSV files associated with the current semester and generates snapshots.
     * Logs the start and end of the processing, including the number of snapshots created.
     */
    public void processCsvFilesToSnapshots() {
        logger.info("Starting CSV processing for semester: " + this.name);
        for (File csvFile : this.csvFiles) {
            CsvProcessor.processCsvFile(this, csvFile);
        }
        logger.info("Processed " + snapshots.size() + " snapshots for semester: " + this.name);
    }
}
