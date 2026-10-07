package com.pardha.langtrack.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pardha.langtrack.entity.QuizAttempt;
import com.pardha.langtrack.repository.QuizAttemptRepository;
import com.pardha.langtrack.service.QuizAttemptService;

@RestController
@RequestMapping("/quiz-attempts")
public class QuizAttemptController {

    private final QuizAttemptRepository repository;
    private final QuizAttemptService service;

    public QuizAttemptController(
            QuizAttemptRepository repository,
            QuizAttemptService service) {

        this.repository = repository;
        this.service = service;
    }

    @PostMapping
public QuizAttempt createAttempt(@RequestBody QuizAttempt attempt) {

    if (!service.canAttemptQuiz(
            attempt.getUserId(),
            attempt.getSubModuleId())) {

        throw new RuntimeException(
                "Maximum 5 quiz attempts reached for today"
        );
    }

    return repository.save(attempt);
}


    @GetMapping("/count")
    public long getTodayAttemptCount(
            @RequestParam Long userId,
            @RequestParam Long subModuleId) {

        return service.getTodayAttemptCount(userId, subModuleId);
    }
}