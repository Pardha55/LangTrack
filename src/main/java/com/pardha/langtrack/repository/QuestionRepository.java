package com.pardha.langtrack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pardha.langtrack.entity.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findBySubModuleId(Long subModuleId);
}