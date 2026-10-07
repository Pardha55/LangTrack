package com.pardha.langtrack.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.pardha.langtrack.repository.QuizAttemptRepository;

@Service
public class QuizAttemptService {

    private final QuizAttemptRepository repository;

    public QuizAttemptService(QuizAttemptRepository repository) {
        this.repository = repository;
    }
     public long getTodayAttemptCount(Long userId, Long subModuleId) {

        LocalDate today = LocalDate.now();

        return repository.countByUserIdAndSubModuleIdAndAttemptDate(
                userId,
                subModuleId,
                today
                 );
    }
    public boolean canAttemptQuiz(Long userId, Long subModuleId) {

        long attemptCount = getTodayAttemptCount(userId, subModuleId);

         System.out.println("Today's attempt count = " + attemptCount);

        return attemptCount < 5;
    }
}