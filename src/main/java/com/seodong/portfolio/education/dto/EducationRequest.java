package com.seodong.portfolio.education.dto;

import jakarta.validation.constraints.NotBlank;

public record EducationRequest(
        @NotBlank(message = "학교명을 입력해주세요.")
        String institution,

        @NotBlank(message = "학위를 입력해주세요.")
        String degree,

        @NotBlank(message = "전공을 입력해주세요.")
        String major,

        String startDate,
        String endDate,
        int sortOrder
) {}
