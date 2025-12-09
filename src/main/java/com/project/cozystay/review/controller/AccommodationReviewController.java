package com.project.cozystay.review.controller;


import com.project.cozystay.auth.CustomOAuth2User;
import com.project.cozystay.review.dto.AccommodationReviewCreateRequest;
import com.project.cozystay.review.dto.AccommodationReviewResponse;
import com.project.cozystay.review.dto.ReviewResponse;
import com.project.cozystay.review.service.AccommodationReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/review/hostel")
@RequiredArgsConstructor
public class AccommodationReviewController {

    private final AccommodationReviewService accommodationReviewService;

    /**
     * 게스트 -> 숙소 리뷰 생성
     */
    @PostMapping
    public ResponseEntity<AccommodationReviewResponse> createAccommodationReview(
            @RequestBody AccommodationReviewCreateRequest request,
            @AuthenticationPrincipal CustomOAuth2User custom
    ) {
        Long guestId = custom.getId();
        return ResponseEntity.ok(accommodationReviewService.createAccommodationReview(guestId, request));
    }

    /**
     * 숙소 리뷰 조회
     */
    @GetMapping("/{accId}")
    public ResponseEntity<List<AccommodationReviewResponse>> getAccommodationReviews(
            @PathVariable Long accId
    ) {
        return ResponseEntity.ok(accommodationReviewService.getAccommodationReviews(accId));
    }


    /**
     * 숙소 리뷰 수정
     */
    @PatchMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateAccommodationReview(
            @RequestParam Long reviewId,
            @RequestBody AccommodationReviewCreateRequest request) {

        return ResponseEntity.ok(accommodationReviewService.updateAccommodationReview(reviewId, request));
    }


    /**
     * 숙소 리뷰 삭제
     */
    @DeleteMapping("/{accId}")
    public ResponseEntity<ReviewResponse> deleteAccommodationReview(
            @PathVariable Long accId,
            @AuthenticationPrincipal CustomOAuth2User custom
    ) {
        Long guestId = custom.getId();
        return ResponseEntity.ok(accommodationReviewService.deleteAccommodationReview(accId, guestId));
    }
}
