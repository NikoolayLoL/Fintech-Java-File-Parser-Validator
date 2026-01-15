package parser;

import annotations.Column;
import annotations.FileSource;
import exceptions.ParsingException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class GenericFileParser<T> {

    public List<T> parse(String filePath, Class<T> clazz) throws IOException, ReflectiveOperationException {
        List<T> results = new ArrayList<>();
        
        FileSource fileSource = clazz.getAnnotation(FileSource.class);
        if (fileSource == null) {
            throw new ParsingException("Class " + clazz.getName() + " must be annotated with @FileSource");
        }
        
        String delimiter = fileSource.delimiter();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                try {
                    T instance = parseLine(line, clazz, delimiter);
                    results.add(instance);
                } catch (Exception e) {
                    throw new ParsingException("Error parsing line " + lineNumber + ": " + line, e);
                }
            }
        }
        
        return results;
    }

    private T parseLine(String line, Class<T> clazz, String delimiter) throws ReflectiveOperationException {
        String[] columns = line.split("\\" + delimiter, -1);
        
        Constructor<T> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        T instance = constructor.newInstance();
        
        Field[] fields = clazz.getDeclaredFields();
        
        for (Field field : fields) {
            Column columnAnnotation = field.getAnnotation(Column.class);
            
            if (columnAnnotation != null) {
                int index = columnAnnotation.index();
                
                if (index >= 0 && index < columns.length) {
                    String value = columns[index].trim();
                    
                    field.setAccessible(true);
                    Object convertedValue = convertValue(value, field.getType());
                    field.set(instance, convertedValue);
                }
            }
        }
        
        return instance;
    }

    private Object convertValue(String value, Class<?> targetType) {
        if (value.isEmpty()) {
            return null;
        }
        
        try {
            if (targetType == String.class) {
                return value;
            } else if (targetType == int.class || targetType == Integer.class) {
                return Integer.parseInt(value);
            } else if (targetType == double.class || targetType == Double.class) {
                return Double.parseDouble(value);
            } else if (targetType == boolean.class || targetType == Boolean.class) {
                return Boolean.parseBoolean(value);
            } else if (targetType == LocalDate.class) {
                return LocalDate.parse(value);
            } else {
                throw new ParsingException("Unsupported type: " + targetType.getName());
            }
        } catch (NumberFormatException | DateTimeParseException e) {
            throw new ParsingException("Failed to convert value '" + value + "' to type " + targetType.getName(), e);
        }
    }
}
