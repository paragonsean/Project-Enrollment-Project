package edu.odu.cs.cs350;

import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TestSection {
    @Test
    public void testSectionConstructor() {
        Section section = new Section("32107", 35, "R1", 40);
        Section section2 = new Section("", 0, "", 0);

        assertThat(section.getCourseRegistrationNumber(), is("32107"));
        assertThat(section.getEnrollment(), is(35));
        assertThat(section.getLink(), is("R1"));
        assertThat(section.getCrossListCapacity(), is(40));
        assertThat(section2.getCourseRegistrationNumber(), is(""));
        assertThat(section2.getEnrollment(), is(0));
        assertThat(section2.getLink(), is(""));
        assertThat(section2.getCrossListCapacity(), is(0));
    }
}
