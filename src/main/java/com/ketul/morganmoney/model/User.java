package com.ketul.morganmoney.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Email is the username — must be unique so two people can't share one
    @Column(unique = true, nullable = false)
    private String email;

    // We never store the real password — only a hashed version
    // Hashing is a one-way process so even if the database is stolen
    // nobody can read the actual passwords
    @Column(nullable = false)
    private String password;

    private String name;

    public User() {}

    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public void setPassword(String password) { this.password = password; }
}
