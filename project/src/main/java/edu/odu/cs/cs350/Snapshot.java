package edu.odu.cs.cs350;

import java.util.ArrayList;
import java.util.List;

/**
 * A snapshot has a date.
 */
public class Snapshot {
    private String date;
    private List<Course> courses;

    /**
     * Create a new snapshot.
     * @param date the date of the snapshot
     */
    public Snapshot(String date) {
        this.date = date;
        this.courses = new ArrayList<>();
    }

    /**
     * Get the date of this snapshot.
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * Add a course to this snapshot.
     * @param course the course to add
     */
    public void addCourse(Course course) {
        courses.add(course);
    }

    /**
     * Get the list of courses for this snapshot.
     * @return a list of courses
     */
    public List<Course> getCourses() {
        return new ArrayList<>(courses);
    }
}
