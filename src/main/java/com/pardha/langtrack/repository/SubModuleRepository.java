package com.pardha.langtrack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pardha.langtrack.entity.SubModule;

public interface SubModuleRepository extends JpaRepository<SubModule, Long> {

    List<SubModule> findByLessonIdOrderByOrderNumber(Long lessonId);
}