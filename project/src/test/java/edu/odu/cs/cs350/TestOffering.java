package edu.odu.cs.cs350;

import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TestOffering {
    @Test
    public void testOfferingConstructor() {
        Offering offering = new Offering("SC190", 40, 39);
        Offering offering2 = new Offering("", 0, 0);

        assertThat(offering.getCrossListGroup(), is("SC190"));
        assertThat(offering.getOverallCapacity(), is(40));
        assertThat(offering.getOverallEnrollment(), is(39));
        assertThat(offering.getSections(), is(empty()));
        assertThat(offering2.getCrossListGroup(), is(""));
        assertThat(offering2.getOverallCapacity(), is(0));
        assertThat(offering2.getOverallEnrollment(), is(0));
        assertThat(offering2.getSections(), is(empty()));
    }

    @Test
    public void testAddSection() {
        Offering offering = new Offering("SC190", 40, 39);
        Section section = new Section("32107", 35, "R1", 40);
        Section section2 = new Section("", 0, "", 0);

        offering.addSection(section);
        offering.addSection(section2);

        assertThat(offering.getCrossListGroup(), is("SC190"));
        assertThat(offering.getOverallCapacity(), is(40));
        assertThat(offering.getOverallEnrollment(), is(39));
        assertThat(offering.getSections(), hasSize(2));
        assertThat(offering.getSections(), contains(section, section2));
    }
}
