package com.project.cozystay.accommodation.controller;

import com.project.cozystay.accommodation.dto.*;
import com.project.cozystay.accommodation.service.AccommodationImageService;
import com.project.cozystay.accommodation.service.AccommodationService;
import com.project.cozystay.auth.CustomOAuth2User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Accommodation", description = "숙소 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accommodations")
@org.springframework.validation.annotation.Validated
public class AccommodationController {

    private final AccommodationService accommodationService;
    private final AccommodationImageService accommodationImageService;

    /**
     * 숙소 정보 조회 (메인 페이지)
     */
    @Operation(summary = "전체 숙소 목록 조회", description = "메인 페이지에 표시될 전체 숙소 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<AccommodationMainResponseDTO>> getAccommodations() {
        List<AccommodationMainResponseDTO> response =  accommodationService.getAllAccommodations();
        return ResponseEntity.ok(response);
    }

    /**
     * 숙소 생성
     */
    @Operation(summary = "숙소 기본 정보 등록", description = "호스트가 새로운 숙소의 기본 정보를 등록합니다.")
    @PostMapping
    public ResponseEntity<AccommodationResponseDTO> createAccommodation(
            @AuthenticationPrincipal CustomOAuth2User principal,
            @RequestBody @Valid AccommodationRequestDTO request
    ) {
        Long hostId = principal.getId();
        AccommodationResponseDTO response = accommodationService.createAccommodation(request, hostId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 숙소 상세정보 생성
     */
    @Operation(summary = "숙소 상세 정보 등록", description = "숙소의 방 개수, 침대 개수, 가전제품 유무 등 상세 정보를 등록합니다.")
    @PostMapping("/details/{accommodationId}")
    public ResponseEntity<AccommodationDetailResponseDTO> addAccommodationDetail(
            @PathVariable Long accommodationId,
            @RequestBody @Valid AccommodationDetailRequestDTO request,
            @AuthenticationPrincipal CustomOAuth2User principal
    ) {
        Long hostId = principal.getId();
        AccommodationDetailResponseDTO response = accommodationService.addAccommodationDetail(accommodationId, hostId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 숙소 상세정보 조회
     */
    @Operation(summary = "숙소 상세 정보 조회", description = "특정 숙소의 모든 정보(기본, 상세, 이미지, 편의시설)를 한꺼번에 조회합니다.")
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
    @Operation(summary = "숙소 활성화(게시)", description = "작성 중인 숙소를 활성화하여 일반 사용자에게 노출시킵니다.")
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
    @Operation(summary = "숙소 이미지 추가", description = "특정 숙소에 이미지를 추가합니다.")
    @PostMapping(value = "/images/{accommodationId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AccommodationImageResponseDTO> addImage(
            @PathVariable Long accommodationId,
            @AuthenticationPrincipal CustomOAuth2User principal,
            @RequestPart("files") @NotEmpty(message = "이미지 파일은 최소 1개 이상이어야 합니다.") List<MultipartFile> files,
            @RequestPart("metadata") @NotEmpty(message = "이미지 메타데이터는 필수입니다.") List<@Valid AccommodationImageRequestDTO> metadata
    ){
        Long hostId = principal.getId();
        AccommodationImageResponseDTO response = accommodationImageService.addImage(accommodationId, hostId, files, metadata);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 숙소 편의시설 (어매니티) 추가
     */
    @Operation(summary = "숙소 편의시설 추가", description = "특정 숙소에 제공되는 편의시설 정보를 추가합니다.")
    @PostMapping("/amenities/{accommodationId}")
    public ResponseEntity<AccommodationAmenityResponseDTO> addAmenities(
            @PathVariable Long accommodationId,
            @RequestBody @NotEmpty(message = "편의시설 목록은 비어있을 수 없습니다.") List<@Valid AccommodationAmenityRequestDTO> request,
            @AuthenticationPrincipal CustomOAuth2User principal
    ){
        Long hostId = principal.getId();
        AccommodationAmenityResponseDTO response = accommodationService.addAmenities(accommodationId, hostId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 숙소 삭제
     */
    @Operation(summary = "숙소 삭제", description = "등록된 숙소 정보를 삭제합니다.")
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
    @Operation(summary = "숙소 기본 정보 수정", description = "숙소의 제목, 설명, 가격 등 기본 정보를 수정합니다.")
    @PatchMapping("/{accommodationId}")
    public ResponseEntity<AccommodationUpdateResponseDTO> updateAccommodation(
            @PathVariable Long accommodationId,
            @RequestBody @Valid AccommodationUpdateRequestDTO request,
            @AuthenticationPrincipal CustomOAuth2User principal

    ){
        Long hostId = principal.getId();
        AccommodationUpdateResponseDTO response = accommodationService.updateAccommodation(accommodationId, hostId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 숙소 상세 정보 수정
     */
    @Operation(summary = "숙소 상세 정보 수정", description = "숙소의 상세 수치(방 개수 등) 정보를 수정합니다.")
    @PatchMapping("/{accommodationId}/details")
    public ResponseEntity<AccommodationDetailUpdateResponseDTO> updateAccommodationDetail(
            @PathVariable Long accommodationId,
            @RequestBody @Valid AccommodationDetailUpdateRequestDTO request,
            @AuthenticationPrincipal CustomOAuth2User principal
    ){
        Long hostId = principal.getId();
        AccommodationDetailUpdateResponseDTO response = accommodationService.updateAccommodationDetail(accommodationId, hostId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 숙소 이미지 삭제
     */
    @Operation(summary = "숙소 이미지 선택 삭제", description = "특정 숙소의 이미지들을 선택하여 삭제합니다.")
    @DeleteMapping("/{accommodationId}/images")
    public ResponseEntity<AccommodationImageDeleteResponseDTO> deleteAccommodationImages(
            @PathVariable Long accommodationId,
            @RequestParam List<Long> imageIds,
            @AuthenticationPrincipal CustomOAuth2User principal
    ){
        Long hostId = principal.getId();
        AccommodationImageDeleteResponseDTO response = accommodationImageService.deleteAccommodationImages(accommodationId, hostId, imageIds);
        return ResponseEntity.ok(response);

    }

    /**
     * 숙소 이미지 카테고리 추가
     */
    @Operation(summary = "숙소 이미지 카테고리 추가", description = "이미지의 카테고리를 추가합니다.")
    @PostMapping("/{accommodationId}/image-categories")
    public ResponseEntity<AccommodationImageCategoryResponseDTO> createImageCategory(
            @PathVariable Long accommodationId,
            @RequestBody @Valid AccommodationImageCategoryRequestDTO request,
            @AuthenticationPrincipal CustomOAuth2User principal
    ){
        Long hostId = principal.getId();
        AccommodationImageCategoryResponseDTO response = accommodationImageService.createImageCategory(accommodationId, hostId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 숙소 이미지 반환 (카테고리 포함)
     */
    @Operation(summary = "숙소 이미지 조회(카테고리 포함)", description = "숙소의 이미지를 조회합니다.(카테고리 포함")
    @GetMapping("/{accommodationId}/image-categories")
    public ResponseEntity<List<CategoryWithImagesDTO>> getImageCategories(
            @PathVariable Long accommodationId
    ){
        List<CategoryWithImagesDTO> response = accommodationImageService.getImageCategories(accommodationId);
        return ResponseEntity.ok(response);
    }

}

