package edu.odu.cs.cs350;

import java.util.*;
import java.util.Map.Entry;

public class ProjectedCourse {
    private String courseName;

    private final NavigableMap<Double, Integer> historicalEnrollmentData = new TreeMap<>();
    private final NavigableMap<Double, Integer> currentEnrollmentData = new TreeMap<>();
    private final NavigableMap<Double, Integer> normalProjections = new TreeMap<>();
    private final NavigableMap<Double, Integer> historicalValueCounts = new TreeMap<>();

    protected final List<Double> historicalRatios = new ArrayList<>(); // To store historical ratios
    private int fullProjectionCount; // To store the full projection count
    private int capacity; // To store the course capacity
    public ProjectedCourse(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseKey() {
        return this.courseName;
    }
    public void setCourseCapacity(int capacity) {
        this.capacity = capacity;   // Set the course capacity
    }
    // Adds historical enrollment data
    public void addHistoricalEnrollment(double index, int count) {
        historicalEnrollmentData.merge(index, count, Integer::sum);
        historicalValueCounts.merge(index, 1, Integer::sum);
    }

    // Adds current enrollment data
    public void addCurrentEnrollment(double index, int value) {
        currentEnrollmentData.put(index, value);
    }
    public int getCapacity() {
        return capacity;
    }
    public String getCourseName() {
        return courseName;
    }
    // Generates projections for quarters
    public void generateProjectionsForQuarters() {
        double[] targetIndices = {0.0,0.1, 0.2, 0.25, 0.3, 0.4, 0.50, 0.75, 1.0}; // Quarter indices: 25%, 50%, 75%, 100%
        for (double targetIndex : targetIndices) {
            generateProjection(targetIndex);
        }
    }

    // Generates a projection for a specific quarter
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
    protected int interpolateHistoricalEnrollment(double index) {
        if (historicalEnrollmentData.containsKey(index)) {
            return historicalEnrollmentData.get(index) / historicalValueCounts.getOrDefault(index, 1);
        }

        Double lowerKey = historicalEnrollmentData.floorKey(index);
        Double upperKey = historicalEnrollmentData.ceilingKey(index);

        if (lowerKey == null || upperKey == null || lowerKey.equals(upperKey)) {
            return 0;
        }

        int lowerValue = historicalEnrollmentData.get(lowerKey) / historicalValueCounts.getOrDefault(lowerKey, 1);
        int upperValue = historicalEnrollmentData.get(upperKey) / historicalValueCounts.getOrDefault(upperKey, 1);

        return (int) Math.ceil(lowerValue + (index - lowerKey) / (upperKey - lowerKey) * (upperValue - lowerValue));
    }

    // Applies curve smoothing by using historical ratios of c(d') / h(d')
    protected double applyCurveSmoothing(int normalProjection) {
        if (historicalRatios.isEmpty()) {
            return normalProjection; // No smoothing if there are no historical ratios
        }

        double averageRatio = historicalRatios.stream().mapToDouble(Double::doubleValue).average().orElse(1.0);
        return normalProjection * averageRatio;
    }

    // Add a historical ratio to the list for curve smoothing
    public void addHistoricalRatio(double ratio) {
        historicalRatios.add(ratio);
    }

    // Getter Methods for data access
    public NavigableMap<Double, Integer> getHistoricalEnrollmentData() {
        return historicalEnrollmentData;
    }

    public NavigableMap<Double, Integer> getCurrentEnrollmentData() {
        return currentEnrollmentData;
    }

    public NavigableMap<Double, Integer> getProjections() {
        return normalProjections;
    }

    // Method to retrieve the full projection count
    public int getFullProjectionCount() {
        return fullProjectionCount;
    }

    // Returns the current enrollment count based on the last key
    public int getEnrollmentCount() {
        return currentEnrollmentData.isEmpty() ? 0 : currentEnrollmentData.get(currentEnrollmentData.lastKey());
    }

    // Returns the maximum projection count
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
