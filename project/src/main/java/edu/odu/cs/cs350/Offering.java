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
    private Map<String, Section> sections = new ConcurrentHashMap<>();

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

    public String getOfferingKey() {
        return offeringKey;
    }

    public String getCrossListGroup() {
        return crossListGroup;
    }

    public int getOverallCapacity() {
        return overallCapacity;
    }

    public int getOverallEnrollment() {
        return overallEnrollment;
    }

    public Map<String, Section> getSections() {
        return sections;
    }

    public void addSection(String crn, int sectionCapacity, int sectionEnrollment, String link) {
        String sectionKey = crn;
        sections.compute(sectionKey, (key, existingSection) -> handleAddOrUpdateSection(key, existingSection, sectionCapacity, sectionEnrollment, link));
    }

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

    @Override
    public String toString() {
        return String.format("Offering{offeringKey='%s', crossListGroup='%s', sections=%s, %d/%d students currently enrolled}", offeringKey, crossListGroup, sections.keySet(), overallEnrollment, overallCapacity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Offering offering = (Offering) o;
        return Objects.equals(offeringKey, offering.offeringKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(offeringKey);
    }
}
