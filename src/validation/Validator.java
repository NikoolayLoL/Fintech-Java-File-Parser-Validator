package validation;

import java.lang.reflect.Field;
import java.util.*;

public class Validator {
    
    private final List<ValidationStrategy> strategies;

    public Validator() {
        this.strategies = new ArrayList<>();
        strategies.add(new NotNullValidationStrategy());
        strategies.add(new RegexValidationStrategy());
        strategies.add(new RangeValidationStrategy());
    }

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
