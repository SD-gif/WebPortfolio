package com.seodong.portfolio.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ProjectRequest(
        @NotBlank(message = "프로젝트 제목을 입력해주세요.")
        @Size(max = 100, message = "제목은 100자 이내로 입력해주세요.")
        String title,

        String description,
        String githubUrl,
        String demoUrl,
        int sortOrder,
        List<String> techStack,
        List<String> features
) {}
