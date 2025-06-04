import java.lang.reflect.*;
import java.util.*;

/**
 * TestExecutor - Core engine for executing individual test cases
 * 
 * This class handles the execution of individual test cases including:
 * - Method discovery and reflection-based invocation
 * - Argument parsing and type conversion
 * - Performance measurement integration
 * - Exception handling and error reporting
 * - Result validation and comparison
 * 
 * The executor supports various input types including TreeNode structures,
 * primitive types, strings, and arrays, with automatic type detection and conversion.
 */
public class TestExecutor {

    /**
     * Executes a single test case against a solution method
     * 
     * This method coordinates the complete test execution process:
     * 1. Parses input arguments based on method signature
     * 2. Invokes the solution method (with optional performance measurement)
     * 3. Validates the result against expected output
     * 4. Handles exceptions and error conditions
     * 5. Records the result in the results manager
     * 
     * @param testCase The test case to execute
     * @param solutionInstance Instance of the solution class
     * @param solutionMethod Method to invoke for testing
     * @param resultsManager Manager to record the test result
     * @param testNumber Sequential number of this test (for display)
     * @param enableProfiling Whether to measure performance metrics
     */
    public static void executeTest(TestDataModels.TestCase testCase, Object solutionInstance,
            Method solutionMethod, TestResultsManager resultsManager, int testNumber,
            boolean enableProfiling) {

        String error = null;
        String actualStr = "";
        boolean passed = false;
        TestDataModels.PerformanceResult performance = null;

        try {
            // Display test information
            printTestHeader(testNumber, testCase);

            // Parse input arguments based on method signature
            Object[] args = parseMethodArguments(solutionMethod, testCase.input);

            // Execute the method with optional performance measurement
            Object result = executeMethodWithProfiling(solutionInstance, solutionMethod, args,
                    enableProfiling);

            // Extract performance data if profiling was enabled
            if (enableProfiling && result instanceof ExecutionResult) {
                ExecutionResult execResult = (ExecutionResult) result;
                performance = execResult.performance;
                result = execResult.actualResult;
                System.out.println("Performance: " + performance);
            }

            // Convert result to string for comparison
            actualStr = OutputFormatter.resultToString(result);

            // Validate result against expected output
            passed = validateResult(testCase.expected, actualStr);

            // Display execution results
            printTestResults(testCase, actualStr, passed);

        } catch (Exception e) {
            error = e.getMessage();
            printTestError(testCase, error);
        }

        // Record the test result
        resultsManager.addResult(testCase.name, passed, testCase.input, testCase.expected,
                actualStr, error, testCase.description, performance);
    }

    /**
     * Finds a solution method by name using reflection
     * 
     * This method searches through all declared methods of a class to find
     * a method with the specified name. It's case-sensitive and returns the
     * first matching method found.
     * 
     * @param clazz The class to search for methods
     * @param methodName The name of the method to find
     * @return The Method object if found
     * @throws NoSuchMethodException if no method with the given name exists
     */
    public static Method findSolutionMethod(Class<?> clazz, String methodName)
            throws NoSuchMethodException {
        // Search through all declared methods
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        throw new NoSuchMethodException(
                "Method " + methodName + " not found in " + clazz.getName());
    }

    /**
     * Parses method arguments based on parameter types
     * 
     * This method analyzes the method signature and converts the input string
     * into appropriate argument objects. It supports:
     * - TreeNode parsing for binary tree problems
     * - Integer/int parameter parsing
     * - String parameter parsing (with quote handling)
     * - Null value handling
     * 
     * @param method The method to analyze for parameter types
     * @param input The input string to parse
     * @return Array of parsed arguments ready for method invocation
     * @throws RuntimeException if parameter types are unsupported
     */
    private static Object[] parseMethodArguments(Method method, String input) {
        Class<?>[] paramTypes = method.getParameterTypes();

        // Handle methods with no parameters
        if (paramTypes.length == 0) {
            return new Object[0];
        }

        // Currently supports single-parameter methods
        if (paramTypes.length == 1) {
            Class<?> paramType = paramTypes[0];

            // Handle TreeNode parameter for binary tree problems
            if (paramType.getSimpleName().equals("TreeNode")) {
                Integer[] array = TreeNode.parseArray(input);
                return new Object[] {TreeNode.fromArray(array)};
            }

            // Handle integer parameters
            if (paramType == int.class || paramType == Integer.class) {
                return new Object[] {Integer.parseInt(input)};
            }

            // Handle string parameters
            if (paramType == String.class) {
                return parseStringParameter(input);
            }

            // Add support for more parameter types as needed
        }

        throw new RuntimeException("Unsupported parameter types for method: " + method.getName());
    }

    /**
     * Parses string parameters with proper null and quote handling
     * 
     * @param input The input string to parse
     * @return Array containing the parsed string parameter
     */
    private static Object[] parseStringParameter(String input) {
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

    /**
     * Executes a method with optional performance profiling
     * 
     * @param instance The object instance to invoke the method on
     * @param method The method to execute
     * @param args Arguments to pass to the method
     * @param enableProfiling Whether to measure performance
     * @return The method result, or ExecutionResult if profiling is enabled
     * @throws Exception if method execution fails
     */
    private static Object executeMethodWithProfiling(Object instance, Method method, Object[] args,
            boolean enableProfiling) throws Exception {
        if (enableProfiling) {
            // Measure performance with warmup and multiple iterations
            TestDataModels.PerformanceResult performance =
                    PerformanceAnalyzer.measurePerformance(instance, method, args, 3, 5);

            // Execute once more for correctness verification
            Object result = method.invoke(instance, args);

            return new ExecutionResult(result, performance);
        } else {
            // Regular execution without performance measurement
            return method.invoke(instance, args);
        }
    }

    /**
     * Validates the actual result against the expected output
     * 
     * This method normalizes both the expected and actual outputs before
     * comparison to handle formatting differences and ensure accurate validation.
     * 
     * @param expected The expected output string
     * @param actual The actual output string
     * @return true if the outputs match after normalization
     */
    private static boolean validateResult(String expected, String actual) {
        String normalizedExpected = OutputFormatter.normalizeOutput(expected);
        String normalizedActual = OutputFormatter.normalizeOutput(actual);
        return normalizedExpected.equals(normalizedActual);
    }

    /**
     * Prints the test header with test number and description
     * 
     * @param testNumber Sequential number of the test
     * @param testCase The test case being executed
     */
    private static void printTestHeader(int testNumber, TestDataModels.TestCase testCase) {
        System.out.println("Test " + testNumber + ": " + testCase.name);
        if (!testCase.description.isEmpty()) {
            System.out.println("Description: " + testCase.description);
        }
    }

    /**
     * Prints the test execution results
     * 
     * @param testCase The executed test case
     * @param actualStr The actual output produced
     * @param passed Whether the test passed validation
     */
    private static void printTestResults(TestDataModels.TestCase testCase, String actualStr,
            boolean passed) {
        System.out.println("Input:    " + testCase.input);
        System.out.println("Expected: " + testCase.expected);
        System.out.println("Got:      " + actualStr);
        System.out.println("Status:   " + (passed ? "✅ PASS" : "❌ FAIL"));
    }

    /**
     * Prints test error information
     * 
     * @param testCase The test case that failed
     * @param error The error message
     */
    private static void printTestError(TestDataModels.TestCase testCase, String error) {
        System.out.println("Input:    " + testCase.input);
        System.out.println("Expected: " + testCase.expected);
        System.out.println("Status:   ❌ ERROR - " + error);
    }

    /**
     * ExecutionResult - Internal class for returning both result and performance data
     * 
     * This wrapper class allows the execution method to return both the actual
     * method result and performance metrics when profiling is enabled.
     */
    private static class ExecutionResult {
        /** The actual result returned by the method */
        final Object actualResult;

        /** Performance metrics from the execution */
        final TestDataModels.PerformanceResult performance;

        /**
         * Constructs an ExecutionResult with both result and performance data
         * 
         * @param actualResult The method's return value
         * @param performance Performance metrics from execution
         */
        ExecutionResult(Object actualResult, TestDataModels.PerformanceResult performance) {
            this.actualResult = actualResult;
            this.performance = performance;
        }
    }
}
