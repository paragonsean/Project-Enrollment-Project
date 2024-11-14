package edu.odu.cs.cs350;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SectionTest {

    private Section section;

    @BeforeEach
    public void setUp() {
        section = new Section("CRN12345", 30, 25, "LNK001");
    }

    @Test
    public void testConstructorValidParameters() {
        assertDoesNotThrow(() -> new Section("CRN12345", 30, 25, "LNK001"));
    }

    @Test
    public void testConstructorInvalidParameters() {
        // Simulate invalid parameters by directly testing the constructor's behavior.
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new Section(null, 30, 25, "LNK001");
        });
        assertEquals("Invalid parameter: Null value provided for one or more section attributes", thrown.getMessage());
    }

    @Test
    public void testSetCrossListCapacityValid() {
        assertDoesNotThrow(() -> section.setCrossListCapacity(35));
        assertEquals(35, section.getCrossListCapacity());
    }

    @Test
    public void testSetCrossListCapacityInvalid() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            section.setCrossListCapacity(-5);
        });
        assertEquals("Section capacity cannot be negative", thrown.getMessage());
    }

    @Test
    public void testSetEnrollmentValid() {
        assertDoesNotThrow(() -> section.setEnrollment(28));
        assertEquals(28, section.getEnrollment());
    }

 
    @Test
    public void testGetCrossListCapacity() {
        assertEquals(30, section.getCrossListCapacity());
    }

    @Test
    public void testGetEnrollment() {
        assertEquals(25, section.getEnrollment());
    }

    @Test
    public void testSetLink() {
        section.setLink("LNK002");
        assertEquals("LNK002", section.getLink());
    }

    @Test
    public void testGetCourseRegistrationNumber() {
        assertEquals("CRN12345", section.getCourseRegistrationNumber());
    }

    @Test
    public void testGetLink() {
        assertEquals("LNK001", section.getLink());
    }

    @Test
    public void testToString() {
        String expected = "Section{courseRegistrationNumber='CRN12345', 25/30 students currently enrolled, link='LNK001'}";
        assertEquals(expected, section.toString());
    }

    @Test
    public void testHashCode() {
        Section anotherSection = new Section("CRN12345", 30, 25, "LNK001");
        assertEquals(section.hashCode(), anotherSection.hashCode());
    }

    @Test
    public void testEquals() {
        Section anotherSection = new Section("CRN12345", 30, 25, "LNK001");
        assertTrue(section.equals(anotherSection));
        assertFalse(section.equals(null));
        assertFalse(section.equals(new Object()));
    }
}
