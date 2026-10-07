package com.pardha.langtrack.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pardha.langtrack.entity.SubModule;
import com.pardha.langtrack.repository.SubModuleRepository;

@RestController
@RequestMapping("/submodules")
public class SubModuleController {

    private final SubModuleRepository repository;

    public SubModuleController(SubModuleRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<SubModule> getAllSubModules() {
        return repository.findAll();
    }

    @GetMapping("/lesson/{lessonId}")
    public List<SubModule> getSubModulesByLesson(@PathVariable Long lessonId) {
        return repository.findByLessonIdOrderByOrderNumber(lessonId);
    }

    @PostMapping
    public SubModule createSubModule(@RequestBody SubModule subModule) {
        return repository.save(subModule);
    }
}