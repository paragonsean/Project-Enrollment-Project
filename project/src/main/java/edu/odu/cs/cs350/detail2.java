package edu.odu.cs.cs350;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.checkerframework.checker.units.qual.s;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DetailedReportGenerator {

    private static final Logger logger = Logger.getLogger(DetailedReportGenerator.class.getName());

    public static void generateReport(Map<String,ProjectedCourse> projectionResults, String outputFilePath) {
        try (Workbook workbook = new XSSFWorkbook()) {
            for (Map.Entry<String, ProjectedCourse> entry : projectionResults.entrySet()) {
                ProjectedCourse course = entry.getValue();
                Sheet sheet = workbook.createSheet(course.getCourseKey());
                createHeaderRow(sheet);
                populateDataRows(sheet, course);
                createChart(sheet, course);
            }

            try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
                workbook.write(fos);
                logger.info("Detailed projection report generated: " + outputFilePath);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error generating detailed projection report", e);
        }
    }

    private static void createHeaderRow(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Quarter");
        headerRow.createCell(1).setCellValue("Historical Enrollment");
        headerRow.createCell(2).setCellValue("Current Enrollment");
        headerRow.createCell(3).setCellValue("Projected Enrollment");
    }

    private static void populateDataRows(Sheet sheet, ProjectedCourse course) {
        double[] quarters = {0.0, 0.25, 0.5, 0.75, 1.0};
        int rowIndex = 1;

        for (double quarter : quarters) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(quarter);

            int historicalEnrollment = course.interpolateHistoricalEnrollment(quarter);
            row.createCell(1).setCellValue(historicalEnrollment);

            int currentEnrollment = course.getCurrentEnrollmentData().getOrDefault(quarter, 0);
            row.createCell(2).setCellValue(currentEnrollment);

            int projectedEnrollment = course.getProjections().getOrDefault(quarter, 0);
            row.createCell(3).setCellValue(projectedEnrollment);
        }
    }
private static void createChart(Sheet sheet, ProjectedCourse course){
    System.out.println("do nothing");
}
}
