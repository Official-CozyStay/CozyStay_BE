package com.project.cozystay.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record SignUpRequest(
        @NotBlank @Email String email,
        @NotBlank String password,
        @NotBlank String nickName
) {
}
