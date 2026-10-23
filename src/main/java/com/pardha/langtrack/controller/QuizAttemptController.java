
package com.pardha.langtrack.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.pardha.langtrack.dto.AnswerFeedback;
import com.pardha.langtrack.dto.QuizAnswer;
import com.pardha.langtrack.dto.QuizResult;
import com.pardha.langtrack.dto.QuizSubmission;
import com.pardha.langtrack.entity.User;
import com.pardha.langtrack.repository.UserRepository;
import com.pardha.langtrack.service.QuizAttemptService;

@RestController
@RequestMapping("/quiz-attempts")
public class QuizAttemptController {

    private final QuizAttemptService quizAttemptService;
    private final UserRepository userRepository;

    public QuizAttemptController(
            QuizAttemptService quizAttemptService,
            UserRepository userRepository) {
        this.quizAttemptService = quizAttemptService;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Please log in first");
        }

        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Authenticated user not found"));
    }

    @PostMapping
    public QuizResult submitQuiz(
            @RequestBody QuizSubmission submission,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        if (submission == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Quiz submission is required");
        }

        submission.setUserId(user.getId());

        return quizAttemptService.evaluateQuiz(submission);
    }

    @PostMapping("/answer")
    public AnswerFeedback checkSingleAnswer(
            @RequestBody QuizAnswer answer,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        if (answer == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Answer is required");
        }

        answer.setUserId(user.getId());

        try {
            return quizAttemptService.checkSingleAnswer(answer);
        } catch (IllegalStateException exception) {
            if ("No lives remaining. Restore your lives before continuing."
                    .equals(exception.getMessage())) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        exception.getMessage());
            }

            throw exception;
        }
    }

    @GetMapping("/lives")
    public int getLives(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        return quizAttemptService.getLives(user.getId());
    }

    @GetMapping("/submodule-status")
    public Map<String, Boolean> getSubModuleStatus(
            @RequestParam Long subModuleId,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        boolean completed =
                quizAttemptService.hasPassedSubModuleForUser(
                        user.getId(),
                        subModuleId);

        return Map.of("completed", completed);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalState(
            IllegalStateException ex) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("message", ex.getMessage()));
    }
}
