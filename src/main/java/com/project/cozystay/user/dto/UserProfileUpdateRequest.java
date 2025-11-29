package com.project.cozystay.user.dto;

import jakarta.validation.constraints.Size;

/**
 * 프로필 수정 요청 DTO
 */
public record UserProfileUpdateRequest(
        @Size(min = 1, max = 30)
        String nickName,
        String profileImageUrl
) {
}
