package com.pardha.langtrack.entity;

public class ProgressSummary {

    private Long userId;
    private int lessonsCompleted;
    private int totalXp;
    private double averageScore;

    public ProgressSummary(Long userId,
                           int lessonsCompleted,
                           int totalXp,
                           double averageScore) {
        this.userId = userId;
        this.lessonsCompleted = lessonsCompleted;
        this.totalXp = totalXp;
        this.averageScore = averageScore;
    }

    public Long getUserId() {
        return userId;
    }

    public int getLessonsCompleted() {
        return lessonsCompleted;
    }

    public int getTotalXp() {
        return totalXp;
    }

    public double getAverageScore() {
        return averageScore;
    }
}