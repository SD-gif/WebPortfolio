package com.seodong.portfolio.profile.dto;

import com.seodong.portfolio.profile.Profile;

public record ProfileResponse(
        String name,
        String role,
        String email,
        String githubUrl,
        String bio,
        int projectCount,
        int expYears
) {
    public static ProfileResponse from(Profile p) {
        return new ProfileResponse(
                p.getName(), p.getRole(), p.getEmail(),
                p.getGithubUrl(), p.getBio(),
                p.getProjectCount(), p.getExpYears()
        );
    }
}
