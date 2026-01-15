package models;

import annotations.*;

import java.time.LocalDate;

/**
 * Model class representing a financial transaction record from a pipe-delimited file.
 * <p>
 * This class demonstrates the use of parsing and validation annotations for a typical
 * fintech transaction file. Each field is mapped to a column in the file and validated
 * according to business rules.
 * </p>
 * 
 * <p><b>File Format:</b></p>
 * <pre>
 * transactionId|amount|timestamp
 * TX12345|1250.75|2024-03-15
 * TX12346|89.99|2024-03-16
 * </pre>
 * 
 * <p><b>Validation Rules:</b></p>
 * <ul>
 *   <li>Transaction ID: Required (cannot be null)</li>
 *   <li>Amount: Required, must be between 0.01 and 1,000,000</li>
 *   <li>Timestamp: Required (cannot be null)</li>
 * </ul>
 * 
 * @see annotations.FileSource
 * @see annotations.Column
 * @see annotations.NotNull
 * @see annotations.Range
 * @author FinTech Corp
 * @version 1.0.0
 * @since 1.0.0
 */
@FileSource(delimiter = "|")
public class Transaction {
    
    @Column(index = 0, name = "transactionId")
    @NotNull(message = "Transaction ID cannot be null")
    private String transactionId;
    
    @Column(index = 1, name = "amount")
    @NotNull(message = "Amount cannot be null")
    @Range(min = 0.01, max = 1000000.0, message = "Amount must be between 0.01 and 1,000,000")
    private Double amount;
    
    @Column(index = 2, name = "timestamp")
    @NotNull(message = "Timestamp cannot be null")
    private LocalDate timestamp;

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", amount=" + amount +
                ", timestamp=" + timestamp +
                '}';
    }
}
