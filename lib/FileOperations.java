import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * FileOperations - Utility class for file I/O operations
 * 
 * This class provides centralized file handling capabilities for the test framework:
 * - Reading test case files from JSON format
 * - File path validation and existence checking
 * - Error handling for I/O operations
 * - Integration with JsonProcessor for parsing
 * 
 * The class abstracts file system operations and provides a clean interface
 * for loading test data while handling common I/O exceptions gracefully.
 */
public class FileOperations {

    /**
     * Loads test cases from a JSON file
     * 
     * This method handles the complete process of loading and parsing test cases:
     * 1. Validates that the file exists and is readable
     * 2. Reads the entire file content as a string
     * 3. Parses the JSON content using JsonProcessor
     * 4. Converts raw data into TestCase objects
     * 5. Provides meaningful error messages for common issues
     * 
     * @param filename Path to the JSON file containing test cases
     * @return List of parsed TestCase objects
     * @throws IOException if file cannot be read or parsed
     * @throws IllegalArgumentException if filename is null or empty
     */
    public static List<TestDataModels.TestCase> loadTestCases(String filename) throws IOException {
        // Validate input parameters
        validateFilename(filename);

        // Verify file exists and is readable
        Path filePath = Paths.get(filename);
        validateFileAccess(filePath);

        try {
            // Read the entire file content
            String content = Files.readString(filePath);

            // Parse JSON content into raw maps
            List<Map<String, String>> rawCases = JsonProcessor.parseTestCases(content);

            // Convert raw data to TestCase objects
            List<TestDataModels.TestCase> testCases = new ArrayList<>();
            for (Map<String, String> rawCase : rawCases) {
                testCases.add(new TestDataModels.TestCase(rawCase));
            }

            return testCases;

        } catch (IOException e) {
            throw new IOException("Failed to read test file '" + filename + "': " + e.getMessage(),
                    e);
        } catch (RuntimeException e) {
            throw new IOException("Failed to parse test file '" + filename + "': " + e.getMessage(),
                    e);
        }
    }

    /**
     * Checks if a file exists and is accessible
     * 
     * This utility method performs basic file system checks to determine
     * if a file can be read. It's useful for validation before attempting
     * file operations.
     * 
     * @param filename Path to the file to check
     * @return true if the file exists and is readable, false otherwise
     */
    public static boolean isFileAccessible(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return false;
        }

        try {
            Path filePath = Paths.get(filename);
            return Files.exists(filePath) && Files.isReadable(filePath)
                    && Files.isRegularFile(filePath);
        } catch (Exception e) {
            // Any exception means the file is not accessible
            return false;
        }
    }

    /**
     * Gets the file size in bytes
     * 
     * @param filename Path to the file
     * @return File size in bytes, or -1 if file cannot be accessed
     */
    public static long getFileSize(String filename) {
        if (!isFileAccessible(filename)) {
            return -1;
        }

        try {
            return Files.size(Paths.get(filename));
        } catch (IOException e) {
            return -1;
        }
    }

    /**
     * Gets the absolute path of a file
     * 
     * This method resolves relative paths to absolute paths, which can be
     * useful for error reporting and debugging.
     * 
     * @param filename The filename to resolve
     * @return Absolute path as a string, or null if path cannot be resolved
     */
    public static String getAbsolutePath(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return null;
        }

        try {
            return Paths.get(filename).toAbsolutePath().toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Creates a backup of a file before overwriting
     * 
     * This utility method creates a backup copy with a timestamp suffix
     * before allowing the original file to be overwritten.
     * 
     * @param filename Path to the file to backup
     * @return Path to the backup file, or null if backup failed
     */
    public static String createBackup(String filename) {
        if (!isFileAccessible(filename)) {
            return null;
        }

        try {
            Path originalPath = Paths.get(filename);
            String backupName = generateBackupFilename(filename);
            Path backupPath = Paths.get(backupName);

            Files.copy(originalPath, backupPath, StandardCopyOption.REPLACE_EXISTING);
            return backupName;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Writes content to a file with error handling
     * 
     * This method provides a safe way to write content to files with
     * proper error handling and optional backup creation.
     * 
     * @param filename Path to the file to write
     * @param content Content to write to the file
     * @param createBackup Whether to create a backup before writing
     * @throws IOException if the write operation fails
     */
    public static void writeToFile(String filename, String content, boolean createBackup)
            throws IOException {
        validateFilename(filename);

        Path filePath = Paths.get(filename);

        // Create backup if requested and file exists
        if (createBackup && Files.exists(filePath)) {
            String backupPath = createBackup(filename);
            if (backupPath == null) {
                throw new IOException("Failed to create backup of existing file: " + filename);
            }
        }

        try {
            // Ensure parent directories exist
            Path parentDir = filePath.getParent();
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }

            // Write content to file
            Files.writeString(filePath, content, StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Failed to write to file '" + filename + "': " + e.getMessage(),
                    e);
        }
    }

    /**
     * Writes content to a file without backup
     * 
     * Convenience method for writing files without backup creation.
     * 
     * @param filename Path to the file to write
     * @param content Content to write to the file
     * @throws IOException if the write operation fails
     */
    public static void writeToFile(String filename, String content) throws IOException {
        writeToFile(filename, content, false);
    }

    /**
     * Validates that a filename is not null or empty
     * 
     * @param filename The filename to validate
     * @throws IllegalArgumentException if filename is null or empty
     */
    private static void validateFilename(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        }
        if (filename.trim().isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be empty");
        }
    }

    /**
     * Validates that a file exists and is accessible for reading
     * 
     * @param filePath The path to validate
     * @throws IOException if file is not accessible
     */
    private static void validateFileAccess(Path filePath) throws IOException {
        if (!Files.exists(filePath)) {
            throw new IOException("Test file not found: " + filePath.toAbsolutePath());
        }

        if (!Files.isRegularFile(filePath)) {
            throw new IOException("Path is not a regular file: " + filePath.toAbsolutePath());
        }

        if (!Files.isReadable(filePath)) {
            throw new IOException("Test file is not readable: " + filePath.toAbsolutePath());
        }
    }

    /**
     * Generates a backup filename with timestamp
     * 
     * @param originalFilename The original filename
     * @return Backup filename with timestamp suffix
     */
    private static String generateBackupFilename(String originalFilename) {
        String timestamp = String.valueOf(System.currentTimeMillis());

        int lastDotIndex = originalFilename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            String nameWithoutExt = originalFilename.substring(0, lastDotIndex);
            String extension = originalFilename.substring(lastDotIndex);
            return nameWithoutExt + ".backup." + timestamp + extension;
        } else {
            return originalFilename + ".backup." + timestamp;
        }
    }

    /**
     * Gets detailed file information for debugging
     * 
     * @param filename Path to the file
     * @return Formatted string with file information, or error message
     */
    public static String getFileInfo(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return "Invalid filename";
        }

        try {
            Path filePath = Paths.get(filename);

            if (!Files.exists(filePath)) {
                return "File does not exist: " + filePath.toAbsolutePath();
            }

            StringBuilder info = new StringBuilder();
            info.append("File: ").append(filePath.toAbsolutePath()).append("\n");
            info.append("Size: ").append(Files.size(filePath)).append(" bytes\n");
            info.append("Readable: ").append(Files.isReadable(filePath)).append("\n");
            info.append("Writable: ").append(Files.isWritable(filePath)).append("\n");
            info.append("Regular file: ").append(Files.isRegularFile(filePath)).append("\n");

            return info.toString();
        } catch (IOException e) {
            return "Error accessing file: " + e.getMessage();
        }
    }
}
