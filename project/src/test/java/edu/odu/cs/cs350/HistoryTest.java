// package edu.odu.cs.cs350;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.io.TempDir;

// import java.io.BufferedWriter;
// import java.io.File;
// import java.io.FileWriter;
// import java.io.IOException;
// import java.util.List;

// import static org.hamcrest.MatcherAssert.assertThat;
// import static org.hamcrest.Matchers.*;

// public class HistoryTest {

//     private History history;

//     @TempDir
//     File tempDir;

//     @BeforeEach
//     public void setUp() throws IOException {
//         // Initialize the History class with the temporary directory
//         history = new History(tempDir.getAbsolutePath());
//     }

//     @Test
//     public void testAddAndGetSemesters() {
//         Semester semester1 = new Semester("Fall2023", tempDir.getAbsolutePath(), null, null, List.of(), null);
//         Semester semester2 = new Semester("Spring2023", tempDir.getAbsolutePath(), null, null, List.of(), null);

//         history.addSemester(semester1);
//         history.addSemester(semester2);

//         List<Semester> semesters = history.getSemesters();
//         assertThat(semesters, hasSize(2));
//     }

//     @Test
//     public void testLoadSemestersFromRootDirectory() throws IOException {
//         // Simulate a semester directory with `dates.txt`
//         File semesterDir = new File(tempDir, "Fall2023");
//         semesterDir.mkdir();

//         File datesFile = new File(semesterDir, "dates.txt");
//         try (BufferedWriter writer = new BufferedWriter(new FileWriter(datesFile))) {
//             writer.write("2023-08-01\n2023-12-01\n");
//         }

//         // Initialize history and load semesters
//         history.loadSemestersFromRootDirectory(tempDir.getAbsolutePath());

//         List<Semester> semesters = history.getSemesters();
//         assertThat(semesters, hasSize(1));
//         assertThat(semesters.get(0).getName(), is("Fall2023"));
//     }

//     @Test
//     public void testAddDuplicateSemester() {
//         Semester semester = new Semester("Fall2023", tempDir.getAbsolutePath(), null, null, List.of(), null);

//         history.addSemester(semester);
//         history.addSemester(semester); // Duplicate add

//         List<Semester> semesters = history.getSemesters();
//         assertThat(semesters, hasSize(1));
//         assertThat(semesters.get(0).getName(), is("Fall2023"));
//     }

//     @Test
//     public void testEmptySemesters() {
//         List<Semester> semesters = history.getSemesters();
//         assertThat(semesters, is(empty()));
//     }
// }
