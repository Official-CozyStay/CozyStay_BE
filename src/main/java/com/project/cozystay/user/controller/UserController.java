package com.project.cozystay.user.controller;

import com.project.cozystay.auth.CustomOAuth2User;
import com.project.cozystay.user.domain.User;
import com.project.cozystay.user.dto.*;
import com.project.cozystay.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "사용자 프로필 및 관리 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/users")
public class UserController {

    private final UserService userService;

    /**
     * 내 프로필 조회 (마이페이지)
     */
    @Operation(summary = "내 프로필 조회", description = "로그인한 사용자의 프로필 정보를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyProfile(
            @AuthenticationPrincipal CustomOAuth2User principal
    ) {
        User user = principal.getUser();
        UserProfileResponse response = userService.getMyProfile(user);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 내 프로필 수정
     */
    @Operation(summary = "내 프로필 수정", description = "로그인한 사용자의 프로필 정보(닉네임, 이미지 등)를 수정합니다.")
    @PatchMapping("/me/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateMyProfile(
            @AuthenticationPrincipal CustomOAuth2User principal,
            @RequestBody @Valid UserProfileUpdateRequest request
    ) {
        Long userId = principal.getUser().getId();
        UserProfileResponse response = userService.updateMyProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success("프로필이 수정되었습니다.", response));
    }

    /**
     * 내 등급 / 통계 조회
     */
    @Operation(summary = "내 등급 및 통계 조회", description = "로그인한 사용자의 현재 등급과 숙박 통계 정보를 조회합니다.")
    @GetMapping("/me/grade")
    public ResponseEntity<ApiResponse<UserGradeResponse>> getMyGrade(
            @AuthenticationPrincipal CustomOAuth2User principal
    ) {
        Long userId = principal.getUser().getId();
        UserGradeResponse response = userService.getMyGrade(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 호스트 전환
     */
    @Operation(summary = "호스트로 전환", description = "일반 사용자가 호스트 권한을 획득하여 숙소를 등록할 수 있게 합니다.")
    @PostMapping("/me/host")
    public ResponseEntity<ApiResponse<Void>> becomeHost(
            @AuthenticationPrincipal CustomOAuth2User principal
    ) {
        Long userId = principal.getUser().getId();
        userService.becomeHost(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("호스트로 전환되었습니다.", null));
    }


    /**
     * 호스트 공개 프로필 조회
     */
    @Operation(summary = "호스트 공개 프로필 조회", description = "다른 사용자가 볼 수 있는 특정 호스트의 공개 프로필을 조회합니다.")
    @GetMapping("/{userId}/public-profile")
    public ResponseEntity<ApiResponse<PublicUserProfileResponse>> getPublicProfile(
            @PathVariable Long userId
    ) {
        PublicUserProfileResponse response = userService.getPublicProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
