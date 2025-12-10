package com.project.cozystay.accommodation.controller;

import com.project.cozystay.accommodation.domain.Accommodation;
import com.project.cozystay.accommodation.dto.*;
import com.project.cozystay.accommodation.service.AccommodationService;
import com.project.cozystay.auth.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accommodations")
public class AccommodationController {

    private final AccommodationService accommodationService;

    /**
     * 숙소 정보 조회 (메인 페이지)
     */
    @GetMapping
    public ResponseEntity<List<AccommodationResponseDTO>> getAccommodations() {
        List<AccommodationResponseDTO> response =  accommodationService.getAllAccommodations();
        return ResponseEntity.ok(response);
    }

    /**
     * 숙소 생성
     */
    @PostMapping
    public ResponseEntity<AccommodationResponseDTO> createAccommodation(
            @AuthenticationPrincipal CustomOAuth2User principal,
            @RequestBody AccommodationRequestDTO request
    ) {
        Long hostId = principal.getId();
        AccommodationResponseDTO response = accommodationService.createAccommodation(request, hostId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 숙소 상세정보 생성
     */
    @PostMapping("/details/{accommodationId}")
    public ResponseEntity<AccommodationDetailResponseDTO> addAccommodationDetail(
            @PathVariable Long accommodationId,
            @RequestBody AccommodationDetailRequestDTO request,
            @AuthenticationPrincipal CustomOAuth2User principal
    ) {
        Long hostId = principal.getId();
        AccommodationDetailResponseDTO response = accommodationService.addAccommodationDetail(accommodationId, hostId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 숙소 상세정보 조회
     */
    @GetMapping("/{accommodationId}")
    public ResponseEntity<AccommodationFullResponseDTO> getAccommodationDetail(
            @PathVariable Long accommodationId
    ) {
        AccommodationFullResponseDTO response = accommodationService.getAccommodationDetail(accommodationId);
        return ResponseEntity.ok(response);
    }

    /**
     * 숙소 상태 변경(DRAFT -> ACTIVE)
     */
    @PatchMapping("/{accommodationId}/publish")
    public ResponseEntity<Void> changePublish(
            @PathVariable Long accommodationId,
            @AuthenticationPrincipal CustomOAuth2User principal
    ) {
        Long hostId = principal.getId();
        accommodationService.publish(accommodationId, hostId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 숙소 이미지 추가
     */
    @PostMapping("/images/{accommodationId}")
    public ResponseEntity<AccommodationImageResponseDTO> addImage(
            @PathVariable Long accommodationId,
            @RequestBody List<AccommodationImageRequestDTO> request,
            @AuthenticationPrincipal CustomOAuth2User principal
    ){
        Long hostId = principal.getId();
        AccommodationImageResponseDTO response = accommodationService.addImage(accommodationId, hostId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 숙소 편의시설 (어매니티) 추가
     */
    @PostMapping("/amenities/{accommodationId}")
    public ResponseEntity<AccommodationAmenityResponseDTO> addAmenities(
            @PathVariable Long accommodationId,
            @RequestBody List<AccommodationAmenityRequestDTO> request,
            @AuthenticationPrincipal CustomOAuth2User principal
    ){
        Long hostId = principal.getId();
        AccommodationAmenityResponseDTO response = accommodationService.addAmenities(accommodationId, hostId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 숙소 삭제
     */
    @DeleteMapping("/{accommodationId}")
    public ResponseEntity<AccommodationDeleteResponseDTO> deleteAccommodation(
            @PathVariable Long accommodationId,
            @AuthenticationPrincipal CustomOAuth2User principal
    ){
        Long hostId = principal.getId();
        AccommodationDeleteResponseDTO response = accommodationService.deleteAccommodation(accommodationId, hostId);
        return ResponseEntity.ok(response);
    }

    /**
     * 숙소 기본정보 수정
     */
    @PatchMapping("/{accommodationId}")
    public ResponseEntity<AccommodationUpdateResponseDTO> updateAccommodation(
            @PathVariable Long accommodationId,
            @RequestBody AccommodationUpdateRequestDTO request,
            @AuthenticationPrincipal CustomOAuth2User principal

    ){
        Long hostId = principal.getId();
        AccommodationUpdateResponseDTO response = accommodationService.updateAccommodation(accommodationId, hostId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 숙소 상세 정보 수정
     */
    @PatchMapping("/{accommodationId}/details")
    public ResponseEntity<AccommodationDetailUpdateResponseDTO> updateAccommodationDetail(
            @PathVariable Long accommodationId,
            @RequestBody AccommodationDetailUpdateRequestDTO request,
            @AuthenticationPrincipal CustomOAuth2User principal
    ){
        Long hostId = principal.getId();
        AccommodationDetailUpdateResponseDTO response = accommodationService.updateAccommodationDetail(accommodationId, hostId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 숙소 이미지 삭제
     */
    @DeleteMapping("/{accommodationId}/images")
    public ResponseEntity<AccommodationImageDeleteResponseDTO> deleteAccommodationImages(
            @PathVariable Long accommodationId,
            @RequestBody AccommodationImageDeleteRequestDTO request,
            @AuthenticationPrincipal CustomOAuth2User principal
    ){
        Long hostId = principal.getId();
        AccommodationImageDeleteResponseDTO response = accommodationService.deleteAccommodationImages(accommodationId, hostId, request);
        return ResponseEntity.ok(response);

    }

}

