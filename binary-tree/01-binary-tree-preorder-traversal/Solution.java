import java.util.List;

public class Solution {
  public List<Integer> preorderTraversal(TreeNode root) {
    return null;
  }

  public static void main(String[] args) {
    String testFile = "binary-tree/01-binary-tree-preorder-traversal/tests.json";
    Solution solution = new Solution();
    TestRunner.TestResults results = TestRunner.runTests(testFile, solution, "preorderTraversal");
    results.printSummary();
  }
}
