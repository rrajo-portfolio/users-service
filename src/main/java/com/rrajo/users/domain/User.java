package com.rrajo.users.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(name = "uk_users_email", columnNames = "email")})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 120)
    private String fullName;
    @Column(nullable = false, length = 160)
    private String email;
    @Column(length = 30)
    private String phone;
    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String s) {
        this.fullName = s;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String s) {
        this.email = s;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String s) {
        this.phone = s;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant t) {
        this.createdAt = t;
    }
}