package com.seodong.portfolio.project;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("SELECT p FROM Project p ORDER BY p.sortOrder ASC, p.createdAt DESC")
    Page<Project> findAllOrdered(Pageable pageable);
}
