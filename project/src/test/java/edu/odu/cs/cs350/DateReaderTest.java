package edu.odu.cs.cs350;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class DateReaderTest {

    @TempDir
    Path tempDir;
    Path datesFile;

    @BeforeEach
    public void setup() throws IOException {
        datesFile = tempDir.resolve("dates.txt");
        Files.write(datesFile, "2023-08-15\n2023-09-01\n".getBytes());
    }

    @Test
    public void testInitializeSuccessfully() {
        assertDoesNotThrow(() -> {
            DateReader dateReader = new DateReader(tempDir.toString());
        });
    }

    @Test
    public void testGetDeadlineDate() throws IOException {
        DateReader dateReader = new DateReader(tempDir.toString());
        LocalDate expectedAddDeadlineDate = LocalDate.of(2023, 9, 1);
        assertEquals(expectedAddDeadlineDate, dateReader.getDeadlineDate());
    }

    @Test
    public void testGetPreregistrationDate() throws IOException {
        DateReader dateReader = new DateReader(tempDir.toString());
        LocalDate expectedPreregistrationDate = LocalDate.of(2023, 8, 15);
        assertEquals(expectedPreregistrationDate, dateReader.getPreregistrationDate());
    }

    @Test
    public void testInvalidDateFormatInDatesFile() throws IOException {
        Files.write(datesFile, "invalid-date\n2023-09-01\n".getBytes());
        Exception exception = assertThrows(RuntimeException.class, () -> {
            new DateReader(tempDir.toString());
        });
        assertTrue(exception.getMessage().contains("Failed to initialize DateReader"));
    }

    @Test
    public void testConvertStringToDate() {
        String dateString = "2024-11-10";
        String invalidDate = "11-10-2024";
        LocalDate expectedDate = LocalDate.of(2024, 11, 10);
        String expectedMessage = "Invalid date format: 11-10-2024. Expected format: yyyy-MM-dd";
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            DateReader.convertStringToDate(invalidDate);
        });
        assertEquals(expectedDate, DateReader.convertStringToDate(dateString));
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testExtractDateFromFileName() {
        String fileName = "2024-11-10.csv";
        String invalidFile = "11-10-2024.csv";
        LocalDate expectedDate = LocalDate.of(2024, 11, 10);
        String expectedMessage = "Invalid date format in file name: 11-10-2024.csv";
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            DateReader.extractDateFromFileName(invalidFile);
        });
        assertEquals(expectedDate, DateReader.extractDateFromFileName(fileName));
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testMissingDatesFile() throws IOException {
        DateReader dateReader = new DateReader("src/test/data/sampleHistory2/202410");
        Path datesFilePath = Path.of("src/test/data/sampleHistory2/202410/dates.txt");
        Files.delete(datesFilePath);
        String expectedMessage = "Missing dates.txt in semester directory: 202410";
        Exception exception = assertThrows(IOException.class, () -> {
            dateReader.loadDates();
        });
        assertEquals(expectedMessage, exception.getMessage());
        Files.write(datesFilePath, "2024-03-25\n2024-09-03".getBytes());
    }
}