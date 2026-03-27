package com.project.cozystay.user.dto;

import com.project.cozystay.user.domain.User;
import lombok.Builder;

@Builder
public record UserGradeResponse(
        String grade,
        int totalCompletedBookings,
        int totalStayedNights,
        int reviewCount,
        String nextGrade,          // 예: "GOLD"
        int remainingBookingsToNextGrade,
        int remainingNightsToNextGrade
) {
    public static UserGradeResponse from(User user,
                                         String nextGrade,
                                         int remainingBookings,
                                         int remainingNights) {
        return UserGradeResponse.builder()
                .grade(user.getUserGrade().name())
                .totalCompletedBookings(user.getTotalCompletedBookings())
                .totalStayedNights(user.getTotalStayedNights())
                .reviewCount(user.getReviewCount())
                .nextGrade(nextGrade)
                .remainingBookingsToNextGrade(remainingBookings)
                .remainingNightsToNextGrade(remainingNights)
                .build();
    }
}
