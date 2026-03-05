package com.project.cozystay.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UsernameCheckRequest(
        @NotBlank
        @Pattern(
                regexp = "^[a-z0-9]{4,20}$",
                message = "아이디는 영문 소문자+숫자 4~20자만 가능합니다"
        )
        String username
) {
}
