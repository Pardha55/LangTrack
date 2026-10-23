package com.pardha.langtrack.dto;

import java.time.LocalDate;

public class StreakSummary {

    private Long userId;
    private int currentStreak;
    private int longestStreak;
    private LocalDate lastStreakDate;

    public StreakSummary() {
    }

    public StreakSummary(
            Long userId,
            int currentStreak,
            int longestStreak,
            LocalDate lastStreakDate) {
        this.userId = userId;
        this.currentStreak = currentStreak;
        this.longestStreak = longestStreak;
        this.lastStreakDate = lastStreakDate;
    }

    public Long getUserId() {
        return userId;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public int getLongestStreak() {
        return longestStreak;
    }

    public LocalDate getLastStreakDate() {
        return lastStreakDate;
    }
}