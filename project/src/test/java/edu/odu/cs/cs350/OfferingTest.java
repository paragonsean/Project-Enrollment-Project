package edu.odu.cs.cs350;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.Iterator;

public class OfferingTest {

    private Offering offering;
    private Section section1;
    private Section section2;

    @Before
    public void setUp() {
        // Initialize offering with all required parameters
        offering = new Offering("CS", "418", "XLST01", 100, "Dr. Smith");

        // Initialize sections with the necessary parameters
        section1 = new Section("12345", "Fall 2022", "Main", 30, 25, "A1", "XLST01");
        section2 = new Section("54321", "Fall 2022", "Main", 30, 15, "A1", "XLST01");
    }

    @Test
    public void testConstructor() {
        assertEquals("CS", offering.getSubj());
        assertEquals("418", offering.getCrse());
        assertEquals("XLST01", offering.getXlstGroup());
        assertEquals(100, offering.getOverallCap());
        assertEquals("Dr. Smith", offering.getProfessor());
    }

    @Test
    public void testAddSection() {
        assertTrue(offering.addSection(section1));
        assertFalse(offering.addSection(section1)); // Duplicate CRN in the same offering
        
        Section section3 = new Section("67890", "Fall 2022", "Main", 30, 20, "B1", "XLST02");
        assertFalse(offering.addSection(section3)); // Different XLST_GROUP should fail
    }

    @Test
    public void testGetOverallEnr() {
        offering.addSection(section1);
        offering.addSection(section2);
        section1.enrollStudent(); // ENR becomes 26
        section2.enrollStudent(); // ENR becomes 16
        assertEquals(41, offering.getOverallEnr());
    }

    @Test
    public void testCanEnrollMoreStudents() {
        offering.addSection(section1);
        offering.addSection(section2);
        
        // Enroll students but keep it below the overall cap
        for (int i = 0; i < 5; i++) {
            section1.enrollStudent();
            section2.enrollStudent();
        }
        assertTrue(offering.canEnrollMoreStudents());

        // Fill up the sections to reach the overall cap
        for (int i = 0; i < 35; i++) {
            section1.enrollStudent();
        }
        for (int i = 0; i < 35; i++) {
            section2.enrollStudent();
        }
        assertFalse(offering.canEnrollMoreStudents());
    }

    @Test
    public void testEnrollStudentInSection() {
        offering.addSection(section1);
        assertTrue(offering.enrollStudentInSection("12345")); // CRN exists, should enroll
        assertFalse(offering.enrollStudentInSection("99999")); // Non-existent CRN, should fail
    }

    @Test
    public void testIterator() {
        offering.addSection(section1);
        offering.addSection(section2);
        Iterator<Section> iterator = offering.iterator();
        
        assertTrue(iterator.hasNext());
        assertEquals(section1, iterator.next());
        
        assertTrue(iterator.hasNext());
        assertEquals(section2, iterator.next());
        
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testToString() {
        offering.addSection(section1);
        String output = offering.toString();
        
        // Check if essential information is contained in the output
        assertTrue(output.contains("Course Name=CS418"));
        assertTrue(output.contains("Cross-list Group=XLST01"));
        assertTrue(output.contains("Overall Capacity=100"));
        assertTrue(output.contains("Total Enrollment=25"));
        assertTrue(output.contains("CRN=12345"));
    }

    @Test
    public void testEquals() {
        Offering offering2 = new Offering("CS", "418", "XLST01", 100, "Dr. Smith");
        
        // Both offerings are empty, so they should be equal
        assertTrue(offering.equals(offering2));
        
        // Add sections to one offering only
        offering.addSection(section1);
        offering2.addSection(section2); // Sections differ, so equals should now be false
        assertFalse(offering.equals(offering2));
    }
}
