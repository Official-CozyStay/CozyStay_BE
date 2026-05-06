package com.project.cozystay.review.service;

import com.project.cozystay.accommodation.domain.Accommodation;
import com.project.cozystay.accommodation.repository.AccommodationRepository;
import com.project.cozystay.booking.domain.Booking;
import com.project.cozystay.booking.domain.BookingStatus;
import com.project.cozystay.booking.exception.AccommodationNotFoundException;
import com.project.cozystay.booking.exception.BookingNotFoundException;
import com.project.cozystay.booking.repository.BookingRepository;
import com.project.cozystay.comment.dto.CommentResponseDTO;
import com.project.cozystay.review.domain.AccommodationReview;
import com.project.cozystay.review.dto.AccommodationReviewCreateRequest;
import com.project.cozystay.review.dto.AccommodationReviewResponse;
import com.project.cozystay.review.dto.ReviewResponse;
import com.project.cozystay.review.exception.ReviewAlreadyExistsException;
import com.project.cozystay.review.exception.ReviewCreationNotAllowedException;
import com.project.cozystay.review.exception.ReviewNotFoundException;
import com.project.cozystay.review.exception.ReviewUpdateNotAllowedException;

import com.project.cozystay.review.repository.AccommodationReviewRepository;
import com.project.cozystay.user.domain.User;
import com.project.cozystay.user.exception.UserNotFoundException;
import com.project.cozystay.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AccommodationReviewService {

    private final AccommodationReviewRepository accommodationReviewRepository;
    private final AccommodationRepository accommodationRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;


    private static final BigDecimal NUMBER_OF_RATING_CRITERIA = new BigDecimal("5");

    // 숙소 리뷰 생성
    @Transactional
    public AccommodationReviewResponse createAccommodationReview(Long guestId, AccommodationReviewCreateRequest request) {
        User guest = userRepository.findById(guestId)
                .orElseThrow(() -> new UserNotFoundException(guestId));

        Booking booking = bookingRepository.findById(request.bookingId())
                .orElseThrow(() -> new BookingNotFoundException(request.bookingId()));

        // 중복 리뷰 생성 방지
        if (accommodationReviewRepository.existsByBooking_Id(request.bookingId())) {
            throw new ReviewAlreadyExistsException(request.bookingId());
        }

        // 예약자와 리뷰 작성자가 동일한지 확인
        if (!Objects.equals(booking.getGuestId(), guestId)) {
            throw new AccessDeniedException("리뷰를 작성할 권한이 없습니다.");
        }

        // 예약 상태가 COMPLETED 인지 확인
        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new ReviewCreationNotAllowedException("체크아웃이 완료된 예약에 대해서만 리뷰를 작성할 수 있습니다.");
        }

        BigDecimal ratingOverall = calculateRating(request);
        AccommodationReview review = AccommodationReview.of(booking, booking.getAccommodation(), guest, request, ratingOverall);

        accommodationReviewRepository.save(review);

        // 숙소 평점 및 리뷰 수 업데이트
        updateAccommodationStats(booking.getAccommodation().getId());

        guest.increaseReviewCount(); // 리뷰 카운트 +1

        return new AccommodationReviewResponse(
                review.getId(),
                review.getBooking().getId(),
                review.getGuest().getUsername(),
                review.getGuest().getProfileImageUrl(),
                review.getRatingOverall(),
                review.getRatingCleanliness(),
                review.getRatingAccuracy(),
                review.getRatingCheckin(),
                review.getRatingCommunication(),
                review.getRatingLocation(),
                review.getReviewComment(),
                review.getComment() != null ? CommentResponseDTO.from(review.getComment()) : null
        );
    }


    // 숙소 리뷰 조회
    public List<AccommodationReviewResponse> getAccommodationReviews(Long accId){

        if (!accommodationRepository.existsById(accId)) {
            throw new AccommodationNotFoundException(accId);
        }

        List<AccommodationReview> accommodationReviewList = accommodationReviewRepository.findByAccommodation_Id(accId);

        return accommodationReviewList.stream()
                .map(review -> new AccommodationReviewResponse(
                        review.getId(),
                        review.getBooking().getId(),
                        review.getGuest().getNickName(),
                        review.getGuest().getProfileImageUrl(),
                        review.getRatingOverall(),
                        review.getRatingCleanliness(),
                        review.getRatingAccuracy(),
                        review.getRatingCheckin(),
                        review.getRatingCommunication(),
                        review.getRatingLocation(),
                        review.getReviewComment(),
                        review.getComment() != null ? CommentResponseDTO.from(review.getComment()) : null
                ))
                .collect(Collectors.toList());

    }

    // 숙소 리뷰 수정
    @Transactional
    public ReviewResponse updateAccommodationReview(Long reviewId, AccommodationReviewCreateRequest request){

        AccommodationReview review = accommodationReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        if(review.getComment() != null){
            throw new ReviewUpdateNotAllowedException("답글이 달린 리뷰는 수정할 수 없습니다.");
        }

        BigDecimal ratingOverall = calculateRating(request);
        review.update(request, ratingOverall);

        // 숙소 평점 및 리뷰 수 업데이트
        updateAccommodationStats(review.getAccommodation().getId());

        // TODO 응답 형식 변경 고려
        return new ReviewResponse("리뷰를 수정하였습니다");
    }


    // 숙소 리뷰 삭제
    @Transactional
    public ReviewResponse deleteAccommodationReview(Long reviewId){

        AccommodationReview review = accommodationReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        Long accommodationId = review.getAccommodation().getId();
        accommodationReviewRepository.delete(review);

        // 숙소 평점 및 리뷰 수 업데이트
        updateAccommodationStats(accommodationId);

        return new ReviewResponse("리뷰를 삭제하였습니다");
    }

    private void updateAccommodationStats(Long accommodationId) {
        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new AccommodationNotFoundException(accommodationId));

        var summary = accommodationReviewRepository.getReviewSummary(accommodationId);
        accommodation.updateReviewStats(summary.getAverage(), summary.getCount().intValue());
    }


    // 숙소 평점 계산 헬퍼 메서드
    public BigDecimal calculateRating(AccommodationReviewCreateRequest request){

        BigDecimal sum = request.ratingCleanliness()
                .add(request.ratingAccuracy())
                .add(request.ratingCheckin())
                .add(request.ratingLocation())
                .add(request.ratingCommunication());

        return sum.divide(NUMBER_OF_RATING_CRITERIA, 1, RoundingMode.HALF_UP);
    }


    // 특정 사용자가 작성한 모든 숙소 리뷰들 조회
    public List<AccommodationReviewResponse> getAccommodationReviewListByGuest(Long guestId){

        List<AccommodationReview> reviewList = accommodationReviewRepository.findByGuestId(guestId);

        return reviewList.stream()
                .map(review -> new AccommodationReviewResponse(
                        review.getId(),
                        review.getBooking().getId(),
                        review.getGuest().getNickName(),
                        review.getGuest().getProfileImageUrl(),
                        review.getRatingOverall(),
                        review.getRatingCleanliness(),
                        review.getRatingAccuracy(),
                        review.getRatingCheckin(),
                        review.getRatingCommunication(),
                        review.getRatingLocation(),
                        review.getReviewComment(),
                        review.getComment() != null ? CommentResponseDTO.from(review.getComment()) : null
                ))
                .collect(Collectors.toList());
    }



    // 특정 사용자 + 특정 숙소 리뷰 조회
    public AccommodationReviewResponse getAccommodationReviewByGuest(Long guestId, Long accId){

        AccommodationReview review = accommodationReviewRepository.findByGuestAndAccommodation(guestId, accId)
                .orElseThrow(() -> new ReviewNotFoundException("해당 숙소에 대해 작성하신 리뷰를 찾을 수 없습니다. (숙소 ID: " + accId + ")"));

        return new AccommodationReviewResponse(
                review.getId(),
                review.getBooking().getId(),
                review.getGuest().getNickName(),
                review.getGuest().getProfileImageUrl(),
                review.getRatingOverall(),
                review.getRatingCleanliness(),
                review.getRatingAccuracy(),
                review.getRatingCheckin(),
                review.getRatingCommunication(),
                review.getRatingLocation(),
                review.getReviewComment(),
                review.getComment() != null ? CommentResponseDTO.from(review.getComment()) : null
        );
    }
}
