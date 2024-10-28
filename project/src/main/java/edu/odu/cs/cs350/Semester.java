// package edu.odu.cs.cs350;

// import java.io.File;
// import java.io.IOException;
// import java.net.MalformedURLException;
// import java.net.URL;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.nio.file.Paths;
// import java.time.LocalDate;
// import java.util.List;



// public class Semester {
//     private boolean isURL;
//     private String name;
//     private LocalDate preRegDate;
//     private LocalDate addDeadline;
//     private URL url;
//     private Path pathToSemesterDir;
//     private List<File> csvFiles;
//     private final DateReader dateReader = new DateReader(); // DateReader instance

//     // Default constructor
//     public Semester() {
//         this.isURL = false;
//         this.name = "";
//         this.preRegDate = null;
//         this.addDeadline = null;
//     }

//     // Parameterized constructor
//     public Semester(String semesterPath, LocalDate preRegDate, LocalDate addDeadline) throws Throwable {
//         this.isURL = false;
//         setName(semesterPath);
//         this.preRegDate = preRegDate;
//         this.addDeadline = addDeadline;
//         setPath(semesterPath);
//     }

//     public URL getURL() {
//         return this.url;
//     }

//     public String getName() {
//         return this.name;
//     }

//     public LocalDate getPreRegDate() {
//         return this.preRegDate;
//     }

//     public LocalDate getAddDeadline() {
//         return this.addDeadline;
//     }

//     public Path getPath() {
//         return this.pathToSemesterDir;
//     }

//     public List<File> getCsvFiles() {
//         return this.csvFiles;
//     }

//     // Sets the semester name based on the directory path
//     public void setName(String semesterPath) {
//         String[] tokens = semesterPath.split("[\\\\|/]");
//         this.name = tokens[tokens.length - 1];
//     }

//     // Sets the path for the Semester instance, handling URLs and local paths
//     public boolean setPath(String semesterDirPath) throws Throwable {
//         String s = semesterDirPath.trim().toLowerCase();
//         this.isURL = s.startsWith("http://") || s.startsWith("https://");

//         try {
//             if (isURL) {
//                 this.url = new URL(semesterDirPath);
//                 this.pathToSemesterDir = Paths.get(this.url.getPath());
//             } else {
//                 this.pathToSemesterDir = Paths.get(semesterDirPath);
//             }
//         } catch (MalformedURLException e) {
//             throw new IOException("Invalid URL provided for semester path: " + semesterDirPath, e);
//         }

//         return this.isURL;
//     }

//     // Sets registration dates using DateReader
//     public void setDates() throws IOException {
//         LocalDate[] dates = dateReader.getRegistrationDates(this.pathToSemesterDir);
//         this.preRegDate = dates[0];
//         this.addDeadline = dates[1];
//     }

//     // Fetches files either from a URL or local directory
//     public List<File> fetchFiles() throws IOException {
//         if (isURL) {
//             this.csvFiles = CSVUtils.fetchFilesFromURL(this.url);
//         } else if (Files.isRegularFile(this.pathToSemesterDir)) {
//             this.csvFiles = CSVUtils.fetchFilesFromPath(this.pathToSemesterDir);
//         }

//         // Set dates from dates.txt using DateReader
//         for (File file : this.csvFiles) {
//             if (file.getName().equals("dates.txt")) {
//                 setDates();
//                 break;
//             }
//         }

//         return this.csvFiles;
//     }

// public List<SnapshotData> readCsvByLine(String filename) throws IOException {
//     try {
//         return CSVUtils.readCsvByLine(filename);
//     } catch (Exception e) {
//         throw new IOException("Error reading CSV file by line: " + filename, e);
//     }
// }


//     // Get start and end dates from CSV files
//     public LocalDate[] getSemesterStartAndEndDates() throws IOException {
//         return dateReader.extractDatesFromCsvFiles(this.pathToSemesterDir);
//     }
// }
