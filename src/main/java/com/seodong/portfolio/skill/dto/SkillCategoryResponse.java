package com.seodong.portfolio.skill.dto;

import com.seodong.portfolio.skill.SkillCategory;

import java.util.List;

public record SkillCategoryResponse(
        Long id,
        String name,
        int sortOrder,
        List<SkillItemResponse> skills
) {
    public static SkillCategoryResponse from(SkillCategory sc) {
        return new SkillCategoryResponse(
                sc.getId(),
                sc.getName(),
                sc.getSortOrder(),
                sc.getSkills().stream().map(SkillItemResponse::from).toList()
        );
    }
}
