# Test Directory Structure

The enhanced LeetCode test framework is now organized as follows:

```plaintext
lib/
├── [Framework Components]
│   ├── TestRunner.java              # Main orchestrator
│   ├── TestExecutor.java            # Enhanced execution engine
│   ├── TypeParser.java              # Universal type parsing (NEW)
│   ├── TestDataModels.java          # Enhanced with type hints
│   ├── JsonProcessor.java           # Object-aware JSON parsing
│   ├── FileOperations.java          # File I/O operations
│   ├── TreeNode.java               # Binary tree data structure
│   ├── PerformanceAnalyzer.java    # Performance measurement
│   ├── TestResultsManager.java     # Result collection and reporting
│   └── OutputFormatter.java        # Result formatting utilities
│
├── README.md                        # Main framework documentation
│
└── tests/                          # Organized test suite
    ├── README.md                   # Test suite documentation
    ├── run-all-tests.sh           # Complete test runner script
    ├── DIRECTORY_STRUCTURE.md     # This file
    │
    ├── unit/                       # Unit tests for individual components
    │   └── TestFrameworkTests.java # 85+ comprehensive unit tests
    │
    ├── integration/                # End-to-end workflow tests
    │   └── SimpleTest.java        # Integration testing
    │
    └── examples/                   # Demonstrations and examples
        ├── DemoSolution.java      # Sample solution class
        ├── DemoRunner.java        # Method-specific demos
        ├── FinalDemo.java         # Complete showcase
        ├── tree-tests.json        # Binary tree test cases
        ├── array-tests.json       # Array problem test cases
        ├── demo-tests.json        # Multi-method test suite
        └── demo-tests-results.json # Example results file
```

## Benefits of This Organization

### 🎯 **Clear Separation of Concerns**

- **Framework code** stays in the root `lib/` directory
- **Test code** is organized in the `tests/` subdirectory
- **Different test types** have their own subdirectories

### 📁 **Logical Grouping**

- **Unit tests** for individual component validation
- **Integration tests** for end-to-end workflow validation
- **Examples** for demonstrations and learning

### 🔧 **Easy Maintenance**

- Tests are easy to find and modify
- Clear structure for adding new tests
- Separate documentation for test suite

### 🚀 **Simple Execution**

- Run all tests: `./tests/run-all-tests.sh`
- Run specific category: `cd tests/unit && java -cp ../../:. TestFrameworkTests`
- Run examples: `cd tests/examples && java -cp ../../:. FinalDemo`

## Quick Start

### Run All Tests

```bash
cd lib/tests
./run-all-tests.sh
```

### Run Individual Test Categories

```bash
# Unit tests only
cd lib/tests/unit
java -cp ../../:. TestFrameworkTests

# Integration tests only
cd lib/tests/integration
java -cp ../../:. SimpleTest

# Examples and demos
cd lib/tests/examples
java -cp ../../:. FinalDemo
```

### Add New Tests

1. **Unit tests**: Add to `tests/unit/TestFrameworkTests.java`
2. **Integration tests**: Create new files in `tests/integration/`
3. **Examples**: Add files to `tests/examples/`

This organization maintains the framework's simplicity while providing a clean, professional structure for testing and demonstration.
