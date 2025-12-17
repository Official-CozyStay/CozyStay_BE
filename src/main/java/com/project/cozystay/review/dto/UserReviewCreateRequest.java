package com.project.cozystay.review.dto;

import com.project.cozystay.review.domain.UserReview;
import com.project.cozystay.user.domain.User;

import java.math.BigDecimal;

/**
 * 호스트 -> 게스트/사용자
 * 리뷰 생성 요청 DTO
 */
public record UserReviewCreateRequest (

    Long targetGuestId,
    //private Long bookingId,
    BigDecimal rating,
    String comment
) {

    // 생성자
    public UserReviewCreateRequest{
        rating = (rating == null) ? BigDecimal.ZERO : rating;
        comment = (comment.isBlank()) ? "" : comment;
    }

    // DTO -> Entity
    public UserReview toEntity(
            //Long bookingId
            User reviewerHost,
            User targetGuest) {

        return UserReview.of(reviewerHost, targetGuest, this.rating, this.comment);
    }
}
