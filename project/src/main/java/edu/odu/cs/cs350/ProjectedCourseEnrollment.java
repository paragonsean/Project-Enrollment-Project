package edu.odu.cs.cs350;

import java.util.*;
import java.util.Map.Entry;

public class ProjectedCourseEnrollment {
    private String courseName;
    private int enrollmentCapacity;

    private final NavigableMap<Double, Integer> historicalEnrollmentData = new TreeMap<>();
    private final NavigableMap<Double, Integer> currentEnrollmentData = new TreeMap<>();
    private final NavigableMap<Double, Integer> normalProjections = new TreeMap<>();
    private final NavigableMap<Double, Integer> trapezoidalProjections = new TreeMap<>();
    private final NavigableMap<Double, Integer> historicalValueCounts = new TreeMap<>();

    public ProjectedCourseEnrollment(String courseName, int enrollmentCapacity) {
        this.courseName = courseName;
        this.enrollmentCapacity = enrollmentCapacity;
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

    // Generates projections for quarters
    public void generateProjectionsForQuarters() {
        double[] targetIndices = {0.25, 0.50, 0.75, 1.0}; // Quarter indices: 25%, 50%, 75%, 100%
        for (double targetIndex : targetIndices) {
            generateProjection(targetIndex);
        }
    }

    // Generates a projection for a specific quarter
    public void generateProjection(double targetIndex) {
        if (currentEnrollmentData.isEmpty()) {
            throw new IllegalStateException("Current enrollment data is empty. Cannot generate projection.");
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

        // Trapezoidal projection: Based on the trapezoidal area
        int trapezoidalProjection = calculateTrapezoidalProjection(targetIndex);

        // Store both projections
        normalProjections.put(targetIndex, normalProjection);
        trapezoidalProjections.put(targetIndex, trapezoidalProjection);
    }

    // Interpolates historical enrollment data for a given index
    private int interpolateHistoricalEnrollment(double index) {
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

    // Calculates the trapezoidal projection for a given quarter (targetIndex)
    private int calculateTrapezoidalProjection(double targetIndex) {
        // Calculate the trapezoidal area based on the historical enrollment data
        double area = 0.0;
        List<Map.Entry<Double, Integer>> entries = new ArrayList<>(historicalEnrollmentData.entrySet());

        // Find the previous and next data points around the targetIndex
        for (int i = 1; i < entries.size(); i++) {
            Map.Entry<Double, Integer> prev = entries.get(i - 1);
            Map.Entry<Double, Integer> curr = entries.get(i);

            // Calculate the width and height of the trapezoid
            double width = curr.getKey() - prev.getKey();
            double height = (prev.getValue() + curr.getValue()) / 2.0;
            area += width * height;
        }

        // Normalize the trapezoidal area based on the target index
        double normalizedArea = area * targetIndex;

        return (int) Math.ceil(normalizedArea);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Projections for course: ").append(courseName).append("\n");
        normalProjections.forEach((index, count) -> {
            sb.append(String.format("Date Fraction: %.2f | Normal Projection: %d | Trapezoidal Projection: %d\n",
                    index, count, trapezoidalProjections.get(index)));
        });
        return sb.toString();
    }


}


