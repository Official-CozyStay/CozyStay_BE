package com.project.cozystay.favorite.controller;


import com.project.cozystay.auth.CustomOAuth2User;
import com.project.cozystay.favorite.dto.*;
import com.project.cozystay.favorite.service.FavoriteFacade;
import com.project.cozystay.favorite.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Favorite", description = "즐겨찾기(위시리스트) 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final FavoriteFacade favoriteFacade;

    /**
     * 즐겨찾기 목록 조회
     */
    @Operation(summary = "즐겨찾기 폴더 목록 조회", description = "사용자의 모든 즐겨찾기 폴더 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<FavoriteResponseDTO>> getFavorites(
            @AuthenticationPrincipal CustomOAuth2User principal) {
        Long userId = principal.getId();
        List<FavoriteResponseDTO> response = favoriteService.getFavorites(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 즐겨찾기 생성
     */
    @Operation(summary = "즐겨찾기 폴더 생성", description = "새로운 즐겨찾기 폴더를 생성합니다.")
    @PostMapping
    public ResponseEntity<FavoriteCreateResponseDTO> createFavorite(
            @AuthenticationPrincipal CustomOAuth2User principal,
            @RequestBody FavoriteCreateRequestDTO request
    ){
        Long userId = principal.getId();
        FavoriteCreateResponseDTO response = favoriteFacade.createFavorite(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 즐겨찾기 상세 조회
     */
    @Operation(summary = "즐겨찾기 폴더 상세 조회", description = "특정 즐겨찾기 폴더와 그 안에 포함된 숙소 목록을 조회합니다.")
    @GetMapping("/{favoriteId}")
    public ResponseEntity<FavoriteDetailResponseDTO> getFavorite(
            @AuthenticationPrincipal CustomOAuth2User principal,
            @PathVariable Long favoriteId
    ){
        Long userId = principal.getId();
        FavoriteDetailResponseDTO response = favoriteService.getFavoriteDetail(userId, favoriteId);
        return ResponseEntity.ok(response);
    }

    /**
     * 즐겨찾기 수정
     */
    @Operation(summary = "즐겨찾기 폴더 수정", description = "즐겨찾기 폴더의 이름이나 설명을 수정합니다.")
    @PatchMapping("/{favoriteId}")
    public ResponseEntity<Void> updateFavorite(
            @AuthenticationPrincipal CustomOAuth2User principal,
            @PathVariable Long favoriteId,
            @RequestBody FavoriteUpdateRequestDTO request
    ) {
        Long userId = principal.getId();
        favoriteService.updateFavorite(userId, favoriteId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * 즐겨찾기 삭제
     */
    @Operation(summary = "즐겨찾기 폴더 삭제", description = "즐겨찾기 폴더를 삭제합니다.")
    @DeleteMapping("/{favoriteId}")
    public ResponseEntity<Void> deleteFavorite(
            @AuthenticationPrincipal CustomOAuth2User principal,
            @PathVariable Long favoriteId
    ){
        Long userId = principal.getId();
        favoriteService.deleteFavorite(userId, favoriteId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 즐겨찾기 숙소 추가
     */
    @Operation(summary = "즐겨찾기 폴더에 숙소 추가", description = "특정 즐겨찾기 폴더에 숙소를 추가합니다.")
    @PostMapping("/{favoriteId}/accommodations/{accommodationId}")
    public ResponseEntity<Void> addAccommodation(
            @AuthenticationPrincipal CustomOAuth2User principal,
            @PathVariable Long favoriteId,
            @PathVariable Long accommodationId
    ) {
        Long userId = principal.getId();
        favoriteService.addAccommodation(userId, favoriteId, accommodationId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 즐겨찾기 숙소 삭제
     */
    @Operation(summary = "즐겨찾기 폴더에서 숙소 삭제", description = "특정 즐겨찾기 폴더에서 숙소를 제거합니다.")
    @DeleteMapping("/{favoriteId}/accommodations/{accommodationId}")
    public ResponseEntity<Void> deleteAccommodation(
            @AuthenticationPrincipal CustomOAuth2User principal,
            @PathVariable Long favoriteId,
            @PathVariable Long accommodationId
    ){
        Long userId = principal.getId();
        favoriteService.deleteAccommodation(userId, favoriteId, accommodationId);
        return ResponseEntity.noContent().build();
    }
}
