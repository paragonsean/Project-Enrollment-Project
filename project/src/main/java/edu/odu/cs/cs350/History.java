package edu.odu.cs.cs350;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class History {

    private static final Logger logger = LoggerFactory.getLogger(History.class);
    private final NavigableSet<Semester> semesters;

    public History() {
        // TreeSet ensures the semesters are sorted by addDeadline
        this.semesters = new TreeSet<>(Comparator.comparing(Semester::getAddDeadline));
    }

    /**
     * Add a semester to the history.
     *
     * @param semester The semester to add.
     */
    public void addSemester(Semester semester) {
        if (semester == null) {
            throw new IllegalArgumentException("Semester cannot be null.");
        }
        boolean added = semesters.add(semester);
        if (added) {
            logger.info("Added semester: {}", semester.getName());
        } else {
            logger.warn("Semester already exists and was not added: {}", semester.getName());
        }
    }

    /**
     * Retrieve a semester by its name.
     *
     * @param semesterName The name of the semester.
     * @return An Optional containing the Semester if found, otherwise empty.
     */
    public Optional<Semester> getSemester(String semesterName) {
        return semesters.stream()
                .filter(semester -> semester.getName().equalsIgnoreCase(semesterName))
                .findFirst();
    }

    /**
     * Retrieve all semesters.
     *
     * @return A list of all semesters, sorted by their addDeadline.
     */
    public List<Semester> getSemesters() {
        return new ArrayList<>(semesters);
    }

    /**
     * Load semesters from a list of directories.
     *
     * @param directories List of directories containing semester data.
     * @param cutoffDate  Optional cutoff date for filtering data.
     * @throws IOException If an error occurs during loading.
     */
    public void loadSemestersFromDirectories(List<File> directories, Optional<LocalDate> cutoffDate) throws IOException {
        FileProcessor processor = new FileProcessor();
        List<Semester> loadedSemesters = processor.loadSemestersFromDirectories(directories, cutoffDate);
        loadedSemesters.forEach(this::addSemester);
        logger.info("Loaded {} semesters from directories.", loadedSemesters.size());
    }

    /**
     * Extract all historical semesters (excluding the most recent).
     *
     * @return A list of historical semesters.
     */
    public List<Semester> extractLastHistoricalSemesters() {
        if (semesters.isEmpty()) {
            logger.warn("No historical semesters available.");
            return Collections.emptyList();
        }

        return new ArrayList<>(semesters.headSet(semesters.last(), false));
    }

    /**
     * Get the most recent semester.
     *
     * @return An Optional containing the most recent Semester, or empty if no semesters exist.
     */
    public Optional<Semester> getMostRecentSemester() {
        return Optional.ofNullable(semesters.isEmpty() ? null : semesters.last());
    }
}
