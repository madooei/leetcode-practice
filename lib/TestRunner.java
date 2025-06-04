import java.lang.reflect.*;
import java.util.*;

/**
 * TestRunner - Main orchestrator for LeetCode problem testing
 * 
 * This refactored TestRunner serves as the primary entry point and coordinator
 * for the modular testing framework. It integrates all the specialized components
 * to provide a clean, easy-to-use interface for running comprehensive test suites.
 * 
 * Key Features:
 * - Simplified API with multiple convenience methods
 * - Integration with all modular components
 * - Automatic method discovery and execution
 * - Optional performance profiling and result saving
 * - Comprehensive error handling and reporting
 * 
 * The orchestrator coordinates between:
 * - FileOperations for loading test data
 * - TestExecutor for running individual tests
 * - TestResultsManager for collecting and reporting results
 * - PerformanceAnalyzer for complexity analysis
 * - OutputFormatter for result presentation
 */
public class TestRunner {

    /**
     * Comprehensive test runner with full feature control
     * 
     * This is the primary method that orchestrates the entire testing process:
     * 1. Loads test cases from the specified file
     * 2. Discovers the solution method using reflection
     * 3. Executes all test cases with optional performance profiling
     * 4. Collects and analyzes results
     * 5. Generates comprehensive reports
     * 6. Optionally saves results to file
     * 
     * @param testFile Path to the JSON file containing test cases
     * @param solutionInstance Instance of the solution class to test
     * @param methodName Name of the method to invoke for testing
     * @param enableProfiling Whether to measure execution time and memory usage
     * @param saveToFile Whether to save results to a JSON file
     * @return TestResultsManager containing all test results and statistics
     */
    public static TestResultsManager runTests(String testFile, Object solutionInstance,
            String methodName, boolean enableProfiling, boolean saveToFile) {

        // Initialize results manager
        TestResultsManager resultsManager = new TestResultsManager();
        if (enableProfiling) {
            resultsManager.enablePerformanceReporting();
        }

        try {
            // Load test cases from file
            List<TestDataModels.TestCase> testCases = FileOperations.loadTestCases(testFile);

            // Display test run header
            printTestRunHeader(testCases.size(), enableProfiling);

            // Discover the solution method using reflection
            Method solutionMethod =
                    TestExecutor.findSolutionMethod(solutionInstance.getClass(), methodName);

            // Execute all test cases
            executeAllTests(testCases, solutionInstance, solutionMethod, resultsManager,
                    enableProfiling);

        } catch (Exception e) {
            System.err.println("Error running tests: " + e.getMessage());
            e.printStackTrace();
        }

        // Save results to file if requested
        if (saveToFile) {
            resultsManager.setTestFilePath(testFile);
            resultsManager.saveToFile();
        }

        return resultsManager;
    }

    /**
     * Test runner with performance profiling enabled
     * 
     * Convenience method that enables performance profiling by default.
     * 
     * @param testFile Path to the JSON file containing test cases
     * @param solutionInstance Instance of the solution class to test
     * @param methodName Name of the method to invoke for testing
     * @param saveToFile Whether to save results to a JSON file
     * @return TestResultsManager containing all test results and statistics
     */
    public static TestResultsManager runTests(String testFile, Object solutionInstance,
            String methodName, boolean saveToFile) {
        return runTests(testFile, solutionInstance, methodName, true, saveToFile);
    }

    /**
     * Simple test runner with default settings
     * 
     * Convenience method for basic testing without performance profiling or file saving.
     * Suitable for quick validation during development.
     * 
     * @param testFile Path to the JSON file containing test cases
     * @param solutionInstance Instance of the solution class to test
     * @param methodName Name of the method to invoke for testing
     * @return TestResultsManager containing all test results and statistics
     */
    public static TestResultsManager runTests(String testFile, Object solutionInstance,
            String methodName) {
        return runTests(testFile, solutionInstance, methodName, false, false);
    }

    /**
     * Executes all test cases in sequence
     * 
     * This method coordinates the execution of individual test cases,
     * providing progress feedback and handling any execution errors gracefully.
     * 
     * @param testCases List of test cases to execute
     * @param solutionInstance Instance of the solution class
     * @param solutionMethod Method to invoke for each test
     * @param resultsManager Manager to collect test results
     * @param enableProfiling Whether performance profiling is enabled
     */
    private static void executeAllTests(List<TestDataModels.TestCase> testCases,
            Object solutionInstance, Method solutionMethod, TestResultsManager resultsManager,
            boolean enableProfiling) {

        for (int i = 0; i < testCases.size(); i++) {
            TestDataModels.TestCase testCase = testCases.get(i);

            // Execute individual test case
            TestExecutor.executeTest(testCase, solutionInstance, solutionMethod, resultsManager,
                    i + 1, enableProfiling);

            // Add spacing between tests for readability
            System.out.println();
        }
    }

    /**
     * Prints the test run header with configuration information
     * 
     * @param testCount Number of test cases to be executed
     * @param enableProfiling Whether performance profiling is enabled
     */
    private static void printTestRunHeader(int testCount, boolean enableProfiling) {
        String profilingInfo = enableProfiling ? " with performance profiling" : "";
        System.out.println("Running " + testCount + " test cases" + profilingInfo + "...");
        System.out.println("=" + "=".repeat(60));
    }

    /**
     * Main method for standalone execution
     * 
     * This method provides a command-line interface for the TestRunner,
     * allowing direct execution from the command line with automatic
     * solution class discovery and method detection.
     * 
     * Usage: java TestRunner <test-file.json> [method-name]
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            printUsageAndExit();
            return;
        }

        String testFile = args[0];
        String methodName = (args.length > 1) ? args[1] : null;

        System.out.println("TestRunner - LeetCode Problem Testing Framework");
        System.out.println("Test file: " + testFile);
        System.out.println("Looking for Solution class in current directory...");

        try {
            // Attempt to load Solution class dynamically
            Class<?> solutionClass = Class.forName("Solution");
            Object solutionInstance = solutionClass.getDeclaredConstructor().newInstance();

            // Discover solution method
            Method foundMethod = discoverSolutionMethod(solutionClass, methodName);
            if (foundMethod == null) {
                printMethodDiscoveryError();
                return;
            }

            System.out.println("Found method: " + foundMethod.getName());
            System.out.println();

            // Execute tests with performance profiling and result saving
            TestResultsManager results =
                    runTests(testFile, solutionInstance, foundMethod.getName(), true, true);

            // Print comprehensive summary
            results.printSummary();

        } catch (ClassNotFoundException e) {
            System.err.println("Error: Solution class not found in current directory");
            System.err.println(
                    "Make sure you have a Solution.java file compiled in the current directory");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Discovers the solution method using intelligent heuristics
     * 
     * This method attempts to find the appropriate solution method by:
     * 1. Using the explicitly specified method name if provided
     * 2. Trying common LeetCode method names
     * 3. Looking for methods that match typical solution patterns
     * 
     * @param solutionClass The solution class to search
     * @param explicitMethodName Explicitly specified method name (can be null)
     * @return The discovered Method object, or null if none found
     */
    private static Method discoverSolutionMethod(Class<?> solutionClass,
            String explicitMethodName) {
        try {
            // Use explicit method name if provided
            if (explicitMethodName != null && !explicitMethodName.trim().isEmpty()) {
                return TestExecutor.findSolutionMethod(solutionClass, explicitMethodName.trim());
            }

            // Try common LeetCode method names
            String[] commonMethods = {"preorderTraversal", "inorderTraversal", "postorderTraversal",
                    "solve", "solution", "twoSum", "addTwoNumbers", "lengthOfLongestSubstring",
                    "findMedianSortedArrays", "longestPalindrome", "convert", "reverse", "myAtoi",
                    "isPalindrome", "maxArea", "intToRoman", "romanToInt", "longestCommonPrefix",
                    "threeSum", "threeSumClosest", "letterCombinations", "fourSum",
                    "removeNthFromEnd", "isValid", "mergeTwoLists", "generateParenthesis"};

            for (String methodName : commonMethods) {
                try {
                    return TestExecutor.findSolutionMethod(solutionClass, methodName);
                } catch (NoSuchMethodException e) {
                    // Continue searching
                }
            }

            // If no common methods found, try to find any public method
            Method[] publicMethods = solutionClass.getDeclaredMethods();
            for (Method method : publicMethods) {
                if (Modifier.isPublic(method.getModifiers()) && !method.getName().equals("main")
                        && !method.getName().startsWith("get")
                        && !method.getName().startsWith("set")) {
                    return method;
                }
            }

        } catch (NoSuchMethodException e) {
            // Method not found with explicit name
        }

        return null; // No suitable method found
    }

    /**
     * Prints usage information and exits
     */
    private static void printUsageAndExit() {
        System.out.println("Usage: java TestRunner <test-file.json> [method-name]");
        System.out.println();
        System.out.println("Arguments:");
        System.out.println("  test-file.json  : JSON file containing test cases");
        System.out.println(
                "  method-name     : Optional method name to invoke (auto-detected if not provided)");
        System.out.println();
        System.out.println("This will look for a Solution class in the current directory");
        System.out.println("and automatically discover the appropriate solution method.");
    }

    /**
     * Prints method discovery error message
     */
    private static void printMethodDiscoveryError() {
        System.err.println("No suitable solution method found in the Solution class.");
        System.err.println("Please specify the method name as a command line argument.");
        System.err.println();
        System.err.println("Example: java TestRunner tests.json myMethod");
        System.err.println();
        System.err.println("Or ensure your Solution class has one of these common method names:");
        System.err.println("  - preorderTraversal, inorderTraversal, postorderTraversal");
        System.err.println("  - solve, solution");
        System.err.println("  - twoSum, addTwoNumbers, etc.");
    }
}
