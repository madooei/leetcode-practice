import java.util.*;

/**
 * Definition for a binary tree node. Common data structure used across multiple LeetCode problems.
 */
public class TreeNode {
    public int val;
    public TreeNode left;
    public TreeNode right;

    public TreeNode() {}

    public TreeNode(int val) {
        this.val = val;
    }

    public TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }

    /**
     * Create a tree from array representation (LeetCode format)
     * 
     * @param arr Array representation where null represents missing nodes
     * @return Root of the constructed tree
     */
    public static TreeNode fromArray(Integer[] arr) {
        if (arr == null || arr.length == 0 || arr[0] == null) {
            return null;
        }

        TreeNode root = new TreeNode(arr[0]);
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        int i = 1;
        while (!queue.isEmpty() && i < arr.length) {
            TreeNode node = queue.poll();

            // Left child
            if (i < arr.length && arr[i] != null) {
                node.left = new TreeNode(arr[i]);
                queue.offer(node.left);
            }
            i++;

            // Right child
            if (i < arr.length && arr[i] != null) {
                node.right = new TreeNode(arr[i]);
                queue.offer(node.right);
            }
            i++;
        }

        return root;
    }

    /**
     * Parse array string to Integer array
     * 
     * @param arrayStr String like "[1,null,2,3]"
     * @return Integer array
     */
    public static Integer[] parseArray(String arrayStr) {
        if (arrayStr.equals("[]")) {
            return new Integer[0];
        }

        // Remove brackets and split
        String content = arrayStr.substring(1, arrayStr.length() - 1);
        String[] parts = content.split(",");
        Integer[] result = new Integer[parts.length];

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();
            if (part.equals("null")) {
                result[i] = null;
            } else {
                result[i] = Integer.parseInt(part);
            }
        }

        return result;
    }

    /**
     * Convert tree to level-order array representation
     * 
     * @param root Root of the tree
     * @return List representing the tree
     */
    public static List<Integer> toArray(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null)
            return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            if (node != null) {
                result.add(node.val);
                queue.offer(node.left);
                queue.offer(node.right);
            } else {
                result.add(null);
            }
        }

        // Remove trailing nulls
        while (!result.isEmpty() && result.get(result.size() - 1) == null) {
            result.remove(result.size() - 1);
        }

        return result;
    }

    @Override
    public String toString() {
        return toArray(this).toString();
    }
}
