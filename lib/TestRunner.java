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
        public PerformanceResult performance;

        public TestResult(String testName, boolean passed, String input, String expected,
                String actual, String error, PerformanceResult performance) {
            this.testName = testName;
            this.passed = passed;
            this.input = input;
            this.expected = expected;
            this.actual = actual;
            this.error = error;
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

        public void addResult(String testName, boolean passed, String input, String expected,
                String actual, String error, PerformanceResult performance) {
            results.add(
                    new TestResult(testName, passed, input, expected, actual, error, performance));
            this.total++;
            if (passed)
                this.passed++;
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
            // Very basic complexity analysis based on execution time trends
            List<TestResult> passedResults =
                    results.stream().filter(r -> r.passed && r.performance != null)
                            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

            if (passedResults.size() >= 3) {
                // Sort by input size (very rough estimation)
                passedResults.sort((a, b) -> a.input.length() - b.input.length());

                double firstTime = passedResults.get(0).performance.getExecutionTimeMs();
                double lastTime = passedResults.get(passedResults.size() - 1).performance
                        .getExecutionTimeMs();

                if (lastTime > firstTime * 2) {
                    System.out.println(
                            "  💡 Performance scales with input size - consider optimizing for larger inputs");
                } else {
                    System.out.println("  ✅ Performance appears consistent across test cases");
                }
            }
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
     */
    public static TestResults runTests(String testFile, Object solutionInstance, String methodName,
            boolean enableProfiling) {
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

        return results;
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
            passed = actualStr.equals(testCase.expected);

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
                error, performance);
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
                return new Object[] {input.replace("\"", "")};
            }

            // Add more parameter type handling as needed
        }

        throw new RuntimeException("Unsupported parameter types for method: " + method.getName());
    }

    /**
     * Convert result object to string for comparison Normalizes formatting to match expected output
     * (no spaces after commas)
     */
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

        // Normalize formatting: remove spaces after commas to match expected format
        return resultStr.replaceAll(", ", ",");
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
