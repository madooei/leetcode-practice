package shared;

import java.util.*;
import java.util.stream.Collectors;

/**
 * TypeParser - Universal type parsing utility for LeetCode-style input data
 * 
 * This utility class provides comprehensive support for parsing various data types
 * commonly used in LeetCode problems, including:
 * - Primitive types (int, double, boolean, etc.)
 * - Arrays (int[], String[], char[][], etc.)
 * - Collections (List<Integer>, List<String>, etc.)
 * - LeetCode structures (TreeNode, ListNode, etc.)
 * - Complex nested structures
 * 
 * The parser supports both automatic type detection via reflection and explicit
 * type specification via type hints for precise control over input interpretation.
 */
public class TypeParser {

    /**
     * Parses a parameter value using explicit type hint or reflection-based detection
     * 
     * @param paramType The parameter type from method signature (for fallback)
     * @param inputValue The string value to parse
     * @param typeHint Optional explicit type hint (null for automatic detection)
     * @return The parsed parameter object
     * @throws RuntimeException if parameter type is unsupported or parsing fails
     */
    public static Object parseParameter(Class<?> paramType, String inputValue, String typeHint) {
        String effectiveType = typeHint != null ? typeHint : paramType.getSimpleName();

        // Handle null inputs
        if (inputValue == null || inputValue.trim().equals("null")) {
            return null;
        }

        inputValue = inputValue.trim();

        try {
            // Primitive and wrapper types
            switch (effectiveType) {
                case "int":
                case "Integer":
                    return parseInteger(inputValue);

                case "long":
                case "Long":
                    return parseLong(inputValue);

                case "double":
                case "Double":
                    return parseDouble(inputValue);

                case "boolean":
                case "Boolean":
                    return parseBoolean(inputValue);

                case "String":
                    return parseString(inputValue);

                case "char":
                case "Character":
                    return parseCharacter(inputValue);

                // Array types
                case "int[]":
                    return parseIntArray(inputValue);

                case "Integer[]":
                    return parseIntegerArray(inputValue);

                case "String[]":
                    return parseStringArray(inputValue);

                case "double[]":
                    return parseDoubleArray(inputValue);

                case "char[]":
                    return parseCharArray(inputValue);

                case "boolean[]":
                    return parseBooleanArray(inputValue);

                // 2D arrays (matrices)
                case "int[][]":
                    return parseInt2DArray(inputValue);

                case "char[][]":
                    return parseChar2DArray(inputValue);

                case "String[][]":
                    return parseString2DArray(inputValue);

                // Collections
                case "List<Integer>":
                    return parseIntegerList(inputValue);

                case "List<String>":
                    return parseStringList(inputValue);

                case "List<Double>":
                    return parseDoubleList(inputValue);

                case "List<List<Integer>>":
                    return parseIntegerListList(inputValue);

                case "List<List<String>>":
                    return parseStringListList(inputValue);

                // LeetCode structures
                case "TreeNode":
                    return parseTreeNode(inputValue);

                case "ListNode":
                    return parseListNode(inputValue);

                default:
                    // Fallback to original parameter type handling
                    return parseParameterFallback(paramType, inputValue);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse input '" + inputValue + "' as type '"
                    + effectiveType + "': " + e.getMessage(), e);
        }
    }

    // Primitive type parsers

    private static Integer parseInteger(String input) {
        return Integer.parseInt(parseStringValue(input));
    }

    private static Long parseLong(String input) {
        return Long.parseLong(parseStringValue(input));
    }

    private static Double parseDouble(String input) {
        return Double.parseDouble(parseStringValue(input));
    }

    private static Boolean parseBoolean(String input) {
        return Boolean.parseBoolean(parseStringValue(input).toLowerCase());
    }

    private static Character parseCharacter(String input) {
        String cleaned = parseStringValue(input);
        if (cleaned.length() != 1) {
            throw new IllegalArgumentException("Character input must be exactly one character");
        }
        return cleaned.charAt(0);
    }

    // Array parsers

    private static int[] parseIntArray(String input) {
        if (!input.startsWith("[") || !input.endsWith("]")) {
            throw new IllegalArgumentException("Array input must be in format [1,2,3]");
        }

        String content = input.substring(1, input.length() - 1).trim();
        if (content.isEmpty()) {
            return new int[0];
        }

        String[] parts = splitArrayElements(content);
        int[] result = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Integer.parseInt(parts[i].trim());
        }
        return result;
    }

    private static Integer[] parseIntegerArray(String input) {
        if (!input.startsWith("[") || !input.endsWith("]")) {
            throw new IllegalArgumentException("Array input must be in format [1,2,3]");
        }

        String content = input.substring(1, input.length() - 1).trim();
        if (content.isEmpty()) {
            return new Integer[0];
        }

        String[] parts = splitArrayElements(content);
        Integer[] result = new Integer[parts.length];
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();
            result[i] = part.equals("null") ? null : Integer.parseInt(part);
        }
        return result;
    }

    private static String[] parseStringArray(String input) {
        if (!input.startsWith("[") || !input.endsWith("]")) {
            throw new IllegalArgumentException("Array input must be in format [\"a\",\"b\",\"c\"]");
        }

        String content = input.substring(1, input.length() - 1).trim();
        if (content.isEmpty()) {
            return new String[0];
        }

        String[] parts = splitArrayElements(content);
        String[] result = new String[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = parseStringValue(parts[i].trim());
        }
        return result;
    }

    private static double[] parseDoubleArray(String input) {
        if (!input.startsWith("[") || !input.endsWith("]")) {
            throw new IllegalArgumentException("Array input must be in format [1.0,2.5,3.14]");
        }

        String content = input.substring(1, input.length() - 1).trim();
        if (content.isEmpty()) {
            return new double[0];
        }

        String[] parts = splitArrayElements(content);
        double[] result = new double[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Double.parseDouble(parts[i].trim());
        }
        return result;
    }

    private static char[] parseCharArray(String input) {
        if (!input.startsWith("[") || !input.endsWith("]")) {
            throw new IllegalArgumentException("Array input must be in format [\"a\",\"b\",\"c\"]");
        }

        String content = input.substring(1, input.length() - 1).trim();
        if (content.isEmpty()) {
            return new char[0];
        }

        String[] parts = splitArrayElements(content);
        char[] result = new char[parts.length];
        for (int i = 0; i < parts.length; i++) {
            String charStr = parseStringValue(parts[i].trim());
            if (charStr.length() != 1) {
                throw new IllegalArgumentException("Each character must be exactly one character");
            }
            result[i] = charStr.charAt(0);
        }
        return result;
    }

    private static boolean[] parseBooleanArray(String input) {
        if (!input.startsWith("[") || !input.endsWith("]")) {
            throw new IllegalArgumentException("Array input must be in format [true,false,true]");
        }

        String content = input.substring(1, input.length() - 1).trim();
        if (content.isEmpty()) {
            return new boolean[0];
        }

        String[] parts = splitArrayElements(content);
        boolean[] result = new boolean[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Boolean.parseBoolean(parts[i].trim().toLowerCase());
        }
        return result;
    }

    // 2D array parsers

    private static int[][] parseInt2DArray(String input) {
        if (!input.startsWith("[[") || !input.endsWith("]]")) {
            throw new IllegalArgumentException("2D array input must be in format [[1,2],[3,4]]");
        }

        String content = input.substring(1, input.length() - 1).trim();
        if (content.isEmpty()) {
            return new int[0][];
        }

        List<String> rows = splitNestedArrays(content);
        int[][] result = new int[rows.size()][];
        for (int i = 0; i < rows.size(); i++) {
            result[i] = parseIntArray(rows.get(i));
        }
        return result;
    }

    private static char[][] parseChar2DArray(String input) {
        if (!input.startsWith("[[") || !input.endsWith("]]")) {
            throw new IllegalArgumentException(
                    "2D array input must be in format [[\"a\",\"b\"],[\"c\",\"d\"]]");
        }

        String content = input.substring(1, input.length() - 1).trim();
        if (content.isEmpty()) {
            return new char[0][];
        }

        List<String> rows = splitNestedArrays(content);
        char[][] result = new char[rows.size()][];
        for (int i = 0; i < rows.size(); i++) {
            result[i] = parseCharArray(rows.get(i));
        }
        return result;
    }

    private static String[][] parseString2DArray(String input) {
        if (!input.startsWith("[[") || !input.endsWith("]]")) {
            throw new IllegalArgumentException(
                    "2D array input must be in format [[\"a\",\"b\"],[\"c\",\"d\"]]");
        }

        String content = input.substring(1, input.length() - 1).trim();
        if (content.isEmpty()) {
            return new String[0][];
        }

        List<String> rows = splitNestedArrays(content);
        String[][] result = new String[rows.size()][];
        for (int i = 0; i < rows.size(); i++) {
            result[i] = parseStringArray(rows.get(i));
        }
        return result;
    }

    // List parsers

    private static List<Integer> parseIntegerList(String input) {
        Integer[] array = parseIntegerArray(input);
        return new ArrayList<>(Arrays.asList(array));
    }

    private static List<String> parseStringList(String input) {
        String[] array = parseStringArray(input);
        return new ArrayList<>(Arrays.asList(array));
    }

    private static List<Double> parseDoubleList(String input) {
        double[] array = parseDoubleArray(input);
        return Arrays.stream(array).boxed().collect(Collectors.toList());
    }

    private static List<List<Integer>> parseIntegerListList(String input) {
        int[][] array = parseInt2DArray(input);
        List<List<Integer>> result = new ArrayList<>();
        for (int[] row : array) {
            result.add(Arrays.stream(row).boxed().collect(Collectors.toList()));
        }
        return result;
    }

    private static List<List<String>> parseStringListList(String input) {
        String[][] array = parseString2DArray(input);
        List<List<String>> result = new ArrayList<>();
        for (String[] row : array) {
            result.add(new ArrayList<>(Arrays.asList(row)));
        }
        return result;
    }

    // LeetCode structure parsers

    private static TreeNode parseTreeNode(String input) {
        String cleanInput = parseStringValue(input);
        Integer[] array = TreeNode.parseArray(cleanInput);
        return TreeNode.fromArray(array);
    }

    private static Object parseListNode(String input) {
        // TODO: Implement ListNode parsing when ListNode class is available
        throw new RuntimeException("ListNode parsing not yet implemented");
    }

    // Utility methods

    /**
     * Parses string values with proper null and quote handling
     */
    private static String parseString(String input) {
        return parseStringValue(input);
    }

    private static String parseStringValue(String input) {
        if (input == null)
            return null;
        input = input.trim();

        // Handle null input
        if (input.equals("null")) {
            return null;
        }

        // Remove surrounding quotes for string inputs
        while (input.startsWith("\"") && input.endsWith("\"") && input.length() >= 2) {
            String unquoted = input.substring(1, input.length() - 1);

            // Handle escaped quotes
            if (unquoted.startsWith("\\\"") && unquoted.endsWith("\\\"")) {
                unquoted = unquoted.substring(2, unquoted.length() - 2);
            }

            input = unquoted;

            // Prevent infinite loop
            if (input.equals(unquoted)) {
                break;
            }
        }

        // Handle remaining escape sequences
        input = input.replace("\\\"", "\"").replace("\\\\", "\\");

        return input;
    }

    /**
     * Splits array elements while respecting nested structures
     */
    private static String[] splitArrayElements(String content) {
        List<String> elements = new ArrayList<>();
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
                    elements.add(content.substring(start, i).trim());
                    start = i + 1;
                }
            }
        }

        // Add the last element
        if (start < content.length()) {
            elements.add(content.substring(start).trim());
        }

        return elements.toArray(new String[0]);
    }

    /**
     * Splits nested arrays for 2D array parsing
     */
    private static List<String> splitNestedArrays(String content) {
        List<String> arrays = new ArrayList<>();
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
                if (c == '[') {
                    if (depth == 0) {
                        start = i; // Start of new array
                    }
                    depth++;
                } else if (c == ']') {
                    depth--;
                    if (depth == 0) {
                        // End of array
                        arrays.add(content.substring(start, i + 1));
                    }
                }
            }
        }

        return arrays;
    }

    /**
     * Fallback to original parameter type handling for backward compatibility
     */
    private static Object parseParameterFallback(Class<?> paramType, String inputValue) {
        // Handle TreeNode parameter for binary tree problems
        if (paramType.getSimpleName().equals("TreeNode")) {
            String cleanInput = parseStringValue(inputValue);
            Integer[] array = TreeNode.parseArray(cleanInput);
            return TreeNode.fromArray(array);
        }

        // Handle integer parameters
        if (paramType == int.class || paramType == Integer.class) {
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
}
