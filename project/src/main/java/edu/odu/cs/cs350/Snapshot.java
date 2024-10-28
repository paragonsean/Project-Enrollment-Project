// package edu.odu.cs.cs350;

// import lombok.Data;
// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.EqualsAndHashCode;
// import java.io.File;
// import java.io.IOException;
// import java.time.LocalDate;
// import java.util.*;
// import java.util.Iterator;

// /**
//  * The Snapshot class represents a snapshot of courses at a specific date.
//  * It implements the Iterable interface to allow iteration over the courses.
//  * An instance of Snapshot contains a date and a collection of Course objects
//  * stored in a TreeMap, which ensures that the courses are sorted by their names.
//  */
// @Data
// @AllArgsConstructor
// @Builder
// @EqualsAndHashCode
// public class Snapshot implements Iterable<Course> {
//     private LocalDate date;  // Date of the snapshot in YYYY-MM-DD format
//     private String fileName; // Name of the CSV file 
//     @Builder.Default // Default for courses TreeMap
//     private Map<String, Courses> courses = new TreeMap<>();

//     private final DateReader dateReader = new DateReader(); // Instance of DateReader to parse dates

//     // Primary constructor with file and date
//     public Snapshot(File file, LocalDate date) {
//         this.file = file;
//         this.date = date;
//         this.courses = new TreeMap<String, Course>();
//     }

//     // Default constructor chaining to primary
//     public Snapshot() {
//         this(null, null);
//     }

//     // Date-only constructor chaining to primary
//     public Snapshot(LocalDate date) {
//         this(null, date);
//     }

//     /**
//      * Deep copy constructor that duplicates the file, date, and courses.
//      *
//      * @param other Snapshot to copy
//      */
//     public Snapshot(Snapshot other) {
//         this(other.file, other.date);
//         this.courses = new TreeMap<>();
//         for (Map.Entry<String, Course> entry : other.courses.entrySet()) {
//             Course originalCourse = entry.getValue();
//             // Assuming Course has subject and courseNumber fields
//             this.courses.put(entry.getKey(), new Course(originalCourse.getSubject(), originalCourse.getCourseNumber()));
//         }
//     }

//     /**
//      * Constructor that sets the file and uses it to set the snapshot date.
//      * 
//      * @param file File representing the CSV file for the snapshot
//      * @throws IOException if there is an error parsing the date from the file name
//      */
//     public Snapshot(File file) throws IOException {
//         this(file, null);
//         setDateFromCsvFile(file);  // Set the date using DateReader
//     }

//     /**
//      * Adds a course to the collection of courses.
//      *
//      * @param course the Course object to be added
//      */
//     public void addCourse(Course course) {
//         courses.put(course.getCourseName(), course);
//     }

//     /**
//      * Uses DateReader to set the date from the CSV file name.
//      * 
//      * @param csvFile File representing the CSV file
//      * @throws IOException if there is an error extracting the date
//      */
//     private void setDateFromCsvFile(File csvFile) throws IOException {
//         this.date = dateReader.extractDateFromCsvFileName(csvFile.toPath());
//     }

//     /**
//      * Retrieves the collection of Course objects.
//      *
//      * @return a Collection of Course objects from the TreeMap.
//      */
//     public Collection<Course> getCourses() {
//         return courses.values();
//     }

//     /**
//      * Retrieves a Course object from the collection based on the provided course name.
//      *
//      * @param courseName the name of the course to retrieve
//      * @return the Course object associated with the given course name, or null if no such course exists
//      */
//     public Course getCourseByName(String courseName) {
//         return courses.get(courseName);
//     }

//     /**
//      * Retrieves the set of course names.
//      *
//      * @return a set containing the names of all courses in sorted order.
//      */
//     public Set<String> getCourseNames() {
//         return courses.keySet();
//     }

//     /**
//      * Returns an iterator over the elements in this collection.
//      * 
//      * @return an Iterator over the courses in this Snapshot.
//      */
//     @Override
//     public Iterator<Course> iterator() {
//         return courses.values().iterator();
//     }
// }
