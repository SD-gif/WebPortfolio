package com.seodong.portfolio.project.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seodong.portfolio.project.Project;

import java.time.LocalDate;
import java.util.List;

public record ProjectDetailResponse(
        Long id,
        String title,
        String description,
        List<String> techStack,
        String githubUrl,
        String demoUrl,
        List<String> features,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate createdAt
) {
    public static ProjectDetailResponse from(Project p) {
        return new ProjectDetailResponse(
                p.getId(),
                p.getTitle(),
                p.getDescription(),
                p.getTechStacks().stream().map(ts -> ts.getTech()).toList(),
                p.getGithubUrl(),
                p.getDemoUrl(),
                p.getFeatures().stream().map(f -> f.getFeature()).toList(),
                p.getCreatedAt() != null ? p.getCreatedAt().toLocalDate() : null
        );
    }
}
