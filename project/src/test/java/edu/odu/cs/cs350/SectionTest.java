package edu.odu.cs.cs350;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import edu.odu.cs.cs350.Section;
import edu.odu.cs.cs350.EnrollmentException;




    public class SectionTest {
    
        private Section section;
    
        @Before
        public void setUp() {
            section = new Section("12345", "fall", "main", 30, 25, "A1", "GROUP1");
        }
    
        @Test
        public void testGetCrn() {
            assertEquals("12345", section.getCrn());
        }
    
        @Test
        public void testGetSemester() {
            assertEquals("fall", section.getSemester());
        }
    
        @Test
        public void testGetCampus() {
            assertEquals("main", section.getCampus());
        }
    
        @Test
        public void testGetXlstGroup() {
            assertEquals("GROUP1", section.getXlstGroup());
        }
    
        @Test
        public void testGetXlstCap() {
            assertEquals(30, section.getXlstCap());
        }
    
        @Test
        public void testGetEnr() {
            assertEquals(25, section.getEnr());
        }
    
        @Test
        public void testGetLink() {
            assertEquals("A1", section.getLink());
        }
    
        @Test
        public void testIsLecture() {
            assertTrue(section.isLecture());
        }
    
        @Test
        public void testEnrollStudentSuccess() {
            assertTrue(section.enrollStudent());
            assertEquals(26, section.getEnr());
        }
    
        @Test
        public void testEnrollStudentFailure() {
            section = new Section("12345", "fall", "main", 25, 25, "A1", "GROUP1");
            assertFalse(section.enrollStudent());
            assertEquals(25, section.getEnr());
        }
    
        @Test
        public void testHashCode() {
            Section sameSection = new Section("12345", "fall", "main", 30, 25, "A1", "GROUP1");
            assertEquals(section.hashCode(), sameSection.hashCode());
        }
    
        @Test
        public void testEquals() {
            Section sameSection = new Section("12345", "fall", "main", 30, 25, "A1", "GROUP1");
            Section differentSection = new Section("54321", "spring", "main", 30, 25, "B1", "GROUP2");
            assertTrue(section.equals(sameSection));
            assertFalse(section.equals(differentSection));
        }
    
        @Test
        public void testToString() {
            String expected = "Section [CRN=12345, XLST_CAP=30, ENR=25, LINK=A1, semester=fall, campus=main, crossListed=false]";
            assertEquals(expected, section.toString());
        }
    }



