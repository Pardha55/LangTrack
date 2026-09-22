package com.pardha.langtrack.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pardha.langtrack.entity.Lesson;
import com.pardha.langtrack.repository.LessonRepository;

@RestController
@RequestMapping("/lessons")
public class LessonController {

    private final LessonRepository repository;

    public LessonController(LessonRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Lesson> getLessons() {
        return repository.findAll();
    }

    @GetMapping("/language/{language}")
    public List<Lesson> getLessonsByLanguage(@PathVariable String language) {
    return repository.findByLanguage(language);
    }
    @PostMapping
    public Lesson createLesson(@RequestBody Lesson lesson) {
        return repository.save(lesson);
    }
}