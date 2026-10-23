package com.pardha.langtrack.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pardha.langtrack.dto.LeaderboardEntry;
import com.pardha.langtrack.entity.User;
import com.pardha.langtrack.repository.ProgressRepository;
import com.pardha.langtrack.repository.QuizAttemptRepository;
import com.pardha.langtrack.repository.UserRepository;

@Service
public class LeaderboardService {

    private final QuizAttemptRepository quizAttemptRepository;
    private final UserRepository userRepository;
    private final ProgressRepository progressRepository;

    public LeaderboardService(
            QuizAttemptRepository quizAttemptRepository,
            UserRepository userRepository,
            ProgressRepository progressRepository) {

        this.quizAttemptRepository = quizAttemptRepository;
        this.userRepository = userRepository;
        this.progressRepository = progressRepository;
    }

    @Transactional(readOnly = true)
    public List<LeaderboardEntry> getWeeklyLeaderboard() {

        LocalDate today = LocalDate.now();

        LocalDate startDate = today.with(
                TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        LocalDate endDate = startDate.plusDays(6);

        List<Object[]> results =
                quizAttemptRepository.findWeeklyXpLeaderboard(
                        startDate, endDate);

        List<LeaderboardEntry> leaderboard = new ArrayList<>();

        // Avoid calling the cumulative XP query with an empty IN list.
        if (results.isEmpty()) {
            return leaderboard;
        }

        List<Long> userIds = new ArrayList<>();

        for (Object[] result : results) {
            userIds.add(((Number) result[0]).longValue());
        }

        Map<Long, User> usersById = new HashMap<>();

        for (User user : userRepository.findAllById(userIds)) {
            usersById.put(user.getId(), user);
        }

        // Ask the database to sum cumulative XP only for leaderboard users.
        Map<Long, Long> cumulativeXpByUser = new HashMap<>();

        List<Object[]> cumulativeXpResults =
                progressRepository.findCumulativeXpByUserIds(userIds);

        for (Object[] result : cumulativeXpResults) {
            Long userId = ((Number) result[0]).longValue();
            long cumulativeXp = ((Number) result[1]).longValue();

            cumulativeXpByUser.put(userId, cumulativeXp);
        }

        List<LeaderboardRow> rows = new ArrayList<>();

        for (Object[] result : results) {

            Long userId = ((Number) result[0]).longValue();
            long weeklyXp = ((Number) result[1]).longValue();

            User user = usersById.get(userId);

            if (user != null) {
                long cumulativeXp =
                        cumulativeXpByUser.getOrDefault(userId, 0L);

                rows.add(new LeaderboardRow(
                        user,
                        weeklyXp,
                        cumulativeXp));
            }
        }

        // Ranking priority:
        // 1. Weekly XP: highest first.
        // 2. Effective current streak: highest first.
        // 3. Cumulative XP: highest first.
        // 4. User ID: lowest first.
        rows.sort(
                Comparator.comparingLong(LeaderboardRow::weeklyXp)
                        .reversed()
                        .thenComparing(
                                Comparator.comparingInt(
                                        (LeaderboardRow row) ->
                                                getEffectiveStreak(row.user(), today))
                                        .reversed())
                        .thenComparing(
                                Comparator.comparingLong(
                                        LeaderboardRow::cumulativeXp)
                                        .reversed())
                        .thenComparing(
                                row -> row.user().getId()));

        int rank = 1;

        for (LeaderboardRow row : rows) {
            User user = row.user();

            leaderboard.add(new LeaderboardEntry(
                    rank,
                    user.getId(),
                    user.getUsername(),
                    row.weeklyXp()));

            rank++;
        }

        return leaderboard;
    }

    private int getEffectiveStreak(User user, LocalDate today) {
        LocalDate lastStreakDate = user.getLastStreakDate();

        if (lastStreakDate == null) {
            return 0;
        }

        boolean activeToday = lastStreakDate.equals(today);
        boolean activeYesterday = lastStreakDate.equals(today.minusDays(1));

        if (activeToday || activeYesterday) {
            return user.getCurrentStreak();
        }

        return 0;
    }

    private record LeaderboardRow(
            User user,
            long weeklyXp,
            long cumulativeXp) {
    }
}
