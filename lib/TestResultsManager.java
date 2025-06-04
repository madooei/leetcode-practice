import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * TestResultsManager - Comprehensive test results collection and reporting
 * 
 * This class manages the complete lifecycle of test results including: - Collection and aggregation
 * of individual test results - Statistical analysis and summary generation - Performance reporting
 * with complexity analysis - JSON export functionality for result persistence - Console output
 * formatting and presentation
 * 
 * The manager provides both summary statistics and detailed per-test reporting, with optional
 * performance profiling and complexity analysis capabilities.
 */
public class TestResultsManager {

    /** Collection of all test results */
    private final List<TestDataModels.TestResult> results;

    /** Count of passed tests */
    private int passedCount;

    /** Total count of executed tests */
    private int totalCount;

    /** Flag to enable performance reporting */
    private boolean performanceReportingEnabled;

    /** Path to the original test file (for generating results file path) */
    private String testFilePath;

    /**
     * Constructs a new TestResultsManager
     * 
     * Initializes an empty results collection with all counters reset.
     */
    public TestResultsManager() {
        this.results = new ArrayList<>();
        this.passedCount = 0;
        this.totalCount = 0;
        this.performanceReportingEnabled = false;
        this.testFilePath = "";
    }

    /**
     * Adds a test result to the collection
     * 
     * This method records a single test execution result and updates the internal statistics
     * counters automatically.
     * 
     * @param testName Identifier for the test case
     * @param passed Whether the test passed validation
     * @param input Input data used for the test
     * @param expected Expected output for comparison
     * @param actual Actual output produced by execution
     * @param error Error message if execution failed (null if no error)
     * @param description Test description for documentation
     * @param performance Performance metrics (null if not measured)
     */
    public void addResult(String testName, boolean passed, String input, String expected,
            String actual, String error, String description,
            TestDataModels.PerformanceResult performance) {

        TestDataModels.TestResult result = new TestDataModels.TestResult(testName, passed, input,
                expected, actual, error, description, performance);

        results.add(result);
        totalCount++;
        if (passed) {
            passedCount++;
        }
    }

    /**
     * Enables performance reporting for summary output
     * 
     * When enabled, the manager will include performance metrics and complexity analysis in the
     * summary reports.
     */
    public void enablePerformanceReporting() {
        this.performanceReportingEnabled = true;
    }

    /**
     * Sets the test file path for result file generation
     * 
     * This path is used to automatically generate the output file name for saving results in JSON
     * format.
     * 
     * @param path Path to the original test file
     */
    public void setTestFilePath(String path) {
        this.testFilePath = path;
    }

    /**
     * Saves test results to a JSON file
     * 
     * Generates a comprehensive JSON report containing all test results with performance data and
     * saves it alongside the original test file. The output file name is automatically generated
     * based on the test file path.
     */
    public void saveToFile() {
        if (testFilePath.isEmpty()) {
            System.err.println("Cannot save results: no test file path specified");
            return;
        }

        try {
            String resultsPath = generateResultsFilePath(testFilePath);
            String jsonContent = generateResultsJson();
            Files.writeString(Paths.get(resultsPath), jsonContent);
            System.out.println("📄 Results saved to: " + resultsPath);
        } catch (IOException e) {
            System.err.println("Error saving results to file: " + e.getMessage());
        }
    }

    /**
     * Prints a comprehensive summary of all test results
     * 
     * Displays overall statistics, performance analysis (if enabled), and details about failed
     * tests for debugging purposes.
     */
    public void printSummary() {
        printHeader();
        printOverallStats();

        if (performanceReportingEnabled && !results.isEmpty()) {
            printPerformanceSummary();
        }

        printConclusion();
    }

    /**
     * Generates the results file path based on the test file path
     * 
     * Converts "test.json" to "test-results.json" format.
     * 
     * @param testFilePath Original test file path
     * @return Generated results file path
     */
    private String generateResultsFilePath(String testFilePath) {
        if (testFilePath.endsWith(".json")) {
            return testFilePath.substring(0, testFilePath.length() - 5) + "-results.json";
        } else {
            return testFilePath + "-results.json";
        }
    }

    /**
     * Generates JSON representation of all test results
     * 
     * Creates a properly formatted JSON array containing all test results with escaped strings and
     * performance data.
     * 
     * @return JSON string representation of results
     */
    private String generateResultsJson() {
        StringBuilder json = new StringBuilder();
        json.append("[\n");

        for (int i = 0; i < results.size(); i++) {
            TestDataModels.TestResult result = results.get(i);
            json.append("  {\n");
            json.append("    \"name\": \"").append(escapeJson(result.testName)).append("\",\n");
            json.append("    \"description\": \"").append(escapeJson(result.description))
                    .append("\",\n");
            json.append("    \"input\": \"").append(escapeJson(result.input)).append("\",\n");
            json.append("    \"expected\": \"").append(escapeJson(result.expected)).append("\",\n");
            json.append("    \"got\": \"").append(escapeJson(result.actual)).append("\",\n");
            json.append("    \"status\": \"").append(result.passed ? "✅ PASS" : "❌ FAIL")
                    .append("\"");

            // Add performance data if available
            if (result.hasPerformanceData()) {
                json.append(",\n    \"performance\": \"")
                        .append(escapeJson(result.performance.toString())).append("\"");
            }

            // Add error information if present
            if (result.hasError()) {
                json.append(",\n    \"error\": \"").append(escapeJson(result.error)).append("\"");
            }

            json.append("\n  }");

            if (i < results.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }

        json.append("]");
        return json.toString();
    }

    /**
     * Escapes special characters for JSON output
     * 
     * Handles quotes, newlines, carriage returns, and tabs to ensure valid JSON formatting.
     * 
     * @param str String to escape
     * @return Escaped string safe for JSON
     */
    private String escapeJson(String str) {
        if (str == null)
            return "";
        return str.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t",
                "\\t");
    }

    /**
     * Prints the summary header with separator line
     */
    private void printHeader() {
        System.out.println("=" + "=".repeat(60));
    }

    /**
     * Prints overall test statistics
     */
    private void printOverallStats() {
        double successRate = totalCount > 0 ? (passedCount * 100.0 / totalCount) : 0;
        System.out.println(String.format("Results: %d/%d tests passed (%.1f%%)", passedCount,
                totalCount, successRate));
    }

    /**
     * Prints detailed performance analysis summary
     * 
     * Includes statistical analysis of execution times, memory usage, and algorithmic complexity
     * detection if sufficient data is available.
     */
    private void printPerformanceSummary() {
        System.out.println("\n📊 Performance Summary:");

        // Calculate performance statistics
        PerformanceStats stats = calculatePerformanceStats();

        if (stats.validResults > 0) {
            printPerformanceMetrics(stats);

            // Perform complexity analysis if enough data points
            if (results.size() >= 3) {
                printComplexityAnalysis();
            }
        } else {
            System.out.println("  No performance data available for analysis");
        }
    }

    /**
     * Calculates aggregate performance statistics
     * 
     * @return PerformanceStats object containing calculated metrics
     */
    private PerformanceStats calculatePerformanceStats() {
        double totalTime = 0;
        double maxTime = 0;
        double minTime = Double.MAX_VALUE;
        long totalMemory = 0;
        long maxMemory = 0;
        int validResults = 0;

        for (TestDataModels.TestResult result : results) {
            if (result.passed && result.hasPerformanceData()) {
                double timeMs = result.performance.getExecutionTimeMs();
                long memoryBytes = result.performance.memoryUsedBytes;

                totalTime += timeMs;
                maxTime = Math.max(maxTime, timeMs);
                minTime = Math.min(minTime, timeMs);
                totalMemory += memoryBytes;
                maxMemory = Math.max(maxMemory, memoryBytes);
                validResults++;
            }
        }

        return new PerformanceStats(totalTime, minTime, maxTime, totalMemory, maxMemory,
                validResults);
    }

    /**
     * Prints formatted performance metrics
     * 
     * @param stats Calculated performance statistics
     */
    private void printPerformanceMetrics(PerformanceStats stats) {
        double avgTime = stats.totalTime / stats.validResults;
        double avgMemoryMB = (stats.totalMemory / stats.validResults) / (1024.0 * 1024.0);
        double maxMemoryMB = stats.maxMemory / (1024.0 * 1024.0);

        System.out.println(String.format("  Average Time: %.3f ms", avgTime));
        System.out.println(
                String.format("  Time Range: %.3f ms - %.3f ms", stats.minTime, stats.maxTime));
        System.out.println(String.format("  Average Memory: %.2f MB", avgMemoryMB));
        System.out.println(String.format("  Peak Memory: %.2f MB", maxMemoryMB));
    }

    /**
     * Prints algorithmic complexity analysis
     */
    private void printComplexityAnalysis() {
        PerformanceAnalyzer.ComplexityAnalysis analysis =
                PerformanceAnalyzer.analyzeComplexity(results);
        System.out.println("  🔍 Complexity Analysis: " + analysis.description);

        if (analysis.confidence > 0.7) {
            System.out.println(
                    "    Confidence: " + String.format("%.1f%%", analysis.confidence * 100));
            if (!analysis.recommendation.isEmpty()) {
                System.out.println("    💡 " + analysis.recommendation);
            }
        } else {
            System.out.println(
                    "    ⚠️  Low confidence - results may be affected by JVM warmup or small input sizes");
        }
    }

    /**
     * Prints conclusion based on overall test results
     */
    private void printConclusion() {
        if (passedCount == totalCount) {
            System.out.println("🎉 All tests passed!");
        } else {
            System.out.println("❌ Some tests failed. Check your implementation.");
            printFailedTestDetails();
        }
    }

    /**
     * Prints details about failed tests for debugging
     */
    private void printFailedTestDetails() {
        System.out.println("\nFailed tests:");
        for (TestDataModels.TestResult result : results) {
            if (!result.passed) {
                String errorInfo = result.hasError() ? result.error : "Wrong output";
                System.out.println("  - " + result.testName + ": " + errorInfo);
            }
        }
    }

    /**
     * Internal class for performance statistics aggregation
     */
    private static class PerformanceStats {
        final double totalTime;
        final double minTime;
        final double maxTime;
        final long totalMemory;
        final long maxMemory;
        final int validResults;

        PerformanceStats(double totalTime, double minTime, double maxTime, long totalMemory,
                long maxMemory, int validResults) {
            this.totalTime = totalTime;
            this.minTime = minTime;
            this.maxTime = maxTime;
            this.totalMemory = totalMemory;
            this.maxMemory = maxMemory;
            this.validResults = validResults;
        }
    }

    // Getter methods for accessing results data

    /**
     * Gets the list of all test results
     * 
     * @return Unmodifiable list of test results
     */
    public List<TestDataModels.TestResult> getResults() {
        return Collections.unmodifiableList(results);
    }

    /**
     * Gets the number of passed tests
     * 
     * @return Count of passed tests
     */
    public int getPassedCount() {
        return passedCount;
    }

    /**
     * Gets the total number of executed tests
     * 
     * @return Total test count
     */
    public int getTotalCount() {
        return totalCount;
    }

    /**
     * Checks if performance reporting is enabled
     * 
     * @return true if performance reporting is enabled
     */
    public boolean isPerformanceReportingEnabled() {
        return performanceReportingEnabled;
    }
}
