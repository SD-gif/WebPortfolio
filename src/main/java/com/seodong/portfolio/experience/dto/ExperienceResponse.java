package com.seodong.portfolio.experience.dto;

import com.seodong.portfolio.experience.Experience;
import com.seodong.portfolio.experience.ExperienceTechStack;

import java.util.List;

public record ExperienceResponse(
        Long id,
        String icon,
        String title,
        String summary,
        String situation,
        String approach,
        String learned,
        String imageUrl,
        List<String> techStack,
        int sortOrder
) {
    public static ExperienceResponse from(Experience e) {
        return new ExperienceResponse(
                e.getId(),
                e.getIcon(),
                e.getTitle(),
                e.getSummary(),
                e.getSituation(),
                e.getApproach(),
                e.getLearned(),
                e.getImageUrl(),
                e.getTechStacks().stream().map(ExperienceTechStack::getTech).toList(),
                e.getSortOrder()
        );
    }
}
