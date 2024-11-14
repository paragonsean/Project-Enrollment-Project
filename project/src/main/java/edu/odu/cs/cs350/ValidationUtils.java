package edu.odu.cs.cs350;

import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ValidationUtils {
    private static final Logger logger = Logger.getLogger(ValidationUtils.class.getName());

    public static void validateOfferingParameters(String offeringKey, String xlstGroup) {
        if (offeringKey == null) {
            String message = "Invalid parameter: Null value provided for offeringKey, xlstGroup, instructor, or semesterCode";
            logger.log(Level.SEVERE, message);
            throw new IllegalArgumentException(message);
        }
    }

    public static void validateSectionParameters(String crn) {
        if (crn == null) {
            String message = "Invalid parameter: Null value provided for one or more section attributes";
            logger.log(Level.SEVERE, message);
            throw new IllegalArgumentException(message);
        }
    }

    public static void validateCapacity(int capacity) {
        if (capacity < 0) {
            String message = "Section capacity cannot be negative";
            logger.log(Level.SEVERE, message);
            throw new IllegalArgumentException(message);
        }
    }

    public static void validateEnrollment(int enrollment, int capacity) {
        if (enrollment < 0) {
            String message = "Section enrollment cannot be negative";
            logger.log(Level.SEVERE, message);
            throw new IllegalArgumentException(message);
        }
        if (enrollment > capacity) {
            enrollment = capacity;
            String message = "Section enrollment cannot exceed section capacity";
            logger.log(Level.SEVERE, message);
        }
    }

    public static void validateCourseParameters(String subject, String courseNumber) {
        if (subject == null || subject.trim().isEmpty() || courseNumber == null) {
            String message = "Invalid parameter: Null or empty value provided for subject or date";
            logger.log(Level.SEVERE, message);
            throw new IllegalArgumentException(message);
        }
    }

    public static String generateSemesterCode(LocalDate date) {
        int year = date.getYear();
        int month = date.getMonthValue();
        String semester;

        if (month >= 1 && month <= 5) {
            semester = "SPR";
        } else if (month >= 6 && month <= 8) {
            semester = "SUM";
        } else {
            semester = "FAL";
        }

        return year + semester;
    }

    public static String sanitizeLink(String link) {
        return (link == null || link.trim().isEmpty()) ? "1" : link;
    }

    public static String determineXlstGroup(String xlstGroup, String crn) {
        return (xlstGroup == null || xlstGroup.trim().isEmpty()) ? crn : xlstGroup;
    }
}
