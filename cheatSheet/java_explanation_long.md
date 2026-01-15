# ReflectiveDataEngine - Complete Technical Guide

*An in-depth explanation for developers new to Java*

---

## Table of Contents

1. [Introduction](#introduction)
2. [The Problem We're Solving](#the-problem-were-solving)
3. [Understanding Java Basics](#understanding-java-basics)
4. [Core Concept: Annotations](#core-concept-annotations)
5. [Core Concept: Reflection](#core-concept-reflection)
6. [Architecture Overview](#architecture-overview)
7. [The Parsing Engine](#the-parsing-engine)
8. [The Validation Framework](#the-validation-framework)
9. [Design Patterns Used](#design-patterns-used)
10. [Complete Code Walkthrough](#complete-code-walkthrough)
11. [Error Handling](#error-handling)
12. [Performance Considerations](#performance-considerations)
13. [Extending the Framework](#extending-the-framework)

---

## Introduction

This document explains the **ReflectiveDataEngine** framework—a system that converts text files into Java objects and validates them automatically. If you're new to Java or programming concepts like reflection and design patterns, this guide will walk you through everything step by step.

---

## The Problem We're Solving

### Real-World Scenario

A financial company receives data files every night from legacy systems:

**transactions.txt** (pipe-delimited):
```
TX12345|1250.75|2024-03-15
TX12346|89.99|2024-03-16
```

**customers.txt** (comma-delimited):
```
John Doe,john.doe@example.com,35
Jane Smith,jane.smith@company.org,28
```

**security_audits.txt** (semicolon-delimited):
```
192.168.1.100;LOW
10.0.0.5;CRITICAL
```

### The Traditional Solution (What We DON'T Want)

You'd write separate parsing code for each file type:

```java
// TransactionParser.java
public class TransactionParser {
    public List<Transaction> parse(String filePath) throws IOException {
        List<Transaction> transactions = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\\|");
            Transaction tx = new Transaction();
            tx.setTransactionId(parts[0]);
            tx.setAmount(Double.parseDouble(parts[1]));
            tx.setTimestamp(LocalDate.parse(parts[2]));
            transactions.add(tx);
        }
        
        reader.close();
        return transactions;
    }
}

// CustomerParser.java
public class CustomerParser {
    public List<Customer> parse(String filePath) throws IOException {
        // 50 more lines of similar code...
    }
}

// SecurityAuditParser.java
public class SecurityAuditParser {
    public List<SecurityAudit> parse(String filePath) throws IOException {
        // 50 more lines of similar code...
    }
}
```

**Problems:**
- Repetitive code (file reading, splitting, conversion)
- Hard to maintain (change the logic in one place, must change everywhere)
- Not scalable (new file type = write another parser)

### Our Solution (What We WANT)

One **generic parser** that works for all file types:

```java
GenericFileParser<Transaction> parser = new GenericFileParser<>();
List<Transaction> transactions = parser.parse("transactions.txt", Transaction.class);

// Same parser, different class
List<Customer> customers = parser.parse("customers.txt", Customer.class);
List<SecurityAudit> audits = parser.parse("audits.txt", SecurityAudit.class);
```

How? **Annotations + Reflection + Generic Programming**

---

## Understanding Java Basics

Before diving into the framework, let's establish some fundamentals.

### Classes and Objects

**Class** = Blueprint  
**Object** = Instance of that blueprint

```java
// Class (blueprint)
public class Car {
    private String model;
    private int year;
}

// Objects (instances)
Car car1 = new Car();  // First car
Car car2 = new Car();  // Second car
```

### Fields (Member Variables)

Data stored in an object.

```java
public class Customer {
    private String name;     // Field
    private String email;    // Field
    private Integer age;     // Field
}
```

### Access Modifiers

- `public`: Anyone can access
- `private`: Only this class can access
- `protected`: This class and subclasses can access

```java
public class Example {
    public String publicField = "visible everywhere";
    private String privateField = "only visible inside this class";
}
```

### Generics

Generics let you write code that works with any type.

**Without generics:**
```java
public List<String> parseStrings() { ... }
public List<Integer> parseIntegers() { ... }
public List<Customer> parseCustomers() { ... }
```

**With generics:**
```java
public <T> List<T> parse(Class<T> clazz) { ... }
// Works for any type T
```

---

## Core Concept: Annotations

### What Are Annotations?

Annotations are **metadata** you attach to code. They're like labels or tags that other code can read.

**Think of them like this:**
- A book has metadata: title, author, ISBN
- Java code can have metadata: `@Override`, `@Deprecated`, custom annotations

### Built-in Annotations

```java
@Override  // Tells compiler: "This overrides a parent method"
public String toString() {
    return "Custom string";
}

@Deprecated  // Tells developers: "Don't use this anymore"
public void oldMethod() {
    // ...
}
```

### Creating Custom Annotations

Our framework uses custom annotations:

```java
@Retention(RetentionPolicy.RUNTIME)  // Keep annotation at runtime
@Target(ElementType.TYPE)  // Can be applied to classes
public @interface FileSource {
    String delimiter();  // Parameter: what delimiter to use
}
```

### How to Use Them

```java
@FileSource(delimiter = "|")  // Apply annotation to class
public class Transaction {
    // ...
}
```

### Reading Annotations at Runtime

```java
// Ask the class: "Do you have @FileSource annotation?"
FileSource annotation = Transaction.class.getAnnotation(FileSource.class);

if (annotation != null) {
    String delimiter = annotation.delimiter();  // Get the delimiter value
    System.out.println("Delimiter: " + delimiter);  // Output: Delimiter: |
}
```

**Key Point:** Annotations don't do anything by themselves. Other code must read them and act accordingly.

---

## Core Concept: Reflection

### What Is Reflection?

Reflection is Java's ability to inspect and manipulate classes, methods, and fields **at runtime**.

**Normal code (compile-time):**
```java
Customer customer = new Customer();
customer.setName("John");  // You write this in your code
```

**Reflection (runtime):**
```java
Class<?> clazz = Class.forName("Customer");  // Load class by name
Object customer = clazz.getDeclaredConstructor().newInstance();  // Create instance

Field field = clazz.getDeclaredField("name");  // Get field by name
field.setAccessible(true);  // Allow access to private field
field.set(customer, "John");  // Set the value
```

### Why Use Reflection?

When you write a **generic parser**, you don't know what classes it will parse. Reflection lets you work with any class dynamically.

### Key Reflection Operations

#### 1. Getting a Class Object

```java
// Three ways to get a Class object
Class<?> c1 = Customer.class;  // If you know the class name
Class<?> c2 = customer.getClass();  // If you have an instance
Class<?> c3 = Class.forName("models.Customer");  // Load by string name
```

#### 2. Creating Instances

```java
// Get the no-argument constructor
Constructor<?> constructor = clazz.getDeclaredConstructor();
constructor.setAccessible(true);

// Create a new instance
Object instance = constructor.newInstance();
```

#### 3. Getting Fields

```java
// Get all fields (even private ones)
Field[] fields = clazz.getDeclaredFields();

for (Field field : fields) {
    System.out.println("Field name: " + field.getName());
    System.out.println("Field type: " + field.getType());
}
```

#### 4. Reading Annotations from Fields

```java
Field field = clazz.getDeclaredField("email");
Regex regexAnnotation = field.getAnnotation(Regex.class);

if (regexAnnotation != null) {
    String pattern = regexAnnotation.pattern();
    System.out.println("Email must match: " + pattern);
}
```

#### 5. Setting Field Values

```java
Field field = clazz.getDeclaredField("name");
field.setAccessible(true);  // Critical! Allows access to private fields
field.set(instance, "John Doe");  // Set the value
```

#### 6. Getting Field Values

```java
Field field = clazz.getDeclaredField("age");
field.setAccessible(true);
Object value = field.get(instance);  // Get the value
System.out.println("Age: " + value);
```

---

## Architecture Overview

The framework has 5 main packages:

### 1. `annotations` Package

Defines metadata for mapping and validation:

- `@FileSource` - Specifies file delimiter
- `@Column` - Maps field to column index
- `@NotNull` - Field cannot be null
- `@Regex` - Field must match pattern
- `@Range` - Numeric field must be in range

### 2. `exceptions` Package

Custom exception for parsing errors:

- `ParsingException` - Thrown when parsing fails

### 3. `models` Package

Example domain classes that use annotations:

- `Transaction` - Financial transaction record
- `Customer` - Customer information
- `SecurityAudit` - Security log entry

### 4. `parser` Package

The core parsing engine:

- `GenericFileParser<T>` - Converts text files to objects

### 5. `validation` Package

Validation framework using Strategy pattern:

- `ValidationStrategy` - Interface for validation rules
- `NotNullValidationStrategy` - Checks for null values
- `RegexValidationStrategy` - Checks regex patterns
- `RangeValidationStrategy` - Checks numeric ranges
- `Validator` - Orchestrates all validations

---

## The Parsing Engine

### Step-by-Step: How Parsing Works

#### Step 1: Check for @FileSource Annotation

```java
public <T> List<T> parse(String filePath, Class<T> clazz) throws IOException {
    // Get the @FileSource annotation from the class
    FileSource fileSource = clazz.getAnnotation(FileSource.class);
    
    if (fileSource == null) {
        throw new ParsingException("Class must have @FileSource annotation");
    }
    
    String delimiter = fileSource.delimiter();  // Get the delimiter (e.g., "|")
    // ...
}
```

#### Step 2: Read the File Line-by-Line

```java
List<T> results = new ArrayList<>();

try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
    String line;
    int lineNumber = 0;
    
    while ((line = reader.readLine()) != null) {
        lineNumber++;
        
        // Skip empty lines
        if (line.trim().isEmpty()) {
            continue;
        }
        
        // Parse this line into an object
        T instance = parseLine(line, clazz, delimiter);
        results.add(instance);
    }
}

return results;
```

#### Step 3: Parse a Single Line

```java
private <T> T parseLine(String line, Class<T> clazz, String delimiter) {
    // Split the line by delimiter
    // Special handling: "\\" escapes special regex characters like "|"
    // "-1" keeps empty trailing fields
    String[] columns = line.split("\\\\" + delimiter, -1);
    
    // Create a new instance of T
    Constructor<T> constructor = clazz.getDeclaredConstructor();
    constructor.setAccessible(true);
    T instance = constructor.newInstance();
    
    // Get all fields from the class
    Field[] fields = clazz.getDeclaredFields();
    
    for (Field field : fields) {
        // Check if field has @Column annotation
        Column columnAnnotation = field.getAnnotation(Column.class);
        
        if (columnAnnotation != null) {
            int index = columnAnnotation.index();  // Get column index
            
            // Make sure the index is valid
            if (index >= 0 && index < columns.length) {
                String value = columns[index].trim();  // Get string value
                
                // Convert string to the field's type
                Object convertedValue = convertValue(value, field.getType());
                
                // Set the value
                field.setAccessible(true);
                field.set(instance, convertedValue);
            }
        }
    }
    
    return instance;
}
```

#### Step 4: Type Conversion

```java
private Object convertValue(String value, Class<?> targetType) {
    if (value.isEmpty()) {
        return null;
    }
    
    try {
        return switch (targetType.getName()) {
            case "java.lang.String" -> value;
            case "int", "java.lang.Integer" -> Integer.parseInt(value);
            case "double", "java.lang.Double" -> Double.parseDouble(value);
            case "boolean", "java.lang.Boolean" -> Boolean.parseBoolean(value);
            case "java.time.LocalDate" -> LocalDate.parse(value);  // Expects ISO format: 2024-01-15
            default -> throw new ParsingException("Unsupported type: " + targetType.getName());
        };
    } catch (NumberFormatException | DateTimeParseException e) {
        throw new ParsingException("Failed to convert '" + value + "' to " + targetType.getName(), e);
    }
}
```

### Example Walkthrough

**File:** `transactions.txt`
```
TX12345|1250.75|2024-03-15
```

**Class:**
```java
@FileSource(delimiter = "|")
public class Transaction {
    @Column(index = 0)
    private String transactionId;
    
    @Column(index = 1)
    private Double amount;
    
    @Column(index = 2)
    private LocalDate timestamp;
}
```

**Parsing Process:**

1. **Read annotation:** `delimiter = "|"`
2. **Read line:** `"TX12345|1250.75|2024-03-15"`
3. **Split line:** `["TX12345", "1250.75", "2024-03-15"]`
4. **Create instance:** `Transaction tx = new Transaction();`
5. **Process fields:**
   - Field `transactionId` has `@Column(index = 0)`
     - Get value at index 0: `"TX12345"`
     - Type is `String`, no conversion needed
     - Set: `tx.transactionId = "TX12345"`
   - Field `amount` has `@Column(index = 1)`
     - Get value at index 1: `"1250.75"`
     - Type is `Double`, convert: `Double.parseDouble("1250.75")` → `1250.75`
     - Set: `tx.amount = 1250.75`
   - Field `timestamp` has `@Column(index = 2)`
     - Get value at index 2: `"2024-03-15"`
     - Type is `LocalDate`, convert: `LocalDate.parse("2024-03-15")`
     - Set: `tx.timestamp = LocalDate(2024, 3, 15)`
6. **Return:** `tx`

---

## The Validation Framework

### Strategy Pattern Overview

Instead of one giant validation method, we break it into separate **strategies**:

```java
// Interface: all strategies must implement this
public interface ValidationStrategy {
    String validate(Field field, Object value);
    // Returns: error message if invalid, null if valid
}
```

### Strategy 1: NotNull Validation

```java
public class NotNullValidationStrategy implements ValidationStrategy {
    @Override
    public String validate(Field field, Object value) {
        // Check if field has @NotNull annotation
        NotNull notNull = field.getAnnotation(NotNull.class);
        
        if (notNull != null) {  // Annotation exists
            if (value == null) {  // Value is null
                return notNull.message();  // Return error message
            }
        }
        
        return null;  // Valid (no error)
    }
}
```

**Example:**
```java
@Column(index = 0)
@NotNull(message = "Transaction ID cannot be null")
private String transactionId;

// If transactionId is null:
// Returns: "Transaction ID cannot be null"
```

### Strategy 2: Regex Validation

```java
public class RegexValidationStrategy implements ValidationStrategy {
    @Override
    public String validate(Field field, Object value) {
        Regex regex = field.getAnnotation(Regex.class);
        
        if (regex != null && value != null) {
            String stringValue = value.toString();
            Pattern pattern = Pattern.compile(regex.pattern());
            
            if (!pattern.matcher(stringValue).matches()) {
                return regex.message();  // Pattern didn't match
            }
        }
        
        return null;  // Valid
    }
}
```

**Example:**
```java
@Column(index = 1)
@Regex(pattern = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Invalid email format")
private String email;

// If email is "invalid-email":
// Returns: "Invalid email format"
```

### Strategy 3: Range Validation

```java
public class RangeValidationStrategy implements ValidationStrategy {
    @Override
    public String validate(Field field, Object value) {
        Range range = field.getAnnotation(Range.class);
        
        if (range != null && value != null) {
            double numericValue;
            
            // Convert to double for comparison
            if (value instanceof Number) {
                numericValue = ((Number) value).doubleValue();
            } else {
                try {
                    numericValue = Double.parseDouble(value.toString());
                } catch (NumberFormatException e) {
                    return "Value is not a valid number";
                }
            }
            
            // Check range
            if (numericValue < range.min() || numericValue > range.max()) {
                return range.message();
            }
        }
        
        return null;  // Valid
    }
}
```

**Example:**
```java
@Column(index = 2)
@Range(min = 18, max = 120, message = "Age must be between 18 and 120")
private Integer age;

// If age is 15:
// Returns: "Age must be between 18 and 120"
```

### The Validator Class (Orchestrator)

```java
public class Validator {
    private final List<ValidationStrategy> strategies;
    
    public Validator() {
        this.strategies = new ArrayList<>();
        // Register all strategies
        strategies.add(new NotNullValidationStrategy());
        strategies.add(new RegexValidationStrategy());
        strategies.add(new RangeValidationStrategy());
    }
    
    public <T> Map<T, Set<String>> validate(List<T> objects) {
        Map<T, Set<String>> validationErrors = new HashMap<>();
        
        // Check each object
        for (T object : objects) {
            Set<String> errors = validateObject(object);
            
            if (!errors.isEmpty()) {
                validationErrors.put(object, errors);
            }
        }
        
        return validationErrors;
    }
    
    private <T> Set<String> validateObject(T object) {
        Set<String> errors = new HashSet<>();
        
        // Get all fields
        Field[] fields = object.getClass().getDeclaredFields();
        
        for (Field field : fields) {
            field.setAccessible(true);
            
            // Get the field's value
            Object value;
            try {
                value = field.get(object);
            } catch (IllegalAccessException e) {
                errors.add("Unable to access field: " + field.getName());
                continue;
            }
            
            // Run all strategies on this field
            for (ValidationStrategy strategy : strategies) {
                String error = strategy.validate(field, value);
                if (error != null) {
                    errors.add(field.getName() + ": " + error);
                }
            }
        }
        
        return errors;
    }
}
```

### Complete Validation Example

**Class:**
```java
@FileSource(delimiter = ",")
public class Customer {
    @Column(index = 0)
    @NotNull(message = "Name required")
    private String name;
    
    @Column(index = 1)
    @Regex(pattern = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Invalid email")
    private String email;
    
    @Column(index = 2)
    @Range(min = 18, max = 120, message = "Age must be 18-120")
    private Integer age;
}
```

**Data:**
```
John Doe,john@email.com,35     → Valid
Jane Smith,invalid-email,17    → Invalid (email format, age range)
,bob@email.com,25              → Invalid (name is null)
```

**Validation Results:**
```java
Map<Customer, Set<String>> errors = validator.validate(customers);

// Customer #1: No errors (valid)
// Customer #2: ["email: Invalid email", "age: Age must be 18-120"]
// Customer #3: ["name: Name required"]
```

---

## Design Patterns Used

### 1. Strategy Pattern

**Problem:** Different validation rules require different logic.

**Solution:** Define a common interface, create separate implementations.

**Benefits:**
- Easy to add new validation rules (create new strategy class)
- Each strategy is independent and testable
- Validator doesn't need to know about specific validation logic

### 2. Template Method Pattern

The `parse()` method defines the overall algorithm:
1. Read annotation
2. Open file
3. For each line: parse and create object
4. Return results

Subclasses or strategies handle specific steps.

### 3. Generic Programming

```java
public <T> List<T> parse(String filePath, Class<T> clazz)
```

**Benefits:**
- One method works for all types
- Type safety (compiler checks types)
- No need to cast results

---

## Complete Code Walkthrough

### Scenario: Parsing Customer Data

**File:** `customers.txt`
```
John Doe,john@email.com,35
Jane Smith,jane@email.com,28
```

**Step 1: Define the Model**

```java
@FileSource(delimiter = ",")  // Tell parser: use commas
public class Customer {
    @Column(index = 0, name = "name")
    @NotNull(message = "Name required")
    private String name;
    
    @Column(index = 1, name = "email")
    @Regex(pattern = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Invalid email")
    private String email;
    
    @Column(index = 2, name = "age")
    @Range(min = 18, max = 120, message = "Age must be 18-120")
    private Integer age;
    
    @Override
    public String toString() {
        return "Customer{name='" + name + "', email='" + email + "', age=" + age + "}";
    }
}
```

**Step 2: Parse the File**

```java
GenericFileParser<Customer> parser = new GenericFileParser<>();
List<Customer> customers = parser.parse("customers.txt", Customer.class);

// Result:
// [Customer{name='John Doe', email='john@email.com', age=35},
//  Customer{name='Jane Smith', email='jane@email.com', age=28}]
```

**Step 3: Validate**

```java
Validator validator = new Validator();
Map<Customer, Set<String>> errors = validator.validate(customers);

if (errors.isEmpty()) {
    System.out.println("All customers are valid!");
} else {
    errors.forEach((customer, errorMessages) -> {
        System.out.println("Invalid customer: " + customer);
        errorMessages.forEach(msg -> System.out.println("  - " + msg));
    });
}
```

---

## Error Handling

### Custom Exception

```java
public class ParsingException extends RuntimeException {
    public ParsingException(String message) {
        super(message);
    }
    
    public ParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

**Why unchecked (RuntimeException)?**
- Parsing errors are often unrecoverable
- No need to declare in method signatures
- Cleaner code

### Error Scenarios

**1. Missing @FileSource:**
```java
throw new ParsingException("Class " + clazz.getName() + " must have @FileSource annotation");
```

**2. Type conversion failure:**
```java
throw new ParsingException("Failed to convert '" + value + "' to " + targetType.getName(), e);
```

**3. Line parsing failure:**
```java
throw new ParsingException("Error parsing line " + lineNumber + ": " + line, e);
```

---

## Performance Considerations

### Reflection is Slower

Reflection has overhead compared to direct field access.

**Direct access:** `customer.setName("John")` - Very fast  
**Reflection:** `field.set(customer, "John")` - Slower

**Mitigation:**
- Annotations are read once per class (cached by JVM)
- For batch processing (thousands of records), the overhead is acceptable
- File I/O is usually the bottleneck, not reflection

### When NOT to Use This Framework

- **Real-time processing:** If you need millisecond response times
- **Huge files:** Millions of records—consider streaming or database imports
- **Complex formats:** Multi-line records, nested structures

---

## Extending the Framework

### Adding a New Validation Rule

**Example:** Add `@Email` annotation

**Step 1: Create annotation**
```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Email {
    String message() default "Invalid email";
}
```

**Step 2: Create strategy**
```java
public class EmailValidationStrategy implements ValidationStrategy {
    @Override
    public String validate(Field field, Object value) {
        Email email = field.getAnnotation(Email.class);
        if (email != null && value != null) {
            String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
            if (!value.toString().matches(emailPattern)) {
                return email.message();
            }
        }
        return null;
    }
}
```

**Step 3: Register in Validator**
```java
public Validator() {
    strategies.add(new NotNullValidationStrategy());
    strategies.add(new RegexValidationStrategy());
    strategies.add(new RangeValidationStrategy());
    strategies.add(new EmailValidationStrategy());  // Add new strategy
}
```

**Step 4: Use it**
```java
@Column(index = 1)
@Email(message = "Please provide a valid email")
private String email;
```

---

## Key Takeaways

1. **Annotations** provide metadata that drives the framework
2. **Reflection** enables generic, runtime manipulation of classes
3. **Strategy Pattern** makes validation extensible
4. **Generics** provide type safety and reusability
5. **Separation of Concerns** keeps parsing, validation, and domain logic separate

The framework demonstrates professional software engineering:
- DRY (Don't Repeat Yourself)
- Open/Closed Principle (open for extension, closed for modification)
- Single Responsibility (each class has one job)
- Testability (each component can be tested independently)

---

## Conclusion

This framework transforms hundreds of lines of repetitive parsing code into simple annotations. By understanding annotations, reflection, and design patterns, you can build powerful, flexible systems that adapt to changing requirements without rewriting core logic.

**Before:** 3 file types = 3 parsers = 300+ lines  
**After:** 3 file types = 3 annotated classes = 60 lines

The real power isn't just code reduction—it's **maintainability**. Adding a new file type takes 5 minutes instead of hours, and changes to validation rules happen in one place, not scattered across multiple parsers.
