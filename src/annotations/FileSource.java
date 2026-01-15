package annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify the delimiter used in a flat file source.
 * <p>
 * This class-level annotation must be present on any POJO that will be parsed
 * by the {@link parser.GenericFileParser}. It defines the character(s) used
 * to separate columns in the source file.
 * </p>
 * 
 * <p><b>Example Usage:</b></p>
 * <pre>
 * &#64;FileSource(delimiter = "|")
 * public class Transaction {
 *     // fields...
 * }
 * </pre>
 * 
 * @author FinTech Corp
 * @version 1.0.0
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FileSource {
    
    /**
     * The delimiter character(s) used to separate columns in the file.
     * <p>
     * Common delimiters include:
     * <ul>
     *   <li>Pipe: "|"</li>
     *   <li>Comma: ","</li>
     *   <li>Semicolon: ";"</li>
     *   <li>Tab: "\t"</li>
     * </ul>
     * 
     * @return the delimiter string
     */
    String delimiter();
}
