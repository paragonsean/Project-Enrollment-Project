package edu.odu.cs.cs350;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class SnapshotTest {

    private Course course1;
    private Course course2;

    @Before
    public void setUp() {
        course1 = new Course("CS", "101");
        course1.addOfferingsAndSections("CRN101", "XLST101", 20, 15, 30, 30, "LNK001");

        course2 = new Course("CS", "102");
        course2.addOfferingsAndSections("CRN102", "XLST102", 15, 10, 25, 25, "LNK002");
    }

    @Test
    public void testDefaultConstructor() {
        Snapshot snapshot = new Snapshot();
        assertNotNull(snapshot);
        assertEquals("00000000.csv", snapshot.getFileName());
        assertNotNull(snapshot.getDate());
        assertTrue(snapshot.getCourses().isEmpty());
    }

    @Test
    public void testParameterizedConstructorWithFilename() {
        List<Course> courses = Arrays.asList(course1, course2);
        Snapshot snapshot = new Snapshot("2023-10-01.csv", courses);

        assertNotNull(snapshot);
        assertEquals("2023-10-01.csv", snapshot.getFileName());
        assertEquals(LocalDate.of(2023, 10, 1), snapshot.getDate());
        assertEquals(2, snapshot.getCourses().size());
        assertTrue(snapshot.getCourses().containsKey("CS101"));
        assertTrue(hasEntry("CS102", course2).matches(snapshot.getCourses()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParameterizedConstructorWithInvalidFilename() {
        Snapshot snapshot = new Snapshot("invalid_filename.csv", Arrays.asList(course1, course2));
    }

    @Test
    public void testParameterizedConstructorWithDate() {
        List<Course> courses = Arrays.asList(course1, course2);
        LocalDate date = LocalDate.of(2023, 10, 1);
        Snapshot snapshot = new Snapshot(date, courses);

        assertNotNull(snapshot);
        assertEquals(date, snapshot.getDate());
        assertEquals(2, snapshot.getCourses().size());
        assertTrue(hasKey("CS101").matches(snapshot.getCourses()));
        assertTrue(hasEntry("CS102", course2).matches(snapshot.getCourses()));
    }

    @Test
    public void testAddCourse() {
        Snapshot snapshot = new Snapshot();
        snapshot.addCourse(course1);

        assertEquals(1, snapshot.getCourses().size());
        assertTrue(hasKey("CS101").matches(snapshot.getCourses()));
    }

    @Test
    public void testGetCourse() {
        Snapshot snapshot = new Snapshot();
        snapshot.addCourse(course1);

        Course course = snapshot.getCourse("CS101");
        assertNotNull(course);
        assertEquals(course1, course);
    }

    @Test
    public void testGetCourseEnrollments() {
        List<Course> courses = Arrays.asList(course1, course2);
        Snapshot snapshot = new Snapshot("2023-10-01.csv", courses);

        Map<String, Integer> enrollments = snapshot.getCourseEnrollments();
        assertEquals(2, enrollments.size());
        assertEquals(Integer.valueOf(30), enrollments.get("CS101"));
        assertEquals(Integer.valueOf(25), enrollments.get("CS102"));
    }

    @Test
    public void testGetTotalSectionEnrollment() {
        List<Course> courses = Arrays.asList(course1, course2);
        Snapshot snapshot = new Snapshot("2023-10-01.csv", courses);

        int totalEnrollment = snapshot.getCourseCapacity(course1.getCourseKey());
        assertEquals(25, totalEnrollment);
    }

    @Test
    public void testGetTotalSectionCapacity() {
        List<Course> courses = Arrays.asList(course1, course2);
        Snapshot snapshot = new Snapshot("2023-10-01.csv", courses);

        int totalCapacity = snapshot.getCourseCapacity(course2.getCourseKey());
        assertEquals(35, totalCapacity);
    }

    @Test
    public void testToString() {
        List<Course> courses = Arrays.asList(course1, course2);
        Snapshot snapshot = new Snapshot("2023-10-01.csv", courses);

        String snapshotString = snapshot.toString();
        assertTrue(snapshotString.contains("Snapshot Filename: 2023-10-01.csv"));
        assertTrue(snapshotString.contains("Date: 2023-10-01"));
        assertTrue(snapshotString.contains(course1.toString()));
        assertTrue(snapshotString.contains(course2.toString()));
    }
}
