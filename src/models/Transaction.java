package models;

import annotations.*;

import java.time.LocalDate;

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
