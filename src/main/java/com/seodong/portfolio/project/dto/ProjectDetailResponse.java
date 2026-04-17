package com.seodong.portfolio.project.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seodong.portfolio.project.Project;

import java.time.LocalDate;
import java.util.List;

public record ProjectDetailResponse(
        Long id,
        String title,
        String summary,
        String description,
        List<String> techStack,
        String githubUrl,
        String demoUrl,
        String duration,
        String teamSize,
        String role,
        List<String> features,
        List<PointItem> devPoints,
        List<PointItem> troubleshooting,
        List<ContentBlock> contentBlocks,
        List<ProjectMediaResponse> media,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate createdAt
) {
    public record PointItem(String label, String content, String imageUrl) {}
    public record ContentBlock(String blockType, String content) {}

    public static ProjectDetailResponse from(Project p) {
        return new ProjectDetailResponse(
                p.getId(),
                p.getTitle(),
                p.getSummary(),
                p.getDescription(),
                p.getTechStacks().stream().map(ts -> ts.getTech()).toList(),
                p.getGithubUrl(),
                p.getDemoUrl(),
                p.getDuration(),
                p.getTeamSize(),
                p.getRole(),
                p.getFeatures().stream().map(f -> f.getFeature()).toList(),
                p.getDevPoints().stream().map(d -> new PointItem(d.getLabel(), d.getContent(), d.getImageUrl())).toList(),
                p.getTroubleshootings().stream().map(t -> new PointItem(t.getLabel(), t.getContent(), t.getImageUrl())).toList(),
                p.getContentBlocks().stream().map(b -> new ContentBlock(b.getBlockType(), b.getContent())).toList(),
                p.getMediaList().stream().map(ProjectMediaResponse::from).toList(),
                p.getCreatedAt() != null ? p.getCreatedAt().toLocalDate() : null
        );
    }
}
