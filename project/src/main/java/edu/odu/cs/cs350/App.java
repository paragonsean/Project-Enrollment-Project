package edu.odu.cs.cs350;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class App {
    public static void main(String[] args) throws IOException {
        History history = new History();

        // List of semester directories to scan
        List<File> semesterDirs = Arrays.asList(
            new File("/home/spoca/Desktop/summary/202230"),
            new File("/home/spoca/Desktop/summary/202310")
        );

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        history.loadSemestersFromDirectories(semesterDirs, formatter);

        // Generate an enrollment report
        history.generateEnrollmentReport();

        // Compare two specific semesters
        history.compareSemesters("202230", "202310");

        // Retrieve and print the enrollment history of a specific course
        Map<LocalDate, Integer> enrollmentHistory = history.getCourseEnrollmentHistory("CS350");
        enrollmentHistory.forEach((date, enrollment) ->
            System.out.printf("Date: %s | Enrollment: %d\n", date, enrollment)
        );
    }
}
