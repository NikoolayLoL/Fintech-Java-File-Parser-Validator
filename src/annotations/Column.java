package annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to map a field to a specific column index in a delimited file.
 * <p>
 * This field-level annotation is used by {@link parser.GenericFileParser} to
 * determine which column in the source file should populate this field.
 * Column indices are zero-based.
 * </p>
 * 
 * <p><b>Example Usage:</b></p>
 * <pre>
 * &#64;Column(index = 0, name = "transactionId")
 * private String transactionId;
 * </pre>
 * 
 * @author FinTech Corp
 * @version 1.0.0
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
    
    /**
     * The zero-based index of the column in the file.
     * <p>
     * For example, in the line "A|B|C", index 0 is "A", index 1 is "B", and index 2 is "C".
     * </p>
     * 
     * @return the column index
     */
    int index();
    
    /**
     * The logical name of the column for documentation purposes.
     * <p>
     * This is not used by the parser but helps with code readability.
     * </p>
     * 
     * @return the column name
     */
    String name();
}
