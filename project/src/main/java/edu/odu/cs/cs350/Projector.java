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

    public void processHistoricSemesters(List<Semester> historicSems, DateTimeFormatter dtf) {
        for (Semester semester : historicSems) {
            for (Snapshot snapshot : semester.getSnapshots()) {
                double normalizedDate = semester.normalizeDate(snapshot.getDate());
                snapshot.getCourseEnrollments().forEach((courseName, enrollment) -> 
                    addHistoricalEnrollment(normalizedDate, courseName, enrollment, snapshot.getCourseCapacity(courseName)));
            }
        }
    }

    public void processCurrentSemester(Semester currentSemester, DateTimeFormatter dtf) {
        for (Snapshot snapshot : currentSemester.getSnapshots()) {
            double normalizedDate = currentSemester.normalizeDate(snapshot.getDate());
            snapshot.getCourseEnrollments().forEach((courseName, enrollment) -> 
                addCurrentEnrollment(normalizedDate, courseName, enrollment, snapshot.getCourseCapacity(courseName)));
        }
    }

    public void generateProjectionsForCourses() {
        for (String courseName : currentSemesterCourses) {
            ProjectedCourse course = projections.get(courseName);
            if (course != null) {
                course.generateProjectionsForQuarters();
                summaryReport.addCourse(course);
            }
        }
    }

    public List<ProjectedCourse> getProjectionResults() {
        List<ProjectedCourse> results = new ArrayList<>();
        for (String courseName : currentSemesterCourses) {
            results.add(projections.get(courseName));
        }
        return results;
    }

    public void displaySummaryReport(Semester currentSemester, DateTimeFormatter dtf) {
        summaryReport.displayProjectionResults(
                currentSemester.getPreRegDate().format(dtf),
                currentSemester.getAddDeadline().format(dtf),
                LocalDate.now().format(dtf)
        );
    }

    private void addHistoricalEnrollment(double normalizedDate, String courseName, int enrollment, int capacity) {
        ProjectedCourse projectedCourse = projections.computeIfAbsent(courseName, ProjectedCourse::new);
        projectedCourse.setCourseCapacity(capacity);
        projectedCourse.addHistoricalEnrollment(normalizedDate, enrollment);
    }

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
                    new Semester("C:\\Users\\spoca\\Desktop\\202310"),
                    new Semester("C:\\Users\\spoca\\Desktop\\202320"),
                    new Semester("C:\\Users\\spoca\\Desktop\\202230"),
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
    