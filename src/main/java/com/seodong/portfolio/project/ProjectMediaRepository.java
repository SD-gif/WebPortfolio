package com.seodong.portfolio.project;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectMediaRepository extends JpaRepository<ProjectMedia, Long> {
    List<ProjectMedia> findByProjectIdOrderBySortOrderAsc(Long projectId);
}
