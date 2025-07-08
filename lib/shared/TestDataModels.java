package shared;

import java.util.*;

/**
 * TestDataModels - Core data structures for test case management
 * 
 * This class contains all the fundamental data models used throughout the testing framework.
 * It includes structures for test cases, performance measurements, and test results.
 * These models provide a clean separation of data representation from business logic.
 */
public class TestDataModels {

    /**
     * TestCase - Represents a single test case with input, expected output, and metadata
     * 
     * This immutable data structure encapsulates all information needed to execute
     * a single test case including the test name, input data, expected result,
     * optional type hints for explicit input interpretation, and optional description
     * for documentation purposes.
     * 
     * Enhanced to support both legacy single-input format and new multi-input format
     * with optional type hints for precise data structure interpretation.
     */
    public static class TestCase {
        /** The name/identifier of the test case */
        public final String name;

        /** The input data for the test (as string representation or JSON array) */
        public final String input;

        /** Optional type hints for input interpretation (null for automatic detection) */
        public final String[] inputTypes;

        /** The expected output/result (as string representation) */
        public final String expected;

        /** Optional description explaining what the test verifies */
        public final String description;

        /**
         * Constructs a TestCase from a map of string data
         * 
         * Supports both legacy format and new format with inputTypes:
         * Legacy: {"name": "test", "input": "value", "expected": "result"}
         * New: {"name": "test", "input": ["val1", "val2"], "inputTypes": ["int", "String"], "expected": "result"}
         * 
         * @param data Map containing test case data with keys: name, input, expected, description, inputTypes
         */
        public TestCase(Map<String, Object> data) {
            this.name = (String) data.getOrDefault("name", "Unknown");

            // Handle both string and array input formats
            Object inputObj = data.get("input");
            if (inputObj instanceof String) {
                this.input = (String) inputObj;
            } else if (inputObj instanceof List) {
                // Convert list to JSON array string
                List<?> inputList = (List<?>) inputObj;
                this.input = convertListToJsonArray(inputList);
            } else {
                this.input = "";
            }

            this.expected = (String) data.getOrDefault("expected", "");
            this.description = (String) data.getOrDefault("description", "");

            // Handle inputTypes field (optional)
            Object typesObj = data.get("inputTypes");
            if (typesObj instanceof List) {
                List<?> typesList = (List<?>) typesObj;
                this.inputTypes = typesList.stream().map(Object::toString).toArray(String[]::new);
            } else {
                this.inputTypes = null; // Use automatic type detection
            }
        }


        /**
         * Converts a list to JSON array string representation
         * 
         * @param list The list to convert
         * @return JSON array string
         */
        private String convertListToJsonArray(List<?> list) {
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                if (i > 0)
                    sb.append(", ");
                Object item = list.get(i);
                // Don't add extra quotes - preserve the original string format
                sb.append("\"").append(item).append("\"");
            }
            sb.append("]");
            return sb.toString();
        }

        /**
         * Checks if this test case has explicit type hints
         * 
         * @return true if inputTypes are specified
         */
        public boolean hasTypeHints() {
            return inputTypes != null && inputTypes.length > 0;
        }

        /**
         * String representation of the test case for debugging
         */
        @Override
        public String toString() {
            String typeInfo = hasTypeHints() ? ", types=" + Arrays.toString(inputTypes) : "";
            return String.format("TestCase{name='%s', input='%s', expected='%s'%s}", name, input,
                    expected, typeInfo);
        }
    }

    /**
     * PerformanceResult - Encapsulates performance measurement data
     * 
     * This class stores timing and memory usage metrics from test execution.
     * It provides convenient methods to convert raw measurements into
     * human-readable formats (milliseconds, megabytes).
     */
    public static class PerformanceResult {
        /** Execution time in nanoseconds */
        public final long executionTimeNanos;

        /** Memory usage in bytes */
        public final long memoryUsedBytes;

        /** Number of iterations used for measurement */
        public final int iterations;

        /**
         * Constructs a PerformanceResult with measurement data
         * 
         * @param executionTimeNanos Total execution time in nanoseconds
         * @param memoryUsedBytes Memory used in bytes
         * @param iterations Number of measurement iterations
         */
        public PerformanceResult(long executionTimeNanos, long memoryUsedBytes, int iterations) {
            this.executionTimeNanos = executionTimeNanos;
            this.memoryUsedBytes = memoryUsedBytes;
            this.iterations = iterations;
        }

        /**
         * Converts execution time to milliseconds
         * 
         * @return Execution time in milliseconds (double precision)
         */
        public double getExecutionTimeMs() {
            return executionTimeNanos / 1_000_000.0;
        }

        /**
         * Converts memory usage to megabytes
         * 
         * @return Memory usage in megabytes (double precision)
         */
        public double getMemoryUsedMB() {
            return memoryUsedBytes / (1024.0 * 1024.0);
        }

        /**
         * Human-readable representation of performance metrics
         */
        @Override
        public String toString() {
            return String.format("Time: %.3f ms, Memory: %.2f MB (%d iterations)",
                    getExecutionTimeMs(), getMemoryUsedMB(), iterations);
        }
    }

    /**
     * TestResult - Complete result data for a single test execution
     * 
     * This comprehensive data structure captures everything about a test execution:
     * the test metadata, execution results, performance metrics, and any errors.
     * It serves as the primary data transfer object between execution and reporting.
     */
    public static class TestResult {
        /** Name of the executed test */
        public final String testName;

        /** Whether the test passed (true) or failed (false) */
        public final boolean passed;

        /** The input that was provided to the test */
        public final String input;

        /** The expected output for comparison */
        public final String expected;

        /** The actual output produced by the test */
        public final String actual;

        /** Error message if test execution failed (null if no error) */
        public final String error;

        /** Description of what the test verifies */
        public final String description;

        /** Performance measurements (null if performance tracking disabled) */
        public final PerformanceResult performance;

        /**
         * Constructs a complete TestResult with all execution data
         * 
         * @param testName Name identifier of the test
         * @param passed Whether the test passed validation
         * @param input Input data used for the test
         * @param expected Expected output for comparison
         * @param actual Actual output produced by execution
         * @param error Error message if execution failed (null otherwise)
         * @param description Test description for documentation
         * @param performance Performance metrics (null if not measured)
         */
        public TestResult(String testName, boolean passed, String input, String expected,
                String actual, String error, String description, PerformanceResult performance) {
            this.testName = testName;
            this.passed = passed;
            this.input = input;
            this.expected = expected;
            this.actual = actual;
            this.error = error;
            this.description = description;
            this.performance = performance;
        }

        /**
         * Indicates whether this test had an execution error
         * 
         * @return true if there was an error during execution
         */
        public boolean hasError() {
            return error != null && !error.isEmpty();
        }

        /**
         * Indicates whether performance data is available
         * 
         * @return true if performance metrics were collected
         */
        public boolean hasPerformanceData() {
            return performance != null;
        }

        /**
         * String representation for debugging purposes
         */
        @Override
        public String toString() {
            return String.format("TestResult{name='%s', passed=%s, error='%s'}", testName, passed,
                    error);
        }
    }
}
