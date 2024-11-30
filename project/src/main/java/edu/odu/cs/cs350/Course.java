package edu.odu.cs.cs350;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Course implements Comparable<Course> {

    /**
     * Logger instance for the Course class.
     * This logger is used to log messages for the Course class.
     * It is a static final member, meaning it is shared among all instances of the class
     * and cannot be modified after initialization.
     */
    private static final Logger logger = Logger.getLogger(Course.class.getName());

    /**
     * Error message indicating that the provided capacity or enrollment values are invalid.
     */
    private static final String INVALID_CAPACITY_OR_ENROLLMENT_VALUES = "Invalid capacity or enrollment values";

    /**
     * A format string used to generate a string representation of a Course object.
     * The format includes the subject, course number, and offerings of the course.
     * 
     * Format: "Course{subject='%s', courseNumber=%d, offerings=%s}"
     */
    private static final String COURSE_SECTIONS_TO_STRING = "Course{subject='%s', courseNumber=%d, offerings=%s}";

    /**
     * A format string used to create a summary of a course section.
     * The format includes the section key, capacity, and enrollment.
     * Example usage:
     * String summary = String.format(SECTION_SUMMARY_FORMAT, key, capacity, enrollment);
     */
    private static final String SECTION_SUMMARY_FORMAT = "Section{key='%s', capacity=%d, enrollment=%d}";

    /**
     * The subject of the course.
     */
    private String subject;

    /**
     * The course number associated with the course.
     */
    private String courseNumber;

    /**
     * The unique identifier for the course.
     */
    protected String courseKey;
    private LocalDate PTRM_START;
    private LocalDate PTRM_END;
    private double normalizedDate;
    /**
     * A map that stores course offerings.
     * The key is a string representing the course identifier,
     * and the value is an Offering object containing details about the course offering.
     */
    private Map<String, Offering> offerings = new HashMap<>();

    /**
     * Constructs a Course object with the specified subject and course number.
     *
     * @param subject the subject of the course (e.g., "CS" for Computer Science)
     * @param courseNumber the course number (e.g., "350" for CS350)
     */
    public Course(String subject, String courseNumber) {
        initializeCourse(subject, courseNumber);
    }

    /**
     * Initializes the course with the given subject and course number.
     * Validates the course parameters before setting the values.
     *
     * @param subject the subject of the course (e.g., "CS")
     * @param courseNumber the course number (e.g., "350")
     * @throws IllegalArgumentException if the course parameters are invalid
     */
    private void initializeCourse(String subject, String courseNumber) {
        ValidationUtils.validateCourseParameters(subject, courseNumber);
        this.subject = subject;
        this.courseNumber = courseNumber;
        this.courseKey = getCourseName();
    }

    /**
     * Adds offerings and sections to the course.
     *
     * @param crn                The course reference number.
     * @param crossListGroup     The cross-list group identifier.
     * @param sectionCapacity    The capacity of the section.
     * @param sectionEnrollment  The current enrollment of the section.
     * @param offeringCapacity   The capacity of the offering.
     * @param offeringEnrollment The current enrollment of the offering.
     * @param link               The link associated with the section.
     */
    public void addOfferingsAndSections(String crn, String crossListGroup, int sectionCapacity, int sectionEnrollment, int offeringCapacity, int offeringEnrollment, String link) {
        String sanitizedLink = ValidationUtils.sanitizeLink(link);
        String determinedcrossListGroup = ValidationUtils.determineXlstGroup(crossListGroup, crn);
        ValidationUtils.validateCapacity(sectionCapacity);
        ValidationUtils.validateEnrollment(sectionEnrollment, sectionCapacity);
        Offering offering = addOffering(determinedcrossListGroup, offeringCapacity, offeringEnrollment);
        addSectionToOffering(offering, crn, sectionCapacity, sectionEnrollment, sanitizedLink);
    }

    /**
     * Adds a section to the given offering.
     *
     * @param offering the offering to which the section will be added
     * @param crn the course registration number of the section
     * @param sectionCapacity the capacity of the section
     * @param sectionEnrollment the current enrollment of the section
     * @param sanitizedLink a sanitized link associated with the section
     */
    private void addSectionToOffering(Offering offering, String crn, int sectionCapacity, int sectionEnrollment, String sanitizedLink) {
        // logger.info(String.format("Adding section with CRN: %s to Offering: %s", crn, offering.getOfferingKey()));
        offering.addSection(crn, sectionCapacity, sectionEnrollment, crn);
    }

    public void setPTRM_START(LocalDate start) {
        PTRM_START = start;
    }
    public void setPTRM_END(LocalDate end) {
        PTRM_END = end;
    }
    public void setNormalizedDate(double normalizedDate){
        this.normalizedDate = normalizedDate;
    }
    public double getNormalizedDate(){
        return this.normalizedDate;
    }
    public LocalDate getPTRM_START(){
        return this.PTRM_START;
    }
    public LocalDate getPTRM_END(){
        return this.PTRM_END;
    }
    /**
        return Collections.unmodifiableMap(offerings);
     *
     * @return a map where the key is a string representing the course identifier
     *         and the value is an Offering object containing details about the course offering
     */
    public Map<String, Offering> getOfferings() {
        return offerings;
    }

    /**
     * Merges the offerings and sections from another course into this course.
     * This method assumes that the courses have the same course key.
     * If the course keys are different, an IllegalArgumentException is thrown.
     *
     * @param otherCourse the course to merge into this course
     * @throws IllegalArgumentException if the course keys are different
     */
    public void mergeCourse(Course otherCourse) {
        if (!this.courseKey.equals(otherCourse.getCourseKey())) {
            throw new IllegalArgumentException("Cannot merge courses with different keys");
        }

        for (Offering otherOffering : otherCourse.getOfferings().values()) {
            for (Section otherSection : otherOffering.getSections().values()) {
                addOfferingsAndSections(
                    otherSection.getCourseRegistrationNumber(),
                    otherOffering.getCrossListGroup(),
                    otherSection.getCrossListCapacity(),
                    otherSection.getEnrollment(),
                    otherOffering.getOverallCapacity(),
                    otherOffering.getOverallEnrollment(),
                    otherSection.getLink()
                );
            }
        }
    }



    /**
     * Retrieves the subject of the course.
     *
     * @return the subject as a String
     */
    public String getSubject() {
        return subject;
    }



    /**
     * Retrieves the course number.
     *
     * @return the course number as a String
     */
    public String getCourseNumber(){
        return courseNumber;
    }

    /**
     * Retrieves the set of keys from the offerings map.
     *
     * @return a Set containing all the keys from the offerings map.
     */
    public Set<String> getKeySet() {
        return offerings.keySet();
    }

    /**
     * Retrieves the unique key associated with the course.
     *
     * @return the course key as a String
     */
    protected String getCourseKey() {
        return courseKey;
    }

    /**
     * Retrieves the full course name by concatenating the subject and course number.
     *
     * @return the full course name as a String
     */
    private String getCourseName() {
        return subject + courseNumber;
    }

    /**
     * Generates a  key for a course offering based on the provided cross-list group.
     *
     * @param crossListGroup the cross-list group identifier for the course
     * @return a  for the course offering, combining the cross-list group and course name 
     */
    private String generateOfferingKey(String crossListGroup) {
        return crossListGroup + "-" + courseKey;
    }

    /**
     * Retrieves an existing Offering from the offerings map or creates a new one if it does not exist.
     * The Offering is identified by a key generated from the crossListGroup.
     *
     * @param crossListGroup the group identifier for cross-listed courses
     * @param offeringCapacity the capacity of the offering
     * @param offeringEnrollment the current enrollment of the offering
     * @return the existing or newly created Offering
     */
    private Offering addOffering(String crossListGroup, int offeringCapacity, int offeringEnrollment) {
        String offeringKey = generateOfferingKey(crossListGroup);
        return offerings.computeIfAbsent(offeringKey, key -> createNewOffering(key, offeringCapacity, offeringEnrollment));
    }

    /**
     * Creates a new Offering instance with the specified key, capacity, and enrollment.
     *
     * @param offeringKey the unique key identifying the offering
     * @param capacity the maximum number of students that can enroll in the offering
     * @param enrollment the current number of students enrolled in the offering
     * @return a new Offering instance with the specified parameters
     */
    private Offering createNewOffering(String offeringKey, int capacity, int enrollment) {
        // logger.info(String.format("Creating new Offering with key: %s", offeringKey));
        return new Offering(offeringKey, capacity, enrollment); // Only pass offeringKey here
    }

    /**
     * Calculates the total enrollment across all course offerings.
     *
     * @return the sum of enrollments for all offerings of this course.
     */
    public int getTotalOfferingEnrollment() {
        return offerings.values().stream().mapToInt(Offering::getOverallEnrollment).sum();
    }
    /**
     * Calculates the total enrollment for the course, which is the larger of
     * the total section enrollment and the total offering capacity.
     *
     * @return the larger of total section enrollment and total offering capacity.
     */
    public int getTotalEnrollment() {
        int totalSectionEnrollment = offerings.values().stream()
                .flatMap(offering -> offering.getSections().values().stream())
                .mapToInt(Section::getEnrollment)
                .sum();

        int totalOfferingCapacity = offerings.values().stream()
                .mapToInt(Offering::getOverallCapacity)
                .sum();

        return Math.max(totalSectionEnrollment, totalOfferingCapacity);
    }
    /**
     * Calculates the total capacity for the course, which is the larger of
     * the total section capacity and the total offering capacity.
     *
     * @return the larger of total section capacity and total offering capacity.
     */
    public int getTotalCapacity() {
        int totalSectionCapacity = offerings.values().stream()
                .flatMap(offering -> offering.getSections().values().stream())
                .mapToInt(Section::getCrossListCapacity)
                .sum();

        int totalOfferingCapacity = offerings.values().stream()
                .mapToInt(Offering::getOverallCapacity)
                .sum();

        return Math.max(totalSectionCapacity, totalOfferingCapacity);
    }

    /**
     * Calculates the total enrollment across all sections of all offerings.
     *
     * @return the total number of students enrolled in all sections.
     */
    public int getTotalSectionEnrollment() {
        return offerings.values().stream()
                .flatMap(offering -> offering.getSections().values().stream())
                .mapToInt(Section::getEnrollment)
                .sum();
    }

    /**
     * Calculates the total capacity of all course offerings.
     *
     * @return the sum of the overall capacities of all offerings.
     */
    public int getTotalOfferingCapacity() {
        return offerings.values().stream()
                .mapToInt(Offering::getOverallCapacity)
                .sum();
    }

    /**
     * Calculates the total capacity of all sections across all offerings.
     *
     * This method iterates through all offerings, retrieves their sections,
     * and sums up the cross-list capacities of these sections.
     *
     * @return the total capacity of all sections.
     */
    public int getTotalSectionCapacity() {
        return offerings.values().stream()
                .flatMap(offering -> offering.getSections().values().stream())
                .mapToInt(Section::getCrossListCapacity)
                .sum();
    }

    /**
     * Retrieves the enrollment number for a specific section identified by its CRN (Course Reference Number)
     * within a given cross-list group.
     *
     * @param crn the Course Reference Number of the section
     * @param crossListGroup the cross-list group identifier
     * @return the enrollment number of the specified section
     * @throws IllegalArgumentException if the section is not found for the given CRN and cross-list group
     */
    public int getSectionEnrollment(String crn, String crossListGroup) {
        ValidationUtils.validateSectionParameters(crn);
        String offeringKey = generateOfferingKey(crossListGroup);
        Offering offering = offerings.get(offeringKey);
        if (offering != null) {
            Section section = offering.getSections().get(crn);
            if (section != null) {
                return section.getEnrollment();
            }
        }
        String errorMessage = String.format("Section not found for CRN: %s in XLST Group: %s", crn, crossListGroup);
        logger.log(Level.SEVERE, errorMessage);
        throw new IllegalArgumentException(errorMessage);
    }

    /**
     * Returns a string representation of the Course object.
     * The string representation consists of the subject, course number,
     * number of offerings, and the string representations of each offering
     * and their respective sections.
     *
     * @return a string representation of the Course object
     */
    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", "Course{", "}");
        joiner.add("subject='" + subject + "'")
              .add("courseNumber=" + courseNumber)
              .add("offerings=" + offerings.size());
        for (Offering offering : offerings.values()) {
            joiner.add(offering.toString());
            for (Section section : offering.getSections().values()) {
                joiner.add(section.toString());
            }
        }
        return joiner.toString();
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * 
     * @param o the reference object with which to compare.
     * @return {@code true} if this object is the same as the obj
     *         argument; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course that = (Course) o;
        return Objects.equals(courseKey, that.courseKey);
    }

    /**
     * Returns a hash code value for the object. This method is supported for the benefit of hash tables such as those provided by HashMap.
     * The hash code is computed based on the courseKey field.
     *
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(courseKey);
    }

    /**
     * Compares this Course object with the specified Course object for order.
     * Returns a negative integer, zero, or a positive integer as this object's
     * subject is less than, equal to, or greater than the specified object's subject.
     *
     * @param other the Course object to be compared.
     * @return a negative integer, zero, or a positive integer as this object's
     *         subject is less than, equal to, or greater than the specified object's subject.
     *         If the specified Course object is null, returns 1.
     */
  @Override
public int compareTo(Course other) {
    if (other == null) {
        return 1;
    }
    return Double.compare(this.normalizedDate, other.normalizedDate);
}
}
