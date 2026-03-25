package com.seodong.portfolio.project.dto;

import com.seodong.portfolio.project.ProjectMedia;

public record ProjectMediaResponse(
        Long id,
        String url,
        String mediaType
) {
    public static ProjectMediaResponse from(ProjectMedia m) {
        return new ProjectMediaResponse(m.getId(), m.getUrl(), m.getMediaType().name());
    }
}
