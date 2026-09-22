package com.pardha.langtrack.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pardha.langtrack.entity.Progress;
import com.pardha.langtrack.entity.ProgressSummary;
import com.pardha.langtrack.service.ProgressService;

@RestController
@RequestMapping("/progress")
public class ProgressController {

    
    private final ProgressService service;

   public ProgressController(ProgressService service) {
    this.service = service;
}

   @GetMapping
public List<Progress> getProgress() {
    return service.getAllProgress();
}
    @GetMapping("/user/{userId}")
    public List<Progress> getProgressByUser(@PathVariable Long userId) {
        return service.getProgressByUser(userId);
    }

    @GetMapping("/completed")
    public List<Progress> getCompletedProgress() {
        return service.getCompletedProgress();
    }

    @GetMapping("/user/{userId}/summary")
    public ProgressSummary getUserSummary(@PathVariable Long userId) {
         return service.getUserSummary(userId);
    }

  @PostMapping
public Progress createProgress(@RequestBody Progress progress) {
    return service.createProgress(progress);
}
}