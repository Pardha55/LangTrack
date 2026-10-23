
package com.pardha.langtrack.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pardha.langtrack.entity.Progress;

public interface ProgressRepository extends JpaRepository<Progress, Long> {

    List<Progress> findByUserId(Long userId);

    List<Progress> findByCompleted(boolean completed);

    Optional<Progress> findByUserIdAndLessonId(
            Long userId,
            Long lessonId
    );

    @Query("""
            SELECT p.userId, SUM(p.xpEarned)
            FROM Progress p
            WHERE p.userId IN :userIds
            GROUP BY p.userId
            """)
    List<Object[]> findCumulativeXpByUserIds(
            @Param("userIds") List<Long> userIds
    );
}
