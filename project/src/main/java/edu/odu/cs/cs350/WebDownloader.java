package edu.odu.cs.cs350;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebDownloader {

    private static final Logger logger = Logger.getLogger(WebDownloader.class.getName());
    private static final String BASE_URL = "https://www.cs.odu.edu/~zeil/courseSchedule/History/";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final Map<String, Map<String, LocalDate>> semesterDateRanges = new HashMap<>();

    private void downloadSemesterFolder(String semester, File baseDir) throws IOException {
        String semesterUrl = BASE_URL + semester;
        File semesterDir = new File(baseDir, semester);
    
        if (!semesterDir.exists() && !semesterDir.mkdirs()) {
            throw new IOException("Failed to create directory for semester: " + semester);
        }
    
        // Fetch the list of files in the directory
        List<String> filesInDirectory = fetchFileListFromWeb(semesterUrl);
    
        // Download all files in the directory
        for (String fileName : filesInDirectory) {
            String fileUrl = semesterUrl + "/" + fileName;
            File targetFile = new File(semesterDir, fileName);
    
            try {
                downloadFile(fileUrl, targetFile);
                logger.log(Level.INFO, "Downloaded {0} for semester {1}", new Object[]{fileName, semester});
            } catch (IOException e) {
                logger.log(Level.WARNING, "Failed to download file {0} for semester {1}", new Object[]{fileName, semester});
            }
        }
    }
    
    private List<String> fetchFileListFromWeb(String directoryUrl) throws IOException {
        List<String> fileList = new ArrayList<>();
        HttpURLConnection connection = (HttpURLConnection) new URL(directoryUrl).openConnection();
        connection.setRequestMethod("GET");
    
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            Pattern filePattern = Pattern.compile("href=\"([^\"]*\\.\\w{3,4})\""); // Match valid file extensions like .txt or .csv
            while ((line = reader.readLine()) != null) {
                Matcher matcher = filePattern.matcher(line);
                if (matcher.find()) {
                    String fileName = matcher.group(1);
                    if (!fileName.contains("?")) { // Filter out query strings or invalid file names
                        fileList.add(fileName);
                    }
                }
            }
        } finally {
            connection.disconnect();
        }
    
        return fileList;
    }
    
    
    private void downloadFile(String fileUrl, File targetFile) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(fileUrl).openConnection();
        connection.setRequestMethod("GET");
    
        try (InputStream inputStream = connection.getInputStream()) {
            Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } finally {
            connection.disconnect();
        }
    }
    
    public void downloadAllRelevantData(File baseDir) throws IOException {
        LocalDate today = LocalDate.now();
    
        // Fetch all directories from the web page
        List<String> directories = fetchDirectories(BASE_URL);
    
        // Identify the current semester
        String currentSemester = findCurrentSemester(directories, today);
        if (currentSemester != null) {
            downloadSemesterFiles(currentSemester, baseDir);
        }
    
        // Download data for the previous 4 semesters
        List<String> previousSemesters = getPreviousSemesters(currentSemester, 4);
        for (String semester : previousSemesters) {
            downloadSemesterFiles(semester, baseDir);
        }
    }
    
    
    private void downloadDatesFile(String semester, File baseDir) throws IOException {
        String semesterUrl = BASE_URL + semester + "/dates.txt";
        File semesterDir = new File(baseDir, semester);
    
        if (!semesterDir.exists() && !semesterDir.mkdirs()) {
            throw new IOException("Failed to create directory for semester: " + semester);
        }
    
        File targetFile = new File(semesterDir, "dates.txt");
        HttpURLConnection connection = (HttpURLConnection) new URL(semesterUrl).openConnection();
        connection.setRequestMethod("GET");
    
        try (InputStream inputStream = connection.getInputStream()) {
            Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            logger.log(Level.INFO, "Downloaded dates.txt for semester {0}", semester);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to download dates.txt for semester {0}", semester);
        } finally {
            connection.disconnect();
        }
    }
    private void downloadSemesterFiles(String semester, File baseDir) throws IOException {
        String semesterUrl = BASE_URL + semester;
        File semesterDir = new File(baseDir, semester);
    
        if (!semesterDir.exists() && !semesterDir.mkdirs()) {
            throw new IOException("Failed to create directory for semester: " + semester);
        }
    
        // Download and parse dates.txt
        String datesFileUrl = semesterUrl + "/dates.txt";
        File datesFile = new File(semesterDir, "dates.txt");
        Map<String, LocalDate> dates;
    
        try {
            downloadFile(datesFileUrl, datesFile);
            logger.log(Level.INFO, "Downloaded dates.txt for semester {0}", semester);
            dates = parseDatesTxt(datesFileUrl);
            semesterDateRanges.put(semester, dates); // Save the semester date range
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to download dates.txt for semester {0}. Skipping this semester.", semester);
            return;
        }
    
        // Fetch and download .csv files
        List<String> filesToDownload = fetchFileListFromWeb(semesterUrl);
        for (String fileName : filesToDownload) {
            if (fileName.endsWith(".csv")) {
                downloadCsvFileAcrossSemesters(fileName, semesterUrl, baseDir);
            }
        }
    }
    
    private void downloadCsvFileAcrossSemesters(String fileName, String semesterUrl, File baseDir) throws IOException {
        LocalDate fileDate = extractDateFromFileName(fileName);
        if (fileDate == null) {
            logger.log(Level.WARNING, "Failed to extract date from file name: {0}", fileName);
            return;
        }
    
        boolean fileDownloaded = false;
    
        // Check file against all semester date ranges
        for (Map.Entry<String, Map<String, LocalDate>> entry : semesterDateRanges.entrySet()) {
            String semester = entry.getKey();
            Map<String, LocalDate> dates = entry.getValue();
    
            LocalDate startDate = dates.get("startDate");
            LocalDate endDate = dates.get("endDate");
    
            if (startDate != null && endDate != null && !fileDate.isBefore(startDate) && !fileDate.isAfter(endDate)) {
                File semesterDir = new File(baseDir, semester);
                File targetFile = new File(semesterDir, fileName);
    
                if (!targetFile.exists()) {
                    downloadFile(semesterUrl + "/" + fileName, targetFile);
                    logger.log(Level.INFO, "Downloaded {0} for semester {1}", new Object[]{fileName, semester});
                    fileDownloaded = true;
                }
            }
        }
    
        if (!fileDownloaded) {
            logger.log(Level.INFO, "Skipped {0} (does not fall within any known semester date range).", fileName);
        }
    }
    

    private List<String> fetchDirectories(String url) throws IOException {
        List<String> directories = new ArrayList<>();
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            Pattern directoryPattern = Pattern.compile("href=\"(\\d{6})/\"");
            while ((line = reader.readLine()) != null) {
                Matcher matcher = directoryPattern.matcher(line);
                if (matcher.find()) {
                    directories.add(matcher.group(1));
                }
            }
        } finally {
            connection.disconnect();
        }

        return directories;
    }

    private String findCurrentSemester(List<String> directories, LocalDate today) throws IOException {
        for (String dir : directories) {
            String datesTxtUrl = BASE_URL + dir + "/dates.txt";
            try {
                Map<String, LocalDate> dates = parseDatesTxt(datesTxtUrl);

                LocalDate startDate = dates.get("startDate");
                LocalDate endDate = dates.get("endDate");
                if (startDate != null && endDate != null) {
                    if (!today.isBefore(startDate) && !today.isAfter(endDate)) {
                        logger.log(Level.INFO, "Current semester found: {0}", dir);
                        return dir;
                    }
                } else {
                    logger.log(Level.WARNING, "Invalid or incomplete dates in dates.txt for {0}", dir);
                }
            } catch (IOException e) {
                logger.log(Level.WARNING, "Failed to parse dates.txt for {0}. Skipping this directory.", dir);
            }
        }

        logger.warning("No current semester found.");
        return null;
    }

    private Map<String, LocalDate> parseDatesTxt(String url) throws IOException {
        Map<String, LocalDate> dates = new HashMap<>();
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() != 200) {
            logger.log(Level.SEVERE, "Failed to fetch dates.txt. HTTP response code: {0}", connection.getResponseCode());
            return dates;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            int lineCount = 0;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    try {
                        LocalDate date = LocalDate.parse(line, DATE_FORMATTER);
                        if (lineCount == 0) {
                            dates.put("startDate", date);
                        } else if (lineCount == 1) {
                            dates.put("endDate", date);
                        }
                    } catch (Exception e) {
                        logger.log(Level.WARNING, "Invalid date format in line: {0}", line);
                    }
                    lineCount++;
                }
            }
        } finally {
            connection.disconnect();
        }

        return dates;
    }

    private List<String> getPreviousSemesters(String currentSemester, int count) {
        List<String> previousSemesters = new ArrayList<>();
        int year = Integer.parseInt(currentSemester.substring(0, 4));
        int term = Integer.parseInt(currentSemester.substring(4, 6));

        for (int i = 0; i < count; i++) {
            term -= 10;
            if (term == 0) {
                term = 30;
                year -= 1;
            }
            previousSemesters.add(String.format("%04d%02d", year, term));
        }

        return previousSemesters;
    }



    private void downloadSemesterFiles(String semester, LocalDate today, File baseDir) throws IOException {
        String semesterUrl = BASE_URL + semester;
        File semesterDir = new File(baseDir, semester);

        Map<String, LocalDate> dates = parseDatesTxt(semesterUrl + "/dates.txt");
        List<String> filesToDownload = fetchFileListFromWeb(semesterUrl);

        LocalDate startDate = dates.get("startDate");
        LocalDate endDate = dates.get("endDate");

        if (endDate != null && endDate.isAfter(LocalDate.now().minusYears(1))) {
            for (String fileName : filesToDownload) {
                if (fileName.endsWith(".csv")) {
                    LocalDate fileDate = extractDateFromFileName(fileName);
                    if (fileDate != null && !fileDate.isBefore(startDate) && !fileDate.isAfter(endDate)) {
                        downloadFile(semesterUrl + "/" + fileName, new File(semesterDir, fileName));
                    }
                }
            }
        }
    }


    private LocalDate extractDateFromFileName(String fileName) {
        try {
            // Assumes file names are in the format "YYYY-MM-DD.csv"
            String datePart = fileName.replace(".csv", ""); // Remove the file extension
            return LocalDate.parse(datePart, DATE_FORMATTER);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to extract date from file name: {0}", fileName);
            return null;
        }
    }
    

    public static void main(String[] args) {
        WebDownloader downloader = new WebDownloader();
        File baseDir = new File("C:\\SemesterData");

        try {
            downloader.downloadAllRelevantData(baseDir);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error during download process", e);
        }
    }
}
