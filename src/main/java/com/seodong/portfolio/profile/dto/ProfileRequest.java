package com.seodong.portfolio.profile.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfileRequest(
        @NotBlank(message = "이름을 입력해주세요.")
        @Size(max = 50, message = "이름은 50자 이내로 입력해주세요.")
        String name,

        @NotBlank(message = "역할을 입력해주세요.")
        @Size(max = 100, message = "역할은 100자 이내로 입력해주세요.")
        String role,

        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,

        String githubUrl,
        String bio,

        @Min(value = 0, message = "프로젝트 수는 0 이상이어야 합니다.")
        int projectCount,

        @Min(value = 0, message = "경력은 0 이상이어야 합니다.")
        int expYears
) {}
