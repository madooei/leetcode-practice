public class HelloWorldSolution {
  public String sayHello(String name) {
    if (name == null || name.trim().isEmpty()) {
      return "Hello, World!";
    }
    return "Hello, " + name + "!";
  }

  public static void main(String[] args) {
    String testFile = "hello-world/tests.json";
    HelloWorldSolution solution = new HelloWorldSolution();
    TestResultsManager results =
        TestRunner.runTests(testFile, solution, "sayHello", true, true);
    results.printSummary();
  }
}
