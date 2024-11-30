package edu.odu.cs.cs350;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class FileProcessorTest {

    private FileProcessor fileProcessor;

    @BeforeEach
    public void setUp() {
        fileProcessor = new FileProcessor();
    }

    @Test
    public void testConvertStringToFiles() throws IOException {
        String directoryPath = "src/test/resources/testDirectory";
        File directory = new File(directoryPath);
        directory.mkdirs();

        new File(directory, "file1.csv").createNewFile();
        new File(directory, "file2.csv").createNewFile();

        List<File> files = fileProcessor.convertStringToFiles(directoryPath);

        assertThat(files, hasSize(2));
        assertThat(files, containsInAnyOrder(
                new File(directoryPath + "/file1.csv"),
                new File(directoryPath + "/file2.csv")
        ));

        // Clean up
        for (File file : files) {
            file.delete();
        }
        directory.delete();
    }

    @Test
    public void testLoadSemestersFromDirectories() throws IOException {
        String directoryPath = "src/test/resources/testSemesterDirectory";
        File directory = new File(directoryPath);
        directory.mkdirs();

        File datesFile = new File(directory, "dates.txt");
        try (var writer = new java.io.FileWriter(datesFile)) {
            writer.write("2023-01-01\n2023-05-01\n");
        }

        List<File> directories = List.of(directory);

        List<Semester> semesters = fileProcessor.loadSemestersFromDirectories(directories);

        assertThat(semesters, hasSize(1));
        assertThat(semesters.get(0).getName(), is("testSemesterDirectory"));

        // Clean up
        datesFile.delete();
        directory.delete();
    }

    @Test
    public void testProcessDirectory() throws IOException {
        String directoryPath = "src/test/resources/testProcessDirectory";
        File directory = new File(directoryPath);
        directory.mkdirs();

        File datesFile = new File(directory, "dates.txt");
        try (var writer = new java.io.FileWriter(datesFile)) {
            writer.write("2023-01-01\n2023-05-01\n");
        }

        Semester semester = fileProcessor.processDirectory(directory, Optional.empty());

        assertNotNull(semester);
        assertEquals("testProcessDirectory", semester.getName());

        // Clean up
        datesFile.delete();
        directory.delete();
    }

    @Test
    public void testFilterCsvFilesByDate() throws IOException {
        String directoryPath = "src/test/resources/testFilterDirectory";
        File directory = new File(directoryPath);
        directory.mkdirs();

        File file1 = new File(directory, "2023-01-01.csv");
        File file2 = new File(directory, "2023-01-02.csv");
        File invalidFile = new File(directory, "invalid.csv");

        file1.createNewFile();
        file2.createNewFile();
        invalidFile.createNewFile();

        LocalDate preRegDate = LocalDate.of(2023, 1, 1);
        LocalDate addDeadline = LocalDate.of(2023, 1, 3);
        Optional<LocalDate> cutoffDate = Optional.of(LocalDate.of(2023, 1, 2));

        List<File> filteredFiles = fileProcessor.filterCsvFilesByDate(directory, preRegDate, addDeadline, cutoffDate);

        assertThat(filteredFiles, hasSize(2));
        assertThat(filteredFiles, containsInAnyOrder(file1, file2));

        // Clean up
        file1.delete();
        file2.delete();
        invalidFile.delete();
        directory.delete();
    }

    @Test
    public void testLoadSemestersFromDirectoriesWithCutoff() throws IOException {
        String directoryPath = "src/test/resources/testLoadWithCutoff";
        File directory = new File(directoryPath);
        directory.mkdirs();

        File datesFile = new File(directory, "dates.txt");
        try (var writer = new java.io.FileWriter(datesFile)) {
            writer.write("2023-01-01\n2023-05-01\n");
        }

        List<File> directories = List.of(directory);
        Optional<LocalDate> cutoffDate = Optional.of(LocalDate.of(2023, 4, 1));

        List<Semester> semesters = fileProcessor.loadSemestersFromDirectoriesWithCutoff(directories, cutoffDate);

        assertThat(semesters, hasSize(1));
        assertThat(semesters.get(0).getName(), is("testLoadWithCutoff"));

        // Clean up
        datesFile.delete();
        directory.delete();
    }
}
