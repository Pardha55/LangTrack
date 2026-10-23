
package com.pardha.langtrack.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.pardha.langtrack.dto.QuestionView;
import com.pardha.langtrack.entity.Question;
import com.pardha.langtrack.repository.QuestionRepository;
import com.pardha.langtrack.service.QuizAttemptService;

@RestController
@RequestMapping("/questions")
public class QuestionController {

    private final QuestionRepository repository;
private final QuizAttemptService quizAttemptService;

    public QuestionController(
        QuestionRepository repository,
        QuizAttemptService quizAttemptService) {
    this.repository = repository;
    this.quizAttemptService = quizAttemptService;
}

    @GetMapping
    public List<QuestionView> getAllQuestions() {
        return repository.findAll()
                .stream()
                .map(this::toQuestionView)
                .toList();
    }

    @GetMapping("/submodule/{subModuleId}")
public List<QuestionView> getQuestionsBySubModule(
        @PathVariable Long subModuleId,
        @RequestParam Long userId) {

    boolean allowed = quizAttemptService.canAccessSubModule(
            userId, subModuleId);

    if (!allowed) {
        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "This submodule is locked. Pass the previous submodule first.");
    }

    return repository.findBySubModuleId(subModuleId)
            .stream()
            .map(this::toQuestionView)
            .toList();
}

    @PostMapping
    public Question createQuestion(@RequestBody Question question) {
        return repository.save(question);
    }

    private QuestionView toQuestionView(Question question) {
        return new QuestionView(
                question.getId(),
                question.getQuestionText(),
                question.getOptionA(),
                question.getOptionB(),
                question.getOptionC(),
                question.getOptionD()
        );
    }
}
