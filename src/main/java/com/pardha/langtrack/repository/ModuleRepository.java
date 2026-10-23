
package com.pardha.langtrack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pardha.langtrack.entity.Module;

public interface ModuleRepository extends JpaRepository<Module, Integer> {

    List<Module> findByLesson_IdOrderByModuleNumberAsc(Long lessonId);
}
