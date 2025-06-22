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
     * This method analyzes the method signature and converts the input(s)
     * into appropriate argument objects. It supports both single string inputs
     * (backward compatibility) and array inputs (for multiple parameters).
     * 
     * Supported parameter types:
     * - TreeNode parsing for binary tree problems
     * - Integer/int parameter parsing
     * - String parameter parsing (with quote handling)
     * - Null value handling
     * 
     * @param method The method to analyze for parameter types
     * @param input The input string or array to parse
     * @return Array of parsed arguments ready for method invocation
     * @throws RuntimeException if parameter types are unsupported
     */
    private static Object[] parseMethodArguments(Method method, String input) {
        Class<?>[] paramTypes = method.getParameterTypes();

        // Handle methods with no parameters
        if (paramTypes.length == 0) {
            return new Object[0];
        }

        // Determine if input should be parsed as multiple parameters based on method signature
        String[] inputValues = parseInputValues(input, paramTypes.length);

        // Validate parameter count matches input count
        if (paramTypes.length != inputValues.length) {
            throw new RuntimeException(
                    "Parameter count mismatch for method " + method.getName() + ": expected "
                            + paramTypes.length + " but got " + inputValues.length + " inputs");
        }

        // Parse each parameter according to its type
        Object[] args = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            args[i] = parseParameter(paramTypes[i], inputValues[i]);
        }

        return args;
    }

    /**
     * Parses input values from either string or array format
     * 
     * This method determines if the input should be parsed as multiple parameters
     * based on the expected parameter count and input format.
     * 
     * @param input The input to parse (either string or JSON array format)
     * @param expectedParamCount Number of parameters the method expects
     * @return Array of individual input values
     */
    private static String[] parseInputValues(String input, int expectedParamCount) {
        input = input.trim();

        // If method expects only one parameter, treat everything as single input
        // This handles TreeNode arrays like [1,null,2,3] correctly
        if (expectedParamCount == 1) {
            return new String[] {input};
        }

        // For multi-parameter methods, try to parse as JSON array
        if (input.startsWith("[") && input.endsWith("]")) {
            String[] params = parseJsonArray(input);

            // If we get the expected number of parameters, use them
            if (params.length == expectedParamCount) {
                return params;
            }
        }

        // Fallback: treat as single input (may cause parameter count mismatch error later)
        return new String[] {input};
    }

    /**
     * Parses a JSON array string into individual values
     * 
     * @param jsonArray The JSON array string to parse
     * @return Array of individual string values
     */
    private static String[] parseJsonArray(String jsonArray) {
        // Remove outer brackets
        String content = jsonArray.substring(1, jsonArray.length() - 1).trim();

        if (content.isEmpty()) {
            return new String[0];
        }

        List<String> values = new ArrayList<>();
        int depth = 0;
        int start = 0;
        boolean inQuotes = false;
        boolean escaped = false;

        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);

            if (escaped) {
                escaped = false;
                continue;
            }

            if (c == '\\') {
                escaped = true;
                continue;
            }

            if (c == '"' && !escaped) {
                inQuotes = !inQuotes;
            } else if (!inQuotes) {
                if (c == '[' || c == '{') {
                    depth++;
                } else if (c == ']' || c == '}') {
                    depth--;
                } else if (c == ',' && depth == 0) {
                    // Found separator at top level
                    values.add(content.substring(start, i).trim());
                    start = i + 1;
                }
            }
        }

        // Add the last value
        if (start < content.length()) {
            values.add(content.substring(start).trim());
        }

        return values.toArray(new String[0]);
    }

    /**
     * Parses a single parameter based on its type
     * 
     * @param paramType The expected parameter type
     * @param inputValue The string value to parse
     * @return The parsed parameter object
     * @throws RuntimeException if parameter type is unsupported
     */
    private static Object parseParameter(Class<?> paramType, String inputValue) {
        // Handle TreeNode parameter for binary tree problems
        if (paramType.getSimpleName().equals("TreeNode")) {
            // Remove quotes from TreeNode input if present
            String cleanInput = parseStringValue(inputValue);
            Integer[] array = TreeNode.parseArray(cleanInput);
            return TreeNode.fromArray(array);
        }

        // Handle integer parameters
        if (paramType == int.class || paramType == Integer.class) {
            // Remove quotes if present
            String cleanInput = parseStringValue(inputValue);
            return Integer.parseInt(cleanInput.trim());
        }

        // Handle string parameters
        if (paramType == String.class) {
            return parseStringValue(inputValue);
        }

        // Handle boolean parameters
        if (paramType == boolean.class || paramType == Boolean.class) {
            String cleanInput = parseStringValue(inputValue);
            return Boolean.parseBoolean(cleanInput.trim().toLowerCase());
        }

        // Handle double parameters
        if (paramType == double.class || paramType == Double.class) {
            String cleanInput = parseStringValue(inputValue);
            return Double.parseDouble(cleanInput.trim());
        }

        // Handle long parameters
        if (paramType == long.class || paramType == Long.class) {
            String cleanInput = parseStringValue(inputValue);
            return Long.parseLong(cleanInput.trim());
        }

        throw new RuntimeException("Unsupported parameter type: " + paramType.getName());
    }

    /**
     * Parses string values with proper null and quote handling
     * 
     * @param input The input string to parse
     * @return The parsed string value
     */
    private static String parseStringValue(String input) {
        input = input.trim();

        // Handle null input
        if (input.equals("null")) {
            return null;
        }

        // Remove surrounding quotes for string inputs (may have multiple levels)
        // Handle escaped quotes properly
        while (input.startsWith("\"") && input.endsWith("\"") && input.length() >= 2) {
            String unquoted = input.substring(1, input.length() - 1);

            // If after removing quotes we still have quotes, and they are escaped, unescape them
            if (unquoted.startsWith("\\\"") && unquoted.endsWith("\\\"")) {
                unquoted = unquoted.substring(2, unquoted.length() - 2);
            }

            input = unquoted;

            // Prevent infinite loop if the string doesn't change
            if (input.equals(unquoted)) {
                break;
            }
        }

        // Handle remaining escape sequences
        input = input.replace("\\\"", "\"").replace("\\\\", "\\");

        return input;
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
