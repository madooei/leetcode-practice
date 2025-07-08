package shared;

import java.lang.reflect.*;
import java.util.*;

/**
 * PerformanceAnalyzer - Advanced performance measurement and complexity analysis
 * 
 * This class provides sophisticated performance analysis capabilities including:
 * - Accurate timing measurements with JVM warmup
 * - Memory usage tracking
 * - Algorithm complexity detection and analysis
 * - Statistical analysis of performance patterns
 * 
 * The analyzer can automatically detect common algorithmic complexities
 * (O(1), O(log n), O(n), O(n log n), O(n²)) and provide optimization recommendations.
 */
public class PerformanceAnalyzer {

    /**
     * Measures the performance of a method execution with proper JVM warmup
     * 
     * This method performs accurate performance measurement by:
     * 1. Running warmup iterations to allow JIT compilation
     * 2. Forcing garbage collection before measurement
     * 3. Measuring multiple iterations for statistical accuracy
     * 4. Calculating average execution time and memory usage
     * 
     * @param instance The object instance to invoke the method on
     * @param method The method to measure
     * @param args Arguments to pass to the method
     * @param warmupRuns Number of warmup iterations for JIT optimization
     * @param measureRuns Number of measured iterations for averaging
     * @return PerformanceResult containing timing and memory metrics
     */
    public static TestDataModels.PerformanceResult measurePerformance(Object instance,
            Method method, Object[] args, int warmupRuns, int measureRuns) {
        Runtime runtime = Runtime.getRuntime();

        try {
            // Phase 1: Warmup runs to enable JIT compilation
            for (int i = 0; i < warmupRuns; i++) {
                method.invoke(instance, args);
            }

            // Phase 2: Prepare for accurate measurement
            System.gc(); // Request garbage collection
            Thread.sleep(10); // Allow GC to complete

            // Phase 3: Baseline memory measurement
            long startMemory = runtime.totalMemory() - runtime.freeMemory();
            long startTime = System.nanoTime();

            // Phase 4: Execute measured runs
            for (int i = 0; i < measureRuns; i++) {
                method.invoke(instance, args);
            }

            // Phase 5: Capture final measurements
            long endTime = System.nanoTime();
            long endMemory = runtime.totalMemory() - runtime.freeMemory();

            // Calculate averages and ensure non-negative memory usage
            long executionTime = (endTime - startTime) / measureRuns;
            long memoryUsed = Math.max(0, endMemory - startMemory);

            return new TestDataModels.PerformanceResult(executionTime, memoryUsed, measureRuns);

        } catch (Exception e) {
            // Return zero metrics if measurement fails
            return new TestDataModels.PerformanceResult(0, 0, 0);
        }
    }

    /**
     * ComplexityAnalysis - Result of algorithmic complexity analysis
     * 
     * Contains the detected complexity pattern, confidence level, and optimization recommendations.
     */
    public static class ComplexityAnalysis {
        /** Human-readable description of the detected complexity */
        public final String description;

        /** Confidence level (0.0 to 1.0) in the analysis accuracy */
        public final double confidence;

        /** Optimization recommendation based on the detected pattern */
        public final String recommendation;

        /**
         * Constructs a ComplexityAnalysis result
         * 
         * @param description The complexity description (e.g., "O(n) - Linear time")
         * @param confidence Confidence level from 0.0 to 1.0
         * @param recommendation Optimization advice for the developer
         */
        public ComplexityAnalysis(String description, double confidence, String recommendation) {
            this.description = description;
            this.confidence = confidence;
            this.recommendation = recommendation;
        }
    }

    /**
     * DataPoint - Internal structure for complexity analysis
     * 
     * Represents a single measurement point with input size and execution time.
     */
    private static class DataPoint {
        /** Estimated size of the input data */
        final int inputSize;

        /** Measured execution time in milliseconds */
        final double timeMs;

        /**
         * Constructs a DataPoint for analysis
         * 
         * @param inputSize The estimated input size
         * @param timeMs The execution time in milliseconds
         */
        DataPoint(int inputSize, double timeMs) {
            this.inputSize = inputSize;
            this.timeMs = timeMs;
        }
    }

    /**
     * Analyzes algorithmic complexity based on performance results
     * 
     * This method examines the relationship between input sizes and execution times
     * to provide insights about performance characteristics. It focuses on providing
     * useful information about performance patterns rather than trying to guess
     * exact complexity classes.
     * 
     * @param results List of test results with performance data
     * @return ComplexityAnalysis containing performance insights
     */
    public static ComplexityAnalysis analyzeComplexity(List<TestDataModels.TestResult> results) {
        // Filter to only passed results with performance data
        List<TestDataModels.TestResult> validResults = new ArrayList<>();
        for (TestDataModels.TestResult result : results) {
            if (result.passed && result.hasPerformanceData()) {
                validResults.add(result);
            }
        }

        if (validResults.size() < 3) {
            return new ComplexityAnalysis("Insufficient data", 0.0,
                    "Need at least 3 successful test cases for performance analysis");
        }

        // Convert results to data points for analysis
        List<DataPoint> dataPoints = convertToDataPoints(validResults);

        if (dataPoints.size() < 3) {
            return new ComplexityAnalysis("Unable to estimate input sizes", 0.0,
                    "Cannot determine input sizes for performance analysis");
        }

        // Sort data points by input size for analysis
        dataPoints.sort((a, b) -> Integer.compare(a.inputSize, b.inputSize));

        // Analyze performance characteristics
        return analyzePerformanceCharacteristics(dataPoints);
    }

    /**
     * Converts test results to data points for complexity analysis
     * 
     * @param results List of test results with performance data
     * @return List of data points with estimated input sizes and timing data
     */
    private static List<DataPoint> convertToDataPoints(List<TestDataModels.TestResult> results) {
        List<DataPoint> dataPoints = new ArrayList<>();

        for (TestDataModels.TestResult result : results) {
            int inputSize = estimateInputSize(result.input);
            double timeMs = result.performance.getExecutionTimeMs();

            if (inputSize > 0 && timeMs > 0) {
                dataPoints.add(new DataPoint(inputSize, timeMs));
            }
        }

        return dataPoints;
    }

    /**
     * Estimates the size of input data based on string representation
     * 
     * This heuristic method attempts to determine the algorithmic input size
     * from various input formats including arrays, strings, and numbers.
     * 
     * @param input The string representation of the input
     * @return Estimated input size (0 if cannot be determined)
     */
    private static int estimateInputSize(String input) {
        // Handle empty array case
        if (input.equals("[]")) {
            return 0;
        }

        // Handle array inputs like "[1,2,3,4,5]"
        if (input.startsWith("[") && input.endsWith("]")) {
            String content = input.substring(1, input.length() - 1).trim();
            if (content.isEmpty()) {
                return 0;
            }

            // Count non-null elements in the array
            String[] elements = content.split(",");
            int count = 0;
            for (String element : elements) {
                if (!element.trim().equals("null")) {
                    count++;
                }
            }
            return count;
        }

        // Handle string inputs with quotes
        if (input.startsWith("\"") && input.endsWith("\"")) {
            return input.length() - 2; // Subtract quotes
        }

        // Handle numeric inputs
        try {
            Integer.parseInt(input.trim());
            return 1; // Single number has size 1
        } catch (NumberFormatException e) {
            // Fall back to string length as rough estimate
            return input.length();
        }
    }

    /**
     * Analyzes performance characteristics from data points
     * 
     * This method provides insights about performance patterns without making
     * strong claims about exact complexity classes. It focuses on observable
     * characteristics and provides practical recommendations.
     * 
     * @param dataPoints List of input size and timing measurements
     * @return ComplexityAnalysis with performance insights
     */
    private static ComplexityAnalysis analyzePerformanceCharacteristics(
            List<DataPoint> dataPoints) {
        // Calculate basic statistics
        double[] times = dataPoints.stream().mapToDouble(dp -> dp.timeMs).toArray();
        double meanTime = calculateMean(times);
        double stdDevTime = calculateStandardDeviation(times);
        double timeVariation = meanTime > 0 ? stdDevTime / meanTime : 0;

        // Calculate time ratios between consecutive sizes
        List<Double> timeRatios = new ArrayList<>();
        for (int i = 1; i < dataPoints.size(); i++) {
            DataPoint prev = dataPoints.get(i - 1);
            DataPoint curr = dataPoints.get(i);
            if (prev.timeMs > 0) {
                timeRatios.add(curr.timeMs / prev.timeMs);
            }
        }

        // Calculate average time ratio
        double avgTimeRatio = timeRatios.isEmpty() ? 1.0
                : calculateMean(timeRatios.stream().mapToDouble(Double::doubleValue).toArray());

        // Generate insights based on observed patterns
        String description;
        String recommendation;
        double confidence;

        if (timeVariation < 0.1) {
            // Very consistent execution times
            description = "Consistent execution time across inputs";
            recommendation = "Performance appears stable across different input sizes";
            confidence = 0.9;
        } else if (avgTimeRatio < 1.2) {
            // Small increase in time with input size
            description = "Performance scales well with input size";
            recommendation = "Current implementation handles input growth efficiently";
            confidence = 0.7;
        } else if (avgTimeRatio < 2.0) {
            // Moderate increase in time with input size
            description = "Performance shows moderate scaling with input size";
            recommendation = "Consider optimizing for larger inputs if needed";
            confidence = 0.6;
        } else {
            // Significant increase in time with input size
            description = "Performance degrades noticeably with larger inputs";
            recommendation = "Consider optimizing the algorithm for better scaling";
            confidence = 0.8;
        }

        // Add note about input size limitations
        int maxInputSize = dataPoints.stream().mapToInt(dp -> dp.inputSize).max().orElse(0);
        if (maxInputSize < 100) {
            description += " (based on small input sizes)";
            recommendation += " - Test with larger inputs for more accurate analysis";
            confidence *= 0.8; // Reduce confidence for small input sizes
        }

        return new ComplexityAnalysis(description, confidence, recommendation);
    }

    /**
     * Calculates the arithmetic mean of an array of values
     */
    private static double calculateMean(double[] values) {
        if (values.length == 0)
            return 0.0;

        double sum = 0;
        for (double value : values) {
            sum += value;
        }
        return sum / values.length;
    }

    /**
     * Calculates the standard deviation of an array of values
     */
    private static double calculateStandardDeviation(double[] values) {
        if (values.length == 0)
            return 0.0;

        double mean = calculateMean(values);
        double sumSquaredDiffs = 0;

        for (double value : values) {
            double diff = value - mean;
            sumSquaredDiffs += diff * diff;
        }

        return Math.sqrt(sumSquaredDiffs / values.length);
    }
}
