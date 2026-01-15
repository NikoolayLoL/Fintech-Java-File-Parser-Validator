package validation;

import java.lang.reflect.Field;

public interface ValidationStrategy {
    String validate(Field field, Object value);
}
