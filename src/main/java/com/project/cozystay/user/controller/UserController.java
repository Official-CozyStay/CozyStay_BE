package com.project.cozystay.user.controller;

import com.project.cozystay.auth.CustomOAuth2User;
import com.project.cozystay.user.dto.*;
import com.project.cozystay.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    /**
     * 내 프로필 조회 (마이페이지)
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyProfile(
            @AuthenticationPrincipal CustomOAuth2User principal
    ) {
        Long userId = principal.getId();
        UserProfileResponse response = userService.getMyProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 내 프로필 수정
     */
    @PatchMapping("/me/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateMyProfile(
            @AuthenticationPrincipal CustomOAuth2User principal,
            @RequestBody @Valid UserProfileUpdateRequest request
    ) {
        Long userId = principal.getId();
        UserProfileResponse response = userService.updateMyProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success("프로필이 수정되었습니다.", response));
    }

    /**
     * 내 등급 / 통계 조회
     */
    @GetMapping("/me/grade")
    public ResponseEntity<ApiResponse<UserGradeResponse>> getMyGrade(
            @AuthenticationPrincipal CustomOAuth2User principal
    ) {
        Long userId = principal.getId();
        UserGradeResponse response = userService.getMyGrade(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 호스트 전환
     */
    @PostMapping("/me/host")
    public ResponseEntity<ApiResponse<Void>> becomeHost(
            @AuthenticationPrincipal CustomOAuth2User principal
    ) {
        Long userId = principal.getId();
        userService.becomeHost(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("호스트로 전환되었습니다.", null));
    }


    /**
     * 호스트 공개 프로필 조회
     */
    @GetMapping("/{userId}/public-profile")
    public ResponseEntity<ApiResponse<PublicUserProfileResponse>> getPublicProfile(
            @PathVariable Long userId
    ) {
        PublicUserProfileResponse response = userService.getPublicProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
