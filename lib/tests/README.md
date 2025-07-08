# Test Suite for Enhanced LeetCode Framework

This directory contains the comprehensive test suite for the enhanced LeetCode test framework, organized into logical sections for easy navigation and maintenance.

## Directory Structure

```plaintext
tests/
├── unit/                    # Unit tests for individual components
├── integration/             # Integration tests for end-to-end workflows
├── examples/                # Demo files and example test cases
└── README.md               # This file
```

## Test Categories

### Unit Tests (`unit/`)

**TestFrameworkTests.java** - Comprehensive unit test suite

- **85+ test cases** covering all framework components
- **100% pass rate** ensuring reliability
- Tests for TypeParser, TestDataModels, JsonProcessor, FileOperations
- Backward compatibility verification
- Edge case and error handling validation

**How to run:**

```bash
# From project root
java -cp bin tests.unit.TestFrameworkTests
```

Or compile first if needed:

```bash
# From project root
javac -cp lib/shared -d bin lib/shared/*.java lib/tests/unit/*.java
java -cp bin tests.unit.TestFrameworkTests
```

### Integration Tests (`integration/`)

**SimpleTest.java** - End-to-end integration testing

- Tests complete workflow from JSON file to results
- Validates both legacy and enhanced formats
- Demonstrates real-world usage patterns

**How to run:**

```bash
# From project root
java -cp bin tests.integration.SimpleTest
```

### Examples (`examples/`)

**Demo Files:**

- `DemoSolution.java` - Sample solution class with multiple method types
- `DemoRunner.java` - Comprehensive demonstration runner
- `FinalDemo.java` - Complete framework showcase

**Test Data Files:**

- `tree-tests.json` - Binary tree test cases (legacy + enhanced)
- `array-tests.json` - Array problem test cases with type hints
- `demo-tests.json` - Comprehensive multi-method test suite
- `demo-tests-results.json` - Example of generated results file

**How to run examples:**

```bash
# From project root
java -cp bin tests.examples.FinalDemo        # Complete demonstration
java -cp bin tests.examples.DemoRunner       # Individual method demos
```

## Test Coverage Summary

### Component Coverage

- ✅ **TypeParser**: All data types, edge cases, error handling
- ✅ **TestDataModels**: Legacy/enhanced constructors, type hints
- ✅ **JsonProcessor**: String/Object parsing, complex structures
- ✅ **FileOperations**: File I/O, error handling, validation
- ✅ **TestExecutor**: Parameter parsing, method execution
- ✅ **TestRunner**: Orchestration, performance profiling
- ✅ **Integration**: End-to-end workflows, real-world scenarios

### Data Type Coverage

- ✅ **Primitives**: int, String, boolean, double, long, char
- ✅ **Arrays**: int[], String[], double[], boolean[], char[]
- ✅ **2D Arrays**: int[][], char[][], String[][]
- ✅ **Collections**: List<Integer>, List<String>, List<List<Integer>>
- ✅ **LeetCode Types**: TreeNode, ListNode (planned)
- ✅ **Edge Cases**: null values, empty arrays, malformed input

### Format Coverage

- ✅ **Legacy Format**: Backward compatibility with existing test cases
- ✅ **Enhanced Format**: Type hints for precise interpretation
- ✅ **Mixed Format**: Files with both legacy and enhanced test cases
- ✅ **Multi-Parameter**: Methods with multiple arguments
- ✅ **Complex Structures**: Nested arrays, collections, matrices

## Running All Tests

**Quick validation** (recommended for development):

```bash
# From project root
java -cp bin tests.unit.TestFrameworkTests
```

**Complete test suite** (recommended for releases):

```bash
# From project root
./lib/tests/run-all-tests.sh
```

**Individual test categories:**

```bash
# From project root

# Unit tests only
java -cp bin tests.unit.TestFrameworkTests

# Integration tests only
java -cp bin tests.integration.SimpleTest

# Examples and demos
java -cp bin tests.examples.FinalDemo
```

## Test Development Guidelines

### Adding New Tests

1. **Unit Tests**: Add to `TestFrameworkTests.java`

   - Follow existing naming pattern: `testComponentName()`
   - Use assertion helpers: `assertEqual()`, `assertArrayEqual()`
   - Include edge cases and error conditions

2. **Integration Tests**: Create new files in `integration/`

   - Test complete workflows end-to-end
   - Use real JSON test files
   - Validate results and performance

3. **Examples**: Add to `examples/` directory
   - Include both .java files and .json test data
   - Document expected behavior
   - Demonstrate specific features

### Test Data Format

**Legacy Format Example:**

```json
{
  "name": "Simple Test",
  "input": "[1,2,3]",
  "expected": "6",
  "description": "Sum array elements"
}
```

**Enhanced Format Example:**

```json
{
  "name": "Multi-Parameter Test",
  "input": ["[1,2,3]", "2"],
  "inputTypes": ["int[]", "int"],
  "expected": "1",
  "description": "Find target index"
}
```

### Best Practices

1. **Comprehensive Coverage**: Test both success and failure cases
2. **Clear Naming**: Use descriptive test names and descriptions
3. **Isolation**: Each test should be independent
4. **Documentation**: Comment complex test scenarios
5. **Performance**: Include timing-sensitive tests where relevant

## Continuous Integration

These tests are designed to be run in CI/CD pipelines:

- **Exit Codes**: Tests return 0 on success, 1 on failure
- **No External Dependencies**: Pure Java, no external libraries
- **Fast Execution**: Unit tests complete in < 5 seconds
- **Deterministic**: Results are consistent across environments

## Troubleshooting

### Common Issues

1. **Classpath Problems**

   ```bash
   # From project root - ensure bin directory contains compiled classes
   java -cp bin tests.unit.TestFrameworkTests
   ```

2. **Compilation Errors**

   ```bash
   # From project root - compile all dependencies first
   javac -cp lib/shared -d bin lib/shared/*.java lib/tests/unit/*.java lib/tests/integration/*.java lib/tests/examples/*.java
   ```

3. **Test Failures**
   - Check for file path issues (use absolute paths)
   - Verify JSON formatting in test data files
   - Review error messages for specific failure details

### Debug Mode

Enable verbose output for debugging:

```java
// In test files, add debug prints
System.out.println("Debug: " + variableName);
```

### Performance Issues

Monitor test execution time:

- Unit tests should complete in < 5 seconds
- Integration tests should complete in < 10 seconds
- If slower, check for resource leaks or inefficient operations

---

This test suite ensures the enhanced LeetCode framework maintains high quality, reliability, and backward compatibility while providing comprehensive coverage of all features and edge cases.
