package com.seodong.portfolio.education.dto;

import com.seodong.portfolio.education.Education;

public record EducationResponse(
        Long id,
        String institution,
        String degree,
        String major,
        String startDate,
        String endDate,
        int sortOrder
) {
    public static EducationResponse from(Education e) {
        return new EducationResponse(
                e.getId(), e.getInstitution(), e.getDegree(), e.getMajor(),
                e.getStartDate(), e.getEndDate(), e.getSortOrder()
        );
    }
}
