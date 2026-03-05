package com.project.cozystay.review.dto;

import com.project.cozystay.comment.dto.CommentResponseDTO;
import com.project.cozystay.review.domain.UserReview;

import java.math.BigDecimal;

/**
 * 숙소 -> 게스트/사용자
 * 리뷰 응답 DTO
 */
public record UserReviewResponse (

    Long targetGuestId,

    Long bookingId,

    String userNickName,
    String userProfileImageUrl,

    BigDecimal rating,

    String reviewComment,

    CommentResponseDTO comment
) {
}
