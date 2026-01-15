package validation;

import annotations.Range;

import java.lang.reflect.Field;

public class RangeValidationStrategy implements ValidationStrategy {
    
    @Override
    public String validate(Field field, Object value) {
        Range range = field.getAnnotation(Range.class);
        if (range != null && value != null) {
            double numericValue;
            
            if (value instanceof Number) {
                numericValue = ((Number) value).doubleValue();
            } else {
                try {
                    numericValue = Double.parseDouble(value.toString());
                } catch (NumberFormatException e) {
                    return "Value is not a valid number";
                }
            }
            
            if (numericValue < range.min() || numericValue > range.max()) {
                return range.message();
            }
        }
        return null;
    }
}
