package com.pardha.langtrack.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;

    public User() {
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        System.out.println("Setting username: " + username);
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        System.out.println("Setting email: " + email);
        this.email = email;
    }
}