package edu.odu.cs.cs350;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class App {

    public static void main(String[] args) throws IOException {
        History history = new History();

        // List of semester directories to scan
        List<File> semesterDirs = List.of(
            new File("/Users/spocam/Downloads/summary/202320"),
            new File("/Users/spocam/Downloads/summary/202330")
        );

        // Load semesters from directories
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        history.loadSemestersFromDirectories(semesterDirs, formatter);

        // Generate an enrollment report
        System.out.println("\nEnrollment Report:");
        history.generateEnrollmentReport();

        // Compare two specific semesters
        System.out.println("\nComparison of Semesters 202230 and 202310:");
        history.compareSemesters("202230", "202310");

        // Retrieve and print the enrollment history of a specific course
        System.out.println("\nEnrollment History for Course CS350:");
        Map<LocalDate, Integer> enrollmentHistory = history.getCourseEnrollmentHistory("CS350");
        enrollmentHistory.forEach((date, enrollment) ->
            System.out.printf("Date: %s | Enrollment: %d\n", date, enrollment)
        );
    }
}