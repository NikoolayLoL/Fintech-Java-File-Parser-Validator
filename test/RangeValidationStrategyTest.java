import annotations.Range;
import validation.RangeValidationStrategy;

import java.lang.reflect.Field;

public class RangeValidationStrategyTest {

    static class TestClass {
        @Range(min = 0, max = 100, message = "Value must be between 0 and 100")
        private int score;
        
        private int regularField;
    }

    public static void main(String[] args) throws NoSuchFieldException {
        System.out.println("=== RangeValidationStrategy Tests ===\n");
        
        int passed = 0;
        int total = 0;
        
        total++; if (testValidateValueInRange()) passed++;
        total++; if (testValidateValueBelowRange()) passed++;
        total++; if (testValidateValueAboveRange()) passed++;
        total++; if (testValidateNullValue()) passed++;
        total++; if (testValidateFieldWithoutAnnotation()) passed++;
        total++; if (testValidateValueAtMinBoundary()) passed++;
        total++; if (testValidateValueAtMaxBoundary()) passed++;
        
        System.out.println("\n=== Test Results ===");
        System.out.println("Passed: " + passed + "/" + total);
        System.out.println("Coverage: " + (passed * 100 / total) + "%");
    }

    private static boolean testValidateValueInRange() throws NoSuchFieldException {
        System.out.print("testValidateValueInRange... ");
        RangeValidationStrategy strategy = new RangeValidationStrategy();
        Field field = TestClass.class.getDeclaredField("score");
        
        String error = strategy.validate(field, 50);
        
        if (error == null) {
            System.out.println("PASS");
            return true;
        }
        System.out.println("FAIL");
        return false;
    }

    private static boolean testValidateValueBelowRange() throws NoSuchFieldException {
        System.out.print("testValidateValueBelowRange... ");
        RangeValidationStrategy strategy = new RangeValidationStrategy();
        Field field = TestClass.class.getDeclaredField("score");
        
        String error = strategy.validate(field, -10);
        
        if (error != null && error.equals("Value must be between 0 and 100")) {
            System.out.println("PASS");
            return true;
        }
        System.out.println("FAIL");
        return false;
    }

    private static boolean testValidateValueAboveRange() throws NoSuchFieldException {
        System.out.print("testValidateValueAboveRange... ");
        RangeValidationStrategy strategy = new RangeValidationStrategy();
        Field field = TestClass.class.getDeclaredField("score");
        
        String error = strategy.validate(field, 150);
        
        if (error != null && error.equals("Value must be between 0 and 100")) {
            System.out.println("PASS");
            return true;
        }
        System.out.println("FAIL");
        return false;
    }

    private static boolean testValidateNullValue() throws NoSuchFieldException {
        System.out.print("testValidateNullValue... ");
        RangeValidationStrategy strategy = new RangeValidationStrategy();
        Field field = TestClass.class.getDeclaredField("score");
        
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
        RangeValidationStrategy strategy = new RangeValidationStrategy();
        Field field = TestClass.class.getDeclaredField("regularField");
        
        String error = strategy.validate(field, 500);
        
        if (error == null) {
            System.out.println("PASS");
            return true;
        }
        System.out.println("FAIL");
        return false;
    }

    private static boolean testValidateValueAtMinBoundary() throws NoSuchFieldException {
        System.out.print("testValidateValueAtMinBoundary... ");
        RangeValidationStrategy strategy = new RangeValidationStrategy();
        Field field = TestClass.class.getDeclaredField("score");
        
        String error = strategy.validate(field, 0);
        
        if (error == null) {
            System.out.println("PASS");
            return true;
        }
        System.out.println("FAIL");
        return false;
    }

    private static boolean testValidateValueAtMaxBoundary() throws NoSuchFieldException {
        System.out.print("testValidateValueAtMaxBoundary... ");
        RangeValidationStrategy strategy = new RangeValidationStrategy();
        Field field = TestClass.class.getDeclaredField("score");
        
        String error = strategy.validate(field, 100);
        
        if (error == null) {
            System.out.println("PASS");
            return true;
        }
        System.out.println("FAIL");
        return false;
    }
}
