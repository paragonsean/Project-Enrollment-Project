package edu.odu.cs.cs350;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class HistoryTest {

    private History history;
    private DateTimeFormatter formatter;

    @TempDir
    File tempDir;

    @BeforeEach
    public void setUp() {
        history = new History();
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }

    @Test
    public void testAddAndGetSemester() {
        Semester semester = new Semester("Fall2023", LocalDate.parse("2023-08-01", formatter),
                LocalDate.parse("2023-12-01", formatter), new ArrayList<>());

        history.addSemester(semester);
        Optional<Semester> retrievedSemester = history.getSemester("Fall2023");

        assertThat(retrievedSemester.isPresent(), is(true));
        assertThat(retrievedSemester.get(), is(semester));
    }

    @Test
    public void testGetSemesterNames() {
        Semester semester1 = new Semester("Fall2023", LocalDate.parse("2023-08-01", formatter),
                LocalDate.parse("2023-12-01", formatter), new ArrayList<>());
        Semester semester2 = new Semester("Spring2023", LocalDate.parse("2023-01-01", formatter),
                LocalDate.parse("2023-05-01", formatter), new ArrayList<>());

        history.addSemester(semester1);
        history.addSemester(semester2);

        Set<String> semesterNames = history.getSemesterNames();
        assertThat(semesterNames, containsInAnyOrder("Fall2023", "Spring2023"));
    }

    @Test
    public void testLoadSemestersFromDirectories() throws IOException {
        File directory = new File(tempDir, "Fall2023");
        directory.mkdir();
        File datesFile = new File(directory, "dates.txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(datesFile))) {
            writer.write("preRegDate=2023-08-01\naddDeadline=2023-12-01\n");
        }

        history.loadSemestersFromDirectories(List.of(directory));
        assertThat(history.getSemester("Fall2023").isPresent(), is(true));
    }

    @Test
    public void testGetTotalCourseEnrollment() {
        Semester semester = new Semester("Fall2023", LocalDate.parse("2023-08-01", formatter),
                LocalDate.parse("2023-12-01", formatter), new ArrayList<>());

        Snapshot snapshot = new Snapshot(LocalDate.parse("2023-09-01", formatter), new ArrayList<>());
        Course course = new Course("CS101", "Intro to CS");
        course.setTotalEnrollment(30); // Mock total enrollment
        snapshot.addCourse(course);

        semester.addSnapshot(snapshot);
        history.addSemester(semester);

        int totalEnrollment = history.getTotalCourseEnrollment("CS101");
        assertThat(totalEnrollment, is(30));
    }

    @Test
    public void testGetCourseEnrollmentHistory() {
        Semester semester = new Semester("Fall2023", LocalDate.parse("2023-08-01", formatter),
                LocalDate.parse("2023-12-01", formatter), new ArrayList<>());

        Snapshot snapshot = new Snapshot(LocalDate.parse("2023-09-01", formatter), new ArrayList<>());
        Course course = new Course("CS101", "Intro to CS");
        course.setTotalEnrollment(30); // Mock total enrollment
        snapshot.addCourse(course);

        semester.addSnapshot(snapshot);
        history.addSemester(semester);

        Map<LocalDate, Integer> enrollmentHistory = history.getCourseEnrollmentHistory("CS101");

        assertThat(enrollmentHistory.size(), is(1));
        assertThat(enrollmentHistory.get(LocalDate.parse("2023-09-01", formatter)), is(30));
    }

    @Test
    public void testGenerateEnrollmentReport() {
        Semester semester = new Semester("Fall2023", LocalDate.parse("2023-08-01", formatter),
                LocalDate.parse("2023-12-01", formatter), new ArrayList<>());

        Snapshot snapshot = new Snapshot(LocalDate.parse("2023-09-01", formatter), new ArrayList<>());
        Course course = new Course("CS101", "Intro to CS");
        course.setTotalEnrollment(30); // Mock total enrollment
        snapshot.addCourse(course);

        semester.addSnapshot(snapshot);
        history.addSemester(semester);

        assertDoesNotThrow(() -> history.generateEnrollmentReport());
    }

    @Test
    public void testCompareSemesters() {
        Semester semester1 = new Semester("Fall2023", LocalDate.parse("2023-08-01", formatter),
                LocalDate.parse("2023-12-01", formatter), new ArrayList<>());

        Semester semester2 = new Semester("Spring2023", LocalDate.parse("2023-01-01", formatter),
                LocalDate.parse("2023-05-01", formatter), new ArrayList<>());

        history.addSemester(semester1);
        history.addSemester(semester2);

        assertDoesNotThrow(() -> history.compareSemesters("Fall2023", "Spring2023"));
    }
}
