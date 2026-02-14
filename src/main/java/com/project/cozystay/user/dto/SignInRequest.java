package com.project.cozystay.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record SignInRequest(
        @NotBlank @Email String email,
        @NotBlank String password
) {
}
