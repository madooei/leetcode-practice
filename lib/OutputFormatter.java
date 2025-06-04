import java.util.*;

/**
 * OutputFormatter - Utility class for output normalization and formatting
 * 
 * This class provides standardized formatting and normalization capabilities for test outputs:
 * - Consistent output formatting across different data types
 * - Normalization for accurate result comparison
 * - Handling of various output formats (arrays, lists, primitives)
 * - Whitespace and formatting standardization
 * 
 * The formatter ensures that output comparisons are reliable regardless of
 * minor formatting differences, making test validation more robust.
 */
public class OutputFormatter {

    /**
     * Normalizes output for consistent comparison
     * 
     * This method standardizes output formatting by:
     * - Removing extra whitespace around brackets and parentheses
     * - Standardizing comma spacing in arrays and lists
     * - Normalizing boolean value representations
     * - Trimming leading/trailing whitespace
     * 
     * @param output The output string to normalize
     * @return Normalized output string for comparison
     */
    public static String normalizeOutput(String output) {
        if (output == null) {
            return "null";
        }

        // Start with trimmed input
        String normalized = output.trim();

        // Normalize comma spacing: [1, 2, 3] -> [1,2,3]
        normalized = normalized.replaceAll(", ", ",");

        // Remove extra whitespace around brackets: [ 1,2,3 ] -> [1,2,3]
        normalized = normalized.replaceAll("\\[\\s+", "[");
        normalized = normalized.replaceAll("\\s+\\]", "]");

        // Remove extra whitespace around parentheses: ( 1,2,3 ) -> (1,2,3)
        normalized = normalized.replaceAll("\\(\\s+", "(");
        normalized = normalized.replaceAll("\\s+\\)", ")");

        // Normalize boolean values: True/False -> true/false
        normalized = normalized.replaceAll("\\bTrue\\b", "true");
        normalized = normalized.replaceAll("\\bFalse\\b", "false");

        return normalized;
    }

    /**
     * Converts various result types to standardized string representation
     * 
     * This method handles different Java types and converts them to consistent
     * string representations for display and comparison purposes.
     * 
     * @param result The result object to convert
     * @return String representation of the result
     */
    public static String resultToString(Object result) {
        if (result == null) {
            return "null";
        }

        // Handle different result types with appropriate formatting
        if (result instanceof List) {
            return formatList((List<?>) result);
        } else if (result instanceof Integer[]) {
            return formatIntegerArray((Integer[]) result);
        } else if (result instanceof int[]) {
            return formatIntArray((int[]) result);
        } else if (result instanceof String[]) {
            return formatStringArray((String[]) result);
        } else if (result instanceof boolean[]) {
            return formatBooleanArray((boolean[]) result);
        } else if (result instanceof Object[]) {
            return formatObjectArray((Object[]) result);
        } else if (result instanceof Boolean) {
            return formatBoolean((Boolean) result);
        } else {
            return result.toString();
        }
    }

    /**
     * Formats a List object to string representation
     * 
     * @param list The list to format
     * @return Formatted string representation
     */
    private static String formatList(List<?> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            Object item = list.get(i);
            sb.append(item == null ? "null" : item.toString());
        }

        sb.append("]");
        return sb.toString();
    }

    /**
     * Formats an Integer array to string representation
     * 
     * @param array The Integer array to format
     * @return Formatted string representation
     */
    private static String formatIntegerArray(Integer[] array) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(array[i] == null ? "null" : array[i].toString());
        }

        sb.append("]");
        return sb.toString();
    }

    /**
     * Formats an int array to string representation
     * 
     * @param array The int array to format
     * @return Formatted string representation
     */
    private static String formatIntArray(int[] array) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(array[i]);
        }

        sb.append("]");
        return sb.toString();
    }

    /**
     * Formats a String array to string representation
     * 
     * @param array The String array to format
     * @return Formatted string representation
     */
    private static String formatStringArray(String[] array) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            if (array[i] == null) {
                sb.append("null");
            } else {
                sb.append("\"").append(array[i]).append("\"");
            }
        }

        sb.append("]");
        return sb.toString();
    }

    /**
     * Formats a boolean array to string representation
     * 
     * @param array The boolean array to format
     * @return Formatted string representation
     */
    private static String formatBooleanArray(boolean[] array) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(array[i] ? "true" : "false");
        }

        sb.append("]");
        return sb.toString();
    }

    /**
     * Formats a generic Object array to string representation
     * 
     * @param array The Object array to format
     * @return Formatted string representation
     */
    private static String formatObjectArray(Object[] array) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            Object item = array[i];
            sb.append(item == null ? "null" : item.toString());
        }

        sb.append("]");
        return sb.toString();
    }

    /**
     * Formats a Boolean object to string representation
     * 
     * @param bool The Boolean object to format
     * @return Formatted string representation
     */
    private static String formatBoolean(Boolean bool) {
        return bool ? "true" : "false";
    }

    /**
     * Creates a formatted display string for test input
     * 
     * This method provides a user-friendly representation of test input
     * that may include additional formatting for readability.
     * 
     * @param input The input object to format
     * @return Formatted display string
     */
    public static String formatInputForDisplay(Object input) {
        if (input == null) {
            return "null";
        }

        String inputStr = input.toString();

        // For very long inputs, provide truncation with ellipsis
        if (inputStr.length() > 100) {
            return inputStr.substring(0, 97) + "...";
        }

        return inputStr;
    }

    /**
     * Creates a side-by-side comparison format for expected vs actual results
     * 
     * This method formats the expected and actual results in a way that makes
     * differences more visible for debugging purposes.
     * 
     * @param expected The expected result
     * @param actual The actual result
     * @return Formatted comparison string
     */
    public static String formatComparison(String expected, String actual) {
        StringBuilder comparison = new StringBuilder();

        comparison.append("Expected: ").append(expected).append("\n");
        comparison.append("Actual:   ").append(actual).append("\n");

        // Add difference indicators if strings differ
        if (!normalizeOutput(expected).equals(normalizeOutput(actual))) {
            comparison.append("Diff:     ");
            highlightDifferences(expected, actual, comparison);
        }

        return comparison.toString();
    }

    /**
     * Highlights character-level differences between two strings
     * 
     * This method provides detailed difference analysis by marking
     * positions where the expected and actual outputs differ.
     * 
     * @param expected The expected string
     * @param actual The actual string
     * @param output StringBuilder to append the difference markers
     */
    private static void highlightDifferences(String expected, String actual, StringBuilder output) {
        int maxLength = Math.max(expected.length(), actual.length());

        for (int i = 0; i < maxLength; i++) {
            char expectedChar = (i < expected.length()) ? expected.charAt(i) : ' ';
            char actualChar = (i < actual.length()) ? actual.charAt(i) : ' ';

            if (expectedChar != actualChar) {
                output.append("^"); // Mark differences with caret
            } else {
                output.append(" "); // Space for matching characters
            }
        }
    }

    /**
     * Formats performance metrics for display
     * 
     * @param performance The performance result to format
     * @return Formatted performance string
     */
    public static String formatPerformance(TestDataModels.PerformanceResult performance) {
        if (performance == null) {
            return "No performance data";
        }

        return String.format("⏱️  %.3f ms | 💾 %.2f MB (%d iterations)",
                performance.getExecutionTimeMs(), performance.getMemoryUsedMB(),
                performance.iterations);
    }

    /**
     * Formats a test summary line with status indicators
     * 
     * @param testName Name of the test
     * @param passed Whether the test passed
     * @param hasError Whether there was an execution error
     * @return Formatted test summary line
     */
    public static String formatTestSummary(String testName, boolean passed, boolean hasError) {
        String status;
        if (hasError) {
            status = "❌ ERROR";
        } else if (passed) {
            status = "✅ PASS";
        } else {
            status = "❌ FAIL";
        }

        return String.format("%-30s %s", testName, status);
    }

    /**
     * Formats an overall test run summary
     * 
     * @param passed Number of passed tests
     * @param total Total number of tests
     * @return Formatted summary string
     */
    public static String formatOverallSummary(int passed, int total) {
        double percentage = total > 0 ? (passed * 100.0 / total) : 0.0;

        String emoji = (passed == total) ? "🎉" : "⚠️";

        return String.format("%s %d/%d tests passed (%.1f%%)", emoji, passed, total, percentage);
    }
}
