package com.project.cozystay.user.dto;

import com.project.cozystay.user.domain.User;
import lombok.Builder;

/**
 * 내 프로필 응답 DTO
 */
@Builder
public record UserProfileResponse(
        Long id,
        String email,
        String nickName,
        String profileImageUrl,
        String role,      // USER / HOST / ADMIN
        String grade      // BRONZE / SILVER / GOLD / PLATINUM
) {
    public static UserProfileResponse from(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickName(user.getNickName())
                .profileImageUrl(user.getProfileImageUrl())
                .role(user.getUserRole().name())
                .grade(user.getUserGrade().name())
                .build();
    }
}
