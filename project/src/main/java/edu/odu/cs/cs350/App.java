package edu.odu.cs.cs350;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class App {

    public static void main(String[] args) throws IOException {
        // Initialize the history, file processor, and enrollment calculator
        History history = new History();
        FileProcessor fileProcessor = new FileProcessor();
        EnrollmentCalculator calculator = new EnrollmentCalculator();

        // List of historical semester directories (adjust paths as needed)
        List<File> semesterDirs = List.of(
                new File("C:\\Users\\spoca\\Desktop\\202220"),
                new File("C:\\Users\\spoca\\Desktop\\202320"),
                new File("C:\\Users\\spoca\\Desktop\\202330"),
                new File("C:\\Users\\spoca\\Desktop\\202310")
        );

        // Load historical semesters into History
        fileProcessor.loadSemestersFromDirectories(semesterDirs, Optional.empty())
                .forEach(history::addSemester);

        // Load the current semester (adjust path if necessary)
        File currentSemesterDir = new File("C:\\Users\\spoca\\Desktop\\202410");
        Semester currentSemester = fileProcessor.processDirectory(currentSemesterDir, Optional.empty());
        history.addSemester(currentSemester);

        // Initialize scanner for user input
        Scanner scanner = new Scanner(System.in);

        // Interactive loop for projecting enrollments
        while (true) {
            System.out.println("\nEnter a course to project enrollments for (or type 'exit' to quit):");
            String targetCourse = scanner.nextLine().trim();

            if (targetCourse.equalsIgnoreCase("exit")) {
                System.out.println("Exiting the program.");
                break;
            }

            // Check if the input is valid
            if (targetCourse.isEmpty()) {
                System.out.println("Invalid input. Please enter a valid course name.");
                continue; // Skip this iteration
            }

            // Calculate projections for the input course
            ProjectedCourseEnrollment projection = new ProjectedCourseEnrollment(targetCourse, 100); // Assuming a capacity of 100

            // Collect historical and current data for the input course
            boolean dataFound = false;
            for (Semester semester : history.getSemesters()) {
                for (Snapshot snapshot : semester.getSnapshots()) {
                    double normalizedDate = semester.normalizeDate(snapshot.getDate());
                    Course course = snapshot.getCourses().get(targetCourse);
                    if (course != null) {
                        dataFound = true;
                        int enrollment = course.getTotalEnrollment();
                        if (semester == currentSemester) {
                            projection.addCurrentEnrollment(normalizedDate, enrollment);
                        } else {
                            projection.addHistoricalEnrollment(normalizedDate, enrollment);
                        }
                    }
                }
            }

            // Handle the case where no data is found for the entered course
            if (!dataFound) {
                System.out.printf("No data found for course '%s'. Please try another course.\n", targetCourse);
                continue; // Skip projection generation for this course
            }

            // Generate projections for the input course
            projection.generateProjectionsForQuarters();

            // Display the projections
            System.out.printf("\nProjections for %s:\n", targetCourse);
            System.out.println(projection);

            // Print actual enrollments for the current semester
            System.out.printf("\nActual Enrollments for %s in Current Semester:\n", targetCourse);
            boolean courseFound = false;
            for (Snapshot snapshot : currentSemester.getSnapshots()) {
                double normalizedDate = currentSemester.normalizeDate(snapshot.getDate());
                Course course = snapshot.getCourses().get(targetCourse);
                if (course != null) {
                    courseFound = true;
                    int actualEnrollment = course.getTotalSectionEnrollment();
                    System.out.printf("Date: %.2f | Actual Enrollment: %d\n", normalizedDate, actualEnrollment);
                }
            }

            if (!courseFound) {
                System.out.printf("No actual enrollment data found for course %s in the current semester.\n", targetCourse);
            }

        }

        // Close scanner
        scanner.close();
    }
}
