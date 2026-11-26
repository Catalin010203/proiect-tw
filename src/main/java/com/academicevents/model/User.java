package com.academicevents.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 80)
    @Column(name = "first_name", nullable = false, length = 80)
    private String firstName;

    @NotBlank
    @Size(max = 80)
    @Column(name = "last_name", nullable = false, length = 80)
    private String lastName;

    @NotBlank
    @Email
    @Size(max = 254)
    @Column(name = "email", nullable = false, unique = true, length = 254)
    private String email;

    @NotBlank
    @Column(name = "password_hash", nullable = false, length = 60)
    private String passwordHash;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    protected User() {

    }

    public User(String firstName,
                String lastName,
                String email,
                String passwordHash,
                UserRole role
    ) {
        this.firstName    = firstName;
        this.lastName     = lastName;
        this.email        = email;
        this.passwordHash = passwordHash;
        this.role         = role;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public UserRole getRole() {
        return role;
    }
}
