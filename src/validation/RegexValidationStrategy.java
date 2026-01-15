package validation;

import annotations.Regex;

import java.lang.reflect.Field;
import java.util.regex.Pattern;

public class RegexValidationStrategy implements ValidationStrategy {
    
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
