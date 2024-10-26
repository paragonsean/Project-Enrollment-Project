package edu.odu.cs.cs350;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import  java.util.Iterator;
/**
 * The Snapshot class represents a snapshot of courses at a specific date.
 * It implements the Iterable interface to allow iteration over the courses.
 * 
 * An instance of Snapshot contains a date and a collection of Course objects
 * stored in a TreeMap, which ensures that the courses are sorted by their names.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Snapshot implements Iterable<Course> {
    private LocalDate date;  // Date of the snapshot in YYYY-MM-DD format
    private File file;  
    @Builder.Default     // File representing the CSV file for this snapshot
    private Map<String, Course> courses = new TreeMap<>();  // TreeMap of Course objects, keyed by course name

    private final DateReader dateReader = new DateReader(); // Instance of DateReader to parse dates

    public Snapshot(File file, LocalDate date) {
        this.file = file;
        this.date = date;
        this.courses = new TreeMap<>();
    }


    // Copy constructor, if you need to duplicate a Snapshot instance
    public Snapshot(Snapshot other) {
        this(other.file, other.date);
        this.courses = new TreeMap<>(other.courses); // Deep copy if necessary
    }

    /**
     * Constructor that sets the file and uses it to set the snapshot date.
     * 
     * @param file File representing the CSV file for the snapshot
     * @throws IOException if there is an error parsing the date from the file name
     */
    public Snapshot(File file) throws IOException {
        this.file = file;
        this.courses = new TreeMap<>();
        setDateFromCsvFile(file);  // Set the date using DateReader
    }

    /**
     * Adds a course to the collection of courses.
     *
     * @param course the Course object to be added
     */
    public void addCourse(Course course) {
        courses.put(course.getCourseName(), course);
    }

    /**
     * Uses DateReader to set the date from the CSV file name.
     * 
     * @param csvFile File representing the CSV file
     * @throws IOException if there is an error extracting the date
     */
    private void setDateFromCsvFile(File csvFile) throws IOException {
        this.date = dateReader.extractDateFromCsvFileName(csvFile.toPath());
    }

    /**
     * Retrieves the collection of Course objects.
     *
     * @return a Collection of Course objects from the TreeMap.
     */
    public Collection<Course> getCourses() {
        return courses.values();  // Returns the Collection of Course objects from the TreeMap
    }

    /**
     * Retrieves a Course object from the collection based on the provided course name.
     *
     * @param courseName the name of the course to retrieve
     * @return the Course object associated with the given course name, or null if no such course exists
     */
    public Course getCourseByName(String courseName) {
        return courses.get(courseName);
    }

    /**
     * Retrieves the set of course names.
     *
     * @return a set containing the names of all courses in sorted order.
     */
    public Set<String> getCourseNames() {
        return courses.keySet();  // Returns the set of keys (course names) in sorted order
    }

    /**
     * Returns an iterator over the elements in this collection.
     * 
     * @return an Iterator over the courses in this Snapshot.
     */
    @Override
    public Iterator<Course> iterator() {
        return courses.values().iterator();
    }
}