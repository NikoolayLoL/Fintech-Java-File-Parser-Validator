# ReflectiveDataEngine - Project Overview

## What We Created

A **generic file parsing and validation framework** for Java that eliminates the need to write custom parsers for different file formats.

## The Problem It Solves

Financial institutions often receive data as flat text files with different delimiters:
- Transaction files use pipes: `TX001|500.00|2024-01-15`
- Customer files use commas: `John Doe,john@email.com,35`
- Audit files use semicolons: `192.168.1.1;CRITICAL`

Traditionally, you'd need separate parsing code for each file type—repetitive and error-prone.

## Our Solution

**One parser handles all file types.** You just add annotations to your Java classes:

```java
@FileSource(delimiter = "|")
public class Transaction {
    @Column(index = 0)
    @NotNull(message = "ID required")
    private String id;
    
    @Column(index = 1)
    @Range(min = 0.01, max = 1000000)
    private Double amount;
}
```

Then parse any file:
```java
GenericFileParser<Transaction> parser = new GenericFileParser<>();
List<Transaction> data = parser.parse("transactions.txt", Transaction.class);
```

## Key Technologies

- **Java Reflection**: Inspects classes at runtime to read annotations and set field values
- **Annotations**: Metadata that tells the parser how to handle each field
- **Strategy Pattern**: Pluggable validation rules (NotNull, Regex, Range)
- **Generics**: Works with any class type without hardcoding

## What Makes It Special

✅ **Generic**: Works with any delimiter and data structure  
✅ **Zero Dependencies**: Uses only standard Java libraries  
✅ **Automatic Validation**: Checks data integrity as it parses  
✅ **Type Safe**: Converts strings to proper types (int, double, dates)  
✅ **Extensible**: Easy to add new validation rules  

## Real-World Impact

Before: 3 file types = 3 custom parsers = 1000+ lines of code  
After: 3 file types = 3 annotated classes = 100 lines of code

Adding a new file type? Just create a new class with annotations—no parser code needed.
