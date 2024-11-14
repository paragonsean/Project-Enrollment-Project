package edu.odu.cs.cs350;
import java.io.File;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;



public class SemesterTest {

    private Semester semester;
    private String name;
    private LocalDate preRegDate;
    private LocalDate addDeadline;
    private List<File> csvFiles;

    @BeforeEach
    public void setUp() {
        name = "Fall 2023";
        preRegDate = LocalDate.of(2023, 8, 1);
        addDeadline = LocalDate.of(2023, 9, 1);
        csvFiles = Arrays.asList(new File("file1.csv"), new File("file2.csv"));
        semester = new Semester(name, preRegDate, addDeadline, csvFiles);
    }

    @Test
    public void testConstructor() {
        assertThat(semester.getName(), is(name));
        assertThat(semester.getPreRegDate(), is(preRegDate));
        assertThat(semester.getAddDeadline(), is(addDeadline));
        assertThat(semester.getCsvFiles(), is(csvFiles));
        assertThat(semester.getSnapshots(), is(empty()));
    }

    @Test
    public void testCreateSemester() {
        Semester createdSemester = Semester.createSemester(name, preRegDate, addDeadline, csvFiles);
        assertThat(createdSemester.getName(), is(name));
        assertThat(createdSemester.getPreRegDate(), is(preRegDate));
        assertThat(createdSemester.getAddDeadline(), is(addDeadline));
        assertThat(createdSemester.getCsvFiles(), is(csvFiles));
    
        CsvProcessor.processCsvFilesToSnapshots(createdSemester);
    }

    @Test
    public void testAddSnapshot() {
        Snapshot snapshot = mock(Snapshot.class);
        when(snapshot.getDate()).thenReturn(LocalDate.of(2023, 8, 15));
        semester.addSnapshot(snapshot);
        assertThat(semester.getSnapshotByDate(LocalDate.of(2023, 8, 15)), is(snapshot));
    }

    @Test
    public void testGetSnapshotByDate() {
        Snapshot snapshot = mock(Snapshot.class);
        when(snapshot.getDate()).thenReturn(LocalDate.of(2023, 8, 15));
        semester.addSnapshot(snapshot);
        assertThat(semester.getSnapshotByDate(LocalDate.of(2023, 8, 15)), is(snapshot));
        assertThat(semester.getSnapshotByDate(LocalDate.of(2023, 8, 16)), is(nullValue()));
    }

    @Test
    public void testGetSnapshots() {
        Snapshot snapshot1 = mock(Snapshot.class);
        when(snapshot1.getDate()).thenReturn(LocalDate.of(2023, 8, 15));
        Snapshot snapshot2 = mock(Snapshot.class);
        when(snapshot2.getDate()).thenReturn(LocalDate.of(2023, 8, 16));
        semester.addSnapshot(snapshot1);
        semester.addSnapshot(snapshot2);
        assertThat(semester.getSnapshots(), containsInAnyOrder(snapshot1, snapshot2));
    }

    @Test
    public void testIterator() {
        Snapshot snapshot1 = mock(Snapshot.class);
        when(snapshot1.getDate()).thenReturn(LocalDate.of(2023, 8, 15));
        Snapshot snapshot2 = mock(Snapshot.class);
        when(snapshot2.getDate()).thenReturn(LocalDate.of(2023, 8, 16));
        semester.addSnapshot(snapshot1);
        semester.addSnapshot(snapshot2);
        assertThat(semester, containsInAnyOrder(snapshot1, snapshot2));
    }
}