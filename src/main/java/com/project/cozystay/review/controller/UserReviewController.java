package com.project.cozystay.review.controller;


import com.project.cozystay.auth.CustomOAuth2User;
import com.project.cozystay.review.dto.ReviewResponse;
import com.project.cozystay.review.dto.UserReviewCreateRequest;
import com.project.cozystay.review.dto.UserReviewResponse;
import com.project.cozystay.review.service.UserReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/review/users")
@RequiredArgsConstructor
public class UserReviewController {

    private final UserReviewService userReviewService;

    /**
     * 호스트 -> 게스트/사용자 리뷰 생성
     */
    @PostMapping
    public ResponseEntity<UserReviewResponse> createUserReview(
            @RequestBody UserReviewCreateRequest request,
            @AuthenticationPrincipal CustomOAuth2User custom
    ) {
        Long hostId = custom.getId();
        return ResponseEntity.ok(userReviewService.createUserReview(hostId, request));
    }

    /**
     * 게스트 리뷰 조회
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<UserReviewResponse>> getUserReviews(
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(userReviewService.getUserReviews(userId));
    }


    /**
     * 게스트 리뷰 수정
     */
    @PatchMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateUserReview(
            @PathVariable Long reviewId,
            @RequestBody UserReviewCreateRequest request
    ) {

        return ResponseEntity.ok(userReviewService.updateUserReview(reviewId, request));
    }

    /**
     * 게스트 리뷰 삭제
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> deleteUserReview(
            @PathVariable Long reviewId
    ) {

        return ResponseEntity.ok(userReviewService.deleteUserReview(reviewId));
    }

    /**
     * 특정 호스트가 작성한 모든 게스트 리뷰 조회
     */
    @GetMapping("/all")
    public ResponseEntity<List<UserReviewResponse>> getUserReviewListByHost(
            @AuthenticationPrincipal CustomOAuth2User custom
    ) {
        Long reviewerHostId = custom.getId();
        return ResponseEntity.ok(userReviewService.getUserReviewListByHost(reviewerHostId));
    }

    /**
     * 호스트가 작성한 특정 게스트에 대한 리뷰 조회
     * 주로 게스트 상세페이지에서 '내가 작성한 리뷰'가 먼저 보일 수 있도록 하기 위해서
     */
    @GetMapping("/{targetGuestId}/me")
    public ResponseEntity<UserReviewResponse> getUserReviewByHost(
            @PathVariable Long targetGuestId,
            @AuthenticationPrincipal CustomOAuth2User custom
    ) {
        Long reviewerHostId = custom.getId();
        return ResponseEntity.ok(userReviewService.getUserReviewByHost(targetGuestId, reviewerHostId));
    }

}
