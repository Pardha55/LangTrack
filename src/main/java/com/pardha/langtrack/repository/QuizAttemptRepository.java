package com.pardha.langtrack.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pardha.langtrack.entity.QuizAttempt;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    long countByUserIdAndAttemptDate(
            Long userId,
            LocalDate attemptDate
    );

    long countByUserIdAndSubModuleIdAndAttemptDate(
            Long userId,
            Long subModuleId,
            LocalDate attemptDate
    );

    boolean existsByUserIdAndSubModuleIdAndPassedTrue(
            Long userId,
            Long subModuleId
    );

    @Query("""
            SELECT qa.userId, SUM(qa.xpEarned)
            FROM QuizAttempt qa
            WHERE qa.attemptDate BETWEEN :startDate AND :endDate
            GROUP BY qa.userId
            HAVING SUM(qa.xpEarned) > 0
            ORDER BY SUM(qa.xpEarned) DESC
            """)
    List<Object[]> findWeeklyXpLeaderboard(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}