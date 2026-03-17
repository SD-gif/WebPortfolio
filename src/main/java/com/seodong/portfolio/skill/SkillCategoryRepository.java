package com.seodong.portfolio.skill;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkillCategoryRepository extends JpaRepository<SkillCategory, Long> {
    @EntityGraph(attributePaths = {"skills"})
    List<SkillCategory> findAllByOrderBySortOrderAsc();
}
