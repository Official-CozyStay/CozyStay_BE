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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Accommodation Review", description = "숙소 리뷰 관련 API")
@RestController
@RequestMapping("/api/review/hostel")
@RequiredArgsConstructor
public class AccommodationReviewController {

    private final AccommodationReviewService accommodationReviewService;

    /**
     * 게스트 -> 숙소 리뷰 생성
     */
    @Operation(summary = "숙소 리뷰 생성", description = "게스트가 이용한 숙소에 대한 리뷰를 작성합니다.")
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
    @Operation(summary = "숙소 리뷰 목록 조회", description = "특정 숙소에 등록된 모든 리뷰를 조회합니다.")
    @GetMapping("/{accId}")
    public ResponseEntity<List<AccommodationReviewResponse>> getAccommodationReviews(
            @PathVariable Long accId
    ) {
        return ResponseEntity.ok(accommodationReviewService.getAccommodationReviews(accId));
    }


    /**
     * 숙소 리뷰 수정
     */
    @Operation(summary = "숙소 리뷰 수정", description = "작성한 숙소 리뷰를 수정합니다.")
    @PatchMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateAccommodationReview(
            @PathVariable Long reviewId,
            @RequestBody AccommodationReviewCreateRequest request
    ) {

        return ResponseEntity.ok(accommodationReviewService.updateAccommodationReview(reviewId, request));
    }


    /**
     * 숙소 리뷰 삭제
     */
    @Operation(summary = "숙소 리뷰 삭제", description = "작성한 숙소 리뷰를 삭제합니다.")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> deleteAccommodationReview(
            @PathVariable Long reviewId
    ) {

        return ResponseEntity.ok(accommodationReviewService.deleteAccommodationReview(reviewId));
    }

    /**
     * 게스트가 작성한 모든 숙소 리뷰 조회
     */
    @Operation(summary = "게스트가 작성한 리뷰 목록 조회", description = "로그인한 게스트가 작성한 모든 숙소 리뷰를 조회합니다.")
    @GetMapping("/all")
    public ResponseEntity<List<AccommodationReviewResponse>> getAccommodationReviewListByGuest(
            @AuthenticationPrincipal CustomOAuth2User custom
    ) {
        Long guestId = custom.getId();
        return ResponseEntity.ok(accommodationReviewService.getAccommodationReviewListByGuest(guestId));
    }

    /**
     * 게스트가 작성한 특정 숙소 리뷰 조회
     * 주로 숙소 상세페이지에서 '내가 작성한 리뷰'가 먼저 보일 수 있도록 하기 위해서
     */
    @Operation(summary = "게스트의 특정 숙소 리뷰 조회", description = "로그인한 게스트가 특정 숙소에 작성한 리뷰를 조회합니다.")
    @GetMapping("/{accId}/me")
    public ResponseEntity<AccommodationReviewResponse> getAccommodationReviewByGuest(
            @PathVariable Long accId,
            @AuthenticationPrincipal CustomOAuth2User custom
    ) {
        Long guestId = custom.getId();
        return ResponseEntity.ok(accommodationReviewService.getAccommodationReviewByGuest(guestId, accId));
    }
}
