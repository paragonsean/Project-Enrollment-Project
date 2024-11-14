package edu.odu.cs.cs350;

import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.opencsv.CSVReader;



public class CsvProcessorTest {

    private Semester mockSemester;
    private Snapshot mockSnapshot;
    private File mockFile;
    private LocalDate mockDate;

    @BeforeEach
    public void setUp() {
        mockSemester = mock(Semester.class);
        mockSnapshot = mock(Snapshot.class);
        mockFile = mock(File.class);
        mockDate = LocalDate.of(2023, 10, 1);
    }

    @Test
    public void testProcessCsvFilesToSnapshots() {
        when(mockSemester.getName()).thenReturn("Fall 2023");
        when(mockSemester.getCsvFiles()).thenReturn(Arrays.asList(mockFile));

        CsvProcessor.processCsvFilesToSnapshots(mockSemester);

        verify(mockSemester, times(1)).getCsvFiles();
    }

    @Test
    public void testAddCsvToSnapshot() throws Exception {
        when(mockFile.getName()).thenReturn("test.csv");
        FileReader mockFileReader = mock(FileReader.class);
        CSVReader mockCsvReader = mock(CSVReader.class);
        List<String[]> mockRows = Arrays.asList(
                new String[]{"SUBJ", "CRSE", "CRN", "XLST GROUP", "XLST CAP", "ENR", "OVERALL CAP", "OVERALL ENR", "LINK", "INSTRUCTOR"},
                new String[]{"CS", "350", "12345", "", "30", "25", "30", "25", "", "Dr. Smith"}
        );

        try (MockedStatic<CSVReader> csvReaderMockedStatic = Mockito.mockStatic(CSVReader.class)) {
            csvReaderMockedStatic.when(() -> new CSVReader(mockFileReader)).thenReturn(mockCsvReader);
            when(mockCsvReader.readAll()).thenReturn(mockRows);

            CsvProcessor.addCsvToSnapshot(mockSnapshot, mockFile);

            verify(mockSnapshot, times(1)).addCourse(any(Course.class));
        }
    }

    @Test
    public void testMapFieldsToCourse() {
        String[] fields = {"CS", "350", "12345", "", "30", "25", "30", "25", "", "Dr. Smith"};
        Map<String, Integer> headerIndexMap = new HashMap<>();
        headerIndexMap.put("SUBJ", 0);
        headerIndexMap.put("CRSE", 1);
        headerIndexMap.put("CRN", 2);
        headerIndexMap.put("XLST GROUP", 3);
        headerIndexMap.put("XLST CAP", 4);
        headerIndexMap.put("ENR", 5);
        headerIndexMap.put("OVERALL CAP", 6);
        headerIndexMap.put("OVERALL ENR", 7);
        headerIndexMap.put("LINK", 8);
        headerIndexMap.put("INSTRUCTOR", 9);

        Course course = CsvProcessor.mapFieldsToCourse(fields, headerIndexMap, mockDate);

        assertNotNull(course);
        assertEquals("CS", course.getSubject());
        assertEquals("350", course.getCourseNumber());
    }

    @Test
    public void testParseOrDefault() {
        String[] fields = {"10", "20", "30"};
        int result = CsvProcessor.parseOrDefault(fields, 1, 0);
        assertEquals(20, result);

        result = CsvProcessor.parseOrDefault(fields, 3, 0);
        assertEquals(0, result);
    }

    @Test
    public void testProcessCsvFile() {
        when(mockFile.getName()).thenReturn("20231001_test.csv");
        when(mockSemester.getPreRegDate()).thenReturn(LocalDate.of(2023, 9, 1));
        when(mockSemester.getAddDeadline()).thenReturn(LocalDate.of(2023, 11, 1));
        when(mockSemester.getSnapshotByDate(mockDate)).thenReturn(null);

        try (MockedStatic<DateReader> dateReaderMockedStatic = Mockito.mockStatic(DateReader.class)) {
            dateReaderMockedStatic.when(() -> DateReader.extractDateFromFileName("20231001_test.csv")).thenReturn(mockDate);

            CsvProcessor.processCsvFile(mockSemester, mockFile);

            verify(mockSemester, times(1)).addSnapshot(any(Snapshot.class));
        }
    }

    @Test
    public void testIsFileWithinDateRange() {
        when(mockSemester.getPreRegDate()).thenReturn(LocalDate.of(2023, 9, 1));
        when(mockSemester.getAddDeadline()).thenReturn(LocalDate.of(2023, 11, 1));

        boolean result = CsvProcessor.isFileWithinDateRange(mockDate, mockSemester);
        assertTrue(result);

        result = CsvProcessor.isFileWithinDateRange(LocalDate.of(2023, 8, 1), mockSemester);
        assertFalse(result);
    }

    @Test
    public void testGetOrCreateSnapshot() {
        when(mockSemester.getSnapshotByDate(mockDate)).thenReturn(null);

        Snapshot snapshot = CsvProcessor.getOrCreateSnapshot(mockSemester, mockDate);

        assertNotNull(snapshot);
        verify(mockSemester, times(1)).addSnapshot(snapshot);
    }

    @Test
    public void testCreateHeaderIndexMap() {
        String[] headers = {"SUBJ", "CRSE", "CRN"};
        Map<String, Integer> headerIndexMap = CsvProcessor.createHeaderIndexMap(headers);

        assertEquals(3, headerIndexMap.size());
        assertEquals(0, headerIndexMap.get("SUBJ").intValue());
        assertEquals(1, headerIndexMap.get("CRSE").intValue());
        assertEquals(2, headerIndexMap.get("CRN").intValue());
    }

    @Test
    public void testGetFieldValue() {
        String[] fields = {"CS", "350", "12345"};
        Map<String, Integer> headerIndexMap = new HashMap<>();
        headerIndexMap.put("SUBJ", 0);
        headerIndexMap.put("CRSE", 1);
        headerIndexMap.put("CRN", 2);

        String result = CsvProcessor.getFieldValue(fields, headerIndexMap, "CRSE", "default");
        assertEquals("350", result);

        result = CsvProcessor.getFieldValue(fields, headerIndexMap, "XLST GROUP", "default");
        assertEquals("default", result);
    }

    @Test
    public void testGetHeaderIndex() {
        Map<String, Integer> headerIndexMap = new HashMap<>();
        headerIndexMap.put("SUBJ", 0);
        headerIndexMap.put("CRSE", 1);

        int index = CsvProcessor.getHeaderIndex(headerIndexMap, "CRSE");
        assertEquals(1, index);

        index = CsvProcessor.getHeaderIndex(headerIndexMap, "CRN");
        assertEquals(-1, index);
    }
}