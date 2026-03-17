package com.seodong.portfolio.skill.dto;

import com.seodong.portfolio.skill.SkillCategory;

import java.util.List;

public record SkillListResponse(List<CategoryItem> categories) {

    public record CategoryItem(String category, List<String> skills) {
        public static CategoryItem from(SkillCategory sc) {
            return new CategoryItem(
                    sc.getName(),
                    sc.getSkills().stream().map(s -> s.getName()).toList()
            );
        }
    }
}
