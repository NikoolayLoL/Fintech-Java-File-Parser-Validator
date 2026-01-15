package annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validation annotation to ensure a field value is not null.
 * <p>
 * This field-level annotation is used by {@link validation.Validator} to
 * enforce that a field must have a non-null value after parsing.
 * </p>
 * 
 * <p><b>Example Usage:</b></p>
 * <pre>
 * &#64;NotNull(message = "Transaction ID cannot be null")
 * private String transactionId;
 * </pre>
 * 
 * @author FinTech Corp
 * @version 1.0.0
 * @since 1.0.0
 * @see validation.NotNullValidationStrategy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NotNull {
    
    /**
     * The error message to return if validation fails.
     * 
     * @return the validation error message
     */
    String message() default "Field cannot be null";
}
