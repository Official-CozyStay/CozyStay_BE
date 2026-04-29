package com.project.cozystay.accommodation.service;


import com.project.cozystay.accommodation.domain.*;
import com.project.cozystay.accommodation.dto.*;
import com.project.cozystay.accommodation.repository.AccommodationRepository;
import com.project.cozystay.accommodation.repository.AmenityRepository;
import com.project.cozystay.review.repository.AccommodationReviewRepository;
import com.project.cozystay.user.domain.User;
import com.project.cozystay.user.repository.UserRepository;
import com.project.cozystay.accommodation.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccommodationService {

    private final AccommodationRepository accommodationRepository;
    private final AmenityRepository amenityRepository;
    private final UserRepository userRepository;
    private final AccommodationReviewRepository accommodationReviewRepository;
    private final AccommodationImageCategoryRepository accommodationImageCategoryRepository;


    //숙소 상태가 ACTIVE인 항목만 조회해서 반환
    @Transactional(readOnly = true)
    public List<AccommodationMainResponseDTO> getAllAccommodations() {
        List<Accommodation> accommodations = accommodationRepository.findAllAccommodations();
        return accommodations.stream()
                .map(AccommodationMainResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public AccommodationResponseDTO createAccommodation(AccommodationRequestDTO request, Long hostId) {
        Accommodation accommodation = request.toEntity(hostId);

        accommodationRepository.save(accommodation);

        return AccommodationResponseDTO.fromEntity(accommodation);
    }

    @Transactional
    public AccommodationDetailResponseDTO addAccommodationDetail(Long accommodationId, Long hostId, AccommodationDetailRequestDTO request) {
        Accommodation accommodation = getAccommodation(accommodationId);

        accommodation.validateNotDeletedAccommodation();

        accommodationHostCheck(accommodation, hostId);

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
                .orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 숙소가 없습니다."));

        accommodation.validateActiveAccommodation();

        User host = userRepository.findById(accommodation.getHostId())
                .orElseThrow(()-> new IllegalArgumentException("호스트 유저가 없습니다."));

        // 리뷰 요약 조회
        ReviewSummaryDTO reviewSummary = accommodationReviewRepository.getReviewSummary(accommodationId);

        return AccommodationFullResponseDTO.fromEntity(
                accommodation,
                host.getNickName(),
                host.getProfileImageUrl(),
                reviewSummary
        );
    }

    @Transactional
    public void publish(Long accommodationId, Long hostId){
        Accommodation accommodation = getAccommodation(accommodationId);

        accommodationHostCheck(accommodation, hostId);

        accommodation.publish();
    }

    @Transactional
    public AccommodationAmenityResponseDTO addAmenities(Long accommodationId, Long hostId, List<AccommodationAmenityRequestDTO> request){
        Accommodation accommodation = getAccommodation(accommodationId);

        accommodationHostCheck(accommodation, hostId);

        for (AccommodationAmenityRequestDTO dto : request) {
            Amenity amenity = amenityRepository.findByName(dto.getName())
                    .orElseGet(() -> amenityRepository.save(dto.toEntity()));

            AccommodationAmenity joinEntity = AccommodationAmenity.builder()
                    .accommodation(accommodation)
                    .amenity(amenity)
                    .build();

            accommodation.addAmenity(joinEntity);
        }

        accommodationRepository.save(accommodation);

        return AccommodationAmenityResponseDTO.builder()
                .accommodationId(accommodationId)
                .count(request.size())
                .message("편의시설 등록 완료")
                .build();
    }

    @Transactional
    public AccommodationDeleteResponseDTO deleteAccommodation(Long accommodationId, Long hostId){
        Accommodation accommodation = getAccommodation(accommodationId);

        accommodationHostCheck(accommodation, hostId);
        accommodation.markDelete();

        return AccommodationDeleteResponseDTO.builder()
                .accommodationId(accommodationId)
                .message("숙소가 삭제되었습니다")
                .build();
    }

    @Transactional
    public AccommodationUpdateResponseDTO updateAccommodation(
            Long accommodationId,
            Long hostId,
            AccommodationUpdateRequestDTO request
    ){
        Accommodation accommodation = getAccommodation(accommodationId);

        accommodationHostCheck(accommodation, hostId);

        accommodation.update(request);

        return AccommodationUpdateResponseDTO.builder()
                .accommodationId(accommodation.getId())
                .title(accommodation.getTitle())
                .build();
    }

    @Transactional
    public AccommodationDetailUpdateResponseDTO updateAccommodationDetail(
            Long accommodationId,
            Long hostId,
            AccommodationDetailUpdateRequestDTO request
    ){
        Accommodation accommodation = getAccommodation(accommodationId);

        accommodationHostCheck(accommodation, hostId);

        //AccommodationDetail 엔티티도 영속 상태로 진입
        AccommodationDetail detail = accommodation.getDetail();
        if(detail == null){
            throw new IllegalStateException("상세 정보가 존재하지 않습니다.");
        }

        detail.update(request);

        return AccommodationDetailUpdateResponseDTO.builder()
                .accommodationId(accommodation.getId())
                .message("숙소 상세정보 수정 완료")
                .build();
    }


    /**
     * 숙소의 주인과 요청한 사람이 맞는지 비교하는 공통 메서드
     */
    private void accommodationHostCheck(Accommodation accommodation, Long hostId){
        if (!accommodation.getHostId().equals(hostId)){
            throw new IllegalStateException("숙소의 소유자만 수정할 수 있습니다.");
        }
    }

    private Accommodation getAccommodation(Long accommodationId){
        return accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 숙소가 없습니다."));
    }
}
