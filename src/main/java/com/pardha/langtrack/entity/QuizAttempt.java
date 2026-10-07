package com.pardha.langtrack.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long subModuleId;

    private int score;

    private int totalQuestions;

    private boolean passed;

    private LocalDate attemptDate;

    public QuizAttempt() {
    }

    public QuizAttempt(Long userId,
                       Long subModuleId,
                       int score,
                       int totalQuestions,
                       boolean passed,
                       LocalDate attemptDate) {

        this.userId = userId;
        this.subModuleId = subModuleId;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.passed = passed;
        this.attemptDate = attemptDate;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getSubModuleId() {
        return subModuleId;
    }

    public int getScore() {
        return score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public boolean isPassed() {
        return passed;
    }

    public LocalDate getAttemptDate() {
        return attemptDate;
    }
}