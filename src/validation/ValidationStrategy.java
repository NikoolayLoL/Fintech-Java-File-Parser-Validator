package validation;

import java.lang.reflect.Field;

/**
 * Strategy interface for field-level validation operations.
 * <p>
 * This interface defines the contract for validation strategies that can be applied
 * to object fields. Each strategy validates a specific constraint type (e.g., NotNull,
 * Regex, Range) and returns an error message if validation fails, or {@code null}
 * if validation passes.
 * </p>
 * 
 * <p>
 * Implementations should:
 * </p>
 * <ul>
 *   <li>Check for the presence of a specific validation annotation</li>
 *   <li>Extract validation parameters from the annotation</li>
 *   <li>Validate the field value against those parameters</li>
 *   <li>Return a descriptive error message on failure</li>
 *   <li>Return {@code null} on success</li>
 * </ul>
 * 
 * <p><b>Example Implementation:</b></p>
 * <pre>
 * public class NotNullValidationStrategy implements ValidationStrategy {
 *     &#64;Override
 *     public String validate(Field field, Object value) {
 *         NotNull notNull = field.getAnnotation(NotNull.class);
 *         if (notNull != null &amp;&amp; value == null) {
 *             return notNull.message();
 *         }
 *         return null;
 *     }
 * }
 * </pre>
 * 
 * @see validation.NotNullValidationStrategy
 * @see validation.RegexValidationStrategy
 * @see validation.RangeValidationStrategy
 * @see validation.Validator
 * @author FinTech Corp
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ValidationStrategy {
    
    /**
     * Validates a field value against a specific constraint.
     * 
     * @param field the field to validate (contains annotation metadata)
     * @param value the actual value to validate
     * @return an error message if validation fails, or {@code null} if validation passes
     */
    String validate(Field field, Object value);
}
