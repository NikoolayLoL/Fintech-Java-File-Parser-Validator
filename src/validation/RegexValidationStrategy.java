package validation;

import annotations.Regex;

import java.lang.reflect.Field;
import java.util.regex.Pattern;

/**
 * Validation strategy that enforces regular expression pattern matching.
 * <p>
 * This strategy checks if a field is annotated with {@link annotations.Regex}
 * and validates that the field value matches the specified regular expression pattern.
 * The validation is only performed if the value is not {@code null}. If the pattern
 * does not match, the error message specified in the annotation is returned.
 * </p>
 * 
 * <p><b>Example Usage:</b></p>
 * <pre>
 * // In a model class:
 * &#64;Regex(pattern = "^[A-Z]{2}\\d{4}$", message = "Invalid account format")
 * &#64;Column(index = 1, name = "accountNumber")
 * private String accountNumber;
 * 
 * // Validation:
 * RegexValidationStrategy strategy = new RegexValidationStrategy();
 * String error = strategy.validate(field, "12ABC"); // Returns error message
 * String success = strategy.validate(field, "AB1234"); // Returns null
 * </pre>
 * 
 * @see annotations.Regex
 * @see validation.ValidationStrategy
 * @see validation.Validator
 * @author FinTech Corp
 * @version 1.0.0
 * @since 1.0.0
 */
public class RegexValidationStrategy implements ValidationStrategy {
    
    /**
     * Validates that a field value matches the specified regular expression pattern.
     * 
     * @param field the field to validate (checked for {@code @Regex} annotation)
     * @param value the actual value to validate (converted to string for pattern matching)
     * @return the error message from the annotation if the pattern doesn't match, otherwise {@code null}
     */
    @Override
    public String validate(Field field, Object value) {
        Regex regex = field.getAnnotation(Regex.class);
        if (regex != null && value != null) {
            String stringValue = value.toString();
            Pattern pattern = Pattern.compile(regex.pattern());
            if (!pattern.matcher(stringValue).matches()) {
                return regex.message();
            }
        }
        return null;
    }
}
