package com.pardha.langtrack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pardha.langtrack.entity.Lesson;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

    List<Lesson> findByLanguage(String language);
}