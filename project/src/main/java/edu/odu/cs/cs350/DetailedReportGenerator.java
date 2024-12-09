package edu.odu.cs.cs350;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DetailedReportGenerator {

    private static final Logger logger = Logger.getLogger(DetailedReportGenerator.class.getName());

    public static void generateReport(Map<String, ProjectedCourse> projectionResults, String outputFilePath) {
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
        double[] quarters = {0.0, 0.20, 0.3, 0.4, 0.5, 0.75, 1.0};
        int rowIndex = 1;
        int lastValue = 0;

        for (double quarter : quarters) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(quarter);

            int historicalEnrollment = course.interpolateHistoricalEnrollment(quarter);
            row.createCell(1).setCellValue(historicalEnrollment);

            int currentEnrollment = course.getCurrentEnrollmentData().getOrDefault(quarter, lastValue);
            row.createCell(2).setCellValue(currentEnrollment);

            lastValue = currentEnrollment;

            int projectedEnrollment = course.getProjections().getOrDefault(quarter, lastValue);
            row.createCell(3).setCellValue(projectedEnrollment);

            lastValue = projectedEnrollment;
        }
    }

    private static void createChart(Sheet sheet, ProjectedCourse course) {
        XSSFDrawing drawing = (XSSFDrawing) sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 5, 1, 15, 20);
        XDDFChart chart = drawing.createChart(anchor);
        XDDFChartLegend legend = chart.getOrAddLegend();
        legend.setPosition(LegendPosition.TOP_RIGHT);

        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        bottomAxis.setTitle("Quarter");
        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setTitle("Enrollment");

        XDDFLineChartData data = (XDDFLineChartData) chart.createData(ChartTypes.LINE, bottomAxis, leftAxis);

        double[] quarters = {0.0, 0.2, 0.3, 0.4, 0.5, 0.75, 1.0};

        XDDFDataSource<Double> quartersData = XDDFDataSourcesFactory.fromArray(Arrays.stream(quarters).boxed().toArray(Double[]::new));
        XDDFNumericalDataSource<Integer> historicalData = XDDFDataSourcesFactory.fromArray(
                Arrays.stream(quarters)
                        .mapToInt(course::interpolateHistoricalEnrollment)
                        .boxed()
                        .toArray(Integer[]::new));
        XDDFLineChartData.Series historicalSeries = (XDDFLineChartData.Series) data.addSeries(quartersData, historicalData);
        historicalSeries.setTitle("Historical Enrollment", null);

        XDDFNumericalDataSource<Integer> currentData = XDDFDataSourcesFactory.fromArray(
                Arrays.stream(quarters)
                        .mapToInt(q -> course.getCurrentEnrollmentData().getOrDefault(q, 0))
                        .boxed()
                        .toArray(Integer[]::new));
        XDDFLineChartData.Series currentSeries = (XDDFLineChartData.Series) data.addSeries(quartersData, currentData);
        currentSeries.setTitle("Current Enrollment", null);

        XDDFNumericalDataSource<Integer> projectedData = XDDFDataSourcesFactory.fromArray(
                Arrays.stream(quarters)
                        .mapToInt(q -> course.getProjections().getOrDefault(q, currentData.getPointAt(currentData.getPointCount() - 1).intValue()))
                        .boxed()
                        .toArray(Integer[]::new));
        XDDFLineChartData.Series projectedSeries = (XDDFLineChartData.Series) data.addSeries(quartersData, projectedData);
        projectedSeries.setTitle("Projected Enrollment", null);

        projectedSeries.setSmooth(true);
        chart.plot(data);
    }
}
