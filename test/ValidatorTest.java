package test;

import annotations.*;
import validation.Validator;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ValidatorTest {

    @FileSource(delimiter = ",")
    static class TestClass {
        @Column(index = 0, name = "id")
        @NotNull(message = "ID cannot be null")
        private Integer id;
        
        @Column(index = 1, name = "email")
        @Regex(pattern = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Invalid email format")
        private String email;
        
        @Column(index = 2, name = "age")
        @Range(min = 0, max = 120, message = "Age must be between 0 and 120")
        private int age;

        public TestClass(Integer id, String email, int age) {
            this.id = id;
            this.email = email;
            this.age = age;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Validator Tests ===\n");
        
        int passed = 0;
        int total = 0;
        
        total++; if (testValidateAllValid()) passed++;
        total++; if (testValidateNotNullViolation()) passed++;
        total++; if (testValidateRegexViolation()) passed++;
        total++; if (testValidateRangeViolation()) passed++;
        total++; if (testValidateMultipleViolations()) passed++;
        
        System.out.println("\n=== Test Results ===");
        System.out.println("Passed: " + passed + "/" + total);
        System.out.println("Coverage: " + (passed * 100 / total) + "%");
    }

    private static boolean testValidateAllValid() {
        System.out.print("testValidateAllValid... ");
        TestClass obj1 = new TestClass(1, "test@example.com", 25);
        TestClass obj2 = new TestClass(2, "user@domain.org", 30);
        
        Validator validator = new Validator();
        Map<TestClass, Set<String>> errors = validator.validate(List.of(obj1, obj2));
        
        if (errors.isEmpty()) {
            System.out.println("PASS");
            return true;
        }
        System.out.println("FAIL");
        return false;
    }

    private static boolean testValidateNotNullViolation() {
        System.out.print("testValidateNotNullViolation... ");
        TestClass obj = new TestClass(null, "test@example.com", 25);
        
        Validator validator = new Validator();
        Map<TestClass, Set<String>> errors = validator.validate(List.of(obj));
        
        if (!errors.isEmpty() && errors.get(obj).stream().anyMatch(e -> e.contains("ID cannot be null"))) {
            System.out.println("PASS");
            return true;
        }
        System.out.println("FAIL");
        return false;
    }

    private static boolean testValidateRegexViolation() {
        System.out.print("testValidateRegexViolation... ");
        TestClass obj = new TestClass(1, "invalid-email", 25);
        
        Validator validator = new Validator();
        Map<TestClass, Set<String>> errors = validator.validate(List.of(obj));
        
        if (!errors.isEmpty() && errors.get(obj).stream().anyMatch(e -> e.contains("Invalid email format"))) {
            System.out.println("PASS");
            return true;
        }
        System.out.println("FAIL");
        return false;
    }

    private static boolean testValidateRangeViolation() {
        System.out.print("testValidateRangeViolation... ");
        TestClass obj = new TestClass(1, "test@example.com", 150);
        
        Validator validator = new Validator();
        Map<TestClass, Set<String>> errors = validator.validate(List.of(obj));
        
        if (!errors.isEmpty() && errors.get(obj).stream().anyMatch(e -> e.contains("Age must be between 0 and 120"))) {
            System.out.println("PASS");
            return true;
        }
        System.out.println("FAIL");
        return false;
    }

    private static boolean testValidateMultipleViolations() {
        System.out.print("testValidateMultipleViolations... ");
        TestClass obj = new TestClass(null, "invalid-email", 150);
        
        Validator validator = new Validator();
        Map<TestClass, Set<String>> errors = validator.validate(List.of(obj));
        
        if (!errors.isEmpty() && errors.get(obj).size() == 3) {
            System.out.println("PASS");
            return true;
        }
        System.out.println("FAIL");
        return false;
    }
}
