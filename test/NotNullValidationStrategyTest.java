import annotations.NotNull;
import validation.NotNullValidationStrategy;

import java.lang.reflect.Field;

public class NotNullValidationStrategyTest {

    static class TestClass {
        @NotNull(message = "Field cannot be null")
        private String notNullField;
        
        private String regularField;
    }

    public static void main(String[] args) throws NoSuchFieldException {
        System.out.println("=== NotNullValidationStrategy Tests ===\n");
        
        int passed = 0;
        int total = 0;
        
        total++; if (testValidateNullValue()) passed++;
        total++; if (testValidateNonNullValue()) passed++;
        total++; if (testValidateFieldWithoutAnnotation()) passed++;
        
        System.out.println("\n=== Test Results ===");
        System.out.println("Passed: " + passed + "/" + total);
        System.out.println("Coverage: " + (passed * 100 / total) + "%");
    }

    private static boolean testValidateNullValue() throws NoSuchFieldException {
        System.out.print("testValidateNullValue... ");
        NotNullValidationStrategy strategy = new NotNullValidationStrategy();
        Field field = TestClass.class.getDeclaredField("notNullField");
        
        String error = strategy.validate(field, null);
        
        if (error != null && error.equals("Field cannot be null")) {
            System.out.println("PASS");
            return true;
        }
        System.out.println("FAIL");
        return false;
    }

    private static boolean testValidateNonNullValue() throws NoSuchFieldException {
        System.out.print("testValidateNonNullValue... ");
        NotNullValidationStrategy strategy = new NotNullValidationStrategy();
        Field field = TestClass.class.getDeclaredField("notNullField");
        
        String error = strategy.validate(field, "some value");
        
        if (error == null) {
            System.out.println("PASS");
            return true;
        }
        System.out.println("FAIL");
        return false;
    }

    private static boolean testValidateFieldWithoutAnnotation() throws NoSuchFieldException {
        System.out.print("testValidateFieldWithoutAnnotation... ");
        NotNullValidationStrategy strategy = new NotNullValidationStrategy();
        Field field = TestClass.class.getDeclaredField("regularField");
        
        String error = strategy.validate(field, null);
        
        if (error == null) {
            System.out.println("PASS");
            return true;
        }
        System.out.println("FAIL");
        return false;
    }
}
