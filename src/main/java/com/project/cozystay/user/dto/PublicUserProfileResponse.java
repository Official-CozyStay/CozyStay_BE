package com.project.cozystay.user.dto;

import com.project.cozystay.user.domain.User;
import lombok.Builder;

/**
 * 호스트 프로필 응답 DTO
 */
@Builder
public record PublicUserProfileResponse(
        Long id,
        String nickName,
        String profileImageUrl,
        String grade,
        int reviewCount
) {
    public static PublicUserProfileResponse from(User user) {
        return PublicUserProfileResponse.builder()
                .id(user.getId())
                .nickName(user.getNickName())
                .profileImageUrl(user.getProfileImageUrl())
                .grade(user.getUserGrade().name())
                .reviewCount(user.getReviewCount())
                .build();
    }
}
