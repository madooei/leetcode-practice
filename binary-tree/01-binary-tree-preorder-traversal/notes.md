# Notes

For the iterative solution, I came up with this:

```java
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
```

And then asked AI to review it, and it came up with this:

```java
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
  return values;
}
```

I suppose this version is cleaner and more elegant! 

The learning point here is that although for the traversal we need to process the left child first, we can push the right child onto the stack first because the stack is LIFO (Last In, First Out). This way, when we pop from the stack, the left child will be processed before the right child, maintaining the preorder traversal order.