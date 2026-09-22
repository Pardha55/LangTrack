package com.pardha.langtrack.entity;

import jakarta.persistence.*;

@Entity
public class Progress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long lessonId;

    private int score;

    private boolean completed;

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

    public boolean isCompleted() {
        return completed;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}