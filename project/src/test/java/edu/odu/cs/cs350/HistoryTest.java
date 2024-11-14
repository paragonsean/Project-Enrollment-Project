package edu.odu.cs.cs350;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.google.common.collect.ListMultimap;

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
                LocalDate.parse("2023-12-01", formatter), Arrays.asList());

        history.addSemester(semester);
        Semester retrievedSemester = history.getSemester("Fall2023");

        assertThat(retrievedSemester, is(semester));
    }

    @Test
    public void testGetSemesterNames() {
        Semester semester1 = new Semester("Fall2023", LocalDate.parse("2023-08-01", formatter),
                LocalDate.parse("2023-12-01", formatter), Arrays.asList());
        Semester semester2 = new Semester("Spring2023", LocalDate.parse("2023-01-01", formatter),
                LocalDate.parse("2023-05-01", formatter), Arrays.asList());

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

        history.loadSemestersFromDirectories(List.of(directory), formatter);
        assertThat(history.getSemester("Fall2023"), is(notNullValue()));
    }

    @Test
    public void testExtractDateFromDatesFile() throws IOException {
        File datesFile = new File(tempDir, "dates.txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(datesFile))) {
            writer.write("preRegDate=2023-08-01\naddDeadline=2023-12-01\n");
        }

        LocalDate preRegDate = history.extractDateFromDatesFile(datesFile, "preRegDate", formatter);
        assertThat(preRegDate, is(LocalDate.parse("2023-08-01", formatter)));

        LocalDate addDeadline = history.extractDateFromDatesFile(datesFile, "addDeadline", formatter);
        assertThat(addDeadline, is(LocalDate.parse("2023-12-01", formatter)));
    }

    @Test
    public void testFilterCsvFilesByDate() throws IOException {
        File directory = new File(tempDir, "csvFiles");
        directory.mkdir();
        File csvFile = new File(directory, "2023-09-01.csv");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
            writer.write("sample data");
        }

        List<File> csvFiles = history.filterCsvFilesByDate(directory,
                LocalDate.parse("2023-09-01", formatter),
                LocalDate.parse("2023-09-30", formatter), formatter);

        assertThat(csvFiles, hasSize(1));
        assertThat(csvFiles.get(0).getName(), is("2023-09-01.csv"));
    }

    @Test
    public void testCollectCsvFilesByDate() throws IOException {
        Semester semester = new Semester("Fall2023", LocalDate.parse("2023-08-01", formatter),
                LocalDate.parse("2023-12-01", formatter), Arrays.asList());

        File csvFile = new File(tempDir, "2023-09-01.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
            writer.write("sample data");
        }

        Snapshot snapshot = new Snapshot(LocalDate.parse("2023-09-01", formatter), Arrays.asList());
        semester.addSnapshot(snapshot);
        ListMultimap<LocalDate, String> csvFilesByDate = history.collectCsvFilesByDate(formatter);
        assertThat(csvFilesByDate.size(), is(1));
        assertThat(csvFilesByDate.get(LocalDate.parse("2023-09-01", formatter)).get(0), is(csvFile.getAbsolutePath()));
    }

    @Test
    public void testCompareCourseEnrollments() {
        Semester semester1 = new Semester("Fall2023", LocalDate.parse("2023-08-01", formatter),
                LocalDate.parse("2023-12-01", formatter), Arrays.asList());
        Semester semester2 = new Semester("Spring2023", LocalDate.parse("2023-01-01", formatter),
                LocalDate.parse("2023-05-01", formatter), Arrays.asList());

        Snapshot snapshot1 = new Snapshot(LocalDate.parse("2023-09-01", formatter), Arrays.asList());
        Snapshot snapshot2 = new Snapshot(LocalDate.parse("2023-09-01", formatter), Arrays.asList());

        Course course1 = new Course("CS", "350");
        course1.addOfferingsAndSections("CRN120", "XLST1", 10, 10, 50, 50, "link0");

        Course course2 = new Course("CS", "350");
        course2.addOfferingsAndSections("CRN121", "XLST2", 15, 15, 60, 60, "link1");

        snapshot1.addCourse(course1);
        snapshot2.addCourse(course2);

        semester1.addSnapshot(snapshot1);
        semester2.addSnapshot(snapshot2);

        history.addSemester(semester1);
        history.addSemester(semester2);
        int totalEnrollment = history.getTotalEnrollmentAcrossSemesters(LocalDate.parse("2023-08-01", formatter),
        LocalDate.parse("2023-09-30", formatter));
        System.out.println(totalEnrollment);
        assertDoesNotThrow(() -> history.compareCourseEnrollments("CS350", "Fall2023", "Spring2023"));
    }

    @Test
    public void testGetTotalEnrollmentAcrossSemesters() {
        Semester semester = new Semester("Fall2023", LocalDate.parse("2023-08-01", formatter),
                LocalDate.parse("2023-12-01", formatter), Arrays.asList());

        Snapshot snapshot = new Snapshot(LocalDate.parse("2023-09-01", formatter), Arrays.asList());
        Course course = new Course("CS", "101");
       
        snapshot.addCourse(course);

        semester.addSnapshot(snapshot);
        history.addSemester(semester);

        int totalEnrollment = history.getTotalEnrollmentAcrossSemesters(LocalDate.parse("2023-08-01", formatter),
                LocalDate.parse("2023-09-30", formatter));
        System.out.println(totalEnrollment);
        assertThat(totalEnrollment, is(110));
    }

    @Test
    public void testGenerateEnrollmentReport() {
        Semester semester = new Semester("Fall2023", LocalDate.parse("2023-08-01", formatter),
                LocalDate.parse("2023-12-01", formatter), Arrays.asList());

        Snapshot snapshot = new Snapshot(LocalDate.parse("2023-09-01", formatter), Arrays.asList());
 
        semester.addSnapshot(snapshot);
        history.addSemester(semester);

        assertDoesNotThrow(() -> history.generateEnrollmentReport());
    }

    @Test
    public void testGetCourseEnrollmentHistory() {
        Semester semester = new Semester("Fall2023", LocalDate.parse("2023-08-01", formatter),
                LocalDate.parse("2023-12-01", formatter), Arrays.asList());

        Snapshot snapshot = new Snapshot(LocalDate.parse("2023-09-01", formatter), Arrays.asList());
        Course course = new Course("CS", "101");
        snapshot.addCourse(course);

        semester.addSnapshot(snapshot);
        history.addSemester(semester);

        Map<LocalDate, Integer> enrollmentHistory = history.getCourseEnrollmentHistory("CS101");

        assertThat(enrollmentHistory.size(), is(1));
        assertThat(enrollmentHistory.get(LocalDate.parse("2023-09-01", formatter)), is(30));
    }

    @Test
    public void testCompareSemesters() {
        Semester semester1 = new Semester("Fall2023", LocalDate.parse("2023-08-01", formatter),
                LocalDate.parse("2023-12-01", formatter), Arrays.asList());

        Semester semester2 = new Semester("Spring2023", LocalDate.parse("2023-01-01", formatter),
                LocalDate.parse("2023-05-01", formatter), Arrays.asList());

        Snapshot snapshot1 = new Snapshot(LocalDate.parse("2023-09-01", formatter), Arrays.asList());
        Snapshot snapshot2 = new Snapshot(LocalDate.parse("2023-09-01", formatter), Arrays.asList());

        semester1.addSnapshot(snapshot1);
        semester2.addSnapshot(snapshot2);

        history.addSemester(semester1);
        history.addSemester(semester2);

        assertDoesNotThrow(() -> history.compareSemesters("Fall2023", "Spring2023"));
    }
}
