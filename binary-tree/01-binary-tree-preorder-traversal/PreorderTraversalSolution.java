import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class PreorderTraversalSolution {
  public List<Integer> preorderTraversal(TreeNode root) {
    // return preorderTraversalRec(root);
    return preorderTraversalItr(root);
  }

  // Iterative solution
  private List<Integer> preorderTraversalItr(TreeNode root) {
    List<Integer> values = new ArrayList<>();
    Stack<TreeNode> nodes = new Stack<>();

    TreeNode node = root;
    while (node != null) {
      nodes.push(node);
      values.add(node.val);
      while (node.left != null) {
        node = node.left;
        nodes.push(node);
        values.add(node.val);
      }
      while (!nodes.empty()) {
        node = nodes.pop();
        node = node.right;
        if (node != null) {
          break;
        }
      }
    }
    return values;
  }

  // Recursive solution
  private List<Integer> preorderTraversalRec(TreeNode root) {
    List<Integer> nodes = new ArrayList<>();
    preorderTraversalRecHelper(root, nodes);
    return nodes;
  }

  private void preorderTraversalRecHelper(TreeNode root, List<Integer> nodes) {
    if (root == null) {
      return;
    }
    nodes.add(root.val);
    preorderTraversalRecHelper(root.left, nodes);
    preorderTraversalRecHelper(root.right, nodes);
  }

  public static void main(String[] args) {
    String testFile = "binary-tree/01-binary-tree-preorder-traversal/tests.json";
    PreorderTraversalSolution solution = new PreorderTraversalSolution();
    String methodName = "preorderTraversal";
    boolean profile = true; // Set to true for performance insights (experimental)
    boolean storeResults = true; // creates tests-results.json in this folder
    TestResultsManager results =
        TestRunner.runTests(testFile, solution, methodName, profile, storeResults);
    results.printSummary();
  }
}
