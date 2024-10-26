package edu.odu.cs.cs350;

import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TestSnapshot {
    @Test
    public void testSnapshotConstructor() {
        Snapshot snapshot = new Snapshot("2024-10-25");
        Snapshot snapshot2 = new Snapshot("");

        assertThat(snapshot.getDate(), is("2024-10-25"));
        assertThat(snapshot.getCourses(), is(empty()));
        assertThat(snapshot2.getDate(), is(""));
        assertThat(snapshot2.getCourses(), is(empty()));
    }

    @Test
    public void testAddCourse() {
        Snapshot snapshot = new Snapshot("2024-10-25");
        Course course = new Course("CS", "121G");
        Course course2 = new Course("", "");

        snapshot.addCourse(course);
        snapshot.addCourse(course2);

        assertThat(snapshot.getDate(), is("2024-10-25"));
        assertThat(snapshot.getCourses(), hasSize(2));
        assertThat(snapshot.getCourses(), contains(course, course2));
    }
}
