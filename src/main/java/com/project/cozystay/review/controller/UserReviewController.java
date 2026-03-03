package com.project.cozystay.review.controller;


import com.project.cozystay.auth.CustomOAuth2User;
import com.project.cozystay.review.dto.AccommodationReviewResponse;
import com.project.cozystay.review.dto.ReviewResponse;
import com.project.cozystay.review.dto.UserReviewCreateRequest;
import com.project.cozystay.review.dto.UserReviewResponse;
import com.project.cozystay.review.service.UserReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "User Review", description = "사용자(게스트) 리뷰 관련 API")
@RestController
@RequestMapping("/api/review/users")
@RequiredArgsConstructor
public class UserReviewController {

    private final UserReviewService userReviewService;

    /**
     * 호스트 -> 게스트/사용자 리뷰 생성
     */
    @Operation(summary = "게스트 리뷰 생성", description = "호스트가 게스트에 대한 리뷰를 작성합니다.")
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
    @Operation(summary = "게스트 리뷰 목록 조회", description = "특정 게스트가 받은 모든 리뷰를 조회합니다.")
    @GetMapping("/{userId}")
    public ResponseEntity<List<UserReviewResponse>> getUserReviews(
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(userReviewService.getUserReviews(userId));
    }


    /**
     * 게스트 리뷰 수정
     */
    @Operation(summary = "게스트 리뷰 수정", description = "작성한 게스트 리뷰를 수정합니다.")
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
    @Operation(summary = "게스트 리뷰 삭제", description = "작성한 게스트 리뷰를 삭제합니다.")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> deleteUserReview(
            @PathVariable Long reviewId
    ) {

        return ResponseEntity.ok(userReviewService.deleteUserReview(reviewId));
    }

    /**
     * 특정 호스트가 작성한 모든 게스트 리뷰 조회
     */
    @Operation(summary = "호스트가 작성한 리뷰 목록 조회", description = "로그인한 호스트가 작성한 모든 게스트 리뷰를 조회합니다.")
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
    @Operation(summary = "호스트의 특정 게스트 리뷰 조회", description = "로그인한 호스트가 특정 게스트에게 작성한 리뷰를 조회합니다.")
    @GetMapping("/{targetGuestId}/me")
    public ResponseEntity<UserReviewResponse> getUserReviewByHost(
            @PathVariable Long targetGuestId,
            @AuthenticationPrincipal CustomOAuth2User custom
    ) {
        Long reviewerHostId = custom.getId();
        return ResponseEntity.ok(userReviewService.getUserReviewByHost(targetGuestId, reviewerHostId));
    }

}
