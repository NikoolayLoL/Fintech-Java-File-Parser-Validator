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

/**
 * Generic file parser that converts delimited flat files into Java objects using annotations.
 * <p>
 * This class uses reflection to dynamically parse any flat file into POJOs without requiring
 * custom parsing code for each file type. The POJO class must be annotated with
 * {@link annotations.FileSource} to specify the delimiter, and fields must be annotated
 * with {@link annotations.Column} to map to specific columns.
 * </p>
 * 
 * <p><b>Supported Field Types:</b></p>
 * <ul>
 *   <li>{@link String}</li>
 *   <li>{@code int} and {@link Integer}</li>
 *   <li>{@code double} and {@link Double}</li>
 *   <li>{@code boolean} and {@link Boolean}</li>
 *   <li>{@link java.time.LocalDate}</li>
 * </ul>
 * 
 * <p><b>Example Usage:</b></p>
 * <pre>
 * GenericFileParser&lt;Transaction&gt; parser = new GenericFileParser&lt;&gt;();
 * List&lt;Transaction&gt; transactions = parser.parse("transactions.txt", Transaction.class);
 * </pre>
 * 
 * @param <T> the type of object to parse the file into
 * @author FinTech Corp
 * @version 1.0.0
 * @since 1.0.0
 */
public class GenericFileParser<T> {

    /**
     * Parses a delimited flat file into a list of objects of the specified type.
     * <p>
     * This method reads the file line-by-line, splits each line using the delimiter
     * specified in the {@code @FileSource} annotation, and populates object fields
     * using the {@code @Column} annotations. Empty lines are skipped.
     * </p>
     * 
     * @param filePath the path to the file to parse
     * @param clazz the class type to parse into (must have {@code @FileSource} annotation)
     * @return a list of parsed objects
     * @throws IOException if an I/O error occurs reading the file
     * @throws ReflectiveOperationException if reflection operations fail (e.g., no default constructor)
     * @throws ParsingException if the class is not annotated with {@code @FileSource},
     *         or if data conversion fails
     */
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

    /**
     * Parses a single line of text into an object instance.
     * <p>
     * This method splits the line by the delimiter, creates a new instance using
     * the default constructor, and populates all fields annotated with {@code @Column}.
     * </p>
     * 
     * @param line the line of text to parse
     * @param clazz the class type to instantiate
     * @param delimiter the delimiter character (e.g., "|", ",")
     * @return a new instance with fields populated from the line
     * @throws ReflectiveOperationException if instantiation or field access fails
     * @throws ParsingException if a required column index is out of bounds or conversion fails
     */
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

    /**
     * Converts a string value to the specified target type.
     * <p>
     * This method handles type conversion for all supported field types. Empty strings
     * are converted to {@code null} for object types. For primitive types, a default
     * value is returned (0 for numeric, false for boolean).
     * </p>
     * 
     * @param value the string value to convert
     * @param targetType the target type class (e.g., String.class, Integer.class)
     * @return the converted value, or null for empty strings
     * @throws ParsingException if the target type is unsupported or conversion fails
     */
    private Object convertValue(String value, Class<?> targetType) {
        if (value.isEmpty()) {
            return null;
        }
        
        try {
            return switch (targetType.getName()) {
                case "java.lang.String" -> value;
                case "int", "java.lang.Integer" -> Integer.parseInt(value);
                case "double", "java.lang.Double" -> Double.parseDouble(value);
                case "boolean", "java.lang.Boolean" -> Boolean.parseBoolean(value);
                case "java.time.LocalDate" -> LocalDate.parse(value);
                default -> throw new ParsingException("Unsupported type: " + targetType.getName());
            };
        } catch (NumberFormatException | DateTimeParseException e) {
            throw new ParsingException("Failed to convert value '" + value + "' to type " + targetType.getName(), e);
        }
    }
}
