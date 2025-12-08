package com.project.cozystay.review.dto;

import com.project.cozystay.review.domain.AccommodationReview;
import lombok.*;

import java.math.BigDecimal;

/**
 * 게스트/사용자 -> 숙소
 * 리뷰 응답 DTO
 */
@Builder
public record AccommodationReviewResponse (

    // 숙소 ID
    Long id,

    // 평점
    BigDecimal ratingOverall,
    BigDecimal ratingCleanliness,
    BigDecimal ratingAccuracy,
    BigDecimal ratingCheckin,
    BigDecimal ratingCommunication,
    BigDecimal ratingLocation,

    String comment
) {

    // Entity -> DTO
    public static AccommodationReviewResponse from(AccommodationReview accommodationReview) {

        return AccommodationReviewResponse.builder()
                .id(accommodationReview.getId())
                .ratingOverall(accommodationReview.getRatingOverall())
                .ratingCleanliness(accommodationReview.getRatingCleanliness())
                .ratingAccuracy(accommodationReview.getRatingAccuracy())
                .ratingCheckin(accommodationReview.getRatingCheckin())
                .ratingCommunication(accommodationReview.getRatingCommunication())
                .ratingLocation(accommodationReview.getRatingLocation())
                .comment(accommodationReview.getComment())
                .build();
    }
}
