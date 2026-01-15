package models;

import annotations.*;

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
