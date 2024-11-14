
package edu.odu.cs.cs350;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class TestOffering {

    private Offering offering;

    @BeforeEach
    public void setUp() {
        offering = new Offering("CS101-01", 100, 50);
    }

    @Test
    public void testConstructor() {
        assertEquals("CS101-01", offering.getOfferingKey());
        assertEquals("CS101", offering.getCrossListGroup());
        assertEquals(100, offering.getOverallCapacity());
        assertEquals(50, offering.getOverallEnrollment());
    }

    @Test
    public void testAddSection() {
        offering.addSection("12345", 30, 25, "link1");
        Map<String, Section> sections = offering.getSections();
        assertTrue(sections.containsKey("12345"));
        Section section = sections.get("12345");
        assertEquals(30, section.getCrossListCapacity());
        assertEquals(25, section.getEnrollment());
        assertEquals("link1", section.getLink());
    }

    @Test
    public void testAddSection_UpdateExisting() {
        offering.addSection("12345", 30, 25, "link1");
        offering.addSection("12345", 35, 30, "link2");
        Map<String, Section> sections = offering.getSections();
        assertTrue(sections.containsKey("12345"));
        Section section = sections.get("12345");
        assertEquals(35, section.getCrossListCapacity());
        assertEquals(30, section.getEnrollment());
        assertEquals("link2", section.getLink());
    }

    @Test
    public void testToString() {
        String expected = "Offering{offeringKey='CS101-01', crossListGroup='CS101', sections=[], 50/100 students currently enrolled}";
        assertEquals(expected, offering.toString());
    }

    @Test
    public void testEqualsAndHashCode() {
        Offering offering1 = new Offering("CS101-01", 100, 50);
        Offering offering2 = new Offering("CS101-01", 100, 50);
        Offering offering3 = new Offering("CS102-01", 100, 50);

        assertEquals(offering1, offering2);
        assertNotEquals(offering1, offering3);
        assertEquals(offering1.hashCode(), offering2.hashCode());
        assertNotEquals(offering1.hashCode(), offering3.hashCode());
    }
}