package com.project.cozystay.accommodation.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.cozystay.accommodation.domain.Accommodation;
import com.project.cozystay.accommodation.domain.AccommodationImage;
import com.project.cozystay.accommodation.dto.AccommodationImageDeleteResponseDTO;
import com.project.cozystay.accommodation.dto.AccommodationImageRequestDTO;
import com.project.cozystay.accommodation.dto.AccommodationImageResponseDTO;
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

        List<AccommodationImage> newImages = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            String url = s3Service.uploadFile(files.get(i));
            AccommodationImageRequestDTO dto = request.get(i);
            AccommodationImage image = AccommodationImage.builder()
                    .imageUrl(url)
                    .displayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0)
                    .primary(Boolean.TRUE.equals(dto.getIsPrimary()))
                    .build();
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

    private void accommodationHostCheck(Accommodation accommodation, Long hostId) {
        if (!accommodation.getHostId().equals(hostId)) {
            throw new IllegalStateException("숙소의 소유자만 수정할 수 있습니다.");
        }

    }
}
