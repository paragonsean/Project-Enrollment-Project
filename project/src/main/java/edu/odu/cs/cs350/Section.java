package edu.odu.cs.cs350;

import java.util.Objects;
import java.util.logging.Logger;
/**
 * The {@code Section} class represents a section of a course in an academic setting.
 * It stores details about the section, including course registration number,
 * enrollment, link, and cross-list capacity.
 * 
 * <p>The class includes validation checks to ensure that section parameters such as 
 * capacity and enrollment adhere to logical constraints. Logging is provided for 
 * error handling and validation exceptions.
 * 
 * <p>Note: This class is immutable for course registration number to maintain section integrity.
 */
public class Section {

    private static final Logger logger = Logger.getLogger(Section.class.getName());

    // Fields
    private final String courseRegistrationNumber;
    private int crossListCapacity;
    private String link;
    private int enrollment;

    /**
     * Constructs a {@code Section} object with specified attributes.
     * @param courseRegistrationNumber the Course Registration Number of the section
     * @param crossListCapacity the cross-list capacity of the section
     * @param enrollment the current enrollment in the section
     * @param link a code representing section links such as lab or recitation links
     * 
     * @throws IllegalArgumentException if any parameter validation fails
     */
    public Section(String courseRegistrationNumber, int crossListCapacity, int enrollment, String link) {
        try {
            ValidationUtils.validateSectionParameters(courseRegistrationNumber);
        } catch (IllegalArgumentException e) {
            logger.info(e.getMessage());
            throw e;
        }
        this.courseRegistrationNumber = courseRegistrationNumber;
        this.crossListCapacity = crossListCapacity;
        this.enrollment = enrollment;
        this.link = link;
    }

    /**
     * Sets the cross-list capacity of the section.
     * 
     * @param crossListCapacity the new cross-list capacity to be set
     * @throws IllegalArgumentException if the provided capacity is invalid
     */
    public void setCrossListCapacity(int crossListCapacity) {
        try {
            ValidationUtils.validateCapacity(crossListCapacity);
        } catch (IllegalArgumentException e) {
            logger.info(e.getMessage());
            throw e;
        }
        this.crossListCapacity = crossListCapacity;
    }

    /**
     * Sets the enrollment for the section.
     * 
     * <p>This method validates the provided enrollment value against the section's capacity.
     * If the enrollment value is valid, it updates the section's enrollment.
     * If the enrollment value is invalid, it logs the error and rethrows an IllegalArgumentException.
     *
     * @param enrollment the number of students to enroll in the section
     * @throws IllegalArgumentException if the enrollment value is invalid
     */
    public void setEnrollment(int enrollment) {
        try {
            ValidationUtils.validateEnrollment(enrollment, crossListCapacity);
            this.enrollment = enrollment;
        } catch (IllegalArgumentException e) {
            logger.info(e.getMessage());
            throw e;
        }
    }
    
    /**
     * Retrieves the cross-list capacity of the section.
     *
     * @return the cross-list capacity of the section
     */
    public int getCrossListCapacity() {
        return crossListCapacity;
    }

    /**
     * Returns the number of students currently enrolled in the section.
     *
     * @return the section enrollment count
     */
    public int getEnrollment() {
        return enrollment;
    }

    /**
     * Sets the link associated with the section.
     * 
     * @param link the new link value
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * Retrieves the unique course registration number for the section.
     * 
     * @return the course registration number as a String
     */
    public String getCourseRegistrationNumber() {
        return courseRegistrationNumber;
    }

    /**
     * Retrieves the link associated with the section.
     * 
     * @return the link as a String
     */
    public String getLink() {
        return link;
    }

    /**
     * Returns a string representation of the section.
     * 
     * <p>The string contains details about the section, including course registration number,
     * enrollment, cross-list capacity, and link.
     * 
     * @return a formatted string representing the section
     */
    @Override
    public String toString() {
        return String.format("Section{courseRegistrationNumber='%s', %d/%d students currently enrolled, link='%s'}", courseRegistrationNumber, enrollment, crossListCapacity, link);
    }

    /**
     * Generates a hash code for this Section object based on its course registration number.
     *
     * @return an integer hash code value for this Section object
     */
    @Override
    public int hashCode() {
        return Objects.hash(courseRegistrationNumber);
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * 
     * @param obj the reference object with which to compare.
     * @return {@code true} if this object is the same as the obj argument;
     *         {@code false} otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Section other = (Section) obj;
        return Objects.equals(courseRegistrationNumber, other.courseRegistrationNumber);
    }
}
