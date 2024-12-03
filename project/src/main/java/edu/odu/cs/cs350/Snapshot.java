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

    private String filename; // Filename in the format 'yyyy-MM-dd.csv'
    private LocalDate snapshotDate; // Snapshot date
    private Map<String, Course> coursesByKey; // Courses for this snapshot, stored by course key

    /**
     * A date format instance used to format dates in the "yyyy-MM-dd" pattern.
     * This is a thread-safe, immutable, and static instance of SimpleDateFormat.
     */
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * A regular expression pattern to match dates in the format YYYY-MM-DD.
     * The pattern expects four digits for the year, followed by a hyphen,
     * two digits for the month, another hyphen, and two digits for the day.
     */
    private static final Pattern DATE_PATTERN = Pattern.compile("(\\d{4}-\\d{2}-\\d{2})");

    /**
     * A DateTimeFormatter instance that formats dates in the "yyyy-MM-dd" pattern.
     * This formatter is used to ensure that dates are consistently formatted
     * throughout the application.
     */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Constructs a new Snapshot object with default values.
     * The filename is set to "00000000.csv", the snapshot date is set to the
     * current date,
     * and an empty HashMap is initialized for coursesByKey.
     */
    public Snapshot() {
        this.filename = "00000000.csv";
        this.snapshotDate = LocalDate.now();
        this.coursesByKey = new HashMap<>();
    }

    /**
     * Constructs a Snapshot object with the specified filename and list of courses.
     *
     * @param filename          the name of the file containing the snapshot data,
     *                          expected to be in 'yyyy-MM-dd.csv' format
     * @param coursesInSemester the list of courses in the semester to be included
     *                          in the snapshot
     * @throws IllegalArgumentException if the filename does not contain a valid
     *                                  date in 'yyyy-MM-dd.csv' format
     */
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

    /**
     * Constructs a Snapshot object with the specified date and list of courses.
     *
     * @param date              the date of the snapshot
     * @param coursesInSemester the list of courses in the semester
     */
    public Snapshot(LocalDate date, List<Course> coursesInSemester) {
        this.snapshotDate = date;
        this.coursesByKey = new HashMap<>();
        for (Course course : coursesInSemester) {
            addCourse(course);
        }
    }

    /**
     * Adds a course to the collection of courses. If a course with the same key
     * already exists, it merges the new course with the existing one.
     *
     * @param course the course to be added or merged
     */
    public void addCourse(Course course) {
        String courseKey = course.getCourseKey();
        if (coursesByKey.containsKey(courseKey)) {
            coursesByKey.get(courseKey).mergeCourse(course);
        } else {
            coursesByKey.put(courseKey, course);
        }
    }

    /**
     * Retrieves the Course object associated with the specified course code.
     *
     * @param courseCode the code of the course to retrieve
     * @return the Course object corresponding to the given course code,
     *         or null if no course with the specified code exists
     */
    public Course getCourse(String courseCode) {
        return coursesByKey.get(courseCode);
    }

    /**
     * Get total enrollments for all courses in this snapshot.
     *
     * @return A map of course keys to their total enrollments.
     *         If totalEnrollment > totalSectionEnrollment, totalEnrollment is used.
     */
    public Map<String, Integer> getCourseEnrollments() {
        Map<String, Integer> courseEnrollments = new HashMap<>();

        for (Map.Entry<String, Course> entry : coursesByKey.entrySet()) {
            String courseKey = entry.getKey();
            Course course = entry.getValue();

            // Assuming Course class has these methods
            int totalEnrollment = course.getTotalOfferingEnrollment();
            int totalSectionEnrollment = course.getTotalSectionEnrollment();

            // Add course to map based on condition
            courseEnrollments.put(courseKey, Math.max(totalEnrollment, totalSectionEnrollment));
        }

        return courseEnrollments;
    }

    /**
     * Retrieves the enrollment number for a specified course.
     *
     * @param courseKey the unique key identifying the course
     * @return the maximum of the total offering enrollment or total section enrollment for the course
     * @throws IllegalArgumentException if the course with the specified key does not exist
     */
    public int getCourseEnrollment(String courseKey) {
        Course course = coursesByKey.get(courseKey);

        if (course == null) {
            throw new IllegalArgumentException("Course with key " + courseKey + " does not exist.");
        }

        // Assuming Course class has these methods
        int totalEnrollment = course.getTotalOfferingEnrollment();
        int totalSectionEnrollment = course.getTotalSectionEnrollment();

        // Return the maximum of total enrollment or total section enrollment
        return Math.max(totalEnrollment, totalSectionEnrollment);
    }

    /**
     * Retrieves the capacity of a course identified by the given course key.
     * 
     * This method first attempts to find the course using the provided course key.
     * If the course is not found, an IllegalArgumentException is thrown.
     * It then calculates the total offering capacity of the course. If the total
     * offering capacity is zero, it falls back to the total section capacity.
     * 
     * @param courseKey the key identifying the course
     * @return the capacity of the course, which is the maximum of the total
     *         offering
     *         capacity or the total section capacity
     * @throws IllegalArgumentException if the course with the specified key does
     *                                  not exist
     */
    public int getCourseCapacity(String courseKey) {
        Course course = coursesByKey.get(courseKey);

        if (course == null) {
            throw new IllegalArgumentException("Course with key " + courseKey + " does not exist.");
        }

        // Assuming Course class has these methods
        int totalOfferingCapacity = course.getTotalOfferingCapacity();
        if (totalOfferingCapacity == 0) {
            totalOfferingCapacity = course.getTotalSectionCapacity();
        }
        // Return the maximum of total offering capacity or total section capacity
        return totalOfferingCapacity;
    }

    /**
     * Checks if a course with the specified key exists in the collection.
     *
     * @param courseKey the key of the course to check for
     * @return true if the course exists, false otherwise
     */
    public boolean hasCourse(String courseKey) {
        return coursesByKey.containsKey(courseKey);
    }

    /**
     * Extracts a date from the given filename using the specified date pattern and formatter.
     *
     * @param filename the filename from which to extract the date
     * @param datePattern the pattern to use for matching the date in the filename
     * @param formatter the formatter to use for parsing the date
     * @return the extracted date as a LocalDate, or null if the date could not be parsed
     */
    private LocalDate extractDateFromFilename(String filename, Pattern datePattern, DateTimeFormatter formatter) {
        Matcher matcher = datePattern.matcher(filename);
        if (matcher.find()) {
            try {
                return LocalDate.parse(matcher.group(1), formatter);
            } catch (DateTimeParseException e) {
                logger.log(Level.WARNING, "Error parsing date from filename {0}: {1}",
                        new Object[] { filename, e.getMessage() });
            }
        }
        return null;
    }

    /**
     * Retrieves the name of the file.
     *
     * @return the filename as a String
     */
    public String getFileName() {
        return this.filename;
    }

  

    /**
     * Returns an iterator over elements of type {@code Course}.
     * This iterator allows traversal of the courses contained in this snapshot.
     *
     * @return an {@code Iterator<Course>} over the courses in this snapshot
     */
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
        StringBuilder sb = new StringBuilder(
                "Snapshot Filename: " + filename + "\nDate: " + snapshotDate + "\nCourses:\n");
        for (Course course : coursesByKey.values()) {
            sb.append(course.toString()).append("\n");
        }
        return sb.toString();
    }
}
