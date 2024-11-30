package edu.odu.cs.cs350;

import java.util.*;
import java.util.Map.Entry;

public class ProjectedCourse {
    private String courseName;

    /**
     * A navigable map that stores historical enrollment data.
     * The key represents the enrollment year as a double value.
     * The value represents the number of students enrolled in that year.
     * This map is sorted in ascending order of the enrollment year.
     */
    private final NavigableMap<Double, Integer> historicalEnrollmentData = new TreeMap<>();
    
    /**
     * A NavigableMap to store the current enrollment data for the course.
     * The key represents the enrollment percentage (as a Double) and the value represents the number of students (as an Integer).
     */
    private final NavigableMap<Double, Integer> currentEnrollmentData = new TreeMap<>();
    
    /**
     * A navigable map that stores normal projections.
     * The keys are of type Double and represent some metric or value,
     * while the values are of type Integer and represent the corresponding projection count.
     * This map is implemented using a TreeMap to maintain the natural ordering of the keys.
     */
    private final NavigableMap<Double, Integer> normalProjections = new TreeMap<>();
    
    /**
     * A NavigableMap that stores historical value counts.
     * The keys are Double values representing some metric or measurement,
     * and the values are Integer counts of occurrences of those metrics.
     * This map is sorted in natural order of the keys.
     */
    private final NavigableMap<Double, Integer> historicalValueCounts = new TreeMap<>();

    /**
     * A list to store historical ratios for curve smoothing.
     */
    protected final List<Double> historicalRatios = new ArrayList<>(); // To store historical ratios
    
    private int fullProjectionCount; // To store the full projection count
    private int capacity; // To store the course capacity
   
   
    /**
     * Constructs a new ProjectedCourse with the specified course name.
     *
     * @param courseName the name of the course
     */
    public ProjectedCourse(String courseName) {
        this.courseName = courseName;
    }

    /**
     * Retrieves the key associated with the course.
     *
     * @return the course name as the course key
     */
    public String getCourseKey() {
        return this.courseName;
    }
    /**
     * Sets the capacity for the course.
     *
     * @param capacity the maximum number of students that can enroll in the course
     */
    public void setCourseCapacity(int capacity) {
        this.capacity = capacity;   // Set the course capacity
    }

    /**
     * Adds historical enrollment data for a given index.
     * If the index already exists, the count is added to the existing count.
     * Also increments the count of how many times the index has been added.
     *
     * @param index the index representing a specific historical enrollment period
     * @param count the number of enrollments to add for the given index
     */
    public void addHistoricalEnrollment(double index, int count) {
        historicalEnrollmentData.merge(index, count, Integer::sum);
        historicalValueCounts.merge(index, 1, Integer::sum);
    }

    // Adds current enrollment data
    /**
     * Adds the current enrollment data for a specific index.
     *
     * @param index the index representing a specific point in the enrollment data
     * @param value the enrollment value to be added at the specified index
     */
    public void addCurrentEnrollment(double index, int value) {
        currentEnrollmentData.put(index, value);
    }
    /**
     * Retrieves the capacity of the projected course.
     *
     * @return the capacity of the course
     */
    public int getCapacity() {
        return capacity;
    }
    /**
     * Retrieves the name of the course.
     *
     * @return the name of the course as a String
     */
    public String getCourseName() {
        return courseName;
    }
    // Generates projections for quarters
    /**
     * Generates projections for specific quarter indices.
     * The quarter indices are defined as follows:
     * - 0.0: 0%
     * - 0.1: 10%
     * - 0.2: 20%
     * - 0.25: 25%
     * - 0.3: 30%
     * - 0.4: 40%
     * - 0.50: 50%
     * - 0.75: 75%
     * - 1.0: 100%
     * For each of these indices, the method calls generateProjection. This will impact the overall projection based on the indices we call
     */
    public void generateProjectionsForQuarters() {
        double[] targetIndices = {0.0,0.1, 0.2, 0.25, 0.3, 0.4, 0.50, 0.75, 1.0}; // Quarter indices: 25%, 50%, 75%, 100%
        for (double targetIndex : targetIndices) {
            generateProjection(targetIndex);
        }
    }

    /**
     * Generates a projection for the course enrollment based on the target index.
     * 
     * This method uses current enrollment data and historical data interpolation to
     * calculate a normal projection. It then applies curve smoothing to adjust for
     * stability and stores the resulting projection.
     * 
     * @param targetIndex The target index for which the projection is to be generated.
     *                    If the target index is 1.0, the projection count is stored
     *                    as the full projection count.
     */
    public void generateProjection(double targetIndex) {
        if (currentEnrollmentData.isEmpty()) {
            System.out.println("Skipping projection for course: " + courseName + " as current enrollment data is empty.");
            return; // Skip this projection
        }

        double recentIndex = currentEnrollmentData.lastKey();
        int recentEnrollment = currentEnrollmentData.get(recentIndex);
        int recentHistorical = interpolateHistoricalEnrollment(recentIndex);
        int targetHistorical = interpolateHistoricalEnrollment(targetIndex);

        // Normal projection: Based on current data and historical data interpolation
        int normalProjection = recentEnrollment;

        if (recentHistorical != 0 && targetHistorical != 0) {
            normalProjection = (int) Math.ceil((double) recentEnrollment / recentHistorical * targetHistorical);
        }

        // Curve smoothing using historical ratios (to adjust for stability)
        double smoothedProjection = applyCurveSmoothing(normalProjection);

        // Store the normal projection
        normalProjections.put(targetIndex, (int) smoothedProjection);

        // If the targetIndex is 1 (full projection), store the projection count
        if (targetIndex == 1.0) {
            fullProjectionCount = (int) smoothedProjection;
        }
    }

    // Interpolates historical enrollment data for a given index
    /**
     * Interpolates the historical enrollment data for a given index.
     * If the exact index is found in the historical data, it returns the average enrollment value for that index.
     * If the exact index is not found, it performs a linear interpolation between the nearest lower and upper indices.
     * 
     * @param index the index for which to interpolate the historical enrollment data
     * @return the interpolated enrollment value, or 0 if interpolation is not possible
     */
    protected int interpolateHistoricalEnrollment(double index) {
        if (historicalEnrollmentData.containsKey(index)) {
            return historicalEnrollmentData.get(index) / historicalValueCounts.getOrDefault(index, 1); // divide by the count of the index if multiple values exist
        }

        Double lowerKey = historicalEnrollmentData.floorKey(index);
        Double upperKey = historicalEnrollmentData.ceilingKey(index);

        if (lowerKey == null || upperKey == null || lowerKey.equals(upperKey)) {
            return 0;
        }

        int lowerValue = historicalEnrollmentData.get(lowerKey) / historicalValueCounts.getOrDefault(lowerKey, 1); // divide by the count of the index if multiple values exist
        int upperValue = historicalEnrollmentData.get(upperKey) / historicalValueCounts.getOrDefault(upperKey, 1); // divide by the count of the index if multiple values exist

        return (int) Math.ceil(lowerValue + (index - lowerKey) / (upperKey - lowerKey) * (upperValue - lowerValue));
    }

    // Applies curve smoothing by using historical ratios of c(d') / h(d')
    /**
     * Applies curve smoothing to the given normal projection based on historical ratios.
     * If there are no historical ratios, the normal projection is returned without any smoothing.
     *
     * @param normalProjection the normal projection value to be smoothed
     * @return the smoothed projection value if historical ratios are available, otherwise the original normal projection
     */
    protected double applyCurveSmoothing(int normalProjection) {
        if (historicalRatios.isEmpty()) {
            return normalProjection; // No smoothing if there are no historical ratios
        }

        double averageRatio = historicalRatios.stream().mapToDouble(Double::doubleValue).average().orElse(1.0);
        return normalProjection * averageRatio;
    }

    // Add a historical ratio to the list for curve smoothing
    /**
     * Adds a historical ratio to the list of historical ratios.
     *
     * @param ratio the historical ratio to be added
     */
    public void addHistoricalRatio(double ratio) {
        historicalRatios.add(ratio);
    }

    // Getter Methods for data access
    /**
     * Retrieves the historical enrollment data.
     *
     * @return a NavigableMap where the keys are enrollment numbers (as Double)
     *         and the values are the corresponding counts (as Integer).
     */
    public NavigableMap<Double, Integer> getHistoricalEnrollmentData() {
        return historicalEnrollmentData;
    }

    /**
     * Retrieves the current enrollment data.
     *
     * @return a NavigableMap where the keys are Double values representing some metric (e.g., GPA, score, etc.)
     *         and the values are Integer counts of students corresponding to each metric.
     */
    public NavigableMap<Double, Integer> getCurrentEnrollmentData() {
        return currentEnrollmentData;
    }

    /**
     * Retrieves the projections of the course.
     *
     * @return a NavigableMap where the keys are Double values representing
     *         some metric (e.g., grade thresholds) and the values are Integer
     *         counts or projections associated with those metrics.
     */
    public NavigableMap<Double, Integer> getProjections() {
        return normalProjections;
    }

    // Method to retrieve the full projection count
    public int getFullProjectionCount() {
        return fullProjectionCount;
    }


    /**
     * Retrieves the current enrollment count for the course.
     * If there is no enrollment data, it returns 0.
     *
     * @return the current enrollment count, or 0 if no data is available
     */
    public int getEnrollmentCount() {
        return currentEnrollmentData.isEmpty() ? 0 : currentEnrollmentData.get(currentEnrollmentData.lastKey());
    }

    /**
     * Retrieves the projection count from the normalProjections map.
     * If the map is empty, it returns 0. Otherwise, it returns the value
     * associated with the highest key in the map.
     *
     * @return the projection count, or 0 if the normalProjections map is empty
     */
    public int getProjectionCount() {
        return normalProjections.isEmpty() ? 0 : normalProjections.get(normalProjections.lastKey());
    }

    public void displayProjections() {
        // Build the output for the course
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-6s %-12d %-12d %-12d\n", 
                                this.courseName, 
                                this.getEnrollmentCount(), 
                                this.getProjectionCount(), 
                                this.getCapacity()));
        
        // Print the result to the console
        System.out.println(sb.toString());
    }
    

    /**
     * Returns a string representation of the projected course details.
     * The string includes the course name and a list of normal projections
     * with their corresponding date fractions.
     *
     * @return A formatted string containing the course name and normal projections.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nProjections for course: ").append(courseName).append("\n");
        normalProjections.forEach((index, count) -> {
            sb.append(String.format("Date Fraction: %.2f | Normal Projection: %d\n", index, count));
        });
        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((courseName == null) ? 0 : courseName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProjectedCourse other = (ProjectedCourse) obj;
        if (courseName == null) {
            if (other.courseName != null)
                return false;
        } else if (!courseName.equals(other.courseName))
            return false;
        return true;
    }
}
