package edu.odu.cs.cs350;

import java.time.LocalDate;
import java.util.TreeMap;

public class Semester {
    private String name;
    private String preRegDate;
    private String addDeadline;
    private String semesterStart;
    private String semesterEnd;
    private int semesterYear;
    private String semesterSeason;
    private EnrollmentSnapshotData enrollmentData;
    private TreeMap<String, Snapshot> Snapshots;
    
    public Semester(String name, String preRegDate, String addDeadline, String semesterStart, String semesterEnd, int semesterYear, String semesterSeason) {
        this.name = name;
        this.preRegDate = preRegDate;
        this.addDeadline = addDeadline;
        this.semesterStart = semesterStart;
        this.semesterEnd = semesterEnd;
        this.semesterYear = semesterYear;
        this.semesterSeason = semesterSeason;
        Snapshots = new TreeMap<>();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPreRegDate(String preRegDate) {
        this.preRegDate = preRegDate;
    }

    public void setAddDeadline(String addDeadline) {
        this.addDeadline = addDeadline;
    }

    public String getSemesterStart() {
        return semesterStart;
    }

    public void setSemesterStart(String semesterStart) {
        this.semesterStart = semesterStart;
    }

    public String getSemesterEnd() {
        return semesterEnd;
    }

    public void setSemesterEnd(String semesterEnd) {
        this.semesterEnd = semesterEnd;
    }

    public int getSemesterYear() {
        return semesterYear;
    }

    public void setSemesterYear(int semesterYear) {
        this.semesterYear = semesterYear;
    }

    public String getSemesterSeason() {
        return semesterSeason;
    }

    public void setSemesterSeason(String semesterSeason) {
        this.semesterSeason = semesterSeason;
    }

    public TreeMap<String, Snapshot> getSnapshots() {
        return Snapshots;
    }

    public void setSnapshots(TreeMap<String, Snapshot> snapshots) {
        Snapshots = snapshots;
    }

    // Default constructor
    public Semester() {
        this.name = "";
        this.preRegDate = null;
        this.addDeadline = null;
      
    }

}