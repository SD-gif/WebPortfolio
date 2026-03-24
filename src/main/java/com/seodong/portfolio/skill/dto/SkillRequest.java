package com.seodong.portfolio.skill.dto;

import jakarta.validation.constraints.NotBlank;

public record SkillRequest(
        @NotBlank(message = "스킬명을 입력해주세요.")
        String name,
        int sortOrder
) {}
