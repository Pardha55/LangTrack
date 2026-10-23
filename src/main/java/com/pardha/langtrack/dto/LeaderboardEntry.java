package com.pardha.langtrack.dto;

public class LeaderboardEntry {

    private int rank;
    private Long userId;
    private String username;
    private long weeklyXp;

    public LeaderboardEntry() {
    }

    public LeaderboardEntry(
            int rank,
            Long userId,
            String username,
            long weeklyXp) {
        this.rank = rank;
        this.userId = userId;
        this.username = username;
        this.weeklyXp = weeklyXp;
    }

    public int getRank() {
        return rank;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public long getWeeklyXp() {
        return weeklyXp;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setWeeklyXp(long weeklyXp) {
        this.weeklyXp = weeklyXp;
    }
}