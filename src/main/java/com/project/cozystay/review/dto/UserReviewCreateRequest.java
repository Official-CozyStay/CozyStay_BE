package com.project.cozystay.review.dto;

import com.project.cozystay.booking.domain.Booking;
import com.project.cozystay.comment.domain.Comment;
import com.project.cozystay.review.domain.UserReview;
import com.project.cozystay.user.domain.User;

import java.math.BigDecimal;

/**
 * 호스트 -> 게스트/사용자
 * 리뷰 생성 요청 DTO
 */
public record UserReviewCreateRequest (

    Long targetGuestId,
    Long bookingId,
    BigDecimal rating,
    String reviewComment
) {

    // 생성자
    public UserReviewCreateRequest{
        rating = (rating == null) ? BigDecimal.ZERO : rating;
        reviewComment = (reviewComment.isBlank()) ? "" : reviewComment;
    }

    // DTO -> Entity
    public UserReview toEntity(
            Booking booking,
            User reviewerHost,
            User targetGuest) {

        return UserReview.of(booking, reviewerHost, targetGuest, this.rating, this.reviewComment);
    }
}
