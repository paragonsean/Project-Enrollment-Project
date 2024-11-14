package edu.odu.cs.cs350;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
public class Offering {
    private static final Logger logger = Logger.getLogger(Offering.class.getName());

    private final String offeringKey;
    private final String crossListGroup; // Derived from offeringKey
    private final int overallCapacity;
    private final int overallEnrollment;
    private final Map<String, Section> sections = new ConcurrentHashMap<>();

    /**
     * Constructs an Offering object using only the offering key and other details.
     * The cross-list group is derived from the offering key.
     * @param offeringKey       the unique key for the offering
     * @param overallCapacity   the capacity of the offering
     * @param overallEnrollment the current enrollment of the offering
     */
    public Offering(String offeringKey, int overallCapacity, int overallEnrollment) {
        this.offeringKey = offeringKey;
        this.crossListGroup = deriveCrossListGroup(offeringKey);
        this.overallCapacity = overallCapacity;
        this.overallEnrollment = overallEnrollment;
    }

    /**
     * Derives the cross-list group from the offering key by splitting on '-'.
     *
     * @param offeringKey the unique key for the offering
     * @return the derived cross-list group
     */
    private String deriveCrossListGroup(String offeringKey) {
        String[] parts = offeringKey.split("-");
        return parts.length > 1 ? parts[0] : "";
    }

    /**
     * Retrieves the offering key.
     *
     * @return the offering key as a String
     */
    public String getOfferingKey() {
        return offeringKey;
    }

    /**
     * Retrieves the cross-list group identifier for this offering.
     *
     * @return the cross-list group identifier as a String
     */
    public String getCrossListGroup() {
        return crossListGroup;
    }

    /**
     * Retrieves the overall capacity of the offering.
     *
     * @return the overall capacity as an integer.
     */
    public int getOverallCapacity() {
        return overallCapacity;
    }

    /**
     * Retrieves the overall enrollment for the offering.
     *
     * @return the overall enrollment count
     */
    public int getOverallEnrollment() {
        return overallEnrollment;
    }

    /**
     * Retrieves the map of sections.
     *
     * @return a map where the keys are section identifiers and the values are Section objects.
     */
    public Map<String, Section> getSections() {
        return sections;
    }

    /**
     * Adds a section to the offering or updates an existing section if it already exists.
     *
     * @param crn the course reference number (CRN) of the section
     * @param sectionCapacity the capacity of the section
     * @param sectionEnrollment the current enrollment of the section
     * @param link a link associated with the section
     */
    public void addSection(String crn, int sectionCapacity, int sectionEnrollment, String link) {
        String sectionKey = crn;
        sections.compute(sectionKey, (key, existingSection) -> handleAddOrUpdateSection(key, existingSection, sectionCapacity, sectionEnrollment, link));
    }

    /**
     * Adds a new section or updates an existing section with the provided details.
     *
     * @param key The unique identifier for the section.
     * @param existingSection The existing section to update, or null to create a new section.
     * @param sectionCapacity The capacity of the section.
     * @param sectionEnrollment The current enrollment of the section.
     * @param link The link associated with the section.
     * @return The newly created section or the updated existing section.
     */
    private Section handleAddOrUpdateSection(String key, Section existingSection, int sectionCapacity, int sectionEnrollment, String link) {
        if (existingSection == null) {
            return new Section(key, sectionCapacity, sectionEnrollment, link);
        } else {
            existingSection.setCrossListCapacity(sectionCapacity);
            existingSection.setEnrollment(sectionEnrollment);
            existingSection.setLink(link);
            return existingSection;
        }
    }

    /**
     * Returns a string representation of the Offering object.
     * The string representation includes the offering key, cross-list group,
     * sections, overall enrollment, and overall capacity.
     *
     * @return a formatted string representing the Offering object
     */
    @Override
    public String toString() {
        return String.format("Offering{offeringKey='%s', crossListGroup='%s', sections=%s, %d/%d students currently enrolled}", offeringKey, crossListGroup, sections.keySet(), overallEnrollment, overallCapacity);
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * 
     * @param o the reference object with which to compare.
     * @return {@code true} if this object is the same as the obj argument;
     *         {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Offering offering = (Offering) o;
        return Objects.equals(offeringKey, offering.offeringKey);
    }

    /**
     * Returns a hash code value for the object. This method is supported for the benefit of hash tables such as those provided by HashMap.
     * The hash code is computed based on the offeringKey field.
     *
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(offeringKey);
    }
}
