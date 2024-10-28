package edu.odu.cs.cs350;

/**
 * Custom exception for enrollment errors, such as exceeding section capacity.
 */
class EnrollmentException extends Exception {
    public EnrollmentException(String message) {
        super(message);
    }
}

public class Section {

    
    // Unique identifier for each section
    private String CRN;

    // Cross-list cap: the maximum number of students that can enroll in this section
    private int XLST_CAP;
    // Cross-list group identifier
    private String XLST_GROUP;
    // Number of students currently enrolled in this section
    private int ENR;

    // Used to associate labs and recitations to a lecture
    // LINK consists of an uppercase alphabetic letter and a digit:
    // "1" for lecture, "2" for recitation, and "3" for lab
    private String LINK;

    // Semester in which the section is offered (fall, spring, or summer)
    private String semester;

    // Campus where the section is offered
    private String campus;

    // Whether the section is cross-listed
    private boolean crossListed;

    public Section(String crn, String semester, String campus, int XLST_CAP, int ENR, String LINK,String XLST_GROUP) {
        this.CRN = crn;
        this.semester = semester;
        this.LINK = LINK;
        this.campus = campus;
        this.XLST_CAP = XLST_CAP;
        this.ENR = ENR;
        this.XLST_GROUP = XLST_GROUP;
    }

    public String getXlstGroup(){
        return XLST_GROUP;
    }
    public String getCrn() {
        return CRN;
    }

    public String getSemester() {
        return semester;
    }

    public String getCampus() {
        return campus;
    }

    public int getXlstCap() {
        return XLST_CAP;
    }

    public int getEnr() {
        return ENR;
    }

    public String getLink() {
        return LINK;
    }

    /**
     * Determines if this section is a lecture based on `LINK`.
     * Only sections with `LINK` ending in "1" are considered lectures.
     *
     * @return true if this section is a lecture, false otherwise
     */
    public boolean isLecture() {
        try {
            return LINK != null && LINK.length() == 2 && LINK.endsWith("1");
        } catch (NullPointerException e) {
            System.out.println("Error: LINK code is null.");
            return false;
        }
    }

    /**
     * Enroll a student in this section if section capacity allows.
     *
     * @return true if enrollment succeeded, false if section is full
     */
    public boolean enrollStudent() {
        try {
            if (ENR >= XLST_CAP) {
                throw new EnrollmentException("Enrollment failed: Section capacity of " + XLST_CAP + " reached.");
            }
            ENR++;
            return true;
        } catch (EnrollmentException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public int hashCode() {
        try {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((CRN == null) ? 0 : CRN.hashCode());
            result = prime * result + ((semester == null) ? 0 : semester.hashCode());
            return result;
        } catch (Exception e) {
            System.out.println("Error calculating hash code: " + e.getMessage());
            return 0;
        }
    }

    @Override
    public boolean equals(Object obj) {
        try {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Section other = (Section) obj;
            return CRN.equals(other.CRN) && semester.equals(other.semester);
        } catch (NullPointerException e) {
            System.out.println("Error comparing sections: One or more fields are null.");
            return false;
        }
    }

    @Override
    public String toString() {
        try {
            return "Section [CRN=" + CRN + ", XLST_CAP=" + XLST_CAP + ", ENR=" + ENR + ", LINK=" + LINK + 
                   ", semester=" + semester + ", campus=" + campus + ", crossListed=" + crossListed + "]";
        } catch (Exception e) {
            return "Error generating string representation of Section.";
        }
    }
}
