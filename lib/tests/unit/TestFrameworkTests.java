package tests.unit;

import java.util.*;
import shared.FileOperations;
import shared.JsonProcessor;
import shared.TestDataModels;
import shared.TreeNode;
import shared.TypeParser;
import java.io.*;
import java.nio.file.*;

/**
 * Comprehensive unit tests for the LeetCode Test Framework
 * 
 * This test suite validates all major components of the testing framework:
 * - TypeParser for all supported data types
 * - TestDataModels for enhanced TestCase functionality
 * - JsonProcessor for both legacy and enhanced JSON parsing
 * - FileOperations for file handling
 * - End-to-end integration tests
 * - Backward compatibility verification
 */
public class TestFrameworkTests {

    private static int totalTests = 0;
    private static int passedTests = 0;
    private static List<String> failures = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println("LeetCode Test Framework - Comprehensive Unit Tests");
        System.out.println("=".repeat(70));
        System.out.println();

        // Run all test suites
        testTypeParser();
        testTestDataModels();
        testJsonProcessor();
        testFileOperations();
        testBackwardCompatibility();
        testIntegration();

        // Print final results
        printTestSummary();
    }

    // ================================
    // TypeParser Tests
    // ================================

    private static void testTypeParser() {
        System.out.println("Testing TypeParser...");

        // Primitive types
        testPrimitiveTypes();
        testArrayTypes();
        test2DArrayTypes();
        testListTypes();
        testTreeNodeTypes();
        testEdgeCases();

        System.out.println();
    }

    private static void testPrimitiveTypes() {
        // Integer tests
        assertEqual("Integer parsing", 42, TypeParser.parseParameter(int.class, "42", "int"));
        assertEqual("Integer from quoted string", 100,
                TypeParser.parseParameter(int.class, "\"100\"", "int"));

        // String tests
        assertEqual("String parsing", "hello",
                TypeParser.parseParameter(String.class, "\"hello\"", "String"));
        assertEqual("String with spaces", "hello world",
                TypeParser.parseParameter(String.class, "\"hello world\"", "String"));

        // Boolean tests
        assertEqual("Boolean true", true,
                TypeParser.parseParameter(boolean.class, "true", "boolean"));
        assertEqual("Boolean false", false,
                TypeParser.parseParameter(boolean.class, "false", "boolean"));

        // Double tests
        assertEqual("Double parsing", 3.14,
                TypeParser.parseParameter(double.class, "3.14", "double"));

        // Long tests
        assertEqual("Long parsing", 999999999L,
                TypeParser.parseParameter(long.class, "999999999", "long"));

        // Character tests
        assertEqual("Character parsing", 'a',
                TypeParser.parseParameter(char.class, "\"a\"", "char"));
    }

    private static void testArrayTypes() {
        // int[] tests
        int[] expectedIntArray = {1, 2, 3, 4, 5};
        int[] actualIntArray =
                (int[]) TypeParser.parseParameter(int[].class, "[1,2,3,4,5]", "int[]");
        assertArrayEqual("int[] parsing", expectedIntArray, actualIntArray);

        // String[] tests
        String[] expectedStringArray = {"hello", "world", "test"};
        String[] actualStringArray = (String[]) TypeParser.parseParameter(String[].class,
                "[\"hello\",\"world\",\"test\"]", "String[]");
        assertArrayEqual("String[] parsing", expectedStringArray, actualStringArray);

        // double[] tests
        double[] expectedDoubleArray = {1.1, 2.2, 3.3};
        double[] actualDoubleArray =
                (double[]) TypeParser.parseParameter(double[].class, "[1.1,2.2,3.3]", "double[]");
        assertArrayEqual("double[] parsing", expectedDoubleArray, actualDoubleArray);

        // boolean[] tests
        boolean[] expectedBoolArray = {true, false, true};
        boolean[] actualBoolArray = (boolean[]) TypeParser.parseParameter(boolean[].class,
                "[true,false,true]", "boolean[]");
        assertArrayEqual("boolean[] parsing", expectedBoolArray, actualBoolArray);
    }

    private static void test2DArrayTypes() {
        // int[][] tests
        int[][] expected2DArray = {{1, 2}, {3, 4}, {5, 6}};
        int[][] actual2DArray = (int[][]) TypeParser.parseParameter(int[][].class,
                "[[1,2],[3,4],[5,6]]", "int[][]");
        assert2DArrayEqual("int[][] parsing", expected2DArray, actual2DArray);

        // char[][] tests (matrix)
        char[][] expectedCharMatrix = {{'a', 'b'}, {'c', 'd'}};
        char[][] actualCharMatrix = (char[][]) TypeParser.parseParameter(char[][].class,
                "[[\"a\",\"b\"],[\"c\",\"d\"]]", "char[][]");
        assert2DCharArrayEqual("char[][] parsing", expectedCharMatrix, actualCharMatrix);
    }

    private static void testListTypes() {
        // List<Integer> tests
        List<Integer> expectedIntList = Arrays.asList(1, 2, 3, 4, 5);
        @SuppressWarnings("unchecked")
        List<Integer> actualIntList = (List<Integer>) TypeParser.parseParameter(List.class,
                "[1,2,3,4,5]", "List<Integer>");
        assertEqual("List<Integer> parsing", expectedIntList, actualIntList);

        // List<String> tests
        List<String> expectedStringList = Arrays.asList("hello", "world");
        @SuppressWarnings("unchecked")
        List<String> actualStringList = (List<String>) TypeParser.parseParameter(List.class,
                "[\"hello\",\"world\"]", "List<String>");
        assertEqual("List<String> parsing", expectedStringList, actualStringList);
    }

    private static void testTreeNodeTypes() {
        // TreeNode tests
        TreeNode expectedTree = TreeNode.fromArray(new Integer[] {1, 2, 3, 4, 5});
        TreeNode actualTree =
                (TreeNode) TypeParser.parseParameter(TreeNode.class, "[1,2,3,4,5]", "TreeNode");

        // Compare tree structures by converting back to arrays
        List<Integer> expectedArray = TreeNode.toArray(expectedTree);
        List<Integer> actualArray = TreeNode.toArray(actualTree);
        assertEqual("TreeNode parsing", expectedArray, actualArray);

        // Test null tree
        TreeNode nullTree = (TreeNode) TypeParser.parseParameter(TreeNode.class, "[]", "TreeNode");
        assertEqual("Empty TreeNode", null, nullTree);
    }

    private static void testEdgeCases() {
        // Null handling
        assertEqual("Null string", null, TypeParser.parseParameter(String.class, "null", "String"));
        assertEqual("Null integer", null,
                TypeParser.parseParameter(Integer.class, "null", "Integer"));

        // Empty arrays
        int[] emptyIntArray = (int[]) TypeParser.parseParameter(int[].class, "[]", "int[]");
        assertEqual("Empty int array length", 0, emptyIntArray.length);

        // Whitespace handling
        assertEqual("Integer with whitespace", 42,
                TypeParser.parseParameter(int.class, "  42  ", "int"));
        assertEqual("String with whitespace", "test",
                TypeParser.parseParameter(String.class, "  \"test\"  ", "String"));
    }

    // ================================
    // TestDataModels Tests
    // ================================

    private static void testTestDataModels() {
        System.out.println("Testing TestDataModels...");

        testLegacyTestCase();
        testEnhancedTestCase();
        testTypeHints();

        System.out.println();
    }

    private static void testLegacyTestCase() {
        Map<String, Object> legacyData = new HashMap<>();
        legacyData.put("name", "Legacy Test");
        legacyData.put("input", "\"hello world\"");
        legacyData.put("expected", "\"hello world\"");
        legacyData.put("description", "Test backward compatibility");

        TestDataModels.TestCase testCase = new TestDataModels.TestCase(legacyData);

        assertEqual("Legacy name", "Legacy Test", testCase.name);
        assertEqual("Legacy input", "\"hello world\"", testCase.input);
        assertEqual("Legacy expected", "\"hello world\"", testCase.expected);
        assertEqual("Legacy description", "Test backward compatibility", testCase.description);
        assertEqual("Legacy has no type hints", false, testCase.hasTypeHints());
    }

    private static void testEnhancedTestCase() {
        Map<String, Object> enhancedData = new HashMap<>();
        enhancedData.put("name", "Enhanced Test");
        enhancedData.put("input", Arrays.asList("[1,2,3]", "5"));
        enhancedData.put("inputTypes", Arrays.asList("int[]", "int"));
        enhancedData.put("expected", "true");
        enhancedData.put("description", "Test array and integer inputs");

        TestDataModels.TestCase testCase = new TestDataModels.TestCase(enhancedData);

        assertEqual("Enhanced name", "Enhanced Test", testCase.name);
        assertEqual("Enhanced input", "[\"[1,2,3]\", \"5\"]", testCase.input);
        assertEqual("Enhanced expected", "true", testCase.expected);
        assertEqual("Enhanced description", "Test array and integer inputs", testCase.description);
        assertEqual("Enhanced has type hints", true, testCase.hasTypeHints());
        assertArrayEqual("Enhanced type hints", new String[] {"int[]", "int"}, testCase.inputTypes);
    }

    private static void testTypeHints() {
        // Test with type hints
        Map<String, Object> withHints = new HashMap<>();
        withHints.put("name", "With Hints");
        withHints.put("input", Arrays.asList("[1,2,3]"));
        withHints.put("inputTypes", Arrays.asList("int[]"));
        withHints.put("expected", "[1,2,3]");

        TestDataModels.TestCase withHintsCase = new TestDataModels.TestCase(withHints);
        assertEqual("Has type hints", true, withHintsCase.hasTypeHints());
        assertEqual("Type hints length", 1, withHintsCase.inputTypes.length);
        assertEqual("Type hint value", "int[]", withHintsCase.inputTypes[0]);

        // Test without type hints
        Map<String, Object> withoutHints = new HashMap<>();
        withoutHints.put("name", "Without Hints");
        withoutHints.put("input", "[1,2,3]");
        withoutHints.put("expected", "[1,2,3]");

        TestDataModels.TestCase withoutHintsCase = new TestDataModels.TestCase(withoutHints);
        assertEqual("No type hints", false, withoutHintsCase.hasTypeHints());
    }

    // ================================
    // JsonProcessor Tests
    // ================================

    private static void testJsonProcessor() {
        System.out.println("Testing JsonProcessor...");

        testBasicJsonParsing();
        testEnhancedJsonParsing();
        testComplexJsonStructures();

        System.out.println();
    }

    private static void testBasicJsonParsing() {
        String jsonInput =
                "[{\"name\":\"Test 1\",\"input\":\"42\",\"expected\":\"42\",\"description\":\"Simple test\"}]";

        List<Map<String, String>> legacyResult = JsonProcessor.parseTestCases(jsonInput);
        assertEqual("Legacy parsing - count", 1, legacyResult.size());

        Map<String, String> testCase = legacyResult.get(0);
        assertEqual("Legacy parsing - name", "Test 1", testCase.get("name"));
        assertEqual("Legacy parsing - input", "42", testCase.get("input"));
        assertEqual("Legacy parsing - expected", "42", testCase.get("expected"));
        assertEqual("Legacy parsing - description", "Simple test", testCase.get("description"));

        List<Map<String, Object>> rawResult = JsonProcessor.parseTestCasesRaw(jsonInput);
        assertEqual("Raw parsing - count", 1, rawResult.size());

        Map<String, Object> rawTestCase = rawResult.get(0);
        assertEqual("Raw parsing - name", "Test 1", rawTestCase.get("name"));
        assertEqual("Raw parsing - input", "42", rawTestCase.get("input"));
    }

    private static void testEnhancedJsonParsing() {
        String enhancedJson =
                "[{\"name\":\"Enhanced Test\",\"input\":[\"[1,2,3]\",\"5\"],\"inputTypes\":[\"int[]\",\"int\"],\"expected\":\"true\"}]";

        List<Map<String, Object>> result = JsonProcessor.parseTestCasesRaw(enhancedJson);
        assertEqual("Enhanced parsing - count", 1, result.size());

        Map<String, Object> testCase = result.get(0);
        assertEqual("Enhanced parsing - name", "Enhanced Test", testCase.get("name"));

        @SuppressWarnings("unchecked")
        List<String> inputList = (List<String>) testCase.get("input");
        assertEqual("Enhanced parsing - input list size", 2, inputList.size());
        assertEqual("Enhanced parsing - first input", "[1,2,3]", inputList.get(0));
        assertEqual("Enhanced parsing - second input", "5", inputList.get(1));

        @SuppressWarnings("unchecked")
        List<String> typesList = (List<String>) testCase.get("inputTypes");
        assertEqual("Enhanced parsing - types list size", 2, typesList.size());
        assertEqual("Enhanced parsing - first type", "int[]", typesList.get(0));
        assertEqual("Enhanced parsing - second type", "int", typesList.get(1));
    }

    private static void testComplexJsonStructures() {
        String complexJson =
                "[{\"name\":\"Complex\",\"input\":[[1,2],[3,4]],\"expected\":\"result\"}]";

        List<Map<String, Object>> result = JsonProcessor.parseTestCasesRaw(complexJson);
        assertEqual("Complex parsing - count", 1, result.size());

        Map<String, Object> testCase = result.get(0);
        @SuppressWarnings("unchecked")
        List<List<Long>> nestedList = (List<List<Long>>) testCase.get("input");
        assertEqual("Complex parsing - outer list size", 2, nestedList.size());
        assertEqual("Complex parsing - first inner list size", 2, nestedList.get(0).size());
        assertEqual("Complex parsing - first element", Long.valueOf(1), nestedList.get(0).get(0));
    }

    // ================================
    // FileOperations Tests
    // ================================

    private static void testFileOperations() {
        System.out.println("Testing FileOperations...");

        try {
            testFileLoading();
            testFileCreationAndCleanup();
        } catch (Exception e) {
            addFailure("FileOperations test failed: " + e.getMessage());
        }

        System.out.println();
    }

    private static void testFileLoading() throws IOException {
        // Create a temporary test file
        String testJson =
                "[{\"name\":\"File Test\",\"input\":\"42\",\"expected\":\"42\",\"description\":\"Test file loading\"}]";
        String tempFile = "temp_test.json";

        try {
            Files.writeString(Paths.get(tempFile), testJson);

            List<TestDataModels.TestCase> testCases = FileOperations.loadTestCases(tempFile);
            assertEqual("File loading - test case count", 1, testCases.size());

            TestDataModels.TestCase testCase = testCases.get(0);
            assertEqual("File loading - name", "File Test", testCase.name);
            assertEqual("File loading - input", "42", testCase.input);
            assertEqual("File loading - expected", "42", testCase.expected);

        } finally {
            // Cleanup
            try {
                Files.deleteIfExists(Paths.get(tempFile));
            } catch (IOException e) {
                // Ignore cleanup errors
            }
        }
    }

    private static void testFileCreationAndCleanup() throws IOException {
        String testFile = "test_output.json";
        String testContent = "{\"test\": \"content\"}";

        try {
            // Test file writing
            FileOperations.writeToFile(testFile, testContent);
            assertEqual("File exists after writing", true,
                    FileOperations.isFileAccessible(testFile));

            // Test file reading
            String readContent = Files.readString(Paths.get(testFile));
            assertEqual("File content matches", testContent, readContent);

            // Test file info
            String fileInfo = FileOperations.getFileInfo(testFile);
            assertEqual("File info contains path", true, fileInfo.contains(testFile));

        } finally {
            // Cleanup
            try {
                Files.deleteIfExists(Paths.get(testFile));
            } catch (IOException e) {
                // Ignore cleanup errors
            }
        }
    }

    // ================================
    // Backward Compatibility Tests
    // ================================

    private static void testBackwardCompatibility() {
        System.out.println("Testing Backward Compatibility...");

        testLegacyJsonFormat();
        testLegacyParameterParsing();

        System.out.println();
    }

    private static void testLegacyJsonFormat() {
        // Test that old JSON format still works
        String legacyJson =
                "[{\"name\":\"Legacy\",\"input\":\"[1,2,3]\",\"expected\":\"[1,2,3]\",\"description\":\"Legacy format\"}]";

        try {
            List<Map<String, String>> legacyResult = JsonProcessor.parseTestCases(legacyJson);
            assertEqual("Legacy JSON parsing works", 1, legacyResult.size());

            List<Map<String, Object>> rawResult = JsonProcessor.parseTestCasesRaw(legacyJson);
            assertEqual("Legacy JSON raw parsing works", 1, rawResult.size());

            // Test that TestCase constructor works with legacy format
            TestDataModels.TestCase testCase = new TestDataModels.TestCase(rawResult.get(0));
            assertEqual("Legacy TestCase creation", "Legacy", testCase.name);
            assertEqual("Legacy TestCase has no type hints", false, testCase.hasTypeHints());

        } catch (Exception e) {
            addFailure("Legacy JSON format compatibility failed: " + e.getMessage());
        }
    }

    private static void testLegacyParameterParsing() {
        // Test that old parameter parsing still works without type hints
        try {
            // TreeNode parsing without explicit type hint (should use reflection)
            Object result = TypeParser.parseParameter(TreeNode.class, "[1,2,3]", null);
            assertEqual("Legacy TreeNode parsing works", true, result instanceof TreeNode);

            // Integer parsing without explicit type hint
            Object intResult = TypeParser.parseParameter(int.class, "42", null);
            assertEqual("Legacy int parsing works", 42, intResult);

            // String parsing without explicit type hint
            Object stringResult = TypeParser.parseParameter(String.class, "\"hello\"", null);
            assertEqual("Legacy String parsing works", "hello", stringResult);

        } catch (Exception e) {
            addFailure("Legacy parameter parsing compatibility failed: " + e.getMessage());
        }
    }

    // ================================
    // Integration Tests
    // ================================

    private static void testIntegration() {
        System.out.println("Testing End-to-End Integration...");

        testFullWorkflow();

        System.out.println();
    }

    private static void testFullWorkflow() {
        try {
            // Create a comprehensive test file
            String testJson = "[\n" + "  {\n" + "    \"name\": \"Array Problem\",\n"
                    + "    \"input\": [\"[1,2,3,4,5]\", \"3\"],\n"
                    + "    \"inputTypes\": [\"int[]\", \"int\"],\n"
                    + "    \"expected\": \"true\",\n"
                    + "    \"description\": \"Test array with target value\"\n" + "  },\n" + "  {\n"
                    + "    \"name\": \"Tree Problem\",\n" + "    \"input\": \"[1,2,3,4,5]\",\n"
                    + "    \"expected\": \"[1,2,4,5,3]\",\n"
                    + "    \"description\": \"Test tree traversal (legacy format)\"\n" + "  }\n"
                    + "]";

            String tempFile = "integration_test.json";

            try {
                // Write test file
                Files.writeString(Paths.get(tempFile), testJson);

                // Load test cases using FileOperations
                List<TestDataModels.TestCase> testCases = FileOperations.loadTestCases(tempFile);
                assertEqual("Integration - test case count", 2, testCases.size());

                // Test first case (enhanced format with type hints)
                TestDataModels.TestCase case1 = testCases.get(0);
                assertEqual("Integration - case1 name", "Array Problem", case1.name);
                assertEqual("Integration - case1 has type hints", true, case1.hasTypeHints());
                assertEqual("Integration - case1 type hints count", 2, case1.inputTypes.length);
                assertEqual("Integration - case1 first type", "int[]", case1.inputTypes[0]);
                assertEqual("Integration - case1 second type", "int", case1.inputTypes[1]);

                // Test second case (legacy format)
                TestDataModels.TestCase case2 = testCases.get(1);
                assertEqual("Integration - case2 name", "Tree Problem", case2.name);
                assertEqual("Integration - case2 has no type hints", false, case2.hasTypeHints());
                assertEqual("Integration - case2 input", "[1,2,3,4,5]", case2.input);

                // Test parameter parsing for both cases
                if (case1.hasTypeHints()) {
                    String[] inputValues =
                            case1.input.substring(1, case1.input.length() - 1).split(", ");
                    Object param1 = TypeParser.parseParameter(int[].class,
                            inputValues[0].replace("\"", ""), case1.inputTypes[0]);
                    Object param2 = TypeParser.parseParameter(int.class,
                            inputValues[1].replace("\"", ""), case1.inputTypes[1]);

                    assertEqual("Integration - param1 is int array", true, param1 instanceof int[]);
                    assertEqual("Integration - param2 is integer", true, param2 instanceof Integer);
                }

                // Test TreeNode parsing for case2 (legacy)
                Object treeParam = TypeParser.parseParameter(TreeNode.class, case2.input, null);
                assertEqual("Integration - tree param is TreeNode", true,
                        treeParam instanceof TreeNode);

            } finally {
                // Cleanup
                try {
                    Files.deleteIfExists(Paths.get(tempFile));
                } catch (IOException e) {
                    // Ignore cleanup errors
                }
            }

        } catch (Exception e) {
            addFailure("Integration test failed: " + e.getMessage());
        }
    }

    // ================================
    // Test Utilities
    // ================================

    private static void assertEqual(String testName, Object expected, Object actual) {
        totalTests++;
        if (Objects.equals(expected, actual)) {
            passedTests++;
            System.out.println("✅ " + testName);
        } else {
            String failure = "❌ " + testName + " - Expected: " + expected + ", Actual: " + actual;
            System.out.println(failure);
            failures.add(failure);
        }
    }

    private static void assertArrayEqual(String testName, int[] expected, int[] actual) {
        totalTests++;
        if (Arrays.equals(expected, actual)) {
            passedTests++;
            System.out.println("✅ " + testName);
        } else {
            String failure = "❌ " + testName + " - Expected: " + Arrays.toString(expected)
                    + ", Actual: " + Arrays.toString(actual);
            System.out.println(failure);
            failures.add(failure);
        }
    }

    private static void assertArrayEqual(String testName, String[] expected, String[] actual) {
        totalTests++;
        if (Arrays.equals(expected, actual)) {
            passedTests++;
            System.out.println("✅ " + testName);
        } else {
            String failure = "❌ " + testName + " - Expected: " + Arrays.toString(expected)
                    + ", Actual: " + Arrays.toString(actual);
            System.out.println(failure);
            failures.add(failure);
        }
    }

    private static void assertArrayEqual(String testName, double[] expected, double[] actual) {
        totalTests++;
        if (Arrays.equals(expected, actual)) {
            passedTests++;
            System.out.println("✅ " + testName);
        } else {
            String failure = "❌ " + testName + " - Expected: " + Arrays.toString(expected)
                    + ", Actual: " + Arrays.toString(actual);
            System.out.println(failure);
            failures.add(failure);
        }
    }

    private static void assertArrayEqual(String testName, boolean[] expected, boolean[] actual) {
        totalTests++;
        if (Arrays.equals(expected, actual)) {
            passedTests++;
            System.out.println("✅ " + testName);
        } else {
            String failure = "❌ " + testName + " - Expected: " + Arrays.toString(expected)
                    + ", Actual: " + Arrays.toString(actual);
            System.out.println(failure);
            failures.add(failure);
        }
    }

    private static void assert2DArrayEqual(String testName, int[][] expected, int[][] actual) {
        totalTests++;
        if (Arrays.deepEquals(expected, actual)) {
            passedTests++;
            System.out.println("✅ " + testName);
        } else {
            String failure = "❌ " + testName + " - Expected: " + Arrays.deepToString(expected)
                    + ", Actual: " + Arrays.deepToString(actual);
            System.out.println(failure);
            failures.add(failure);
        }
    }

    private static void assert2DCharArrayEqual(String testName, char[][] expected,
            char[][] actual) {
        totalTests++;
        if (Arrays.deepEquals(expected, actual)) {
            passedTests++;
            System.out.println("✅ " + testName);
        } else {
            String failure = "❌ " + testName + " - Expected: " + Arrays.deepToString(expected)
                    + ", Actual: " + Arrays.deepToString(actual);
            System.out.println(failure);
            failures.add(failure);
        }
    }

    private static void addFailure(String message) {
        totalTests++;
        System.out.println("❌ " + message);
        failures.add(message);
    }

    private static void printTestSummary() {
        System.out.println("=".repeat(70));
        System.out.println("TEST SUMMARY");
        System.out.println("=".repeat(70));
        System.out.println("Total Tests: " + totalTests);
        System.out.println("Passed: " + passedTests);
        System.out.println("Failed: " + (totalTests - passedTests));
        System.out.println(
                "Success Rate: " + String.format("%.1f%%", (passedTests * 100.0 / totalTests)));

        if (!failures.isEmpty()) {
            System.out.println("\nFAILURES:");
            for (String failure : failures) {
                System.out.println("  " + failure);
            }
        }

        System.out.println("=".repeat(70));

        if (passedTests == totalTests) {
            System.out.println(
                    "🎉 All tests passed! The enhanced test framework is working correctly.");
        } else {
            System.out.println("⚠️  Some tests failed. Please review the failures above.");
        }
    }
}
