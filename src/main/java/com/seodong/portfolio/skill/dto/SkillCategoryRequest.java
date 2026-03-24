package com.seodong.portfolio.skill.dto;

import jakarta.validation.constraints.NotBlank;

public record SkillCategoryRequest(
        @NotBlank(message = "카테고리명을 입력해주세요.")
        String name,
        int sortOrder
) {}
