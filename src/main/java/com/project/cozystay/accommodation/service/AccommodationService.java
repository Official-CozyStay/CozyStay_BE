package com.project.cozystay.accommodation.service;


import com.project.cozystay.accommodation.domain.*;
import com.project.cozystay.accommodation.dto.*;
import com.project.cozystay.accommodation.repository.AccommodationAmenityRepository;
import com.project.cozystay.accommodation.repository.AccommodationImageRepository;
import com.project.cozystay.accommodation.repository.AccommodationRepository;
import com.project.cozystay.accommodation.repository.AmenityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccommodationService {

    private final AccommodationRepository accommodationRepository;
    private final AccommodationImageRepository accommodationImageRepository;
    private final AccommodationAmenityRepository accommodationAmenityRepository;
    private final AmenityRepository amenityRepository;

    @Transactional(readOnly = true)
    public List<AccommodationResponseDTO> getAllAccommodations() {
        List<Accommodation> accommodations = accommodationRepository.findAllAccommodations();
        return accommodations.stream()
                .map(AccommodationResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public AccommodationResponseDTO createAccommodation(AccommodationRequestDTO request) {
        Accommodation accommodation = request.toEntity();

        accommodationRepository.save(accommodation);

        return AccommodationResponseDTO.fromEntity(accommodation);
    }

    @Transactional
    public AccommodationDetailResponseDTO addAccommodationDetail(Long accommodationId, AccommodationDetailRequestDTO request) {

        // 숙소 ID 조회
        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 숙소가 없습니다."));

        AccommodationDetail detail = request.toEntity();

        accommodation.addDetail(detail);

        accommodationRepository.save(accommodation);

        return AccommodationDetailResponseDTO.builder()
                .message("숙소 상세 정보 등록 완료")
                .accommodationId(accommodationId)
                .build();
    }

    @Transactional(readOnly = true)
    public AccommodationFullResponseDTO getAccommodationDetail(Long accommodationId) {
        Accommodation accommodation = accommodationRepository.findDetailById(accommodationId)
                .orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 숙소가 없습니다"));

        return AccommodationFullResponseDTO.fromEntity(accommodation);
    }

    @Transactional
    public void publish(Long accommodationId){
        Accommodation accommodation = accommodationRepository.findDetailById(accommodationId)
                .orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 숙소가 없습니다."));

        accommodation.publish();
    }

    @Transactional
    public AccommodationImageResponseDTO addImage(Long accommodationId, List<AccommodationImageRequestDTO> request) {
        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 숙소가 없습니다"));

        List<Long>Id = new ArrayList<>();

        for (AccommodationImageRequestDTO dto : request) {

            AccommodationImage image = dto.toEntity();

            image.assignAccommodation(accommodation);

            accommodationImageRepository.save(image);

            Id.add(image.getImageId());
        }


        accommodationRepository.save(accommodation);

        return AccommodationImageResponseDTO.builder()
                .message("이미지 등록 완료")
                .accommodationId(accommodationId)
                .imageId(Id)
                .build();
    }

    @Transactional
    public AccommodationAmenityResponseDTO addAmenities(Long accommodationId, List<AccommodationAmenityRequestDTO> request){
        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 숙소가 없습니다"));

        int count = 0;

        for (AccommodationAmenityRequestDTO dto : request) {

            Amenity amenity = amenityRepository.save(
                    Amenity.builder()
                            .name(dto.getName())
                            .icon(dto.getIcon())
                            .category(dto.getCategory())
                            .build()
            );

            AccommodationAmenity joinEntity = AccommodationAmenity.builder()
                    .accommodation(accommodation)
                    .amenity(amenity)
                    .build();

            accommodationAmenityRepository.save(joinEntity);

            count++;
        }
        return AccommodationAmenityResponseDTO.builder()
                .accommodationId(accommodationId)
                .count(count)
                .message("편의시설 등록 완료")
                .build();
    }
}
