package com.project.cozystay.favorite.controller;


import com.project.cozystay.auth.CustomOAuth2User;
import com.project.cozystay.favorite.dto.FavoriteCreateRequestDTO;
import com.project.cozystay.favorite.dto.FavoriteDetailResponseDTO;
import com.project.cozystay.favorite.dto.FavoriteResponseDTO;
import com.project.cozystay.favorite.dto.FavoriteUpdateRequestDTO;
import com.project.cozystay.favorite.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    /**
     * 즐겨찾기 목록 조회
     */
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
    @PostMapping
    public ResponseEntity<Void> createFavorite(
            @AuthenticationPrincipal CustomOAuth2User principal,
            @RequestBody FavoriteCreateRequestDTO request
    ){
        Long userId = principal.getId();
        favoriteService.createFavorite(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 즐겨찾기 상세 조회
     */
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
