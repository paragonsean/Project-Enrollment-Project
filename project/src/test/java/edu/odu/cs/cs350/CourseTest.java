package edu.odu.cs.cs350;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CourseTest {
    private Map<String, Course> coursesByKey;
    private Course course;
    private Course course2;

    @BeforeEach
    public void setUp() {
        coursesByKey = new HashMap<>();
        course = new Course("CS", "350");
        course2 = new Course("CS", "350");
    }

    // Method to add a course to the snapshot
    public void addCourse(Course course) {
        String courseKey = course.getCourseKey();
        if (coursesByKey.containsKey(courseKey)) {
            coursesByKey.get(courseKey).mergeCourse(course);
        } else {
            coursesByKey.put(courseKey, course);
        }
    }

    @Test
    public void testAddAndMergeCourses() {
        // Create 5 courses with the same key "CS350" and enrollment of 10 each
        Course course1 = new Course("CS", "350");
        course1.addOfferingsAndSections("CRN120", "XLST1", 10, 10, 50, 50, "link0");
        addCourse(course1);
    
        Course course2 = new Course("CS", "350");
        course2.addOfferingsAndSections("CRN121", "XLST1", 10, 10, 50, 10, "link1");
        addCourse(course2);
    
        Course course3 = new Course("CS", "350");
        course3.addOfferingsAndSections("CRN122", "XLST1", 10, 10, 50, 10, "link2");
        addCourse(course3);
    
        Course course4 = new Course("CS", "350");
        course4.addOfferingsAndSections("CRN123", "XLST1", 10, 10, 50, 10, "link3");
        addCourse(course4);
    
        Course course5 = new Course("CS", "350");
        course5.addOfferingsAndSections("CRN124", "XLST1", 10, 10, 50, 10, "link4");
        addCourse(course5);
    
        // Verify that only one entry exists for the same course key "CS350"
        assertEquals(1, coursesByKey.entrySet().stream().filter(entry -> entry.getKey().equals("CS350")).count());

        // Verify merged values for "CS350"
        Course mergedCourse = coursesByKey.get("CS350");
        assertNotNull(mergedCourse, "Merged course should exist for key 'CS350'");
        assertEquals(1, mergedCourse.getOfferings().size(), "Total offerings should reflect merged sections");
        assertEquals(50, mergedCourse.getTotalSectionEnrollment(), "Total section enrollment should reflect merged enrollment (5 * 10)");
        assertEquals(50, mergedCourse.getTotalSectionCapacity(), "Total section capacity should reflect merged capacity (5 * 10)");
        assertEquals(50, mergedCourse.getTotalOfferingEnrollment(), "Total offering enrollment should reflect merged enrollment (5 * 10)");
        System.out.println(mergedCourse.getTotalOfferingCapacity());
        assertEquals(50, mergedCourse.getTotalOfferingCapacity(), "Total offering capacity should reflect merged capacity (5 * 10)");

        // Create 5 courses with different keys ("CS351" to "CS355") and enrollment of 10 each
        Course differentCourse1 = new Course("CS", "351");
        differentCourse1.addOfferingsAndSections("CRN131", "XLST2", 10, 10, 10, 10, "link5");
        addCourse(differentCourse1);
    
        Course differentCourse2 = new Course("CS", "352");
        differentCourse2.addOfferingsAndSections("CRN132", "XLST3", 10, 10, 10, 10, "link6");
        addCourse(differentCourse2);
    
        Course differentCourse3 = new Course("CS", "353");
        differentCourse3.addOfferingsAndSections("CRN133", "XLST4", 10, 10, 10, 10, "link7");
        addCourse(differentCourse3);
    
        Course differentCourse4 = new Course("CS", "354");
        differentCourse4.addOfferingsAndSections("CRN134", "XLST5", 10, 10, 10, 10, "link8");
        addCourse(differentCourse4);
    
        Course differentCourse5 = new Course("CS", "355");
        differentCourse5.addOfferingsAndSections("CRN135", "XLST6", 10, 10, 10, 10, "link9");
        addCourse(differentCourse5);
    
        // Verify that each different course was added separately
        assertTrue(coursesByKey.containsKey("CS351"), "Course with key 'CS351' should exist independently.");
        assertTrue(coursesByKey.containsKey("CS352"), "Course with key 'CS352' should exist independently.");
        assertTrue(coursesByKey.containsKey("CS353"), "Course with key 'CS353' should exist independently.");
        assertTrue(coursesByKey.containsKey("CS354"), "Course with key 'CS354' should exist independently.");
        assertTrue(coursesByKey.containsKey("CS355"), "Course with key 'CS355' should exist independently.");
    
        // Verify each different course's properties
        assertEquals(1, coursesByKey.get("CS351").getOfferings().size(), "CS351 should have 1 offering");
        assertEquals(10, coursesByKey.get("CS351").getTotalSectionEnrollment(), "CS351 should have an enrollment of 10");
        assertEquals(10, coursesByKey.get("CS351").getTotalSectionCapacity(), "CS351 should have a capacity of 10");
    
        assertEquals(1, coursesByKey.get("CS352").getOfferings().size(), "CS352 should have 1 offering");
        assertEquals(10, coursesByKey.get("CS352").getTotalSectionEnrollment(), "CS352 should have an enrollment of 10");
        assertEquals(10, coursesByKey.get("CS352").getTotalSectionCapacity(), "CS352 should have a capacity of 10");
    
        assertEquals(1, coursesByKey.get("CS353").getOfferings().size(), "CS353 should have 1 offering");
        assertEquals(10, coursesByKey.get("CS353").getTotalSectionEnrollment(), "CS353 should have an enrollment of 10");
        assertEquals(10, coursesByKey.get("CS353").getTotalSectionCapacity(), "CS353 should have a capacity of 10");
    
        assertEquals(1, coursesByKey.get("CS354").getOfferings().size(), "CS354 should have 1 offering");
        assertEquals(10, coursesByKey.get("CS354").getTotalSectionEnrollment(), "CS354 should have an enrollment of 10");
        assertEquals(10, coursesByKey.get("CS354").getTotalSectionCapacity(), "CS354 should have a capacity of 10");
    
        assertEquals(1, coursesByKey.get("CS355").getOfferings().size(), "CS355 should have 1 offering");
        assertEquals(10, coursesByKey.get("CS355").getTotalSectionEnrollment(), "CS355 should have an enrollment of 10");
        assertEquals(10, coursesByKey.get("CS355").getTotalSectionCapacity(), "CS355 should have a capacity of 10");
    
        // Validate that the map contains exactly 6 keys (1 merged and 5 unique)
        assertEquals(6, coursesByKey.size(), "Map should contain 1 merged course and 5 unique courses");
    }
    
    @Test
    public void testAddOfferingsAndSections() {
        course.addOfferingsAndSections("CRN123", "XLST2", 10, 10, 10, 10, "link");
        course.addOfferingsAndSections("CRN1234", "XLST2", 10, 10, 10, 10, "link");
        Offering offering = course.getOfferings().get("XLST2-CS350");
        assertEquals(10, offering.getOverallCapacity());
        assertEquals(10, offering.getOverallEnrollment());
        assertEquals(20, course.getTotalSectionEnrollment());
        assertEquals(10, course.getTotalOfferingEnrollment());
        assertEquals(10, course.getTotalOfferingCapacity());
        assertEquals(20, course.getTotalSectionCapacity());
    }

    @Test
    public void testGetTotalOfferingEnrollment() {
        course.addOfferingsAndSections("CRN12347", "XLST2", 10, 10, 10, 10, "link");
        assertEquals(10, course.getTotalOfferingEnrollment());
    }

    @Test
    public void testGetTotalSectionEnrollment() {
        course.addOfferingsAndSections("CRN12347", "XLST2", 10, 10, 10, 10, "link");
        course.addOfferingsAndSections("CRN12348", "XLST2", 10, 10, 10, 10, "link");
        assertEquals(20, course.getTotalSectionEnrollment());
    }

    @Test
    public void testGetTotalOfferingCapacity() {
        course.addOfferingsAndSections("CRN12347", "XLST2", 10, 10, 10, 10, "link");
        assertEquals(10, course.getTotalOfferingCapacity());
    }

    @Test
    public void testGetTotalSectionCapacity() {
        course.addOfferingsAndSections("CRN12347", "XLST2", 10, 10, 10, 10, "link");
        assertEquals(10, course.getTotalSectionCapacity());
    }

    

    @Test
    public void testToString() {
        course.addOfferingsAndSections("CRN12347", "XLST2", 10, 10, 10, 10, "link");
        String courseString = course.toString();
        assertTrue(courseString.contains("subject='CS'"));
        assertTrue(courseString.contains("courseNumber=350"));
        assertTrue(courseString.contains("offerings=1"));
    }

    @Test
    public void testEqualsAndHashCode() {
        Course sameCourse = new Course("CS", "350");
        assertEquals(course, sameCourse);
        assertEquals(course.hashCode(), sameCourse.hashCode());
        Course differentCourse = new Course("CS", "351");
        assertNotEquals(course, differentCourse);
        assertNotEquals(course.hashCode(), differentCourse.hashCode());
    }

    @Test
    public void testMergeCourse() {
        course.addOfferingsAndSections("CRN123", "XLST1", 10, 10, 10, 10, "link");
        course2.addOfferingsAndSections("CRN124", "XLST1", 10, 10, 10, 10, "link");
        course.mergeCourse(course2);

        assertEquals(20, course.getTotalSectionEnrollment(), "Total section enrollment should reflect merged values");
        assertEquals(10, course.getTotalOfferingCapacity(), "Total offering capacity should remain the same");
    }

    @Test
    public void testMergeCourseThrowsException() {
        Course differentCourse = new Course("CS", "351");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            course.mergeCourse(differentCourse);
        });
        String expectedMessage = "Cannot merge courses with different keys";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
}
