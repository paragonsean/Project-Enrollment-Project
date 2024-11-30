package edu.odu.cs.cs350;

import java.io.File;
import java.io.IOException;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class History {

    private static final Logger logger = LoggerFactory.getLogger(History.class);
    private final NavigableSet<Semester> semesters;
    private final FileProcessor fileProcessor;

    // Constructor for loading semesters from a root directory
    public History(String rootDirectoryPath) throws IOException {
        this.semesters = new TreeSet<>(Comparator.comparing(Semester::getAddDeadline));
        this.fileProcessor = new FileProcessor();
        loadSemestersFromRootDirectory(rootDirectoryPath);
    }

    // Add a semester to the history
    public void addSemester(Semester semester) {
        if (semester == null) {
            throw new IllegalArgumentException("Semester cannot be null.");
        }
        if (semesters.contains(semester)) {
            logger.warn("Duplicate semester detected: {}. Updating existing entry.", semester);
            semesters.remove(semester);
        }

        semesters.add(semester);
        logger.info("Added semester: {}", semester);
    }

    // Get all semesters in the history
    public List<Semester> getSemesters() {
        return new ArrayList<>(semesters);
    }

    // Load semesters from the root directory
    public void loadSemestersFromRootDirectory(String rootDirectoryPath) throws IOException {
        List<File> directories = fileProcessor.convertStringToFiles(rootDirectoryPath);
        List<Semester> loadedSemesters = fileProcessor.loadSemestersFromDirectoriesWithCutoff(directories, Optional.empty());
        for (Semester semester : loadedSemesters) {
            addSemester(semester);
        }
        logger.info("Loaded {} semesters from directories.", semesters.size());
    }
}
