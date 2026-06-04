package com.project.cozystay.accommodation.service;

import com.project.cozystay.accommodation.domain.Accommodation;
import com.project.cozystay.accommodation.domain.AccommodationImage;
import com.project.cozystay.accommodation.domain.AccommodationImageCategory;
import com.project.cozystay.accommodation.dto.*;
import com.project.cozystay.accommodation.repository.AccommodationImageCategoryRepository;
import com.project.cozystay.accommodation.repository.AccommodationImageRepository;
import com.project.cozystay.accommodation.repository.AccommodationRepository;
import com.project.cozystay.common.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccommodationImageService {
    private final AccommodationRepository accommodationRepository;
    private final AccommodationImageRepository accommodationImageRepository;
    private final AccommodationImageCategoryRepository accommodationImageCategoryRepository;
    private final S3Service s3Service;

    @Transactional
    public AccommodationImageResponseDTO addImage(
            Long accommodationId,
            Long hostId,
            List<MultipartFile> files,
            List<AccommodationImageRequestDTO> request) {

        if (files.size() != request.size()) {
            throw new IllegalArgumentException("파일 개수와 메타데이터 개수가 일치해야 합니다.");
        }

        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 숙소가 없습니다."));

        accommodationHostCheck(accommodation, hostId);

        long requestPrimaryCount = request.stream()
                .filter(dto -> Boolean.TRUE.equals(dto.isPrimary()))
                .count();

        if (requestPrimaryCount > 1) {
            throw new IllegalArgumentException("대표 이미지는 하나만 지정할 수 있습니다.");
        }

        boolean hasRequestPrimary = requestPrimaryCount == 1;

        boolean hasExistingPrimary = accommodation.getImages().stream()
                .anyMatch(AccommodationImage::isPrimary);

        if (hasRequestPrimary) {
            accommodation.getImages().forEach(AccommodationImage::unsetPrimary);
        }

        //대표 이미지가 없을 경우, 대표 이미지를 자동으로 선택 (기본값은 첫 번째 이미지)
        int fallbackPrimaryIndex = 0;

        if (!hasRequestPrimary && !hasExistingPrimary) {
            int minDisplayOrder = Integer.MAX_VALUE;

            for (int i = 0; i < request.size(); i++) {
                Integer displayOrder = request.get(i).displayOrder();

                if (displayOrder != null && displayOrder< minDisplayOrder) {
                    minDisplayOrder = displayOrder;
                    fallbackPrimaryIndex = i;
                }
            }
        }

        List<AccommodationImage> newImages = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            String url = s3Service.uploadFile(files.get(i));
            AccommodationImageRequestDTO dto = request.get(i);

            boolean isPrimary = Boolean.TRUE.equals(dto.isPrimary());

            if (!hasRequestPrimary && !hasExistingPrimary) {
                isPrimary = i == fallbackPrimaryIndex;
            }

            AccommodationImage image = AccommodationImage.create(
                    url,
                    dto.displayOrder(),
                    isPrimary
            );

            if (dto.categoryId() != null) {
                AccommodationImageCategory category = accommodationImageCategoryRepository
                        .findByIdAndAccommodation_Id(dto.categoryId(), accommodationId)
                        .orElseThrow(() -> new IllegalArgumentException("해당 숙소에 속한 이미지 카테고리를 찾을 수 없습니다."));

                image.assignCategory(category);
            }

            accommodation.addImage(image);
            newImages.add(image);
        }
        accommodationRepository.save(accommodation);

        List<Long> imageIds = newImages.stream()
                .map(AccommodationImage::getId)
                .toList();

        return AccommodationImageResponseDTO.builder()
                .message("이미지 등록 완료")
                .accommodationId(accommodationId)
                .imageId(imageIds)
                .build();
    }

    @Transactional
    public AccommodationImageDeleteResponseDTO deleteAccommodationImages(
            Long accommodationId,
            Long hostId,
            List<Long> imageIds
    ) {
        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 숙소가 없습니다."));

        accommodationHostCheck(accommodation, hostId);

        List<AccommodationImage> images = accommodationImageRepository
                .findAllByIdInAndAccommodationId(imageIds, accommodationId);

        if (images.size() != imageIds.size()) {
            throw new IllegalArgumentException("일부 이미지가 존재하지 않거나 해당 숙소에 속하지 않습니다.");
        }

        for (AccommodationImage image : images) {
            s3Service.deleteFile(image.getImageUrl());
        }

        accommodationImageRepository.deleteAll(images);

        return AccommodationImageDeleteResponseDTO.builder()
                .imageIds(imageIds)
                .message("이미지 삭제 완료")
                .build();
    }

    @Transactional
    public AccommodationImageCategoryResponseDTO createImageCategory(
            Long accommodationId,
            Long hostId,
            AccommodationImageCategoryRequestDTO request){
        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new IllegalArgumentException("숙소를 찾을 수 없습니다."));

        accommodation.validateNotDeletedAccommodation();

        accommodationHostCheck(accommodation, hostId);

        AccommodationImageCategory category = AccommodationImageCategory.create(
                accommodation, request.name(), request.displayOrder());

        category = accommodationImageCategoryRepository.save(category);

        return AccommodationImageCategoryResponseDTO.builder()
                .categoryId(category.getId())
                .name(category.getName())
                .displayOrder(category.getDisplayOrder())
                .build();
    }


    @Transactional(readOnly = true)
    public List<CategoryWithImagesDTO> getImageCategories(Long accommodationId) {
        Accommodation accommodation = accommodationRepository.findByIdWithImagesAndCategories(accommodationId)
                .orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 숙소가 없습니다."));

        return CategoryWithImagesDTO.fromAccommodation(accommodation);
    }

    @Transactional
    public AccommodationPrimaryImageResponseDTO updatePrimaryImage(Long accommodationId, Long hostId, Long imageId) {
        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 숙소가 없습니다."));

        accommodationHostCheck(accommodation, hostId);

        AccommodationImage targetImage = accommodationImageRepository.findByIdAndAccommodationId(imageId, accommodationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 숙소의 이미지를 찾을 수 없습니다."));

        Long beforePrimaryImageId = accommodation.getImages().stream()
                .filter(AccommodationImage::isPrimary)
                .map(AccommodationImage::getId)
                .findFirst()
                .orElse(null);

        accommodation.getImages().forEach(AccommodationImage::unsetPrimary);
        targetImage.onPrimary();

        return new AccommodationPrimaryImageResponseDTO(
                accommodationId,
                beforePrimaryImageId,
                targetImage.getId(),
                "대표 이미지가 변경되었습니다."
        );
    }

    private void accommodationHostCheck(Accommodation accommodation, Long hostId) {
        if (!accommodation.getHostId().equals(hostId)) {
            throw new IllegalStateException("숙소의 소유자만 수정할 수 있습니다.");
        }

    }
}
