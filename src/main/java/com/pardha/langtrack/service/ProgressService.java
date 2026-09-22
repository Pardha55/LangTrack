package com.pardha.langtrack.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pardha.langtrack.entity.Progress;
import com.pardha.langtrack.entity.ProgressSummary;
import com.pardha.langtrack.repository.ProgressRepository;

@Service
public class ProgressService {

    private final ProgressRepository repository;

    public ProgressService(ProgressRepository repository) {
        this.repository = repository;
    }

    public List<Progress> getProgressByUser(Long userId) {
        return repository.findByUserId(userId);
    }

    public List<Progress> getCompletedProgress() {
        return repository.findByCompleted(true);
    }
    public ProgressSummary getUserSummary(Long userId) {

    List<Progress> progressList = repository.findByUserId(userId);

    int lessonsCompleted = 0;
    int totalXp = 0;
    int totalScore = 0;

    for (Progress progress : progressList) {

        if (progress.isCompleted()) {
            lessonsCompleted++;
        }

        totalXp += progress.getScore();
        totalScore += progress.getScore();
    }

    double averageScore = 0;

    if (!progressList.isEmpty()) {
        averageScore = (double) totalScore / progressList.size();
    }

    return new ProgressSummary(
            userId,
            lessonsCompleted,
            totalXp,
            averageScore
    );
}
public List<Progress> getAllProgress() {
    return repository.findAll();
}

public Progress createProgress(Progress progress) {
    return repository.save(progress);
}
}