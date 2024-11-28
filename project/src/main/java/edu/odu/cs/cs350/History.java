package edu.odu.cs.cs350;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class History {

    private Map<String, Semester> semesters;

    public History() {
        this.semesters = new HashMap<>();
    }

    /**
     * Add a semester to the history. If the semester already exists, it is replaced.
     * 
     * @param semester The semester to add.
     */
    public void addSemester(Semester semester) {
        semesters.computeIfAbsent(semester.getName(), k -> {
            System.out.printf("Adding new semester: %s\n", semester.getName());
            return semester;
        });
    }

    /**
     * Retrieve a semester by its name.
     * 
     * @param semesterName The name of the semester.
     * @return The Semester object if found, null otherwise.
     */
    public Semester getSemester(String semesterName) {
        return semesters.get(semesterName);
    }

    /**
     * Retrieve all semester names.
     * 
     * @return A set of all semester names.
     */
    public Set<String> getSemesterNames() {
        return semesters.keySet();
    }

    /**
     * Generate an enrollment report for all semesters, courses, and snapshots.
     */
    public void generateEnrollmentReport() {
        semesters.values().forEach(semester -> {
            System.out.printf("Semester: %s\n", semester.getName());
            semester.getSnapshots().forEach(snapshot -> {
                System.out.printf("  Date: %s\n", snapshot.getDate());
                snapshot.getCourses().forEach((courseKey, course) -> {
                    System.out.printf("    Course: %s | Enrollment: %d\n",
                            courseKey, course.getTotalSectionEnrollment());
                });
            });
        });
    }

    /**
     * Retrieve the enrollment history of a specific course across all semesters.
     * 
     * @param courseKey The key representing the course (e.g., course ID or name).
     * @return A map where the key is the date and the value is the total enrollment for the course.
     */
    public Map<LocalDate, Integer> getCourseEnrollmentHistory(String courseKey) {
        return semesters.values().stream()
            .flatMap(semester -> semester.getSnapshots().stream())
            .filter(snapshot -> snapshot.getCourse(courseKey) != null)
            .collect(Collectors.toMap(
                Snapshot::getDate,
                snapshot -> snapshot.getCourse(courseKey).getTotalSectionEnrollment(),
                (v1, v2) -> v1,  // Handle duplicate keys by keeping the first value
                TreeMap::new      // Use TreeMap for sorted dates
            ));
    }

    /**
     * Load semesters from a list of directories using a specified formatter.
     * 
     * @param directories List of directories containing semester data.
     * @param formatter   The DateTimeFormatter used to parse dates.
     * @throws IOException If an error occurs during loading.
     */
    public void loadSemestersFromDirectories(List<File> directories, DateTimeFormatter formatter) throws IOException {
        FileProcessor processor = new FileProcessor();
        List<Semester> loadedSemesters = processor.loadSemestersFromDirectories(directories, formatter);
        for (Semester semester : loadedSemesters) {
            addSemester(semester);
        }
    }
}