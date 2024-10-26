package edu.odu.cs.cs350;

/**
 * A section has a course registration number, enrollment, link,
 * and cross-list capacity.
 */
public class Section {
    private String courseRegistrationNumber;
    private Integer enrollment;
    private String link;
    private Integer crossListCapacity;

    /**
     * Create a new section.
     * @param courseRegistrationNumber the course registration number of the section
     * @param enrollment the enrollment of the section
     * @param link the link of the section
     * @param crossListCapacity the cross-list capacity of the section
     */
    public Section(String courseRegistrationNumber,
                   Integer enrollment,
                   String link,
                   Integer crossListCapacity) {
        this.courseRegistrationNumber = courseRegistrationNumber;
        this.enrollment = enrollment;
        this.link = link;
        this.crossListCapacity = crossListCapacity;
    }

    /**
     * Get the course registration number of this section.
     * @return the course registration number
     */
    public String getCourseRegistrationNumber() {
        return courseRegistrationNumber;
    }

    /**
     * Get the enrollment of this section.
     * @return the enrollment
     */
    public Integer getEnrollment() {
        return enrollment;
    }

    /**
     * Get the link of this section.
     * @return the link
     */
    public String getLink() {
        return link;
    }

    /**
     * Get the cross-list capacity of this section.
     * @return the cross-list capacity
     */
    public Integer getCrossListCapacity() {
        return crossListCapacity;
    }
}
