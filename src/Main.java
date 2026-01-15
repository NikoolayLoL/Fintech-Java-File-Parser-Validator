import models.Transaction;
import models.Customer;
import models.SecurityAudit;
import parser.GenericFileParser;
import validation.Validator;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Demo application showcasing the generic file parser and validation framework.
 * <p>
 * This class demonstrates parsing and validating three different file types:
 * </p>
 * <ul>
 *   <li>Pipe-delimited transactions</li>
 *   <li>Comma-delimited customer records</li>
 *   <li>Semicolon-delimited security audit logs</li>
 * </ul>
 * 
 * <p>
 * Each demonstration method parses a sample data file using {@link parser.GenericFileParser},
 * validates the parsed objects using {@link validation.Validator}, and displays the results
 * including any validation errors.
 * </p>
 * 
 * <p><b>Usage:</b></p>
 * <pre>
 * mvn compile
 * mvn exec:java -Dexec.mainClass="Main"
 * </pre>
 * 
 * @author FinTech Corp
 * @version 1.0.0
 * @since 1.0.0
 */
public class Main {
    
    /**
     * Main entry point that runs all three parsing demonstrations.
     * 
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        demonstrateTransactionParsing();
        System.out.println("\n" + "=".repeat(80) + "\n");
        demonstrateCustomerParsing();
        System.out.println("\n" + "=".repeat(80) + "\n");
        demonstrateSecurityAuditParsing();
    }

    /**
     * Demonstrates parsing and validating pipe-delimited transaction files.
     * <p>
     * This method:
     * </p>
     * <ol>
     *   <li>Parses {@code data/transactions.txt} into {@link models.Transaction} objects</li>
     *   <li>Validates amounts are within range (0.01 to 1,000,000)</li>
     *   <li>Validates all required fields are present</li>
     *   <li>Displays parsing results and any validation errors</li>
     * </ol>
     */
    private static void demonstrateTransactionParsing() {
        System.out.println("=== Transaction Parsing Demo ===\n");
        
        GenericFileParser<Transaction> parser = new GenericFileParser<>();
        
        try {
            List<Transaction> transactions = parser.parse("data/transactions.txt", Transaction.class);
            
            System.out.println("Parsed " + transactions.size() + " transactions:");
            for (Transaction txn : transactions) {
                System.out.println("  " + txn);
            }
            
            Validator validator = new Validator();
            Map<Transaction, Set<String>> errors = validator.validate(transactions);
            
            if (errors.isEmpty()) {
                System.out.println("\nAll transactions are valid!");
            } else {
                System.out.println("\nValidation errors found:");
                errors.forEach((txn, errs) -> {
                    System.out.println("  " + txn);
                    errs.forEach(err -> System.out.println("    - " + err));
                });
            }
        } catch (IOException | ReflectiveOperationException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * Demonstrates parsing and validating comma-delimited customer files.
     * <p>
     * This method:
     * </p>
     * <ol>
     *   <li>Parses {@code data/customers.txt} into {@link models.Customer} objects</li>
     *   <li>Validates email addresses match the standard email pattern</li>
     *   <li>Validates ages are within range (18 to 120)</li>
     *   <li>Validates all required fields are present</li>
     *   <li>Displays parsing results and any validation errors</li>
     * </ol>
     */
    private static void demonstrateCustomerParsing() {
        System.out.println("=== Customer Parsing Demo ===\n");
        
        GenericFileParser<Customer> parser = new GenericFileParser<>();
        
        try {
            List<Customer> customers = parser.parse("data/customers.txt", Customer.class);
            
            System.out.println("Parsed " + customers.size() + " customers:");
            for (Customer customer : customers) {
                System.out.println("  " + customer);
            }
            
            Validator validator = new Validator();
            Map<Customer, Set<String>> errors = validator.validate(customers);
            
            if (errors.isEmpty()) {
                System.out.println("\nAll customers are valid!");
            } else {
                System.out.println("\nValidation errors found:");
                errors.forEach((customer, errs) -> {
                    System.out.println("  " + customer);
                    errs.forEach(err -> System.out.println("    - " + err));
                });
            }
        } catch (IOException | ReflectiveOperationException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * Demonstrates parsing and validating semicolon-delimited security audit files.
     * <p>
     * This method:
     * </p>
     * <ol>
     *   <li>Parses {@code data/security_audits.txt} into {@link models.SecurityAudit} objects</li>
     *   <li>Validates IP addresses match IPv4 format</li>
     *   <li>Validates severity levels are one of: LOW, MEDIUM, HIGH, or CRITICAL</li>
     *   <li>Validates all required fields are present</li>
     *   <li>Displays parsing results and any validation errors</li>
     * </ol>
     */
    private static void demonstrateSecurityAuditParsing() {
        System.out.println("=== Security Audit Parsing Demo ===\n");
        
        GenericFileParser<SecurityAudit> parser = new GenericFileParser<>();
        
        try {
            List<SecurityAudit> audits = parser.parse("data/security_audits.txt", SecurityAudit.class);
            
            System.out.println("Parsed " + audits.size() + " security audit records:");
            for (SecurityAudit audit : audits) {
                System.out.println("  " + audit);
            }
            
            Validator validator = new Validator();
            Map<SecurityAudit, Set<String>> errors = validator.validate(audits);
            
            if (errors.isEmpty()) {
                System.out.println("\n✓ All security audit records are valid!");
            } else {
                System.out.println("\n✗ Validation errors found:");
                errors.forEach((audit, errs) -> {
                    System.out.println("  " + audit);
                    errs.forEach(err -> System.out.println("    - " + err));
                });
            }
        } catch (IOException | ReflectiveOperationException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
