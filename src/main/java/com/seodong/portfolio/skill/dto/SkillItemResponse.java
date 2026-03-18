package com.seodong.portfolio.skill.dto;

import com.seodong.portfolio.skill.Skill;

public record SkillItemResponse(
        Long id,
        String name,
        int sortOrder
) {
    public static SkillItemResponse from(Skill s) {
        return new SkillItemResponse(s.getId(), s.getName(), s.getSortOrder());
    }
}
