package edu.odu.cs.cs350;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Snapshot implements Iterable<Course> {
    private static final Logger logger = Logger.getLogger(Snapshot.class.getName());

    private String filename;  // Filename in the format 'yyyy-MM-dd.csv'
    private LocalDate snapshotDate;  // Snapshot date
    private Map<String, Course> coursesByKey;  // Courses for this snapshot, stored by course key

    // Date format for verifying file names
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final Pattern DATE_PATTERN = Pattern.compile("(\\d{4}-\\d{2}-\\d{2})");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Default constructor
    public Snapshot() {
        this.filename = "00000000.csv";
        this.snapshotDate = LocalDate.now();
        this.coursesByKey = new HashMap<>();
    }

    // Parameterized constructor that attempts to parse the date from the filename
    public Snapshot(String filename, List<Course> coursesInSemester) {
        this.filename = filename;
        this.snapshotDate = extractDateFromFilename(filename, DATE_PATTERN, DATE_FORMATTER);
        if (this.snapshotDate == null) {
            throw new IllegalArgumentException("Invalid filename format. Date not found in 'yyyy-MM-dd.csv' format.");
        }
        this.coursesByKey = new HashMap<>();
        for (Course course : coursesInSemester) {
            addCourse(course);
        }
    }

    // Parameterized constructor with a known date
    public Snapshot(LocalDate date, List<Course> coursesInSemester) {
        this.snapshotDate = date;
        this.coursesByKey = new HashMap<>();
        for (Course course : coursesInSemester) {
            addCourse(course);
        }
    }

    // Method to add a course to the snapshot
    public void addCourse(Course course) {
        String courseKey = course.getCourseKey();
        if (coursesByKey.containsKey(courseKey)) {
            coursesByKey.get(courseKey).mergeCourse(course);
        } else {
            coursesByKey.put(courseKey, course);
        }
    }

    // Method to get a course by its course code (CRSE)
    public Course getCourse(String courseCode) {
        return coursesByKey.get(courseCode);
    }

    // Get total enrollments for all courses in this snapshot
    public Map<String, Integer> getCourseEnrollments() {
        Map<String, Integer> courseEnrollments = new HashMap<>();
        for (Map.Entry<String, Course> entry : coursesByKey.entrySet()) {
            String courseKey = entry.getKey();
            Course course = entry.getValue();
            int totalEnrollment = course.getTotalOfferingEnrollment(); // Assuming Course class has this method
            courseEnrollments.put(courseKey, totalEnrollment);
        }
        return courseEnrollments;
    }

   
    // Extracts date from filename using regex pattern and formatter
    private LocalDate extractDateFromFilename(String filename, Pattern datePattern, DateTimeFormatter formatter) {
        Matcher matcher = datePattern.matcher(filename);
        if (matcher.find()) {
            try {
                return LocalDate.parse(matcher.group(1), formatter);
            } catch (DateTimeParseException e) {
                logger.log(Level.WARNING, "Error parsing date from filename {0}: {1}", new Object[]{filename, e.getMessage()});
            }
        }
        return null;
    }

    // Getters for filename and date
    public String getFileName() {
        return this.filename;
    }

    public Map<String, Course> getCourses() {
        return this.coursesByKey;
    }

    @Override
    public Iterator<Course> iterator() {
        return coursesByKey.values().iterator();
    }
    
    public LocalDate getDate() {
        return this.snapshotDate;
    }

    // toString method to provide a human-readable representation of the snapshot
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Snapshot Filename: " + filename + "\nDate: " + snapshotDate + "\nCourses:\n");
        for (Course course : coursesByKey.values()) {
            sb.append(course.toString()).append("\n");
        }
        return sb.toString();
    }
}
