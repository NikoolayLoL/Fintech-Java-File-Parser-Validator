package validation;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Orchestrates validation of Java objects using multiple validation strategies.
 * <p>
 * This class applies the Strategy pattern to validate objects based on field-level
 * annotations. It supports multiple validation types (NotNull, Regex, Range) by
 * delegating to registered {@link ValidationStrategy} implementations.
 * </p>
 * 
 * <p>
 * The validator processes each object in a list, checks all fields, and collects
 * validation errors. Objects that pass all validations are not included in the result map.
 * </p>
 * 
 * <p><b>Example Usage:</b></p>
 * <pre>
 * Validator validator = new Validator();
 * List&lt;Transaction&gt; transactions = parser.parse("transactions.txt", Transaction.class);
 * Map&lt;Transaction, Set&lt;String&gt;&gt; errors = validator.validate(transactions);
 * 
 * if (errors.isEmpty()) {
 *     System.out.println("All transactions are valid");
 * } else {
 *     errors.forEach((tx, msgs) -&gt; {
 *         System.out.println("Invalid transaction: " + tx);
 *         msgs.forEach(System.out::println);
 *     });
 * }
 * </pre>
 * 
 * @see ValidationStrategy
 * @see NotNullValidationStrategy
 * @see RegexValidationStrategy
 * @see RangeValidationStrategy
 * @author FinTech Corp
 * @version 1.0.0
 * @since 1.0.0
 */
public class Validator {
    
    private final List<ValidationStrategy> strategies;

    /**
     * Creates a new Validator with all standard validation strategies registered.
     * <p>
     * The following strategies are automatically registered:
     * </p>
     * <ul>
     *   <li>{@link NotNullValidationStrategy} - validates {@code @NotNull} annotations</li>
     *   <li>{@link RegexValidationStrategy} - validates {@code @Regex} annotations</li>
     *   <li>{@link RangeValidationStrategy} - validates {@code @Range} annotations</li>
     * </ul>
     */
    public Validator() {
        this.strategies = new ArrayList<>();
        strategies.add(new NotNullValidationStrategy());
        strategies.add(new RegexValidationStrategy());
        strategies.add(new RangeValidationStrategy());
    }

    /**
     * Validates a list of objects and returns all validation errors.
     * <p>
     * Each object is validated against all registered strategies. Objects with
     * no errors are excluded from the result. Error messages are formatted as
     * "fieldName: error message".
     * </p>
     * 
     * @param <T> the type of objects to validate
     * @param objects the list of objects to validate
     * @return a map of invalid objects to their validation error messages
     *         (empty map if all objects are valid)
     */
    public <T> Map<T, Set<String>> validate(List<T> objects) {
        Map<T, Set<String>> validationErrors = new HashMap<>();
        
        for (T object : objects) {
            Set<String> errors = validateObject(object);
            if (!errors.isEmpty()) {
                validationErrors.put(object, errors);
            }
        }
        
        return validationErrors;
    }

    /**
     * Validates a single object by checking all its fields against all strategies.
     * 
     * @param <T> the type of object to validate
     * @param object the object to validate
     * @return a set of validation error messages (empty if object is valid)
     */
    private <T> Set<String> validateObject(T object) {
        Set<String> errors = new HashSet<>();
        
        Field[] fields = object.getClass().getDeclaredFields();
        
        for (Field field : fields) {
            field.setAccessible(true);
            
            Object value;
            try {
                value = field.get(object);
            } catch (IllegalAccessException e) {
                errors.add("Unable to access field: " + field.getName());
                continue;
            }
            
            for (ValidationStrategy strategy : strategies) {
                String error = strategy.validate(field, value);
                if (error != null) {
                    errors.add(field.getName() + ": " + error);
                }
            }
        }
        
        return errors;
    }
}
