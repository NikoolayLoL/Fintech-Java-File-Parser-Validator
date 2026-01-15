package models;

import annotations.*;

/**
 * Model class representing a customer record from a comma-delimited file.
 * <p>
 * This class demonstrates the use of parsing and validation annotations for customer
 * data, including pattern-based email validation and range-based age validation.
 * </p>
 * 
 * <p><b>File Format:</b></p>
 * <pre>
 * name,email,age
 * John Doe,john.doe@example.com,35
 * Jane Smith,jane.smith@company.org,28
 * </pre>
 * 
 * <p><b>Validation Rules:</b></p>
 * <ul>
 *   <li>Name: Required (cannot be null)</li>
 *   <li>Email: Required, must match standard email pattern</li>
 *   <li>Age: Required, must be between 18 and 120</li>
 * </ul>
 * 
 * @see annotations.FileSource
 * @see annotations.Column
 * @see annotations.NotNull
 * @see annotations.Regex
 * @see annotations.Range
 * @author FinTech Corp
 * @version 1.0.0
 * @since 1.0.0
 */
@FileSource(delimiter = ",")
public class Customer {
    
    @Column(index = 0, name = "name")
    @NotNull(message = "Name cannot be null")
    private String name;
    
    @Column(index = 1, name = "email")
    @NotNull(message = "Email cannot be null")
    @Regex(pattern = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Invalid email format")
    private String email;
    
    @Column(index = 2, name = "age")
    @NotNull(message = "Age cannot be null")
    @Range(min = 18, max = 120, message = "Age must be between 18 and 120")
    private Integer age;

    @Override
    public String toString() {
        return "Customer{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                '}';
    }
}
