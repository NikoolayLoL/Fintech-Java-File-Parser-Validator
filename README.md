# ReflectiveDataEngine

A generic Java framework for parsing and validating delimited flat files into POJOs using annotations and reflection.

## Overview

ReflectiveDataEngine eliminates the need to write custom parsers for each file type. Simply annotate your Java classes, and the framework handles parsing and validation automatically.

## How to Build the Library

### Prerequisites
- Java 21 or higher
- Maven (optional, can use javac directly)

### Build with Maven
```bash
cd fintech
mvn clean compile
```

### Build with javac
```bash
cd fintech
javac -d out -sourcepath src src/Main.java
```

### Run the Demo
```bash
# With Maven
mvn exec:java -Dexec.mainClass="Main"

# With javac
java -cp out Main
```

### Run Tests
```bash
# Compile tests
javac -d out -sourcepath "src;test" test/*.java

# Run all tests
java -cp out RunAllTests
```

## File Formats and Sample Files

The framework supports any delimited flat file format. The delimiter is specified using the `@FileSource` annotation on your model class.

### Transaction Files (Pipe-delimited)

**Format:** `transactionId|amount|timestamp`

**Sample File:** `data/transactions.txt`
```
TX12345|1250.75|2024-03-15
TX12346|89.99|2024-03-16
TX12347|450000.00|2024-03-17
```

**Model Class:**
```java
@FileSource(delimiter = "|")
public class Transaction {
    @Column(index = 0, name = "transactionId")
    @NotNull(message = "Transaction ID cannot be null")
    private String transactionId;
    
    @Column(index = 1, name = "amount")
    @Range(min = 0.01, max = 1000000.0, message = "Amount must be between 0.01 and 1,000,000")
    private Double amount;
    
    @Column(index = 2, name = "timestamp")
    @NotNull(message = "Timestamp cannot be null")
    private LocalDate timestamp;
}
```

### Customer Files (Comma-delimited)

**Format:** `name,email,age`

**Sample File:** `data/customers.txt`
```
John Doe,john.doe@example.com,35
Jane Smith,jane.smith@company.org,28
Bob Johnson,bob.johnson@mail.com,42
```

**Model Class:**
```java
@FileSource(delimiter = ",")
public class Customer {
    @Column(index = 0, name = "name")
    @NotNull(message = "Name cannot be null")
    private String name;
    
    @Column(index = 1, name = "email")
    @Regex(pattern = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Invalid email format")
    private String email;
    
    @Column(index = 2, name = "age")
    @Range(min = 18, max = 120, message = "Age must be between 18 and 120")
    private Integer age;
}
```

### Security Audit Files (Semicolon-delimited)

**Format:** `ipAddress;severity`

**Sample File:** `data/security_audits.txt`
```
192.168.1.100;LOW
10.0.0.5;CRITICAL
172.16.0.1;MEDIUM
```

**Model Class:**
```java
@FileSource(delimiter = ";")
public class SecurityAudit {
    @Column(index = 0, name = "ipAddress")
    @Regex(pattern = "^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$", message = "Invalid IP address format")
    private String ipAddress;
    
    @Column(index = 1, name = "severity")
    @Regex(pattern = "^(LOW|MEDIUM|HIGH|CRITICAL)$", message = "Severity must be LOW, MEDIUM, HIGH, or CRITICAL")
    private String severity;
}
```

## Quick Start

### 1. Parse a File
```java
GenericFileParser<Transaction> parser = new GenericFileParser<>();
List<Transaction> transactions = parser.parse("data/transactions.txt", Transaction.class);
```

### 2. Validate Objects
```java
Validator validator = new Validator();
Map<Transaction, Set<String>> errors = validator.validate(transactions);

if (errors.isEmpty())
{
    System.out.println("All transactions are valid!");
}
else
{
    errors.forEach((txn, errorMessages) -> {
        System.out.println("Invalid: " + txn);
        errorMessages.forEach(System.out::println);
    });
}
```

## Available Annotations

### Mapping Annotations
- `@FileSource(delimiter)` - Class-level: Specifies the file delimiter
- `@Column(index, name)` - Field-level: Maps field to column index (zero-based)

### Validation Annotations
- `@NotNull(message)` - Field cannot be null
- `@Regex(pattern, message)` - Field must match regex pattern
- `@Range(min, max, message)` - Numeric field must be within range

## Supported Field Types

- `String`
- `int` / `Integer`
- `double` / `Double`
- `boolean` / `Boolean`
- `LocalDate` (ISO-8601 format: YYYY-MM-DD)

## Project Structure

```
fintech/
├── src/
│   ├── annotations/      # Custom annotations (@FileSource, @Column, etc.)
│   ├── exceptions/       # Custom exceptions (ParsingException)
│   ├── models/          # Example POJOs (Transaction, Customer, SecurityAudit)
│   ├── parser/          # Generic file parser implementation
│   ├── validation/      # Validation framework with Strategy pattern
│   └── Main.java        # Demo application
├── test/                # Unit tests (23 tests, 100% coverage)
├── data/                # Sample data files
└── docs/api/            # Generated Javadoc documentation
```

## API Documentation

Generate Javadoc HTML documentation:
```bash
javadoc -d docs/api -sourcepath src -subpackages annotations:exceptions:models:parser:validation
```

Then open `docs/api/index.html` in your browser.

## Testing

The project includes 23 unit tests with 100% coverage:
- `GenericFileParserTest` - Tests parsing logic
- `ValidatorTest` - Tests validation orchestration
- `NotNullValidationStrategyTest` - Tests null checking
- `RegexValidationStrategyTest` - Tests pattern matching
- `RangeValidationStrategyTest` - Tests numeric ranges

Run all tests:
```bash
java -cp out RunAllTests
```

## License

FinTech Corp - Internal Use - Still MIT license