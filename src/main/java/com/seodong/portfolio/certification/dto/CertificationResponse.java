package com.seodong.portfolio.certification.dto;

import com.seodong.portfolio.certification.Certification;

public record CertificationResponse(
        Long id,
        String name,
        String issuer,
        String acquiredDate,
        String type,
        int sortOrder
) {
    public static CertificationResponse from(Certification c) {
        return new CertificationResponse(
                c.getId(), c.getName(), c.getIssuer(),
                c.getAcquiredDate(), c.getType(), c.getSortOrder()
        );
    }
}
