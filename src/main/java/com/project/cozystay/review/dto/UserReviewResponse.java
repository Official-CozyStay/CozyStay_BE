package com.project.cozystay.review.dto;

import com.project.cozystay.review.domain.UserReview;
import lombok.*;

import java.math.BigDecimal;

/**
 * 숙소 -> 게스트/사용자
 * 리뷰 응답 DTO
 */
@Builder
public record UserReviewResponse (

    Long targetGuestId,

//  Long bookingId,

    BigDecimal rating,

    String comment
) {
    // Entity -> DTO
    public static UserReviewResponse from(UserReview userReview){

        return UserReviewResponse.builder()
                .targetGuestId(userReview.getTargetGuest().getId())
                .rating(userReview.getRating())
                .comment(userReview.getComment())
                .build();
    }
}
