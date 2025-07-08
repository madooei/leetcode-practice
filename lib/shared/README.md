# LeetCode Test Framework Library

A comprehensive, modular testing framework designed specifically for LeetCode problems with support for a wide variety of data types and optional type hints for precise input interpretation.

## Overview

This library provides a robust testing infrastructure that can handle the complexity and diversity of LeetCode problems while maintaining backward compatibility with existing test cases. The framework has been enhanced to support both automatic type detection (via reflection) and explicit type hints for precise control over input interpretation.

### Key Features

- **Universal Type Support**: Handles all common LeetCode data types including primitives, arrays, collections, and custom structures
- **Optional Type Hints**: Explicit control over input interpretation when automatic detection isn't sufficient
- **Backward Compatibility**: All existing test cases continue to work without modification
- **Modular Architecture**: Clean separation of concerns with specialized components
- **Performance Profiling**: Optional execution time and memory usage analysis
- **Comprehensive Testing**: Full unit test suite with 100% test coverage

## Architecture

The framework consists of several specialized components:

```plaintext
lib/
├── shared/                      # Framework Components (package shared;)
│   ├── TestRunner.java          # Main orchestrator and entry point
│   ├── TestExecutor.java        # Core test execution engine
│   ├── TestDataModels.java      # Data structures for test cases and results
│   ├── TypeParser.java          # Universal type parsing utility (NEW)
│   ├── JsonProcessor.java       # Enhanced JSON parsing with Object support
│   ├── FileOperations.java      # File I/O operations
│   ├── TreeNode.java            # Binary tree data structure
│   ├── PerformanceAnalyzer.java # Performance measurement
│   ├── TestResultsManager.java  # Result collection and reporting
│   ├── OutputFormatter.java     # Result formatting utilities
│   └── README.md                # Framework documentation
└── tests/                       # Organized test suite
    ├── unit/                    # Unit tests (85+ comprehensive tests)
    ├── integration/             # End-to-end workflow tests
    ├── examples/                # Demonstrations and sample files
    ├── run-all-tests.sh         # Complete test runner script
    └── README.md                # Test suite documentation
```

## Quick Start

### Basic Usage (Legacy Format)

```java
import shared.*;

public static void main(String[] args) {
    String testFile = "tests.json";
    MySolution solution = new MySolution();
    String methodName = "solve";

    TestResultsManager results = TestRunner.runTests(testFile, solution, methodName);
    results.printSummary();
}
```

### Enhanced Usage (With Type Hints)

```java
import shared.*;

public static void main(String[] args) {
    String testFile = "tests.json";
    MySolution solution = new MySolution();
    String methodName = "solve";
    boolean profile = true; // Enable performance profiling

    TestResultsManager results = TestRunner.runTests(testFile, solution, methodName, profile);
    results.printSummary();
}
```

**Enhanced Test Case Format:**

```json
{
  "name": "Array Search",
  "input": ["[1,2,3,4,5]", "3"],
  "inputTypes": ["int[]", "int"],
  "expected": "2",
  "description": "Find index of target in sorted array"
}
```

## Test Case Formats

The framework supports both legacy and enhanced test case formats:

### Legacy Format (Backward Compatible)

```json
[
  {
    "name": "Simple Test",
    "input": "\"hello world\"",
    "expected": "\"hello world\"",
    "description": "Basic string test"
  },
  {
    "name": "Tree Traversal",
    "input": "[1,2,3,4,5]",
    "expected": "[1,2,4,5,3]",
    "description": "Preorder traversal of binary tree"
  }
]
```

### Enhanced Format (With Type Hints)

```json
[
  {
    "name": "Multi-Parameter Test",
    "input": ["[1,2,3,4,5]", "3", "true"],
    "inputTypes": ["int[]", "int", "boolean"],
    "expected": "2",
    "description": "Search with multiple parameters"
  },
  {
    "name": "Matrix Problem",
    "input": ["[[1,2],[3,4]]", "5"],
    "inputTypes": ["int[][]", "int"],
    "expected": "false",
    "description": "2D matrix search"
  }
]
```

## Supported Data Types

### Primitive Types

- `int`, `Integer`
- `long`, `Long`
- `double`, `Double`
- `boolean`, `Boolean`
- `char`, `Character`
- `String`

### Array Types

- `int[]`, `Integer[]`
- `String[]`
- `double[]`
- `char[]`
- `boolean[]`

### 2D Arrays (Matrices)

- `int[][]`
- `char[][]`
- `String[][]`

### Collections

- `List<Integer>`
- `List<String>`
- `List<Double>`
- `List<List<Integer>>`
- `List<List<String>>`

### LeetCode Structures

- `TreeNode` - Binary tree with array representation
- `ListNode` - Linked list (implementation pending)

## Type Hint System

The optional type hint system provides explicit control over input interpretation:

### When to Use Type Hints

1. **Ambiguous Inputs**: Same input string can represent different data types

   ```json
   // Without type hints: [1,2,3] could be TreeNode or int[]
   // With type hints: Explicitly specify "int[]" or "TreeNode"
   ```

2. **Complex Structures**: Multi-dimensional arrays or nested collections

   ```json
   {
     "input": ["[[1,2],[3,4]]"],
     "inputTypes": ["int[][]"]
   }
   ```

3. **Multiple Parameters**: When method takes multiple arguments

   ```json
   {
     "input": ["[1,2,3]", "2", "true"],
     "inputTypes": ["int[]", "int", "boolean"]
   }
   ```

### Type Hint Examples

```json
// Array problem
{
  "input": ["[1,2,3,4,5]"],
  "inputTypes": ["int[]"]
}

// Tree problem (same input, different interpretation)
{
  "input": ["[1,2,3,4,5]"],
  "inputTypes": ["TreeNode"]
}

// Matrix problem
{
  "input": ["[[\"a\",\"b\"],[\"c\",\"d\"]]"],
  "inputTypes": ["char[][]"]
}

// Multi-parameter problem
{
  "input": ["[1,2,3]", "2", "[4,5,6]"],
  "inputTypes": ["int[]", "int", "int[]"]
}

// List collections
{
  "input": ["[[1,2],[3,4],[5,6]]"],
  "inputTypes": ["List<List<Integer>>"]
}
```

## Component Details

### TypeParser

The `TypeParser` class is the heart of the enhanced framework, providing universal type parsing capabilities:

```java
// Parse with explicit type hint
Object result = TypeParser.parseParameter(paramType, inputValue, "int[]");

// Parse with automatic detection (legacy mode)
Object result = TypeParser.parseParameter(paramType, inputValue, null);
```

**Supported Parsing Examples:**

```java
// Primitives
int value = (int) TypeParser.parseParameter(int.class, "42", "int");
String text = (String) TypeParser.parseParameter(String.class, "\"hello\"", "String");

// Arrays
int[] array = (int[]) TypeParser.parseParameter(int[].class, "[1,2,3]", "int[]");
char[][] matrix = (char[][]) TypeParser.parseParameter(char[][].class, "[[\"a\",\"b\"],[\"c\",\"d\"]]", "char[][]");

// Collections
List<Integer> list = (List<Integer>) TypeParser.parseParameter(List.class, "[1,2,3]", "List<Integer>");

// LeetCode structures
TreeNode tree = (TreeNode) TypeParser.parseParameter(TreeNode.class, "[1,2,3]", "TreeNode");
```

### TestDataModels

Enhanced `TestCase` model supports both legacy and new formats:

```java
// Legacy constructor (backward compatible)
Map<String, Object> legacyData = Map.of(
    "name", "Test",
    "input", "42",
    "expected", "42"
);
TestCase testCase = new TestCase(legacyData);

// Enhanced constructor with type hints
Map<String, Object> enhancedData = Map.of(
    "name", "Enhanced Test",
    "input", Arrays.asList("[1,2,3]", "5"),
    "inputTypes", Arrays.asList("int[]", "int"),
    "expected", "true"
);
TestCase enhancedCase = new TestCase(enhancedData);

// Check for type hints
if (testCase.hasTypeHints()) {
    String[] types = testCase.inputTypes;
    // Use explicit types
}
```

### JsonProcessor

Enhanced JSON processor supports both string-only and Object-based parsing:

```java
// Legacy format (string values only)
List<Map<String, String>> legacyTests = JsonProcessor.parseTestCases(jsonContent);

// Enhanced format (preserves arrays/objects)
List<Map<String, Object>> enhancedTests = JsonProcessor.parseTestCasesRaw(jsonContent);
```

## Migration Guide

### For Existing Projects

**No changes required!** All existing test cases continue to work exactly as before:

```json
// This continues to work unchanged
[
  {
    "name": "Existing Test",
    "input": "[1,2,3]",
    "expected": "[1,2,4,5,3]",
    "description": "Tree preorder traversal"
  }
]
```

### To Use Enhanced Features

Simply add `inputTypes` to your test cases when you need explicit control:

```json
// Enhanced version with type hints
[
  {
    "name": "Array Problem",
    "input": ["[1,2,3]", "2"],
    "inputTypes": ["int[]", "int"],
    "expected": "1",
    "description": "Find target index in array"
  }
]
```

## Advanced Usage

### Custom Solution Classes

```java
public class TwoSumSolution {
    public int[] twoSum(int[] nums, int target) {
        // Implementation
        return new int[]{0, 1};
    }

    public static void main(String[] args) {
        TwoSumSolution solution = new TwoSumSolution();
        TestResultsManager results = TestRunner.runTests(
            "tests.json",
            solution,
            "twoSum",
            true  // Enable performance profiling
        );
        results.printSummary();
    }
}
```

### Performance Profiling

```java
// Enable performance profiling
TestResultsManager results = TestRunner.runTests(testFile, solution, methodName, true);

// Access performance data
for (TestResult result : results.getAllResults()) {
    if (result.hasPerformanceData()) {
        PerformanceResult perf = result.performance;
        System.out.println("Execution time: " + perf.getExecutionTimeMs() + "ms");
        System.out.println("Memory used: " + perf.getMemoryUsedMB() + "MB");
    }
}
```

### Error Handling

The framework provides comprehensive error handling:

```java
try {
    TestResultsManager results = TestRunner.runTests(testFile, solution, methodName);
    results.printSummary();
} catch (Exception e) {
    System.err.println("Test execution failed: " + e.getMessage());
    e.printStackTrace();
}
```

## Testing the Framework

Run the comprehensive test suite:

```bash
# Make sure to run this from the root directory
sh ./lib/tests/run-all-tests.sh
```

Or run individual test files in VSCode (or IntelliJ).

The test suite includes:

- **85+ unit tests** covering all components
- **Type parsing tests** for all supported data types
- **Backward compatibility verification**
- **End-to-end integration tests**
- **Performance and edge case testing**

## Best Practices

### 1. Use Type Hints When Needed

```json
// Good: Explicit when ambiguous
{
  "input": ["[1,2,3]", "2"],
  "inputTypes": ["int[]", "int"]
}

// Avoid: Unnecessary type hints
{
  "input": "\"hello\"",
  "inputTypes": ["String"]  // Not needed, String is unambiguous
}
```

### 2. Organize Test Cases

```json
// Good: Descriptive names and descriptions
{
  "name": "Empty Array Edge Case",
  "input": ["[]"],
  "inputTypes": ["int[]"],
  "expected": "-1",
  "description": "Should return -1 when array is empty"
}
```

### 3. Test Edge Cases

Include comprehensive test coverage:

- Empty inputs: `[]`, `""`, `null`
- Boundary values: minimum/maximum sizes
- Invalid inputs: malformed data
- Performance cases: large datasets

### 4. Use Consistent Formatting

```json
// Consistent indentation and structure
[
  {
    "name": "Test Case 1",
    "input": ["[1,2,3]"],
    "inputTypes": ["int[]"],
    "expected": "6",
    "description": "Sum of array elements"
  },
  {
    "name": "Test Case 2",
    "input": ["[4,5,6]"],
    "inputTypes": ["int[]"],
    "expected": "15",
    "description": "Sum of different array"
  }
]
```

## Troubleshooting

### Common Issues

1. **Type Hint Mismatch**

   ```
   Error: Type hint count mismatch: expected 2 but got 1 type hints
   ```

   Solution: Ensure `inputTypes` array length matches method parameter count

2. **Parsing Errors**

   ```
   Error: Failed to parse input '[1,2,3' as type 'int[]'
   ```

   Solution: Check JSON formatting, ensure proper brackets and quotes

3. **Method Not Found**
   ```
   Error: Method 'solve' not found in MySolution
   ```
   Solution: Verify method name and class have correct method signature

### Debug Tips

1. **Enable Verbose Output**: Use debug test files

   ```json
   // Create tests-debug.json with single test case
   [{ "name": "Debug", "input": "...", "expected": "..." }]
   ```

2. **Check Type Hints**: Use `testCase.hasTypeHints()` in debugging

3. **Validate JSON**: Use online JSON validators for complex test files

4. **Test Components Individually**: Use `TestFrameworkTests.java` to isolate issues

## Contributing

When extending the framework:

1. **Add New Types**: Extend `TypeParser.parseParameter()` with new type cases
2. **Update Tests**: Add comprehensive tests in `TestFrameworkTests.java`
3. **Maintain Compatibility**: Ensure all existing tests still pass
4. **Document Changes**: Update this README with new features

## Version History

- **v2.0** (Current): Enhanced framework with type hints, universal type support
- **v1.0**: Original framework with basic TreeNode and primitive type support

---

The enhanced test framework maintains the simplicity and effectiveness of the original design while adding powerful new capabilities for handling the full spectrum of LeetCode problems. All existing code continues to work unchanged while new projects can take advantage of the enhanced features for more precise and flexible testing.
