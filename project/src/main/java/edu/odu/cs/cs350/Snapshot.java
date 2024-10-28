package edu.odu.cs.cs350;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Snapshot {
    private LocalDate date;
    private EnrollmentSnapshotData enrollmentData;
    private TreeMap<String, Course> courses;
    private String filename; // Name of the file that the snapshot was created from

    public Snapshot(LocalDate date, EnrollmentSnapshotData enrollmentData) {
        this.date = date;
        this.enrollmentData = enrollmentData;
        this.courses = new TreeMap<>();
    }

    public LocalDate getDate() {
        return date;
    }

    public EnrollmentSnapshotData getEnrollmentData() {
        return enrollmentData;
    }

    public void addCourse(Course course) {
        this.courses.put(course.getCourseName(), course);
    }

    public Course getCourseByName(String courseName) {
        return courses.get(courseName);
    }

    public void printCourses() {
        System.out.println("Date: " + date);
        System.out.println("Enrollment Data: " + enrollmentData);
        System.out.println("Courses:");
        for (Course course : courses.values()) {
            System.out.println(course);
        }
    }

    public Iterable<Course> getCourses() {
        return courses.values();
    }
}
