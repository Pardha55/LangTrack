package com.pardha.langtrack.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pardha.langtrack.entity.QuizAttempt;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    long countByUserIdAndSubModuleIdAndAttemptDate(
            Long userId,
            Long subModuleId,
            LocalDate attemptDate
    );
}