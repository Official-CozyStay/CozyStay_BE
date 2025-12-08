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
            @PathVariable Long userId) {

        return ResponseEntity.ok(userReviewService.getUserReviews(userId));
    }


    /**
     * 게스트 리뷰 수정
     */
    @PatchMapping()
    public ResponseEntity<ReviewResponse> updateUserReview(
            @RequestBody UserReviewCreateRequest request,
            @AuthenticationPrincipal CustomOAuth2User custom) {

        Long reviewerId = custom.getId();
        return ResponseEntity.ok(userReviewService.updateUserReview(request, reviewerId));
    }

    /**
     * 게스트 리뷰 삭제
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<ReviewResponse> deleteUserReview(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomOAuth2User custom) {

        Long reviewerId = custom.getId();
        return ResponseEntity.ok(userReviewService.deleteUserReview(userId, reviewerId));
    }

}
