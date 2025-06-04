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
    if (root == null) return values;
    
    Stack<TreeNode> stack = new Stack<>();
    stack.push(root);
    
    while (!stack.isEmpty()) {
        TreeNode node = stack.pop();
        values.add(node.val);
        
        // Push right first, then left
        // This ensures left is processed before right (LIFO)
        if (node.right != null) {
            stack.push(node.right);
        }
        if (node.left != null) {
            stack.push(node.left);
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
