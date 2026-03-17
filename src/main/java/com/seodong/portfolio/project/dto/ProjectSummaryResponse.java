package com.seodong.portfolio.project.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seodong.portfolio.project.Project;

import java.time.LocalDate;
import java.util.List;

public record ProjectSummaryResponse(
        Long id,
        String title,
        String description,
        List<String> techStack,
        String githubUrl,
        String demoUrl,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate createdAt
) {
    public static ProjectSummaryResponse from(Project p) {
        return new ProjectSummaryResponse(
                p.getId(),
                p.getTitle(),
                p.getDescription(),
                p.getTechStacks().stream().map(ts -> ts.getTech()).toList(),
                p.getGithubUrl(),
                p.getDemoUrl(),
                p.getCreatedAt() != null ? p.getCreatedAt().toLocalDate() : null
        );
    }
}
