package tests.examples;

import shared.TestResultsManager;
import shared.TestRunner;

/**
 * Demonstration of the enhanced test framework capabilities
 * 
 * This demo shows:
 * 1. Legacy format compatibility (automatic type detection)
 * 2. Enhanced format with type hints 
 * 3. Multiple data types (TreeNode, int[], int[][], int)
 * 4. Multi-parameter method testing
 */
public class DemoRunner {

    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("Enhanced LeetCode Test Framework - Live Demonstration");
        System.out.println("=".repeat(80));
        System.out.println();

        DemoSolution solution = new DemoSolution();

        // Test 1: Binary Tree Preorder Traversal (both legacy and enhanced)
        System.out.println("🌳 Testing Binary Tree Preorder Traversal...");
        testMethod(solution, "preorderTraversal",
                "Tree traversal with both legacy and enhanced formats",
                "lib/tests/examples/preorder-tests.json");

        // Test 2: Array Binary Search with multiple parameters
        System.out.println("🔍 Testing Array Search Insert...");
        testMethod(solution, "searchInsert", "Binary search with array and target parameters",
                "lib/tests/examples/search-insert-tests.json");

        // Test 3: 2D Matrix Search
        System.out.println("📊 Testing 2D Matrix Search...");
        testMethod(solution, "searchMatrix", "2D matrix search with int[][] type hint",
                "lib/tests/examples/search-matrix-tests.json");

        // Test 4: Array with K Distance Check
        System.out.println("📏 Testing Contains Nearby Duplicate...");
        testMethod(solution, "containsNearbyDuplicate", "Multi-parameter array problem",
                "lib/tests/examples/contains-nearby-duplicate-tests.json");

        System.out.println("=".repeat(80));
        System.out.println("✅ Demo completed! The enhanced framework successfully handles:");
        System.out.println("   • Legacy format (backward compatibility)");
        System.out.println("   • Enhanced format with type hints");
        System.out.println("   • Multiple data types (TreeNode, int[], int[][], primitives)");
        System.out.println("   • Multi-parameter methods");
        System.out.println("   • Automatic and explicit type parsing");
        System.out.println("=".repeat(80));
    }

    private static void testMethod(DemoSolution solution, String methodName, String description,
            String testFile) {
        System.out.println("Method: " + methodName);
        System.out.println("Description: " + description);
        System.out.println();

        try {
            boolean enableProfiling = true; // Enable performance profiling
            TestResultsManager results =
                    TestRunner.runTests(testFile, solution, methodName, enableProfiling);

            results.printSummary();

        } catch (Exception e) {
            System.err.println("Error testing " + methodName + ": " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("-".repeat(60));
        System.out.println();
    }
}
