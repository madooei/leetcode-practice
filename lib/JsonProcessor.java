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
     * @param jsonContent The JSON string to parse (must be a JSON array)
     * @return List of maps representing parsed test case objects
     * @throws RuntimeException if JSON format is invalid
     */
    public static List<Map<String, String>> parseTestCases(String jsonContent) {
        List<Map<String, String>> testCases = new ArrayList<>();

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
            Map<String, String> testCase = parseJsonObject(obj);
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
     * Parses a single JSON object string into a key-value map
     * 
     * @param obj The JSON object string to parse
     * @return Map containing key-value pairs from the JSON object
     */
    private static Map<String, String> parseJsonObject(String obj) {
        Map<String, String> result = new HashMap<>();

        // Remove outer braces from object
        obj = obj.trim();
        if (obj.startsWith("{"))
            obj = obj.substring(1);
        if (obj.endsWith("}"))
            obj = obj.substring(0, obj.length() - 1);

        // Split object into key-value pairs
        List<String> pairs = splitJsonPairs(obj);

        // Process each key-value pair
        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2); // Split on first colon only
            if (keyValue.length == 2) {
                String key = unquote(keyValue[0].trim());
                String value = unquote(keyValue[1].trim());
                result.put(key, value);
            }
        }

        return result;
    }

    /**
     * Splits JSON object content into individual key-value pair strings
     * 
     * This method respects quoted strings and doesn't split on commas within quotes.
     * It properly handles escaped quotes and maintains string integrity.
     * 
     * @param content The JSON object content (without outer braces)
     * @return List of key-value pair strings
     */
    private static List<String> splitJsonPairs(String content) {
        List<String> pairs = new ArrayList<>();
        boolean inQuotes = false;
        int start = 0;

        // Process character by character
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);

            // Toggle quote state (handle escaped quotes)
            if (c == '"' && (i == 0 || content.charAt(i - 1) != '\\')) {
                inQuotes = !inQuotes;
            }
            // Split on comma only when not inside quotes
            else if (c == ',' && !inQuotes) {
                pairs.add(content.substring(start, i).trim());
                start = i + 1;
            }
        }

        // Add the final pair
        if (start < content.length()) {
            pairs.add(content.substring(start).trim());
        }

        return pairs;
    }

    /**
     * Removes surrounding quotes from a string if present
     * 
     * @param str The string to unquote
     * @return The string with surrounding quotes removed, or original if no quotes
     */
    private static String unquote(String str) {
        str = str.trim();
        if (str.startsWith("\"") && str.endsWith("\"")) {
            return str.substring(1, str.length() - 1);
        }
        return str;
    }
}
