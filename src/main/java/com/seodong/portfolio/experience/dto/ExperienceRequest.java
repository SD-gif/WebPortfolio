package com.seodong.portfolio.experience.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ExperienceRequest(
        String icon,

        @NotBlank(message = "제목을 입력해주세요.")
        @Size(max = 30, message = "제목은 30자 이내로 입력해주세요.")
        String title,

        @Size(max = 80, message = "간략 설명은 80자 이내로 입력해주세요.")
        String summary,

        @NotEmpty(message = "상황을 1개 이상 입력해주세요.")
        @Size(max = 3, message = "상황은 최대 3개까지 입력할 수 있습니다.")
        List<@NotBlank(message = "빈 항목은 저장할 수 없습니다.")
             @Size(max = 130, message = "항목은 130자 이내로 입력해주세요.") String> situation,

        @NotEmpty(message = "접근을 1개 이상 입력해주세요.")
        @Size(max = 3, message = "접근은 최대 3개까지 입력할 수 있습니다.")
        List<@NotBlank(message = "빈 항목은 저장할 수 없습니다.")
             @Size(max = 130, message = "항목은 130자 이내로 입력해주세요.") String> approach,

        @NotEmpty(message = "배운 점을 1개 이상 입력해주세요.")
        @Size(max = 3, message = "배운 점은 최대 3개까지 입력할 수 있습니다.")
        List<@NotBlank(message = "빈 항목은 저장할 수 없습니다.")
             @Size(max = 130, message = "항목은 130자 이내로 입력해주세요.") String> learned,

        String imageUrl,
        List<String> techStack,
        int sortOrder,
        Long linkedProjectId
) {}
