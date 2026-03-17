package com.seodong.portfolio.contact.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContactRequest(
        @NotBlank(message = "이름을 입력해주세요.")
        @Size(max = 50, message = "이름은 50자 이내로 입력해주세요.")
        String name,

        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,

        @NotBlank(message = "내용을 입력해주세요.")
        @Size(max = 1000, message = "내용은 1000자 이내로 입력해주세요.")
        String message
) {}
