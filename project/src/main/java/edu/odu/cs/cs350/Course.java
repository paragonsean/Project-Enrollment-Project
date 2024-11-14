package edu.odu.cs.cs350;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Course implements Comparable<Course> {

    private static final Logger logger = Logger.getLogger(Course.class.getName());

    private static final String INVALID_CAPACITY_OR_ENROLLMENT_VALUES = "Invalid capacity or enrollment values";
    private static final String COURSE_SECTIONS_TO_STRING = "Course{subject='%s', courseNumber=%d, offerings=%s}";
    private static final String SECTION_SUMMARY_FORMAT = "Section{key='%s', capacity=%d, enrollment=%d}";

    private String subject;
    private String courseNumber;
    protected String courseKey;
    private Map<String, Offering> offerings = new HashMap<>();

    public Course(String subject, String courseNumber) {
        initializeCourse(subject, courseNumber);
    }

    private void initializeCourse(String subject, String courseNumber) {
        ValidationUtils.validateCourseParameters(subject, courseNumber);
        this.subject = subject;
        this.courseNumber = courseNumber;
        this.courseKey = getCourseName();
    }

    public void addOfferingsAndSections(String crn, String crossListGroup, int sectionCapacity, int sectionEnrollment, int offeringCapacity, int offeringEnrollment, String link) {
        String sanitizedLink = ValidationUtils.sanitizeLink(link);
        String determinedcrossListGroup = ValidationUtils.determineXlstGroup(crossListGroup, crn);
        ValidationUtils.validateCapacity(sectionCapacity);
        ValidationUtils.validateEnrollment(sectionEnrollment, sectionCapacity);
        Offering offering = getOrCreateOffering(determinedcrossListGroup, offeringCapacity, offeringEnrollment);
        addSectionToOffering(offering, crn, sectionCapacity, sectionEnrollment, sanitizedLink);
    }

    private void addSectionToOffering(Offering offering, String crn, int sectionCapacity, int sectionEnrollment, String sanitizedLink) {
        logger.info(String.format("Adding section with CRN: %s to Offering: %s", crn, offering.getOfferingKey()));
        offering.addSection(crn, sectionCapacity, sectionEnrollment, crn);
    }

    public Map<String, Offering> getOfferings() {
        return offerings;
    }

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
    public String getSubject() {
        return subject;
    }
    public String getCourseNumber(){
        return courseNumber;
    }

    public Set<String> getKeySet() {
        return offerings.keySet();
    }

    protected String getCourseKey() {
        return courseKey;
    }

    private String getCourseName() {
        return subject + courseNumber;
    }

    private String generateOfferingKey(String crossListGroup) {
        return crossListGroup + "-" + courseKey;
    }

    private Offering getOrCreateOffering(String crossListGroup, int offeringCapacity, int offeringEnrollment) {
        String offeringKey = generateOfferingKey(crossListGroup);
        return offerings.computeIfAbsent(offeringKey, key -> createNewOffering(key, offeringCapacity, offeringEnrollment));
    }

    private Offering createNewOffering(String offeringKey, int capacity, int enrollment) {
        logger.info(String.format("Creating new Offering with key: %s", offeringKey));
        return new Offering(offeringKey, capacity, enrollment); // Only pass offeringKey here
    }

    public int getTotalOfferingEnrollment() {
        return offerings.values().stream().mapToInt(Offering::getOverallEnrollment).sum();
    }

    public int getTotalSectionEnrollment() {
        return offerings.values().stream()
                .flatMap(offering -> offering.getSections().values().stream())
                .mapToInt(Section::getEnrollment)
                .sum();
    }

    public int getTotalOfferingCapacity() {
        return offerings.values().stream()
                .mapToInt(Offering::getOverallCapacity)
                .sum();
    }

    public int getTotalSectionCapacity() {
        return offerings.values().stream()
                .flatMap(offering -> offering.getSections().values().stream())
                .mapToInt(Section::getCrossListCapacity)
                .sum();
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course that = (Course) o;
        return Objects.equals(courseKey, that.courseKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseKey);
    }

    @Override
    public int compareTo(Course other) {
        if (other == null) {
            return 1;
        }
        return this.subject.compareTo(other.subject);
    }
}
