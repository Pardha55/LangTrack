package com.pardha.langtrack.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

    private int lives = 5;
    private LocalDateTime lastLifeLostAt;

    private int currentStreak = 0;
    private int longestStreak = 0;
    private LocalDate lastStreakDate;
    private String passwordHash;

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

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        if (lives < 0 || lives > 5) {
            throw new IllegalArgumentException(
                    "Lives must be between 0 and 5");
        }

        this.lives = lives;
    }

    public LocalDateTime getLastLifeLostAt() {
        return lastLifeLostAt;
    }

    public void setLastLifeLostAt(LocalDateTime lastLifeLostAt) {
        this.lastLifeLostAt = lastLifeLostAt;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        if (currentStreak < 0) {
            throw new IllegalArgumentException(
                    "Current streak cannot be negative");
        }

        this.currentStreak = currentStreak;
    }

    public int getLongestStreak() {
        return longestStreak;
    }

    public void setLongestStreak(int longestStreak) {
        if (longestStreak < 0) {
            throw new IllegalArgumentException(
                    "Longest streak cannot be negative");
        }

        this.longestStreak = longestStreak;
    }

    public LocalDate getLastStreakDate() {
        return lastStreakDate;
    }

    public void setLastStreakDate(LocalDate lastStreakDate) {
        this.lastStreakDate = lastStreakDate;
    }

    @JsonIgnore
public String getPasswordHash() {
    return passwordHash;
}

public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
}

}