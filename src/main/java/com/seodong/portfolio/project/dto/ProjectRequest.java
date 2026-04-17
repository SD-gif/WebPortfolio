package com.seodong.portfolio.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ProjectRequest(
        @NotBlank(message = "프로젝트 제목을 입력해주세요.")
        @Size(max = 30, message = "제목은 30자 이내로 입력해주세요.")
        String title,

        @Size(max = 80, message = "간략 소개는 80자 이내로 입력해주세요.")
        String summary,

        String description,
        String githubUrl,
        String demoUrl,
        String duration,
        String teamSize,
        String role,
        int sortOrder,
        List<String> techStack,
        List<String> features,
        List<PointItem> devPoints,
        List<PointItem> troubleshooting,
        List<ContentBlock> contentBlocks
) {
    public record PointItem(
            @NotBlank String label,
            @NotBlank String content,
            String imageUrl
    ) {}

    public record ContentBlock(
            @NotBlank String blockType,
            @NotBlank String content
    ) {}
}
