package validation;

import annotations.NotNull;

import java.lang.reflect.Field;

/**
 * Validation strategy that enforces non-null field values.
 * <p>
 * This strategy checks if a field is annotated with {@link annotations.NotNull}
 * and validates that the field value is not {@code null}. If the value is {@code null},
 * the error message specified in the annotation is returned.
 * </p>
 * 
 * <p><b>Example Usage:</b></p>
 * <pre>
 * // In a model class:
 * &#64;NotNull(message = "Transaction ID cannot be null")
 * &#64;Column(index = 0, name = "transactionId")
 * private String transactionId;
 * 
 * // Validation:
 * NotNullValidationStrategy strategy = new NotNullValidationStrategy();
 * String error = strategy.validate(field, null); // Returns the error message
 * String success = strategy.validate(field, "TX123"); // Returns null
 * </pre>
 * 
 * @see annotations.NotNull
 * @see validation.ValidationStrategy
 * @see validation.Validator
 * @author FinTech Corp
 * @version 1.0.0
 * @since 1.0.0
 */
public class NotNullValidationStrategy implements ValidationStrategy {
    
    /**
     * Validates that a field value is not null.
     * 
     * @param field the field to validate (checked for {@code @NotNull} annotation)
     * @param value the actual value to validate
     * @return the error message from the annotation if value is null, otherwise {@code null}
     */
    @Override
    public String validate(Field field, Object value) {
        NotNull notNull = field.getAnnotation(NotNull.class);
        if (notNull != null && value == null) {
            return notNull.message();
        }
        return null;
    }
}
