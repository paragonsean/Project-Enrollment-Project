package edu.odu.cs.cs350;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;






public class DateReaderTest {

    private DateReader dateReader;

    @BeforeEach
    public void setUp() {
        dateReader = new DateReader();
    }

    @Test
    public void testGetRegistrationDates(@TempDir Path tempDir) throws IOException {
        Path datesFile = tempDir.resolve("dates.txt");
        Files.write(datesFile, "2023-01-15\n2023-02-01".getBytes());

        LocalDate[] dates = dateReader.getRegistrationDates(tempDir);

        assertThat(dates, arrayWithSize(2));
        assertThat(dates[0], is(LocalDate.of(2023, 1, 15)));
        assertThat(dates[1], is(LocalDate.of(2023, 2, 1)));
    }

    @Test
    public void testGetRegistrationDatesMissingFile(@TempDir Path tempDir) {
        IOException exception = assertThrows(IOException.class, () -> {
            dateReader.getRegistrationDates(tempDir);
        });

        assertThat(exception.getMessage(), containsString("Missing dates.txt"));
    }

    @Test
    public void testGetRegistrationDatesInvalidFormat(@TempDir Path tempDir) throws IOException {
        Path datesFile = tempDir.resolve("dates.txt");
        Files.write(datesFile, "invalid-date\n2023-02-01".getBytes());

        IOException exception = assertThrows(IOException.class, () -> {
            dateReader.getRegistrationDates(tempDir);
        });

        assertThat(exception.getMessage(), containsString("Invalid date format"));
    }

    @Test
    public void testExtractDatesFromCsvFiles(@TempDir Path tempDir) throws IOException {
        Files.createFile(tempDir.resolve("2023-01-15.csv"));
        Files.createFile(tempDir.resolve("2023-02-01.csv"));

        LocalDate[] dates = dateReader.extractDatesFromCsvFiles(tempDir);

        assertThat(dates, arrayWithSize(2));
        assertThat(dates[0], is(LocalDate.of(2023, 1, 15)));
        assertThat(dates[1], is(LocalDate.of(2023, 2, 1)));
    }

    @Test
    public void testExtractDatesFromCsvFilesNoCsvFiles(@TempDir Path tempDir) {
        IOException exception = assertThrows(IOException.class, () -> {
            dateReader.extractDatesFromCsvFiles(tempDir);
        });

        assertThat(exception.getMessage(), containsString("No CSV files found"));
    }

    @Test
    public void testParsePreviousSemesterCode() {
        String previousSemester = dateReader.parsePreviousSemesterCode("202310");
        assertThat(previousSemester, is("202230"));

        previousSemester = dateReader.parsePreviousSemesterCode("202210");
        assertThat(previousSemester, is("202130"));
    }

    @Test
    public void testParseNextSemesterCode() {
        String nextSemester = dateReader.parseNextSemesterCode("202230");
        assertThat(nextSemester, is("202310"));

        nextSemester = dateReader.parseNextSemesterCode("202130");
        assertThat(nextSemester, is("202210"));
    }

    @Test
    public void testParseYearFromSemesterCode() {
        int year = dateReader.parseYearFromSemesterCode("202310");
        assertThat(year, is(2023));
    }

    @Test
    public void testParseTermFromSemesterCode() {
        int term = dateReader.parseTermFromSemesterCode("202310");
        assertThat(term, is(10));
    }

    @Test
    public void testParseYearFromSemesterCodeInvalidFormat() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            dateReader.parseYearFromSemesterCode("invalid");
        });

        assertThat(exception.getMessage(), containsString("Invalid semester code format"));
    }

    @Test
    public void testParseTermFromSemesterCodeInvalidFormat() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            dateReader.parseTermFromSemesterCode("invalid");
        });

        assertThat(exception.getMessage(), containsString("Invalid semester code format"));
    }
}