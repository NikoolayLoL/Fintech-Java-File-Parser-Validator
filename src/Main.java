import models.Transaction;
import models.Customer;
import models.SecurityAudit;
import parser.GenericFileParser;
import validation.Validator;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Main {
    
    public static void main(String[] args) {
        demonstrateTransactionParsing();
        System.out.println("\n" + "=".repeat(80) + "\n");
        demonstrateCustomerParsing();
        System.out.println("\n" + "=".repeat(80) + "\n");
        demonstrateSecurityAuditParsing();
    }

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
                System.out.println("\nAll security audit records are valid!");
            } else {
                System.out.println("\nValidation errors found:");
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
