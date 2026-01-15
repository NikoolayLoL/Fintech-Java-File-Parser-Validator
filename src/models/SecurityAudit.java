package models;

import annotations.*;

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
