package exceptions;

/**
 * Custom unchecked exception thrown when file parsing or data conversion fails.
 * <p>
 * This runtime exception is used by {@link parser.GenericFileParser} to signal
 * errors during file parsing, such as:
 * <ul>
 *   <li>Missing {@link annotations.FileSource} annotation</li>
 *   <li>Invalid data format (e.g., non-numeric value in numeric field)</li>
 *   <li>Unsupported field type for conversion</li>
 *   <li>Malformed data on a specific line</li>
 * </ul>
 * 
 * <p>
 * As an unchecked exception, it does not need to be declared in method signatures
 * or caught explicitly, allowing for cleaner code when parsing errors are considered
 * unrecoverable.
 * </p>
 * 
 * @author FinTech Corp
 * @version 1.0.0
 * @since 1.0.0
 */
public class ParsingException extends RuntimeException {
    
    /**
     * Constructs a new ParsingException with the specified detail message.
     * 
     * @param message the detail message explaining the parsing error
     */
    public ParsingException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new ParsingException with the specified detail message and cause.
     * <p>
     * This constructor is useful when wrapping lower-level exceptions (e.g., NumberFormatException)
     * with additional context about what was being parsed.
     * </p>
     * 
     * @param message the detail message explaining the parsing error
     * @param cause the underlying exception that caused the parsing failure
     */
    public ParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
