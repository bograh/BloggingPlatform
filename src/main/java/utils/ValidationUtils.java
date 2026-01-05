package utils;

import exceptions.InvalidInputException;
import exceptions.InvalidIntegerException;

/**
 * Utility class providing validation methods for user input and business rules.
 * Centralizes validation logic to ensure consistency across the application.
 */
public class ValidationUtils {


    /**
     * Validates that an input field is not null or empty (after trimming whitespace).
     *
     * @param input     the input
     * @param fieldName the field name
     */
    public static void validateNonEmpty(String input, String fieldName) {
        if (input == null || input.trim().isEmpty()) {
            throw new InvalidInputException(fieldName + " cannot be empty");
        }
    }

    /**
     * Validates that an integer is not negative.
     *
     * @param n the input
     */
    public static void validatePositiveInteger(int n) {
        if (n < 0) {
            throw new InvalidIntegerException("Integer value cannot be negative");
        }
    }

    /**
     * Validates that a text input is not null or empty (after trimming whitespace).
     *
     * @param input the text to validate
     * @return true if the input is not null and contains non-whitespace characters, false otherwise
     */
    public boolean validateNonEmptyText(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new InvalidInputException("Input cannot be empty");
        }
        return true;
    }

}
