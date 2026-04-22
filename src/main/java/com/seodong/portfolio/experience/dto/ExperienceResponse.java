package com.seodong.portfolio.experience.dto;

import com.seodong.portfolio.experience.Experience;
import com.seodong.portfolio.experience.ExperienceTechStack;

import java.util.List;

public record ExperienceResponse(
        Long id,
        String icon,
        String title,
        String summary,
        List<String> situation,
        List<String> approach,
        List<String> learned,
        String imageUrl,
        List<String> techStack,
        int sortOrder,
        Long linkedProjectId
) {
    public static ExperienceResponse from(Experience e) {
        return new ExperienceResponse(
                e.getId(),
                e.getIcon(),
                e.getTitle(),
                e.getSummary(),
                List.copyOf(e.getSituation()),
                List.copyOf(e.getApproach()),
                List.copyOf(e.getLearned()),
                e.getImageUrl(),
                e.getTechStacks().stream().map(ExperienceTechStack::getTech).toList(),
                e.getSortOrder(),
                e.getLinkedProjectId()
        );
    }
}
