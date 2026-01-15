package models;

import annotations.*;

/**
 * Model class representing a security audit log entry from a semicolon-delimited file.
 * <p>
 * This class demonstrates complex regular expression validation for both IP addresses
 * and enumerated severity levels. It's suitable for parsing security monitoring and
 * audit trail files.
 * </p>
 * 
 * <p><b>File Format:</b></p>
 * <pre>
 * ipAddress;severity
 * 192.168.1.100;LOW
 * 10.0.0.5;CRITICAL
 * 172.16.0.1;MEDIUM
 * </pre>
 * 
 * <p><b>Validation Rules:</b></p>
 * <ul>
 *   <li>IP Address: Required, must be valid IPv4 format (e.g., 192.168.1.1)</li>
 *   <li>Severity: Required, must be one of: LOW, MEDIUM, HIGH, or CRITICAL</li>
 * </ul>
 * 
 * @see annotations.FileSource
 * @see annotations.Column
 * @see annotations.NotNull
 * @see annotations.Regex
 * @author FinTech Corp
 * @version 1.0.0
 * @since 1.0.0
 */
@FileSource(delimiter = ";")
public class SecurityAudit {
    
    @Column(index = 0, name = "ipAddress")
    @NotNull(message = "IP address cannot be null")
    @Regex(pattern = "^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$", message = "Invalid IP address format")
    private String ipAddress;
    
    @Column(index = 1, name = "severity")
    @NotNull(message = "Severity cannot be null")
    @Regex(pattern = "^(LOW|MEDIUM|HIGH|CRITICAL)$", message = "Severity must be LOW, MEDIUM, HIGH, or CRITICAL")
    private String severity;

    @Override
    public String toString() {
        return "SecurityAudit{" +
                "ipAddress='" + ipAddress + '\'' +
                ", severity='" + severity + '\'' +
                '}';
    }
}
