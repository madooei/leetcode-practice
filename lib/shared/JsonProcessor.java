package shared;

import java.util.*;

/**
 * JsonProcessor - Utility class for parsing JSON test case files
 * 
 * This class provides a lightweight JSON parser specifically designed for parsing
 * test case files without external dependencies. It handles basic JSON parsing
 * operations including object splitting, key-value extraction, and string unquoting.
 * 
 * Key Features:
 * - Parse JSON arrays of test case objects
 * - Handle nested JSON structures
 * - Escape sequence processing
 * - Error handling for malformed JSON
 */
public class JsonProcessor {

    /**
     * Parses a JSON string containing an array of test case objects
     * 
     * Enhanced to support both legacy format (string values only) and new format
     * (with array/object values for enhanced input handling).
     * 
     * @param jsonContent The JSON string to parse (must be a JSON array)
     * @return List of maps representing parsed test case objects
     * @throws RuntimeException if JSON format is invalid
     */
    public static List<Map<String, String>> parseTestCases(String jsonContent) {
        List<Map<String, Object>> rawTestCases = parseTestCasesRaw(jsonContent);
        List<Map<String, String>> testCases = new ArrayList<>();

        // Convert to legacy format for backward compatibility
        for (Map<String, Object> rawCase : rawTestCases) {
            Map<String, String> legacyCase = new HashMap<>();
            for (Map.Entry<String, Object> entry : rawCase.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value instanceof String) {
                    legacyCase.put(key, (String) value);
                } else {
                    // Convert non-string values to JSON representation
                    legacyCase.put(key, objectToJsonString(value));
                }
            }
            testCases.add(legacyCase);
        }

        return testCases;
    }

    /**
     * Parses test cases into raw Object format (supports new enhanced format)
     * 
     * @param jsonContent The JSON string to parse
     * @return List of maps with Object values (preserves arrays and objects)
     */
    public static List<Map<String, Object>> parseTestCasesRaw(String jsonContent) {
        List<Map<String, Object>> testCases = new ArrayList<>();

        // Remove whitespace and validate outer brackets
        jsonContent = jsonContent.trim();
        if (!jsonContent.startsWith("[") || !jsonContent.endsWith("]")) {
            throw new RuntimeException("Invalid JSON format - must be an array");
        }

        // Extract content between brackets
        String content = jsonContent.substring(1, jsonContent.length() - 1).trim();
        if (content.isEmpty()) {
            return testCases; // Return empty list for empty array
        }

        // Split JSON content into individual objects
        List<String> objects = splitJsonObjects(content);

        // Parse each object and add to results
        for (String obj : objects) {
            Map<String, Object> testCase = parseJsonObjectRaw(obj);
            testCases.add(testCase);
        }

        return testCases;
    }

    /**
     * Splits JSON content into individual object strings
     * 
     * This method handles nested braces correctly by tracking depth.
     * It identifies object boundaries and separates them properly.
     * 
     * @param content The JSON content to split (without outer array brackets)
     * @return List of individual JSON object strings
     */
    private static List<String> splitJsonObjects(String content) {
        List<String> objects = new ArrayList<>();
        int depth = 0;
        int start = 0;

        // Traverse content character by character
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);

            if (c == '{') {
                depth++; // Entering nested object
            } else if (c == '}') {
                depth--; // Exiting nested object

                // Found complete object at depth 0
                if (depth == 0) {
                    objects.add(content.substring(start, i + 1).trim());

                    // Skip comma and whitespace after object
                    while (i + 1 < content.length() && (content.charAt(i + 1) == ','
                            || Character.isWhitespace(content.charAt(i + 1)))) {
                        i++;
                    }
                    start = i + 1; // Start of next object
                }
            }
        }

        return objects;
    }

    /**
     * Unescapes a JSON string value
     * 
     * This method handles standard JSON escape sequences:
     * - \" for double quotes
     * - \\ for backslash
     * - \/ for forward slash
     * - \b for backspace
     * - \f for form feed
     * - \n for newline
     * - \r for carriage return
     * - \t for tab
     * 
     * @param value The JSON string value to unescape
     * @return The unescaped string
     */
    private static String unescapeJsonString(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        // Remove surrounding quotes if present
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '\\' && i + 1 < value.length()) {
                char next = value.charAt(i + 1);
                switch (next) {
                    case '"':
                        result.append('"');
                        i++;
                        break;
                    case '\\':
                        result.append('\\');
                        i++;
                        break;
                    case '/':
                        result.append('/');
                        i++;
                        break;
                    case 'b':
                        result.append('\b');
                        i++;
                        break;
                    case 'f':
                        result.append('\f');
                        i++;
                        break;
                    case 'n':
                        result.append('\n');
                        i++;
                        break;
                    case 'r':
                        result.append('\r');
                        i++;
                        break;
                    case 't':
                        result.append('\t');
                        i++;
                        break;
                    case 'u':
                        if (i + 5 < value.length()) {
                            String hex = value.substring(i + 2, i + 6);
                            try {
                                result.append((char) Integer.parseInt(hex, 16));
                                i += 5;
                            } catch (NumberFormatException e) {
                                result.append(c);
                            }
                        } else {
                            result.append(c);
                        }
                        break;
                    default:
                        result.append(c);
                }
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * Parses a single JSON object string into a map of key-value pairs
     * 
     * @param jsonObject The JSON object string to parse
     * @return Map containing the parsed key-value pairs
     * @throws RuntimeException if JSON format is invalid
     */
    private static Map<String, String> parseJsonObject(String jsonObject) {
        Map<String, String> result = new HashMap<>();

        // Remove outer braces and trim
        jsonObject = jsonObject.trim();
        if (!jsonObject.startsWith("{") || !jsonObject.endsWith("}")) {
            throw new RuntimeException("Invalid JSON object format");
        }
        jsonObject = jsonObject.substring(1, jsonObject.length() - 1).trim();

        if (jsonObject.isEmpty()) {
            return result;
        }

        // Split into key-value pairs
        List<String> pairs = splitJsonPairs(jsonObject);

        // Parse each pair
        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2);
            if (keyValue.length != 2) {
                throw new RuntimeException("Invalid key-value pair format: " + pair);
            }

            String key = keyValue[0].trim();
            String value = keyValue[1].trim();

            // Remove quotes from key
            if (key.startsWith("\"") && key.endsWith("\"")) {
                key = key.substring(1, key.length() - 1);
            }

            // For array or object values, don't unescape - preserve as-is
            if (value.startsWith("[") || value.startsWith("{")) {
                // Keep arrays and objects as raw JSON strings
            } else {
                // Only unescape string values
                value = unescapeJsonString(value);
            }

            result.put(key, value);
        }

        return result;
    }

    /**
     * Splits JSON object content into individual key-value pair strings
     * 
     * This method respects quoted strings, arrays, and objects and doesn't split 
     * on commas within them. It properly handles escaped quotes and maintains 
     * structural integrity.
     * 
     * @param content The JSON object content (without outer braces)
     * @return List of key-value pair strings
     */
    private static List<String> splitJsonPairs(String content) {
        List<String> pairs = new ArrayList<>();
        boolean inQuotes = false;
        int bracketDepth = 0;
        int braceDepth = 0;
        int start = 0;

        // Process character by character
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);

            // Handle escape sequences
            if (c == '\\' && i + 1 < content.length()) {
                i++; // Skip the next character
                continue;
            }

            // Toggle quote state
            if (c == '"') {
                inQuotes = !inQuotes;
            }
            // Track array and object depth when not in quotes
            else if (!inQuotes) {
                if (c == '[') {
                    bracketDepth++;
                } else if (c == ']') {
                    bracketDepth--;
                } else if (c == '{') {
                    braceDepth++;
                } else if (c == '}') {
                    braceDepth--;
                }
                // Split on comma only when at top level
                else if (c == ',' && bracketDepth == 0 && braceDepth == 0) {
                    pairs.add(content.substring(start, i).trim());
                    start = i + 1;
                }
            }
        }

        // Add the final pair
        if (start < content.length()) {
            pairs.add(content.substring(start).trim());
        }

        return pairs;
    }

    /**
     * Parses a single JSON object string into a map with Object values
     * 
     * This enhanced version preserves arrays and objects as List and Map objects
     * rather than converting everything to strings.
     * 
     * @param jsonObject The JSON object string to parse
     * @return Map containing the parsed key-value pairs with proper types
     * @throws RuntimeException if JSON format is invalid
     */
    private static Map<String, Object> parseJsonObjectRaw(String jsonObject) {
        Map<String, Object> result = new HashMap<>();

        // Remove outer braces and trim
        jsonObject = jsonObject.trim();
        if (!jsonObject.startsWith("{") || !jsonObject.endsWith("}")) {
            throw new RuntimeException("Invalid JSON object format");
        }
        jsonObject = jsonObject.substring(1, jsonObject.length() - 1).trim();

        if (jsonObject.isEmpty()) {
            return result;
        }

        // Split into key-value pairs
        List<String> pairs = splitJsonPairs(jsonObject);

        // Parse each pair
        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2);
            if (keyValue.length != 2) {
                throw new RuntimeException("Invalid key-value pair format: " + pair);
            }

            String key = keyValue[0].trim();
            String valueStr = keyValue[1].trim();

            // Remove quotes from key
            if (key.startsWith("\"") && key.endsWith("\"")) {
                key = key.substring(1, key.length() - 1);
            }

            // Parse value based on its type
            Object value = parseJsonValue(valueStr);
            result.put(key, value);
        }

        return result;
    }

    /**
     * Parses a JSON value and returns the appropriate Java object
     * 
     * @param valueStr The JSON value string to parse
     * @return Parsed object (String, List, Map, Boolean, Number, or null)
     */
    private static Object parseJsonValue(String valueStr) {
        valueStr = valueStr.trim();

        // Handle null
        if (valueStr.equals("null")) {
            return null;
        }

        // Handle arrays
        if (valueStr.startsWith("[") && valueStr.endsWith("]")) {
            return parseJsonArray(valueStr);
        }

        // Handle objects
        if (valueStr.startsWith("{") && valueStr.endsWith("}")) {
            return parseJsonObjectRaw(valueStr);
        }

        // Handle booleans
        if (valueStr.equals("true")) {
            return true;
        }
        if (valueStr.equals("false")) {
            return false;
        }

        // Handle numbers
        try {
            if (valueStr.contains(".")) {
                return Double.parseDouble(valueStr);
            } else {
                return Long.parseLong(valueStr);
            }
        } catch (NumberFormatException e) {
            // Not a number, treat as string
        }

        // Handle strings (remove quotes and unescape)
        return unescapeJsonString(valueStr);
    }

    /**
     * Parses a JSON array into a List
     * 
     * @param arrayStr The JSON array string
     * @return List containing parsed elements
     */
    private static List<Object> parseJsonArray(String arrayStr) {
        List<Object> result = new ArrayList<>();

        // Remove outer brackets
        arrayStr = arrayStr.substring(1, arrayStr.length() - 1).trim();
        if (arrayStr.isEmpty()) {
            return result;
        }

        // Split array elements
        List<String> elements = splitArrayElements(arrayStr);

        // Parse each element
        for (String element : elements) {
            result.add(parseJsonValue(element.trim()));
        }

        return result;
    }

    /**
     * Splits array elements while respecting nested structures
     * 
     * @param content The array content (without outer brackets)
     * @return List of element strings
     */
    private static List<String> splitArrayElements(String content) {
        List<String> elements = new ArrayList<>();
        boolean inQuotes = false;
        int bracketDepth = 0;
        int braceDepth = 0;
        int start = 0;

        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);

            // Handle escape sequences
            if (c == '\\' && i + 1 < content.length()) {
                i++; // Skip the next character
                continue;
            }

            // Toggle quote state
            if (c == '"') {
                inQuotes = !inQuotes;
            }
            // Track depth when not in quotes
            else if (!inQuotes) {
                if (c == '[') {
                    bracketDepth++;
                } else if (c == ']') {
                    bracketDepth--;
                } else if (c == '{') {
                    braceDepth++;
                } else if (c == '}') {
                    braceDepth--;
                }
                // Split on comma only at top level
                else if (c == ',' && bracketDepth == 0 && braceDepth == 0) {
                    elements.add(content.substring(start, i).trim());
                    start = i + 1;
                }
            }
        }

        // Add the final element
        if (start < content.length()) {
            elements.add(content.substring(start).trim());
        }

        return elements;
    }

    /**
     * Converts an Object to JSON string representation
     * 
     * @param obj The object to convert
     * @return JSON string representation
     */
    private static String objectToJsonString(Object obj) {
        if (obj == null) {
            return "null";
        }

        if (obj instanceof String) {
            return "\"" + escapeJsonString((String) obj) + "\"";
        }

        if (obj instanceof Boolean || obj instanceof Number) {
            return obj.toString();
        }

        if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                if (i > 0)
                    sb.append(", ");
                sb.append(objectToJsonString(list.get(i)));
            }
            sb.append("]");
            return sb.toString();
        }

        if (obj instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) obj;
            StringBuilder sb = new StringBuilder("{");
            boolean first = true;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (!first)
                    sb.append(", ");
                first = false;
                sb.append("\"").append(entry.getKey()).append("\": ");
                sb.append(objectToJsonString(entry.getValue()));
            }
            sb.append("}");
            return sb.toString();
        }

        // Fallback: convert to string and quote
        return "\"" + escapeJsonString(obj.toString()) + "\"";
    }

    /**
     * Escapes a string for JSON representation
     * 
     * @param str The string to escape
     * @return Escaped string
     */
    private static String escapeJsonString(String str) {
        if (str == null)
            return "";

        return str.replace("\\", "\\\\").replace("\"", "\\\"").replace("\b", "\\b")
                .replace("\f", "\\f").replace("\n", "\\n").replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
