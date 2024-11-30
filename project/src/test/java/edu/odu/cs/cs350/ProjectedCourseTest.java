package edu.odu.cs.cs350;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NavigableMap;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectedCourseTest {

    private ProjectedCourse projectedCourse;

    @BeforeEach
    public void setUp() {
        projectedCourse = new ProjectedCourse("CS350");
    }

    @Test
    public void testGetCourseKey() {
        assertEquals("CS350", projectedCourse.getCourseKey());
    }

    @Test
    public void testAddHistoricalEnrollment() {
        projectedCourse.addHistoricalEnrollment(0.25, 50);
        NavigableMap<Double, Integer> historicalData = projectedCourse.getHistoricalEnrollmentData();
        assertEquals(50, historicalData.get(0.25));
    }

    @Test
    public void testAddCurrentEnrollment() {
        projectedCourse.addCurrentEnrollment(0.25, 50);
        NavigableMap<Double, Integer> currentData = projectedCourse.getCurrentEnrollmentData();
        assertEquals(50, currentData.get(0.25));
    }

    @Test
    public void testGenerateProjectionsForQuarters() {
        projectedCourse.addCurrentEnrollment(0.25, 50);
        projectedCourse.addHistoricalEnrollment(0.25, 40);
        projectedCourse.addHistoricalEnrollment(0.50, 80);
        projectedCourse.generateProjectionsForQuarters();
        NavigableMap<Double, Integer> projections = projectedCourse.getProjections();
        assertFalse(projections.isEmpty());
    }

    @Test
    public void testGenerateProjection() {
        projectedCourse.addCurrentEnrollment(0.25, 50);
        projectedCourse.addHistoricalEnrollment(0.25, 40);
        projectedCourse.addHistoricalEnrollment(0.50, 80);
        projectedCourse.generateProjection(0.50);
        NavigableMap<Double, Integer> projections = projectedCourse.getProjections();
        assertEquals(100, projections.get(0.50));
    }

    @Test
    public void testInterpolateHistoricalEnrollment() {
        projectedCourse.addHistoricalEnrollment(0.25, 40);
        projectedCourse.addHistoricalEnrollment(0.50, 80);
        int interpolatedValue = projectedCourse.interpolateHistoricalEnrollment(0.375);
        assertEquals(60, interpolatedValue);
    }

    @Test
    public void testApplyCurveSmoothing() {
        projectedCourse.addHistoricalRatio(1.2);
        projectedCourse.addHistoricalRatio(0.8);
        double smoothedValue = projectedCourse.applyCurveSmoothing(100);
        assertEquals(100, smoothedValue, 0.1);
    }

    @Test
    public void testAddHistoricalRatio() {
        projectedCourse.addHistoricalRatio(1.2);
        assertFalse(projectedCourse.historicalRatios.isEmpty());
    }

    @Test
    public void testGetFullProjectionCount() {
        projectedCourse.addCurrentEnrollment(0.25, 50);
        projectedCourse.addHistoricalEnrollment(0.25, 40);
        projectedCourse.addHistoricalEnrollment(0.30, 40);
        projectedCourse.addHistoricalEnrollment(0.75, 75);
        projectedCourse.addHistoricalEnrollment(0.8, 80);
        projectedCourse.addHistoricalEnrollment(1.01, 85);
        projectedCourse.generateProjection(1.0);
        projectedCourse.generateProjectionsForQuarters();
        System.out.println(projectedCourse);
        System.out.println(projectedCourse.getProjectionCount());
        assertEquals(100, projectedCourse.getFullProjectionCount());
    }

    @Test
    public void testGetEnrollmentCount() {
        projectedCourse.addCurrentEnrollment(0.25, 50);
        assertEquals(50, projectedCourse.getEnrollmentCount());
    }

    @Test
    public void testGetProjectionCount() {
        projectedCourse.addCurrentEnrollment(0.25, 50);
        projectedCourse.addHistoricalEnrollment(0.25, 40);
        projectedCourse.addHistoricalEnrollment(0.50, 80);
        projectedCourse.generateProjection(1.0);
        System.out.println(projectedCourse.getFullProjectionCount());
        assertEquals(100, projectedCourse.getFullProjectionCount());
    }

    @Test
    public void testDisplayProjections() {
        projectedCourse.addCurrentEnrollment(0.25, 50);
        projectedCourse.addHistoricalEnrollment(0.25, 40);
        projectedCourse.addHistoricalEnrollment(0.50, 80);
        projectedCourse.generateProjectionsForQuarters();
        projectedCourse.displayProjections();
        // This test primarily prints output to the console. Verify manually or redirect stdout in advanced testing.
    }

    @Test
    public void testToString() {
        projectedCourse.addCurrentEnrollment(0.25, 50);
        projectedCourse.addHistoricalEnrollment(0.25, 40);
        projectedCourse.addHistoricalEnrollment(0.50, 80);
        projectedCourse.generateProjectionsForQuarters();
        String result = projectedCourse.toString();
        assertTrue(result.contains("Projections for course: CS350"));
    }
}
