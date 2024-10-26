// package edu.odu.cs.cs350.enp;

// import java.io.*;
// import java.nio.file.*;
// import java.time.LocalDate;
// import java.time.format.DateTimeFormatter;
// import java.util.*;
// import java.util.logging.Logger;

// public class Semester {
//     private static final Logger logger = Logger.getLogger(Semester.class.getName());
//     private String name;                          // Name of the semester
//     private LocalDate preRegistrationStart;       // Pre-registration start date
//     private LocalDate addDeadline;                // Add deadline date
//     private DateTimeFormatter dateTimeFormatter;  // Formatter for parsing snapshot dates
//     private List<Snapshot> snapshots;             // List of snapshots for this semester
//     private CsvReader csvReader;                  // CSV reader utility for handling CSV files

//     // Constructor
//     public Semester(String name, LocalDate preRegistrationStart, LocalDate addDeadline, DateTimeFormatter dateTimeFormatter) {
//         this.name = name;
//         this.preRegistrationStart = preRegistrationStart;
//         this.addDeadline = addDeadline;
//         this.dateTimeFormatter = dateTimeFormatter;
//         this.snapshots = new ArrayList<>();
//         this.csvReader = new CsvReader();
//     }

//     // Getters
//     public String getName() {
//         return name;
//     }

//     public LocalDate getPreRegistrationStart() {
//         return preRegistrationStart;
//     }

//     public LocalDate getAddDeadline() {
//         return addDeadline;
//     }

//     public List<Snapshot> getSnapshots() {
//         return new ArrayList<>(snapshots);
//     }

//     /**
//      * Lists all CSV files in the specified directory.
//      *
//      * @param semesterDir The directory containing snapshot files.
//      * @return A list of CSV file paths.
//      * @throws IOException If an error occurs while listing files.
//      */
//     public List<Path> listCsvFiles(String semesterDir) throws IOException {
//         List<Path> csvFiles = new ArrayList<>();
//         try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(semesterDir), "*.csv")) {
//             for (Path path : directoryStream) {
//                 csvFiles.add(path);
//             }
//         }
//         return csvFiles;
//     }

//     /**
//      * Filters snapshots within the pre-registration start and add deadline dates.
//      *
//      * @param csvFiles A list of CSV file paths to filter.
//      * @return A list of filtered Snapshot objects.
//      * @throws Exception If an error occurs while processing snapshot files.
//      */
//     public List<Snapshot> filterSnapshots(List<Path> csvFiles) throws Exception {
//         List<Snapshot> filteredSnapshots = new ArrayList<>();
//         for (Path path : csvFiles) {
//             String fileName = path.getFileName().toString();
//             LocalDate snapshotDate;
//             try {
//                 snapshotDate = LocalDate.parse(fileName.substring(0, 10), dateTimeFormatter);
//             } catch (Exception e) {
//                 throw new RuntimeException("Error parsing date from file name: " + fileName, e);
//             }
//             if (!snapshotDate.isBefore(preRegistrationStart) && !snapshotDate.isAfter(addDeadline)) {
//                 filteredSnapshots.add(new Snapshot(fileName, snapshotDate));
//             }
//         }
//         filteredSnapshots.sort(Comparator.comparing(Snapshot::getDate));
//         this.snapshots = filteredSnapshots; // Update snapshots list with valid snapshots
//         return filteredSnapshots;
//     }

//     /**
//      * Adds a new snapshot to the semester.
//      *
//      * @param snapshot The snapshot to add.
//      */
//     public void addSnapshot(Snapshot snapshot) {
//         snapshots.add(snapshot);
//         snapshots.sort(Comparator.comparing(Snapshot::getDate)); // Keep snapshots sorted by date
//     }

//     /**
//      * Lists all snapshots.
//      *
//      * @return A list of all snapshots.
//      */
//     public List<Snapshot> listSnapshots() {
//         return new ArrayList<>(snapshots);
//     }

//     /**
//      * Filters snapshots by a date range within the semester.
//      *
//      * @param startDate The start date of the range.
//      * @param endDate   The end date of the range.
//      * @return A list of snapshots within the specified date range.
//      */
//     public List<Snapshot> filterSnapshotsByDate(LocalDate startDate, LocalDate endDate) {
//         List<Snapshot> filteredSnapshots = new ArrayList<>();
//         for (Snapshot snapshot : snapshots) {
//             if (!snapshot.getDate().isBefore(startDate) && !snapshot.getDate().isAfter(endDate)) {
//                 filteredSnapshots.add(snapshot);
//             }
//         }
//         return filteredSnapshots;
//     }
// }
