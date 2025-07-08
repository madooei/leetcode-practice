# LeetCode Practice Setup 🚀

A professional VS Code environment for practicing LeetCode problems with automated testing, debugging, and clean project organization.

## 📋 Prerequisites

- **Java Development Kit (JDK)** 8 or higher (try <https://adoptium.net/> for easy installation)
- **Visual Studio Code** with the Java Extension Pack (automatically suggested when you open this project)

> [!TIP]
> Refer to the section on [IntelliJ](#intellij) if you prefer using IntelliJ IDEA.

## 📁 Project Structure

```plaintext
leetcode-practice/
├── .vscode/
│   ├── extensions.json     # Recommends Java Extension Pack
│   └── settings.json       # Java project configuration
├── README.md               # This file
├── TODOs.md                # List of tasks (problems to solves, etc.)
├── bin/                    # Compiled Java classes (auto-generated)
├── lib/                    # Testing framework and external libraries
│   ├── shared/             # Framework classes (use `import shared.*;`)
│   │   ├── TestRunner.java # Test execution framework
│   │   ├── TypeParser.java # Universal type parsing
│   │   ├── TreeNode.java   # Binary tree utilities
│   │   └── ... (other framework classes)
│   └── tests/              # Framework test suite
└── hello-world/            # Template for new problems
    ├── README.md           # Problem statement
    ├── Starter.md          # Original LeetCode template
    ├── tests.json          # Test cases
    ├── HelloWorldSolution.java   # Your implementation
    ├── board.excalidraw.svg      # Optional Excalidraw whiteboard
    └── notes.md            # Your notes and observations
```

## 🚀 Getting Started

### 1. Initial Setup

1. **Open this project in VS Code**
2. **Install recommended extensions** when prompted (Java Extension Pack)
3. **Wait for Java extension to initialize** (you'll see progress in the bottom status bar)

### 2. Test Your Setup

Navigate to the `hello-world` folder and open `HelloWorldSolution.java`. You should see two options above the `main` method:

- **Run** - Execute the program
- **Debug** - Run with debugging capabilities

Click **Run** to verify everything works!

## 📝 Solving a Problem - Step by Step

Let's walk through solving the **Binary Tree Preorder Traversal** problem as an example.

### Step 0: Update the TODOs.md

Before starting a new problem, update the `TODOs.md` file to include the problem you are about to solve. This helps keep track of your progress and what you plan to work on next.

### Step 1: Create Your Problem Directory

Organize problems by topic and number them for easy reference:

```bash
mkdir -p binary-tree/01-binary-tree-preorder-traversal
cd binary-tree/01-binary-tree-preorder-traversal
```

### Step 2: Update VS Code Settings

**Important:** Add your new directory to the source paths in `.vscode/settings.json`:

```json
{
  "java.project.sourcePaths": [
    "lib",
    "hello-world",
    "binary-tree/01-binary-tree-preorder-traversal" // Add this line
  ]
  // ... other settings
}
```

> [!NOTE]
> The `"lib"` entry in source paths provides access to external JAR libraries and framework classes. The framework classes are in the `shared` package, so you'll need to add `import shared.*;` to your solution files.

### Step 3: Create Problem Files

> [!TIP]
> You can copy the structure from the `hello-world` example, but with problem-specific content.

#### 3.1 Create `README.md` (Problem Statement)

Copy the problem description from LeetCode:

```markdown
# Binary Tree Preorder Traversal

Given the root of a binary tree, return the preorder traversal of its nodes' values.

**Example 1:**

- Input: root = [1,null,2,3]
- Output: [1,2,3]

**Example 2:**

- Input: root = []
- Output: []

**Constraints:**

- The number of nodes in the tree is in the range [0, 100]
- -100 <= Node.val <= 100
```

#### 3.2 Create `Starter.md` (Original Template)

Copy the starter code from LeetCode exactly as provided:

```java
/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode() {}
 *     TreeNode(int val) { this.val = val; }
 *     TreeNode(int val, TreeNode left, TreeNode right) {
 *         this.val = val;
 *         this.left = left;
 *         this.right = right;
 *     }
 * }
 */
class Solution {
    public List<Integer> preorderTraversal(TreeNode root) {

    }
}
```

#### 3.3 Create `tests.json` (Test Cases)

Start with LeetCode examples, then add your own. Use the enhanced format with type hints for precise input interpretation:

```json
[
  {
    "name": "Example 1",
    "input": ["[1,null,2,3]"],
    "inputTypes": ["TreeNode"],
    "expected": "[1,2,3]",
    "description": "Right-skewed tree with left child"
  },
  {
    "name": "Example 2",
    "input": ["[]"],
    "inputTypes": ["TreeNode"],
    "expected": "[]",
    "description": "Empty tree"
  },
  {
    "name": "Example 3",
    "input": ["[1]"],
    "inputTypes": ["TreeNode"],
    "expected": "[1]",
    "description": "Single node tree"
  },
  {
    "name": "Complete Binary Tree",
    "input": ["[1,2,3,4,5,6,7]"],
    "inputTypes": ["TreeNode"],
    "expected": "[1,2,4,5,3,6,7]",
    "description": "Complete binary tree - preorder should be root, left subtree, right subtree"
  }
]
```

> [!NOTE]
> The `input` field is an array where each element represents an argument to your solution method. The `inputTypes` field specifies how to interpret each input (e.g., "TreeNode", "int[]", "String"). The `expected` field should be a string representation of the expected output. This format ensures precise type handling and supports methods with multiple parameters.

**Supported Input Types:**
- **TreeNode**: Binary tree from array notation (`"[1,2,3,null,null,4,5]"`)
- **Arrays**: `"int[]"`, `"String[]"`, `"double[]"`, `"boolean[]"`
- **2D Arrays**: `"int[][]"`, `"char[][]"`, `"String[][]"`
- **Collections**: `"List<Integer>"`, `"List<String>"`, `"List<List<Integer>>"`
- **Primitives**: `"int"`, `"String"`, `"boolean"`, `"double"`, `"long"`, `"char"`

**Examples for Different Problem Types:**

*Array Problem (Two Sum):*
```json
{
  "name": "Example 1",
  "input": ["[2,7,11,15]", "9"],
  "inputTypes": ["int[]", "int"],
  "expected": "[0,1]",
  "description": "Target sum found at indices 0 and 1"
}
```

*Matrix Problem (Search 2D Matrix):*
```json
{
  "name": "Matrix Search",
  "input": ["[[1,4,7,11],[2,5,8,12],[3,6,9,16]]", "5"],
  "inputTypes": ["int[][]", "int"],
  "expected": "true",
  "description": "Target 5 exists in the matrix"
}
```

*String Problem (Valid Parentheses):*
```json
{
  "name": "Valid Parentheses",
  "input": ["()[]{}"],
  "inputTypes": ["String"],
  "expected": "true",
  "description": "All brackets are properly matched"
}
```

#### 3.4 Framework Classes Available

Looking at the starter code, you need a `TreeNode` class to represent binary tree nodes. Good news! The framework already provides this in `lib/shared/TreeNode.java`.

The enhanced testing framework includes many utility classes already available in the `shared` package. For example, the provided `TreeNode` class can handle array input and output:

```java
// Create tree from LeetCode array format
TreeNode root = TreeNode.fromArray(new Integer[]{1, null, 2, 3});

// Parse string representation
Integer[] array = TreeNode.parseArray("[1,null,2,3]");

// Convert tree back to array (debugging)
List<Integer> arrayForm = TreeNode.toArray(root);
```

#### 3.5 Create Solution Java File (Your Implementation)

```java
import java.util.*;
import shared.*;  // Import framework classes

public class PreorderTraversalSolution {
  public List<Integer> preorderTraversal(TreeNode root) {
    // TODO: Implement your solution here
    return null;
  }

  public static void main(String[] args) {
    String testFile = "binary-tree/01-binary-tree-preorder-traversal/tests.json";
    PreorderTraversalSolution solution = new PreorderTraversalSolution();
    String methodName = "preorderTraversal";
    boolean profile = true; // Set to true for performance insights (experimental)
    TestResultsManager results = TestRunner.runTests(testFile, solution, methodName, profile);
    results.printSummary();
  }
}
```

**Key Points:**

- **Unique class name**: Use a descriptive name like `PreorderTraversalSolution`.
  - This name must be unique across your problems.
  - Follow the format: `<ProblemName>Solution.java` (e.g., `PreorderTraversalSolution.java`).
- **Main method**: Add a `main` method to run tests directly.
- **Import framework classes** with `import shared.*;` to access `TreeNode`, `TestRunner`, and `TestResultsManager`
- **Use the full path** to your `tests.json` file relative to the project root
- The `TestRunner` automatically detects your solution method by name
- Notice the arguments to `runTests`: the path to your test file, the solution instance, the method name to call, and whether to profile performance (experimental feature).

### Step 4: Implement and Test

1. **Implement your solution** in the `preorderTraversal` method
2. **Run tests** by clicking **Run** above the `main` method
3. **Debug** by setting breakpoints and clicking **Debug**

#### Example Implementation:

```java
public List<Integer> preorderTraversal(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    preorderHelper(root, result);
    return result;
}

private void preorderHelper(TreeNode root, List<Integer> result) {
    if (root == null) return;

    result.add(root.val);                    // Visit root
    preorderHelper(root.left, result);       // Traverse left subtree
    preorderHelper(root.right, result);      // Traverse right subtree
}
```

#### Excalidraw Whiteboard (Optional but Helpful)

Create a `board.excalidraw.svg` file in the problem directory. The Excalidraw VS Code extension (suggested in `.vscode/extensions.json` and also available in the marketplace as [`pomdtr.excalidraw-editor`](https://marketplace.visualstudio.com/items?itemName=pomdtr.excalidraw-editor)) allows you to create diagrams directly in your project. This can be useful for visualizing tree structures, algorithm flow, etc.

### Step 5: Keep Notes (Optional but Recommended)

Create `notes.md` to track your learning:

```markdown
# Binary Tree Preorder Traversal - Notes

## Approach 1: Recursive

- **Time Complexity:** O(n) - visit each node once
- **Space Complexity:** O(h) - recursion stack, where h is tree height
- **Key insight:** Preorder = root → left → right

## Challenges Faced:

- Initially forgot the base case for null nodes
- Confused about the order of recursive calls

## Alternative Approaches:

- Iterative with stack (simulate recursion)
- Morris traversal (O(1) space)
```

Another way to keep notes is "journaling" where you write down your thoughts, challenges, and insights like a diary (with date stamps). This allows you to track your progress over time and see how your understanding evolves.

You can also include links to the original LeetCode problem, relevant articles, or videos that helped you understand the topic better.

### Step 6: Commit, Submit, and Check Off Your TODOs

Once you have completed the problem:

- **Commit your changes** to version control (e.g., Git)
- **Submit your solution** on LeetCode
  - There are many more tests available on LeetCode that you can run to verify your solution. If any of those tests fail, you need to go back and fix your code. See the Debugging Guide section below for tips on how to debug your code.
- **Check off the problem** in your `TODOs.md` file
  - Update the to-do list to include any follow-up tasks related to this problem, such as reviewing lecture notes or exploring advanced techniques.

## 🔧 How It All Works

### VS Code Integration

The Java Extension Pack provides:

- **Automatic compilation** when you save files
- **IntelliSense** for code completion and error detection
- **Integrated debugging** with breakpoints and variable inspection
- **Run/Debug buttons** that appear above `main` methods

### Test Framework

The enhanced `TestRunner` class:

1. **Loads test cases** from your `tests.json` file with support for the new format
2. **Uses type hints** for precise input interpretation when specified
3. **Supports all LeetCode data types** including arrays, matrices, collections, and custom structures
4. **Automatically calls** your solution method using reflection
5. **Converts inputs** to appropriate types (e.g., `"[1,null,2,3]"` → `TreeNode`, `"[1,2,3]"` → `int[]`)
6. **Compares outputs** and provides detailed feedback with clear pass/fail indicators

### Directory Structure Benefits

- **`lib/shared/` package:** Framework classes available via `import shared.*;`
- **`lib/` folder:** External JAR libraries auto-included in classpath
- **Topic-based organization:** Group related problems together
- **Numbered problems:** Easy to track progress and find specific problems
- **Complete documentation:** README, tests, notes, and starter code all in one place

### Important: Class Naming Strategy

Since all problem directories are in the Java source path, **class names must be unique** across your entire project. This is why we use descriptive names like `PreorderTraversalSolution` instead of a generic name like `Solution`.

**Naming Convention:**

- `PreorderTraversalSolution.java` for Binary Tree Preorder Traversal
- `TwoSumSolution.java` for Two Sum problem
- `ValidParenthesesSolution.java` for Valid Parentheses

This prevents Java classpath conflicts and makes your code more organized!

## 🐛 Debugging Guide

### Setting Breakpoints

1. **Click in the left margin** next to any line number to set a breakpoint (red dot appears)
2. **Hover over the `main` method** and click **Debug**
3. **Execution will pause** at your breakpoint
4. **Use the debug panel** to inspect variables, step through code, etc.

### Debug Controls

- **Continue (F5):** Resume execution
- **Step Over (F10):** Execute next line
- **Step Into (F11):** Enter method calls
- **Step Out (Shift+F11):** Exit current method

### Viewing Variables

- **Hover over variables** to see their current values
- **Use the Variables panel** to see all local variables
- **Add expressions to Watch** to monitor specific values

### Debugging Tips

You can make a copy of `tests.json` (e.g., `tests-debug.json`) to run specific test cases while debugging. This allows you to focus on one or two cases without running the entire suite. Make sure to update the path in your `main` method accordingly:

```java
String testFile = "binary-tree/01-binary-tree-preorder-traversal/tests-debug.json";
```

**Example debug test file:**
```json
[
  {
    "name": "Debug Case",
    "input": ["[1,2,3,4,5]"],
    "inputTypes": ["TreeNode"],
    "expected": "[1,2,4,5,3]",
    "description": "Debugging preorder traversal"
  }
]
```

You can also add print statements in your code to log variable values or execution flow. This is especially useful for understanding complex logic or when you're stuck on a specific case. For example, consider these helper methods:

```java
// For debugging
private void printStack(Stack<TreeNode> stack) {
  StringBuffer buffer = new StringBuffer();
  buffer.append("[ ");
  for (TreeNode node : stack) {
    buffer.append(node.val);
    buffer.append(", ");
  }
  buffer.append("]");
  System.out.println("Stack: " + buffer.toString());
}

// For debugging
private void push(TreeNode node, Stack<TreeNode> stack) {
  System.out.print("Push " + node.val + " --- ");
  stack.push(node);
  printStack(stack);
}

// For debugging
private TreeNode pop(Stack<TreeNode> stack) {
  TreeNode node = stack.pop();
  System.out.print("Pop " + node.val + " --- ");
  printStack(stack);
  return node;
  }
```

Now you can call `push(node, stack)` and `pop(stack)` instead of directly using `stack.push(node)` and `stack.pop()`. This will print the stack state every time you push or pop a node, helping you understand how your algorithm is progressing.

When I find myself stuck, I often write down my thoughts in the `notes.md` file. This helps me clarify my thinking and often leads to breakthroughs. This technique is known as "rubber duck debugging" - explaining your code to an imaginary friend (or a rubber duck) can help you see problems more clearly.

I encourage you to take a systematic (hypothesis-driven) approach to debugging: In dealing with logical errors, you will often find that you don't know what's going on! Your instinct might be to take the code you have and change it to fix it. One of the worst things you can do is start changing your code without a plan. You will likely be digging yourself deeper! Instead, do this:

1. Gather data about the error that you have encountered.
2. Through careful observation, make a guess (form a hypothesis) about what the program is doing to cause the bug.
3. Conduct a test to see if the guess is correct or not (i.e., validate or refute the hypothesis).
4. If the test contradicts the guess, revise it or replace it with a new guess and go back to step 3.
5. If the test confirms the guess, use debugging tools to isolate the source of the bug and identify its cause.
6. Once the bug is localized and identified, determine a fix, apply and test if the fix worked.
7. Repeat the last step until the bug is fixed.

## 📚 Available Utilities

### TestRunner Features

- **Automatic method detection:** Finds your solution method by name
- **Universal type support:** TreeNode, arrays (1D/2D), collections, primitives, and more
- **Type hints system:** Explicit control over input interpretation using `inputTypes`
- **Multi-parameter support:** Handle methods with multiple arguments seamlessly
- **Detailed output:** Shows input, expected, actual, and pass/fail status
- **Error handling:** Catches exceptions and shows helpful messages
- **Performance profiling:** Measures execution time and memory usage (experimental)
- **Performance insights:** Provides performance metrics for each test case and overall summary (experimental)
- **Comprehensive testing:** Run the framework's own test suite with `./lib/tests/run-all-tests.sh` from project root

### Third-Party Libraries

If you need to add more third-party libraries, simply place the JAR files in the `lib/` folder and they will be available to all your problems. For example, the current TestRunner uses a built-in simple JSON parser, but if you want to use a more robust library like Jackson, you can add it easily like this:

```bash
curl -o lib/jackson-core.jar https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-core/2.15.2/jackson-core-2.15.2.jar
curl -o lib/jackson-databind.jar https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-databind/2.15.2/jackson-databind-2.15.2.jar
curl -o lib/jackson-annotations.jar https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-annotations/2.15.2/jackson-annotations-2.15.2.jar
```

Then import them in your Java files as needed:

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
```

## 📈 Workflow Tips

### 1. Start Simple

- Begin with the basic examples from LeetCode
- Get those working before adding complex test cases
- Use print statements to understand your code flow

### 2. Test Incrementally

- Add one test case at a time
- Verify edge cases: empty input, single elements, large inputs
- Create tests for different tree shapes: balanced, skewed, complete

### 3. Document Your Learning

- Keep notes on different approaches you try
- Record time/space complexity analysis
- Note patterns you discover across similar problems

### 4. Organize by Topic

Suggested folder structure:

```plaintext
├── array/
├── binary-tree/
├── dynamic-programming/
├── graph/
├── linked-list/
├── string/
└── two-pointers/
```

## 🆘 Troubleshooting

### "Class not found" errors

- Ensure your problem directory is added to `java.project.sourcePaths` in `.vscode/settings.json`
- Restart VS Code after adding new source paths

### Tests not running

- Verify the path to `tests.json` is correct (relative to project root)
- Check JSON syntax using a JSON validator
- Ensure your solution method name matches what you pass to `TestRunner.runTests()`

### IntelliSense not working

- Wait for Java extension to fully initialize (check status bar)
- Try reloading VS Code window: `Ctrl+Shift+P` → "Developer: Reload Window"

### Code formatting issues

- The project uses Google Java Style Guide automatically
- Save your file to auto-format, or use `Shift+Alt+F`

## 🎯 Example: Complete Problem Setup

Here's what a fully set up problem directory looks like:

```plaintext
binary-tree/01-binary-tree-preorder-traversal/
├── README.md          # Problem statement from LeetCode
├── Starter.md         # Original template code
├── tests.json         # Test cases (start with examples, add more)
├── PreorderTraversalSolution.java  # Your implementation with main method
├── board.excalidraw.svg            # Whiteboard for visualizing the problem
└── notes.md           # Your observations, learnings, resources, etc.
```

When you click **Run** on `PreorderTraversalSolution.java`, you'll see output like:

```plaintext
Running 4 test cases...
============================================================
Test 1: Example 1
Description: Right-skewed tree with left child
Input:    [1,null,2,3]
Expected: [1,2,3]
Got:      [1,2,3]
Status:   ✅ PASS

Test 2: Example 2
Description: Empty tree
Input:    []
Expected: []
Got:      []
Status:   ✅ PASS

============================================================
Results: 4/4 tests passed (100.0%)
🎉 All tests passed!
```

> [!NOTE]
> If performance profiling is enabled, you'll also see execution time and memory usage for each test case.

## 🚀 Ready to Start!

1. Open the `hello-world` example and run it to verify your setup
2. Create your first problem directory following the steps above
3. Update `.vscode/settings.json` with your new directory
4. Start coding and testing!

Happy coding! 🎉

> [!TIP]
> This setup scales beautifully - you can solve hundreds of problems using this same structure, and everything stays organized and easily testable.

## IntelliJ

You can open this folder in IntelliJ. The `leetcode-practice.iml` provides the necessary project structure. IntelliJ will automatically recognize the Java files and provide similar functionality to VS Code, including running and debugging tests.

When you create a new folder for a problem, make sure to right-click the folder and select "Mark Directory as" → "Sources Root" to ensure IntelliJ recognizes it as part of the source path. You should do this for any folder that contains Java files you want to run or debug.

You can use the following plugins to enhance your IntelliJ experience:

- [**jGRASP**](https://plugins.jetbrains.com/plugin/12769-jgrasp) for visualizing data structures, objects, and primitives while debugging.
- [**Java Visualizer**](https://plugins.jetbrains.com/plugin/11512-java-visualizer): For visualizing call stacks and objects on the heap while debugging.
- [**Excalidraw**](https://plugins.jetbrains.com/plugin/17096-excalidraw-integration): For creating diagrams directly in your project.
- [**CheckStyle-IDEA**](https://plugins.jetbrains.com/plugin/1065-checkstyle-idea): For code style checks and formatting.
