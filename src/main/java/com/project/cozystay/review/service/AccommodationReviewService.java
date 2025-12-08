package com.project.cozystay.review.service;

import com.project.cozystay.accommodation.domain.Accommodation;
import com.project.cozystay.accommodation.repository.AccommodationRepository;
import com.project.cozystay.review.domain.AccommodationReview;
import com.project.cozystay.review.dto.AccommodationReviewCreateRequest;
import com.project.cozystay.review.dto.AccommodationReviewResponse;
import com.project.cozystay.review.dto.ReviewResponse;
import com.project.cozystay.review.repository.AccommodationReviewRepository;
import com.project.cozystay.user.domain.User;
import com.project.cozystay.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AccommodationReviewService {

    private final AccommodationReviewRepository accommodationReviewRepository;
    private final AccommodationRepository accommodationRepository;
    private final UserRepository userRepository;

    // 숙소 리뷰 생성
    @Transactional
    public AccommodationReviewResponse createAccommodationReview(Long guestId, AccommodationReviewCreateRequest request) {
        User guest = userRepository.findById(guestId)
                .orElseThrow(() -> new IllegalArgumentException("게스트가 존재하지 않습니다."));

        Accommodation accommodation = accommodationRepository.findById(request.accommodationId())
                .orElseThrow(() -> new IllegalArgumentException("숙소가 존재하지 않습니다."));


        //TODO 이 사용자가 해당 숙소를 실제로 이용했는지(예약 완료 여부) 검증 로직 추가
        //TODO bookingId를 통해 중복 생성 방지 로직 추가
        BigDecimal ratingOverall = calculateRating(request);
        AccommodationReview review = request.toEntity(accommodation, guest, ratingOverall);

        accommodationReviewRepository.save(review);

        guest.increaseReviewCount(); // 리뷰 카운트 +1

        return AccommodationReviewResponse.from(review);
    }


    // 숙소 리뷰 조회
    public List<AccommodationReviewResponse> getAccommodationReviews(Long accId){

        if (!accommodationRepository.existsById(accId)) {
            throw new IllegalArgumentException("존재하지 않는 숙소입니다. id=" + accId);
        }


        List<AccommodationReview> accommodationReviewList = accommodationReviewRepository.findByAccommodation_AccommodationId(accId);

        return accommodationReviewList.stream()
                .map(AccommodationReviewResponse::from)
                .toList();

    }

    // 숙소 리뷰 수정
    //TODO 사장님이 답글을 달기 전까지만 수정 가능하도록하는 로직 추가 필요
    @Transactional
    public ReviewResponse updateAccommodationReview(Long guestId, AccommodationReviewCreateRequest request){

        AccommodationReview review = accommodationReviewRepository.findByGuestAndAccommodation(guestId, request.accommodationId())
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰가 존재하지 않습니다."));

        BigDecimal ratingOverall = calculateRating(request);
        review.update(request, ratingOverall);

        return new ReviewResponse("리뷰를 수정하였습니다");
    }


    // 숙소 리뷰 삭제
    @Transactional
    public ReviewResponse deleteAccommodationReview(Long guestId, Long accId){
        AccommodationReview review = accommodationReviewRepository.findByGuestAndAccommodation(guestId, accId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰가 존재하지 않습니다."));

        accommodationReviewRepository.delete(review);

        return new ReviewResponse("리뷰를 삭제하였습니다");
    }

    // TODO 특정 사용자가 작성한 숙소 리뷰들 조회
    // TODO 특정 사용자 + 특정 숙소 리뷰 조회

    // 숙소 평점 계산 헬퍼 메서드
    public BigDecimal calculateRating(AccommodationReviewCreateRequest request){

        BigDecimal sum = request.ratingCleanliness()
                .add(request.ratingAccuracy())
                .add(request.ratingCheckin())
                .add(request.ratingLocation())
                .add(request.ratingCommunication());

        return sum.divide(BigDecimal.valueOf(5), 1, RoundingMode.HALF_UP);
    }
}
