# Hello World Greeting

Write a function that takes a person's name as input and returns a personalized greeting. This is a simple warm-up problem designed to help you verify your development environment and understand the testing framework.

## Function Signature

```java
public String sayHello(String name)
```

## Requirements

1. If the input `name` is a valid non-empty string, return `"Hello, [name]!"`
2. If the input `name` is `null`, empty, or contains only whitespace, return `"Hello, World!"`
3. The greeting should be properly formatted with correct capitalization and punctuation

## Examples

**Example 1:**
- **Input:** `"Alice"`
- **Output:** `"Hello, Alice!"`
- **Explanation:** Valid name provided, so we greet the person by name

**Example 2:**
- **Input:** `""`
- **Output:** `"Hello, World!"`
- **Explanation:** Empty string should return the default greeting

**Example 3:**
- **Input:** `"   "`
- **Output:** `"Hello, World!"`
- **Explanation:** Whitespace-only string should return the default greeting

**Example 4:**
- **Input:** `null`
- **Output:** `"Hello, World!"`
- **Explanation:** Null input should return the default greeting

**Example 5:**
- **Input:** `"Bob Smith"`
- **Output:** `"Hello, Bob Smith!"`
- **Explanation:** Names with spaces should work correctly

## Constraints

- The input string can be `null`
- The input string length can be 0 to 100 characters
- The input may contain letters, spaces, numbers, or special characters
- Only English characters are expected

