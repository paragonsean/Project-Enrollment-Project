// package edu.odu.cs.cs350.loadFolder;

// import java.io.File;
// import java.util.Scanner;
// import java.time.LocalDate;

// public class LoadFolder {
//     public loadFolder(String path){ //https://stackoverflow.com/questions/3154488/how-do-i-iterate-through-the-files-in-a-directory-and-its-sub-directories-in-ja
//         File folder = new File(path);
//         int code;
//         LocalDate preRegistrationDate;
//         LocalDate deadlineDate;
//         String snapshotPath;
//         for (File subFolder : folder.listFiles()){
//             if (subFolder.isDirectory()){ //which should be every file (exclude any non folders)
//                 code = Integer.parseInt(subFolder.getName()); //TODO: error catching
//                 File datesFile = new File(subFolder.getPath() + "/dates.txt");
//                 if (datesFile.exists() && !datesFile.isDirectory()){ //https://stackoverflow.com/questions/1816673/how-do-i-check-if-a-file-exists-in-java
//                     if (subFolder.listFiles().length > 3) //includes 1 dates.txt + 2 snapshot files
//                     {
//                         //parse the datesFile
//                         Scanner scanner = new Scanner(datesFile);
//                         preRegistrationDate = LocalDate.parse(scanner.nextLine()); //https://stackoverflow.com/questions/18873014/parse-string-date-in-yyyy-mm-dd-format
//                         deadlineDate = LocalDate.parse(scanner.nextLine());
//                         scanner.close();
//                         for (File file : subFolder.listFiles()){
//                             if (file.getName() == "dates.txt"){
//                                 continue;
//                             }
//                             else{
//                                 snapshotPath = file.getAbsolutePath();
//                                 //TODO: individual snapshot files, parse them to the snapshot class
//                                 //combine the snapshots, make them fit between the preregistration & deadline
//                             }
//                         }
//                     }
//                     else{
//                         //TODO: throw IOException: "Insufficient semester files" in <semester code>
//                     }
//                 }
//                 else{
//                     //TODO: throw IOException "missing dates.txt" in <semester code>
//                 }
//             }
//         }
//     }
// }
