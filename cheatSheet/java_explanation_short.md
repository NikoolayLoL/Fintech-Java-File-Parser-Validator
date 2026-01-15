# ReflectiveDataEngine - Short Technical Guide

*A beginner-friendly guide to understanding how this framework works*

## What This Framework Does

Imagine you have a text file with data like this:
```
John,john@email.com,35
Jane,jane@email.com,28
```

You want to convert each line into a Java object. Normally, you'd write code like:
```java
String[] parts = line.split(",");
Customer customer = new Customer();
customer.setName(parts[0]);
customer.setEmail(parts[1]);
customer.setAge(Integer.parseInt(parts[2]));
```

With our framework, you just add **annotations** (special labels) to your class, and it handles everything automatically.

---

## Core Concept #1: Annotations

**What are annotations?**  
Think of annotations as sticky notes you attach to your code. They don't do anything by themselves, but other code can read them and act accordingly.

**Example:**
```java
@FileSource(delimiter = ",")  // ← Sticky note: "This file uses commas"
public class Customer {
    
    @Column(index = 0)  // ← Sticky note: "This is column 0"
    private String name;
    
    @Column(index = 1)
    private String email;
    
    @Column(index = 2)
    private Integer age;
}
```

The parser reads these sticky notes to know:
1. How to split the line (comma)
2. Which column goes to which field

---

## Core Concept #2: Reflection

**What is reflection?**  
Reflection is Java's ability to look at classes "in the mirror" while the program is running.

**What it lets us do:**
- Ask a class: "What fields do you have?"
- Ask a field: "Do you have any annotations?"
- Tell a field: "Set yourself to this value"

**Example without reflection (traditional):**
```java
Customer customer = new Customer();
customer.setName("John");  // We hardcode the field name
```

**Example with reflection (generic):**
```java
// We don't know what class or fields exist!
Object instance = clazz.newInstance();  // Create any object
Field field = clazz.getDeclaredField("name");  // Find any field
field.set(instance, "John");  // Set any value
```

This is how our parser can work with **any class** without knowing its name ahead of time.

---

## How The Parser Works (Step-by-Step)

### Step 1: Read the Annotation
```java
@FileSource(delimiter = ",")
public class Customer { ... }
```

The parser asks: "What delimiter does this class use?"  
Answer: Found `@FileSource` with delimiter = ","

### Step 2: Read the File
```
John,john@email.com,35
Jane,jane@email.com,28
```

### Step 3: Process Each Line

For line "John,john@email.com,35":

1. **Split the line:** `["John", "john@email.com", "35"]`

2. **Create a new object:** `Customer customer = new Customer();`

3. **Find all fields with @Column:**
   - Field "name" has `@Column(index = 0)` → Get value at position 0 → "John"
   - Field "email" has `@Column(index = 1)` → Get value at position 1 → "john@email.com"
   - Field "age" has `@Column(index = 2)` → Get value at position 2 → "35"

4. **Convert types:** String "35" → Integer 35

5. **Set the values:**
   ```java
   field.setAccessible(true);  // Allow access to private fields
   field.set(customer, value);  // Set the value
   ```

### Step 4: Return the Results
```java
List<Customer> customers = [customer1, customer2, ...]
```

---

## Validation System

After parsing, we need to check if the data is valid.

### The Strategy Pattern

Instead of writing validation code in one giant method, we use **strategies**—separate classes that each handle one type of validation.

**The Interface (Contract):**
```java
public interface ValidationStrategy {
    String validate(Field field, Object value);
}
```

Each strategy implements this contract differently:

**Strategy 1: Check for null values**
```java
public class NotNullValidationStrategy implements ValidationStrategy {
    public String validate(Field field, Object value) {
        if (field has @NotNull annotation && value is null) {
            return "This field cannot be null";
        }
        return null;  // Valid!
    }
}
```

**Strategy 2: Check regex patterns**
```java
public class RegexValidationStrategy implements ValidationStrategy {
    public String validate(Field field, Object value) {
        if (field has @Regex annotation) {
            if (value doesn't match the pattern) {
                return "Invalid format";
            }
        }
        return null;  // Valid!
    }
}
```

**Strategy 3: Check numeric ranges**
```java
public class RangeValidationStrategy implements ValidationStrategy {
    public String validate(Field field, Object value) {
        if (field has @Range annotation) {
            if (value < min or value > max) {
                return "Value out of range";
            }
        }
        return null;  // Valid!
    }
}
```

### The Validator (Orchestrator)

The `Validator` class uses all strategies:

```java
public class Validator {
    private List<ValidationStrategy> strategies;
    
    public Map<Object, Set<String>> validate(List<Object> objects) {
        Map<Object, Set<String>> errors = new HashMap<>();
        
        for (Object obj : objects) {
            Set<String> objectErrors = new HashSet<>();
            
            // Check all fields
            for (Field field : obj.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(obj);
                
                // Run all validation strategies
                for (ValidationStrategy strategy : strategies) {
                    String error = strategy.validate(field, value);
                    if (error != null) {
                        objectErrors.add(error);
                    }
                }
            }
            
            if (!objectErrors.isEmpty()) {
                errors.put(obj, objectErrors);
            }
        }
        
        return errors;
    }
}
```

---

## Type Conversion

Files contain strings, but fields need specific types.

```java
private Object convertValue(String value, Class<?> targetType) {
    if (value.isEmpty()) {
        return null;
    }
    
    try {
        return switch (targetType.getName()) {
            case "java.lang.String" -> value;  // Already a string
            case "int", "java.lang.Integer" -> Integer.parseInt(value);  // "35" → 35
            case "double", "java.lang.Double" -> Double.parseDouble(value);  // "1250.75" → 1250.75
            case "boolean", "java.lang.Boolean" -> Boolean.parseBoolean(value);  // "true" → true
            case "java.time.LocalDate" -> LocalDate.parse(value);  // "2024-01-15" → LocalDate object
            default -> throw new ParsingException("Unsupported type: " + targetType.getName());
        };
    } catch (NumberFormatException | DateTimeParseException e) {
        throw new ParsingException("Failed to convert value '" + value + "' to type " + targetType.getName(), e);
    }
}
```

---

## Complete Example Flow

**1. You have a file:** `customers.txt`
```
John,john@email.com,35
Jane,invalid-email,17
```

**2. You create a class:**
```java
@FileSource(delimiter = ",")
public class Customer {
    @Column(index = 0)
    @NotNull(message = "Name required")
    private String name;
    
    @Column(index = 1)
    @Regex(pattern = "^[A-Z]+@.+$", message = "Invalid email")
    private String email;
    
    @Column(index = 2)
    @Range(min = 18, max = 120, message = "Age must be 18-120")
    private Integer age;
}
```

**3. You parse the file:**
```java
GenericFileParser<Customer> parser = new GenericFileParser<>();
List<Customer> customers = parser.parse("customers.txt", Customer.class);
// Result: [Customer(John, john@email.com, 35), Customer(Jane, invalid-email, 17)]
```

**4. You validate:**
```java
Validator validator = new Validator();
Map<Customer, Set<String>> errors = validator.validate(customers);
```

**5. Results:**
```
Customer #1 (John): Valid ✓
Customer #2 (Jane): 
  - email: Invalid email format
  - age: Age must be 18-120
```

---

## Why This Matters

### Traditional Approach (Without Framework)

```java
// TransactionParser.java (100 lines)
// CustomerParser.java (100 lines)
// SecurityAuditParser.java (100 lines)
// Total: 300+ lines of repetitive code
```

### With Framework

```java
// Transaction.java (20 lines with annotations)
// Customer.java (20 lines with annotations)
// SecurityAudit.java (20 lines with annotations)
// Total: 60 lines, no parsing code needed!
```

**Benefits:**
- ✅ Less code to write
- ✅ Less code to test
- ✅ Less code to maintain
- ✅ Adding new file types takes minutes, not hours
- ✅ Validation logic is centralized and reusable

---

## Key Takeaways

1. **Annotations** = Metadata that describes how to handle your data
2. **Reflection** = Runtime inspection and manipulation of classes
3. **Strategy Pattern** = Pluggable validation rules
4. **Generics** = Write code once, use for any type

The magic is that the parser never knows about `Customer`, `Transaction`, or any specific class. It just reads annotations and uses reflection to do its job—making it truly **generic** and **reusable**.
