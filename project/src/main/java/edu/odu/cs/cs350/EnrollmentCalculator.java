package edu.odu.cs.cs350;

import java.util.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.logging.Logger;

public class EnrollmentCalculator {
    private static final Logger logger = Logger.getLogger(EnrollmentCalculator.class.getName());
    private final Map<String, ProjectedCourseEnrollment> courseProjections = new HashMap<>();
    private double areaThreshold;

    public EnrollmentCalculator() {
        this.areaThreshold = 1e-10; // Default threshold value
    }

    public void setAreaThreshold(double threshold) {
        this.areaThreshold = threshold;
    }

    public Map<String, ProjectedCourseEnrollment> getCourseProjections() {
        return courseProjections;
    }

    /**
     * Calculate projections based on historical and current data.
     * This includes normal projections and trapezoidal projections.
     */
    public void calculateProjections(History history, Semester currentSemester) {
        List<Semester> historicalSemesters = history.extractLastHistoricalSemesters();

        if (historicalSemesters == null || historicalSemesters.isEmpty()) {
            logger.warning("No historical semesters available. Skipping projections.");
            return;
        }

        // Iterate over current semester snapshots to initialize course projections
        for (Snapshot snapshot : currentSemester.getSnapshots()) {
            snapshot.getCourses().forEach((courseName, course) -> {
                ProjectedCourseEnrollment projection = courseProjections.computeIfAbsent(
                        courseName,
                        name -> new ProjectedCourseEnrollment(name, course.getTotalSectionCapacity())
                );
                double normalizedDate = normalizeDateToFloat(snapshot.getDate(), currentSemester.getPreRegDate(), currentSemester.getAddDeadline());
                projection.addCurrentEnrollment(normalizedDate, course.getTotalSectionEnrollment());
            });
        }

        // Populate historical data and compute projections
        for (Semester semester : historicalSemesters) {
            for (Snapshot snapshot : semester.getSnapshots()) {
                snapshot.getCourses().forEach((courseName, course) -> {
                    ProjectedCourseEnrollment projection = courseProjections.get(courseName);
                    if (projection != null) {
                        double normalizedDate = normalizeDateToFloat(snapshot.getDate(), semester.getPreRegDate(), semester.getAddDeadline());
                        projection.addHistoricalEnrollment(normalizedDate, course.getTotalSectionEnrollment());
                    }
                });
            }
        }

        // Generate projections for all courses
        courseProjections.values().forEach(ProjectedCourseEnrollment::generateProjectionsForQuarters);


    }

    /**
     * Normalizes the date to a floating-point value between 0 and 1.
     *
     * @param targetDate The date to normalize.
     * @param startDate  The start date of the range.
     * @param endDate    The end date of the range.
     * @return A normalized double between 0 and 1.
     */
    public static double normalizeDateToFloat(LocalDate targetDate, LocalDate startDate, LocalDate endDate) {
        long totalDuration = ChronoUnit.DAYS.between(startDate, endDate);
        long elapsedDuration = ChronoUnit.DAYS.between(startDate, targetDate);
        return totalDuration > 0 ? (double) elapsedDuration / totalDuration : 0.0;
    }
}
