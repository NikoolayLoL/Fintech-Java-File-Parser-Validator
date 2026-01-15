package validation;

import annotations.Range;

import java.lang.reflect.Field;

/**
 * Validation strategy that enforces numeric range constraints.
 * <p>
 * This strategy checks if a field is annotated with {@link annotations.Range}
 * and validates that the numeric value falls within the specified minimum and
 * maximum bounds (inclusive). The validation is only performed if the value is
 * not {@code null}.
 * </p>
 * 
 * <p>
 * The strategy handles both {@link Number} instances and string values that can
 * be parsed to numbers. If the value cannot be converted to a number, a validation
 * error is returned.
 * </p>
 * 
 * <p><b>Example Usage:</b></p>
 * <pre>
 * // In a model class:
 * &#64;Range(min = 0.0, max = 1000000.0, message = "Amount must be between 0 and 1,000,000")
 * &#64;Column(index = 2, name = "amount")
 * private double amount;
 * 
 * // Validation:
 * RangeValidationStrategy strategy = new RangeValidationStrategy();
 * String error = strategy.validate(field, -100.0); // Returns error message
 * String success = strategy.validate(field, 500.0); // Returns null
 * </pre>
 * 
 * @see annotations.Range
 * @see validation.ValidationStrategy
 * @see validation.Validator
 * @author FinTech Corp
 * @version 1.0.0
 * @since 1.0.0
 */
public class RangeValidationStrategy implements ValidationStrategy {
    
    /**
     * Validates that a numeric field value falls within the specified range.
     * 
     * @param field the field to validate (checked for {@code @Range} annotation)
     * @param value the actual value to validate (must be numeric or convertible to a number)
     * @return the error message from the annotation if the value is out of range,
     *         "Value is not a valid number" if the value cannot be converted to a number,
     *         or {@code null} if validation passes
     */
    @Override
    public String validate(Field field, Object value) {
        Range range = field.getAnnotation(Range.class);
        if (range != null && value != null) {
            double numericValue;
            
            if (value instanceof Number) {
                numericValue = ((Number) value).doubleValue();
            } else {
                try {
                    numericValue = Double.parseDouble(value.toString());
                } catch (NumberFormatException e) {
                    return "Value is not a valid number";
                }
            }
            
            if (numericValue < range.min() || numericValue > range.max()) {
                return range.message();
            }
        }
        return null;
    }
}
