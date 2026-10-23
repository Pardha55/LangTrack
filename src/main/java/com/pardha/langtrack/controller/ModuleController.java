
package com.pardha.langtrack.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pardha.langtrack.entity.Module;
import com.pardha.langtrack.repository.ModuleRepository;

@RestController
@RequestMapping("/modules")
public class ModuleController {

    private final ModuleRepository repository;

    public ModuleController(ModuleRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/lesson/{lessonId}")
    public List<Module> getModulesByLesson(
            @PathVariable Long lessonId) {

        return repository.findByLesson_IdOrderByModuleNumberAsc(
                lessonId);
    }
}
