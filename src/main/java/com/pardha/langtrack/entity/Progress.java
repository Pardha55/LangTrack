package com.pardha.langtrack.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Progress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long lessonId;

    private int score;

    private boolean completed;

    private int xpEarned;

    public Progress() {
    }

    public Progress(Long userId, Long lessonId, int score, boolean completed) {
        this.userId = userId;
        this.lessonId = lessonId;
        this.score = score;
        this.completed = completed;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getLessonId() {
        return lessonId;
    }

    public int getScore() {
        return score;
    }

    public int getXpEarned() {
        return xpEarned;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setXpEarned(int xpEarned) {
        this.xpEarned = xpEarned;
    }
}