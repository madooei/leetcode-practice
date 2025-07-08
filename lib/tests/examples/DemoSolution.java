package tests.examples;

import shared.TreeNode;

/**
 * Demo solution class to showcase the enhanced test framework capabilities
 */
public class DemoSolution {

    /**
     * Example method for binary tree problems (legacy format compatible)
     */
    public java.util.List<Integer> preorderTraversal(TreeNode root) {
        java.util.List<Integer> result = new java.util.ArrayList<>();
        preorderHelper(root, result);
        return result;
    }

    private void preorderHelper(TreeNode node, java.util.List<Integer> result) {
        if (node == null)
            return;
        result.add(node.val);
        preorderHelper(node.left, result);
        preorderHelper(node.right, result);
    }

    /**
     * Example method for array problems (enhanced format with type hints)
     */
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

    /**
     * Example method for 2D matrix problems
     * 
     * This algorithm works for matrices where:
     * - Each row is sorted from left to right
     * - Each column is sorted from top to bottom
     * 
     * Strategy: Start from top-right corner, move left if target < current, down if target > current
     */
    public boolean searchMatrix(int[][] matrix, int target) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return false;
        }

        int rows = matrix.length;
        int cols = matrix[0].length;

        // Start from top-right corner
        int row = 0;
        int col = cols - 1;

        while (row < rows && col >= 0) {
            int current = matrix[row][col];

            if (current == target) {
                return true;
            } else if (current > target) {
                // Target is smaller, move left
                col--;
            } else {
                // Target is larger, move down
                row++;
            }
        }

        return false;
    }

    /**
     * Example method with multiple parameters of different types
     */
    public boolean containsNearbyDuplicate(int[] nums, int k) {
        java.util.Map<Integer, Integer> map = new java.util.HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            if (map.containsKey(nums[i]) && i - map.get(nums[i]) <= k) {
                return true;
            }
            map.put(nums[i], i);
        }

        return false;
    }
}
