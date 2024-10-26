package edu.odu.cs.cs350;

import java.util.ArrayList;
import java.util.List;

/**
 * An offering has a cross-list group, overall capacity,
 * and overall enrollment.
 */
public class Offering {
    private String crossListGroup;
    private Integer overallCapacity;
    private Integer overallEnrollment;
    private List<Section> sections;

    /**
     * Create a new offering.
     * @param crossListGroup the cross-list group of the offering
     * @param overallCapacity the overall capacity of the offering
     * @param overallEnrollment the overall enrollment of the offering
     */
    public Offering(String crossListGroup,
                    Integer overallCapacity,
                    Integer overallEnrollment) {
        this.crossListGroup = crossListGroup;
        this.overallCapacity = overallCapacity;
        this.overallEnrollment = overallEnrollment;
        this.sections = new ArrayList<>();
    }

    /**
     * Get the cross-list group of this offering.
     * @return the cross-list group
     */
    public String getCrossListGroup() {
        return crossListGroup;
    }

    /**
     * Get the overall capacity of this offering.
     * @return the overall capacity
     */
    public Integer getOverallCapacity() {
        return overallCapacity;
    }

    /**
     * Get the overall enrollment of this offering.
     * @return the overall enrollment
     */
    public Integer getOverallEnrollment() {
        return overallEnrollment;
    }

    /**
     * Add a section to this offering.
     * @param section the section to add
     */
    public void addSection(Section section) {
        sections.add(section);
    }

    /**
     * Get the list of sections for this offering.
     * @return a list of sections
     */
    public List<Section> getSections() {
        return new ArrayList<>(sections);
    }
}
