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

    public void addSemester(Semester semester) {
        semesters.put(semester.getName(), semester);
    }

    public Semester getSemester(String semesterName) {
        return semesters.get(semesterName);
    }

    public Set<String> getSemesterNames() {
        return semesters.keySet();
    }

    public void generateEnrollmentReport() {
        System.out.println("Enrollment Report by Semester:");
        semesters.forEach((name, semester) -> {
            int totalEnrollment = semester.getSnapshots().stream()
                .mapToInt(Snapshot::getTotalSectionEnrollment)
                .sum();
            System.out.printf("Semester: %s | Total Enrollment: %d\n", name, totalEnrollment);
        });
    }

    public Map<LocalDate, Integer> getCourseEnrollmentHistory(String courseKey) {
        return semesters.values().stream()
            .flatMap(semester -> semester.getSnapshots().stream())
            .filter(snapshot -> snapshot.getCourse(courseKey) != null)
            .collect(Collectors.toMap(
                Snapshot::getDate,
                snapshot -> snapshot.getCourse(courseKey).getTotalSectionEnrollment(),
                (v1, v2) -> v1,
                TreeMap::new
            ));
    }

    public void compareSemesters(String semester1Name, String semester2Name) {
        Semester semester1 = semesters.get(semester1Name);
        Semester semester2 = semesters.get(semester2Name);

        if (semester1 == null || semester2 == null) {
            System.out.println("One or both semesters not found.");
            return;
        }

        System.out.printf("Comparison between %s and %s:\n", semester1Name, semester2Name);

        semester1.getSnapshots().forEach(snapshot1 -> {
            Snapshot snapshot2 = semester2.getSnapshotByDate(snapshot1.getDate());
            if (snapshot2 != null) {
                int enrollmentDifference = snapshot1.getTotalSectionEnrollment()
                        - snapshot2.getTotalSectionEnrollment();
                System.out.printf("Date: %s | Enrollment Difference: %d\n",
                        snapshot1.getDate(), enrollmentDifference);
            }
        });
    }

    public void loadSemestersFromDirectories(List<File> directories, DateTimeFormatter formatter) throws IOException {
        FileProcessor processor = new FileProcessor();
        List<Semester> loadedSemesters = processor.loadSemestersFromDirectories(directories, formatter);
        for (Semester semester : loadedSemesters) {
            addSemester(semester);
        }
    }
}