package annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validation annotation to enforce numeric boundaries on a field value.
 * <p>
 * This field-level annotation is used by {@link validation.Validator} to
 * ensure that numeric field values fall within the specified min/max range (inclusive).
 * Works with any numeric type ({@code int}, {@code double}, {@code Integer}, {@code Double}, etc.).
 * </p>
 * 
 * <p><b>Example Usage:</b></p>
 * <pre>
 * &#64;Range(min = 18, max = 120, message = "Age must be between 18 and 120")
 * private int age;
 * </pre>
 * 
 * @author FinTech Corp
 * @version 1.0.0
 * @since 1.0.0
 * @see validation.RangeValidationStrategy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Range {
    
    /**
     * The minimum allowed value (inclusive).
     * 
     * @return the minimum value
     */
    double min();
    
    /**
     * The maximum allowed value (inclusive).
     * 
     * @return the maximum value
     */
    double max();
    
    /**
     * The error message to return if validation fails.
     * 
     * @return the validation error message
     */
    String message() default "Value is out of range";
}
