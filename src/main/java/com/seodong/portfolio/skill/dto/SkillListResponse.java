package com.seodong.portfolio.skill.dto;

import com.seodong.portfolio.skill.SkillCategory;

import java.util.List;

public record SkillListResponse(List<CategoryItem> categories) {

    public record SkillItem(String name, int level) {}

    public record CategoryItem(String category, List<SkillItem> skills) {
        public static CategoryItem from(SkillCategory sc) {
            return new CategoryItem(
                    sc.getName(),
                    sc.getSkills().stream().map(s -> new SkillItem(s.getName(), s.getLevel())).toList()
            );
        }
    }
}
