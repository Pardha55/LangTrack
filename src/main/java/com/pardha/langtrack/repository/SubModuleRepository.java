
package com.pardha.langtrack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pardha.langtrack.entity.SubModule;

public interface SubModuleRepository extends JpaRepository<SubModule, Long> {

    List<SubModule> findByModule_Lesson_IdOrderByModule_ModuleNumberAscOrderNumberAsc(
        Long lessonId);

    List<SubModule> findByModule_Lesson_Id(Long lessonId);
     
    List<SubModule> findByModule_IdOrderByOrderNumber(Integer moduleId);

}
