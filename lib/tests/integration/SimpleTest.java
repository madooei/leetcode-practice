package tests.integration;

import java.util.*;
import shared.TestResultsManager;
import shared.TestRunner;
import shared.TreeNode;

/**
 * Integration test for the enhanced test framework
 * Tests end-to-end workflow with both legacy and enhanced formats
 */
public class SimpleTest {

    /**
     * Simple solution class for testing
     */
    public static class TestSolution {
        public List<Integer> preorderTraversal(TreeNode root) {
            List<Integer> result = new ArrayList<>();
            preorderHelper(root, result);
            return result;
        }

        private void preorderHelper(TreeNode node, List<Integer> result) {
            if (node == null)
                return;
            result.add(node.val);
            preorderHelper(node.left, result);
            preorderHelper(node.right, result);
        }

        public int searchInsert(int[] nums, int target) {
            int left = 0, right = nums.length - 1;

            while (left <= right) {
                int mid = left + (right - left) / 2;
                if (nums[mid] == target) {
                    return mid;
                } else if (nums[mid] < target) {
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }

            return left;
        }
    }

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Integration Test - End-to-End Workflow");
        System.out.println("=".repeat(60));

        TestSolution solution = new TestSolution();

        System.out.println("\n1️⃣ Testing Tree Traversal (Legacy + Enhanced):");
        String testFile = "lib/tests/examples/tree-tests.json";
        TestResultsManager results =
                TestRunner.runTests(testFile, solution, "preorderTraversal");
        printQuickSummary(results);

        System.out.println("\n2️⃣ Testing Array Search (Enhanced with Type Hints):");
        testFile = "lib/tests/examples/array-tests.json";
        TestResultsManager results2 =
                TestRunner.runTests(testFile, solution, "searchInsert");
        printQuickSummary(results2);

        // Overall summary
        int totalTests = results.getTotalCount() + results2.getTotalCount();
        int totalPassed = results.getPassedCount() + results2.getPassedCount();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("Integration Test Summary");
        System.out.println("=".repeat(60));
        System.out.println("Total Tests: " + totalTests);
        System.out.println("Passed: " + totalPassed);
        System.out.println("Failed: " + (totalTests - totalPassed));

        if (totalPassed == totalTests) {
            System.out.println("Status: ✅ ALL INTEGRATION TESTS PASSED");
            System.out.println("\n🎉 End-to-end workflow is working correctly!");
        } else {
            System.out.println("Status: ❌ SOME INTEGRATION TESTS FAILED");
            System.out.println("\n⚠️ Please review the failed tests above.");
        }
        System.out.println("=".repeat(60));
    }

    private static void printQuickSummary(TestResultsManager results) {
        int total = results.getTotalCount();
        int passed = results.getPassedCount();
        System.out.println("   Result: " + passed + "/" + total + " tests passed"
                + (passed == total ? " ✅" : " ❌"));
    }
}
