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
}
