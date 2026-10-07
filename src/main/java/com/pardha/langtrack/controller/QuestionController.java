package com.pardha.langtrack.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pardha.langtrack.entity.Question;
import com.pardha.langtrack.repository.QuestionRepository;

@RestController
@RequestMapping("/questions")
public class QuestionController {

    private final QuestionRepository repository;

    public QuestionController(QuestionRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Question> getAllQuestions() {
        return repository.findAll();
    }

    @GetMapping("/submodule/{subModuleId}")
    public List<Question> getQuestionsBySubModule(@PathVariable Long subModuleId) {
        return repository.findBySubModuleId(subModuleId);
    }

    @PostMapping
    public Question createQuestion(@RequestBody Question question) {
        return repository.save(question);
    }
}