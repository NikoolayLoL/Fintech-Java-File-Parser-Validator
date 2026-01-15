package test;

import annotations.Regex;
import validation.RegexValidationStrategy;

import java.lang.reflect.Field;

public class RegexValidationStrategyTest {

    static class TestClass {
        @Regex(pattern = "^[0-9]{3}-[0-9]{4}$", message = "Invalid phone format")
        private String phone;
        
        private String regularField;
    }

    public static void main(String[] args) throws NoSuchFieldException {
        System.out.println("=== RegexValidationStrategy Tests ===\n");
        
        int passed = 0;
        int total = 0;
        
        total++; if (testValidateValidPattern()) passed++;
        total++; if (testValidateInvalidPattern()) passed++;
        total++; if (testValidateNullValue()) passed++;
        total++; if (testValidateFieldWithoutAnnotation()) passed++;
        
        System.out.println("\n=== Test Results ===");
        System.out.println("Passed: " + passed + "/" + total);
        System.out.println("Coverage: " + (passed * 100 / total) + "%");
    }

    private static boolean testValidateValidPattern() throws NoSuchFieldException {
        System.out.print("testValidateValidPattern... ");
        RegexValidationStrategy strategy = new RegexValidationStrategy();
        Field field = TestClass.class.getDeclaredField("phone");
        
        String error = strategy.validate(field, "123-4567");
        
        if (error == null) {
            System.out.println("PASS");
            return true;
        }
        System.out.println("FAIL");
        return false;
    }

    private static boolean testValidateInvalidPattern() throws NoSuchFieldException {
        System.out.print("testValidateInvalidPattern... ");
        RegexValidationStrategy strategy = new RegexValidationStrategy();
        Field field = TestClass.class.getDeclaredField("phone");
        
        String error = strategy.validate(field, "12345678");
        
        if (error != null && error.equals("Invalid phone format")) {
            System.out.println("PASS");
            return true;
        }
        System.out.println("FAIL");
        return false;
    }

    private static boolean testValidateNullValue() throws NoSuchFieldException {
        System.out.print("testValidateNullValue... ");
        RegexValidationStrategy strategy = new RegexValidationStrategy();
        Field field = TestClass.class.getDeclaredField("phone");
        
        String error = strategy.validate(field, null);
        
        if (error == null) {
            System.out.println("PASS");
            return true;
        }
        System.out.println("FAIL");
        return false;
    }

    private static boolean testValidateFieldWithoutAnnotation() throws NoSuchFieldException {
        System.out.print("testValidateFieldWithoutAnnotation... ");
        RegexValidationStrategy strategy = new RegexValidationStrategy();
        Field field = TestClass.class.getDeclaredField("regularField");
        
        String error = strategy.validate(field, "anything");
        
        if (error == null) {
            System.out.println("PASS");
            return true;
        }
        System.out.println("FAIL");
        return false;
    }
}
