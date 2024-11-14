package edu.odu.cs.cs350;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class SemesterTest {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private Semester semester;

    @TempDir
    File tempDir;

    @BeforeEach
    public void setUp() throws IOException {
        // Set up CSV files with specific dates for testing
        File csvFile1 = new File(tempDir, "2023-09-01.csv");
        File csvFile2 = new File(tempDir, "2023-10-01.csv");
        File csvFile3 = new File(tempDir, "2023-11-01.csv");

        writeCsvFile(csvFile1);
        writeCsvFile(csvFile2);
        writeCsvFile(csvFile3);

        List<File> csvFiles = List.of(csvFile1, csvFile2, csvFile3);

        LocalDate preRegDate = LocalDate.parse("2023-09-01", DATE_FORMATTER);
        LocalDate addDeadline = LocalDate.parse("2023-11-01", DATE_FORMATTER);

        // Initialize Semester with files
        semester = new Semester("Fall2023", preRegDate, addDeadline, csvFiles);
    }

    private void writeCsvFile(File csvFile) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
            writer.write("SUBJ,CRSE,CRN,XLST GROUP,XLST CAP,ENR,OVERALL CAP,OVERALL ENR,LINK,INSTRUCTOR\n");
            writer.write("CS,101,CRN101,XLST101,20,15,30,30,LNK001,Prof. Smith\n");
            writer.write("CS,102,CRN102,XLST102,15,10,25,25,LNK002,Prof. Jones\n");
        }
    }

    @Test
    public void testConstructor() {
        assertNotNull(semester);
        assertEquals("Fall2023", semester.getName());
        assertEquals(LocalDate.parse("2023-09-01", DATE_FORMATTER), semester.getPreRegDate());
        assertEquals(LocalDate.parse("2023-11-01", DATE_FORMATTER), semester.getAddDeadline());
        assertEquals(3, semester.getCsvFiles().size());
    }

    @Test
    public void testCreateSemester() {
        // Creating a semester using the factory method
        Semester createdSemester = Semester.createSemester("Fall2023", LocalDate.parse("2023-09-01", DATE_FORMATTER),
                LocalDate.parse("2023-11-01", DATE_FORMATTER), new ArrayList<>(semester.getCsvFiles()));

        assertNotNull(createdSemester);
        assertEquals("Fall2023", createdSemester.getName());
        assertEquals(3, createdSemester.getSnapshots().size());
    }

    @Test
    public void testProcessCsvFilesToSnapshots() {
        semester.processCsvFilesToSnapshots();
        assertEquals(3, semester.getSnapshots().size());

        Snapshot snapshot = semester.getSnapshotByDate(LocalDate.parse("2023-10-01", DATE_FORMATTER));
        assertNotNull(snapshot);
        assertEquals(2, snapshot.getCourses().size());
    }

    @Test
    public void testAddSnapshot() {
        Snapshot newSnapshot = new Snapshot(LocalDate.parse("2023-12-01", DATE_FORMATTER), new ArrayList<>());
        semester.addSnapshot(newSnapshot);

        assertEquals(1, semester.getSnapshots().size());
        assertEquals(newSnapshot, semester.getSnapshotByDate(LocalDate.parse("2023-12-01", DATE_FORMATTER)));
    }

    @Test
    public void testGetSnapshotByDate() {
        semester.processCsvFilesToSnapshots();
        Snapshot snapshot = semester.getSnapshotByDate(LocalDate.parse("2023-10-01", DATE_FORMATTER));
        assertNotNull(snapshot);
        assertEquals(2, snapshot.getCourses().size());
    }

    @Test
    public void testInvalidDateRange() {
        // Test a date out of range (should not include a snapshot for this date)
        semester.processCsvFilesToSnapshots();
        Snapshot snapshot = semester.getSnapshotByDate(LocalDate.parse("2024-01-01", DATE_FORMATTER));
        assertEquals(null, snapshot);
    }

    @Test
    public void testExtractDateFromFilename() {
        LocalDate date = semester.extractDateFromFilename("2023-10-01.csv", Pattern.compile("(\\d{4}-\\d{2}-\\d{2})\\.csv"));
        assertEquals(LocalDate.parse("2023-10-01", DATE_FORMATTER), date);

        LocalDate invalidDate = semester.extractDateFromFilename("invalid.csv", Pattern.compile("(\\d{4}-\\d{2}-\\d{2})\\.csv"));
        assertEquals(null, invalidDate);
    }

    @Test
    public void testGetColumnIndexNotFound() {
        String[] headers = {"SUBJ", "CRSE", "CRN", "XLST GROUP", "XLST CAP", "ENR", "OVERALL CAP", "OVERALL ENR", "LINK", "INSTRUCTOR"};
        int index = semester.getColumnIndex(headers, "SUBJ");
        assertEquals(0, index);
    
        int overallCapIndex = semester.getColumnIndex(headers, "OVERALL CAP");
        assertEquals(6, overallCapIndex);

        int overallEnrIndex = semester.getColumnIndex(headers, "OVERALL ENR");
        assertEquals(7, overallEnrIndex);
    }
    @Test
    public void testParseOrDefault() {
        String[] fields = {"10", "20", "", "40"};
        assertEquals(10, semester.parseOrDefault(fields, 0, 0));
        assertEquals(0, semester.parseOrDefault(fields, 2, 0)); // empty value defaults to 0
        assertEquals(40, semester.parseOrDefault(fields, 3, 0));
    }
}
