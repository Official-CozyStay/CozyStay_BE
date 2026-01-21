package com.project.cozystay.review.dto;

import com.project.cozystay.accommodation.domain.Accommodation;
import com.project.cozystay.booking.domain.Booking;
import com.project.cozystay.comment.domain.Comment;
import com.project.cozystay.review.domain.AccommodationReview;
import com.project.cozystay.user.domain.User;

import java.math.BigDecimal;

/**
 * 게스트/사용자 -> 숙소
 * 리뷰 생성 요청 DTO
 */
public record AccommodationReviewCreateRequest(

    // 숙소 ID
    Long accommodationId,
    Long bookingId,

    // 평점
    BigDecimal ratingCleanliness,
    BigDecimal ratingAccuracy,
    BigDecimal ratingCheckin,
    BigDecimal ratingCommunication,
    BigDecimal ratingLocation,

    // 리뷰
    String reviewComment
) {

    // 기본 생성자
    public AccommodationReviewCreateRequest {
        // null 이면 0
        ratingCleanliness = (ratingCleanliness == null) ? BigDecimal.ZERO : ratingCleanliness;
        ratingAccuracy = (ratingAccuracy == null) ? BigDecimal.ZERO : ratingAccuracy;
        ratingCheckin = (ratingCheckin == null) ? BigDecimal.ZERO : ratingCheckin;
        ratingCommunication = (ratingCommunication == null) ? BigDecimal.ZERO : ratingCommunication;
        ratingLocation = (ratingLocation == null) ? BigDecimal.ZERO : ratingLocation;

        // comment가 null이면 빈 문자열
        reviewComment = (reviewComment == null) ? "" : reviewComment;
    }

    // DTO -> Entity
    public AccommodationReview toEntity(Booking booking,
                                        Accommodation accommodation,
                                        User guest,
                                        BigDecimal ratingOverall) {
        return AccommodationReview.of(
                booking,
                accommodation,
                guest,
                this,
                ratingOverall
        );
    }
}
