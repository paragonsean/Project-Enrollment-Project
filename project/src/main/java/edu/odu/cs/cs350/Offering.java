package edu.odu.cs.cs350;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Offering {

    private Set<String> courseCodes;  // Cross-listed course codes, e.g., CS620, DASC620
    private String professor;         // Instructor for all sections
    private int overallCap;           // Max enrollment across all sections
    private Map<String, Section> sections;  // Map from course code to Section

    /**
     * Constructor for a new offering.
     * 
     * @param courseCodes a set of cross-listed course codes
     * @param professor the professor for the offering
     * @param overallCap the maximum number of students across all sections
     */
    public Offering(Set<String> courseCodes, String professor, int overallCap) {
        this.courseCodes = new HashSet<>(courseCodes);
        this.professor = professor;
        this.overallCap = overallCap;
        this.sections = new HashMap<>();
    }

    public Set<String> getCourseCodes() {
        return courseCodes;
    }

    public String getProfessor() {
        return professor;
    }

    public int getOverallCap() {
        return overallCap;
    }

    /**
     * Adds a section to this offering based on its unique course code.
     * 
     * @param section the section to add
     * @return true if the section was added successfully, false if course code already exists
     */
    public boolean addSection(Section section) {
        String courseCode = section.getCrn(); // Unique course code based on CRN and semester
        if (sections.containsKey(courseCode)) {
            System.out.println("Section with Course Code " + courseCode + " already exists.");
            return false;
        }
        sections.put(courseCode, section);
        return true;
    }

    /**
     * Get the total number of students enrolled across all sections.
     */
    public int getOverallEnrollment() {
        return sections.values().stream().mapToInt(Section::getEnr).sum();
    }

    /**
     * Enroll a student in a specified section by course code, respecting section and overall caps.
     * 
     * @param courseCode the unique course code of the section
     * @return true if enrollment succeeded, false if enrollment limits were reached
     */
    public boolean enrollStudentInSection(String courseCode) {
        Section section = sections.get(courseCode);
        if (section == null) {
            System.out.println("Section not found for course code: " + courseCode);
            return false;
        }

        // Check overall cap
        if (getOverallEnrollment() < overallCap && section.getEnr() < section.getXLST_CAP()) {
            return section.enrollStudent();
        }

        System.out.println("Enrollment limit reached for section or overall offering.");
        return false;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Offering: ").append(courseCodes);
        builder.append("\nProfessor: ").append(professor);
        builder.append("\nOverall Cap: ").append(overallCap);
        builder.append("\nOverall Enrollment: ").append(getOverallEnrollment()).append("\nSections:\n");

        for (Section section : sections.values()) {
            builder.append("  ").append(section).append("\n");
        }

        return builder.toString();
    }
}
