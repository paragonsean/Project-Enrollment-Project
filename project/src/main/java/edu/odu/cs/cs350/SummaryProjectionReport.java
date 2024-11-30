package edu.odu.cs.cs350;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Logger;

public class SummaryProjectionReport {

    private static final Logger logger = Logger.getLogger(SummaryProjectionReport.class.getName());
    private final Map<String, ProjectedCourse> courseProjections; // Store projections in a map

    // Default Constructor
    public SummaryProjectionReport() {
        this.courseProjections = new TreeMap<>(); // TreeMap ensures sorted order by course name
    }

    /**
     * Adds or updates a course projection.
     *
     * @param course The course to be added or updated.
     */
    public void addCourse(ProjectedCourse course) {
        courseProjections.put(course.getCourseKey(), course);
        logger.info("Added or updated course: " + course.getCourseKey());
    }

    /**
     * Returns the map of projection results.
     *
     * @return A map of projected courses keyed by course name.
     */
    public Map<String, ProjectedCourse> getProjectionResults() {
        return courseProjections;
    }

    /**
     * Displays the projection results to the console.
     *
     * @param startDate   The start date of the enrollment period.
     * @param endDate     The end date of the enrollment period.
     * @param currentDate The current date.
     */
    public void displayProjectionResults(String startDate, String endDate, String currentDate) {
        int elapsedPercentage = enrollmentPeriod(startDate, endDate, currentDate);

        logger.info(elapsedPercentage + "% of the enrollment period has elapsed.");
        System.out.println(elapsedPercentage + "% of the enrollment period has elapsed.");
        System.out.printf("%-10s %-12s %-12s %-12s%n", "Course", "Enrollment", "Projected", "Capacity");
        System.out.println("---------------------------------------------------");

        for (ProjectedCourse course : courseProjections.values()) {
            course.displayProjections();
        }
    }

    /**
     * Calculates the percentage of the enrollment period that has elapsed.
     *
     * @param startDate   The start date in YYYY-MM-DD format.
     * @param endDate     The end date in YYYY-MM-DD format.
     * @param currentDate The current date in YYYY-MM-DD format.
     * @return Percentage of time elapsed as an integer between 0 and 100.
     */
    public int enrollmentPeriod(String startDate, String endDate, String currentDate) {
        try {
            LocalDate firstDate = LocalDate.parse(startDate);
            LocalDate secondDate = LocalDate.parse(endDate);
            LocalDate current = LocalDate.parse(currentDate);

            long period = ChronoUnit.DAYS.between(firstDate, secondDate);
            long elapsed = ChronoUnit.DAYS.between(firstDate, current);

            int percentPassed = (int) Math.round(100 * ((double) elapsed / (double) period));
            percentPassed = Math.max(0, Math.min(100, percentPassed)); // Clamp between 0 and 100

            logger.info("Enrollment period: " + percentPassed + "% elapsed.");
            return percentPassed;

        } catch (Exception e) {
            logger.severe("Error calculating enrollment period: " + e.getMessage());
            return 0;
        }
    }
}
