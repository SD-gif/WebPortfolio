package com.seodong.portfolio.certification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CertificationRequest(
        @NotBlank(message = "이름을 입력해주세요.")
        String name,

        String issuer,
        String acquiredDate,

        @NotBlank
        @Pattern(regexp = "CERT|AWARD", message = "type은 CERT 또는 AWARD여야 합니다.")
        String type,

        int sortOrder
) {}
