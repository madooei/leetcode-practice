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
     * Test results tracking
     */
    public static class TestResults {
        public List<TestResult> results = new ArrayList<>();
        public int passed = 0;
        public int total = 0;

        public void addResult(String testName, boolean passed, String input, String expected,
                String actual, String error) {
            results.add(new TestResult(testName, passed, input, expected, actual, error));
            this.total++;
            if (passed)
                this.passed++;
        }

        public void printSummary() {
            System.out.println("=" + "=".repeat(60));
            System.out.println(String.format("Results: %d/%d tests passed (%.1f%%)", passed, total,
                    total > 0 ? (passed * 100.0 / total) : 0));

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
    }

    public static class TestResult {
        public String testName;
        public boolean passed;
        public String input;
        public String expected;
        public String actual;
        public String error;

        public TestResult(String testName, boolean passed, String input, String expected,
                String actual, String error) {
            this.testName = testName;
            this.passed = passed;
            this.input = input;
            this.expected = expected;
            this.actual = actual;
            this.error = error;
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
     */
    public static TestResults runTests(String testFile, Object solutionInstance,
            String methodName) {
        TestResults results = new TestResults();

        try {
            List<TestCase> testCases = loadTestCases(testFile);
            System.out.println("Running " + testCases.size() + " test cases...");
            System.out.println("=" + "=".repeat(60));

            // Get the solution method using reflection
            Method solutionMethod = findSolutionMethod(solutionInstance.getClass(), methodName);

            for (int i = 0; i < testCases.size(); i++) {
                TestCase testCase = testCases.get(i);
                runSingleTest(testCase, solutionInstance, solutionMethod, results, i + 1);
                System.out.println();
            }

        } catch (Exception e) {
            System.err.println("Error running tests: " + e.getMessage());
            e.printStackTrace();
        }

        return results;
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
     * Run a single test case
     */
    private static void runSingleTest(TestCase testCase, Object solutionInstance,
            Method solutionMethod, TestResults results, int testNum) {
        String error = null;
        String actualStr = "";
        boolean passed = false;

        try {
            System.out.println("Test " + testNum + ": " + testCase.name);
            if (!testCase.description.isEmpty()) {
                System.out.println("Description: " + testCase.description);
            }

            // Parse input based on method parameter types
            Object[] args = parseMethodArguments(solutionMethod, testCase.input);

            // Execute the solution method
            Object result = solutionMethod.invoke(solutionInstance, args);

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
                error);
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
