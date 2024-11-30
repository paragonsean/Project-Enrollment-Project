package edu.odu.cs.cs350;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SemesterTest {

    @TempDir
    File tempDir;

    private Semester semester;

    @BeforeEach
    public void setUp() throws IOException {
        // Create a temporary directory to mimic semester directories
        File semesterDir = new File(tempDir, "TestSemester");
        semesterDir.mkdir();

        // Create a dates.txt file with pre-registration and add-deadline dates
        File datesFile = new File(semesterDir, "dates.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(datesFile))) {
            writer.write("2023-01-01\n");
            writer.write("2023-05-01\n");
        }

        // Create mock CSV files
        File csv1 = new File(semesterDir, "2023-01-23.csv");
        File csv2 = new File(semesterDir, "2023-02-15.csv");
        csv1.createNewFile();
        csv2.createNewFile();

        // Prepare Semester instance
        List<File> csvFiles = List.of(csv1, csv2);
        semester = new Semester("TestSemester", semesterDir.getPath(),
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 5, 1),
                csvFiles,
                new DateReader(semesterDir.getPath())
        );
    }

    @Test
    public void testConstructor() {
        assertEquals("TestSemester", semester.getName());
        assertEquals(LocalDate.of(2023, 1, 1), semester.getPreRegDate());
        assertEquals(LocalDate.of(2023, 5, 1), semester.getAddDeadline());
        assertEquals(2, semester.getCsvFiles().size());
    }

    @Test
    public void testAddSnapshot() {
        Snapshot snapshot = new Snapshot(LocalDate.of(2023, 2, 1), new ArrayList<>());
        semester.addSnapshot(snapshot);
        Snapshot retrievedSnapshot = semester.getSnapshotByDate(LocalDate.of(2023, 2, 1));
        assertNotNull(retrievedSnapshot);
        assertEquals(snapshot, retrievedSnapshot);
    }

    @Test
    public void testGetSnapshots() {
        Snapshot snapshot1 = new Snapshot(LocalDate.of(2023, 2, 1), new ArrayList<>());
        Snapshot snapshot2 = new Snapshot(LocalDate.of(2023, 3, 1), new ArrayList<>());
        semester.addSnapshot(snapshot1);
        semester.addSnapshot(snapshot2);

        List<Snapshot> snapshots = new ArrayList<>(semester.getSnapshots());
        assertEquals(2, snapshots.size());
        assertTrue(snapshots.contains(snapshot1));
        assertTrue(snapshots.contains(snapshot2));
    }

    @Test
    public void testNormalizeDate() {
        double normalizedDate = semester.normalizeDate(LocalDate.of(2023, 3, 1));
        assertEquals(0.5, normalizedDate, 0.01);
    }

    @Test
    public void testNormalizeDateWithNullDate() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            semester.normalizeDate(null);
        });
        assertEquals("Date cannot be null", exception.getMessage());
    }

    @Test
    public void testNormalizeDateOutOfRange() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            semester.normalizeDate(LocalDate.of(2022, 12, 31));
        });
        assertEquals("Date is out of range", exception.getMessage());
    }

    @Test
    public void testProcessCsvFilesToSnapshots() {
        semester.processCsvFilesToSnapshots();
        assertFalse(semester.getSnapshots().isEmpty());
    }

    @Test
    public void testCreateSemester() throws IOException {
        File semesterDir = new File(tempDir, "CreatedSemester");
        semesterDir.mkdir();

        File datesFile = new File(semesterDir, "dates.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(datesFile))) {
            writer.write("2023-01-01\n");
            writer.write("2023-05-01\n");
        }

        File csv1 = new File(semesterDir, "2023-01-23.csv");
        File csv2 = new File(semesterDir, "2023-02-15.csv");
        csv1.createNewFile();
        csv2.createNewFile();

        List<File> csvFiles = List.of(csv1, csv2);
        Semester createdSemester = Semester.createSemester("CreatedSemester",
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 5, 1),
                csvFiles,
                semesterDir.getPath()
        );

        assertEquals("CreatedSemester", createdSemester.getName());
        assertEquals(LocalDate.of(2023, 1, 1), createdSemester.getPreRegDate());
        assertEquals(LocalDate.of(2023, 5, 1), createdSemester.getAddDeadline());
        assertEquals(2, createdSemester.getCsvFiles().size());
    }
}
