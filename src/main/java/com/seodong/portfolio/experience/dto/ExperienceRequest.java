package com.seodong.portfolio.experience.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ExperienceRequest(
        String icon,

        @NotBlank(message = "제목을 입력해주세요.")
        String title,

        @Size(max = 80, message = "간략 설명은 80자 이내로 입력해주세요.")
        String summary,

        @NotBlank(message = "상황을 입력해주세요.")
        @Size(max = 200, message = "상황은 200자 이내로 입력해주세요.")
        String situation,

        @NotBlank(message = "접근을 입력해주세요.")
        @Size(max = 200, message = "접근은 200자 이내로 입력해주세요.")
        String approach,

        @NotBlank(message = "배운 점을 입력해주세요.")
        @Size(max = 150, message = "배운 점은 150자 이내로 입력해주세요.")
        String learned,

        String imageUrl,
        List<String> techStack,
        int sortOrder
) {}
