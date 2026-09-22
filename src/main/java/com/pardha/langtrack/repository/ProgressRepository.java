package com.pardha.langtrack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pardha.langtrack.entity.Progress;
public interface ProgressRepository extends JpaRepository<Progress, Long> {

 List<Progress> findByUserId(Long userId);
 List<Progress> findByCompleted(boolean completed);
}