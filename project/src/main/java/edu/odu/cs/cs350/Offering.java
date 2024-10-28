package edu.odu.cs.cs350;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Offering implements Iterable<Section> {

    private String subj;                 // Department offering the course, e.g., "CS"
    private String crse;                 // Course number, e.g., "418"
    private String xlstGroup;            // Cross-list group identifier
    private int overallCap;              // Maximum number of students that can enroll in this offering
    private Map<String, Section> sections; // Map of sections in the offering, keyed by CRN
    private String proffesor; // Name of the professor teaching the course
    
    /**
     * Constructor for a new Offering.
     *
     * @param subj       the department offering the course
     * @param crse       the course number
     * @param xlstGroup  cross-list group identifier
     * @param overallCap the maximum number of students across all sections in this offering
     */
    public Offering(String subj, String crse, String xlstGroup, int overallCap, String proffesor) {
        this.subj = subj;
        this.crse = crse;
        this.xlstGroup = xlstGroup;
        this.overallCap = overallCap;
        this.sections = new HashMap<>();
        this.proffesor = proffesor;
    }

    public String getSubj() {
        return subj;
    }

    public String getCrse() {
        return crse;
    }

    public String getXlstGroup() {
        return xlstGroup;
    }

    public int getOverallCap() {
        return overallCap;
    }

    public int getOverallEnr() {
        return sections.values().stream().mapToInt(Section::getEnr).sum();
    }
    public String getProfessor(){
        return proffesor;
    }
    /**
     * Adds a section to this offering if it shares the same XLST GROUP.
     *
     * @param section the section to add
     * @return true if the section was added successfully, false if it doesn’t match the offering's XLST GROUP
     */
    public boolean addSection(Section section) {
        if (!section.getXlstGroup().equals(this.xlstGroup)) {
            System.out.println("Section does not match the offering's XLST GROUP.");
            return false;
        }

        if (sections.containsKey(section.getCrn())) {
            System.out.println("Section with CRN " + section.getCrn() + " already exists in this offering.");
            return false;
        }

        sections.put(section.getCrn(), section);
        return true;
    }

    /**
     * Check if a student can be enrolled without exceeding the overall cap.
     *
     * @return true if enrollment is possible, false if the overall cap is reached
     */
    public boolean canEnrollMoreStudents() {
        return getOverallEnr() < overallCap;
    }

    /**
     * Enroll a student in a specified section by CRN, respecting section and overall caps.
     *
     * @param crn the CRN of the section
     * @return true if enrollment succeeded, false otherwise
     */
    public boolean enrollStudentInSection(String crn) {
        Section section = sections.get(crn);
        if (section == null) {
            System.out.println("Section with CRN " + crn + " not found in this offering.");
            return false;
        }

        if (!canEnrollMoreStudents()) {
            System.out.println("Enrollment failed: Offering has reached the overall capacity of " + overallCap);
            return false;
        }

        return section.enrollStudent();
    }

    /**
     * Provides an iterator over the sections in this offering.
     *
     * @return an iterator over the sections in this offering
     */
    @Override
    public Iterator<Section> iterator() {
        return sections.values().iterator();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Offering [Course Name=").append(subj).append(crse)
                .append(", Cross-list Group=").append(xlstGroup)
                .append(", Overall Capacity=").append(overallCap)
                .append(", Total Enrollment=").append(getOverallEnr())
                .append(", Sections=\n");
        for (Section section : sections.values()) {
            builder.append("  ").append(section).append("\n");
        }
        builder.append("]");
        return builder.toString();
    }

   

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Offering)) return false;
        Offering other = (Offering) obj;
        return subj.equals(other.subj) && crse.equals(other.crse) && xlstGroup.equals(other.xlstGroup)
                && overallCap == other.overallCap && sections.equals(other.sections);
    }
}
