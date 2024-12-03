package edu.odu.cs.cs350;

import java.io.File;
import java.io.IOException;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class History {

    private static final Logger logger = LoggerFactory.getLogger(History.class);
    private final NavigableSet<Semester> semesters;
  

    // Constructor for loading semesters from a root directory
    public History(String rootDirectoryPath) throws IOException {
        this.semesters = new TreeSet<>(Comparator.comparing(Semester::getAddDeadline));
      
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
    public List<File> convertStringToFiles(String directory) {
        List<File> files = new ArrayList<>();
        File dir = new File(directory);
        if (dir.exists() && dir.isDirectory()) {
            File[] fileArray = dir.listFiles();
            if (fileArray != null) {
                files.addAll(Arrays.asList(fileArray));
            }
        }
        return files;
    }
    // Load semesters from the root directory
    public void loadSemestersFromRootDirectory(String rootDirectoryPath) throws IOException {
        List<File> directories = convertStringToFiles(rootDirectoryPath);
        for (File directory : directories) {
            if (!directory.isDirectory()) {
                logger.warn("Skipping non-directory file: {}", directory);
                continue;
            }
            if (directory.canRead()) {
                if (directory.listFiles().length < 2) {
                    logger.warn("Skipping empty directory: {}", directory);
                    continue;
                }
            File[] files = directory.listFiles();
            boolean containsDatesTxt = Arrays.stream(files).anyMatch(file -> file.getName().equals("dates.txt"));
            if (files.length > 2 && !containsDatesTxt) {
                    Semester semester = new Semester(directory.getAbsolutePath());
                    addSemester(semester);
                }
            if(directory.isDirectory()){
                loadSemestersFromRootDirectory(directory.toString());
            }
        }
        logger.info("Loaded {} semesters from directories.", semesters.size());
    }

}}


