package edu.odu.cs.cs350;

import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TestCourse {
    @Test
    public void testCourseConstructor() {
        Course course = new Course("CS", "121G");
        Course course2 = new Course("", "");

        assertThat(course.getSubject(), is("CS"));
        assertThat(course.getCourseNumber(), is("121G"));
        assertThat(course.getCourseName(), is("CS121G"));
        assertThat(course.getOfferings(), is(empty()));
        assertThat(course2.getSubject(), is(""));
        assertThat(course2.getCourseNumber(), is(""));
        assertThat(course2.getCourseName(), is(""));
        assertThat(course2.getOfferings(), is(empty()));
    }

    @Test
    public void testAddOffering() {
        Course course = new Course("CS", "121G");
        Offering offering = new Offering("SC190", 40, 39);
        Offering offering2 = new Offering("", 0, 0);

        course.addOffering(offering);
        course.addOffering(offering2);

        assertThat(course.getSubject(), is("CS"));
        assertThat(course.getCourseNumber(), is("121G"));
        assertThat(course.getCourseName(), is("CS121G"));
        assertThat(course.getOfferings(), hasSize(2));
        assertThat(course.getOfferings(), contains(offering, offering2));
    }
}
