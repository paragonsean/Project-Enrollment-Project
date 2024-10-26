package edu.odu.cs.cs350;

import java.util.ArrayList;
import java.util.List;

/**
 * A course has a subject and course number.
 */
public class Course {
    private String subject;
    private String courseNumber;
    private List<Offering> offerings;

    /**
     * Create a new course.
     * @param subject the subject of the course
     * @param courseNumber the course number of the course
     */
    public Course(String subject,
                  String courseNumber) {
        this.subject = subject;
        this.courseNumber = courseNumber;
        this.offerings = new ArrayList<>();
    }

    /**
     * Get the subject of this course.
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Get the course number of this course.
     * @return the course number
     */
    public String getCourseNumber() {
        return courseNumber;
    }

    /**
     * Get the full course name of this course,
     * which is the concatenation of the subject and course number.
     * @return the full course name
     */
    public String getCourseName() {
        return subject + courseNumber;
    }

    /**
     * Add an offering to this course.
     * @param offering the offering to add
     */
    public void addOffering(Offering offering) {
        offerings.add(offering);
    }

    /**
     * Get the list of offerings for this course.
     * @return a list of offerings
     */
    public List<Offering> getOfferings() {
        return new ArrayList<>(offerings);
    }
}
