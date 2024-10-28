package edu.odu.cs.cs350;
import java.util.Map;
import java.util.TreeMap;

public class Course implements Comparable<Course> {
    private String courseId;
    private String courseName;
    private Map<String, Offering> offerings; // Map of sections in the offering, keyed by CRN
    public Course(String subj, String crse) {
        this.courseId = subj;
        this.courseName = crse;
        this.offerings = new TreeMap<>();
        
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    @Override
    public String toString() {
        return "Course ID: " + courseId + ", Course Name: " + courseName;
    }

    @Override
    public int compareTo(Course other) {
        return this.courseName.compareTo(other.courseName);
    }
}
