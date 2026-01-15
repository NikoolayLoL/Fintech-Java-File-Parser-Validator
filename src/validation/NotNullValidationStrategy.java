package validation;

import annotations.NotNull;

import java.lang.reflect.Field;

public class NotNullValidationStrategy implements ValidationStrategy {
    
    @Override
    public String validate(Field field, Object value) {
        NotNull notNull = field.getAnnotation(NotNull.class);
        if (notNull != null && value == null) {
            return notNull.message();
        }
        return null;
    }
}
