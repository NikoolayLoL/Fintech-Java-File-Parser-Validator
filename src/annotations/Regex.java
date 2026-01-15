package annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validation annotation to enforce that a field's string representation matches a regular expression pattern.
 * <p>
 * This field-level annotation is used by {@link validation.Validator} to
 * validate that the field value conforms to the specified regex pattern.
 * </p>
 * 
 * <p><b>Example Usage:</b></p>
 * <pre>
 * &#64;Regex(pattern = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Invalid email format")
 * private String email;
 * </pre>
 * 
 * @author FinTech Corp
 * @version 1.0.0
 * @since 1.0.0
 * @see validation.RegexValidationStrategy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Regex {
    
    /**
     * The regular expression pattern that the field value must match.
     * <p>
     * Uses standard Java {@link java.util.regex.Pattern} syntax.
     * </p>
     * 
     * @return the regex pattern
     */
    String pattern();
    
    /**
     * The error message to return if validation fails.
     * 
     * @return the validation error message
     */
    String message() default "Value does not match required pattern";
}
