package edu.odu.cs.cs350;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

public class Projector {

    private static final Logger logger = Logger.getLogger(Projector.class.getName());
    private final SummaryProjectionReport summaryReport;
    private final Map<String, ProjectedCourse> projections;
    private final Set<String> currentSemesterCourses;
    private final DetailedReportGenerator detailedReport;
    public Projector() {
        this.summaryReport = new SummaryProjectionReport();
        this.projections = new HashMap<>();
        this.currentSemesterCourses = new HashSet<>();
        this.detailedReport = new DetailedReportGenerator();
    }

    /**
     * Processes a list of historic semesters and updates historical enrollment data.
     *
     * @param historicSems a list of Semester objects representing historic semesters
     * @param dtf a DateTimeFormatter used for formatting dates
     */
    public void processHistoricSemesters(List<Semester> historicSems, DateTimeFormatter dtf) {
        for (Semester semester : historicSems) {
            for (Snapshot snapshot : semester.getSnapshots()) {
                double normalizedDate = semester.normalizeDate(snapshot.getDate());
                snapshot.getCourseEnrollments().forEach((courseName, enrollment) -> 
                    addHistoricalEnrollment(normalizedDate, courseName, enrollment, snapshot.getCourseCapacity(courseName)));
            }
        }
    }

    /**
     * Processes the current semester by iterating through its snapshots and adding the current enrollment
     * for each course. The enrollment data is normalized based on the snapshot date.
     *
     * @param currentSemester the current semester containing snapshots to be processed
     * @param dtf the DateTimeFormatter used for formatting dates
     */
    public void processCurrentSemester(Semester currentSemester, DateTimeFormatter dtf) {
        for (Snapshot snapshot : currentSemester.getSnapshots()) {
            double normalizedDate = currentSemester.normalizeDate(snapshot.getDate());
            snapshot.getCourseEnrollments().forEach((courseName, enrollment) -> 
                addCurrentEnrollment(normalizedDate, courseName, enrollment, snapshot.getCourseCapacity(courseName)));
        }
    }

    /**
     * Generates projections for all courses in the current semester.
     * For each course in the current semester, it retrieves the corresponding
     * ProjectedCourse object from the projections map. If the course is found,
     * it generates projections for the quarters and adds the course to the summary report.
     */
    public void generateProjectionsForCourses() {
        for (String courseName : currentSemesterCourses) {
            ProjectedCourse course = projections.get(courseName);
            if (course != null) {
                course.generateProjectionsForQuarters();
                summaryReport.addCourse(course);
            }
        }
    }

    /**
     * Retrieves the projection results for the current semester courses.
     *
     * This method iterates over the list of current semester courses and 
     * retrieves the corresponding projected course from the projections map.
     * The results are collected into a list and returned.
     *
     * @return a list of ProjectedCourse objects representing the projection results
     *         for the current semester courses.
     */
    public List<ProjectedCourse> getProjectionResults() {
        List<ProjectedCourse> results = new ArrayList<>();
        for (String courseName : currentSemesterCourses) {
            results.add(projections.get(courseName));
        }
        return results;
    }

    /**
     * Displays the summary report for the given semester.
     *
     * @param currentSemester the current semester for which the summary report is to be displayed
     * @param dtf the DateTimeFormatter used to format the dates in the report
     */
    public void displaySummaryReport(Semester currentSemester, DateTimeFormatter dtf) {
        summaryReport.displayProjectionResults(
                currentSemester.getPreRegDate().format(dtf),
                currentSemester.getAddDeadline().format(dtf),
                LocalDate.now().format(dtf)
        );
    }

    /**
     * Adds historical enrollment data for a course.
     *
     * @param normalizedDate the date of the enrollment, normalized to a specific format
     * @param courseName the name of the course
     * @param enrollment the number of students enrolled in the course
     * @param capacity the maximum capacity of the course
     */
    private void addHistoricalEnrollment(double normalizedDate, String courseName, int enrollment, int capacity) {
        ProjectedCourse projectedCourse = projections.computeIfAbsent(courseName, ProjectedCourse::new);
        projectedCourse.setCourseCapacity(capacity);
        projectedCourse.addHistoricalEnrollment(normalizedDate, enrollment);
    }


    /**
     * Adds current enrollment data for a course.
     *
     * @param normalizedDate the date of the enrollment, normalized to a specific format
     * @param courseName the name of the course
     * @param enrollment the number of students enrolled in the course
     * @param capacity the maximum capacity of the course
     */
    private void addCurrentEnrollment(double normalizedDate, String courseName, int enrollment, int capacity) {
        ProjectedCourse projectedCourse = projections.computeIfAbsent(courseName, ProjectedCourse::new);
        projectedCourse.setCourseCapacity(capacity);
        projectedCourse.addCurrentEnrollment(normalizedDate, enrollment);
        currentSemesterCourses.add(courseName);
    }

    public static void main(String[] args) {
        try {
            // Initialize semesters
            Semester currentSem = new Semester("C:\\SemesterData\\202420");
            List<Semester> historicSems = Arrays.asList(
                    new Semester("C:\\SemesterData\\\\202310"),
                    new Semester("C:\\SemesterData\\\\202320"),
                    new Semester("C:\\SemesterData\\\\202330"),
                    new Semester("C:\\SemesterData\\202410")
            );
    
            // Process projections
            Projector projector = new Projector();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
            projector.processHistoricSemesters(historicSems, dtf);
            projector.processCurrentSemester(currentSem, dtf);
            projector.generateProjectionsForCourses();
    
            // Generate reports
            List<ProjectedCourse> projectionResults = projector.getProjectionResults();
            projector.displaySummaryReport(currentSem, dtf);
    
            // Use DetailedReportGenerator to generate Excel report
            DetailedReportGenerator.generateReport(projectionResults, "detailed_projection_report.xlsx");
        } catch (Exception e) {
            Logger.getLogger(Projector.class.getName()).log(Level.SEVERE, "An error occurred", e);
        }
    }
}
    