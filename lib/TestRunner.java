import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.lang.reflect.*;

/**
 * Generic test runner for LeetCode problems Loads test cases from JSON files and executes them
 * against solutions
 */
public class TestRunner {

    /**
     * Simple JSON parser for test cases (no external dependencies)
     */
    public static class SimpleJsonParser {

        public static List<Map<String, String>> parseTestCases(String jsonContent) {
            List<Map<String, String>> testCases = new ArrayList<>();

            // Remove whitespace and outer brackets
            jsonContent = jsonContent.trim();
            if (!jsonContent.startsWith("[") || !jsonContent.endsWith("]")) {
                throw new RuntimeException("Invalid JSON format - must be an array");
            }

            String content = jsonContent.substring(1, jsonContent.length() - 1).trim();
            if (content.isEmpty()) {
                return testCases;
            }

            // Split by objects (look for },{)
            List<String> objects = splitJsonObjects(content);

            for (String obj : objects) {
                Map<String, String> testCase = parseJsonObject(obj);
                testCases.add(testCase);
            }

            return testCases;
        }

        private static List<String> splitJsonObjects(String content) {
            List<String> objects = new ArrayList<>();
            int depth = 0;
            int start = 0;

            for (int i = 0; i < content.length(); i++) {
                char c = content.charAt(i);
                if (c == '{') {
                    depth++;
                } else if (c == '}') {
                    depth--;
                    if (depth == 0) {
                        objects.add(content.substring(start, i + 1).trim());
                        // Skip comma and whitespace
                        while (i + 1 < content.length() && (content.charAt(i + 1) == ','
                                || Character.isWhitespace(content.charAt(i + 1)))) {
                            i++;
                        }
                        start = i + 1;
                    }
                }
            }

            return objects;
        }

        private static Map<String, String> parseJsonObject(String obj) {
            Map<String, String> result = new HashMap<>();

            // Remove outer braces
            obj = obj.trim();
            if (obj.startsWith("{"))
                obj = obj.substring(1);
            if (obj.endsWith("}"))
                obj = obj.substring(0, obj.length() - 1);

            // Simple key-value parsing
            List<String> pairs = splitJsonPairs(obj);

            for (String pair : pairs) {
                String[] keyValue = pair.split(":", 2);
                if (keyValue.length == 2) {
                    String key = unquote(keyValue[0].trim());
                    String value = unquote(keyValue[1].trim());
                    result.put(key, value);
                }
            }

            return result;
        }

        private static List<String> splitJsonPairs(String content) {
            List<String> pairs = new ArrayList<>();
            boolean inQuotes = false;
            int start = 0;

            for (int i = 0; i < content.length(); i++) {
                char c = content.charAt(i);
                if (c == '"' && (i == 0 || content.charAt(i - 1) != '\\')) {
                    inQuotes = !inQuotes;
                } else if (c == ',' && !inQuotes) {
                    pairs.add(content.substring(start, i).trim());
                    start = i + 1;
                }
            }

            if (start < content.length()) {
                pairs.add(content.substring(start).trim());
            }

            return pairs;
        }

        private static String unquote(String str) {
            str = str.trim();
            if (str.startsWith("\"") && str.endsWith("\"")) {
                return str.substring(1, str.length() - 1);
            }
            return str;
        }
    }

    /**
     * Test case data structure
     */
    public static class TestCase {
        public String name;
        public String input;
        public String expected;
        public String description;

        public TestCase(Map<String, String> data) {
            this.name = data.getOrDefault("name", "Unknown");
            this.input = data.getOrDefault("input", "");
            this.expected = data.getOrDefault("expected", "");
            this.description = data.getOrDefault("description", "");
        }

        @Override
        public String toString() {
            return String.format("TestCase{name='%s', input='%s', expected='%s'}", name, input,
                    expected);
        }
    }

    /**
     * Performance profiling results
     */
    public static class PerformanceResult {
        public long executionTimeNanos;
        public long memoryUsedBytes;
        public int iterations;

        public PerformanceResult(long executionTimeNanos, long memoryUsedBytes, int iterations) {
            this.executionTimeNanos = executionTimeNanos;
            this.memoryUsedBytes = memoryUsedBytes;
            this.iterations = iterations;
        }

        public double getExecutionTimeMs() {
            return executionTimeNanos / 1_000_000.0;
        }

        public double getMemoryUsedMB() {
            return memoryUsedBytes / (1024.0 * 1024.0);
        }

        @Override
        public String toString() {
            return String.format("Time: %.3f ms, Memory: %.2f MB (%d iterations)",
                    getExecutionTimeMs(), getMemoryUsedMB(), iterations);
        }
    }

    /**
     * Enhanced test result with performance data
     */
    public static class TestResult {
        public String testName;
        public boolean passed;
        public String input;
        public String expected;
        public String actual;
        public String error;
        public String description;
        public PerformanceResult performance;

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
    }

    /**
     * Test results tracking with performance stats
     */
    public static class TestResults {
        public List<TestResult> results = new ArrayList<>();
        public int passed = 0;
        public int total = 0;
        public boolean showPerformance = false;
        public String testFilePath = "";

        public void addResult(String testName, boolean passed, String input, String expected,
                String actual, String error, String description, PerformanceResult performance) {
            results.add(new TestResult(testName, passed, input, expected, actual, error,
                    description, performance));
            this.total++;
            if (passed)
                this.passed++;
        }

        public void setTestFilePath(String path) {
            this.testFilePath = path;
        }

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

        private String generateResultsFilePath(String testFilePath) {
            // Convert "hello-world/tests.json" to "hello-world/tests-results.json"
            if (testFilePath.endsWith(".json")) {
                return testFilePath.substring(0, testFilePath.length() - 5) + "-results.json";
            } else {
                return testFilePath + "-results.json";
            }
        }

        private String generateResultsJson() {
            StringBuilder json = new StringBuilder();
            json.append("[\n");

            for (int i = 0; i < results.size(); i++) {
                TestResult result = results.get(i);
                json.append("  {\n");
                json.append("    \"name\": \"").append(escapeJson(result.testName)).append("\",\n");
                json.append("    \"description\": \"").append(escapeJson(result.description))
                        .append("\",\n");
                json.append("    \"input\": \"").append(escapeJson(result.input)).append("\",\n");
                json.append("    \"expected\": \"").append(escapeJson(result.expected))
                        .append("\",\n");
                json.append("    \"got\": \"").append(escapeJson(result.actual)).append("\",\n");
                json.append("    \"status\": \"").append(result.passed ? "✅ PASS" : "❌ FAIL")
                        .append("\"");

                if (result.performance != null) {
                    json.append(",\n    \"performance\": \"")
                            .append(escapeJson(result.performance.toString())).append("\"");
                }

                if (result.error != null) {
                    json.append(",\n    \"error\": \"").append(escapeJson(result.error))
                            .append("\"");
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

        private String escapeJson(String str) {
            if (str == null)
                return "";
            return str.replace("\"", "\\\"") // Only escape quotes
                    .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
        }

        public void enablePerformanceReporting() {
            this.showPerformance = true;
        }

        public void printSummary() {
            System.out.println("=" + "=".repeat(60));
            System.out.println(String.format("Results: %d/%d tests passed (%.1f%%)", passed, total,
                    total > 0 ? (passed * 100.0 / total) : 0));

            if (showPerformance && !results.isEmpty()) {
                printPerformanceSummary();
            }

            if (passed == total) {
                System.out.println("🎉 All tests passed!");
            } else {
                System.out.println("❌ Some tests failed. Check your implementation.");
                System.out.println("\nFailed tests:");
                for (TestResult result : results) {
                    if (!result.passed) {
                        System.out.println("  - " + result.testName + ": "
                                + (result.error != null ? result.error : "Wrong output"));
                    }
                }
            }
        }

        private void printPerformanceSummary() {
            System.out.println("\n📊 Performance Summary:");

            // Calculate statistics
            double totalTime = 0;
            double maxTime = 0;
            double minTime = Double.MAX_VALUE;
            long totalMemory = 0;
            long maxMemory = 0;
            int validResults = 0;

            for (TestResult result : results) {
                if (result.passed && result.performance != null) {
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

            if (validResults > 0) {
                double avgTime = totalTime / validResults;
                double avgMemoryMB = (totalMemory / validResults) / (1024.0 * 1024.0);
                double maxMemoryMB = maxMemory / (1024.0 * 1024.0);

                System.out.println(String.format("  Average Time: %.3f ms", avgTime));
                System.out.println(
                        String.format("  Time Range: %.3f ms - %.3f ms", minTime, maxTime));
                System.out.println(String.format("  Average Memory: %.2f MB", avgMemoryMB));
                System.out.println(String.format("  Peak Memory: %.2f MB", maxMemoryMB));

                // Simple complexity hint based on performance scaling
                if (results.size() >= 3) {
                    analyzeComplexity();
                }
            }
        }

        private void analyzeComplexity() {
            // Enhanced complexity analysis based on execution time scaling
            List<TestResult> passedResults =
                    results.stream().filter(r -> r.passed && r.performance != null)
                            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

            if (passedResults.size() < 3) {
                System.out.println("  💡 Need more test cases for complexity analysis");
                return;
            }

            // Estimate input sizes and sort by them
            List<DataPoint> dataPoints = new ArrayList<>();
            for (TestResult result : passedResults) {
                int inputSize = estimateInputSize(result.input);
                double timeMs = result.performance.getExecutionTimeMs();
                if (inputSize > 0 && timeMs > 0) {
                    dataPoints.add(new DataPoint(inputSize, timeMs));
                }
            }

            if (dataPoints.size() < 3) {
                System.out.println("  💡 Unable to estimate input sizes for complexity analysis");
                return;
            }

            dataPoints.sort((a, b) -> Integer.compare(a.inputSize, b.inputSize));

            // Analyze different complexity patterns
            ComplexityAnalysis analysis = analyzeComplexityPattern(dataPoints);
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

        private static class DataPoint {
            int inputSize;
            double timeMs;

            DataPoint(int inputSize, double timeMs) {
                this.inputSize = inputSize;
                this.timeMs = timeMs;
            }
        }

        private static class ComplexityAnalysis {
            String description;
            double confidence;
            String recommendation;

            ComplexityAnalysis(String description, double confidence, String recommendation) {
                this.description = description;
                this.confidence = confidence;
                this.recommendation = recommendation;
            }
        }

        private int estimateInputSize(String input) {
            // Estimate input size based on the input format
            if (input.equals("[]")) {
                return 0;
            }

            // For tree inputs like "[1,2,3,4,5,6,7]"
            if (input.startsWith("[") && input.endsWith("]")) {
                String content = input.substring(1, input.length() - 1).trim();
                if (content.isEmpty()) {
                    return 0;
                }

                // Count non-null elements
                String[] elements = content.split(",");
                int count = 0;
                for (String element : elements) {
                    if (!element.trim().equals("null")) {
                        count++;
                    }
                }
                return count;
            }

            // For string inputs, use length
            if (input.startsWith("\"") && input.endsWith("\"")) {
                return input.length() - 2;
            }

            // For numeric inputs, assume size 1
            try {
                Integer.parseInt(input.trim());
                return 1;
            } catch (NumberFormatException e) {
                // Fall back to string length as rough estimate
                return input.length();
            }
        }

        private ComplexityAnalysis analyzeComplexityPattern(List<DataPoint> dataPoints) {
            if (dataPoints.size() < 3) {
                return new ComplexityAnalysis("Insufficient data", 0.0, "");
            }

            int n = dataPoints.size();

            // Calculate growth ratios
            List<Double> growthRatios = new ArrayList<>();
            for (int i = 1; i < n; i++) {
                DataPoint prev = dataPoints.get(i - 1);
                DataPoint curr = dataPoints.get(i);

                if (prev.timeMs > 0 && curr.inputSize > prev.inputSize) {
                    double sizeRatio = (double) curr.inputSize / prev.inputSize;
                    double timeRatio = curr.timeMs / prev.timeMs;

                    if (sizeRatio > 1.1) { // Only consider meaningful size increases
                        growthRatios.add(timeRatio / sizeRatio);
                    }
                }
            }

            if (growthRatios.isEmpty()) {
                return new ComplexityAnalysis("Unable to determine pattern", 0.0,
                        "Input sizes too similar or execution times too variable");
            }

            // Analyze different complexity hypotheses
            double constantScore = analyzeConstantComplexity(dataPoints);
            double linearScore = analyzeLinearComplexity(dataPoints);
            double quadraticScore = analyzeQuadraticComplexity(dataPoints);
            double logarithmicScore = analyzeLogarithmicComplexity(dataPoints);
            double nlogNScore = analyzeNLogNComplexity(dataPoints);

            // Find the best fit
            double maxScore = Math.max(constantScore, Math.max(linearScore,
                    Math.max(quadraticScore, Math.max(logarithmicScore, nlogNScore))));

            if (maxScore < 0.3) {
                return new ComplexityAnalysis("Complex or irregular pattern", maxScore,
                        "Performance doesn't follow standard complexity patterns");
            }

            String complexity;
            String recommendation = "";

            if (maxScore == constantScore) {
                complexity = "O(1) - Constant time";
                recommendation = "Excellent! Performance doesn't depend on input size";
            } else if (maxScore == logarithmicScore) {
                complexity = "O(log n) - Logarithmic time";
                recommendation = "Very good! Performance scales logarithmically";
            } else if (maxScore == linearScore) {
                complexity = "O(n) - Linear time";
                recommendation = "Good! Performance scales linearly with input size";
            } else if (maxScore == nlogNScore) {
                complexity = "O(n log n) - Linearithmic time";
                recommendation = "Acceptable for divide-and-conquer algorithms";
            } else {
                complexity = "O(n²) or higher - Quadratic/Polynomial time";
                recommendation = "Consider optimizing for better performance with large inputs";
            }

            return new ComplexityAnalysis(complexity, maxScore, recommendation);
        }

        private double analyzeConstantComplexity(List<DataPoint> dataPoints) {
            // Check if execution time is roughly constant regardless of input size
            double[] times = dataPoints.stream().mapToDouble(dp -> dp.timeMs).toArray();
            return 1.0 - (standardDeviation(times) / mean(times));
        }

        private double analyzeLinearComplexity(List<DataPoint> dataPoints) {
            // Check if time/size ratio is roughly constant
            List<Double> ratios = new ArrayList<>();
            for (DataPoint dp : dataPoints) {
                if (dp.inputSize > 0) {
                    ratios.add(dp.timeMs / dp.inputSize);
                }
            }

            if (ratios.isEmpty())
                return 0.0;

            double[] ratioArray = ratios.stream().mapToDouble(Double::doubleValue).toArray();
            double cv = standardDeviation(ratioArray) / mean(ratioArray);
            return Math.max(0, 1.0 - cv);
        }

        private double analyzeQuadraticComplexity(List<DataPoint> dataPoints) {
            // Check if time/(size²) ratio is roughly constant
            List<Double> ratios = new ArrayList<>();
            for (DataPoint dp : dataPoints) {
                if (dp.inputSize > 1) {
                    ratios.add(dp.timeMs / (dp.inputSize * dp.inputSize));
                }
            }

            if (ratios.isEmpty())
                return 0.0;

            double[] ratioArray = ratios.stream().mapToDouble(Double::doubleValue).toArray();
            double cv = standardDeviation(ratioArray) / mean(ratioArray);
            return Math.max(0, 1.0 - cv);
        }

        private double analyzeLogarithmicComplexity(List<DataPoint> dataPoints) {
            // Check if time/log(size) ratio is roughly constant
            List<Double> ratios = new ArrayList<>();
            for (DataPoint dp : dataPoints) {
                if (dp.inputSize > 1) {
                    ratios.add(dp.timeMs / Math.log(dp.inputSize));
                }
            }

            if (ratios.isEmpty())
                return 0.0;

            double[] ratioArray = ratios.stream().mapToDouble(Double::doubleValue).toArray();
            double cv = standardDeviation(ratioArray) / mean(ratioArray);
            return Math.max(0, 1.0 - cv);
        }

        private double analyzeNLogNComplexity(List<DataPoint> dataPoints) {
            // Check if time/(size*log(size)) ratio is roughly constant
            List<Double> ratios = new ArrayList<>();
            for (DataPoint dp : dataPoints) {
                if (dp.inputSize > 1) {
                    ratios.add(dp.timeMs / (dp.inputSize * Math.log(dp.inputSize)));
                }
            }

            if (ratios.isEmpty())
                return 0.0;

            double[] ratioArray = ratios.stream().mapToDouble(Double::doubleValue).toArray();
            double cv = standardDeviation(ratioArray) / mean(ratioArray);
            return Math.max(0, 1.0 - cv);
        }

        private double mean(double[] values) {
            double sum = 0;
            for (double value : values) {
                sum += value;
            }
            return sum / values.length;
        }

        private double standardDeviation(double[] values) {
            double mean = mean(values);
            double sumSquaredDiffs = 0;
            for (double value : values) {
                double diff = value - mean;
                sumSquaredDiffs += diff * diff;
            }
            return Math.sqrt(sumSquaredDiffs / values.length);
        }
    }

    /**
     * Measure performance of a method execution
     */
    public static PerformanceResult measurePerformance(Object instance, Method method,
            Object[] args, int warmupRuns, int measureRuns) {
        Runtime runtime = Runtime.getRuntime();

        try {
            // Warmup runs to let JIT optimize
            for (int i = 0; i < warmupRuns; i++) {
                method.invoke(instance, args);
            }

            // Force garbage collection before measurement
            System.gc();
            Thread.sleep(10); // Give GC a moment

            long startMemory = runtime.totalMemory() - runtime.freeMemory();
            long startTime = System.nanoTime();

            // Measured runs
            for (int i = 0; i < measureRuns; i++) {
                method.invoke(instance, args);
            }

            long endTime = System.nanoTime();
            long endMemory = runtime.totalMemory() - runtime.freeMemory();

            long executionTime = (endTime - startTime) / measureRuns;
            long memoryUsed = Math.max(0, endMemory - startMemory);

            return new PerformanceResult(executionTime, memoryUsed, measureRuns);

        } catch (Exception e) {
            return new PerformanceResult(0, 0, 0);
        }
    }

    /**
     * Load test cases from JSON file
     */
    public static List<TestCase> loadTestCases(String filename) throws IOException {
        String content = Files.readString(Paths.get(filename));
        List<Map<String, String>> rawCases = SimpleJsonParser.parseTestCases(content);

        List<TestCase> testCases = new ArrayList<>();
        for (Map<String, String> rawCase : rawCases) {
            testCases.add(new TestCase(rawCase));
        }

        return testCases;
    }

    /**
     * Generic test runner that uses reflection to call solution methods
     * 
     * @param enableProfiling if true, measures execution time and memory usage
     * @param saveToFile if true, save the results to file
     */
    public static TestResults runTests(String testFile, Object solutionInstance, String methodName,
            boolean enableProfiling, boolean saveToFile) {
        TestResults results = new TestResults();
        if (enableProfiling) {
            results.enablePerformanceReporting();
        }

        try {
            List<TestCase> testCases = loadTestCases(testFile);
            System.out.println("Running " + testCases.size() + " test cases"
                    + (enableProfiling ? " with performance profiling..." : "..."));
            System.out.println("=" + "=".repeat(60));

            // Get the solution method using reflection
            Method solutionMethod = findSolutionMethod(solutionInstance.getClass(), methodName);

            for (int i = 0; i < testCases.size(); i++) {
                TestCase testCase = testCases.get(i);
                runSingleTest(testCase, solutionInstance, solutionMethod, results, i + 1,
                        enableProfiling);
                System.out.println();
            }

        } catch (Exception e) {
            System.err.println("Error running tests: " + e.getMessage());
            e.printStackTrace();
        }

        if (saveToFile) {
            results.setTestFilePath(testFile);
            results.saveToFile();
        }

        return results;
    }


    /**
     * Generic test runner that uses reflection to call solution methods
     * 
     * @param enableProfiling if true, measures execution time and memory usage
     */
    public static TestResults runTests(String testFile, Object solutionInstance, String methodName,
            boolean enableProfiling) {
        return runTests(testFile, solutionInstance, methodName, enableProfiling, false);
    }

    /**
     * Generic test runner (backward compatibility)
     */
    public static TestResults runTests(String testFile, Object solutionInstance,
            String methodName) {
        return runTests(testFile, solutionInstance, methodName, false);
    }

    /**
     * Find the solution method using reflection
     */
    private static Method findSolutionMethod(Class<?> clazz, String methodName)
            throws NoSuchMethodException {
        // Look for methods with the given name
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        throw new NoSuchMethodException(
                "Method " + methodName + " not found in " + clazz.getName());
    }

    /**
     * Run a single test case with optional profiling
     */
    private static void runSingleTest(TestCase testCase, Object solutionInstance,
            Method solutionMethod, TestResults results, int testNum, boolean enableProfiling) {
        String error = null;
        String actualStr = "";
        boolean passed = false;
        PerformanceResult performance = null;

        try {
            System.out.println("Test " + testNum + ": " + testCase.name);
            if (!testCase.description.isEmpty()) {
                System.out.println("Description: " + testCase.description);
            }

            // Parse input based on method parameter types
            Object[] args = parseMethodArguments(solutionMethod, testCase.input);

            Object result;
            if (enableProfiling) {
                // Measure performance
                long startTime = System.nanoTime();
                performance = measurePerformance(solutionInstance, solutionMethod, args, 3, 5);
                result = solutionMethod.invoke(solutionInstance, args); // Final execution for
                                                                        // correctness
                System.out.println("Performance: " + performance);
            } else {
                // Regular execution
                result = solutionMethod.invoke(solutionInstance, args);
            }

            // Convert result to string for comparison
            actualStr = resultToString(result);

            // Compare with expected
            String normalizedExpected = normalizeOutput(testCase.expected);
            String normalizedActual = normalizeOutput(actualStr);
            passed = normalizedExpected.equals(normalizedActual);

            System.out.println("Input:    " + testCase.input);
            System.out.println("Expected: " + testCase.expected);
            System.out.println("Got:      " + actualStr);
            System.out.println("Status:   " + (passed ? "✅ PASS" : "❌ FAIL"));

        } catch (Exception e) {
            error = e.getMessage();
            System.out.println("Input:    " + testCase.input);
            System.out.println("Expected: " + testCase.expected);
            System.out.println("Status:   ❌ ERROR - " + error);
        }

        results.addResult(testCase.name, passed, testCase.input, testCase.expected, actualStr,
                error, testCase.description, performance);
    }

    /**
     * Parse method arguments based on parameter types
     */
    private static Object[] parseMethodArguments(Method method, String input) {
        Class<?>[] paramTypes = method.getParameterTypes();

        if (paramTypes.length == 0) {
            return new Object[0];
        }

        if (paramTypes.length == 1) {
            Class<?> paramType = paramTypes[0];

            // Handle TreeNode parameter
            if (paramType.getSimpleName().equals("TreeNode")) {
                Integer[] array = TreeNode.parseArray(input);
                return new Object[] {TreeNode.fromArray(array)};
            }

            // Handle other common types
            if (paramType == int.class || paramType == Integer.class) {
                return new Object[] {Integer.parseInt(input)};
            }

            if (paramType == String.class) {
                // Handle null input
                if (input.equals("null")) {
                    return new Object[] {null};
                }
                // Remove surrounding quotes for string inputs
                if (input.startsWith("\"") && input.endsWith("\"")) {
                    return new Object[] {input.substring(1, input.length() - 1)};
                }
                // Return as-is if no quotes
                return new Object[] {input};
            }

            // Add more parameter type handling as needed
        }

        throw new RuntimeException("Unsupported parameter types for method: " + method.getName());
    }

    private static String normalizeOutput(String output) {
        // Remove spaces after commas: [1, 2, 3] -> [1,2,3]
        output = output.replaceAll(", ", ",");

        // Remove extra whitespace around brackets: [ 1,2,3 ] -> [1,2,3]
        output = output.replaceAll("\\[\\s+", "[");
        output = output.replaceAll("\\s+\\]", "]");

        // Remove extra whitespace around parentheses: ( 1,2,3 ) -> (1,2,3)
        output = output.replaceAll("\\(\\s+", "(");
        output = output.replaceAll("\\s+\\)", ")");

        // Normalize boolean values: True/False -> true/false
        output = output.replaceAll("\\bTrue\\b", "true");
        output = output.replaceAll("\\bFalse\\b", "false");

        return output.trim();
    }

    private static String resultToString(Object result) {
        if (result == null) {
            return "null";
        }

        String resultStr;
        if (result instanceof List) {
            resultStr = result.toString();
        } else if (result instanceof Integer[]) {
            resultStr = Arrays.toString((Integer[]) result);
        } else if (result instanceof int[]) {
            resultStr = Arrays.toString((int[]) result);
        } else {
            resultStr = result.toString();
        }

        return resultStr; // Don't normalize here anymore
    }

    /**
     * Main method for standalone testing
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java TestRunner <test-file.json>");
            System.out.println("This will look for a Solution class in the current directory");
            return;
        }

        String testFile = args[0];
        System.out.println("Test file: " + testFile);
        System.out.println("Looking for Solution class in current directory...");

        try {
            // Try to load Solution class dynamically
            Class<?> solutionClass = Class.forName("Solution");
            Object solutionInstance = solutionClass.getDeclaredConstructor().newInstance();

            // Try common method names
            String[] commonMethods =
                    {"preorderTraversal", "inorderTraversal", "postorderTraversal", "solve"};
            Method foundMethod = null;

            for (String methodName : commonMethods) {
                try {
                    foundMethod = findSolutionMethod(solutionClass, methodName);
                    System.out.println("Found method: " + methodName);
                    break;
                } catch (NoSuchMethodException e) {
                    // Continue searching
                }
            }

            if (foundMethod == null) {
                System.out.println(
                        "No suitable solution method found. Please specify the method name.");
                return;
            }

            TestResults results = runTests(testFile, solutionInstance, foundMethod.getName());
            results.printSummary();

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
