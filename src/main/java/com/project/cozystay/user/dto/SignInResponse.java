package com.project.cozystay.user.dto;

import lombok.Builder;

@Builder
public record SignInResponse(
        Long userId,
        String nickName,
        String role,
        String grantType,
        String accessToken,
        String refreshToken
) {
}
