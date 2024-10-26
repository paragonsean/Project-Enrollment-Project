package odu.edu.cs.cs350;

import java.io.*;
import java.util.*;
import odu.edu.cs.cs350.CustomLogger;

import java.util.logging.Logger;
/**
 * A generic class to read, parse, and write CSV files.
 */
public class CSVReader {
    private static final Logger logger = Logger.getLogger(CSVReader.class.getName());
    
    private char delimiter = ','; // Default delimiter

    // Default constructor
    public CSVReader() {
        logger.info("CSVReader initialized with default delimiter.");
    }

    // Constructor with custom delimiter
    public CSVReader(char delimiter) {
        this.delimiter = delimiter;
        logger.info("CSVReader initialized with custom delimiter: " + delimiter);
    }

    // Public Methods
    public List<String[]> processCSV(String csvFilePath) throws Exception {
        List<String[]> data = new ArrayList<>(); // Create a list to store rows
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String[] headers = readHeaders(br); // Read headers
            data = readData(br, headers); // Read data rows
        }
        return data;
    }

    // Parses a CSV line into an array of fields. Handles quoted fields and escapes quotes properly.
    public String[] parseCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean insideQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char currentChar = line.charAt(i);
            insideQuotes = processCharacter(currentChar, i, line, fields, currentField, insideQuotes);
        }

        processCurrentField(fields, currentField); // Add the last field
        return fields.toArray(new String[0]);
    }

    // Reads the headers of the CSV file.
    public String[] getHeaders(String csvFilePath) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            return readHeaders(br);
        }
    }

    // Reads the rows of the CSV file.
    public List<String[]> getRows(String csvFilePath) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String[] headers = readHeaders(br); // Read the headers, but only process rows
            return readData(br, headers);
        }
    }

    // Writes data to a CSV file.
    public void writeCSV(String csvFilePath, String[] headers, List<String[]> rows) throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFilePath))) {
            // Write headers
            writer.write(String.join(",", headers));
            writer.newLine();

            // Write data rows
            for (String[] row : rows) {
                writer.write(escapeRow(row));
                writer.newLine();
            }
        }
    }

    // Protected Methods

    // Reads the headers of the CSV file.
    protected String[] readHeaders(BufferedReader br) throws IOException {
        String line = br.readLine(); // Read the header
        if (line == null) return new String[0];
        return parseCSVLine(line);
    }

    // Reads the data rows of the CSV file.
    protected List<String[]> readData(BufferedReader br, String[] headers) throws Exception {
        List<String[]> data = new ArrayList<>();
        String row;
        while ((row = br.readLine()) != null) {
            String[] fields = parseCSVLine(row); // Parse each row
            data.add(processRow(fields, headers));
        }
        return data;
    }

    // Processes a CSV row.
    public String[] processRow(String[] row, String[] headers) throws Exception {
        return row; // Default: return the row as is
    }

    // Private Methods

    private boolean processCharacter(char currentChar, int currentIndex, String line, List<String> fields, StringBuilder currentField, boolean insideQuotes) {
        if (insideQuotes) {
            return handleInsideQuotes(currentChar, currentIndex, line, currentField);
        } else {
            return handleOutsideQuotes(currentChar, fields, currentField);
        }
    }

    private boolean handleInsideQuotes(char currentChar, int currentIndex, String line, StringBuilder currentField) {
        if (isQuoteCharacter(currentChar)) {
            if (isEscapedQuote(currentIndex, line)) {
                currentField.append('"');
                return true;
            } else {
                return false; // End of quoted section
            }
        } else {
            processFieldCharacter(currentField, currentChar);
            return true;
        }
    }

    private boolean handleOutsideQuotes(char currentChar, List<String> fields, StringBuilder currentField) {
        if (isQuoteCharacter(currentChar)) {
            return true; // Start of quoted section
        } else if (isFieldSeparator(currentChar)) {
            processCurrentField(fields, currentField); // End of field
            return false; // Still outside quotes
        } else {
            processFieldCharacter(currentField, currentChar);
            return false;
        }
    }

    private void processCurrentField(List<String> fields, StringBuilder currentField) {
        fields.add(currentField.toString().trim());
        currentField.setLength(0); // Clear the field
    }

    private boolean isEscapedQuote(int currentIndex, String line) {
        return currentIndex + 1 < line.length() && line.charAt(currentIndex + 1) == '"';
    }

    private boolean isQuoteCharacter(char currentChar) {
        return currentChar == '"';
    }

    private boolean isFieldSeparator(char currentChar) {
        return currentChar == delimiter;
    }

    private void processFieldCharacter(StringBuilder currentField, char currentChar) {
        currentField.append(currentChar);
    }

    protected Map<String, Integer> mapHeaders(String[] headers) {
        Map<String, Integer> headerMap = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            headerMap.put(headers[i].trim(), i); // Trim in case of extra spaces
        }
        return headerMap;
    }

    private int parseInt(String value) {
        if (value == null || value.trim().isEmpty()) return 0;
        return Integer.parseInt(value.trim());
    }

    public void processCSVAndCalculateTotals(String csvFilePath) throws Exception {
        Map<String, CourseTotals> courseTotalsMap = new HashMap<>();
    
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String[] headers = readHeaders(br);
            Map<String, Integer> headerMap = mapHeaders(headers);
    
            String row;
            while ((row = br.readLine()) != null) {
                String[] fields = parseCSVLine(row);
    
                // Extract necessary fields
                String subj = fields[headerMap.get("SUBJ")];
                String crse = fields[headerMap.get("CRSE")];
                int xlstCap = parseInt(fields[headerMap.get("XLST CAP")]);
                int enr = parseInt(fields[headerMap.get("ENR")]);
                String link = fields[headerMap.get("LINK")];
                String xlstGroup = fields[headerMap.get("XLST GROUP")];
                String overallCap = fields[headerMap.get("OVERALL CAP")];
                String overallEnr = fields[headerMap.get("OVERALL ENR")];
    
                // Create a unique key based on SUBJ and CRSE
                String courseKey = subj + crse;
    
                // If the course already exists, accumulate the totals
                if (courseTotalsMap.containsKey(courseKey)) {
                    CourseTotals existingTotals = courseTotalsMap.get(courseKey);
                    existingTotals.addToTotals(xlstCap, enr);
                } else {
                    // Otherwise, create a new CourseTotals object for this course
                    CourseTotals newTotals = new CourseTotals(subj, crse, xlstCap, enr, link, xlstGroup, overallCap, overallEnr);
                    courseTotalsMap.put(courseKey, newTotals);
                }
            }
    
            // After processing all rows, log the combined totals for each course
            for (CourseTotals totals : courseTotalsMap.values()) {
                CustomLogger.logInfo("Course: " + totals.subj + totals.crse + "\n" +
                                     "Total XLST CAP: " + totals.xlstCap + "\n" +
                                     "Total ENR: " + totals.enr + "\n" +
                                     "LINK: " + totals.link + "\n" +
                                     "XLST GROUP: " + totals.xlstGroup + "\n" +
                                     "OVERALL CAP: " + totals.overallCap + "\n" +
                                     "OVERALL ENR: " + totals.overallEnr + "\n" +
                                     "---------");
            }
        }
    }
    
    
    

    // Escapes fields in a row and formats them for CSV output.
    private String escapeRow(String[] row) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < row.length; i++) {
            String field = row[i];
            if (field.contains(",") || field.contains("\"")) {
                field = "\"" + field.replace("\"", "\"\"") + "\""; // Escape quotes and wrap in quotes
            }
            sb.append(field);
            if (i < row.length - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    // Main method for testing
    public static void main(String[] args) {
        CSVReader csvReader = new CSVReader();
        try {
            // Check if the file path is provided as an argument
            if (args.length < 1) {
                CustomLogger.logError("Please provide a CSV file path as an argument.");
                return;
            }
    
            // Path to the CSV file
            String csvFilePath = args[0];
    
            // Read and process the CSV file to calculate totals for XLST CAP and ENR
            CustomLogger.logInfo("Processing CSV to calculate totals...");
            csvReader.processCSVAndCalculateTotals(csvFilePath);
    
            // Read the headers and rows from the CSV file
            CustomLogger.logInfo("Reading CSV file...");
            String[] headers = csvReader.readHeaders(new BufferedReader(new FileReader(csvFilePath)));
            List<String[]> rows = csvReader.processCSV(csvFilePath);
    
            // Define an output file path
            String outputFilePath = "output.csv";
    
            // Write the data to a new CSV file
            CustomLogger.logInfo("Writing to new CSV file...");
            csvReader.writeCSV(outputFilePath, headers, rows);
    
            CustomLogger.logInfo("CSV has been written to " + outputFilePath);
        } catch (Exception e) {
            CustomLogger.logError("An error occurred: " + e.getMessage());
        }
    }
    
    
    

}