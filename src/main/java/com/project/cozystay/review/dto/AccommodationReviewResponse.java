package com.project.cozystay.review.dto;

import com.project.cozystay.comment.dto.CommentResponseDTO;
import com.project.cozystay.review.domain.AccommodationReview;

import java.math.BigDecimal;

/**
 * 게스트/사용자 -> 숙소
 * 리뷰 응답 DTO
 */
public record AccommodationReviewResponse (

    // 숙소 ID
    Long id,
    Long bookingId,

    // 평점
    BigDecimal ratingOverall,
    BigDecimal ratingCleanliness,
    BigDecimal ratingAccuracy,
    BigDecimal ratingCheckin,
    BigDecimal ratingCommunication,
    BigDecimal ratingLocation,

    String reviewComment,

    CommentResponseDTO comment
) {

}
